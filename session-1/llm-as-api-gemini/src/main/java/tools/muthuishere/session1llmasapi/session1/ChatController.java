package tools.muthuishere.session1llmasapi.session1;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    //    private final ChatClient chatClient;

    // TODO: Inject ChatModel using constructor injection (Lombok @RequiredArgsConstructor already handles this)
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
    // ============================================================================
    @PostMapping("/api/chatplain")
    public ChatBotResponse askQuestion(@RequestBody ChatBotRequest chatBotRequest) {

        String question = chatBotRequest.question();
        log.info("Received question: {}", question);

        // TODO: Call chatModel.call(question) to get the answer
        // String answer = chatModel.call(question);
        
        String answer = "TODO: Implement me! Call chatModel.call(question)";

        log.info("Generated answer: {}", answer);

        return new ChatBotResponse(question, answer);
    }

    // ============================================================================
    // EXERCISE 2: System message + user message
    // ============================================================================
    @PostMapping("/api/chat")
    public ChatBotResponse chatWithSystem(@RequestBody ChatBotRequest chatBotRequest) {

        String question = chatBotRequest.question();
        log.info("Chat with system prompt, question: {}", question);

        // TODO: Create a Prompt with SystemMessage and UserMessage
        // Prompt prompt = new Prompt(List.of(
        //     new SystemMessage("You are a helpful assistant for a tech workshop. Keep answers concise and practical."),
        //     new UserMessage(question)
        // ));

        // TODO: Call chatModel.call(prompt) to get ChatResponse
        // ChatResponse response = chatModel.call(prompt);
        
        // TODO: Extract the text from response
        // String answer = response.getResult().getOutput().getText();

        String answer = "TODO: Implement system + user message pattern";

        return new ChatBotResponse(question, answer);
    }

    // ============================================================================
    // EXERCISE 3: Memory — conversation history tracked by sessionId
    // ============================================================================
    @PostMapping("/api/chatmemory")
    public ChatBotResponse chatWithMemory(@RequestBody ChatBotRequest chatBotRequest) {

        String question = chatBotRequest.question();
        String sessionId = chatBotRequest.sessionId();
        log.info("Chat with memory, sessionId: {}, question: {}", sessionId, question);

//        String answer = chatClient
//                .prompt()
//                .system("You are a helpful assistant for a tech workshop. Remember the conversation context.")
//                .user(question)
//                .advisors(advisor -> advisor.param(CONVERSATION_ID, sessionId))
//                .call()
//                .content();

        String answer = "TODO: Implement conversation memory";

        return new ChatBotResponse(question, answer);
    }

    // ============================================================================
    // EXERCISE 4: Full ChatResponse — exposes metadata, usage (input/output tokens), model info
    // ============================================================================
    @PostMapping("/api/chatresponse")
    public ChatResponse chatResponse(@RequestBody ChatBotRequest chatBotRequest) {

        String question = chatBotRequest.question();
        log.info("Chat response with metadata, question: {}", question);

        // TODO: Create prompt with system + user messages
        // Prompt prompt = new Prompt(List.of(
        //     new SystemMessage("You are a helpful assistant. Keep answers concise."),
        //     new UserMessage(question)
        // ));

        // TODO: Return the full ChatResponse object (not just the text)
        // return chatModel.call(prompt);
        
        // Placeholder response
        Prompt prompt = new Prompt(List.of(
            new SystemMessage("You are a helpful assistant."),
            new UserMessage("TODO: Implement this endpoint")
        ));
        
        return chatModel.call(prompt);
    }

    // ============================================================================
    // EXERCISE 5: Prompt Templates — dynamic prompts with variables
    // ============================================================================
    @PostMapping("/api/chattemplate")
    public ChatBotResponse chatWithTemplate(@RequestBody ChatBotRequest chatBotRequest) {

        String question = chatBotRequest.question();
        String topic = chatBotRequest.topic();
        String style = chatBotRequest.style();
        
        log.info("Chat with template, topic: {}, style: {}, question: {}", topic, style, question);

        // TODO: Create a template string with placeholders
        // String template = """
        //     You are an expert on {topic}.
        //     Answer this question in a {style} way: {question}
        //     """;

        // TODO: Create PromptTemplate and render with values
        // PromptTemplate promptTemplate = new PromptTemplate(template);
        // Prompt prompt = promptTemplate.create(Map.of(
        //     "topic", topic,
        //     "style", style,
        //     "question", question
        // ));

        // TODO: Call chatModel and return response
        // ChatResponse response = chatModel.call(prompt);
        // String answer = response.getResult().getOutput().getText();

        String answer = "TODO: Implement prompt templates";

        return new ChatBotResponse(question, answer);
    }

    // ============================================================================
    // EXERCISE 6: Structured Output — convert LLM response to Java objects
    // ============================================================================
    @PostMapping("/api/chatstructured")
    public BookRecommendation bookRecommendation(@RequestBody ChatBotRequest chatBotRequest) {

        String subject = chatBotRequest.question();
        log.info("Book recommendation for subject: {}", subject);

        // TODO: Create BeanOutputConverter for BookRecommendation
        // BeanOutputConverter<BookRecommendation> converter = 
        //     new BeanOutputConverter<>(BookRecommendation.class);

        // TODO: Get the format instruction from converter
        // String format = converter.getFormat();

        // TODO: Create prompt template with format instructions
        // String userMessage = """
        //     Recommend a book about {subject}.
        //     
        //     {format}
        //     """;

        // TODO: Create and render template
        // PromptTemplate template = new PromptTemplate(userMessage);
        // Prompt prompt = template.create(Map.of(
        //     "subject", subject,
        //     "format", format
        // ));

        // TODO: Get response and convert to BookRecommendation
        // ChatResponse response = chatModel.call(prompt);
        // return converter.convert(response.getResult().getOutput().getText());

        // Placeholder return
        return new BookRecommendation(
                "TODO: Implement structured output",
                "Your Name",
                "Tutorial"
        );
    }
}
