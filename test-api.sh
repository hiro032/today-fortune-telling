#!/bin/bash

# Spring AI RAG Agent API Test Script

BASE_URL="http://localhost:8080/api/chat"

echo "========================================="
echo "Spring AI RAG Agent API 테스트"
echo "========================================="
echo ""

# Health Check
echo "1. Health Check..."
curl -s "$BASE_URL/health"
echo -e "\n"

# Simple Chat
echo "2. Simple Chat 테스트..."
curl -s -X POST "$BASE_URL/simple?message=안녕하세요" | jq .
echo -e "\n"

# RAG Chat
echo "3. RAG Chat 테스트 (회사 정보)..."
curl -s -X POST "$BASE_URL/rag?question=TechCorp은 어디에 위치하나요?" | jq .
echo -e "\n"

echo "4. RAG Chat 테스트 (제품 정보)..."
curl -s -X POST "$BASE_URL/rag?question=AI Platform Pro의 가격은 얼마인가요?" | jq .
echo -e "\n"

# Agent Chat - Weather
echo "5. Agent Chat 테스트 (날씨)..."
curl -s -X POST "$BASE_URL/agent?message=서울의 날씨를 알려주세요" | jq .
echo -e "\n"

# Agent Chat - Calculator
echo "6. Agent Chat 테스트 (계산)..."
curl -s -X POST "$BASE_URL/agent?message=123 곱하기 456은 얼마인가요?" | jq .
echo -e "\n"

# Unified Chat - RAG
echo "7. Unified Chat 테스트 (RAG 모드)..."
curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "TechCorp의 미션이 무엇인가요?",
    "useRag": true,
    "useAgent": false
  }' | jq .
echo -e "\n"

# Unified Chat - Agent
echo "8. Unified Chat 테스트 (Agent 모드)..."
curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "456을 123으로 나누면?",
    "useRag": false,
    "useAgent": true
  }' | jq .
echo -e "\n"

echo "========================================="
echo "테스트 완료"
echo "========================================="
