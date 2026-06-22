package com.shashikant.bankingverification.document.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import com.shashikant.bankingverification.document.dto.DocumentVerificationReportDTO;

@Service
@ConditionalOnBean(ChatClient.Builder.class)
@ConditionalOnExpression("'${spring.ai.openai.api-key:}'.length() > 0")
public class SpringAiVerificationSummaryService implements AiVerificationSummaryService {

    private final ChatClient chatClient;

    public SpringAiVerificationSummaryService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public String generateSummary(DocumentVerificationReportDTO report) {
        return chatClient.prompt()
                .system("""
                        You are a banking document verification assistant.
                        Explain verification results in clear, concise language for a loan officer.
                        Do not make approval decisions. Do not override the rule engine.
                        Mention whether manual review is recommended based only on the provided verification data.
                        Keep the answer under 120 words.
                        """)
                .user(buildPrompt(report))
                .call()
                .content();
    }

    private String buildPrompt(DocumentVerificationReportDTO report) {
        return """
                Generate a human-readable verification summary.

                Document id: %s
                Requested document type: %s
                Classified document type: %s
                Document status: %s
                OCR status: %s
                Classification status: %s
                Classification confidence: %s
                Verification status: %s
                Verification score: %s
                Verification summary: %s
                Verification details:
                %s
                """.formatted(
                report.getDocumentId(),
                report.getRequestedDocumentType(),
                report.getClassifiedDocumentType(),
                report.getDocumentStatus(),
                report.getOcrStatus(),
                report.getClassificationStatus(),
                report.getClassificationConfidence(),
                report.getVerificationStatus(),
                report.getVerificationScore(),
                report.getVerificationSummary(),
                report.getVerificationDetails());
    }
}
