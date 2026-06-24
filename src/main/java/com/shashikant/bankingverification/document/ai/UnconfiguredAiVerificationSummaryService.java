package com.shashikant.bankingverification.document.ai;

import com.shashikant.bankingverification.common.exception.BadRequestException;
import com.shashikant.bankingverification.document.dto.DocumentVerificationReportDTO;

public class UnconfiguredAiVerificationSummaryService implements AiVerificationSummaryService {

    @Override
    public String generateSummary(DocumentVerificationReportDTO report) {
        throw new BadRequestException("AI provider API key is not configured");
    }
}
