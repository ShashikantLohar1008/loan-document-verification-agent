package com.shashikant.bankingverification.document.ocr;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TesseractOcrProperties.class)
public class TesseractOcrConfig {
}
