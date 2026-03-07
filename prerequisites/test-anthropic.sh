#!/bin/bash

# Test Anthropic (Claude) API
# Usage: ./test-anthropic.sh
# Make sure ANTHROPIC_API_KEY is set as environment variable

set -e

echo "========================================"
echo "Anthropic (Claude)"
echo "========================================"
curl -s https://api.anthropic.com/v1/messages \
  -H "x-api-key: $ANTHROPIC_API_KEY" \
  -H "anthropic-version: 2023-06-01" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "claude-3-haiku-20240307",
    "max_tokens": 1024,
    "messages": [
      {"role": "user", "content": "Hello, Claude"}
    ]
  }'

echo ""
echo "========================================"
echo "Done!"
echo "========================================"
