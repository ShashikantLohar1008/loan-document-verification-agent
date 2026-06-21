package com.shashikant.bankingverification.infrastructure.ocr.tesseract;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TesseractOcrProperties.class)
public class TesseractOcrConfig {
}
