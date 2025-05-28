package one.nvl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import one.nvl.agent.EditorAgent;
import one.nvl.agent.NovelistAgent;
import one.nvl.agent.OutlineAgent;
import one.nvl.domain.Catalog;
import one.nvl.domain.Character;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;
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
        /*for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println(beanDefinitionName);
        }*/
        return args -> {
            boolean init = false;
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("\nQ:> ");
                String input = scanner.nextLine();
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
                    if (init) {
                        outline = playwright.createOutline(input);;
                        System.out.printf("\nOUTLINE:=>\n%s", outline);
                        catalog = playwright.createCatalog(outline);
                        System.out.printf("\nCATALOG:=>\n%s", catalog);
                        abstracts = novelist.produceAbstractV2(outline, catalog);
                        System.out.printf("\nABSTRACTS:=>\n%s", abstracts);
                        init = false;
                    } else {
                        String[] inputs = input.split(" ");
                        Character ch = novelist.produce(inputs[0], inputs[1]);
                        System.out.printf("\nCHARACTER-CONTENT-NON-EDIT:=>\n%s", ch.getContent());
                        String edited = editor.edit(outline, catalog, ch.getAbstracts(), ch.getContent());
                        System.out.printf("\nCHARACTER-CONTENT-EDITED:=>\n%s", edited);
                    }
                }
            }
            context.close();
            System.exit(0);
        };
    }

}
