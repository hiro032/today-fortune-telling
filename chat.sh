#!/bin/bash

# Colors
BLUE='\033[0;34m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# API endpoint
API_URL="http://localhost:8080/api/chat"

echo -e "${BLUE}==================================${NC}"
echo -e "${BLUE}   Spring AI Chat CLI (Ollama)   ${NC}"
echo -e "${BLUE}==================================${NC}"
echo ""
echo -e "${YELLOW}Commands:${NC}"
echo -e "  ${GREEN}/simple${NC}  - Simple chat mode (default)"
echo -e "  ${GREEN}/rag${NC}     - RAG mode (Retrieval Augmented Generation)"
echo -e "  ${GREEN}/agent${NC}   - Agent mode (Function calling)"
echo -e "  ${GREEN}/exit${NC}    - Exit chat"
echo -e "  ${GREEN}/quit${NC}    - Exit chat"
echo ""

# Default mode
MODE="simple"

while true; do
    echo -ne "${GREEN}You [$MODE]> ${NC}"
    read -r user_input

    # Check for empty input
    if [ -z "$user_input" ]; then
        continue
    fi

    # Check for commands
    case "$user_input" in
        /exit|/quit)
            echo -e "${BLUE}Goodbye!${NC}"
            exit 0
            ;;
        /simple)
            MODE="simple"
            echo -e "${YELLOW}Switched to Simple chat mode${NC}"
            continue
            ;;
        /rag)
            MODE="rag"
            echo -e "${YELLOW}Switched to RAG mode${NC}"
            continue
            ;;
        /agent)
            MODE="agent"
            echo -e "${YELLOW}Switched to Agent mode${NC}"
            continue
            ;;
    esac

    # Prepare JSON payload based on mode
    if [ "$MODE" = "simple" ]; then
        JSON_PAYLOAD=$(jq -n --arg msg "$user_input" '{message: $msg, useRag: false, useAgent: false}')
    elif [ "$MODE" = "rag" ]; then
        JSON_PAYLOAD=$(jq -n --arg msg "$user_input" '{message: $msg, useRag: true, useAgent: false}')
    elif [ "$MODE" = "agent" ]; then
        JSON_PAYLOAD=$(jq -n --arg msg "$user_input" '{message: $msg, useRag: false, useAgent: true}')
    fi

    # Show thinking indicator
    echo -ne "${BLUE}AI> ${NC}${YELLOW}Thinking...${NC}\r"

    # Make API call
    RESPONSE=$(curl -s -X POST "$API_URL" \
        -H "Content-Type: application/json" \
        -d "$JSON_PAYLOAD")

    # Clear thinking indicator
    echo -ne "\r\033[K"

    # Check if response is valid
    if [ -z "$RESPONSE" ]; then
        echo -e "${RED}Error: No response from server${NC}"
        continue
    fi

    # Extract response using jq
    AI_RESPONSE=$(echo "$RESPONSE" | jq -r '.response // "Error: Could not parse response"')
    SOURCES=$(echo "$RESPONSE" | jq -r '.sources // [] | length')

    # Display AI response
    echo -e "${BLUE}AI> ${NC}${AI_RESPONSE}"

    # Show sources if in RAG mode
    if [ "$MODE" = "rag" ] && [ "$SOURCES" -gt 0 ]; then
        echo -e "${YELLOW}Sources: $SOURCES documents${NC}"
    fi

    echo ""
done
