package com.shashikant.bankingverification.document.service;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.shashikant.bankingverification.common.exception.BadRequestException;
import com.shashikant.bankingverification.common.exception.ResourceNotFoundException;
import com.shashikant.bankingverification.document.classification.DocumentClassificationResult;
import com.shashikant.bankingverification.document.classification.DocumentClassificationService;
import com.shashikant.bankingverification.document.dto.DocumentCreateRequestDTO;
import com.shashikant.bankingverification.document.dto.DocumentClassificationResponseDTO;
import com.shashikant.bankingverification.document.dto.DocumentOcrResponseDTO;
import com.shashikant.bankingverification.document.dto.DocumentResponseDTO;
import com.shashikant.bankingverification.document.dto.DocumentVerificationResponseDTO;
import com.shashikant.bankingverification.document.entity.DocumentEntity;
import com.shashikant.bankingverification.document.enums.ClassificationStatus;
import com.shashikant.bankingverification.document.enums.DocumentStatus;
import com.shashikant.bankingverification.document.enums.DocumentType;
import com.shashikant.bankingverification.document.enums.OcrStatus;
import com.shashikant.bankingverification.document.enums.VerificationStatus;
import com.shashikant.bankingverification.document.repository.DocumentRepository;
import com.shashikant.bankingverification.document.storage.StoredDocumentFile;
import com.shashikant.bankingverification.document.verification.DocumentVerificationResult;
import com.shashikant.bankingverification.document.verification.DocumentVerificationService;
import com.shashikant.bankingverification.document.verification.VerificationCheckResult;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentStorageService documentStorageService;
    private final OcrExtractionService ocrExtractionService;
    private final DocumentClassificationService documentClassificationService;
    private final DocumentVerificationService documentVerificationService;

    public DocumentServiceImpl(DocumentRepository documentRepository, DocumentStorageService documentStorageService,
            OcrExtractionService ocrExtractionService, DocumentClassificationService documentClassificationService,
            DocumentVerificationService documentVerificationService) {
        this.documentRepository = documentRepository;
        this.documentStorageService = documentStorageService;
        this.ocrExtractionService = ocrExtractionService;
        this.documentClassificationService = documentClassificationService;
        this.documentVerificationService = documentVerificationService;
    }

    @Override
    @Transactional
    public DocumentResponseDTO createDocument(DocumentCreateRequestDTO request) {
        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setFileName(request.getFileName());
        documentEntity.setOriginalFileName(request.getOriginalFileName());
        documentEntity.setDocumentType(request.getDocumentType().name());
        documentEntity.setDocumentStatus(DocumentStatus.UPLOADED.name());
        documentEntity.setOcrStatus(OcrStatus.PENDING.name());
        documentEntity.setClassificationStatus(ClassificationStatus.PENDING.name());
        documentEntity.setVerificationStatus(VerificationStatus.PENDING.name());
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
        documentEntity.setClassificationStatus(ClassificationStatus.PENDING.name());
        documentEntity.setVerificationStatus(VerificationStatus.PENDING.name());
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

    @Override
    @Transactional
    public DocumentClassificationResponseDTO classifyDocument(Long documentKey) {
        DocumentEntity documentEntity = findDocumentEntity(documentKey);
        if (documentEntity.getOcrText() == null || documentEntity.getOcrText().isBlank()) {
            throw new BadRequestException("Classification requires completed OCR text");
        }

        DocumentClassificationResult classificationResult = documentClassificationService.classify(documentEntity.getOcrText());
        documentEntity.setClassificationStatus(ClassificationStatus.COMPLETED.name());
        documentEntity.setClassifiedDocumentType(classificationResult.getDocumentType().name());
        documentEntity.setClassificationConfidence(classificationResult.getConfidence());
        documentEntity.setClassificationReason(truncate(classificationResult.getReason(), 1000));
        documentEntity.setClassifiedAt(Instant.now());

        return toClassificationResponse(documentEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentClassificationResponseDTO getClassification(Long documentKey) {
        return toClassificationResponse(findDocumentEntity(documentKey));
    }

    @Override
    @Transactional
    public DocumentVerificationResponseDTO verifyDocument(Long documentKey) {
        DocumentEntity documentEntity = findDocumentEntity(documentKey);
        if (documentEntity.getOcrText() == null || documentEntity.getOcrText().isBlank()) {
            throw new BadRequestException("Verification requires completed OCR text");
        }
        if (documentEntity.getClassifiedDocumentType() == null || documentEntity.getClassifiedDocumentType().isBlank()) {
            throw new BadRequestException("Verification requires document classification");
        }

        DocumentType documentType = DocumentType.valueOf(documentEntity.getClassifiedDocumentType());
        documentEntity.setDocumentStatus(DocumentStatus.PROCESSING.name());

        DocumentVerificationResult verificationResult = documentVerificationService.verify(
                documentType,
                documentEntity.getOcrText());

        documentEntity.setVerificationStatus(verificationResult.getVerificationStatus().name());
        documentEntity.setVerificationScore(verificationResult.getScore());
        documentEntity.setVerificationSummary(truncate(verificationResult.getSummary(), 1000));
        documentEntity.setVerificationDetails(toVerificationDetails(verificationResult));
        documentEntity.setVerifiedAt(Instant.now());
        documentEntity.setDocumentStatus(verificationResult.getVerificationStatus() == VerificationStatus.PASSED
                ? DocumentStatus.VERIFIED.name()
                : DocumentStatus.FAILED.name());

        return toVerificationResponse(documentEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentVerificationResponseDTO getVerification(Long documentKey) {
        return toVerificationResponse(findDocumentEntity(documentKey));
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
        if (documentEntity.getClassificationStatus() != null) {
            response.setClassificationStatus(ClassificationStatus.valueOf(documentEntity.getClassificationStatus()));
        }
        if (documentEntity.getClassifiedDocumentType() != null) {
            response.setClassifiedDocumentType(DocumentType.valueOf(documentEntity.getClassifiedDocumentType()));
        }
        response.setClassificationConfidence(documentEntity.getClassificationConfidence());
        response.setClassifiedAt(documentEntity.getClassifiedAt());
        if (documentEntity.getVerificationStatus() != null) {
            response.setVerificationStatus(VerificationStatus.valueOf(documentEntity.getVerificationStatus()));
        }
        response.setVerificationScore(documentEntity.getVerificationScore());
        response.setVerifiedAt(documentEntity.getVerifiedAt());
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

    private DocumentClassificationResponseDTO toClassificationResponse(DocumentEntity documentEntity) {
        DocumentClassificationResponseDTO response = new DocumentClassificationResponseDTO();
        response.setDocumentId(documentEntity.getDocumentKey());
        if (documentEntity.getClassificationStatus() != null) {
            response.setClassificationStatus(ClassificationStatus.valueOf(documentEntity.getClassificationStatus()));
        }
        if (documentEntity.getClassifiedDocumentType() != null) {
            response.setClassifiedDocumentType(DocumentType.valueOf(documentEntity.getClassifiedDocumentType()));
        }
        response.setConfidence(documentEntity.getClassificationConfidence());
        response.setReason(documentEntity.getClassificationReason());
        response.setClassifiedAt(documentEntity.getClassifiedAt());
        return response;
    }

    private DocumentVerificationResponseDTO toVerificationResponse(DocumentEntity documentEntity) {
        DocumentVerificationResponseDTO response = new DocumentVerificationResponseDTO();
        response.setDocumentId(documentEntity.getDocumentKey());
        if (documentEntity.getClassifiedDocumentType() != null) {
            response.setDocumentType(DocumentType.valueOf(documentEntity.getClassifiedDocumentType()));
        }
        if (documentEntity.getVerificationStatus() != null) {
            response.setVerificationStatus(VerificationStatus.valueOf(documentEntity.getVerificationStatus()));
        }
        response.setScore(documentEntity.getVerificationScore());
        response.setSummary(documentEntity.getVerificationSummary());
        response.setDetails(documentEntity.getVerificationDetails());
        response.setVerifiedAt(documentEntity.getVerifiedAt());
        return response;
    }

    private String toVerificationDetails(DocumentVerificationResult verificationResult) {
        StringBuilder details = new StringBuilder();
        for (VerificationCheckResult checkResult : verificationResult.getChecks()) {
            details.append(checkResult.getRuleCode())
                    .append(": ")
                    .append(checkResult.isPassed() ? "PASSED" : "FAILED")
                    .append(" - ")
                    .append(checkResult.getMessage())
                    .append(System.lineSeparator());
        }
        return details.toString().strip();
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }

        return value.substring(0, maxLength);
    }
}
