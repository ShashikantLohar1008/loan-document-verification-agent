package com.shashikant.bankingverification.document.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.shashikant.bankingverification.document.dto.DocumentAuditHistoryResponseDTO;
import com.shashikant.bankingverification.document.dto.DocumentCreateRequestDTO;
import com.shashikant.bankingverification.document.dto.DocumentAiSummaryResponseDTO;
import com.shashikant.bankingverification.document.dto.DocumentClassificationResponseDTO;
import com.shashikant.bankingverification.document.dto.DocumentOcrResponseDTO;
import com.shashikant.bankingverification.document.dto.DocumentResponseDTO;
import com.shashikant.bankingverification.document.dto.DocumentVerificationReportDTO;
import com.shashikant.bankingverification.document.dto.DocumentVerificationResponseDTO;
import com.shashikant.bankingverification.document.enums.DocumentType;
import com.shashikant.bankingverification.document.service.DocumentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public ResponseEntity<DocumentResponseDTO> createDocument(@Valid @RequestBody DocumentCreateRequestDTO request) {
        DocumentResponseDTO response = documentService.createDocument(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponseDTO> uploadDocument(@RequestParam("file") MultipartFile file,
            @RequestParam("documentType") DocumentType documentType) {
        DocumentResponseDTO response = documentService.uploadDocument(file, documentType);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/documents/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponseDTO>> getDocuments() {
        return ResponseEntity.ok(documentService.getDocuments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponseDTO> getDocumentById(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    @PostMapping("/{id}/ocr")
    public ResponseEntity<DocumentOcrResponseDTO> extractOcr(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.extractOcr(id));
    }

    @GetMapping("/{id}/ocr")
    public ResponseEntity<DocumentOcrResponseDTO> getOcr(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getOcr(id));
    }

    @PostMapping("/{id}/classify")
    public ResponseEntity<DocumentClassificationResponseDTO> classifyDocument(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.classifyDocument(id));
    }

    @GetMapping("/{id}/classification")
    public ResponseEntity<DocumentClassificationResponseDTO> getClassification(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getClassification(id));
    }

    @PostMapping("/{id}/verify")
    public ResponseEntity<DocumentVerificationResponseDTO> verifyDocument(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.verifyDocument(id));
    }

    @GetMapping("/{id}/verification")
    public ResponseEntity<DocumentVerificationResponseDTO> getVerification(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getVerification(id));
    }

    @GetMapping("/{id}/report")
    public ResponseEntity<DocumentVerificationReportDTO> getVerificationReport(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getVerificationReport(id));
    }

    @PostMapping("/{id}/ai-summary")
    public ResponseEntity<DocumentAiSummaryResponseDTO> generateAiSummary(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.generateAiSummary(id));
    }

    @PostMapping("/{id}/ai-tool-summary")
    public ResponseEntity<DocumentAiSummaryResponseDTO> generateAiToolSummary(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.generateAiToolSummary(id));
    }

    @GetMapping("/{id}/ai-summary")
    public ResponseEntity<DocumentAiSummaryResponseDTO> getAiSummary(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getAiSummary(id));
    }

    @GetMapping("/{id}/audit-history")
    public ResponseEntity<List<DocumentAuditHistoryResponseDTO>> getAuditHistory(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getAuditHistory(id));
    }
}
