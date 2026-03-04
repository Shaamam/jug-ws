# TextRAG Skeleton - Document Q&A with RAG Workshop

## 🎯 Learning Objectives

- Build a RAG (Retrieval-Augmented Generation) system with Spring AI
- Process documents (PDF, Word, Text) and store them in a vector database
- Implement semantic search with PGVector
- Use Google Vertex AI Gemini for chat and embeddings
- Configure QuestionAnswerAdvisor for context-aware responses

## 📋 Prerequisites

- Java 21 installed
- PostgreSQL with pgvector extension
- Google Cloud Platform account with Vertex AI API enabled
- GCP credentials configured (`gcloud auth application-default login`)

## 🤔 What is RAG?

**Retrieval-Augmented Generation (RAG)** combines the power of:
1. **Vector Search** - Find relevant document chunks using semantic similarity
2. **LLM Generation** - Generate answers using retrieved context

**Why RAG?**
- **Accurate:** Answers based on your specific documents
- **Up-to-date:** Knowledge from documents loaded at runtime
- **Traceable:** Can cite specific document sections
- **Cost-effective:** No need to fine-tune models

---

## 🏗️ Setup Instructions

Follow these steps to set up your Spring Boot application with RAG capabilities.

### **Step 1: Prerequisites**

- Java 21 installed
- PostgreSQL 16+ with pgvector extension
- Google Cloud Platform account
- GCP credentials: `gcloud auth application-default login`

### **Step 2: Enable Dependencies**

Open `build.gradle` and uncomment the Spring AI dependencies:

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    
    // Uncomment these Spring AI dependencies
    implementation 'org.springframework.ai:spring-ai-advisors-vector-store'
    implementation 'org.springframework.ai:spring-ai-starter-model-vertex-ai-embedding'
    implementation 'org.springframework.ai:spring-ai-starter-model-vertex-ai-gemini'
    implementation 'org.springframework.ai:spring-ai-starter-vector-store-pgvector'

    // Other dependencies already uncommented...
}
```

**What each dependency does:**
- `spring-ai-advisors-vector-store`: Provides QuestionAnswerAdvisor for RAG workflow
- `spring-ai-starter-model-vertex-ai-embedding`: Google Vertex AI embeddings (text-embedding-005)
- `spring-ai-starter-model-vertex-ai-gemini`: Google Gemini chat models
- `spring-ai-starter-vector-store-pgvector`: PostgreSQL vector store integration

**After uncommenting**, refresh Gradle dependencies:
```bash
./gradlew clean build
```

### **Step 3: Setup PostgreSQL with pgvector**

Install and configure PostgreSQL with pgvector extension:

**Using Docker:**
```bash
docker run -d \
  --name postgres-pgvector \
  -e POSTGRES_USER=shaama \
  -e POSTGRES_PASSWORD=xyz \
  -e POSTGRES_DB=work \
  -p 5432:5432 \
  pgvector/pgvector:pg16
```

**Or using Homebrew (macOS):**
```bash
# Install PostgreSQL
brew install postgresql@16

# Install pgvector
brew install pgvector

# Start PostgreSQL
brew services start postgresql@16

# Create database
createdb work
psql work -c "CREATE EXTENSION IF NOT EXISTS vector;"
```

**Verify installation:**
```bash
psql -U shaama -d work -c "SELECT * FROM pg_extension WHERE extname = 'vector';"
```

### **Step 4: Configure Application Properties**

Open `src/main/resources/application.properties` and replace with:

```properties
spring.application.name=textrag-skeleton

server.port=8080

# Google Vertex AI - Gemini Chat Model
spring.ai.vertex.ai.gemini.project-id=your-gcp-project-id
spring.ai.vertex.ai.gemini.location=us-central1
spring.ai.vertex.ai.gemini.chat.options.model=gemini-2.0-flash-exp
spring.ai.vertex.ai.gemini.chat.options.temperature=0.7

# PostgreSQL Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/work
spring.datasource.username=shaama
spring.datasource.password=xyz
spring.datasource.driver-class-name=org.postgresql.Driver

# Google Vertex AI - Embeddings
spring.ai.vertex.ai.embedding.project-id=your-gcp-project-id
spring.ai.vertex.ai.embedding.location=us-central1
spring.ai.vertex.ai.embedding.text.options.model=text-embedding-005

# PGVector Configuration
spring.ai.vectorstore.pgvector.initialize-schema=true
spring.ai.vectorstore.pgvector.remove-existing-vector-store-table=true
spring.ai.vectorstore.pgvector.dimensions=768
spring.ai.vectorstore.pgvector.table-name=velocity_motors_data
spring.ai.vectorstore.pgvector.schema-validation=false
spring.ai.vectorstore.pgvector.index-type=NONE

