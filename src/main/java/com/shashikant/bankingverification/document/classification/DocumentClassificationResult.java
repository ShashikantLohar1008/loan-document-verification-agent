package com.shashikant.bankingverification.document.classification;

import com.shashikant.bankingverification.document.enums.DocumentType;

public class DocumentClassificationResult {

    private final DocumentType documentType;
    private final double confidence;
    private final String reason;

    public DocumentClassificationResult(DocumentType documentType, double confidence, String reason) {
        this.documentType = documentType;
        this.confidence = confidence;
        this.reason = reason;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public double getConfidence() {
        return confidence;
    }

    public String getReason() {
        return reason;
    }
}
