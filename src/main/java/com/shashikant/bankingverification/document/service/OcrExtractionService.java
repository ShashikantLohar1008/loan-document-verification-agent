package com.shashikant.bankingverification.document.service;

import java.nio.file.Path;

public interface OcrExtractionService {

    String extractText(Path documentPath, String contentType);
}
