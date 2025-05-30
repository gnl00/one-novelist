package one.nvl;

import lombok.extern.slf4j.Slf4j;
import one.nvl.agent.EditorAgent;
import one.nvl.agent.NovelistAgent;
import one.nvl.agent.OutlineAgent;
import one.nvl.domain.Chapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

@Slf4j
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    private OutlineAgent playwright;
    @Autowired
    private NovelistAgent novelist;
    @Autowired
    private EditorAgent editor;

    @Bean
    CommandLineRunner runner(ConfigurableApplicationContext context) {
        return args -> {
            boolean init = false;
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("\nQ:> ");
                String input = scanner.nextLine();
                try {
                    if (Objects.equals(input, "exit")) {
                        break;
                    }
                    if (input.equals("init")) {
                        init = true;
                        System.out.println("NOVELIST initialized!");
                    } else {
                        String outline = "";
                        String catalog = "";
                        String abstracts = "";
                        Map<String, Map<String, String>> catalogMap = new HashMap<>();
                        if (init) {
                            outline = playwright.createOutline(input);;
                            System.out.printf("\nOUTLINE:=>\n%s", outline);
                            catalog = playwright.createCatalog(outline);
                            System.out.printf("\nCATALOG:=>\n%s", catalog);
                            abstracts = novelist.produceAbstractV2(outline, catalog);
                            System.out.printf("\nABSTRACTS:=>\n%s", abstracts);
                            init = false;
                        } else {
                            String[] inputs = input.split("/");
                            Chapter ch = null;
                            if (inputs.length == 2) {
                                ch = novelist.produce(inputs[0], inputs[1]);
                                System.out.printf("\nCHARACTER-CONTENT:=>\n%s", ch.getContent());
                            }
                            String auditedContent = "";
                            if (input.equals("audit") && null != ch) {
                                auditedContent = editor.audit(abstracts, ch.getContent());
                                System.out.printf("\nCHARACTER-CONTENT-AUDITED:=>\n%s", ch.getContent());
                            }
                            if (input.equals("edit")) {
                                String edited = editor.edit(outline, catalog, abstracts, auditedContent);
                                System.out.printf("\nCHARACTER-CONTENT-EDITED:=>\n%s", edited);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("Unknown command input={}", input, e);
                }
            }
            context.close();
            System.exit(0);
        };
    }

}
