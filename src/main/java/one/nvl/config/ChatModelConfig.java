package one.nvl.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "chat.model")
public class ChatModelConfig {

    private ModelConfig custom;
    private ModelConfig siliconflow;

    @Data
    static class ModelConfig {
        private String apiKey;
        private String baseUrl;
        private String modelName;
    }

    @Lazy
    @Qualifier("customChatModel")
    @Bean
    public ChatModel customChatModel() {
        return OpenAiChatModel.builder()
                .defaultOptions(OpenAiChatOptions.builder().model(custom.getModelName()).build())
                .openAiApi(OpenAiApi.builder().baseUrl(custom.getBaseUrl()).apiKey(custom.getApiKey()).build())
                .build();
    }

    @Lazy
    @Qualifier("siliconFlowChatModel")
    @Bean
    public ChatModel siliconFlowChatModel() {
        return OpenAiChatModel.builder()
                .defaultOptions(OpenAiChatOptions.builder().model(siliconflow.getModelName()).build())
                .openAiApi(OpenAiApi.builder().baseUrl(siliconflow.getBaseUrl()).apiKey(siliconflow.getApiKey()).build())
                .build();
    }

}
