package com.shashikant.bankingverification.document.dto;

import java.time.Instant;

import com.shashikant.bankingverification.document.enums.ClassificationStatus;
import com.shashikant.bankingverification.document.enums.DocumentType;

public class DocumentClassificationResponseDTO {

    private Long documentId;
    private ClassificationStatus classificationStatus;
    private DocumentType classifiedDocumentType;
    private Double confidence;
    private String reason;
    private Instant classifiedAt;

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public ClassificationStatus getClassificationStatus() {
        return classificationStatus;
    }

    public void setClassificationStatus(ClassificationStatus classificationStatus) {
        this.classificationStatus = classificationStatus;
    }

    public DocumentType getClassifiedDocumentType() {
        return classifiedDocumentType;
    }

    public void setClassifiedDocumentType(DocumentType classifiedDocumentType) {
        this.classifiedDocumentType = classifiedDocumentType;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Instant getClassifiedAt() {
        return classifiedAt;
    }

    public void setClassifiedAt(Instant classifiedAt) {
        this.classifiedAt = classifiedAt;
    }
}
