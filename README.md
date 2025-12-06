# Spring AI RAG + Agent Demo

Spring AI를 활용한 RAG(Retrieval-Augmented Generation)와 Agent(Function Calling) 기능을 구현한 샘플 프로젝트입니다.

## 주요 기능

### 1. 오늘의 운세 (Fortune Telling)
- 이름, 성별, 생년월일 입력을 통한 오늘의 운세 조회
- 웹 UI를 통한 사용자 친화적 인터페이스
- AI가 생성하는 맞춤형 운세 (전반적 운세, 연애운, 금전운, 건강운)
- 별자리, 행운의 숫자, 행운의 색상 제공

### 2. Simple Chat
- 기본 LLM 채팅 기능
- Ollama 로컬 모델과 직접 대화

### 3. RAG (Retrieval-Augmented Generation)
- 문서 기반 질의응답 시스템
- 벡터 스토어를 활용한 유사 문서 검색
- 검색된 컨텍스트를 기반으로 정확한 답변 생성

### 4. Agent (Function Calling)
- AI 에이전트가 필요에 따라 함수를 호출
- 날씨 조회 기능 (WeatherFunction)
- 계산기 기능 (CalculatorFunction)
- 운세 조회 기능 (FortuneFunction)
- 확장 가능한 함수 구조

## 기술 스택

- Java 17
- Spring Boot 3.2.0
- Spring AI 1.0.0-M4
- Ollama (로컬 LLM)
- Gradle 8.5
- HTML/CSS/JavaScript (Frontend)

## 프로젝트 구조

```
spring-ai-rag-agent/
├── src/main/java/com/example/springai/
│   ├── SpringAiRagAgentApplication.java  # 메인 애플리케이션
│   ├── config/
│   │   ├── RagConfiguration.java         # RAG 설정
│   │   └── AgentConfiguration.java       # Agent 함수 설정
│   ├── controller/
│   │   └── ChatController.java           # REST API 컨트롤러
│   ├── service/
│   │   ├── ChatService.java              # 기본 채팅 서비스
│   │   ├── RagService.java               # RAG 서비스
│   │   └── AgentService.java             # Agent 서비스
│   ├── agent/
│   │   ├── WeatherFunction.java          # 날씨 조회 함수
│   │   └── CalculatorFunction.java       # 계산기 함수
│   └── model/
│       ├── ChatRequest.java              # 요청 모델
│       └── ChatResponse.java             # 응답 모델
└── src/main/resources/
    ├── application.yml                   # 설정 파일
    └── documents/                        # RAG용 문서 저장소
        ├── company-info.txt
        ├── products.txt
        └── faq.txt
```

## 설치 및 실행

### 1. 사전 요구사항

