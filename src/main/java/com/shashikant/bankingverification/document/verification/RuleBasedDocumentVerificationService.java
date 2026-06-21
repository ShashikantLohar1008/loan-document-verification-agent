package com.shashikant.bankingverification.document.verification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.shashikant.bankingverification.document.enums.DocumentType;
import com.shashikant.bankingverification.document.enums.VerificationStatus;

@Service
public class RuleBasedDocumentVerificationService implements DocumentVerificationService {

    private static final Pattern PAN_PATTERN = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]", Pattern.CASE_INSENSITIVE);
    private static final Pattern AADHAAR_PATTERN = Pattern.compile("\\b[2-9][0-9]{3}\\s?[0-9]{4}\\s?[0-9]{4}\\b");
    private static final Pattern IFSC_PATTERN = Pattern.compile("\\b[A-Z]{4}0[A-Z0-9]{6}\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("\\b[0-9]{9,18}\\b");
    private static final Pattern PASSPORT_PATTERN = Pattern.compile("\\b[A-Z][0-9]{7}\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern DATE_PATTERN = Pattern.compile("\\b[0-3]?[0-9][/-][0-1]?[0-9][/-][0-9]{2,4}\\b");

    @Override
    public DocumentVerificationResult verify(DocumentType documentType, String extractedText) {
        String normalizedText = normalize(extractedText);
        List<VerificationCheckResult> checks = switch (documentType) {
            case PAN -> verifyPan(normalizedText);
            case AADHAAR -> verifyAadhaar(normalizedText);
            case SALARY_SLIP -> verifySalarySlip(normalizedText);
            case BANK_STATEMENT -> verifyBankStatement(normalizedText);
            case PASSPORT -> verifyPassport(normalizedText);
            case UNKNOWN -> verifyUnknown();
        };

        long passedChecks = checks.stream().filter(VerificationCheckResult::isPassed).count();
        double score = checks.isEmpty() ? 0.0 : passedChecks / (double) checks.size();
        VerificationStatus status = score >= 0.70 ? VerificationStatus.PASSED : VerificationStatus.FAILED;
        String summary = passedChecks + " of " + checks.size() + " verification rules passed";

        return new DocumentVerificationResult(status, round(score), summary, checks);
    }

    private List<VerificationCheckResult> verifyPan(String text) {
        List<VerificationCheckResult> checks = new ArrayList<>();
        checks.add(check("PAN_NUMBER", PAN_PATTERN.matcher(text).find(), "PAN number format should be present"));
        checks.add(check("PAN_KEYWORD", containsAny(text, "permanent account number", "income tax department"),
                "PAN related authority keyword should be present"));
        checks.add(check("PAN_IDENTITY", containsAny(text, "name", "father", "date of birth") || DATE_PATTERN.matcher(text).find(),
                "Identity or date information should be present"));
        return checks;
    }

    private List<VerificationCheckResult> verifyAadhaar(String text) {
        List<VerificationCheckResult> checks = new ArrayList<>();
        checks.add(check("AADHAAR_NUMBER", AADHAAR_PATTERN.matcher(text).find(), "Aadhaar number format should be present"));
        checks.add(check("AADHAAR_KEYWORD", containsAny(text, "aadhaar", "unique identification", "uidai"),
                "Aadhaar or UIDAI keyword should be present"));
        checks.add(check("AADHAAR_IDENTITY", containsAny(text, "dob", "year of birth", "male", "female", "address"),
                "Basic identity or address clue should be present"));
        return checks;
    }

    private List<VerificationCheckResult> verifySalarySlip(String text) {
        List<VerificationCheckResult> checks = new ArrayList<>();
        checks.add(check("SALARY_KEYWORD", containsAny(text, "salary slip", "payslip", "pay slip"),
                "Salary slip keyword should be present"));
        checks.add(check("SALARY_EARNINGS", containsAny(text, "basic", "earnings", "gross salary"),
                "Earnings information should be present"));
        checks.add(check("SALARY_NET_PAY", containsAny(text, "net pay", "net salary", "take home"),
                "Net pay information should be present"));
        checks.add(check("SALARY_DEDUCTIONS", containsAny(text, "deductions", "pf", "tax"),
                "Deduction information should be present"));
        return checks;
    }

    private List<VerificationCheckResult> verifyBankStatement(String text) {
        List<VerificationCheckResult> checks = new ArrayList<>();
        checks.add(check("BANK_ACCOUNT", contains(text, "account") && ACCOUNT_NUMBER_PATTERN.matcher(text).find(),
                "Account number should be present"));
        checks.add(check("BANK_IFSC", IFSC_PATTERN.matcher(text).find() || contains(text, "ifsc"),
                "IFSC information should be present"));
        checks.add(check("BANK_TRANSACTIONS", containsAny(text, "transaction", "debit", "credit", "balance"),
                "Transaction or balance information should be present"));
        return checks;
    }

    private List<VerificationCheckResult> verifyPassport(String text) {
        List<VerificationCheckResult> checks = new ArrayList<>();
        checks.add(check("PASSPORT_NUMBER", PASSPORT_PATTERN.matcher(text).find() || contains(text, "passport no"),
                "Passport number should be present"));
        checks.add(check("PASSPORT_KEYWORD", containsAny(text, "passport", "republic of india"),
                "Passport authority keyword should be present"));
        checks.add(check("PASSPORT_IDENTITY", containsAny(text, "surname", "given name", "nationality", "date of birth"),
                "Passport identity fields should be present"));
        return checks;
    }

    private List<VerificationCheckResult> verifyUnknown() {
        return List.of(new VerificationCheckResult("DOCUMENT_TYPE", false,
                "Document type is UNKNOWN, so rule based verification cannot be completed"));
    }

    private VerificationCheckResult check(String ruleCode, boolean passed, String message) {
        return new VerificationCheckResult(ruleCode, passed, message);
    }

    private boolean containsAny(String text, String... values) {
        for (String value : values) {
            if (contains(text, value)) {
                return true;
            }
        }
        return false;
    }

    private boolean contains(String text, String value) {
        return text.contains(value);
    }

    private String normalize(String text) {
        if (text == null) {
            return "";
        }

        return text.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
