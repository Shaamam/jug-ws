# Session 1a: Gemini API - Workshop

## 🎯 Learning Objectives

By the end of this session, you will:
- Integrate Google Gemini with Spring AI
- Understand Spring AI's provider-agnostic abstractions
- See how the same code works with different LLM providers
- Compare OpenAI vs Gemini behavior

## 📋 Prerequisites

- Java 21 installed
- Google Gemini API key (get one at https://aistudio.google.com/app/apikey)
- IDE (IntelliJ IDEA / VS Code)
- Completed Session 1 (recommended but not required)

## 🔄 What's Different from Session 1?

The **exact same code** from Session 1 works here! We only change:
1. **Dependency:** `spring-ai-starter-model-google-genai` instead of OpenAI
2. **Configuration:** Gemini API key and model name
3. **Everything else stays the same!**

This demonstrates Spring AI's **abstraction power** - write once, use any LLM provider.

---

## 🏗️ Project Setup

### Step 1: Environment Configuration

Create a `.env` file in the project root:

```bash
GEMINI_API_KEY=your-gemini-api-key-here
```

**Get your key:** https://aistudio.google.com/app/apikey

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
    
    // Google Gemini instead of OpenAI
    implementation 'org.springframework.ai:spring-ai-starter-model-google-genai'
}
```

**Key Difference:** Using `spring-ai-starter-model-google-genai` instead of OpenAI starter!

### Step 3: Add Application Properties

Open `src/main/resources/application.properties` and add:

```properties
spring.application.name=session1-llm-as-api

spring.ai.model.chat=google-genai
spring.ai.google.genai.api-key=${GEMINI_API_KEY}
spring.ai.google.genai.chat.options.model=gemini-3-flash-preview


# Disable embeddings (all types)
spring.ai.model.embedding=none
spring.ai.model.embedding.text=none
spring.ai.model.embedding.multimodal=none

# Disable image generation
spring.ai.model.image=none

# Disable audio
spring.ai.model.audio.transcription=none
spring.ai.model.audio.speech=none

# Disable moderation
spring.ai.model.moderation=none
```

**Configuration Comparison:**

| Property | OpenAI (Session 1) | Gemini (Session 1a) |
|----------|-------------------|---------------------|
| API Key | `spring.ai.openai.api-key` | `spring.ai.google.genai.api-key` |
| Model | `gpt-4o-mini` | `gemini-3-flash-preview` |
| Provider | `openai` | `google-genai` |

**But the code stays the same!** That's Spring AI's abstraction power.

### Step 4: Run the Application

```bash
./gradlew bootRun
# or
task dev
```

The application will start on `http://localhost:8080`

---

## 💻 Implementation Steps

### Exercise: Copy Implementation from Session 1

**The code is identical!** You can either:

1. **Option A:** Copy your `ChatController.java` from Session 1
2. **Option B:** Implement the same 6 endpoints following Session 1 README

**All endpoints work the same:**
- `POST /api/chatplain`
- `POST /api/chat`
- `POST /api/chatmemory`
- `POST /api/chatresponse`
- `POST /api/chattemplate`
- `POST /api/chatstructured`

---

## 🔍 Comparing Responses

### Test Both Models

#### Gemini (this session):
```bash
curl -X POST http://localhost:8080/api/chatplain \
  -H "Content-Type: application/json" \
  -d '{"question": "Explain dependency injection in one sentence"}'
```

#### Run the same request against Session 1 (OpenAI) and compare!

### Observations to Make:

1. **Response Style:** 
   - Is one more verbose?
   - Which is more casual vs formal?

2. **Token Usage:**
   - Check `/api/chatresponse` metadata
   - Compare `promptTokens` and `generationTokens`

3. **Structured Output:**
   - Do both correctly format JSON for `/api/chatstructured`?

4. **Memory:**
   - Test conversation context with `/api/chatmemory`
   - Do both maintain context equally well?

---

## 🎯 Success Criteria

- ✅ All 6 endpoints working with Gemini
- ✅ Same code as Session 1 (just different dependencies/config)
- ✅ Understand provider abstraction in Spring AI
- ✅ Can compare Gemini vs OpenAI behavior

---

## 🐛 Common Issues & Solutions

### Issue: "API key not valid"
**Solution:** Get a new key from https://aistudio.google.com/app/apikey

### Issue: "Model not found"
**Solution:** Check available models at https://ai.google.dev/gemini-api/docs/models/gemini

### Issue: Different response format
**Solution:** This is expected! Different models have different behaviors. Spring AI handles the differences.

---

## 📚 Key Concepts Learned

1. **Provider Abstraction** - Same code, different LLMs
2. **ChatModel Interface** - Universal LLM interaction
3. **Configuration Over Code** - Switch providers via properties
4. **Model Differences** - Understanding LLM behavior variations

---

## 🧪 Experiment Ideas

1. **A/B Testing:** Send the same question to both and compare
2. **Cost Analysis:** Compare token usage for the same prompts
3. **Multi-Provider:** Configure both and switch at runtime
4. **Model Selection:** Try different Gemini models:
   - `gemini-pro`
   - `gemini-ultra`
   - `gemini-3-flash-preview`

---

## 📖 Additional Resources

- [Spring AI Google Gemini Support](https://docs.spring.io/spring-ai/reference/api/chat/google-genai-chat.html)
- [Google AI Studio](https://aistudio.google.com/)
- [Gemini API Documentation](https://ai.google.dev/docs)
