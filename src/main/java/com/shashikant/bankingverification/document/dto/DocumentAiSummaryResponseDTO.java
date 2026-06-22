package com.shashikant.bankingverification.document.dto;

import java.time.Instant;

import com.shashikant.bankingverification.document.enums.AiSummaryStatus;

public class DocumentAiSummaryResponseDTO {

    private Long documentId;
    private AiSummaryStatus aiSummaryStatus;
    private String summary;
    private Instant generatedAt;
    private String errorMessage;

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public AiSummaryStatus getAiSummaryStatus() {
        return aiSummaryStatus;
    }

    public void setAiSummaryStatus(AiSummaryStatus aiSummaryStatus) {
        this.aiSummaryStatus = aiSummaryStatus;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Instant generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
