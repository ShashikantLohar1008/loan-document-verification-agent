package com.shashikant.bankingverification.infrastructure.ocr.tesseract;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.shashikant.bankingverification.application.document.OcrExtractionService;
import com.shashikant.bankingverification.shared.exception.BadRequestException;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

@Service
public class TesseractOcrExtractionService implements OcrExtractionService {

    private static final String PDF_CONTENT_TYPE = "application/pdf";

    private final TesseractOcrProperties tesseractOcrProperties;

    public TesseractOcrExtractionService(TesseractOcrProperties tesseractOcrProperties) {
        this.tesseractOcrProperties = tesseractOcrProperties;
    }

    @Override
    public String extractText(Path documentPath, String contentType) {
        validate(documentPath);

        try {
            ITesseract tesseract = createTesseract();
            if (PDF_CONTENT_TYPE.equals(contentType)) {
                return extractPdfText(tesseract, documentPath);
            }

            return normalize(tesseract.doOCR(documentPath.toFile()));
        } catch (TesseractException | IOException exception) {
            throw new BadRequestException("OCR extraction failed: " + exception.getMessage());
        }
    }

    private ITesseract createTesseract() {
        if (StringUtils.hasText(tesseractOcrProperties.getLibraryPath())) {
            System.setProperty("jna.library.path", tesseractOcrProperties.getLibraryPath());
        }

        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(tesseractOcrProperties.getDatapath());
        tesseract.setLanguage(tesseractOcrProperties.getLanguage());
        return tesseract;
    }

    private String extractPdfText(ITesseract tesseract, Path documentPath) throws IOException, TesseractException {
        StringBuilder extractedText = new StringBuilder();

        try (PDDocument document = Loader.loadPDF(documentPath.toFile())) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); pageIndex++) {
                BufferedImage pageImage = pdfRenderer.renderImageWithDPI(
                        pageIndex,
                        tesseractOcrProperties.getPdfDpi(),
                        ImageType.RGB);
                extractedText.append(tesseract.doOCR(pageImage)).append(System.lineSeparator());
            }
        }

        return normalize(extractedText.toString());
    }

    private void validate(Path documentPath) {
        if (documentPath == null || !Files.exists(documentPath)) {
            throw new BadRequestException("Stored document file was not found");
        }

        File file = documentPath.toFile();
        if (!file.isFile()) {
            throw new BadRequestException("Stored document path is not a file");
        }
    }

    private String normalize(String text) {
        if (text == null) {
            return "";
        }

        return text.strip();
    }
}
