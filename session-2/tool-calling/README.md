# Session 2: Tool Calling - Teaching LLMs to Execute Functions

## 🎯 Learning Objectives

By the end of this session, you will:
- Understand Spring AI's tool calling mechanism
- Implement functions that LLMs can invoke
- Use `@ToolCallbackDescription` decorators for function documentation
- Build an intelligent assistant that can query business data
- Learn how LLMs decide when to call tools

## 📋 Prerequisites

- Completed Session 1 (LLM basics)
- Java 21 installed
- OpenAI API key configured (`.env` file with `OPENAI_API_KEY`)
- Basic understanding of Spring Boot and dependency injection

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

### Step 3: Add Application Properties

Open `src/main/resources/application.properties` and add:

```properties
spring.application.name=session2-tool-calling

# OpenAI Configuration
spring.ai.openai.chat.options.model=gpt-4o-mini
spring.ai.model.chat=openai
spring.ai.openai.chat.api-key=${OPENAI_API_KEY}

# Disable unused Spring AI features
spring.ai.model.embedding=none
spring.ai.model.embedding.text=none
spring.ai.model.embedding.multimodal=none
spring.ai.model.image=none
spring.ai.model.audio.transcription=none
spring.ai.model.audio.speech=none
spring.ai.model.moderation=none
```

### Step 4: Run the Application

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

## 🧠 What is Tool Calling?

**Tool calling** (also known as function calling) allows Language Models to:
1. Receive a description of available functions
2. Decide which function(s) to call based on user intent
3. Generate function arguments from natural language
4. Execute the function and incorporate results into the response

**Example Flow:**
```
User: "What products are low in stock?"
  ↓
LLM: [Analyzes question, decides to call getLowStockProducts()]
  ↓
System: [Executes getLowStockProducts(), returns data]
  ↓
LLM: [Formats results into natural language]
  ↓
Response: "We have 2 products low in stock: Monitor (8 items) and Webcam (3 items)"
```

---

## 🏗️ Project Structure

```
src/main/java/.../session2/
  ├── ToolCallingController.java    # Main controller with tool-enabled chat
  ├── InventoryTools.java            # Product management tools (Exercise 2)
  ├── EmployeeTools.java             # Employee management tools (Exercise 3)
  ├── Product.java                   # Product record
  └── Employee.java                  # Employee record
```

---

## 💻 Implementation Steps

### Exercise 2.1: Get All Products

**Objective:** Create a tool that returns the full product inventory.

**File:** `InventoryTools.java` → `getProducts()`

**Implementation:**

Replace the TODO line with:

```java
return List.of(
    new Product("P001", "Laptop", 15, 999.99),
    new Product("P002", "Mouse", 50, 29.99),
    new Product("P003", "Keyboard", 30, 79.99),
    new Product("P004", "Monitor", 8, 299.99),
    new Product("P005", "Webcam", 3, 89.99)
);
```

**Complete Method:**
```java
@ToolCallbackDescription("Get all products in the inventory")
public List<Product> getProducts() {
    return List.of(
        new Product("P001", "Laptop", 15, 999.99),
        new Product("P002", "Mouse", 50, 29.99),
        new Product("P003", "Keyboard", 30, 79.99),
        new Product("P004", "Monitor", 8, 299.99),
        new Product("P005", "Webcam", 3, 89.99)
    );
}
```

**Why This Matters:** This provides the LLM with inventory data it can reference when answering questions.

---

### Exercise 2.2: Get Product by ID

**Objective:** Create a tool that finds a single product by its ID.

**File:** `InventoryTools.java` → `getProductById()`

**Implementation:**

Replace the TODO line with:

```java
return getProducts().stream()
    .filter(p -> p.id().equalsIgnoreCase(productId))
    .findFirst()
    .orElse(null);
```

**Complete Method:**
```java
@ToolCallbackDescription("Get a specific product by its ID")
public Product getProductById(
        @ToolCallbackDescription("Product ID like P001, P002, P003") String productId) {
    return getProducts().stream()
        .filter(p -> p.id().equalsIgnoreCase(productId))
        .findFirst()
        .orElse(null);
}
```

