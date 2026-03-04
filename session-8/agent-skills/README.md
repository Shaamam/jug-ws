# Session 8: Agent Skills - Workshop

## 🎯 Learning Objectives

- Understand skill-based agent architecture
- Create reusable LLM skills as markdown files
- Use SkillsTool for dynamic skill loading
- Integrate FileSystemTools for file operations

## 📋 Prerequisites

- OpenAI API key
- Understanding of prompting

## 🤔 What are Agent Skills?

**Skills** are reusable prompt templates stored as `.md` files.

**Why Skills?**
- **Reusable:** Define once, use everywhere
- **Maintainable:** Update prompts without code changes
- **Shareable:** Share skills across projects
- **Versionable:** Track in git

---

## 🏗️ Setup Instructions

Follow these steps to set up your Spring Boot application with Agent Skills.

### **Step 1: Prerequisites**

- Java 21 installed
- OpenAI API key (get from https://platform.openai.com/api-keys)
- Gradle wrapper included in project

### **Step 2: Enable Dependencies**

Open `build.gradle` and uncomment the Spring AI dependencies:

```gradle
dependencies {
    // Spring AI dependencies - uncomment these
    implementation platform("org.springframework.ai:spring-ai-bom:2.0.0-M2")
    implementation "org.springframework.ai:spring-ai-starter-model-openai"
    implementation 'ai.timefold.constraints:spring-ai-agent-utils:0.4.2'
    
    // Other dependencies already uncommented...
}
```

**What each dependency does:**
- `spring-ai-bom`: Bill of Materials that manages Spring AI version compatibility
- `spring-ai-starter-model-openai`: OpenAI integration for chat models (includes ChatModel, ChatClient)
- `spring-ai-agent-utils`: Community library providing SkillsTool and FileSystemTools for agent capabilities

**After uncommenting**, refresh Gradle dependencies:
```bash
./gradlew clean build
```

### **Step 3: Configure Application Properties**

Open `src/main/resources/application.properties` and replace with:

```properties
spring.application.name=session8-agent-skills

# OpenAI Configuration
spring.ai.openai.api-key=${OPENAI_API_KEY}
spring.ai.openai.base-url=https://api.openai.com
spring.ai.openai.chat.model=gpt-4o-mini
spring.ai.openai.chat.temperature=0.7

# Logging
logging.level.tools.muthuishere.session8=INFO
logging.level.org.springframework.ai=DEBUG
```

**What each property does:**
- `spring.ai.openai.api-key`: Your OpenAI API key (read from environment variable)
- `spring.ai.openai.base-url`: OpenAI API endpoint
- `spring.ai.openai.chat.model`: Model to use (gpt-4o-mini for cost-effectiveness)
- `spring.ai.openai.chat.temperature`: Controls randomness (0.7 = balanced creativity)
- `logging.level.*`: Debug logging for troubleshooting

### **Step 4: Set Environment Variables**

Before running, export your OpenAI API key:

```bash
export OPENAI_API_KEY=sk-proj-your-key-here
```

### **Step 5: Run the Application**

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

---

## 💻 Implementation

### Exercise 1: Create Skill Files

**Location:** `skills/code-explainer/SKILL.md`

```markdown
---
name: code-explainer
description: Explains code snippets in plain English with examples
---

# Code Explainer

When asked to explain code:

1. **One-Line Summary**: Start with a single sentence describing what the code does
2. **Section Breakdown**: Walk through each significant section or line
3. **Concepts**: Identify and explain any design patterns, algorithms
4. **Potential Issues**: Highlight any bugs or improvement opportunities
5. **Analogy**: If complex, provide a real-world analogy
6. **Simplified Example**: Show a minimal version

Use plain English. Assume the reader knows basic programming.
```

### Exercise 2: More Skills

**email-composer/SKILL.md:**
```markdown
---
name: email-composer
description: Composes professional emails
---

# Email Composer

1. **Subject Line**: Clear, specific
2. **Greeting**: "Hi [Name]" for internal, "Dear [Name]" for external
3. **Opening**: State purpose in first sentence
4. **Body**: Short paragraphs (2-3 sentences max)
5. **Action Items**: Clearly state what you need
6. **Closing**: Clear call to action
7. **Sign-off**: "Best regards" for formal, "Thanks" for casual

Keep professional but approachable.
```

**product-advisor/SKILL.md:**
```markdown
---
name: product-advisor
description: Recommends products based on needs
---

# Product Advisor

1. Ask about budget (low: <$500, medium: $500-$1500, high: >$1500)
2. Ask about use case (gaming, productivity, creative, general)
3. Recommend 2-3 products with pros/cons
4. Mention warranty (standard: 1 year, extended: 3 years)
5. If undecided, suggest mid-range option

Keep concise and actionable.
```

### Exercise 3: Controller with Skills

**Objective:** Build a chat endpoint that uses SkillsTool to load markdown skills and FileSystemTools for file operations.

**File:** `AgentSkillsController.java`

**Key Concepts:**
- `SkillsTool` - Loads markdown skill files from a directory and makes them available to the LLM
- `FileSystemTools` - Provides file system operations (read, write, list, etc.)
- Skills directory structure: `skills/{skill-name}/SKILL.md`

**Implementation:**

Replace the TODO sections in the constructor and chat method:

**Constructor:**
```java
public AgentSkillsController(ChatModel chatModel) {
    this.chatClient = ChatClient.builder(chatModel)
            .defaultToolCallbacks(SkillsTool.builder()
                    .addSkillsDirectory("skills")  // ← Loads all skills from directory
                    .build())
            .defaultTools(FileSystemTools.builder().build())  // ← File operations
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

    return new ChatBotResponse(request.question(), answer);
}
```

**Complete Controller:**
```java
package tools.muthuishere.session8.agentskills;

import ai.timefold.solver.constraints.tools.FileSystemTools;
import ai.timefold.solver.constraints.tools.SkillsTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AgentSkillsController {
    private final ChatClient chatClient;

    public AgentSkillsController(ChatModel chatModel) {
        // SkillsTool loads all markdown skills from the skills directory
        // FileSystemTools provides file operations (read, write, list, etc.)
        this.chatClient = ChatClient.builder(chatModel)
                .defaultToolCallbacks(SkillsTool.builder()
                        .addSkillsDirectory("skills")
                        .build())
                .defaultTools(FileSystemTools.builder().build())
                .build();
    }

    @PostMapping("/api/chat")
    public ChatBotResponse chat(@RequestBody ChatBotRequest request) {
        log.info("Received question: {}", request.question());

        String answer = chatClient.prompt()
                .user(request.question())
                .call()
                .content();

        return new ChatBotResponse(request.question(), answer);
    }
}
```

**How It Works:**

1. **Skill Loading:** At startup, `SkillsTool` scans the `skills/` directory for folders containing `SKILL.md` files
   
2. **Skill Registration:** Each skill (code-explainer, email-composer, product-advisor) becomes available to the LLM

3. **Question Analysis:** User asks "Explain this code: for(int i=0; i<10; i++){...}"

4. **Skill Selection:** GPT analyzes the question and decides to use the `code-explainer` skill

5. **Skill Execution:** LLM follows the instructions in `code-explainer/SKILL.md` to structure its response

6. **File Operations:** If needed, FileSystemTools can be used to read/write files during the conversation

---

## 🧪 Testing

```bash
# Use code-explainer skill
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "Explain this Java code: public static void main(String[] args) { System.out.println(\"Hello\"); }"}'

# Use email-composer skill
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "Compose a professional email to request vacation approval"}'

# Use product-advisor skill
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "Recommend a laptop for software development, budget $1500"}'
```

**How it works:**
1. LLM analyzes question
2. Decides which skill to use
3. Follows skill instructions
4. Generates response

---

## 🎯 Success Criteria

- ✅ Skills directory created with 3+ skills
- ✅ SkillsTool loads skills
- ✅ LLM follows skill instructions
- ✅ FileSystemTools available for file operations

---

## 🚀 Extension Ideas

1. **Custom Skills:** Create domain-specific skills
2. **Skill Chaining:** Combine multiple skills
3. **Skill Parameters:** Pass variables to skills
4. **Skill Library:** Build reusable skill collection

---

## 📚 Key Concepts

1. **SkillsTool** - Loads markdown skills
2. **FileSystemTools** - File operations
3. **Dynamic Loading** - Skills loaded at runtime
4. **Prompt Engineering** - Structured instructions

---

## 📖 Resources

- [Spring AI Agent Utils](https://github.com/spring-projects-experimental/spring-ai-agent-utils)
- [Prompt Engineering Guide](https://www.promptingguide.ai/)
