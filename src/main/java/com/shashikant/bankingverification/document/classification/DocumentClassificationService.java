package com.shashikant.bankingverification.document.classification;

public interface DocumentClassificationService {

    DocumentClassificationResult classify(String extractedText);
}
