package tools.muthuishere.session8.agentskills;

import lombok.extern.slf4j.Slf4j;
import org.springaicommunity.agent.tools.ShellTools;
import org.springaicommunity.agent.tools.SkillsTool;
import org.springaicommunity.agent.tools.FileSystemTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.util.Map;

@Slf4j
@RestController
public class AgentSkillsController {

    private final ChatClient chatClient;
    private final ChatClient basicSkillsChatClient;

    public AgentSkillsController(ChatModel chatModel, ResourceLoader resourceLoader) {
        this.chatClient = ChatClient.builder(chatModel)
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

        this.basicSkillsChatClient = ChatClient.builder(chatModel)
                .defaultToolCallbacks(SkillsTool.builder()
//                            .addSkillsDirectory(".claude/skills")

                        .addSkillsResource(resourceLoader.getResource("classpath:basicskills"))
                        .build())
                .defaultTools(FileSystemTools.builder().build())
                .defaultTools(ShellTools.builder().build())
                .defaultToolContext(Map.of(
                        "workingDirectory", Path.of(System.getProperty("user.dir"))
                ))
                .build();


    }

    @PostMapping("/api/chat")
    public ChatBotResponse chat(@RequestBody ChatBotRequest request) {
        log.info("[AGENT SKILLS] Received question: {}", request.question());
        
        ChatResponse response = chatClient.prompt()
                .user(request.question())
                .call()
                .chatResponse();

        String answer = response.getResult().getOutput().getText();

        // Log tool calls metadata
        response.getResults().forEach(result -> {
            if (result.getMetadata() != null && result.getMetadata().getFinishReason() != null) {
                log.info("[AGENT SKILLS] Finish reason: {}", result.getMetadata().getFinishReason());
            }
        });
        
        log.info("[AGENT SKILLS] Generated answer: {}", answer);
        return new ChatBotResponse(request.question(), answer);
    }

    @PostMapping("/api/personalskills/chat")
    public ChatBotResponse personalSkillsChat(@RequestBody ChatBotRequest request) {
        log.info("[PERSONAL SKILLS] Received question: {}", request.question());
        
        ChatResponse response = chatClient.prompt()
                .user(request.question())
                .call()
                .chatResponse();
        
        String answer = response.getResult().getOutput().getText();
        
        // Log tool calls metadata
        response.getResults().forEach(result -> {
            if (result.getMetadata() != null && result.getMetadata().getFinishReason() != null) {
                log.info("[PERSONAL SKILLS] Finish reason: {}", result.getMetadata().getFinishReason());
            }
        });
        
        log.info("[PERSONAL SKILLS] Generated answer: {}", answer);
        return new ChatBotResponse(request.question(), answer);
    }

    @PostMapping("/api/basicskills/chat")
    public ChatBotResponse basicSkillsChat(@RequestBody ChatBotRequest request) {
        log.info("[BASIC SKILLS] Received question: {}", request.question());
        
        ChatResponse response = basicSkillsChatClient.prompt()
                .user(request.question())
                .call()
                .chatResponse();
        
        String answer = response.getResult().getOutput().getText();
        
        // Log tool calls metadata
        response.getResults().forEach(result -> {
            if (result.getMetadata() != null && result.getMetadata().getFinishReason() != null) {
                log.info("[BASIC SKILLS] Finish reason: {}", result.getMetadata().getFinishReason());
            }
        });
        
        log.info("[BASIC SKILLS] Generated answer: {}", answer);
        return new ChatBotResponse(request.question(), answer);
    }
}
