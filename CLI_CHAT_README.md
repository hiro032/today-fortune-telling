# Console Chat CLI

콘솔 기반 대화형 채팅 인터페이스입니다. Ollama를 통해 로컬에서 실행되는 AI와 대화할 수 있습니다.

## 사용 방법

### 1. 대화형 채팅 시작

```bash
./chat.sh
```

### 2. 데모 실행

```bash
./chat-demo.sh
```

## 채팅 명령어

대화형 모드에서 사용 가능한 명령어:

- `/simple` - Simple 채팅 모드 (기본)
- `/rag` - RAG 모드 (문서 기반 답변)
- `/agent` - Agent 모드 (함수 호출 기능)
- `/exit` 또는 `/quit` - 채팅 종료

## 사용 예시

```
==================================
   Spring AI Chat CLI (Ollama)
==================================

Commands:
  /simple  - Simple chat mode (default)
  /rag     - RAG mode (Retrieval Augmented Generation)
  /agent   - Agent mode (Function calling)
  /exit    - Exit chat
  /quit    - Exit chat

You [simple]> Hello!
AI> How can I assist you today?

You [simple]> /rag
Switched to RAG mode

You [rag]> What is Spring AI?
AI> Spring AI is a framework that...
Sources: 3 documents

You [rag]> /exit
Goodbye!
```

## 기능

### Simple 모드
- 기본 대화 모드
- Ollama의 llama3.2 모델 사용
- 일반적인 질문에 답변

### RAG 모드
- Retrieval Augmented Generation
- 문서 기반으로 답변 생성
- 소스 문서 참조 표시

### Agent 모드
- 함수 호출 기능 활성화
- 계산, 날씨 등의 도구 사용 가능
- 더 복잡한 작업 수행

## 기술 스택

- **Bash** - CLI 인터페이스
- **curl** - HTTP 요청
- **jq** - JSON 파싱
- **Spring Boot** - 백엔드 API (http://localhost:8080)
- **Ollama** - 로컬 LLM (llama3.2)

## 요구사항

- Spring Boot 애플리케이션이 실행 중이어야 함 (`./gradlew bootRun`)
- Ollama 서비스가 실행 중이어야 함 (`ollama serve`)
- jq가 설치되어 있어야 함

## 색상 코드

- 🔵 파란색: 시스템 메시지, AI 응답
- 🟢 초록색: 사용자 입력 프롬프트, 명령어
- 🟡 노란색: 안내 메시지, 상태 변경
- 🔴 빨간색: 에러 메시지

## 트러블슈팅

### "Connection refused" 에러
- Spring Boot 애플리케이션이 실행 중인지 확인하세요
- `curl http://localhost:8080/api/chat/health`로 서버 상태 확인

### 응답이 너무 느림
- Ollama가 처음 실행될 때 모델 로딩에 시간이 걸릴 수 있습니다
- 모델이 로드되면 이후 응답은 빨라집니다

### jq 명령어를 찾을 수 없음
```bash
brew install jq  # macOS
sudo apt-get install jq  # Ubuntu/Debian
```

## API 직접 호출

CLI 없이 직접 API를 호출하려면:

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Hello!",
    "useRag": false,
    "useAgent": false
  }'
```
