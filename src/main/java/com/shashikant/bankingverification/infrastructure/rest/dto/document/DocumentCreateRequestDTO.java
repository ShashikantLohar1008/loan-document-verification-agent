package com.shashikant.bankingverification.infrastructure.rest.dto.document;

import com.shashikant.bankingverification.domain.document.DocumentType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class DocumentCreateRequestDTO {

    @NotBlank(message = "fileName is required")
    @Size(max = 255, message = "fileName must be at most 255 characters")
    private String fileName;

    @NotBlank(message = "originalFileName is required")
    @Size(max = 255, message = "originalFileName must be at most 255 characters")
    private String originalFileName;

    @NotNull(message = "documentType is required")
    private DocumentType documentType;

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
}
