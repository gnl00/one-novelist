package one.nvl.agent;

import lombok.extern.slf4j.Slf4j;
import one.nvl.SysPrompt;
import one.nvl.domain.Catalog;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;

@Slf4j
public class OutlineAgent {

    private final ChatClient chatClient;

    public OutlineAgent(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String createOutline(String theme) {
        log.info("OutlineAgent#createOutline");
        return chatClient.prompt().user(u -> u.text(SysPrompt.OUTLINE_CONTEXT_PROMPT).param("theme", theme)).call().content();
    }

    public String createCatalog(String outline) {
        log.info("OutlineAgent#createCatalog");
        /*List<Catalog> catalogs = chatClient.prompt(outline).user(u -> u.text(SysPrompt.OUTLINE_CATALOG_PROMPT_V2)).call().entity(new ParameterizedTypeReference<List<Catalog>>() {
        });
        return catalogs;*/
        return chatClient.prompt(outline).user(u -> u.text(SysPrompt.OUTLINE_CATALOG_PROMPT)).call().content();
    }
}
