package tools.muthuishere.session8.agentskills;

import lombok.extern.slf4j.Slf4j;
import org.springaicommunity.agent.tools.SkillsTool;
import org.springaicommunity.agent.tools.FileSystemTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AgentSkillsController {

    private final ChatClient chatClient;

    // ============================================================================
    // EXERCISE: Build ChatClient with Skills and FileSystem Tools
    // Refer to README.md for implementation
    // ============================================================================
    public AgentSkillsController(ChatModel chatModel) {
        // TODO: Build ChatClient with SkillsTool and FileSystemTools - see README Exercise
        this.chatClient = null; // Replace with actual implementation
    }

    // ============================================================================
    // EXERCISE: Chat endpoint that uses skills
    // Refer to README.md for implementation
    // ============================================================================
    @PostMapping("/api/chat")
    public ChatBotResponse chat(@RequestBody ChatBotRequest request) {
        log.info("Received question: {}", request.question());

        // TODO: Implement chat using ChatClient - see README Exercise
        String answer = "TODO: Implement chat with agent skills!";

        log.info("Generated answer: {}", answer);
        return new ChatBotResponse(request.question(), answer);
    }
}
