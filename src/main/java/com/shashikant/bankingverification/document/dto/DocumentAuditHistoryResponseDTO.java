package com.shashikant.bankingverification.document.dto;

import java.time.Instant;

import com.shashikant.bankingverification.document.enums.DocumentAuditEventType;

public class DocumentAuditHistoryResponseDTO {

    private Long auditId;
    private Long documentId;
    private DocumentAuditEventType eventType;
    private String eventStatus;
    private Double score;
    private String summary;
    private String details;
    private Instant createdAt;

    public Long getAuditId() {
        return auditId;
    }

    public void setAuditId(Long auditId) {
        this.auditId = auditId;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public DocumentAuditEventType getEventType() {
        return eventType;
    }

    public void setEventType(DocumentAuditEventType eventType) {
        this.eventType = eventType;
    }

    public String getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(String eventStatus) {
        this.eventStatus = eventStatus;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
