package com.shashikant.bankingverification.document.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.shashikant.bankingverification.document.dto.DocumentCreateRequestDTO;
import com.shashikant.bankingverification.document.dto.DocumentClassificationResponseDTO;
import com.shashikant.bankingverification.document.dto.DocumentOcrResponseDTO;
import com.shashikant.bankingverification.document.dto.DocumentResponseDTO;
import com.shashikant.bankingverification.document.dto.DocumentVerificationResponseDTO;
import com.shashikant.bankingverification.document.enums.DocumentType;

public interface DocumentService {

    DocumentResponseDTO createDocument(DocumentCreateRequestDTO request);

    DocumentResponseDTO uploadDocument(MultipartFile file, DocumentType documentType);

    List<DocumentResponseDTO> getDocuments();

    DocumentResponseDTO getDocumentById(Long documentKey);

    DocumentOcrResponseDTO extractOcr(Long documentKey);

    DocumentOcrResponseDTO getOcr(Long documentKey);

    DocumentClassificationResponseDTO classifyDocument(Long documentKey);

    DocumentClassificationResponseDTO getClassification(Long documentKey);

    DocumentVerificationResponseDTO verifyDocument(Long documentKey);

    DocumentVerificationResponseDTO getVerification(Long documentKey);
}
