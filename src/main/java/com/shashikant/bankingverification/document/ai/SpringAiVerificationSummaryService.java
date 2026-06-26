package com.shashikant.bankingverification.document.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.support.ToolCallbacks;

import com.shashikant.bankingverification.document.ai.tool.DocumentVerificationReportTool;
import com.shashikant.bankingverification.document.dto.DocumentVerificationReportDTO;

public class SpringAiVerificationSummaryService implements AiVerificationSummaryService {

    private final ChatClient chatClient;

    public SpringAiVerificationSummaryService(ChatClient chatClient) {
        this.chatClient = chatClient;
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

    @Override
    public String generateSummaryWithTool(Long documentId, DocumentVerificationReportTool documentVerificationReportTool) {
        return chatClient.prompt()
                .system("""
                        You are a banking document verification assistant.
                        Use the getVerificationReport tool to fetch the document verification report.
                        Explain the tool result in clear, concise language for a loan officer.
                        Do not make approval decisions. Do not override the rule engine.
                        Mention whether manual review is recommended based only on the tool result.
                        Keep the answer under 120 words.
                        """)
                .user("Generate a verification summary for document id: " + documentId)
                .toolCallbacks(ToolCallbacks.from(documentVerificationReportTool))
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
