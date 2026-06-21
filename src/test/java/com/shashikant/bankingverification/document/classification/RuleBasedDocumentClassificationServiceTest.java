package com.shashikant.bankingverification.document.classification;

import static org.testng.Assert.assertEquals;

import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.shashikant.bankingverification.document.enums.DocumentType;

class RuleBasedDocumentClassificationServiceTest {

    @InjectMocks
    private RuleBasedDocumentClassificationService classificationService;

    private AutoCloseable mocks;

    @BeforeMethod
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void classifyReturnsPanWhenTextContainsPanPatternAndKeywords() {
        String text = """
                Income Tax Department
                Government of India
                Permanent Account Number
                ABCDE1234F
                Name Rahul Sharma
                """;

        DocumentClassificationResult result = classificationService.classify(text);

        assertEquals(result.getDocumentType(), DocumentType.PAN);
        assertEquals(result.getConfidence(), 0.95);
    }

    @Test
    public void classifyReturnsAadhaarWhenTextContainsAadhaarPatternAndKeywords() {
        String text = """
                Government of India
                Aadhaar
                2345 6789 1234
                DOB 01/01/1995
                """;

        DocumentClassificationResult result = classificationService.classify(text);

        assertEquals(result.getDocumentType(), DocumentType.AADHAAR);
        assertEquals(result.getConfidence(), 0.93);
    }

    @Test
    public void classifyReturnsBankStatementWhenTextContainsAccountAndTransactionKeywords() {
        String text = """
                Monthly Bank Statement
                Account Number 123456789012
                IFSC HDFC0001234
                Transaction Balance Debit Credit
                """;

        DocumentClassificationResult result = classificationService.classify(text);

        assertEquals(result.getDocumentType(), DocumentType.BANK_STATEMENT);
        assertEquals(result.getConfidence(), 0.88);
    }

    @Test
    public void classifyReturnsUnknownWhenTextDoesNotMatchAnyDocumentRule() {
        String text = "This is a general scanned note without banking or identity document information.";

        DocumentClassificationResult result = classificationService.classify(text);

        assertEquals(result.getDocumentType(), DocumentType.UNKNOWN);
        assertEquals(result.getConfidence(), 0.25);
    }
}
