# Session 8: Spring AI Agent Skills

## What Are Agent Skills?

**Agent Skills** are modular capabilities packaged as Markdown files that AI agents can discover and use on demand. Think of them as plugin-like modules that extend what an AI agent can do, without hardcoding everything into prompts.

### Key Concepts

1. **Modular Design**: Each skill is a self-contained folder with instructions and resources
2. **Progressive Disclosure**: Agents only load what they need, when they need it
3. **LLM-Agnostic**: Skills work across different AI models (OpenAI, Anthropic, Google, etc.)

### Skill Structure

```
my-skill/
├── SKILL.md          # Required: instructions + metadata
├── scripts/          # Optional: helper scripts
├── references/       # Optional: documentation
└── assets/           # Optional: templates, resources
```

### How Skills Work (3-Step Process)

1. **Discovery** (at startup)
   - Agent scans skill folders and reads YAML frontmatter
   - Builds a lightweight registry of available skills
   - Only loads skill names and descriptions (minimal context)

2. **Semantic Matching** (during conversation)
   - User makes a request
   - LLM matches request to relevant skill descriptions
   - Decides if a skill should be invoked

3. **Execution** (on skill invocation)
   - Full SKILL.md content is loaded
   - LLM follows the instructions
   - Can read reference files or run scripts as needed

## How Spring AI Simplifies Agent Skills

Spring AI brings Agent Skills to Java developers with:

### 1. **Seamless Integration**
- Add skills with just a few lines of configuration
- No architectural changes to existing apps
- Works with Spring Boot's familiar patterns

### 2. **LLM Portability**
- Write skills once, use with any LLM provider
- Switch between OpenAI, Anthropic, Google without code changes
- No vendor lock-in

### 3. **Tool-Based Approach**
Spring AI implements three core tools:

- **SkillsTool**: Discovers and loads skills on demand
- **FileSystemTools**: Reads reference files from skills
- **ShellTools**: Executes helper scripts (optional)

### 4. **Resource Loading**
- Load skills from filesystem: `.addSkillsDirectory(".claude/skills")`
- Load from classpath: `.addSkillsResource(resourceLoader.getResource("classpath:skills"))`
- Perfect for packaged JAR/WAR deployments

## Our Code: session8-agent-skills

This project demonstrates Spring AI Agent Skills with a REST API that uses multiple skills.

### Project Structure

```
session8/
├── src/main/
│   ├── java/
│   │   └── tools/muthuishere/session8/
│   │       ├── Session8AgentSkillsApplication.java
│   │       └── agentskills/
│   │           ├── AgentSkillsController.java   # Main controller
│   │           ├── ChatBotRequest.java          # Request DTO
│   │           └── ChatBotResponse.java         # Response DTO
│   └── resources/
│       ├── application.properties
│       └── skills/                               # Our skills folder
│           └── pptx/                            # PowerPoint generation skill
│               ├── SKILL.md                     # Main instructions
│               ├── pptxgenjs.md                # JavaScript guide
│               ├── editing.md                  # Editing guide
│               └── scripts/                    # Helper scripts
└── requests/
    └── session8/
        └── agentskills.http                     # Test requests
```

### Key Components

#### 1. AgentSkillsController.java

```java
@RestController
public class AgentSkillsController {
    private final ChatClient chatClient;

    public AgentSkillsController(ChatModel chatModel, ResourceLoader resourceLoader) {
        this.chatClient = ChatClient.builder(chatModel)
                .defaultToolCallbacks(SkillsTool.builder()
                        .addSkillsResource(resourceLoader.getResource("classpath:skills"))
                        .build())
                .defaultTools(FileSystemTools.builder().build())
                .defaultToolContext(Map.of(
                    "workingDirectory", Path.of(System.getProperty("user.dir"))
                ))
                .build();
    }

    @PostMapping("/api/chat")
    public ChatBotResponse chat(@RequestBody ChatBotRequest request) {
        String answer = chatClient.prompt()
                .user(request.question())
                .call()
                .content();
        return new ChatBotResponse(request.question(), answer);
    }
}
```

**What it does:**
- Configures ChatClient with SkillsTool pointing to `classpath:skills`
- Adds FileSystemTools for reading skill files
- Sets up working directory context
- Provides REST endpoint `/api/chat` for user questions

#### 2. Available Skills

**PPTX Skill** (`src/main/resources/skills/pptx/`)
- Creates PowerPoint presentations
- Reads/edits existing presentations
- Uses PptxGenJS for generation
- Includes design guidelines and color palettes

### How to Use

#### 1. Start the Application

```bash
./gradlew bootRun
```

#### 2. Test with HTTP Requests

**Create a PowerPoint about Spring AI:**

```http
POST http://localhost:8080/api/chat
Content-Type: application/json

{
  "question": "Create a PowerPoint presentation explaining Spring AI. Include slides covering: what is Spring AI, key features, architecture overview, how to get started, and a practical example. Use a professional design with the Teal Trust color palette."
}
```

The agent will:
1. Match your request to the `pptx` skill
2. Load the full SKILL.md instructions
3. Read design guidelines and color palette info
4. Generate the presentation following best practices

#### 3. Other Example Requests

See `requests/session8/agentskills.http` for more examples:
- Code explanation requests
- Email composition
- Product recommendations

### Configuration

**application.properties:**
```properties
spring.application.name=session8-agent-skills
server.port=8080

# OpenAI Configuration
spring.ai.openai.chat.api-key=${OPENAI_API_KEY}
spring.ai.openai.chat.options.model=gpt-4o-mini
```

**Environment Variables:**
```bash
export OPENAI_API_KEY=your-api-key-here
```

### Dependencies

```xml
<dependency>
    <groupId>org.springaicommunity</groupId>
    <artifactId>spring-ai-agent-utils</artifactId>
    <version>0.4.2</version>
</dependency>

<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-openai</artifactId>
    <version>2.0.0-M2</version>
</dependency>
```

## Benefits of This Approach

### 1. **Modularity**
- Add new skills by creating new folders
- Update skill behavior without code changes
- Share skills across projects

### 2. **Token Efficiency**
- Only loads skills when needed
- Keeps context window lean
- Can register hundreds of skills efficiently

### 3. **Flexibility**
- Skills can include scripts, templates, references
- Progressive disclosure of information
- On-demand resource loading

### 4. **Portability**
- Skills work with any LLM provider
- Easy to switch models
- No vendor lock-in

## Security Considerations

⚠️ **Important**: Scripts in skills execute directly on your machine without sandboxing.

**Best Practices:**
- Review all skill scripts before use
- Be cautious with third-party skills
- Consider running in a containerized environment
- Implement approval workflows for sensitive operations

## Resources

- **Spring AI Documentation**: [docs.spring.io/spring-ai](https://docs.spring.io/spring-ai)
- **Agent Skills Specification**: [agentskills.io](https://agentskills.io)
- **Spring AI Agent Utils**: [GitHub Repository](https://github.com/tzolov/spring-ai-agent-utils)
- **Blog Post**: [Spring AI Agentic Patterns - Agent Skills](https://spring.io/blog/2026/01/13/spring-ai-generic-agent-skills)

## Next Steps

1. **Explore More Skills**: Add code-reviewer, email-composer, or product-advisor skills
2. **Create Custom Skills**: Build domain-specific skills for your use case
3. **Advanced Patterns**: Explore TodoWriteTool, AskUserQuestionTool, and Subagent Orchestration

---

*Part of the Spring AI Agentic Patterns series - exploring modular, reusable AI capabilities in Java applications.*
