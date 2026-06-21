package com.shashikant.bankingverification.document.verification;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.shashikant.bankingverification.document.enums.DocumentType;
import com.shashikant.bankingverification.document.enums.VerificationStatus;

class RuleBasedDocumentVerificationServiceTest {

    @InjectMocks
    private RuleBasedDocumentVerificationService verificationService;

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
    public void verifyReturnsPassedForValidPanText() {
        String text = """
                Income Tax Department
                Permanent Account Number
                ABCDE1234F
                Name Rahul Sharma
                Date of Birth 01/01/1995
                """;

        DocumentVerificationResult result = verificationService.verify(DocumentType.PAN, text);

        assertEquals(result.getVerificationStatus(), VerificationStatus.PASSED);
        assertEquals(result.getScore(), 1.0);
        assertEquals(result.getChecks().size(), 3);
        assertTrue(result.getChecks().stream().allMatch(VerificationCheckResult::isPassed));
    }

    @Test
    public void verifyReturnsPassedForValidBankStatementText() {
        String text = """
                Bank Statement
                Account Number 123456789012
                IFSC HDFC0001234
                Transaction Date Debit Credit Balance
                """;

        DocumentVerificationResult result = verificationService.verify(DocumentType.BANK_STATEMENT, text);

        assertEquals(result.getVerificationStatus(), VerificationStatus.PASSED);
        assertEquals(result.getScore(), 1.0);
        assertEquals(result.getChecks().size(), 3);
    }

    @Test
    public void verifyReturnsFailedForWeakPanText() {
        String text = "ABCDE1234F";

        DocumentVerificationResult result = verificationService.verify(DocumentType.PAN, text);

        assertEquals(result.getVerificationStatus(), VerificationStatus.FAILED);
        assertEquals(result.getScore(), 0.33);
        assertEquals(result.getChecks().stream().filter(VerificationCheckResult::isPassed).count(), 1L);
    }

    @Test
    public void verifyReturnsFailedForUnknownDocumentType() {
        DocumentVerificationResult result = verificationService.verify(DocumentType.UNKNOWN, "Some OCR text");

        assertEquals(result.getVerificationStatus(), VerificationStatus.FAILED);
        assertEquals(result.getScore(), 0.0);
        assertEquals(result.getChecks().size(), 1);
        assertFalse(result.getChecks().getFirst().isPassed());
    }
}
