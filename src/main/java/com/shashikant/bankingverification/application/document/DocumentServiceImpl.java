package com.shashikant.bankingverification.application.document;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shashikant.bankingverification.domain.document.DocumentStatus;
import com.shashikant.bankingverification.domain.document.DocumentType;
import com.shashikant.bankingverification.infrastructure.persistence.entities.document.DocumentEntity;
import com.shashikant.bankingverification.infrastructure.persistence.repositories.document.DocumentRepository;
import com.shashikant.bankingverification.infrastructure.rest.dto.document.DocumentCreateRequestDTO;
import com.shashikant.bankingverification.infrastructure.rest.dto.document.DocumentResponseDTO;
import com.shashikant.bankingverification.shared.exception.ResourceNotFoundException;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentServiceImpl(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
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
        DocumentEntity documentEntity = documentRepository.findById(documentKey)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentKey));

        return toResponse(documentEntity);
    }

    private DocumentResponseDTO toResponse(DocumentEntity documentEntity) {
        DocumentResponseDTO response = new DocumentResponseDTO();
        response.setId(documentEntity.getDocumentKey());
        response.setFileName(documentEntity.getFileName());
        response.setOriginalFileName(documentEntity.getOriginalFileName());
        response.setDocumentType(DocumentType.valueOf(documentEntity.getDocumentType()));
        response.setDocumentStatus(DocumentStatus.valueOf(documentEntity.getDocumentStatus()));
        response.setUploadedAt(documentEntity.getUploadedAt());
        return response;
    }
}
