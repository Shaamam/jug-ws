#!/bin/bash

# Test OpenAI (GPT) API
# Usage: ./test-openai.sh
# Make sure OPENAI_API_KEY is set as environment variable

set -e

echo "========================================"
echo "OpenAI (GPT)"
echo "========================================"
curl -s https://api.openai.com/v1/responses \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $OPENAI_API_KEY" \
  -d '{
    "model": "gpt-5-nano",
    "input": "write a haiku about ai",
    "store": true
  }'

echo ""
echo "========================================"
echo "Done!"
echo "========================================"
