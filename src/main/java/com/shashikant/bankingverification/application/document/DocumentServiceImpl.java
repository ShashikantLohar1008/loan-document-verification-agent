package com.shashikant.bankingverification.application.document;

import java.time.Instant;
import java.util.List;
import java.nio.file.Path;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.shashikant.bankingverification.domain.document.DocumentStatus;
import com.shashikant.bankingverification.domain.document.DocumentType;
import com.shashikant.bankingverification.domain.document.OcrStatus;
import com.shashikant.bankingverification.infrastructure.files.document.StoredDocumentFile;
import com.shashikant.bankingverification.infrastructure.persistence.entities.document.DocumentEntity;
import com.shashikant.bankingverification.infrastructure.persistence.repositories.document.DocumentRepository;
import com.shashikant.bankingverification.infrastructure.rest.dto.document.DocumentCreateRequestDTO;
import com.shashikant.bankingverification.infrastructure.rest.dto.document.DocumentOcrResponseDTO;
import com.shashikant.bankingverification.infrastructure.rest.dto.document.DocumentResponseDTO;
import com.shashikant.bankingverification.shared.exception.BadRequestException;
import com.shashikant.bankingverification.shared.exception.ResourceNotFoundException;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentStorageService documentStorageService;
    private final OcrExtractionService ocrExtractionService;

    public DocumentServiceImpl(DocumentRepository documentRepository, DocumentStorageService documentStorageService,
            OcrExtractionService ocrExtractionService) {
        this.documentRepository = documentRepository;
        this.documentStorageService = documentStorageService;
        this.ocrExtractionService = ocrExtractionService;
    }

    @Override
    @Transactional
    public DocumentResponseDTO createDocument(DocumentCreateRequestDTO request) {
        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setFileName(request.getFileName());
        documentEntity.setOriginalFileName(request.getOriginalFileName());
        documentEntity.setDocumentType(request.getDocumentType().name());
        documentEntity.setDocumentStatus(DocumentStatus.UPLOADED.name());
        documentEntity.setUploadedAt(Instant.now());

        DocumentEntity savedDocument = documentRepository.save(documentEntity);
        return toResponse(savedDocument);
    }

    @Override
    @Transactional
    public DocumentResponseDTO uploadDocument(MultipartFile file, DocumentType documentType) {
        StoredDocumentFile storedDocumentFile = documentStorageService.store(file);

        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setFileName(storedDocumentFile.fileName());
        documentEntity.setOriginalFileName(storedDocumentFile.originalFileName());
        documentEntity.setDocumentType(documentType.name());
        documentEntity.setDocumentStatus(DocumentStatus.UPLOADED.name());
        documentEntity.setContentType(storedDocumentFile.contentType());
        documentEntity.setFileSize(storedDocumentFile.fileSize());
        documentEntity.setStoragePath(storedDocumentFile.storagePath());
        documentEntity.setOcrStatus(OcrStatus.PENDING.name());
        documentEntity.setUploadedAt(Instant.now());

        DocumentEntity savedDocument = documentRepository.save(documentEntity);
        return toResponse(savedDocument);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponseDTO> getDocuments() {
        return documentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentResponseDTO getDocumentById(Long documentKey) {
        DocumentEntity documentEntity = findDocumentEntity(documentKey);

        return toResponse(documentEntity);
    }

    @Override
    @Transactional(noRollbackFor = BadRequestException.class)
    public DocumentOcrResponseDTO extractOcr(Long documentKey) {
        DocumentEntity documentEntity = findDocumentEntity(documentKey);
        if (documentEntity.getStoragePath() == null || documentEntity.getStoragePath().isBlank()) {
            throw new BadRequestException("OCR requires an uploaded document file");
        }

        documentEntity.setOcrStatus(OcrStatus.PROCESSING.name());
        documentEntity.setOcrErrorMessage(null);

        try {
            String extractedText = ocrExtractionService.extractText(
                    Path.of(documentEntity.getStoragePath()),
                    documentEntity.getContentType());
            documentEntity.setOcrText(extractedText);
            documentEntity.setOcrStatus(OcrStatus.COMPLETED.name());
            documentEntity.setOcrProcessedAt(Instant.now());
        } catch (BadRequestException exception) {
            documentEntity.setOcrStatus(OcrStatus.FAILED.name());
            documentEntity.setOcrErrorMessage(truncate(exception.getMessage(), 1000));
            documentEntity.setOcrProcessedAt(Instant.now());
            throw exception;
        }

        return toOcrResponse(documentEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentOcrResponseDTO getOcr(Long documentKey) {
        return toOcrResponse(findDocumentEntity(documentKey));
    }

    private DocumentEntity findDocumentEntity(Long documentKey) {
        return documentRepository.findById(documentKey)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentKey));
    }

    private DocumentResponseDTO toResponse(DocumentEntity documentEntity) {
        DocumentResponseDTO response = new DocumentResponseDTO();
        response.setId(documentEntity.getDocumentKey());
        response.setFileName(documentEntity.getFileName());
        response.setOriginalFileName(documentEntity.getOriginalFileName());
        response.setDocumentType(DocumentType.valueOf(documentEntity.getDocumentType()));
        response.setDocumentStatus(DocumentStatus.valueOf(documentEntity.getDocumentStatus()));
        response.setContentType(documentEntity.getContentType());
        response.setFileSize(documentEntity.getFileSize());
        if (documentEntity.getOcrStatus() != null) {
            response.setOcrStatus(OcrStatus.valueOf(documentEntity.getOcrStatus()));
        }
        response.setOcrProcessedAt(documentEntity.getOcrProcessedAt());
        response.setUploadedAt(documentEntity.getUploadedAt());
        return response;
    }

    private DocumentOcrResponseDTO toOcrResponse(DocumentEntity documentEntity) {
        DocumentOcrResponseDTO response = new DocumentOcrResponseDTO();
        response.setDocumentId(documentEntity.getDocumentKey());
        if (documentEntity.getOcrStatus() != null) {
            response.setOcrStatus(OcrStatus.valueOf(documentEntity.getOcrStatus()));
        }
        response.setExtractedText(documentEntity.getOcrText());
        response.setProcessedAt(documentEntity.getOcrProcessedAt());
        response.setErrorMessage(documentEntity.getOcrErrorMessage());
        return response;
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }

        return value.substring(0, maxLength);
    }
}
