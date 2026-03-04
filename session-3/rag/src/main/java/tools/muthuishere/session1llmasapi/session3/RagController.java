package tools.muthuishere.session1llmasapi.session3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;
import tools.muthuishere.session1llmasapi.session1.ChatBotRequest;
import tools.muthuishere.session1llmasapi.session1.ChatBotResponse;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RagController {

    // TODO: Inject VectorStore and ChatModel
    private final VectorStore vectorStore;
    private final ChatModel chatModel;

    // ============================================================================
    // EXERCISE 1: Ingest documents into PGVector
    // Refer to README.md for implementation
    // ============================================================================
    @PostMapping("/api/rag/documents")
    public List<Document> ingestDocuments(@RequestBody List<DocumentRequest> requests) {
        log.info("Ingesting {} documents", requests.size());

        // TODO: Implement this endpoint - see README Exercise 1
        List<Document> documents = List.of();
        
        return documents;
    }

    // ============================================================================
    // EXERCISE 2: Similarity search — find relevant chunks
    // Refer to README.md for implementation
    // ============================================================================
    @PostMapping("/api/rag/search")
    public List<Document> search(@RequestBody ChatBotRequest request) {
        log.info("Similarity search for: {}", request.question());

        // TODO: Implement this endpoint - see README Exercise 2
        return List.of();
    }

    // ============================================================================
    // EXERCISE 3: RAG chat — QuestionAnswerAdvisor answers from YOUR data
    // Refer to README.md for implementation
    // ============================================================================
    @PostMapping("/api/rag/chat")
    public ChatBotResponse ragChat(@RequestBody ChatBotRequest request) {
        String question = request.question();
        log.info("RAG chat, question: {}", question);

        // TODO: Implement this endpoint - see README Exercise 3
        String answer = "TODO: Implement RAG chat!";

        return new ChatBotResponse(question, answer);
    }
}
