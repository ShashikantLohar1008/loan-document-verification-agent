package com.shashikant.bankingverification.document.dto;

import java.time.Instant;

import com.shashikant.bankingverification.document.enums.AiSummaryStatus;
import com.shashikant.bankingverification.document.enums.ClassificationStatus;
import com.shashikant.bankingverification.document.enums.DocumentStatus;
import com.shashikant.bankingverification.document.enums.DocumentType;
import com.shashikant.bankingverification.document.enums.OcrStatus;
import com.shashikant.bankingverification.document.enums.VerificationStatus;

public class DocumentVerificationReportDTO {

    private Long documentId;
    private String fileName;
    private String originalFileName;
    private DocumentType requestedDocumentType;
    private DocumentStatus documentStatus;
    private OcrStatus ocrStatus;
    private Instant ocrProcessedAt;
    private ClassificationStatus classificationStatus;
    private DocumentType classifiedDocumentType;
    private Double classificationConfidence;
    private Instant classifiedAt;
    private VerificationStatus verificationStatus;
    private Double verificationScore;
    private String verificationSummary;
    private String verificationDetails;
    private Instant verifiedAt;
    private AiSummaryStatus aiSummaryStatus;
    private String aiSummary;
    private Instant aiSummaryGeneratedAt;
    private Instant uploadedAt;
    private Instant generatedAt;

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public DocumentType getRequestedDocumentType() {
        return requestedDocumentType;
    }

    public void setRequestedDocumentType(DocumentType requestedDocumentType) {
        this.requestedDocumentType = requestedDocumentType;
    }

    public DocumentStatus getDocumentStatus() {
        return documentStatus;
    }

    public void setDocumentStatus(DocumentStatus documentStatus) {
        this.documentStatus = documentStatus;
    }

    public OcrStatus getOcrStatus() {
        return ocrStatus;
    }

    public void setOcrStatus(OcrStatus ocrStatus) {
        this.ocrStatus = ocrStatus;
    }

    public Instant getOcrProcessedAt() {
        return ocrProcessedAt;
    }

    public void setOcrProcessedAt(Instant ocrProcessedAt) {
        this.ocrProcessedAt = ocrProcessedAt;
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

    public Double getClassificationConfidence() {
        return classificationConfidence;
    }

    public void setClassificationConfidence(Double classificationConfidence) {
        this.classificationConfidence = classificationConfidence;
    }

    public Instant getClassifiedAt() {
        return classifiedAt;
    }

    public void setClassifiedAt(Instant classifiedAt) {
        this.classifiedAt = classifiedAt;
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public Double getVerificationScore() {
        return verificationScore;
    }

    public void setVerificationScore(Double verificationScore) {
        this.verificationScore = verificationScore;
    }

    public String getVerificationSummary() {
        return verificationSummary;
    }

    public void setVerificationSummary(String verificationSummary) {
        this.verificationSummary = verificationSummary;
    }

    public String getVerificationDetails() {
        return verificationDetails;
    }

    public void setVerificationDetails(String verificationDetails) {
        this.verificationDetails = verificationDetails;
    }

    public Instant getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(Instant verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public AiSummaryStatus getAiSummaryStatus() {
        return aiSummaryStatus;
    }

    public void setAiSummaryStatus(AiSummaryStatus aiSummaryStatus) {
        this.aiSummaryStatus = aiSummaryStatus;
    }

    public String getAiSummary() {
        return aiSummary;
    }

    public void setAiSummary(String aiSummary) {
        this.aiSummary = aiSummary;
    }

    public Instant getAiSummaryGeneratedAt() {
        return aiSummaryGeneratedAt;
    }

    public void setAiSummaryGeneratedAt(Instant aiSummaryGeneratedAt) {
        this.aiSummaryGeneratedAt = aiSummaryGeneratedAt;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Instant generatedAt) {
        this.generatedAt = generatedAt;
    }
}