- Java 17 이상
- Ollama 설치 및 실행 (https://ollama.com)
- (Gradle은 프로젝트에 포함되어 있으므로 별도 설치 불필요)

### 2. Ollama 설정

Ollama를 설치하고 필요한 모델을 다운로드합니다:

```bash
# Ollama 설치 (macOS)
brew install ollama

# Ollama 서비스 시작
ollama serve

# 채팅 모델 다운로드 (다른 터미널에서)
ollama pull llama3.2

# 임베딩 모델 다운로드
ollama pull nomic-embed-text
```

다른 모델을 사용하려면 `src/main/resources/application.yml` 파일에서 설정 변경:

```yaml
spring:
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        options:
          model: llama3.2  # 원하는 모델로 변경
      embedding:
        options:
          model: nomic-embed-text  # 원하는 임베딩 모델로 변경
```

### 3. 빌드 및 실행

```bash
# 빌드
./gradlew clean build

# 실행
./gradlew bootRun
```

Windows에서는 `gradlew.bat`를 사용하세요:
```bash
gradlew.bat bootRun
```

애플리케이션이 `http://localhost:8080`에서 실행됩니다.

## 운세 웹 UI 사용

브라우저에서 `http://localhost:8080`로 접속하면 운세 조회 화면이 표시됩니다.

1. 이름 입력
2. 성별 선택 (남성/여성)
3. 생년월일 선택
4. "운세 보기" 버튼 클릭
5. AI가 생성한 오늘의 운세 확인

## API 사용법

### 1. 통합 채팅 엔드포인트

모든 기능을 하나의 엔드포인트로 사용:

```bash
# Simple Chat
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "안녕하세요",
    "useRag": false,
    "useAgent": false
  }'

# RAG Chat
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "TechCorp의 미션이 무엇인가요?",
    "useRag": true,
    "useAgent": false
  }'

# Agent Chat
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "서울의 날씨를 알려주세요",
    "useRag": false,
    "useAgent": true
  }'
```

### 2. 개별 엔드포인트

#### Fortune (운세)

```bash
curl -X POST http://localhost:8080/api/fortune \
  -H "Content-Type: application/json" \
  -d '{
    "name": "홍길동",
    "gender": "male",
    "birthDate": "1990-05-15"
  }'
```

응답:
```json
{
  "fortune": "홍길동님의 오늘의 운세...",
  "name": "홍길동"
}
```

#### Simple Chat

```bash
curl -X POST "http://localhost:8080/api/chat/simple?message=안녕하세요"
```

#### RAG Chat

```bash
curl -X POST "http://localhost:8080/api/chat/rag?question=TechCorp의 제품은 무엇인가요?"
```

예제 질문:
- "TechCorp은 어디에 위치하나요?"
- "AI Platform Pro의 가격은 얼마인가요?"
- "지원되는 프로그래밍 언어는 무엇인가요?"

#### Agent Chat

```bash
# 날씨 조회
curl -X POST "http://localhost:8080/api/chat/agent?message=뉴욕의 날씨를 알려주세요"

# 계산
curl -X POST "http://localhost:8080/api/chat/agent?message=123 곱하기 456은 얼마인가요?"
```

### 3. 응답 형식

```json
{
  "response": "답변 내용",
  "sources": [
    "관련 문서 1...",
    "관련 문서 2..."
  ],
  "mode": "simple|rag|agent"
}
```

## 기능 상세 설명

### RAG (Retrieval-Augmented Generation)

1. **문서 로딩**: `src/main/resources/documents/` 디렉토리의 모든 `.txt` 파일을 자동으로 로드
2. **임베딩 생성**: OpenAI의 `text-embedding-3-small` 모델 사용
3. **벡터 저장**: SimpleVectorStore를 사용한 인메모리 저장
4. **검색**: 질문과 유사한 상위 3개 문서 청크 검색
5. **답변 생성**: 검색된 컨텍스트를 기반으로 LLM이 답변 생성

### Agent (Function Calling)

에이전트는 사용자의 질문을 분석하고 필요한 경우 자동으로 함수를 호출합니다.

#### 사용 가능한 함수

1. **WeatherFunction**: 특정 위치의 날씨 정보 조회
   - 파라미터: location (도시명), unit (celsius/fahrenheit)
   - 예: "서울의 날씨를 알려주세요"

2. **CalculatorFunction**: 사칙연산 수행
   - 파라미터: operation (add/subtract/multiply/divide), a, b
   - 예: "125 더하기 375는 얼마인가요?"

## 커스터마이징

### 새로운 문서 추가

`src/main/resources/documents/` 디렉토리에 `.txt` 파일을 추가하면 애플리케이션 시작 시 자동으로 로드됩니다.

### 새로운 Agent Function 추가

1. `agent` 패키지에 새로운 Function 클래스 생성:

```java
public class MyCustomFunction implements Function<Request, Response> {
    public record Request(
        @JsonProperty(required = true, value = "param1")
        @JsonPropertyDescription("Parameter description")
        String param1
    ) {}

    public record Response(String result) {}

    @Override
    public Response apply(Request request) {
        // 함수 로직 구현
        return new Response("result");
    }
}
```

2. `AgentConfiguration`에 빈 등록:

```java
@Bean
@Description("Function description")
public MyCustomFunction myCustomFunction() {
    return new MyCustomFunction();
}
```

3. `AgentService`의 ChatClient에 함수 추가:

```java
this.chatClient = ChatClient.builder(chatModel)
    .defaultFunctions("weatherFunction", "calculatorFunction", "myCustomFunction")
    .build();
```

## 주의사항

- 이 프로젝트는 데모 목적으로 만들어졌으며, 프로덕션 환경에서 사용하려면 추가 보안 및 최적화가 필요합니다.
- SimpleVectorStore는 인메모리 저장소이므로 애플리케이션 재시작 시 데이터가 초기화됩니다.
- 프로덕션 환경에서는 Redis, Pinecone, Weaviate 등의 영구 벡터 스토어 사용을 권장합니다.
- OpenAI API 호출 비용이 발생하므로 사용량을 모니터링하세요.

## 라이선스

MIT License

## 참고 자료

- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [OpenAI API Documentation](https://platform.openai.com/docs)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
