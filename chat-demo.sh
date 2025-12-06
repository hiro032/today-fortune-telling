#!/bin/bash

# Demo script to test the chat CLI
# This simulates a conversation

API_URL="http://localhost:8080/api/chat"

echo "========================================="
echo "   Spring AI Chat CLI Demo"
echo "========================================="
echo ""

# Test 1: Simple chat
echo "Test 1: Simple Chat Mode"
echo "-------------------------"
echo "You> Hello, who are you?"
echo ""

RESPONSE=$(curl -s -X POST "$API_URL" \
    -H "Content-Type: application/json" \
    -d '{"message": "Hello, who are you?", "useRag": false, "useAgent": false}')

AI_RESPONSE=$(echo "$RESPONSE" | jq -r '.response')
echo "AI> $AI_RESPONSE"
echo ""
echo ""

# Test 2: Simple question
echo "Test 2: Ask a question"
echo "----------------------"
echo "You> What is the capital of France?"
echo ""

RESPONSE=$(curl -s -X POST "$API_URL" \
    -H "Content-Type: application/json" \
    -d '{"message": "What is the capital of France?", "useRag": false, "useAgent": false}')

AI_RESPONSE=$(echo "$RESPONSE" | jq -r '.response')
echo "AI> $AI_RESPONSE"
echo ""
echo ""

# Test 3: Code question
echo "Test 3: Programming question"
echo "----------------------------"
echo "You> Write a simple hello world in Python"
echo ""

RESPONSE=$(curl -s -X POST "$API_URL" \
    -H "Content-Type: application/json" \
    -d '{"message": "Write a simple hello world in Python", "useRag": false, "useAgent": false}')

AI_RESPONSE=$(echo "$RESPONSE" | jq -r '.response')
echo "AI> $AI_RESPONSE"
echo ""
echo ""

echo "========================================="
echo "Demo completed!"
echo "Run './chat.sh' for interactive chat"
echo "========================================="
