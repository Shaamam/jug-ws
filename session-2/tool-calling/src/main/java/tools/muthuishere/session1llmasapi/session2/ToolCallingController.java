package tools.muthuishere.session1llmasapi.session2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ToolCallingController {

    private final ChatModel chatModel;
    private final InventoryTools inventoryTools;
    private final EmployeeTools employeeTools;

    // ============================================================================
    // EXERCISE 4: Chat with Tools - Let LLM Call Your Functions
    // Refer to README.md for implementation
    // ============================================================================
    @PostMapping("/api/tools/chat")
    public ChatBotResponse chatWithTools(@RequestBody ChatBotRequest chatBotRequest) {
        String question = chatBotRequest.question();
        log.info("Tool calling, question: {}", question);

        // TODO: Implement this endpoint - see README Exercise 4
        String response = "TODO: Implement ChatClient with tools!";

        return new ChatBotResponse(question, response);
    }
}
