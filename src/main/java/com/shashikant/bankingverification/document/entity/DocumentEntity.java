package com.shashikant.bankingverification.document.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "documents")
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_key", nullable = false)
    private Long documentKey;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;

    @Column(name = "document_type", nullable = false)
    private String documentType;

    @Column(name = "document_status", nullable = false)
    private String documentStatus;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "storage_path")
    private String storagePath;

    @Column(name = "ocr_status")
    private String ocrStatus;

    @Column(name = "ocr_text")
    private String ocrText;

    @Column(name = "ocr_processed_at")
    private Instant ocrProcessedAt;

    @Column(name = "ocr_error_message")
    private String ocrErrorMessage;

    @Column(name = "classification_status")
    private String classificationStatus;

    @Column(name = "classified_document_type")
    private String classifiedDocumentType;

    @Column(name = "classification_confidence")
    private Double classificationConfidence;

    @Column(name = "classification_reason")
    private String classificationReason;

    @Column(name = "classified_at")
    private Instant classifiedAt;

    @Column(name = "verification_status")
    private String verificationStatus;

    @Column(name = "verification_score")
    private Double verificationScore;

    @Column(name = "verification_summary")
    private String verificationSummary;

    @Column(name = "verification_details")
    private String verificationDetails;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    @Column(name = "ai_summary_status")
    private String aiSummaryStatus;

    @Column(name = "ai_summary")
    private String aiSummary;

    @Column(name = "ai_summary_generated_at")
    private Instant aiSummaryGeneratedAt;

    @Column(name = "ai_summary_error_message")
    private String aiSummaryErrorMessage;

    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt;

    public Long getDocumentKey() {
        return documentKey;
    }

    public void setDocumentKey(Long documentKey) {
        this.documentKey = documentKey;
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

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentStatus() {
        return documentStatus;
    }

    public void setDocumentStatus(String documentStatus) {
        this.documentStatus = documentStatus;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getOcrStatus() {
        return ocrStatus;
    }

    public void setOcrStatus(String ocrStatus) {
        this.ocrStatus = ocrStatus;
    }

    public String getOcrText() {
        return ocrText;
    }

    public void setOcrText(String ocrText) {
        this.ocrText = ocrText;
    }

    public Instant getOcrProcessedAt() {
        return ocrProcessedAt;
    }

    public void setOcrProcessedAt(Instant ocrProcessedAt) {
        this.ocrProcessedAt = ocrProcessedAt;
    }

    public String getOcrErrorMessage() {
        return ocrErrorMessage;
    }

    public void setOcrErrorMessage(String ocrErrorMessage) {
        this.ocrErrorMessage = ocrErrorMessage;
    }

    public String getClassificationStatus() {
        return classificationStatus;
    }

    public void setClassificationStatus(String classificationStatus) {
        this.classificationStatus = classificationStatus;
    }

    public String getClassifiedDocumentType() {
        return classifiedDocumentType;
    }

    public void setClassifiedDocumentType(String classifiedDocumentType) {
        this.classifiedDocumentType = classifiedDocumentType;
    }

    public Double getClassificationConfidence() {
        return classificationConfidence;
    }

    public void setClassificationConfidence(Double classificationConfidence) {
        this.classificationConfidence = classificationConfidence;
    }

    public String getClassificationReason() {
        return classificationReason;
    }

    public void setClassificationReason(String classificationReason) {
        this.classificationReason = classificationReason;
    }

    public Instant getClassifiedAt() {
        return classifiedAt;
    }

    public void setClassifiedAt(Instant classifiedAt) {
        this.classifiedAt = classifiedAt;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
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

    public String getAiSummaryStatus() {
        return aiSummaryStatus;
    }

    public void setAiSummaryStatus(String aiSummaryStatus) {
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

    public String getAiSummaryErrorMessage() {
        return aiSummaryErrorMessage;
    }

    public void setAiSummaryErrorMessage(String aiSummaryErrorMessage) {
        this.aiSummaryErrorMessage = aiSummaryErrorMessage;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
