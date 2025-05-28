package one.nvl.agent;

import one.nvl.SysPrompt;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.util.Assert;

public class EditorAgent {

    private final ChatClient chatClient;

    public EditorAgent(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String edit(String outline, String catalog, String abstracts, String content) {
        Assert.notNull(outline, "outline must not be null");
        Assert.notNull(catalog, "catalog must not be null");
        Assert.notNull(abstracts, "abstracts must not be null");
        Assert.notNull(content, "context must not be null");

        String prompt = SysPrompt.EDITOR_EDIT_PROMPT
                + "\n大纲：\n" + outline
                + "\n目录：\n" + catalog
                + "\n章节摘要：\n" + abstracts
                + "\n待编辑章节内容：\n" + content
                ;

        return chatClient.prompt(prompt).call().content();
    }

}
