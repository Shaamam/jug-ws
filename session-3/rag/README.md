# Session 3: RAG (Retrieval Augmented Generation) with PGVector

## 🎯 Learning Objectives

By the end of this session, you will:
- Understand what RAG is and why it's essential for grounding LLM responses
- Work with vector embeddings and similarity search
- Set up PostgreSQL with pgvector extension
- Implement document ingestion into a vector database
- Build RAG-enabled chat that answers from your proprietary data
- Use Spring AI's `QuestionAnswerAdvisor` for context injection

## 📋 Prerequisites

- Completed Sessions 1 & 2
- Java 21 installed
- Docker Desktop installed and running
- OpenAI API key configured
- Basic understanding of databases

## 🧠 What is RAG?

**RAG (Retrieval Augmented Generation)** solves a fundamental LLM limitation: **LLMs don't know about YOUR data**.

### The Problem:
```
User: "What is our return policy?"
LLM: "I don't have access to your specific policies..."
```

### The RAG Solution:
```
1. Store your documents as vector embeddings in a database
2. User asks: "What is our return policy?"
3. System retrieves relevant document chunks via similarity search
4. LLM receives both the question AND the retrieved context
5. LLM answers based on YOUR data: "Your return policy allows..."
```

### Key Concepts:

**Embeddings:** Numerical representations of text (e.g., `[0.23, -0.41, 0.78, ...]`)  
**Vector Database:** Stores embeddings and enables similarity search  
**PGVector:** PostgreSQL extension for vector storage (HNSW indexing)  
**Similarity Search:** Finds documents semantically similar to the query  
**Context Injection:** Adds retrieved documents to the LLM prompt

---

## 🏗️ Architecture

```
┌──────────────┐      ┌─────────────┐      ┌───────────┐
│   Document   │─────>│  Embedding  │─────>│ PGVector  │
│   (Your PDF) │      │   Model     │      │ Database  │
└──────────────┘      └─────────────┘      └───────────┘
                                                  ↓
                                            Similarity
┌──────────────┐                            Search ↓
│ User Query   │───────────────────────────────────┐
└──────────────┘                                   ↓
       ↓                                    ┌──────────────┐
       └────────────────────────────────────>│  LLM (GPT)   │
                                            │ + Context    │
                                            └──────────────┘
                                                  ↓
                                            Answer grounded
                                            in YOUR data
```

---

## 🐳 Docker Setup

### Step 1: Start PostgreSQL with PGVector

```bash
# Using docker-compose (recommended)
docker-compose up -d

# Or manually:
docker run -d \
  --name pgvector \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=vectordb \
  -p 5432:5432 \
  pgvector/pgvector:pg16
```

### Step 2: Verify Connection

```bash
# Check if container is running
docker ps | grep pgvector

# Test connection
psql -h localhost -U postgres -d vectordb -c "SELECT version();"
```

### Step 3: Add Spring AI Dependencies

Open `build.gradle` and **uncomment** all the Spring AI dependencies:

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-webmvc'
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-webmvc-test'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'

    // Uncomment these lines:
    implementation platform("org.springframework.ai:spring-ai-bom:2.0.0-M2")
    implementation 'org.springframework.ai:spring-ai-starter-model-openai'
    
    // RAG dependencies
    implementation 'org.springframework.ai:spring-ai-starter-vector-store-pgvector'
    implementation 'org.springframework.ai:spring-ai-advisors-vector-store'
}
```

**New Dependencies:**
- `spring-ai-starter-vector-store-pgvector` - PGVector integration
- `spring-ai-advisors-vector-store` - QuestionAnswerAdvisor for RAG

### Step 4: Add Application Properties

Open `src/main/resources/application.properties` and add:

```properties
spring.application.name=session3-rag

# OpenAI Configuration
spring.ai.openai.chat.options.model=gpt-4o-mini
spring.ai.model.chat=openai
spring.ai.openai.chat.api-key=${OPENAI_API_KEY}

# PGVector Configuration
spring.datasource.url=jdbc:postgresql://${POSTGRES_HOST:localhost}:${POSTGRES_PORT:5432}/${POSTGRES_DB:vectordb}
spring.datasource.username=${POSTGRES_USER:postgres}
spring.datasource.password=${POSTGRES_PASSWORD:postgres}