**How It Works:**
- The LLM receives the product ID from user input (e.g., "Tell me about product P001")
- Spring AI automatically extracts "P001" and passes it as the `productId` parameter
- The method filters the product list and returns the matching product

---

### Exercise 2.3: Get Low Stock Products

**Objective:** Create a tool that identifies products with low inventory.

**File:** `InventoryTools.java` → `getLowStockProducts()`

**Implementation:**

Replace the TODO line with:

```java
return getProducts().stream()
    .filter(p -> p.quantity() < 10)
    .toList();
```

**Complete Method:**
```java
@ToolCallbackDescription("Get products that are low in stock (less than 10 items)")
public List<Product> getLowStockProducts() {
    return getProducts().stream()
        .filter(p -> p.quantity() < 10)
        .toList();
}
```

**Business Logic:** Products with quantity < 10 are considered "low stock".

---

### Exercise 3.1: Get All Employees

**Objective:** Create a tool that returns the full employee directory.

**File:** `EmployeeTools.java` → `getEmployees()`

**Implementation:**

Replace the TODO line with:

```java
return List.of(
    new Employee("Alice", "Engineering", "Senior Developer"),
    new Employee("Bob", "Engineering", "Tech Lead"),
    new Employee("Carol", "Product", "Product Manager"),
    new Employee("Dave", "Design", "UX Designer"),
    new Employee("Eve", "Sales", "Account Manager")
);
```

**Complete Method:**
```java
@ToolCallbackDescription("Get all employees in the company with their department and role")
public List<Employee> getEmployees() {
    return List.of(
        new Employee("Alice", "Engineering", "Senior Developer"),
        new Employee("Bob", "Engineering", "Tech Lead"),
        new Employee("Carol", "Product", "Product Manager"),
        new Employee("Dave", "Design", "UX Designer"),
        new Employee("Eve", "Sales", "Account Manager")
    );
}
```

---

### Exercise 3.2: Get Employees by Department

**Objective:** Create a tool that filters employees by department.

**File:** `EmployeeTools.java` → `getEmployeesByDepartment()`

**Implementation:**

Replace the TODO line with:

```java
return getEmployees().stream()
    .filter(e -> e.department().equalsIgnoreCase(department))
    .toList();
```

**Complete Method:**
```java
@ToolCallbackDescription("Get employees filtered by department name")
public List<Employee> getEmployeesByDepartment(
        @ToolCallbackDescription("Department name like Engineering, Product, Design, Sales") String department) {
    return getEmployees().stream()
        .filter(e -> e.department().equalsIgnoreCase(department))
        .toList();
}
```

**Example Use Cases:**
- "Who works in Engineering?"
- "List all Product team members"
- "Show me the Design department"

---

### Exercise 4: Chat with Tools Integration

**Objective:** Wire up all tools to the chat endpoint so the LLM can call them.

**File:** `ToolCallingController.java` → `chatWithTools()`

**Key Concepts:**
- `ChatClient` is a fluent API builder for chat interactions
- `.tools()` registers tool instances that the LLM can invoke
- Spring AI automatically handles function serialization and invocation

**Implementation:**

Replace the TODO line with:

```java
String response = ChatClient.create(chatModel)
    .prompt()
    .system("You are a helpful business assistant. Use the available tools to answer questions about our inventory and employees.")
    .user(question)
    .tools(inventoryTools, employeeTools)  // Register your tools here!
    .call()
    .content();
```

**Complete Method:**
```java
@PostMapping("/api/tools/chat")
public ChatBotResponse chatWithTools(@RequestBody ChatBotRequest chatBotRequest) {
    String question = chatBotRequest.question();
    log.info("Tool calling, question: {}", question);

    String response = ChatClient.create(chatModel)
        .prompt()
        .system("You are a helpful business assistant. Use the available tools to answer questions about our inventory and employees.")
        .user(question)
        .tools(inventoryTools, employeeTools)
        .call()
        .content();

    return new ChatBotResponse(question, response);
}
```

**How It Works:**
1. User asks: "What products are low in stock?"
2. LLM analyzes the question and decides to call `getLowStockProducts()`
3. Spring AI invokes the method and gets the results
4. LLM formats the results into natural language
5. Response: "Monitor (8 items) and Webcam (3 items) are low in stock"

---

## 🧪 Testing Your Implementation

