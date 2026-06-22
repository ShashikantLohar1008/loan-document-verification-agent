package com.shashikant.bankingverification.document.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.shashikant.bankingverification.document.ai.AiVerificationSummaryService;
import com.shashikant.bankingverification.document.classification.DocumentClassificationResult;
import com.shashikant.bankingverification.document.classification.DocumentClassificationService;
import com.shashikant.bankingverification.document.dto.DocumentAiSummaryResponseDTO;
import com.shashikant.bankingverification.document.dto.DocumentClassificationResponseDTO;
import com.shashikant.bankingverification.document.dto.DocumentOcrResponseDTO;
import com.shashikant.bankingverification.document.dto.DocumentResponseDTO;
import com.shashikant.bankingverification.document.dto.DocumentVerificationReportDTO;
import com.shashikant.bankingverification.document.dto.DocumentVerificationResponseDTO;
import com.shashikant.bankingverification.document.entity.DocumentEntity;
import com.shashikant.bankingverification.document.enums.AiSummaryStatus;
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

class DocumentServiceImplTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private DocumentStorageService documentStorageService;

    @Mock
    private OcrExtractionService ocrExtractionService;

    @Mock
    private DocumentClassificationService documentClassificationService;

    @Mock
    private DocumentVerificationService documentVerificationService;

    @Mock
    private AiVerificationSummaryService aiVerificationSummaryService;

    @Mock
    private MultipartFile multipartFile;

    private DocumentServiceImpl documentService;

    private AutoCloseable mocks;

    @BeforeMethod
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        documentService = new DocumentServiceImpl(
                documentRepository,
                documentStorageService,
                ocrExtractionService,
                documentClassificationService,
                documentVerificationService,
                aiVerificationSummaryService);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void uploadDocumentStoresFileAndSavesMetadata() {
        StoredDocumentFile storedDocumentFile = new StoredDocumentFile(
                "stored-pan.png",
                "pan.png",
                "image/png",
                2048L,
                "C:/uploads/stored-pan.png");

        when(documentStorageService.store(multipartFile)).thenReturn(storedDocumentFile);
        when(documentRepository.save(any(DocumentEntity.class))).thenAnswer(invocation -> {
            DocumentEntity documentEntity = invocation.getArgument(0);
            documentEntity.setDocumentKey(1L);
            return documentEntity;
        });

        DocumentResponseDTO response = documentService.uploadDocument(multipartFile, DocumentType.PAN);

        assertEquals(response.getId(), Long.valueOf(1L));
        assertEquals(response.getFileName(), "stored-pan.png");
        assertEquals(response.getOriginalFileName(), "pan.png");
        assertEquals(response.getDocumentType(), DocumentType.PAN);
        assertEquals(response.getDocumentStatus(), DocumentStatus.UPLOADED);
        assertEquals(response.getOcrStatus(), OcrStatus.PENDING);
        assertEquals(response.getClassificationStatus(), ClassificationStatus.PENDING);
        assertEquals(response.getVerificationStatus(), VerificationStatus.PENDING);
        assertEquals(response.getAiSummaryStatus(), AiSummaryStatus.PENDING);
        assertNotNull(response.getUploadedAt());
        verify(documentStorageService).store(multipartFile);
        verify(documentRepository).save(any(DocumentEntity.class));
    }

    @Test
    public void extractOcrCallsOcrServiceAndUpdatesDocument() {
        DocumentEntity documentEntity = uploadedDocumentEntity();
        when(documentRepository.findById(1L)).thenReturn(Optional.of(documentEntity));
        when(ocrExtractionService.extractText(any(), any())).thenReturn("Permanent Account Number ABCDE1234F");

        DocumentOcrResponseDTO response = documentService.extractOcr(1L);

        assertEquals(response.getDocumentId(), Long.valueOf(1L));
        assertEquals(response.getOcrStatus(), OcrStatus.COMPLETED);
        assertEquals(response.getExtractedText(), "Permanent Account Number ABCDE1234F");
        assertNotNull(response.getProcessedAt());
        assertEquals(documentEntity.getOcrStatus(), OcrStatus.COMPLETED.name());
        assertEquals(documentEntity.getOcrText(), "Permanent Account Number ABCDE1234F");
        verify(documentRepository).findById(1L);
        verify(ocrExtractionService).extractText(any(), any());
    }

    @Test
    public void classifyDocumentCallsClassificationServiceAndStoresResult() {
        DocumentEntity documentEntity = uploadedDocumentEntity();
        documentEntity.setOcrText("Income Tax Department Permanent Account Number ABCDE1234F");
        when(documentRepository.findById(1L)).thenReturn(Optional.of(documentEntity));
        when(documentClassificationService.classify(documentEntity.getOcrText()))
                .thenReturn(new DocumentClassificationResult(DocumentType.PAN, 0.95, "PAN matched"));

        DocumentClassificationResponseDTO response = documentService.classifyDocument(1L);

        assertEquals(response.getDocumentId(), Long.valueOf(1L));
        assertEquals(response.getClassificationStatus(), ClassificationStatus.COMPLETED);
        assertEquals(response.getClassifiedDocumentType(), DocumentType.PAN);
        assertEquals(response.getConfidence(), 0.95);
        assertEquals(response.getReason(), "PAN matched");
        assertNotNull(response.getClassifiedAt());
        assertEquals(documentEntity.getClassificationStatus(), ClassificationStatus.COMPLETED.name());
        assertEquals(documentEntity.getClassifiedDocumentType(), DocumentType.PAN.name());
        verify(documentClassificationService).classify(documentEntity.getOcrText());
    }

    @Test
    public void verifyDocumentCallsVerificationServiceAndUpdatesDocumentStatus() {
        DocumentEntity documentEntity = uploadedDocumentEntity();
        documentEntity.setOcrText("Income Tax Department Permanent Account Number ABCDE1234F");
        documentEntity.setClassifiedDocumentType(DocumentType.PAN.name());
        when(documentRepository.findById(1L)).thenReturn(Optional.of(documentEntity));
        when(documentVerificationService.verify(DocumentType.PAN, documentEntity.getOcrText()))
                .thenReturn(new DocumentVerificationResult(
                        VerificationStatus.PASSED,
                        1.0,
                        "3 of 3 verification rules passed",
                        List.of(new VerificationCheckResult("PAN_NUMBER", true, "PAN number format should be present"))));

        DocumentVerificationResponseDTO response = documentService.verifyDocument(1L);

        assertEquals(response.getDocumentId(), Long.valueOf(1L));
        assertEquals(response.getDocumentType(), DocumentType.PAN);
        assertEquals(response.getVerificationStatus(), VerificationStatus.PASSED);
        assertEquals(response.getScore(), 1.0);
        assertEquals(response.getSummary(), "3 of 3 verification rules passed");
        assertTrue(response.getDetails().contains("PAN_NUMBER: PASSED"));
        assertNotNull(response.getVerifiedAt());
        assertEquals(documentEntity.getDocumentStatus(), DocumentStatus.VERIFIED.name());
        assertEquals(documentEntity.getVerificationStatus(), VerificationStatus.PASSED.name());
        verify(documentVerificationService).verify(DocumentType.PAN, documentEntity.getOcrText());
    }

    @Test
    public void getVerificationReportReturnsCombinedPipelineData() {
        DocumentEntity documentEntity = uploadedDocumentEntity();
        documentEntity.setOcrStatus(OcrStatus.COMPLETED.name());
        documentEntity.setOcrText("Permanent Account Number ABCDE1234F");
        documentEntity.setClassificationStatus(ClassificationStatus.COMPLETED.name());
        documentEntity.setClassifiedDocumentType(DocumentType.PAN.name());
        documentEntity.setClassificationConfidence(0.95);
        documentEntity.setVerificationStatus(VerificationStatus.PASSED.name());
        documentEntity.setVerificationScore(1.0);
        documentEntity.setVerificationSummary("3 of 3 verification rules passed");
        documentEntity.setVerificationDetails("PAN_NUMBER: PASSED - PAN number format should be present");
        documentEntity.setDocumentStatus(DocumentStatus.VERIFIED.name());
        when(documentRepository.findById(1L)).thenReturn(Optional.of(documentEntity));

        DocumentVerificationReportDTO report = documentService.getVerificationReport(1L);

        assertEquals(report.getDocumentId(), Long.valueOf(1L));
        assertEquals(report.getFileName(), "stored-pan.png");
        assertEquals(report.getRequestedDocumentType(), DocumentType.PAN);
        assertEquals(report.getDocumentStatus(), DocumentStatus.VERIFIED);
        assertEquals(report.getOcrStatus(), OcrStatus.COMPLETED);
        assertEquals(report.getClassificationStatus(), ClassificationStatus.COMPLETED);
        assertEquals(report.getClassifiedDocumentType(), DocumentType.PAN);
        assertEquals(report.getVerificationStatus(), VerificationStatus.PASSED);
        assertEquals(report.getVerificationScore(), 1.0);
        assertEquals(report.getVerificationSummary(), "3 of 3 verification rules passed");
        assertNotNull(report.getGeneratedAt());
    }

    @Test
    public void generateAiSummaryCallsAiServiceAndStoresSummary() {
        DocumentEntity documentEntity = uploadedDocumentEntity();
        documentEntity.setOcrStatus(OcrStatus.COMPLETED.name());
        documentEntity.setClassificationStatus(ClassificationStatus.COMPLETED.name());
        documentEntity.setClassifiedDocumentType(DocumentType.PAN.name());
        documentEntity.setClassificationConfidence(0.95);
        documentEntity.setVerificationStatus(VerificationStatus.PASSED.name());
        documentEntity.setVerificationScore(1.0);
        documentEntity.setVerificationSummary("3 of 3 verification rules passed");
        documentEntity.setVerificationDetails("PAN_NUMBER: PASSED - PAN number format should be present");
        documentEntity.setDocumentStatus(DocumentStatus.VERIFIED.name());
        when(documentRepository.findById(1L)).thenReturn(Optional.of(documentEntity));
        when(aiVerificationSummaryService.generateSummary(any(DocumentVerificationReportDTO.class)))
                .thenReturn("The PAN document passed all verification rules and no manual review is required.");

        DocumentAiSummaryResponseDTO response = documentService.generateAiSummary(1L);

        assertEquals(response.getDocumentId(), Long.valueOf(1L));
        assertEquals(response.getAiSummaryStatus(), AiSummaryStatus.COMPLETED);
        assertEquals(response.getSummary(),
                "The PAN document passed all verification rules and no manual review is required.");
        assertNotNull(response.getGeneratedAt());
        assertEquals(documentEntity.getAiSummaryStatus(), AiSummaryStatus.COMPLETED.name());
        assertEquals(documentEntity.getAiSummary(),
                "The PAN document passed all verification rules and no manual review is required.");
        verify(aiVerificationSummaryService).generateSummary(any(DocumentVerificationReportDTO.class));
    }

    private DocumentEntity uploadedDocumentEntity() {
        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setDocumentKey(1L);
        documentEntity.setFileName("stored-pan.png");
        documentEntity.setOriginalFileName("pan.png");
        documentEntity.setDocumentType(DocumentType.PAN.name());
        documentEntity.setDocumentStatus(DocumentStatus.UPLOADED.name());
        documentEntity.setContentType("image/png");
        documentEntity.setFileSize(2048L);
        documentEntity.setStoragePath("C:/uploads/stored-pan.png");
        documentEntity.setOcrStatus(OcrStatus.PENDING.name());
        documentEntity.setClassificationStatus(ClassificationStatus.PENDING.name());
        documentEntity.setVerificationStatus(VerificationStatus.PENDING.name());
        documentEntity.setAiSummaryStatus(AiSummaryStatus.PENDING.name());
        documentEntity.setUploadedAt(java.time.Instant.now());
        return documentEntity;
    }
}
