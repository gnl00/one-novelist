package one.nvl.agent;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import one.nvl.SysPrompt;
import one.nvl.domain.Chapter;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Data
public class NovelistAgent {

    private final ChatClient chatClient;

    private String outline;
    private String catalog;
    private String abstracts;
    private Map<String, Map<String, String>> catalogsMap;

    public NovelistAgent(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    private void preset(String outline, String catalog) {
        log.info("NovelistAgent#preset");
        this.outline = outline;
        this.catalog = catalog;
    }

    public Map<String, Map<String, String>> produceAbstractV3(String outline, String cls) {
        log.info("NovelistAgent#produceAbstractV3");

        preset(outline, cls);

        String prompt = SysPrompt.NOVELIST_ABSTRACT_PROMPT_V2 +
                "---\n大纲：\n" + outline +
                "---\n目录：\n" + cls;

        String abstracts = chatClient.prompt(prompt).call().content();
        this.catalogsMap = convertAbstracts(abstracts);

        return catalogsMap;
    }

    public String produceAbstractV2(String outline, String cls) {
        log.info("NovelistAgent#produceAbstractV2");

        preset(outline, cls);

        String prompt = SysPrompt.NOVELIST_ABSTRACT_PROMPT_V2 +
                "---\n大纲：\n" + outline +
                "---\n目录：\n" + cls;

        String abstracts = chatClient.prompt(prompt).call().content();
        this.catalogsMap = convertAbstracts(abstracts);

        return abstracts;
    }

    private Map<String, Map<String, String>> convertAbstracts(String str) {
        if (!StringUtils.hasText(str)) {
            return Collections.emptyMap();
        }
        Map<String, Map<String, String>> contentMap = new HashMap<>();
        String currentVol = null;
        String currentCh = null;
        List<String> strList = str.lines().filter(s -> !s.isEmpty()).toList();
        for (String s : strList) {
            s = s.trim();
            if (s.isEmpty()) {
                continue;
            }
            int spaceIdx = -1;
            if (s.startsWith("第") && (spaceIdx = s.indexOf(" ")) != -1 && '卷' == s.charAt(spaceIdx - 1)) {
                currentVol = s;
                contentMap.putIfAbsent(currentVol, new HashMap<>());
            } else if (s.startsWith("第") && (spaceIdx = s.indexOf(" ")) != -1 && '章' == s.charAt(spaceIdx - 1)) {
                currentCh = s;
                contentMap.get(currentVol).putIfAbsent(currentCh, null);
            } else if (StringUtils.hasText(currentVol) && StringUtils.hasText(currentCh)){
                contentMap.get(currentVol).putIfAbsent(currentCh, s);
            }
        }
        return contentMap;
    }

    public Chapter produce(String vol, String ch) {
        Chapter chapter = null;
        try {
            log.info("NovelistAgent#produce");

            // 将一整卷作为上下文传递
            String  volAbstracts = catalogsMap.get(vol).toString();

            chapter = new Chapter();
            chapter.setAbstracts(catalogsMap.get(vol).get(ch));

            log.info("NovelistAgent#produce abstracts from catalog: {}", volAbstracts);
            String input = SysPrompt.NOVELIST_PRODUCE_PROMPT +
                    "\n---\n大纲：\n" + outline +
                    "\n---\n目录：\n" + catalog +
                    "\n---\n章节摘要：\n" + volAbstracts +
                    String.format("---\n请按照上述内容，完成章节：%s %s\n", vol, ch)
                    ;

            Flux<ChatClientResponse> response = chatClient.prompt(input).stream().chatClientResponse();
            AtomicReference<String> content = new AtomicReference<>("");
            response.subscribe(resp -> {
                String ctx = "";
                if (null != resp.chatResponse() && null != resp.chatResponse().getResult()) {
                    String text = resp.chatResponse().getResult().getOutput().getText();
                    System.out.print(text);
                    ctx += resp.chatResponse().getResult().getOutput().getText();
                }
                content.set(ctx);
            });

            // String content = chatClient.prompt().user(input).call().content();
            chapter.setContent(content.get());
        } catch (Exception e) {
            log.error("produce character error, vol={} ch={}", vol, ch, e);
        }
        return chapter;
    }
}
