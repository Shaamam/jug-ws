# Session 1: LLM as API - Workshop

## 🎯 Learning Objectives

By the end of this session, you will:
- Integrate OpenAI with Spring AI
- Make basic chat completions
- Use system prompts for context
- Implement conversation memory
- Work with prompt templates
- Generate structured output from LLMs

## 📋 Prerequisites

- Java 21 installed
- OpenAI API key (get one at https://platform.openai.com)
- IDE (IntelliJ IDEA / VS Code)
- Basic understanding of Spring Boot

## 🏗️ Project Setup

### Step 1: Environment Configuration

Create a `.env` file in the project root:

```bash
OPENAI_API_KEY=sk-your-api-key-here
```

### Step 2: Add Spring AI Dependencies

Open `build.gradle` and **uncomment** the Spring AI dependencies:

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
}
```

**Why?** The BOM (Bill of Materials) manages Spring AI versions, and the OpenAI starter provides the `ChatModel` interface.

### Step 3: Add Application Properties

Open `src/main/resources/application.properties` and add:

```properties
spring.application.name=session1-llm-as-api

# OpenAI Configuration
spring.ai.openai.chat.options.model=gpt-4o-mini
spring.ai.model.chat=openai
spring.ai.openai.chat.api-key=${OPENAI_API_KEY}

```

**Key Properties:**
- `spring.ai.openai.chat.options.model` - Which GPT model to use
- `spring.ai.openai.chat.api-key` - Your API key (from .env file)
- Disable settings prevent Spring Boot from trying to auto-configure unused features

### Step 4: Run the Application

```bash
# Using Gradle
./gradlew bootRun

# Or using Taskfile (if available)
task dev
```

The application will start on `http://localhost:8080`

---

## 💻 Implementation Steps

### Exercise 1: Plain Chat (Simple Q&A)

**Objective:** Create a basic endpoint that sends a question to OpenAI and returns the answer.

**Endpoint:** `POST /api/chatplain`

**Key Concepts:**
- `ChatModel` is the core interface for LLM interaction
- Pass a simple string question to get a string response
- This is the simplest form of LLM integration

**Implementation:**

Replace the TODO line in the `askQuestion` method with:

```java
String answer = chatModel.call(question);
```

**Complete Method (for reference):**
```java
@PostMapping("/api/chatplain")
public ChatBotResponse askQuestion(@RequestBody ChatBotRequest chatBotRequest) {
    String question = chatBotRequest.question();
    log.info("Received question: {}", question);
    
    String answer = chatModel.call(question);
    
    log.info("Generated answer: {}", answer);
    return new ChatBotResponse(question, answer);
}
```

**Test:**
```bash
curl -X POST http://localhost:8080/api/chatplain \
  -H "Content-Type: application/json" \
  -d '{"question": "What is Spring AI?"}'
```

**Expected Output:** A JSON response with the question and AI-generated answer.

---

### Exercise 2: Chat with System Prompt

**Objective:** Add context to the conversation using system messages.

**Endpoint:** `POST /api/chat`

**Key Concepts:**
- **System Message:** Sets the behavior/context for the AI (like a persona or instructions)
- **User Message:** The actual user question
- **Prompt:** Container for multiple messages that form the conversation context

**Implementation:**

Replace the TODO line in the `chatWithSystem` method with:

```java
Prompt prompt = new Prompt(List.of(
    new SystemMessage("You are a helpful assistant for a tech workshop. Keep answers concise and practical."),
    new UserMessage(question)
));

ChatResponse response = chatModel.call(prompt);
String answer = response.getResult().getOutput().getText();
```

**Complete Method (for reference):**
```java
@PostMapping("/api/chat")
public ChatBotResponse chatWithSystem(@RequestBody ChatBotRequest chatBotRequest) {
    String question = chatBotRequest.question();
    log.info("Chat with system prompt, question: {}", question);

    Prompt prompt = new Prompt(List.of(
        new SystemMessage("You are a helpful assistant for a tech workshop. Keep answers concise and practical."),
        new UserMessage(question)
    ));

    ChatResponse response = chatModel.call(prompt);
    String answer = response.getResult().getOutput().getText();

    return new ChatBotResponse(question, answer);
}
```

**Test:**
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "Explain microservices in 2 sentences"}'
```

**Notice:** The response should be concise due to the system prompt!

---

### Exercise 3: Conversation Memory

**Objective:** Maintain conversation context across multiple requests.

**Endpoint:** `POST /api/chatmemory`

**Key Concepts:**
- Use `sessionId` to track conversations
- Store message history in a `ConcurrentHashMap` (already declared in the controller)
- Include both user and assistant messages in history
- System message is added only on first interaction per session

**Implementation:**

Replace the TODO line in the `chatWithMemory` method with:

```java
List<org.springframework.ai.chat.messages.Message> history = conversationHistory.computeIfAbsent(sessionId, k -> new ArrayList<>(List.of(
    new SystemMessage("You are a helpful assistant for a tech workshop. Remember the conversation context.")
)));

