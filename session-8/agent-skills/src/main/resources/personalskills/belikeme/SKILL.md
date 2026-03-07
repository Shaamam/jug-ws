---
name: chat-persona-analyzer
description: Analyzes group chat logs from the references/ folder and enables responding in the voice and style of specific members. Extracts communication patterns, personality traits, and interests to mimic personas. Use when user asks to respond "like [person name]", generate messages matching someone's style, analyze communication patterns, or simulate conversations based on chat history.
---

# Chat Persona Analyzer

This skill analyzes group chat logs (WhatsApp, Discord, Slack, etc.) from the `references/` folder and enables you to respond in the authentic voice and style of specific members. It extracts distinctive communication patterns, personality traits, and interests from chat history to accurately mimic individual personas.

## When to Use

Trigger this skill when the user:
- Asks to respond "like [person name]" or "in [name]'s style"
- Wants to generate messages matching a specific group member's voice
- Requests help crafting responses in a particular persona
- Wants to understand how different personas would react to situations
- Asks to analyze communication patterns from chat logs
- Wants to simulate multi-person conversations based on real group dynamics
- Says "talk like [name]" or "write as [name]"

## Workflow

### Step 1: Load Chat References

**CRITICAL:** When this skill is triggered, you MUST follow this sequence:

1. **List reference files**: Use `list_dir` on the `references/` folder to discover all available chat logs
2. **Read each file**: Use `read_file` to load the complete content of EVERY file found in the references folder
3. **Parse the chats**: As you read, extract all participant names and their messages

**Known Personas (from current reference files):**
- **Kishore College New** (also appears as "Kishore")
- **babu**
- **Janamad**
- **Muthukumaran Navaneethakr**
- **Dilnew Us**

*Note: This list will expand as more chat references are added to the folder.*

**What to extract from each chat log:**
- All participant names/identifiers (look for patterns like "Name:", "Name -", or WhatsApp format)
- Message frequency per person (who talks a lot vs. rarely)
- Individual communication patterns
- Group dynamics and interaction flows
- Cultural context and shared references
- Timestamps and conversation topics

**IMPORTANT:** Do not skip this step. Without reading the reference files, you cannot perform persona analysis or generate authentic responses.

### Step 2: Build Persona Profiles

**After reading all reference files**, create mental profiles for each participant found. For each participant, analyze and extract:

**Personality Traits:**
- Activity level (active/passive communicator)
- Emotional expression patterns
- Professional/personal background hints
- Relationship dynamics with others
- Life stage indicators

**Communication Style:**
- Message length and structure
- Emoji and emoticon usage
- Language preferences (English, code-switching, etc.)
- Media sharing habits (links, videos, images)
- Punctuation and formatting patterns
- Use of mentions/tags
- Response timing and frequency

**Topics of Interest:**
- Recurring themes they discuss
- Types of content they share
- Questions they ask
- Expertise areas they demonstrate

**Distinctive Phrases:**
- Catchphrases or recurring expressions
- Unique vocabulary or slang
- Cultural references
- Humor style

### Step 3: Generate Persona Response

When user requests a response as a specific persona:

**Voice Matching:**
1. Use their typical vocabulary and sentence structure
2. Match their tone (enthusiastic, analytical, witty, sarcastic, etc.)
3. Apply their punctuation patterns
4. Include their characteristic filler words or expressions

**Pattern Application:**
1. Mimic emoji/emoticon usage (frequency and types)
2. Match typical message length (short/long)
3. Apply their formatting style (line breaks, caps, asterisks, etc.)
4. Include media references if they typically share links

**Content Authenticity:**
1. Reference topics they care about
2. Use their humor style
3. Include distinctive quirks and expressions
4. Maintain their relationship dynamics if addressing others

### Step 4: Multi-Persona Conversations

When simulating group interactions:

1. **Maintain consistency**: Each persona keeps their distinct voice throughout
2. **Reflect dynamics**: Show authentic relationships (friendly, formal, joking, etc.)
3. **Use mentions appropriately**: Include @tags as participants typically would
4. **Natural flow**: Build conversations realistically with back-and-forth
5. **Include behaviors**: Reference media sharing, inside jokes, typical topics

## Reference Material

**Location:** `references/` folder in the skill directory

**Current reference files:**
- `references/WhatsApp Chat with Panchayat - SANITIZED.txt` (safe, politically neutral version)
- `references/WhatsApp Chat with Panchayat.txt` (original full chat)

**File to prioritize:** Use the SANITIZED version for general persona work to avoid sensitive content.

**Supported formats:**
- WhatsApp exported chats (.txt)
- Discord chat logs
- Slack message exports
- Generic text-based chat transcripts
- Any readable conversation logs with clear speaker identification

**Setup:** Simply place chat log files in the `references/` folder. The skill automatically reads and analyzes all files when triggered—no configuration needed. You can add multiple chat logs to expand the persona database.

## Usage Examples

**Single persona response:**
- "How would Kishore respond to this news?"
- "Write a message like babu about moving to a new city"
- "Respond as Janamad: [context or message]"
- "Talk like Muthukumaran Navaneethakr and give advice on this AI tool"
- "How would Dilnew Us comment on this funny video?"

**Multi-persona simulation:**
- "Simulate a conversation between Kishore and babu about relocating"
- "How would the Panchayat group react to this tech news?"
- "Generate a group chat with Janamad, Muthukumaran, and Dilnew discussing [topic]"

**Persona analysis:**
- "Analyze babu's communication style"
- "List all personas from the chat"
- "Describe the Panchayat group dynamics"
- "What makes Kishore's voice distinctive?"

## Output Guidelines

**For persona responses:**
- Deliver the message in authentic voice (don't announce "As [name] would say...")
- Include style notes only if explicitly requested
- Match formatting exactly (emojis, line breaks, caps, etc.)
- Stay concise unless the persona is typically verbose

**For persona analysis:**
- Name/identifier
- 3-5 key personality traits
- Communication style summary (2-3 sentences)
- Distinctive features (emojis, phrases, topics)
- 2-3 example phrases from actual chat

## Important Notes

- **Always load fresh**: Read ALL reference files each time the skill is invoked—use `list_dir` + `read_file` for every file
- **Don't assume cached knowledge**: Even if you've read these files before, read them again to ensure accuracy
- **Privacy focus**: Analyze communication style and personality, not personal/private details
- **Neutrality on sensitive topics**: Maintain neutral stance on politics, religion, etc., unless patterns are essential to the persona's voice
- **Multi-source synthesis**: When multiple chat logs exist, synthesize patterns across all sources
- **Language flexibility**: Adapt to any language or cultural context in the logs
- **Extensible design**: Users can add unlimited chat references—skill adapts automatically

## Troubleshooting

**If persona requests aren't working:**
1. Verify you've read the reference files (use list_dir to check references/ folder exists)
2. Confirm persona name matches someone in the chat logs
3. Re-read the relevant chat file to refresh context
4. Check for typos in participant names
