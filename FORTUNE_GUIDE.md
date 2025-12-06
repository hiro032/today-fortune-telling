# 오늘의 운세 - 사용 가이드

## 빠른 시작

### 1. Ollama 실행 확인

```bash
# Ollama가 실행 중인지 확인
curl http://localhost:11434/api/tags

# 모델이 설치되어 있는지 확인
ollama list
```

### 2. 애플리케이션 실행

```bash
./gradlew bootRun
```

### 3. 웹 브라우저에서 접속

```
http://localhost:8080
```

## 웹 UI 사용법

### 메인 화면

![운세 입력 폼]

1. **이름 입력**: 운세를 조회할 분의 이름을 입력합니다
2. **성별 선택**: 남성 또는 여성을 선택합니다
3. **생년월일 선택**: 달력에서 생년월일을 선택합니다
4. **운세 보기 버튼 클릭**: 모든 정보를 입력한 후 버튼을 클릭합니다

### 운세 결과 화면

AI가 생성한 운세가 표시되며, 다음 내용이 포함됩니다:

- **오늘의 전반적인 운세**
- **연애운**: 오늘의 연애 관련 조언
- **금전운**: 재물운에 대한 안내
- **건강운**: 건강 관련 조언
- **조언 및 주의사항**: 오늘 하루를 위한 팁

"다시 보기" 버튼을 클릭하면 새로운 운세를 조회할 수 있습니다.

## API 직접 호출

### cURL 사용

```bash
curl -X POST http://localhost:8080/api/fortune \
  -H "Content-Type: application/json" \
  -d '{
    "name": "홍길동",
    "gender": "male",
    "birthDate": "1990-05-15"
  }'
```

### JavaScript/Fetch 사용

```javascript
fetch('http://localhost:8080/api/fortune', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    name: '홍길동',
    gender: 'male',
    birthDate: '1990-05-15'
  })
})
  .then(response => response.json())
  .then(data => console.log(data.fortune));
```

## 트러블슈팅

### 1. "운세를 가져오는데 실패했습니다" 오류

**원인**: Ollama가 실행되지 않았거나 모델이 설치되지 않음

**해결 방법**:
```bash
# Ollama 실행
ollama serve

# 다른 터미널에서
ollama pull llama3.2
```

### 2. 응답이 느린 경우

**원인**: 로컬 LLM 모델의 추론 시간

**해결 방법**:
- 더 작은 모델 사용 (예: llama3.2:1b)
- GPU 가속 활성화
- 응답 길이 조절

```yaml
# application.yml에서 설정 변경
spring:
  ai:
    ollama:
      chat:
        options:
          model: llama3.2:1b  # 더 작은 모델
```

### 3. CORS 에러

**원인**: 다른 도메인/포트에서 접근 시도

**해결 방법**: FortuneController에 이미 `@CrossOrigin(origins = "*")` 설정되어 있어 문제없음

## 커스터마이징

### 운세 프롬프트 변경

`src/main/java/com/example/springai/service/FortuneService.java` 파일의 `systemMessage` 수정:

```java
String systemMessage = """
    당신은 친절하고 따뜻한 운세 전문가입니다.
    // 원하는 스타일로 수정...
    """;
```

### UI 스타일 변경

`src/main/resources/static/css/style.css` 파일 수정:

```css
/* 배경 그라데이션 색상 변경 */
body {
    background: linear-gradient(135deg, #your-color-1 0%, #your-color-2 100%);
}
```

### 별자리 계산 로직 수정

`src/main/java/com/example/springai/agent/FortuneFunction.java`의 `calculateZodiacSign()` 메서드 수정

## 성능 최적화 팁

1. **응답 캐싱**: 같은 입력에 대한 결과를 캐싱
2. **모델 최적화**: 더 작고 빠른 모델 사용
3. **비동기 처리**: CompletableFuture 사용
4. **타임아웃 설정**: 긴 응답 방지

## 라이선스

MIT License
