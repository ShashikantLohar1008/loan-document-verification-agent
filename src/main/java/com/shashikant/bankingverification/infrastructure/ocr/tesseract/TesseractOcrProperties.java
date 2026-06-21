package com.shashikant.bankingverification.infrastructure.ocr.tesseract;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ocr.tesseract")
public class TesseractOcrProperties {

    private String datapath;
    private String libraryPath;
    private String language = "eng";
    private float pdfDpi = 300;

    public String getDatapath() {
        return datapath;
    }

    public void setDatapath(String datapath) {
        this.datapath = datapath;
    }

    public String getLibraryPath() {
        return libraryPath;
    }

    public void setLibraryPath(String libraryPath) {
        this.libraryPath = libraryPath;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public float getPdfDpi() {
        return pdfDpi;
    }

    public void setPdfDpi(float pdfDpi) {
        this.pdfDpi = pdfDpi;
    }
}
