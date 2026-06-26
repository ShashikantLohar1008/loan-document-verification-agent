package com.shashikant.bankingverification.document.ai.tool;

import java.util.function.Function;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import com.shashikant.bankingverification.document.dto.DocumentVerificationReportDTO;

public class DocumentVerificationReportTool {

    private final Function<Long, DocumentVerificationReportDTO> reportLoader;

    public DocumentVerificationReportTool(Function<Long, DocumentVerificationReportDTO> reportLoader) {
        this.reportLoader = reportLoader;
    }

    @Tool(name = "getVerificationReport", description = "Fetches the rule-based verification report for a document")
    public DocumentVerificationReportDTO getVerificationReport(
            @ToolParam(description = "Document id for which the verification report is required") Long documentId) {
        return reportLoader.apply(documentId);
    }
}
