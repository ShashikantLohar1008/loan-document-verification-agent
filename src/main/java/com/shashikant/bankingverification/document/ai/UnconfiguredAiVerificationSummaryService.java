package com.shashikant.bankingverification.document.ai;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import com.shashikant.bankingverification.common.exception.BadRequestException;
import com.shashikant.bankingverification.document.dto.DocumentVerificationReportDTO;

@Service
@ConditionalOnExpression("'${spring.ai.openai.api-key:}'.length() == 0")
public class UnconfiguredAiVerificationSummaryService implements AiVerificationSummaryService {

    @Override
    public String generateSummary(DocumentVerificationReportDTO report) {
        throw new BadRequestException("OpenAI API key is not configured");
    }
}
