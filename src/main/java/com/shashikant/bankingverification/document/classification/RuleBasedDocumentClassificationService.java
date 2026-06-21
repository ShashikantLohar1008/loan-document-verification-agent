package com.shashikant.bankingverification.document.classification;

import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.shashikant.bankingverification.document.enums.DocumentType;

@Service
public class RuleBasedDocumentClassificationService implements DocumentClassificationService {

    private static final Pattern PAN_PATTERN = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]", Pattern.CASE_INSENSITIVE);
    private static final Pattern AADHAAR_PATTERN = Pattern.compile("\\b[2-9][0-9]{3}\\s?[0-9]{4}\\s?[0-9]{4}\\b");
    private static final Pattern PASSPORT_PATTERN = Pattern.compile("\\b[A-Z][0-9]{7}\\b", Pattern.CASE_INSENSITIVE);

    @Override
    public DocumentClassificationResult classify(String extractedText) {
        String normalizedText = normalize(extractedText);

        if (normalizedText.isBlank()) {
            return new DocumentClassificationResult(DocumentType.UNKNOWN, 0.0, "No OCR text available for classification");
        }

        if (matchesPan(normalizedText)) {
            return new DocumentClassificationResult(DocumentType.PAN, 0.95, "Matched PAN number format and PAN keywords");
        }

        if (matchesAadhaar(normalizedText)) {
            return new DocumentClassificationResult(DocumentType.AADHAAR, 0.93, "Matched Aadhaar number format and identity keywords");
        }

        if (matchesSalarySlip(normalizedText)) {
            return new DocumentClassificationResult(DocumentType.SALARY_SLIP, 0.88, "Matched salary slip payroll keywords");
        }

        if (matchesBankStatement(normalizedText)) {
            return new DocumentClassificationResult(DocumentType.BANK_STATEMENT, 0.88, "Matched bank statement account and transaction keywords");
        }

        if (matchesPassport(normalizedText)) {
            return new DocumentClassificationResult(DocumentType.PASSPORT, 0.90, "Matched passport number format and passport keywords");
        }

        return new DocumentClassificationResult(DocumentType.UNKNOWN, 0.25, "No strong document classification rule matched");
    }

    private boolean matchesPan(String text) {
        return PAN_PATTERN.matcher(text).find()
                && (contains(text, "permanent account number") || contains(text, "income tax department")
                        || contains(text, "govt. of india") || contains(text, "government of india"));
    }

    private boolean matchesAadhaar(String text) {
        return AADHAAR_PATTERN.matcher(text).find()
                && (contains(text, "aadhaar") || contains(text, "unique identification")
                        || contains(text, "government of india"));
    }

    private boolean matchesSalarySlip(String text) {
        return contains(text, "salary slip")
                || (contains(text, "net pay") && contains(text, "basic"))
                || (contains(text, "earnings") && contains(text, "deductions"));
    }

    private boolean matchesBankStatement(String text) {
        return contains(text, "bank statement")
                || (contains(text, "account number") && contains(text, "ifsc"))
                || (contains(text, "transaction") && contains(text, "balance"));
    }

    private boolean matchesPassport(String text) {
        return (contains(text, "passport") || contains(text, "republic of india"))
                && (PASSPORT_PATTERN.matcher(text).find() || contains(text, "passport no"));
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
}
