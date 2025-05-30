package one.nvl.agent;

import one.nvl.SysPrompt;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientResponse;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicReference;

public class EditorAgent {

    private final ChatClient chatClient;

    public EditorAgent(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String edit(String outline, String catalog, String abstracts, String content) {

        String prompt = SysPrompt.EDITOR_EDIT_PROMPT
                + "\n大纲：\n" + outline
                + "\n目录：\n" + catalog
                + "\n章节摘要：\n" + abstracts
                + "\n待优化章节内容：\n" + content
                ;

        Flux<ChatClientResponse> response = chatClient.prompt(prompt).stream().chatClientResponse();
        AtomicReference<String> edited = new AtomicReference<>("");
        response.subscribe(resp -> {
            String ctx = "";
            if (null != resp.chatResponse() && null != resp.chatResponse().getResult()) {
                System.out.print(resp.chatResponse().getResult().getOutput().getText());
                ctx += resp.chatResponse().getResult().getOutput().getText();
            }
            edited.set(ctx);
        });

        return edited.get();
    }

    public String audit(String abstracts, String content) {

        String prompt = SysPrompt.EDITOR_AUDIT_PROMPT
                + "\n---\n摘要：\n" + abstracts
                + "\n---\n待审核内容：\n" + content
                ;

        Flux<ChatClientResponse> response = chatClient.prompt(prompt).stream().chatClientResponse();
        AtomicReference<String> audited = new AtomicReference<>("");
        response.subscribe(resp -> {
            String ctx = "";
            if (null != resp.chatResponse() && null != resp.chatResponse().getResult()) {
                System.out.print(resp.chatResponse().getResult().getOutput().getText());
                ctx += resp.chatResponse().getResult().getOutput().getText();
            }
            audited.set(ctx);
        });

        return audited.get();
    }
}
