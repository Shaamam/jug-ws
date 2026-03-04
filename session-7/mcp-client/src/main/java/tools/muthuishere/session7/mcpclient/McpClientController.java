package tools.muthuishere.session7.mcpclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class McpClientController {

    private final ChatClient chatClient;

    // ============================================================================
    // EXERCISE: Build MCP Client with Tool Discovery
    // Refer to README.md for implementation
    // ============================================================================
    public McpClientController(ChatModel chatModel,
                               ToolCallbackProvider toolCallbackProvider) {
        // TODO: Build ChatClient with tool provider - see README Exercise
        // The ToolCallbackProvider automatically discovers tools from configured MCP servers
        this.chatClient = null; // Replace with actual implementation
    }

    // ============================================================================
    // EXERCISE: Chat endpoint that uses discovered MCP tools
    // Refer to README.md for implementation
    // ============================================================================
    @PostMapping("/api/chat")
    public ChatBotResponse chat(@RequestBody ChatBotRequest request) {
        log.info("Received question: {}", request.question());

        // TODO: Implement chat using ChatClient - see README Exercise
        String answer = "TODO: Implement chat with MCP tools!";

        log.info("Generated answer: {}", answer);
        return new ChatBotResponse(request.question(), answer);
    }
}