# Vector Store Settings
spring.ai.vectorstore.pgvector.initialize-schema=true
spring.ai.vectorstore.pgvector.index-type=HNSW
spring.ai.vectorstore.pgvector.distance-type=COSINE_DISTANCE

# Disable unused Spring AI features
spring.ai.model.image=none
spring.ai.model.audio.transcription=none
spring.ai.model.audio.speech=none
spring.ai.model.moderation=none
```

**Key Settings:**
- `initialize-schema=true` - Auto-creates vector tables
- `index-type=HNSW` - Fast approximate nearest neighbor search
- `distance-type=COSINE_DISTANCE` - Similarity metric for embeddings

**Environment Variables (Optional):**

Create `.env` file to override defaults:

```bash
OPENAI_API_KEY=sk-your-key-here
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=vectordb
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
```

### Step 5: Run the Application

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

---

## 💻 Implementation Steps

### Exercise 1: Document Ingestion

**Objective:** Store documents as vector embeddings in PGVector.

**Endpoint:** `POST /api/rag/documents`

**Key Concepts:**
- `Document` represents a text chunk with metadata
- Spring AI automatically generates embeddings via OpenAI
- PGVector stores both text and embedding vector

**Implementation:**

Replace the TODO section in `ingestDocuments` method with:

```java
List<Document> documents = requests.stream()
    .map(req -> new Document(req.content()))
    .toList();

vectorStore.add(documents);
```

**Complete Method:**
```java
@PostMapping("/api/rag/documents")
public List<Document> ingestDocuments(@RequestBody List<DocumentRequest> requests) {
    log.info("Ingesting {} documents", requests.size());

    List<Document> documents = requests.stream()
        .map(req -> new Document(req.content()))
        .toList();

    vectorStore.add(documents);
    
    return documents;
}
```

**How It Works:**
1. Receives text documents via REST API
2. Converts each text to a `Document` object
3. Spring AI sends text to OpenAI's embedding model (`text-embedding-3-small`)
4. OpenAI returns 1536-dimensional vector
5. PGVector stores both text and vector

**Test:**
```bash
curl -X POST http://localhost:8080/api/rag/documents \
  -H "Content-Type: application/json" \
  -d '[
    {"content": "Our company offers a 30-day money-back guarantee on all products."},
    {"content": "We ship orders within 24 hours via FedEx and UPS."},
    {"content": "Customer support is available Monday-Friday, 9AM-5PM EST."}
  ]'
```

**Expected:** Returns the ingested documents with generated IDs.

---

### Exercise 2: Similarity Search

**Objective:** Find documents semantically similar to a query using vector search.

**Endpoint:** `POST /api/rag/search`

**Key Concepts:**
- `SearchRequest` configures the search (query, topK, similarity threshold)
- `topK` = number of results to return (e.g., top 3 most similar)
- Similarity is measured by cosine distance between vectors
- HNSW index makes search fast even with millions of documents

**Implementation:**

Replace the TODO section in `search` method with:

```java
SearchRequest searchRequest = SearchRequest.builder()
    .query(request.question())
    .topK(3)
    .build();

return vectorStore.similaritySearch(searchRequest);
```

**Complete Method:**
```java
@PostMapping("/api/rag/search")
public List<Document> search(@RequestBody ChatBotRequest request) {
    log.info("Similarity search for: {}", request.question());

    SearchRequest searchRequest = SearchRequest.builder()
        .query(request.question())
        .topK(3)
        .build();

    return vectorStore.similaritySearch(searchRequest);
}
```

**How It Works:**
1. User query is converted to embedding by OpenAI
2. PGVector computes cosine similarity between query vector and all stored vectors
3. Returns top 3 most similar documents
4. No keyword matching - purely semantic similarity!

**Example:**

Query: **"How long does delivery take?"**  
Most Similar Document: **"We ship orders within 24 hours..."**

Even though "delivery" doesn't appear in the document, the semantic meaning matches!

**Test:**
```bash
curl -X POST http://localhost:8080/api/rag/search \
  -H "Content-Type: application/json" \
  -d '{"question": "What is your refund policy?"}'
