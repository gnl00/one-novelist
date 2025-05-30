package one.nvl.agent;

import lombok.extern.slf4j.Slf4j;
import one.nvl.SysPrompt;
import org.springframework.ai.chat.client.ChatClient;

@Slf4j
public class OutlineAgent {

    private final ChatClient chatClient;

    public OutlineAgent(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String createOutlineStream(String theme) {
        log.info("OutlineAgent#createOutlineStream");
        return chatClient.prompt().user(u -> u.text(SysPrompt.OUTLINE_CONTEXT_PROMPT).param("theme", theme)).call().content();
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

        String input = SysPrompt.OUTLINE_CATALOG_PROMPT +
                "\n---\n##大纲内容：\n" + outline;

        return chatClient.prompt().user(u -> u.text(input)).call().content();
    }
}