# Logging
logging.level.io.shaama.textrag=INFO
logging.level.org.springframework.ai=DEBUG
```

**Important Configuration Notes:**
- Replace `your-gcp-project-id` with your actual GCP project ID
- `dimensions=768` matches text-embedding-005 model output size
- `remove-existing-vector-store-table=true` clears data on restart (change to false in production)
- Make sure your GCP credentials are configured: `gcloud auth application-default login`

### **Step 5: Authenticate with Google Cloud**

```bash
# Login to GCP
gcloud auth application-default login

# Verify credentials
gcloud auth application-default print-access-token
```

### **Step 6: Run the Application**

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

---

## 💻 Implementation

### Exercise 1: Create ChatClient Configuration

**Objective:** Configure ChatClient with QuestionAnswerAdvisor for RAG.

**File:** `ChatClientConfig.java`

**Key Concepts:**
- `QuestionAnswerAdvisor` - Automatically retrieves relevant documents and adds them to the prompt context
- `SearchRequest` - Configures similarity threshold and number of results (topK)
- `VectorStore` - Interface for vector similarity search

**Implementation:**

Replace the TODO sections in `ChatClientConfig.java`:

```java
package io.shaama.textrag.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ChatClientConfig {

    private final VertexAiGeminiChatModel vertexAiGeminiChatModel;
    private final VectorStore vectorStore;

    @Bean
    public QuestionAnswerAdvisor questionAnswerAdvisor() {
        return QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(
                        SearchRequest.builder()
                                .similarityThreshold(0.8d)  // Only retrieve chunks with 80%+ similarity
                                .topK(5)                    // Retrieve top 5 most relevant chunks
                                .build()
                )
                .build();
    }

    @Bean
    public ChatClient chatClient() {
        return ChatClient.create(vertexAiGeminiChatModel);
    }
}
```

**How It Works:**
1. `QuestionAnswerAdvisor` intercepts user questions
2. Converts question to embedding vector
3. Searches vector store for similar document chunks
4. Adds retrieved chunks to the prompt context
5. LLM generates answer using the context

---

### Exercise 2: Implement ChatService

**Objective:** Build the service that uses ChatClient with QuestionAnswerAdvisor.

**File:** `ChatService.java`

**Implementation:**

Replace the TODO sections in `ChatService.java`:

```java
package io.shaama.textrag.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatClient chatClient;
    private final QuestionAnswerAdvisor questionAnswerAdvisor;

    public ChatResponse getAnswer(ChatRequest chatRequest) {
        String question = chatRequest.Question();
        String systemPrompt = getSystemPrompt();

        String answer = chatClient
                .prompt()
                .system(systemPrompt)
                .user(question)
                .advisors(questionAnswerAdvisor)  // ← RAG magic happens here!
                .call()
                .content();

        return new ChatResponse(question, answer);
    }

    private static String getSystemPrompt() {
        return  "You are TextRAG Assistant, a specialized AI helper for document question-answering using Retrieval-Augmented Generation (RAG). " +
                "Your role is to provide accurate, helpful answers based on the uploaded document content. " +
                "When answering questions: " +
                "1. Prioritize information from the provided document context " +
                "2. Be precise and cite relevant sections when possible " +
                "3. If the answer isn't in the documents, clearly state that " +
                "4. Provide concise but comprehensive responses " +
                "5. Ask clarifying questions if the user's query is ambiguous " +
                "Always be helpful, accurate, and maintain a professional tone. **Strictly** Dont give result in markdown only answer in PLAIN-TEXT format";
    }
}
```

**Update ChatController:**

In `ChatController.java`, uncomment the implementation:

```java
@PostMapping
public ResponseEntity<ChatResponse> askQuestion(@RequestBody ChatRequest chatRequest) {
    try {
        log.info("Received question: {}", chatRequest.Question());
        ChatResponse response = chatService.getAnswer(chatRequest);
        log.info("Generated response: {}", response.answer());
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        log.error("Error processing question: {}", e.getMessage(), e);
        return ResponseEntity.internalServerError().build();
    }
}
```

---

### Exercise 3: Implement Vector Store Service

**Objective:** Store document chunks in the vector database.

**File:** `VectorStoreService.java`

**Implementation:**

Replace the TODO section in `VectorStoreService.java`:

```java
package io.shaama.textrag.rag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VectorStoreService {

    private final VectorStore vectorStore;

    public String addToVectorStore(List<String> contents){
        // Convert strings to Document objects
        List<Document> documents = contents
                .parallelStream()
                .map(Document::new)  // Each chunk becomes a Document
                .toList();

        // Store documents (automatically generates embeddings)
        vectorStore.add(documents);

        log.info("Added {} documents to vector store", documents.size());
        return "Data Added";
    }
}
```

**Update DocumentController:**

In `DocumentController.java`, uncomment the implementation:

```java
@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<String> uploadDocument(@RequestParam("doc") MultipartFile doc) {
    log.info("Processing document: {}", doc.getOriginalFilename());

    try {
        List<String> processedChunks = documentProcessingService.processDocument(doc);
        log.info("Successfully processed document into {} chunks", processedChunks.size());
        String responseMessage = vectorStoreService.addToVectorStore(processedChunks);
        return ResponseEntity.ok(responseMessage);
    } catch (Exception e) {
        log.error("Error processing document: {}", e.getMessage(), e);
        return ResponseEntity.internalServerError().build();
    }
}
```

**Note:** `DocumentProcessingService.java` is already complete - it handles PDF, Word, and text file extraction.

---

## 🧪 Testing

### 1. Upload a Document

Create a test file `test-doc.txt`:
```text
Spring AI is a framework for building AI applications with Spring Boot.

