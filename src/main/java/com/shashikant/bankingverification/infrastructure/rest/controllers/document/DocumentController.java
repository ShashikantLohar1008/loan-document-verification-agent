package com.shashikant.bankingverification.infrastructure.rest.controllers.document;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.multipart.MultipartFile;

import com.shashikant.bankingverification.application.document.DocumentService;
import com.shashikant.bankingverification.domain.document.DocumentType;
import com.shashikant.bankingverification.infrastructure.rest.dto.document.DocumentCreateRequestDTO;
import com.shashikant.bankingverification.infrastructure.rest.dto.document.DocumentOcrResponseDTO;
import com.shashikant.bankingverification.infrastructure.rest.dto.document.DocumentResponseDTO;

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
}
