# Vector Store 정보

## 현재 Vector Store에 저장된 문서

Vector Store는 **비어있지 않습니다!** 애플리케이션 시작 시 자동으로 다음 문서들을 로드합니다:

### 1. company-info.txt
**TechCorp 회사 정보**
- 회사 소개: 2010년 설립된 AI/ML 전문 기술 회사
- 본사 위치: 샌프란시스코 (추가 오피스: 뉴욕, 런던, 도쿄)
- 미션, 비전, 가치
- 수상 내역
- 연락처 정보

### 2. products.txt
**제품 카탈로그**
- **AI Platform Pro**: 엔터프라이즈 ML 플랫폼 ($5,000/월~)
- **CloudServe Enterprise**: 클라우드 인프라 솔루션 ($0.10/시간~)
- **DataSync Pro**: 실시간 데이터 동기화 서비스 ($1,000/월~)
- **AI Consulting Services**: 맞춤형 컨설팅
- **지원 티어**: Basic, Professional, Enterprise

### 3. faq.txt
**자주 묻는 질문**
- 제품 차이점
- 시작 방법
- 지원 티어
- 보안 및 규정 준수
- 시스템 통합
- 지원 언어
- 교육 프로그램
- 가동 시간 보장
- 청구 방식
- 구독 취소 정책

## 어떻게 로드되나요?

### RagConfiguration.java 동작 방식

```java
@Value("classpath:/documents/*.txt")
private Resource[] documentResources;

public void loadDocuments(VectorStore vectorStore, TokenTextSplitter textSplitter) {
    List<Document> documents = new ArrayList<>();

    // 1. resources/documents/*.txt 파일 읽기
    for (Resource resource : documentResources) {
        TextReader textReader = new TextReader(resource);
        documents.addAll(textReader.get());
    }

    // 2. 텍스트를 작은 청크로 분할
    List<Document> splitDocuments = textSplitter.apply(documents);

    // 3. 각 청크를 임베딩하여 Vector Store에 저장
    vectorStore.add(splitDocuments);
}
```

### 프로세스
1. **파일 읽기**: `src/main/resources/documents/*.txt` 파일 스캔
2. **텍스트 분할**: TokenTextSplitter로 적절한 크기로 분할
3. **임베딩 생성**:
   - Ollama의 `nomic-embed-text` 모델 사용
   - 각 텍스트 청크를 벡터로 변환
4. **Vector Store 저장**: SimpleVectorStore에 벡터 저장
5. **검색 준비**: 유사도 검색 가능 상태

## RAG 동작 확인

### 테스트 쿼리
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "What products does TechCorp offer?",
    "useRag": true,
    "useAgent": false
  }'
```

### 실제 응답
```json
{
  "response": "TechCorp offers the following products:\n1. AI Platform Pro\n2. CloudServe Enterprise\n3. DataSync Pro\n4. AI Consulting Services",
  "sources": [
    "Company Information - TechCorp Inc...",
    "Frequently Asked Questions - TechCorp...",
    "TechCorp Product Catalog..."
  ],
  "mode": "rag"
}
```

✅ **3개의 소스 문서에서 정보를 가져와 답변 생성!**

## 직접 테스트해보기

### 1. RAG 모드로 질문하기
```bash
./chat.sh
# 프롬프트에서:
You [simple]> /rag
You [rag]> What is TechCorp's mission?
```

### 2. TechCorp 관련 질문 예시
- "What products does TechCorp offer?"
- "How much does AI Platform Pro cost?"
- "What support tiers are available?"
- "Where is TechCorp located?"
- "What is the uptime guarantee?"
- "Can I integrate with AWS?"

### 3. Simple 모드와 비교
**Simple 모드 (문서 없이):**
- 일반적인 지식으로 답변
- TechCorp에 대해 모름

**RAG 모드 (문서 기반):**
- 실제 문서 내용으로 답변
- TechCorp의 구체적인 정보 제공
- 소스 문서 표시

## 새 문서 추가 방법

1. `src/main/resources/documents/` 폴더에 `.txt` 파일 추가
2. 애플리케이션 재시작
3. 자동으로 Vector Store에 로드됨

예시:
```bash
echo "New product information..." > src/main/resources/documents/new-product.txt
./gradlew bootRun
```

## Vector Store 구현체

현재는 **SimpleVectorStore** 사용:
- 메모리 내 저장
- 재시작 시 데이터 손실
- 개발/테스트에 적합

프로덕션에서는 다음 사용 고려:
- Redis Vector Store
- Pinecone
- Weaviate
- Chroma
- PostgreSQL with pgvector