history.add(new UserMessage(question));

Prompt prompt = new Prompt(history);
ChatResponse response = chatModel.call(prompt);
String answer = response.getResult().getOutput().getText();

// Store assistant response in history for next turn
history.add(response.getResult().getOutput());
```

**Complete Method (for reference):**
```java
@PostMapping("/api/chatmemory")
public ChatBotResponse chatWithMemory(@RequestBody ChatBotRequest chatBotRequest) {
    String question = chatBotRequest.question();
    String sessionId = chatBotRequest.sessionId();
    log.info("Chat with memory, sessionId: {}, question: {}", sessionId, question);

    List<org.springframework.ai.chat.messages.Message> history = conversationHistory.computeIfAbsent(sessionId, k -> new ArrayList<>(List.of(
        new SystemMessage("You are a helpful assistant for a tech workshop. Remember the conversation context.")
    )));

    history.add(new UserMessage(question));

    Prompt prompt = new Prompt(history);
    ChatResponse response = chatModel.call(prompt);
    String answer = response.getResult().getOutput().getText();

    // Store assistant response in history for next turn
    history.add(response.getResult().getOutput());

    return new ChatBotResponse(question, answer);
}
```

**Test:**
```bash
# First message
curl -X POST http://localhost:8080/api/chatmemory \
  -H "Content-Type: application/json" \
  -d '{"question": "My name is Alice", "sessionId": "user123"}'

# Second message - should remember the name!
curl -X POST http://localhost:8080/api/chatmemory \
  -H "Content-Type: application/json" \
  -d '{"question": "What is my name?", "sessionId": "user123"}'
```

**Expected:** The second response should say "Your name is Alice"

---

### Exercise 4: Full ChatResponse with Metadata

**Objective:** Access detailed response metadata (tokens, model info, finish reason).

**Endpoint:** `POST /api/chatresponse`

**Key Concepts:**
- `ChatResponse` contains not just the text, but also metadata
- Metadata includes token usage (prompt tokens, generation tokens, total)
- Finish reason indicates why the generation stopped (STOP, LENGTH, etc.)
- Useful for monitoring costs and debugging

**Implementation:**

Replace the placeholder line in the `chatResponse` method with:

```java
Prompt prompt = new Prompt(List.of(
    new SystemMessage("You are a helpful assistant. Keep answers concise."),
    new UserMessage(question)
));

return chatModel.call(prompt);
```

**Complete Method (for reference):**
```java
@PostMapping("/api/chatresponse")
public ChatResponse chatResponse(@RequestBody ChatBotRequest chatBotRequest) {
    String question = chatBotRequest.question();
    log.info("Chat response with metadata, question: {}", question);

    Prompt prompt = new Prompt(List.of(
        new SystemMessage("You are a helpful assistant. Keep answers concise."),
        new UserMessage(question)
    ));

    return chatModel.call(prompt);
}
```

**Test:**
```bash
curl -X POST http://localhost:8080/api/chatresponse \
  -H "Content-Type: application/json" \
  -d '{"question": "Hello!"}'
```

**Response Structure:**
```json
{
  "result": {
    "output": { "text": "..." },
    "metadata": {
      "finishReason": "STOP",
      "usage": {
        "promptTokens": 25,
        "generationTokens": 12,
        "totalTokens": 37
      }
    }
  }
}
```

---

### Exercise 5: Prompt Templates

**Objective:** Use dynamic templates with placeholders for reusable prompts.

**Endpoint:** `POST /api/chattemplate`

**Key Concepts:**
- `PromptTemplate` allows variable substitution
- Use `{variable}` placeholders in templates
- Makes prompts reusable and maintainable
- Separates prompt structure from content

**Implementation:**

Replace the TODO line in the `chatWithTemplate` method with:

```java
String template = """
    You are an expert on {topic}.
    Answer this question in a {style} way: {question}
    """;

PromptTemplate promptTemplate = new PromptTemplate(template);
Prompt prompt = promptTemplate.create(Map.of(
    "topic", topic,
    "style", style,
    "question", question
));

