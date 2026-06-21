package com.shashikant.bankingverification.document.verification;

import com.shashikant.bankingverification.document.enums.DocumentType;

public interface DocumentVerificationService {

    DocumentVerificationResult verify(DocumentType documentType, String extractedText);
}
