package com.shashikant.bankingverification.application.document;

import java.util.List;

import com.shashikant.bankingverification.infrastructure.rest.dto.document.DocumentCreateRequestDTO;
import com.shashikant.bankingverification.infrastructure.rest.dto.document.DocumentResponseDTO;

public interface DocumentService {

    DocumentResponseDTO createDocument(DocumentCreateRequestDTO request);

    List<DocumentResponseDTO> getDocuments();

    DocumentResponseDTO getDocumentById(Long documentKey);
}
