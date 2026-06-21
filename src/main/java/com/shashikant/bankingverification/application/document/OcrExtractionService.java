package com.shashikant.bankingverification.application.document;

import java.nio.file.Path;

public interface OcrExtractionService {

    String extractText(Path documentPath, String contentType);
}
