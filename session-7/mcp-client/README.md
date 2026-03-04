# Session 7: MCP Client - Workshop

## 🎯 Learning Objectives

- Build an MCP client that connects to MCP servers
- Consume remote tools via HTTP
- Integrate MCP tools with ChatClient
- Let LLMs invoke tools from remote servers

## 📋 Prerequisites

- Session 4 or 6 MCP server running on port 8080
- OpenAI API key
- Java 21 installed
- Basic understanding of Spring AI

## 🤔 What is an MCP Client?

An **MCP Client** discovers and invokes tools from remote MCP servers.

**Architecture:**
```
User Question → LLM + MCP Client → MCP Server (port 8080) → Tool Execution → Response
```

---

## 🏗️ Setup

### Step 1: Start MCP Server

```bash
# In session4-chapter1 or session6 directory
./gradlew bootRun
```

Server runs on port 8080 and exposes MCP tools.

### Step 2: Add Spring AI Dependencies

Open `build.gradle` and **uncomment** the MCP Client dependencies:

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-webmvc'
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'

    // Uncomment these lines:
    implementation platform("org.springframework.ai:spring-ai-bom:2.0.0-M2")
    // MCP Client — connects to remote MCP servers
    implementation 'org.springframework.ai:spring-ai-starter-mcp-client'
    // LLM — needed because the client decides which tools to call
    implementation 'org.springframework.ai:spring-ai-starter-model-openai'
}
```

**What these do:**
- **spring-ai-starter-mcp-client** - Discovers and invokes tools from MCP servers via HTTP
- **spring-ai-starter-model-openai** - LLM that analyzes questions and decides which tools to call

### Step 3: Add Application Properties

Open `src/main/resources/application.properties` and add:

```properties
spring.application.name=session7-mcp-client
server.port=8081

# MCP Client Configuration
spring.ai.mcp.client.type=SYNC
spring.ai.mcp.client.httpclients.todo-server.url=http://localhost:8080

# OpenAI Configuration
spring.ai.openai.chat.api-key=${OPENAI_API_KEY}
spring.ai.openai.chat.options.model=gpt-4o-mini
spring.ai.model.chat=openai

# Disable unused auto-configs
spring.ai.model.embedding=none
spring.ai.model.embedding.text=none
spring.ai.model.embedding.multimodal=none
spring.ai.model.image=none
spring.ai.model.audio.transcription=none
spring.ai.model.audio.speech=none
spring.ai.model.moderation=none
```

**Key Settings:**
- `server.port=8081` - Client runs on different port than MCP server (8080)
- `spring.ai.mcp.client.type=SYNC` - Synchronous communication with MCP server
- `spring.ai.mcp.client.httpclients.todo-server.url` - Where to find the MCP server

**Environment Variables:**

Create `.env` file:

```bash
OPENAI_API_KEY=sk-your-key-here
```

### Step 4: Run the MCP Client

```bash
./gradlew bootRun
```

The MCP client will start on `http://localhost:8081`

---

## 💻 Implementation

### Exercise: Create McpClientController

**Objective:** Build a chat endpoint that automatically discovers and uses tools from the MCP server.

**File:** `McpClientController.java`

**Key Concepts:**
- `ToolCallbackProvider` - Auto-discovers MCP tools from configured servers
- `ChatClient` - Fluent API for building chat interactions with tools
- The LLM (GPT) analyzes user questions and decides which MCP tools to call

**Implementation:**

Replace the TODO sections in the constructor and chat method:

**Constructor:**
```java
public McpClientController(ChatModel chatModel,
                           ToolCallbackProvider toolCallbackProvider) {
    this.chatClient = ChatClient.builder(chatModel)
            .defaultTools(toolCallbackProvider)  // ← Magic happens here!
            .build();
}
```

**Chat Method:**
```java
@PostMapping("/api/chat")
public ChatBotResponse chat(@RequestBody ChatBotRequest request) {
    log.info("Received question: {}", request.question());

    String answer = chatClient.prompt()
            .user(request.question())
            .call()
            .content();

    log.info("Generated answer: {}", answer);
    return new ChatBotResponse(request.question(), answer);
}
```

**Complete Controller:**
```java
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

    public McpClientController(ChatModel chatModel,
                               ToolCallbackProvider toolCallbackProvider) {
        // ToolCallbackProvider auto-discovers MCP tools from configured servers
        this.chatClient = ChatClient.builder(chatModel)
                .defaultTools(toolCallbackProvider)
                .build();
    }

    @PostMapping("/api/chat")
    public ChatBotResponse chat(@RequestBody ChatBotRequest request) {
        log.info("Received question: {}", request.question());

        String answer = chatClient.prompt()
                .user(request.question())
                .call()
                .content();

        log.info("Generated answer: {}", answer);
        return new ChatBotResponse(request.question(), answer);
    }
}
```

**How It Works:**

1. **Tool Discovery:** When the app starts, `ToolCallbackProvider` makes a request to `http://localhost:8080` to discover available MCP tools

2. **Tool Registration:** Tools like `fetch-all-todos`, `make-todo`, etc. are automatically registered with the `ChatClient`

3. **Question Analysis:** User asks "Create a todo for buying milk"

4. **Tool Selection:** GPT analyzes the question and decides to call the `make-todo` tool

5. **Execution:** ChatClient sends HTTP request to MCP server to execute `make-todo`

6. **Response:** Result is incorporated into the final answer

---
        log.info("Received question: {}", request.question());

        String answer = chatClient.prompt()
                .user(request.question())
                .call()
                .content();

        log.info("Generated answer: {}", answer);
        return new ChatBotResponse(request.question(), answer);
    }
}
```

**Key Points:**
- `ToolCallbackProvider` automatically discovers tools from MCP server
- LLM decides which remote tools to call
- Spring AI handles HTTP communication

---

## 🧪 Testing

```bash
# Create todo via natural language
curl -X POST http://localhost:8081/api/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "Create a todo: Learn Spring AI, not completed yet"}'

# List todos
curl -X POST http://localhost:8081/api/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "Show me all my todos"}'

# Update todo
curl -X POST http://localhost:8081/api/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "Mark todo 1 as completed"}'
```

---

## 🎯 Success Criteria

- ✅ MCP client connects to server
- ✅ Tools auto-discovered
- ✅ LLM invokes remote tools
- ✅ Natural language CRUD operations work

---

## 🚀 Extension Ideas

1. **Multiple Servers:**
   ```properties
   spring.ai.mcp.client.httpclients.todo-server.url=http://localhost:8080
   spring.ai.mcp.client.httpclients.weather-server.url=http://localhost:9090
   ```

2. **Tool Routing:** LLM chooses which server's tools to use

3. **Error Handling:** Handle server unavailability

---

## 📚 Key Concepts

1. **ToolCallbackProvider** - Auto-discovers MCP tools
2. **MCP Client** - Consumes remote tools
3. **HTTP Transport** - Communication protocol
4. **Tool Orchestration** - LLM decides which tools to call

---

## 📖 Resources

- [Spring AI MCP Client](https://docs.spring.io/spring-ai/reference/api/mcp/client.html)
