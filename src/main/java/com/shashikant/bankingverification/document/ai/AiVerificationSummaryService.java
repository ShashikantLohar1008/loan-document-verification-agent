package com.shashikant.bankingverification.document.ai;

import com.shashikant.bankingverification.document.dto.DocumentVerificationReportDTO;

public interface AiVerificationSummaryService {

    String generateSummary(DocumentVerificationReportDTO report);
}
