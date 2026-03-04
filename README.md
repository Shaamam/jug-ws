# Tamil Nadu Java User Group - Spring AI Workshop

This repository contains comprehensive hands-on materials for learning Spring AI, from basic LLM integration to advanced agent architectures and Model Context Protocol (MCP) implementations.

## 🎯 Workshop Overview

This workshop will take you on a journey through the exciting world of AI-powered Java applications using Spring AI. You'll learn how to:

- **Integrate Large Language Models (LLMs)** into Spring Boot applications
- **Implement function calling** to let AI execute business logic
- **Build RAG systems** for context-aware document Q&A
- **Create MCP servers** to expose tools for AI agents
- **Develop MCP clients** to consume remote tools
- **Design skill-based agents** with reusable capabilities

## 📋 Prerequisites

Before starting the workshop, ensure you have:

- **Java 21** or higher installed
- **Gradle** (wrapper included in each project)
- **IDE**: IntelliJ IDEA, VS Code, or Eclipse
- **API Keys**:
  - OpenAI API key ([Get it here](https://platform.openai.com/api-keys))
  - Google Gemini API key ([Get it here](https://aistudio.google.com/app/apikey))
  - Google Cloud Platform account (for Session 3)
- **PostgreSQL** with pgvector extension (for Session 3)
- **Basic knowledge** of Spring Boot and REST APIs

## 📚 Workshop Structure

### Session 1: LLM as API - Getting Started

Learn the fundamentals of integrating LLMs with Spring AI.

#### [Session 1a: OpenAI Integration](session-1/llm-as-api-openai)
- Basic chat completions
- System prompts and context
- Conversation memory
- Prompt templates
- Structured output generation

#### [Session 1b: Gemini Integration](session-1/llm-as-api-gemini)
- Same code, different provider
- Understanding Spring AI's abstraction power
- Comparing OpenAI vs Gemini behavior

**Key Takeaway**: Spring AI's provider-agnostic design lets you switch LLM providers with minimal code changes.

---

### [Session 2: Tool Calling](session-2/tool-calling)

Teach LLMs to execute functions and interact with your business logic.

**What You'll Learn**:
- Spring AI's tool calling mechanism
- Implementing functions LLMs can invoke
- Using `@ToolCallbackDescription` decorators
- Building intelligent assistants that query business data

**Example**: Create a customer service bot that can look up orders, check inventory, and process refunds.

---

### [Session 3: RAG - Retrieval Augmented Generation](session-3/rag)

Build document Q&A systems with semantic search and vector databases.

**What You'll Learn**:
- Processing documents (PDF, Word, Text)
- Storing embeddings in PGVector
- Semantic search implementation
- Using Google Vertex AI Gemini
- Context-aware responses with QuestionAnswerAdvisor

**Example**: Create a system that answers questions about your company's documentation.

---

### Session 4: Model Context Protocol (MCP) - Building Servers

Learn to build MCP servers that expose tools for AI agents.

#### [Chapter 1: Multi-Protocol MCP Server](session-4/mcp-ch1)
- Building a Spring Boot MCP server
- Exposing tools via STDIO and HTTP protocols
- MCP server configuration and profiles
- Creating a Todo application with MCP tools

#### [Chapter 2: Firebase Authentication](session-4/mcp-ch2)
- Adding authentication to MCP servers
- User-specific todo management
- Firebase integration
- OAuth2 resource server configuration

#### [Chapter 3: Enhanced Security](session-4/mcp-ch3)
- Custom authentication entry points
- OAuth2 resource metadata endpoints
- Standardized error responses
- Production-ready security

**Key Concept**: MCP (Model Context Protocol) standardizes how AI agents discover and invoke tools from remote servers.

---

### [Session 6: MCP Cloud Deployment](session-6/mcp-cloud)

Take your MCP server to production.

**What You'll Learn**:
- OAuth2 resource metadata APIs
- OpenID Connect discovery
- Comprehensive health monitoring
- Multi-cloud deployment (AWS, GCP, Azure)
- Production environment configuration

---

### [Session 7: MCP Client](session-7/mcp-client)

Build clients that consume MCP server tools.

**What You'll Learn**:
- Connecting to remote MCP servers
- Discovering and invoking remote tools
- Integrating MCP tools with ChatClient
- Letting LLMs use tools from multiple servers

**Architecture**:
```
User Question → LLM + MCP Client → MCP Server → Tool Execution → Response
```

---

### [Session 8: Agent Skills](session-8/agent-skills)

Create reusable, skill-based agent architectures.

**What You'll Learn**:
- Skill-based agent design patterns
- Creating reusable prompt templates as markdown files
- Dynamic skill loading with SkillsTool
- Integrating file system operations

**Why Skills?**
- **Reusable**: Define once, use everywhere
- **Maintainable**: Update prompts without code changes
- **Shareable**: Share skills across projects
- **Versionable**: Track in git

---

## 🚀 Getting Started

### Quick Start Guide

1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/jug-ws.git
   cd jug-ws
   ```

2. **Start with Session 1**:
   ```bash
   cd session-1/llm-as-api-openai
   ```

3. **Create `.env` file** with your API keys:
   ```bash
   OPENAI_API_KEY=sk-your-api-key-here
   ```

4. **Follow the README** in each session directory for detailed instructions.

### Workshop Path

We recommend following the sessions in order:

```
Session 1 → Session 2 → Session 3 → Session 4 (Ch1-3) → Session 6 → Session 7 → Session 8
```

However, each session is self-contained and can be explored independently based on your interests.

## 🛠️ Technology Stack

- **Spring Boot 3.x**
- **Spring AI 2.0.0-M2 / 1.1.0-M4** (depending on session)
- **Java 21**
- **Gradle** (build tool)
- **H2 / PostgreSQL** (databases)
- **Firebase** (authentication)
- **OpenAI API**
- **Google Gemini API**
- **Google Vertex AI**

## 📖 Additional Resources

### Official Documentation
- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [Model Context Protocol Specification](https://spec.modelcontextprotocol.io/)
- [OpenAI API Reference](https://platform.openai.com/docs/api-reference)
- [Google Gemini Documentation](https://ai.google.dev/docs)

### Useful Links
- [Spring AI GitHub Repository](https://github.com/spring-projects/spring-ai)
- [MCP Server Security Library](https://github.com/springaicommunity/mcp-server-security)
- [Firebase Console](https://console.firebase.google.com/)
- [Google Cloud Console](https://console.cloud.google.com/)

## 💡 Tips for Success

1. **Read the READMEs**: Each session has detailed step-by-step instructions
2. **Uncomment dependencies**: Most projects have dependencies commented out for learning purposes
3. **Check your API keys**: Ensure your `.env` files are properly configured
4. **Test incrementally**: Run the code after each major step
5. **Experiment**: Try modifying prompts and parameters to see how behavior changes
6. **Ask questions**: Leverage the JUG community for support

## 📝 License

This workshop material is provided for educational purposes.

---

**Happy Learning! 🚀**

For questions or support, reach out to the Tamil Nadu Java User Group community.