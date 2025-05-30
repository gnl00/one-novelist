package one.nvl.agent;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import one.nvl.SysPrompt;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class OutlineAgent {

    private final ChatClient chatClient;

    public OutlineAgent(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String createOutlineStream(String theme) {
        log.info("OutlineAgent#createOutlineStream");

        Flux<ChatClientResponse> response = chatClient.prompt().user(u -> u.text(SysPrompt.OUTLINE_CONTEXT_PROMPT).param("theme", theme)).stream().chatClientResponse();
        AtomicReference<String> outline = new AtomicReference<>();
        response.subscribe(resp -> {
            String ctx = "";
            if (null != resp.chatResponse() && null != resp.chatResponse().getResult()) {
                String text = resp.chatResponse().getResult().getOutput().getText();
                System.out.print(text);
                ctx += resp.chatResponse().getResult().getOutput().getText();
            }
            outline.set(ctx);
        });
        return outline.get();
    }

    public Obj createOutline(String theme) {
        log.info("OutlineAgent#createOutline");

        Prompt prompt = PromptTemplate.builder().template(SysPrompt.OUTLINE_CONTEXT_PROMPT).variables(Map.of("theme", theme, "output", SysPrompt.OUTLINE_CONTEXT_OUTPUT_PROMPT)).build().create();

        Obj obj = chatClient.prompt(prompt).call().entity(Obj.class);
        // return chatClient.prompt().user(u -> u.text(SysPrompt.OUTLINE_CONTEXT_PROMPT).param("theme", theme)).call().content();
        return obj;
    }

    @Data
    public static class Obj {
        private List<Map<Object, Object>> catalogs;
        private String outline;
    }

    public String createCatalog(String outline) {
        log.info("OutlineAgent#createCatalog");
        /*chatClient.prompt(outline).user(u -> u.text(SysPrompt.OUTLINE_CATALOG_PROMPT_V2)).call().entity(new ParameterizedTypeReference<List<>>() {
        });
        return catalogs;*/

        String input = SysPrompt.OUTLINE_CATALOG_PROMPT +
                "\n---\n##大纲内容：\n" + outline;

        return chatClient.prompt().user(u -> u.text(input)).call().content();
    }
}
