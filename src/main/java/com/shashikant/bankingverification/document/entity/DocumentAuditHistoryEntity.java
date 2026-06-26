package com.shashikant.bankingverification.document.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "document_audit_history")
public class DocumentAuditHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_key", nullable = false)
    private Long auditKey;

    @Column(name = "document_key", nullable = false)
    private Long documentKey;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "event_status", nullable = false)
    private String eventStatus;

    @Column(name = "score")
    private Double score;

    @Column(name = "summary")
    private String summary;

    @Column(name = "details")
    private String details;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public Long getAuditKey() {
        return auditKey;
    }

    public void setAuditKey(Long auditKey) {
        this.auditKey = auditKey;
    }

    public Long getDocumentKey() {
        return documentKey;
    }

    public void setDocumentKey(Long documentKey) {
        this.documentKey = documentKey;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
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
