package com.shashikant.bankingverification.document.verification;

import java.util.List;

import com.shashikant.bankingverification.document.enums.VerificationStatus;

public class DocumentVerificationResult {

    private final VerificationStatus verificationStatus;
    private final double score;
    private final String summary;
    private final List<VerificationCheckResult> checks;

    public DocumentVerificationResult(VerificationStatus verificationStatus, double score, String summary,
            List<VerificationCheckResult> checks) {
        this.verificationStatus = verificationStatus;
        this.score = score;
        this.summary = summary;
        this.checks = List.copyOf(checks);
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public double getScore() {
        return score;
    }

    public String getSummary() {
        return summary;
    }

    public List<VerificationCheckResult> getChecks() {
        return checks;
    }
}
