package com.shashikant.bankingverification.infrastructure.files.document;

public record StoredDocumentFile(
        String fileName,
        String originalFileName,
        String contentType,
        Long fileSize,
        String storagePath) {
}
