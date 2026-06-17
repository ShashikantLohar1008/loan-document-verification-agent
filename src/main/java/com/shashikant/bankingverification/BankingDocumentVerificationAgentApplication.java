package com.shashikant.bankingverification;

import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BankingDocumentVerificationAgentApplication {

	static Logger logger = Logger.getLogger(BankingDocumentVerificationAgentApplication.class.getName());


	public static void main(String[] args) {
		
		logger.info("Banking Document Verification Agent Application is starting...");
		SpringApplication.run(BankingDocumentVerificationAgentApplication.class, args);
	}

}
