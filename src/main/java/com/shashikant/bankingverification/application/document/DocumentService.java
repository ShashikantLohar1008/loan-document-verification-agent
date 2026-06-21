package com.shashikant.bankingverification.application.document;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.shashikant.bankingverification.domain.document.DocumentType;
import com.shashikant.bankingverification.infrastructure.rest.dto.document.DocumentCreateRequestDTO;
import com.shashikant.bankingverification.infrastructure.rest.dto.document.DocumentResponseDTO;

public interface DocumentService {

    DocumentResponseDTO createDocument(DocumentCreateRequestDTO request);

    DocumentResponseDTO uploadDocument(MultipartFile file, DocumentType documentType);

    List<DocumentResponseDTO> getDocuments();

    DocumentResponseDTO getDocumentById(Long documentKey);
}
