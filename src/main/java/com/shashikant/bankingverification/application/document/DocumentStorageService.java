package com.shashikant.bankingverification.application.document;

import org.springframework.web.multipart.MultipartFile;

import com.shashikant.bankingverification.infrastructure.files.document.StoredDocumentFile;

public interface DocumentStorageService {

    StoredDocumentFile store(MultipartFile file);
}