### Test 1: Inventory Query
```bash
curl -X POST http://localhost:8080/api/tools/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "What products do we have in stock?"}'
```

**Expected:** The LLM should call `getProducts()` and list all 5 products.

---

### Test 2: Low Stock Alert
```bash
curl -X POST http://localhost:8080/api/tools/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "Which products are running low on inventory?"}'
```

**Expected:** The LLM should call `getLowStockProducts()` and report Monitor and Webcam.

---

### Test 3: Product Lookup
```bash
curl -X POST http://localhost:8080/api/tools/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "Tell me about product P001"}'
```

**Expected:** The LLM should call `getProductById("P001")` and describe the Laptop.

---

### Test 4: Employee Directory
```bash
curl -X POST http://localhost:8080/api/tools/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "Who works in the Engineering department?"}'
```

**Expected:** The LLM should call `getEmployeesByDepartment("Engineering")` and list Alice and Bob.

---

### Test 5: Combined Query
```bash
curl -X POST http://localhost:8080/api/tools/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "How many products do we have and who is the Product Manager?"}'
```

**Expected:** The LLM should call **both** `getProducts()` and `getEmployeesByDepartment("Product")` to answer both parts.

---

## 🔍 Debugging Tips

### Issue: "Tool not being called"

**Symptoms:** LLM responds without calling your function

**Causes & Fixes:**
1. **Poor tool description:** Make `@ToolCallbackDescription` clear and specific
   ```java
   // Bad: "Get products"
   // Good: "Get all products in the inventory with their prices and stock levels"
   ```

2. **Missing parameter description:** Describe what the parameter expects
   ```java
   @ToolCallbackDescription("Product ID like P001, P002, or P003")
   String productId
   ```

3. **Question doesn't match tool:** Rephrase your question to be more explicit
   ```bash
   # Vague: "What's our situation?"
   # Clear: "What products are low in stock?"
   ```

---

### Issue: "Function invocation failed"

**Symptoms:** Error in console when LLM tries to call a function

**Causes & Fixes:**
1. **Tool not registered:** Ensure `.tools(inventoryTools, employeeTools)` is present
2. **Null pointer:** Check that `getProducts()` returns a list, not null
3. **Wrong parameter type:** Verify parameter types match LLM's expectations

---

### Issue: "Response is outdated"

**Symptoms:** LLM returns data that doesn't match your code

**Cause:** Spring Boot app wasn't restarted after code changes

**Fix:** Stop and restart the application:
```bash
./gradlew bootRun
```

---

## 📚 Key Takeaways

1. **Tool Callbacks** = Functions that LLMs can invoke autonomously
2. **Descriptions are critical** = Clear descriptions help LLMs choose the right tool
3. **Parameter extraction** = LLMs automatically parse natural language into function arguments
4. **Multiple tools** = LLMs can call multiple functions in sequence to answer complex questions
5. **ChatClient** = Fluent API that simplifies tool registration

---

## 🚀 Next Steps

### Enhancements to Try:
1. **Add more tools:**
   - `updateProductStock(String productId, int newQuantity)`
   - `getEmployeeByName(String name)`

2. **Real database integration:**
   - Replace in-memory lists with JPA repositories
   - Connect to PostgreSQL or H2

3. **Error handling:**
   - Return meaningful messages when products/employees aren't found
   - Handle edge cases (empty results, invalid IDs)

4. **Tool chaining:**
   - Test complex multi-step queries like "Find low stock products and tell me who manages inventory"

---

## 📖 Related Documentation

- [Spring AI Tool Functions](https://docs.spring.io/spring-ai/reference/api/chatclient.html#tool-functions)
- [OpenAI Function Calling](https://platform.openai.com/docs/guides/function-calling)
- [Spring AI Examples](https://github.com/spring-projects/spring-ai/tree/main/spring-ai-spring-boot-autoconfigure)

---

## 🎓 Workshop Progress

✅ **Session 1:** LLM as API - Basic integration  
🎯 **Session 2:** Tool Calling ← You are here  
📍 **Session 3:** RAG with Vector Stores  
📍 **Session 4:** MCP Server Implementation  

---

**Ready to continue?** Move to **Session 3: Retrieval Augmented Generation** to learn about vector databases and semantic search!
