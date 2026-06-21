package com.shashikant.bankingverification.infrastructure.files.document;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.shashikant.bankingverification.application.document.DocumentStorageService;
import com.shashikant.bankingverification.shared.exception.BadRequestException;

@Service
public class LocalDocumentStorageService implements DocumentStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "image/jpeg",
            "image/png");

    private final Path uploadDirectory;

    public LocalDocumentStorageService(DocumentStorageProperties documentStorageProperties) {
        this.uploadDirectory = Path.of(documentStorageProperties.getUploadDir()).toAbsolutePath().normalize();
    }

    @Override
    public StoredDocumentFile store(MultipartFile file) {
        validate(file);

        try {
            Files.createDirectories(uploadDirectory);

            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
            String storedFileName = UUID.randomUUID() + getExtension(originalFileName);
            Path targetLocation = uploadDirectory.resolve(storedFileName).normalize();

            if (!targetLocation.startsWith(uploadDirectory)) {
                throw new BadRequestException("Invalid file path");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            return new StoredDocumentFile(
                    storedFileName,
                    originalFileName,
                    file.getContentType(),
                    file.getSize(),
                    targetLocation.toString());
        } catch (IOException exception) {
            throw new BadRequestException("Could not store uploaded document");
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Document file is required");
        }

        if (!StringUtils.hasText(file.getOriginalFilename())) {
            throw new BadRequestException("Original file name is required");
        }

        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("Only PDF, JPEG, and PNG documents are supported");
        }
    }

    private String getExtension(String fileName) {
        int extensionIndex = fileName.lastIndexOf('.');
        if (extensionIndex < 0) {
            return "";
        }

        return fileName.substring(extensionIndex);
    }
}
