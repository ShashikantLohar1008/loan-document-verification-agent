package com.shashikant.bankingverification.document.storage;

public record StoredDocumentFile(
        String fileName,
        String originalFileName,
        String contentType,
        Long fileSize,
        String storagePath) {
}
