package com.shashikant.bankingverification.document.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiSummaryConfiguration {

    @Bean
    public AiVerificationSummaryService aiVerificationSummaryService(
            @Value("${app.ai.api-key:}") String apiKey,
            @Value("${app.ai.base-url:https://api.groq.com/openai}") String baseUrl,
            @Value("${app.ai.model:llama-3.1-8b-instant}") String model,
            @Value("${app.ai.temperature:0.2}") Double temperature) {
        if (isBlank(apiKey)) {
            return new UnconfiguredAiVerificationSummaryService();
        }

        ChatModel chatModel = createChatModel(apiKey, baseUrl, model, temperature);
        ChatClient chatClient = ChatClient.create(chatModel);
        return new SpringAiVerificationSummaryService(chatClient);
    }

    private ChatModel createChatModel(String apiKey, String baseUrl, String model, Double temperature) {
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(removeTrailingV1(baseUrl))
                .apiKey(apiKey)
                .build();

        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                .model(model)
                .temperature(temperature)
                .build();

        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(chatOptions)
                .build();
    }

    private String removeTrailingV1(String baseUrl) {
        if (baseUrl.endsWith("/v1")) {
            return baseUrl.substring(0, baseUrl.length() - 3);
        }
        return baseUrl;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
