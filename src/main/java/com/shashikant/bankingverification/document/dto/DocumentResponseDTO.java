package com.shashikant.bankingverification.document.dto;

import java.time.Instant;

import com.shashikant.bankingverification.document.enums.ClassificationStatus;
import com.shashikant.bankingverification.document.enums.DocumentStatus;
import com.shashikant.bankingverification.document.enums.DocumentType;
import com.shashikant.bankingverification.document.enums.OcrStatus;

public class DocumentResponseDTO {

    private Long id;
    private String fileName;
    private String originalFileName;
    private DocumentType documentType;
    private DocumentStatus documentStatus;
    private String contentType;
    private Long fileSize;
    private OcrStatus ocrStatus;
    private Instant ocrProcessedAt;
    private ClassificationStatus classificationStatus;
    private DocumentType classifiedDocumentType;
    private Double classificationConfidence;
    private Instant classifiedAt;
    private Instant uploadedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public DocumentStatus getDocumentStatus() {
        return documentStatus;
    }

    public void setDocumentStatus(DocumentStatus documentStatus) {
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

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
