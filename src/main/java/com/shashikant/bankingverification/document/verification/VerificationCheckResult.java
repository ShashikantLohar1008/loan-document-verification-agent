package com.shashikant.bankingverification.document.verification;

public class VerificationCheckResult {

    private final String ruleCode;
    private final boolean passed;
    private final String message;

    public VerificationCheckResult(String ruleCode, boolean passed, String message) {
        this.ruleCode = ruleCode;
        this.passed = passed;
        this.message = message;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public boolean isPassed() {
        return passed;
    }

    public String getMessage() {
        return message;
    }
}
