package com.shashikant.bankingverification.document.service;

import org.springframework.web.multipart.MultipartFile;

import com.shashikant.bankingverification.document.storage.StoredDocumentFile;

public interface DocumentStorageService {

    StoredDocumentFile store(MultipartFile file);
}
