#!/bin/bash

# RAG 모드와 Simple 모드 비교 테스트

API_URL="http://localhost:8080/api/chat"

echo "========================================="
echo "   RAG vs Simple Mode Comparison"
echo "========================================="
echo ""

QUESTION="What is the pricing for AI Platform Pro?"

# Test 1: Simple Mode (No RAG)
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "Question: $QUESTION"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "🔴 SIMPLE MODE (No Documents):"
echo "-------------------------------------------"

RESPONSE=$(curl -s -X POST "$API_URL" \
    -H "Content-Type: application/json" \
    -d "{\"message\": \"$QUESTION\", \"useRag\": false, \"useAgent\": false}")

AI_RESPONSE=$(echo "$RESPONSE" | jq -r '.response')
echo "$AI_RESPONSE"
echo ""
echo ""

# Test 2: RAG Mode (With Documents)
echo "🟢 RAG MODE (With TechCorp Documents):"
echo "-------------------------------------------"

RESPONSE=$(curl -s -X POST "$API_URL" \
    -H "Content-Type: application/json" \
    -d "{\"message\": \"$QUESTION\", \"useRag\": true, \"useAgent\": false}")

AI_RESPONSE=$(echo "$RESPONSE" | jq -r '.response')
SOURCES=$(echo "$RESPONSE" | jq -r '.sources | length')

echo "$AI_RESPONSE"
echo ""
echo "📚 Sources used: $SOURCES documents"
echo ""
echo "Source snippets:"
echo "$RESPONSE" | jq -r '.sources[] | "  - " + .[0:100] + "..."'
echo ""
echo ""

# Test 2: Another question
QUESTION2="Where is TechCorp headquarters located?"

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "Question: $QUESTION2"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "🔴 SIMPLE MODE:"
echo "-------------------------------------------"

RESPONSE=$(curl -s -X POST "$API_URL" \
    -H "Content-Type: application/json" \
    -d "{\"message\": \"$QUESTION2\", \"useRag\": false, \"useAgent\": false}")

AI_RESPONSE=$(echo "$RESPONSE" | jq -r '.response')
echo "$AI_RESPONSE"
echo ""
echo ""

echo "🟢 RAG MODE:"
echo "-------------------------------------------"

RESPONSE=$(curl -s -X POST "$API_URL" \
    -H "Content-Type: application/json" \
    -d "{\"message\": \"$QUESTION2\", \"useRag\": true, \"useAgent\": false}")

AI_RESPONSE=$(echo "$RESPONSE" | jq -r '.response')
SOURCES=$(echo "$RESPONSE" | jq -r '.sources | length')

echo "$AI_RESPONSE"
echo ""
echo "📚 Sources used: $SOURCES documents"
echo ""
echo ""

echo "========================================="
echo "결론:"
echo "- Simple 모드: 일반 지식으로 추측"
echo "- RAG 모드: 실제 문서에서 정확한 정보 제공"
echo "========================================="
