package com.shashikant.bankingverification.infrastructure.rest.dto.document;

import java.time.Instant;

import com.shashikant.bankingverification.domain.document.DocumentStatus;
import com.shashikant.bankingverification.domain.document.DocumentType;
import com.shashikant.bankingverification.domain.document.OcrStatus;

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

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