```

**Expected:** Returns the document about the 30-day money-back guarantee.

---

### Exercise 3: RAG Chat with QuestionAnswerAdvisor

**Objective:** Build a chat endpoint that answers questions using retrieved context.

**Endpoint:** `POST /api/rag/chat`

**Key Concepts:**
- `QuestionAnswerAdvisor` is a Spring AI advisor that automates RAG
- It automatically:
  - Retrieves relevant documents via similarity search  
  - Injects them into the LLM prompt as context  
  - Instructs the LLM to answer based on that context
- No manual prompt engineering needed!

**Implementation:**

Replace the TODO section in `ragChat` method with:

```java
QuestionAnswerAdvisor advisor = QuestionAnswerAdvisor.builder(vectorStore)
    .searchRequest(SearchRequest.builder().topK(3).build())
    .build();

String answer = ChatClient.create(chatModel)
    .prompt()
    .system("You are a helpful assistant. Answer questions based on the provided context. If the context doesn't contain relevant information, say so.")
    .advisors(advisor)
    .user(question)
    .call()
    .content();
```

**Complete Method:**
```java
@PostMapping("/api/rag/chat")
public ChatBotResponse ragChat(@RequestBody ChatBotRequest request) {
    String question = request.question();
    log.info("RAG chat, question: {}", question);

    QuestionAnswerAdvisor advisor = QuestionAnswerAdvisor.builder(vectorStore)
        .searchRequest(SearchRequest.builder().topK(3).build())
        .build();

    String answer = ChatClient.create(chatModel)
        .prompt()
        .system("You are a helpful assistant. Answer questions based on the provided context. If the context doesn't contain relevant information, say so.")
        .advisors(advisor)
        .user(question)
        .call()
        .content();

    return new ChatBotResponse(question, answer);
}
```

**What QuestionAnswerAdvisor Does:**

Behind the scenes, it transforms your prompt like this:

```
Original:
  User: "What is your refund policy?"

After QuestionAnswerAdvisor:
  System: "You are a helpful assistant. Answer based on context..."
  
  Context (injected automatically):
  - "Our company offers a 30-day money-back guarantee on all products."
  
  User: "What is your refund policy?"
```

The LLM now has YOUR data to answer from!

**Test:**
```bash
curl -X POST http://localhost:8080/api/rag/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "Do you offer refunds?"}'
```

**Expected:** The LLM should mention the 30-day money-back guarantee from your ingested documents.

---

## 🧪 Complete Testing Workflow

### Step 1: Start Infrastructure
```bash
# Start PostgreSQL
docker-compose up -d

# Start Spring Boot app
./gradlew bootRun
```

### Step 2: Ingest Company Knowledge Base
```bash
curl -X POST http://localhost:8080/api/rag/documents \
  -H "Content-Type: application/json" \
  -d '[
    {"content": "Our company was founded in 2020 in San Francisco."},
    {"content": "We offer free shipping on orders over $50."},
    {"content": "Our premium plan costs $29.99/month and includes priority support."},
    {"content": "All products come with a 2-year warranty."}
  ]'
```

### Step 3: Test Similarity Search
```bash
# Should return the warranty document
curl -X POST http://localhost:8080/api/rag/search \
  -H "Content-Type: application/json" \
  -d '{"question": "Tell me about product guarantees"}'
