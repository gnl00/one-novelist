package one.nvl.config;

import io.micrometer.observation.ObservationRegistry;
import jakarta.annotation.Resource;
import one.nvl.SysPrompt;
import one.nvl.agent.EditorAgent;
import one.nvl.agent.NovelistAgent;
import one.nvl.agent.OutlineAgent;
import org.springframework.ai.chat.client.DefaultChatClientBuilder;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class AgentConfig {

    @Resource(name = "openAiChatModel")
    ChatModel chatModel;

    @Lazy
    @Bean("outline")
    public OutlineAgent outline() {
        DefaultChatClientBuilder chatClientBuilder = new DefaultChatClientBuilder(chatModel, ObservationRegistry.NOOP, null);
        return new OutlineAgent(chatClientBuilder
                .defaultSystem(SysPrompt.OUTLINE_ROLE_PROMPT)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor()
                )
                .build());
    }

    @Lazy
    @Bean("novelist")
    public NovelistAgent novelist(@Qualifier("novelistMemory") ChatMemory novelistMemory) {
        DefaultChatClientBuilder chatClientBuilder = new DefaultChatClientBuilder(chatModel, ObservationRegistry.NOOP, null);
        return new NovelistAgent(chatClientBuilder
                .defaultSystem(SysPrompt.NOVELIST_ROLE_PROMPT)
                .defaultAdvisors(
                        // new MessageChatMemoryAdvisor(novelistMemory),
                        new SimpleLoggerAdvisor()
                )
                .build());
    }

    @Lazy
    @Bean("editor")
    public EditorAgent editor(@Qualifier("editorMemory") ChatMemory editorMemory) {
        DefaultChatClientBuilder chatClientBuilder = new DefaultChatClientBuilder(chatModel, ObservationRegistry.NOOP, null);
        return new EditorAgent(chatClientBuilder
                .defaultSystem(SysPrompt.EDITOR_ROLE_PROMPT)
                .defaultAdvisors(
                        // new MessageChatMemoryAdvisor(editorMemory),
                        new SimpleLoggerAdvisor()
                )
                .build());
    }

}
