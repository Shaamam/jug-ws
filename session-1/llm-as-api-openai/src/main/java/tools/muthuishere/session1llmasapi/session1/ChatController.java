package tools.muthuishere.session1llmasapi.session1;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@Slf4j
@RestController
public class ChatController {

//    private final ChatClient chatClient;

    private final ChatModel chatModel;

    public ChatController(ChatModel chatModel , ChatMemory chatMemory) {
//        MessageChatMemoryAdvisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        this.chatModel = chatModel;
//        this.chatClient = ChatClient.builder(chatModel)
//                .defaultAdvisors(messageChatMemoryAdvisor)
//                .build();
    }

    // ============================================================================
    // EXERCISE 1: Plain question → answer
    // Refer to README.md for the implementation
    // ============================================================================
    @PostMapping("/api/chatplain")
    public ChatBotResponse askQuestion(@RequestBody ChatBotRequest chatBotRequest) {

        String question = chatBotRequest.question();
        log.info("Received question: {}", question);

        // TODO: Implement this endpoint - see README Exercise 1
        String answer = "TODO: Implement me!";

        log.info("Generated answer: {}", answer);

        return new ChatBotResponse(question, answer);
    }

    // ============================================================================
    // EXERCISE 2: System message + user message
    // Refer to README.md for the implementation
    // ============================================================================
    @PostMapping("/api/chat")
    public ChatBotResponse chatWithSystem(@RequestBody ChatBotRequest chatBotRequest) {

        String question = chatBotRequest.question();
        log.info("Chat with system prompt, question: {}", question);

        // TODO: Implement this endpoint - see README Exercise 2
        String answer = "TODO: Implement me!";

        return new ChatBotResponse(question, answer);
    }

    // ============================================================================
    // EXERCISE 3: Memory — conversation history tracked by sessionId
    // Refer to README.md for the implementation
    // ============================================================================
    @PostMapping("/api/chatmemory")
    public ChatBotResponse chatWithMemory(@RequestBody ChatBotRequest chatBotRequest) {

        String question = chatBotRequest.question();
        String sessionId = chatBotRequest.sessionId();
        log.info("Chat with memory, sessionId: {}, question: {}", sessionId, question);

        // TODO: Implement this endpoint - see README Exercise 3
        String answer = "TODO: Implement me!";

        return new ChatBotResponse(question, answer);
    }

    // ============================================================================
    // EXERCISE 4: Full ChatResponse — exposes metadata, usage, model info
    // Refer to README.md for the implementation
    // ============================================================================
    @PostMapping("/api/chatresponse")
    public ChatResponse chatResponse(@RequestBody ChatBotRequest chatBotRequest) {

        String question = chatBotRequest.question();
        log.info("Chat response with metadata, question: {}", question);

        // TODO: Implement this endpoint - see README Exercise 4
        // For now, return a placeholder to make it compile
        return chatModel.call(new Prompt("TODO: Implement this endpoint"));
    }

    // ============================================================================
    // EXERCISE 5: Prompt Templates
    // Refer to README.md for the implementation
    // ============================================================================
    @PostMapping("/api/chattemplate")
    public ChatBotResponse chatWithTemplate(@RequestBody ChatBotRequest chatBotRequest) {

        String question = chatBotRequest.question();
        String topic = chatBotRequest.topic();
        String style = chatBotRequest.style();
        
        log.info("Chat with template, topic: {}, style: {}, question: {}", topic, style, question);

        // TODO: Implement this endpoint - see README Exercise 5
        String answer = "TODO: Implement me!";

        return new ChatBotResponse(question, answer);
    }

    // ============================================================================
    // EXERCISE 6: Structured Output
    // Refer to README.md for the implementation
    // ============================================================================
    @PostMapping("/api/chatstructured")
    public BookRecommendation bookRecommendation(@RequestBody ChatBotRequest chatBotRequest) {

        String subject = chatBotRequest.question();
        log.info("Book recommendation for subject: {}", subject);

        // TODO: Implement this endpoint - see README Exercise 6
        return new BookRecommendation("TODO", "TODO", "TODO");
    }
}