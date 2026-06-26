package com.shashikant.bankingverification.document.service;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.shashikant.bankingverification.common.exception.BadRequestException;
import com.shashikant.bankingverification.common.exception.ResourceNotFoundException;
import com.shashikant.bankingverification.document.ai.AiVerificationSummaryService;
import com.shashikant.bankingverification.document.ai.tool.DocumentVerificationReportTool;
import com.shashikant.bankingverification.document.dto.DocumentAuditHistoryResponseDTO;
import com.shashikant.bankingverification.document.classification.DocumentClassificationResult;
import com.shashikant.bankingverification.document.classification.DocumentClassificationService;
import com.shashikant.bankingverification.document.dto.DocumentCreateRequestDTO;
import com.shashikant.bankingverification.document.dto.DocumentAiSummaryResponseDTO;
import com.shashikant.bankingverification.document.dto.DocumentClassificationResponseDTO;
import com.shashikant.bankingverification.document.dto.DocumentOcrResponseDTO;
import com.shashikant.bankingverification.document.dto.DocumentResponseDTO;
import com.shashikant.bankingverification.document.dto.DocumentVerificationReportDTO;
import com.shashikant.bankingverification.document.dto.DocumentVerificationResponseDTO;
import com.shashikant.bankingverification.document.entity.DocumentAuditHistoryEntity;
import com.shashikant.bankingverification.document.entity.DocumentEntity;
import com.shashikant.bankingverification.document.enums.AiSummaryStatus;
import com.shashikant.bankingverification.document.enums.ClassificationStatus;
import com.shashikant.bankingverification.document.enums.DocumentAuditEventType;
import com.shashikant.bankingverification.document.enums.DocumentStatus;
import com.shashikant.bankingverification.document.enums.DocumentType;
import com.shashikant.bankingverification.document.enums.OcrStatus;
import com.shashikant.bankingverification.document.enums.VerificationStatus;
import com.shashikant.bankingverification.document.repository.DocumentAuditHistoryRepository;
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
    private final AiVerificationSummaryService aiVerificationSummaryService;
    private final DocumentAuditHistoryRepository documentAuditHistoryRepository;

    public DocumentServiceImpl(DocumentRepository documentRepository, DocumentStorageService documentStorageService,
            OcrExtractionService ocrExtractionService, DocumentClassificationService documentClassificationService,
            DocumentVerificationService documentVerificationService,
            AiVerificationSummaryService aiVerificationSummaryService,
            DocumentAuditHistoryRepository documentAuditHistoryRepository) {
        this.documentRepository = documentRepository;
        this.documentStorageService = documentStorageService;
        this.ocrExtractionService = ocrExtractionService;
        this.documentClassificationService = documentClassificationService;
        this.documentVerificationService = documentVerificationService;
        this.aiVerificationSummaryService = aiVerificationSummaryService;
        this.documentAuditHistoryRepository = documentAuditHistoryRepository;
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
        documentEntity.setAiSummaryStatus(AiSummaryStatus.PENDING.name());
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
        documentEntity.setAiSummaryStatus(AiSummaryStatus.PENDING.name());
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
        recordAuditHistory(
                documentEntity.getDocumentKey(),
                DocumentAuditEventType.VERIFICATION,
                documentEntity.getVerificationStatus(),
                documentEntity.getVerificationScore(),
                documentEntity.getVerificationSummary(),
                documentEntity.getVerificationDetails());

        return toVerificationResponse(documentEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentVerificationResponseDTO getVerification(Long documentKey) {
        return toVerificationResponse(findDocumentEntity(documentKey));
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentVerificationReportDTO getVerificationReport(Long documentKey) {
        return toVerificationReport(findDocumentEntity(documentKey));
    }

    @Override
    @Transactional(noRollbackFor = BadRequestException.class)
    public DocumentAiSummaryResponseDTO generateAiSummary(Long documentKey) {
        DocumentEntity documentEntity = findDocumentEntity(documentKey);
        validateAiSummaryAllowed(documentEntity);

        documentEntity.setAiSummaryStatus(AiSummaryStatus.GENERATING.name());
        documentEntity.setAiSummaryErrorMessage(null);

        try {
            String summary = aiVerificationSummaryService.generateSummary(toVerificationReport(documentEntity));
            documentEntity.setAiSummary(summary);
            documentEntity.setAiSummaryStatus(AiSummaryStatus.COMPLETED.name());
            documentEntity.setAiSummaryGeneratedAt(Instant.now());
            recordAuditHistory(
                    documentEntity.getDocumentKey(),
                    DocumentAuditEventType.AI_SUMMARY,
                    documentEntity.getAiSummaryStatus(),
                    null,
                    documentEntity.getAiSummary(),
                    null);
        } catch (BadRequestException exception) {
            documentEntity.setAiSummaryStatus(AiSummaryStatus.FAILED.name());
            documentEntity.setAiSummaryErrorMessage(truncate(exception.getMessage(), 1000));
            documentEntity.setAiSummaryGeneratedAt(Instant.now());
            throw exception;
        } catch (RuntimeException exception) {
            documentEntity.setAiSummaryStatus(AiSummaryStatus.FAILED.name());
            documentEntity.setAiSummaryErrorMessage(truncate("AI summary generation failed: " + exception.getMessage(), 1000));
            documentEntity.setAiSummaryGeneratedAt(Instant.now());
            throw new BadRequestException("AI summary generation failed");
        }

        return toAiSummaryResponse(documentEntity);
    }

    @Override
    @Transactional(noRollbackFor = BadRequestException.class)
    public DocumentAiSummaryResponseDTO generateAiToolSummary(Long documentKey) {
        DocumentEntity documentEntity = findDocumentEntity(documentKey);
        validateAiSummaryAllowed(documentEntity);

        documentEntity.setAiSummaryStatus(AiSummaryStatus.GENERATING.name());
        documentEntity.setAiSummaryErrorMessage(null);

        try {
            DocumentVerificationReportTool reportTool = new DocumentVerificationReportTool(this::getVerificationReport);
            String summary = aiVerificationSummaryService.generateSummaryWithTool(documentKey, reportTool);
            documentEntity.setAiSummary(summary);
            documentEntity.setAiSummaryStatus(AiSummaryStatus.COMPLETED.name());
            documentEntity.setAiSummaryGeneratedAt(Instant.now());
            recordAuditHistory(
                    documentEntity.getDocumentKey(),
                    DocumentAuditEventType.AI_TOOL_SUMMARY,
                    documentEntity.getAiSummaryStatus(),
                    null,
                    documentEntity.getAiSummary(),
                    null);
        } catch (BadRequestException exception) {
            documentEntity.setAiSummaryStatus(AiSummaryStatus.FAILED.name());
            documentEntity.setAiSummaryErrorMessage(truncate(exception.getMessage(), 1000));
            documentEntity.setAiSummaryGeneratedAt(Instant.now());
            throw exception;
        } catch (RuntimeException exception) {
            documentEntity.setAiSummaryStatus(AiSummaryStatus.FAILED.name());
            documentEntity.setAiSummaryErrorMessage(truncate("AI tool summary generation failed: " + exception.getMessage(), 1000));
            documentEntity.setAiSummaryGeneratedAt(Instant.now());
            throw new BadRequestException("AI tool summary generation failed");
        }

        return toAiSummaryResponse(documentEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentAiSummaryResponseDTO getAiSummary(Long documentKey) {
        return toAiSummaryResponse(findDocumentEntity(documentKey));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentAuditHistoryResponseDTO> getAuditHistory(Long documentKey) {
        findDocumentEntity(documentKey);
        return documentAuditHistoryRepository.findByDocumentKeyOrderByCreatedAtDesc(documentKey)
                .stream()
                .map(this::toAuditHistoryResponse)
                .toList();
    }

    private DocumentEntity findDocumentEntity(Long documentKey) {
        return documentRepository.findById(documentKey)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentKey));
    }

    private void validateAiSummaryAllowed(DocumentEntity documentEntity) {
        if (documentEntity.getVerificationStatus() == null || documentEntity.getVerificationStatus().isBlank()) {
            throw new BadRequestException("AI summary requires completed verification");
        }
        if (VerificationStatus.PENDING.name().equals(documentEntity.getVerificationStatus())) {
            throw new BadRequestException("AI summary requires completed verification");
        }
    }

    private void recordAuditHistory(Long documentKey, DocumentAuditEventType eventType, String eventStatus,
            Double score, String summary, String details) {
        DocumentAuditHistoryEntity auditHistory = new DocumentAuditHistoryEntity();
        auditHistory.setDocumentKey(documentKey);
        auditHistory.setEventType(eventType.name());
        auditHistory.setEventStatus(eventStatus);
        auditHistory.setScore(score);
        auditHistory.setSummary(summary);
        auditHistory.setDetails(details);
        auditHistory.setCreatedAt(Instant.now());
        documentAuditHistoryRepository.save(auditHistory);
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
        if (documentEntity.getAiSummaryStatus() != null) {
            response.setAiSummaryStatus(AiSummaryStatus.valueOf(documentEntity.getAiSummaryStatus()));
        }
        response.setAiSummaryGeneratedAt(documentEntity.getAiSummaryGeneratedAt());
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

    private DocumentVerificationReportDTO toVerificationReport(DocumentEntity documentEntity) {
        DocumentVerificationReportDTO report = new DocumentVerificationReportDTO();
        report.setDocumentId(documentEntity.getDocumentKey());
        report.setFileName(documentEntity.getFileName());
        report.setOriginalFileName(documentEntity.getOriginalFileName());
        report.setRequestedDocumentType(DocumentType.valueOf(documentEntity.getDocumentType()));
        report.setDocumentStatus(DocumentStatus.valueOf(documentEntity.getDocumentStatus()));
        if (documentEntity.getOcrStatus() != null) {
            report.setOcrStatus(OcrStatus.valueOf(documentEntity.getOcrStatus()));
        }
        report.setOcrProcessedAt(documentEntity.getOcrProcessedAt());
        if (documentEntity.getClassificationStatus() != null) {
            report.setClassificationStatus(ClassificationStatus.valueOf(documentEntity.getClassificationStatus()));
        }
        if (documentEntity.getClassifiedDocumentType() != null) {
            report.setClassifiedDocumentType(DocumentType.valueOf(documentEntity.getClassifiedDocumentType()));
        }
        report.setClassificationConfidence(documentEntity.getClassificationConfidence());
        report.setClassifiedAt(documentEntity.getClassifiedAt());
        if (documentEntity.getVerificationStatus() != null) {
            report.setVerificationStatus(VerificationStatus.valueOf(documentEntity.getVerificationStatus()));
        }
        report.setVerificationScore(documentEntity.getVerificationScore());
        report.setVerificationSummary(documentEntity.getVerificationSummary());
        report.setVerificationDetails(documentEntity.getVerificationDetails());
        report.setVerifiedAt(documentEntity.getVerifiedAt());
        if (documentEntity.getAiSummaryStatus() != null) {
            report.setAiSummaryStatus(AiSummaryStatus.valueOf(documentEntity.getAiSummaryStatus()));
        }
        report.setAiSummary(documentEntity.getAiSummary());
        report.setAiSummaryGeneratedAt(documentEntity.getAiSummaryGeneratedAt());
        report.setUploadedAt(documentEntity.getUploadedAt());
        report.setGeneratedAt(Instant.now());
        return report;
    }

    private DocumentAiSummaryResponseDTO toAiSummaryResponse(DocumentEntity documentEntity) {
        DocumentAiSummaryResponseDTO response = new DocumentAiSummaryResponseDTO();
        response.setDocumentId(documentEntity.getDocumentKey());
        if (documentEntity.getAiSummaryStatus() != null) {
            response.setAiSummaryStatus(AiSummaryStatus.valueOf(documentEntity.getAiSummaryStatus()));
        }
        response.setSummary(documentEntity.getAiSummary());
        response.setGeneratedAt(documentEntity.getAiSummaryGeneratedAt());
        response.setErrorMessage(documentEntity.getAiSummaryErrorMessage());
        return response;
    }

    private DocumentAuditHistoryResponseDTO toAuditHistoryResponse(DocumentAuditHistoryEntity auditHistory) {
        DocumentAuditHistoryResponseDTO response = new DocumentAuditHistoryResponseDTO();
        response.setAuditId(auditHistory.getAuditKey());
        response.setDocumentId(auditHistory.getDocumentKey());
        response.setEventType(DocumentAuditEventType.valueOf(auditHistory.getEventType()));
        response.setEventStatus(auditHistory.getEventStatus());
        response.setScore(auditHistory.getScore());
        response.setSummary(auditHistory.getSummary());
        response.setDetails(auditHistory.getDetails());
        response.setCreatedAt(auditHistory.getCreatedAt());
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