```

### Step 4: Test RAG Chat
```bash
# Should answer using retrieved context
curl -X POST http://localhost:8080/api/rag/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "When was your company founded?"}'
```

**Expected:** "Our company was founded in 2020 in San Francisco."

### Step 5: Test Without Context
```bash
# Ask about something NOT in the documents
curl -X POST http://localhost:8080/api/rag/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "What is quantum computing?"}'
```

**Expected:** "The provided context doesn't contain information about quantum computing."

---

## 🔍 Debugging Tips

### Issue: "Connection refused" to PostgreSQL

**Symptoms:** `java.net.ConnectException: Connection refused`

**Causes & Fixes:**
1. **Docker not running:** Start Docker Desktop
2. **Wrong port:** Check `docker ps` - PostgreSQL should be on port 5432
3. **Container stopped:** Run `docker-compose up -d`

**Verify:**
```bash
docker ps | grep pgvector
```

---

### Issue: "Rate limit exceeded" from OpenAI

**Symptoms:** 429 error when ingesting many documents

**Cause:** OpenAI API rate limits for embedding requests

**Fix:** Implement retry logic or batch processing:
```java
@Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000))
public void ingest(List<Document> docs) {
    vectorStore.add(docs);
}
```

---

### Issue: Search returns irrelevant results

**Symptoms:** Documents returned don't match the query

**Causes & Fixes:**
1. **Not enough documents:** Ingest at least 10-20 documents for meaningful similarity
2. **Documents too generic:** Make documents specific and detailed
3. **Wrong topK:** Try `topK(5)` instead of `topK(3)`
4. **Add similarity threshold:**
   ```java
   SearchRequest.builder()
       .query(query)
       .topK(3)
       .similarityThreshold(0.7)  // Only return docs with >70% similarity
       .build();
   ```

---

### Issue: RAG chat still gives wrong answers

**Symptoms:** LLM ignores retrieved context

**Causes & Fixes:**
1. **System prompt too weak:** Make it more explicit:
   ```java
   .system("IMPORTANT: Answer ONLY using the provided context. Do not use your general knowledge.")
   ```

2. **Context not relevant:** Check what `similaritySearch()` returns - maybe the query isn't finding the right documents

3. **Document chunks too large:** Split documents into smaller chunks (200-500 words)

---

## 📚 Advanced Topics

### Chunking Strategies

For large documents, split into chunks before ingesting:

```java
// Example: Split by paragraphs
String largeDoc = "...very long text...";
List<Document> chunks = Arrays.stream(largeDoc.split("\\n\\n"))
    .map(Document::new)
    .toList();

vectorStore.add(chunks);
```

**Best Practices:**
- Chunk size: 200-500 words
- Include overlap (20-50 words) between chunks
- Preserve context boundaries (don't split mid-sentence)

---

### Adding Metadata for Filtering

```java
Document doc = new Document(
    "Our return policy...",
    Map.of(
        "type", "policy",
        "department", "customer_service",
        "version", "2024.1"
    )
);

vectorStore.add(List.of(doc));

// Search with filters
SearchRequest request = SearchRequest.builder()
    .query("refund policy")
    .topK(3)
    .filterExpression("type == 'policy' && department == 'customer_service'")
    .build();
```

---

### Hybrid Search (Keyword + Semantic)

Combine vector similarity with traditional keyword search for best results.

```java
// PGVector supports hybrid search via SQL
// Example: 60% semantic + 40% keyword matching
// (Implementation requires custom query)
```

---

## 🎯 Key Takeaways

1. **RAG grounds LLMs** in your proprietary data  
2. **Vector embeddings** capture semantic meaning, not just keywords  
3. **PGVector** = Production-ready vector database with HNSW indexing  
4. **QuestionAnswerAdvisor** automates context retrieval and injection  
5. **Chunking strategy** matters for retrieval quality  
6. **Metadata filtering** enables precise document selection  

---

## 🚀 Real-World Use Cases

- **Customer Support:** Answer from product manuals, FAQs, policies  
- **Legal:** Search through contracts, case law, regulations  
- **Healthcare:** Query medical literature, patient records  
- **Code Search:** Find relevant code snippets in large codebases  
- **Research:** Summarize and query academic papers  

---

## 📖 Related Documentation

- [Spring AI Vector Stores](https://docs.spring.io/spring-ai/reference/api/vectordbs.html)
- [PGVector Documentation](https://github.com/pgvector/pgvector)
- [OpenAI Embeddings](https://platform.openai.com/docs/guides/embeddings)
- [HNSW Algorithm](https://arxiv.org/abs/1603.09320)

---

## 🎓 Workshop Progress

✅ **Session 1:** LLM as API  
✅ **Session 2:** Tool Calling  
🎯 **Session 3:** RAG with Vector Stores ← You are here  
📍 **Session 4:** MCP Server Implementation  

---

**Ready to continue?** Move to **Session 4: Model Context Protocol** to learn about standardized tool exposure across applications!
