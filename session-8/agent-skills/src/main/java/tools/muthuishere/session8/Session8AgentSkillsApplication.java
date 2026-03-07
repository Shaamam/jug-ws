package tools.muthuishere.session8;

import lombok.extern.slf4j.Slf4j;
import org.osgi.resource.Resource;
import org.springaicommunity.agent.tools.FileSystemTools;
import org.springaicommunity.agent.tools.ShellTools;
import org.springaicommunity.agent.tools.SkillsTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import org.springframework.core.io.ResourceLoader;

import java.nio.file.Path;
import java.util.Map;

@Slf4j
@SpringBootApplication
public class Session8AgentSkillsApplication {

    public static void main(String[] args) {
        SpringApplication.run(Session8AgentSkillsApplication.class, args);
    }


    @Bean
    CommandLineRunner demo(ChatClient.Builder chatClientBuilder, ResourceLoader resourceLoader) {
        return args -> {
            ChatClient chatClient = chatClientBuilder
                    .defaultToolCallbacks(SkillsTool.builder()
//                            .addSkillsDirectory(".claude/skills")

                            .addSkillsResource(resourceLoader.getResource("classpath:personalskills"))
                            .build())
                    .defaultTools(FileSystemTools.builder().build())
                    .defaultTools(ShellTools.builder().build())
                    .defaultToolContext(Map.of(
                            "workingDirectory", Path.of(System.getProperty("user.dir"))
                    ))
                    .build();
            System.out.println(chatClient);



            try {

            String response = chatClient.prompt()
                    .user("Speak like dileeep ,kishoore discussing new film")
                    .call()
                    .content();


            log.info(response);
//                System.out.println(response);
            }catch (Exception e) {
                e.printStackTrace();
            }

        };
    }
}
