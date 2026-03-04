package io.shaama.textrag.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ChatClientConfig {

    // TODO: Exercise 1 - Inject VertexAiGeminiChatModel and VectorStore (see README)
    // private final VertexAiGeminiChatModel vertexAiGeminiChatModel;
    // private final VectorStore vectorStore;

    // TODO: Exercise 1 - Create QuestionAnswerAdvisor bean (see README)
    // @Bean
    // public QuestionAnswerAdvisor questionAnswerAdvisor() {
    //     return QuestionAnswerAdvisor.builder(vectorStore)
    //             .searchRequest(
    //                     SearchRequest.builder()
    //                             .similarityThreshold(0.8d)
    //                             .topK(5)
    //                             .build()
    //             )
    //             .build();
    // }


    // TODO: Exercise 1 - Create ChatClient bean (see README)
    // @Bean
    // public ChatClient chatClient() {
    //     return ChatClient.create(vertexAiGeminiChatModel);
    // }
}