ChatResponse response = chatModel.call(prompt);
String answer = response.getResult().getOutput().getText();
```

**Complete Method (for reference):**
```java
@PostMapping("/api/chattemplate")
public ChatBotResponse chatWithTemplate(@RequestBody ChatBotRequest chatBotRequest) {
    String question = chatBotRequest.question();
    String topic = chatBotRequest.topic();
    String style = chatBotRequest.style();
    
    log.info("Chat with template, topic: {}, style: {}, question: {}", topic, style, question);

    String template = """
        You are an expert on {topic}.
        Answer this question in a {style} way: {question}
        """;

    PromptTemplate promptTemplate = new PromptTemplate(template);
    Prompt prompt = promptTemplate.create(Map.of(
        "topic", topic,
        "style", style,
        "question", question
    ));

    ChatResponse response = chatModel.call(prompt);
    String answer = response.getResult().getOutput().getText();

    return new ChatBotResponse(question, answer);
}
```

**Test:**
```bash
curl -X POST http://localhost:8080/api/chattemplate \
  -H "Content-Type: application/json" \
  -d '{
    "question": "What are best practices?",
    "topic": "Spring Boot",
    "style": "concise"
  }'
```

---

### Exercise 6: Structured Output

**Objective:** Convert LLM response into type-safe Java objects.

**Endpoint:** `POST /api/chatstructured`

**Key Concepts:**
- `BeanOutputConverter` converts JSON to Java objects
- LLM is instructed to return JSON matching a schema
- Parse response into type-safe objects
- Enables strong typing and validation

**Implementation:**

Replace the TODO line in the `bookRecommendation` method with:

```java
BeanOutputConverter<BookRecommendation> converter = new BeanOutputConverter<>(BookRecommendation.class);

String userMessage = """
    Recommend a book about {subject}.
    
    {format}
    """;

PromptTemplate template = new PromptTemplate(userMessage);
Prompt prompt = template.create(Map.of(
    "subject", subject,
    "format", converter.getFormat()
));

ChatResponse response = chatModel.call(prompt);
return converter.convert(response.getResult().getOutput().getText());
```

**Complete Method (for reference):**
```java
@PostMapping("/api/chatstructured")
public BookRecommendation bookRecommendation(@RequestBody ChatBotRequest chatBotRequest) {
    String subject = chatBotRequest.question();
    log.info("Book recommendation for subject: {}", subject);

    BeanOutputConverter<BookRecommendation> converter = new BeanOutputConverter<>(BookRecommendation.class);

    String userMessage = """
        Recommend a book about {subject}.
        
        {format}
        """;

    PromptTemplate template = new PromptTemplate(userMessage);
    Prompt prompt = template.create(Map.of(
        "subject", subject,
        "format", converter.getFormat()
    ));

    ChatResponse response = chatModel.call(prompt);
    return converter.convert(response.getResult().getOutput().getText());
}
```

**The BookRecommendation Record:**
```java
public record BookRecommendation(
    String title,
    String author,
    String genre
) {}
```

**Test:**
```bash
curl -X POST http://localhost:8080/api/chatstructured \
  -H "Content-Type: application/json" \
  -d '{"question": "artificial intelligence"}'
```

**Expected Response:**
```json
{
  "title": "Artificial Intelligence: A Modern Approach",
  "author": "Stuart Russell and Peter Norvig",
  "genre": "Computer Science"
}
```

**Expected Output:**
```json
{
  "title": "Life 3.0",
  "author": "Max Tegmark",
  "genre": "Science/Technology"
}
```

---

## 🎯 Success Criteria

- ✅ All 6 endpoints working correctly
- ✅ Basic chat returns AI responses
- ✅ System prompts affect response style
- ✅ Memory persists across requests with same sessionId
- ✅ Metadata shows token usage
- ✅ Templates work with variables
- ✅ Structured output returns Java objects

---

## 🐛 Common Issues & Solutions

### Issue: "Unauthorized" or 401 errors
**Solution:** Check your OpenAI API key in `.env` file

### Issue: "Model not found"
**Solution:** Verify `spring.ai.openai.chat.options.model=gpt-4o-mini` in application.properties

### Issue: OutOfMemoryError
**Solution:** Increase JVM memory: `./gradlew bootRun -Xmx2g`

### Issue: Conversation memory not working
**Solution:** Ensure you're sending the same `sessionId` in requests

---

## 📚 Key Concepts Learned

1. **ChatModel** - Core abstraction for LLM interactions
2. **Prompt** - Container for messages sent to LLM
3. **SystemMessage** - Sets AI behavior/context
4. **UserMessage** - User input
5. **ChatResponse** - Full response with metadata
6. **PromptTemplate** - Dynamic prompt generation
7. **BeanOutputConverter** - Type-safe LLM outputs

---

## 🚀 Next Steps

- Try different models (gpt-4, gpt-4o)
- Experiment with system prompts
- Add streaming responses
- Implement conversation export/import
- Move to **Session 2: Tool Calling** to learn function execution

---

## 📖 Additional Resources

- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [OpenAI API Reference](https://platform.openai.com/docs/api-reference)
- [Prompt Engineering Guide](https://www.promptingguide.ai/)
