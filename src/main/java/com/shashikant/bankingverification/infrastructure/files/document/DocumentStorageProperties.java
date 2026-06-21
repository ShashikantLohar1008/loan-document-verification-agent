package com.shashikant.bankingverification.infrastructure.files.document;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "document.storage")
public class DocumentStorageProperties {

    private String uploadDir = "uploads/documents";

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
}