It provides abstractions for working with various AI models including OpenAI, Azure OpenAI, and Google Vertex AI.

Spring AI supports vector stores like PGVector, Chroma, and Pinecone for RAG applications.

The framework includes advisors like QuestionAnswerAdvisor for implementing retrieval-augmented generation patterns.
```

Upload it:
```bash
curl -X POST http://localhost:8080/api/v1/rag/documents/upload \
  -F "doc=@test-doc.txt"
```

Expected response: `Data Added`

### 2. Ask Questions

```bash
# Question about Spring AI
curl -X POST http://localhost:8080/api/v1/chat \
  -H "Content-Type: application/json" \
  -d '{"Question": "What is Spring AI?"}'

# Question about vector stores
curl -X POST http://localhost:8080/api/v1/chat \
  -H "Content-Type: application/json" \
  -d '{"Question": "Which vector stores does Spring AI support?"}'

# Question about RAG
curl -X POST http://localhost:8080/api/v1/chat \
  -H "Content-Type: application/json" \
  -d '{"Question": "What is QuestionAnswerAdvisor used for?"}'
```

### 3. Test with PDF

```bash
# Upload a PDF file
curl -X POST http://localhost:8080/api/v1/rag/documents/upload \
  -F "doc=@your-document.pdf"

# Ask questions about the PDF content
curl -X POST http://localhost:8080/api/v1/chat \
  -H "Content-Type: application/json" \
  -d '{"Question": "Summarize the main points of the document"}'
```

---

## 🎯 Success Criteria

- ✅ PostgreSQL with pgvector running
- ✅ GCP credentials configured
- ✅ Application starts without errors
- ✅ Documents uploaded successfully
- ✅ Questions answered using document context
- ✅ Answers are accurate and cite document sources

---

## 🚀 Extension Ideas

1. **Metadata Filtering:** Add metadata to documents (author, date, category) and filter search results
2. **Hybrid Search:** Combine vector search with keyword search
3. **Multi-turn Conversations:** Maintain conversation history across questions
4. **Document Management:** Add endpoints to list, delete, and update documents
5. **Streaming Responses:** Use ChatClient's stream API for real-time responses
6. **Custom Chunking:** Implement smarter chunking strategies (sentence-based, paragraph-based)

---

## 📚 Key Concepts

### RAG Architecture
```
User Question
     ↓
Convert to Embedding
     ↓
Vector Search (PGVector)
     ↓
Retrieve Top K Documents
     ↓
Add to Prompt Context
     ↓
LLM Generates Answer
     ↓
Return Response
```

### Configuration Tuning

**Similarity Threshold:**
- `0.7` - Loose matching, more results
- `0.8` - Balanced (recommended)
- `0.9` - Strict matching, fewer results

**TopK:**
- `3-5` - Good for specific questions
- `5-10` - Better for broad topics
- `10+` - Risk of context overflow

**Embedding Models:**
- `text-embedding-005` - Latest, best quality (768 dimensions)
- `textembedding-gecko@003` - Faster, smaller (768 dimensions)

---

## 🐛 Troubleshooting

**Error: "Cannot connect to PostgreSQL"**
- Check if PostgreSQL is running: `pg_isready`
- Verify credentials in application.properties
- Ensure pgvector extension is installed

**Error: "GCP authentication failed"**
- Run: `gcloud auth application-default login`
- Verify project ID in application.properties
- Check Vertex AI API is enabled in GCP Console

**Error: "Vector dimension mismatch"**
- Ensure `spring.ai.vectorstore.pgvector.dimensions=768` matches your embedding model
- text-embedding-005 uses 768 dimensions

**Documents uploaded but no results found:**
- Lower similarity threshold (try 0.7)
- Increase topK value (try 10)
- Check logs for embedding generation errors

---

## 📖 Resources

- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [Google Vertex AI](https://cloud.google.com/vertex-ai/docs)
- [PGVector Extension](https://github.com/pgvector/pgvector)
- [RAG Best Practices](https://www.pinecone.io/learn/retrieval-augmented-generation/)

---

## 🏁 What You've Built

A complete RAG system that:
- Processes multi-format documents (PDF, Word, Text)
- Stores document embeddings in PostgreSQL with pgvector
- Performs semantic search to find relevant context
- Uses Google Vertex AI Gemini for intelligent question answering
- Provides API endpoints for document upload and chat

This is production-ready foundation for building document Q&A systems, knowledge bases, and AI-powered search applications!
