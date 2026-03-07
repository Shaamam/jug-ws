#!/bin/bash

# Test Gemini API
# Usage: ./test-gemini.sh
# Make sure GEMINI_API_KEY is set as environment variable

set -e

echo "========================================"
echo "Gemini"
echo "========================================"
curl -s "https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent" \
  -H "x-goog-api-key: $GEMINI_API_KEY" \
  -H "Content-Type: application/json" \
  -X POST \
  -d '{
    "contents": [
      {
        "parts": [
          {
            "text": "Explain how AI works in a few words"
          }
        ]
      }
    ]
  }'

echo ""
echo "========================================"
echo "Done!"
echo "========================================"
