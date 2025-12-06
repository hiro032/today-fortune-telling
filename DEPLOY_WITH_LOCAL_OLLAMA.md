# 로컬 Ollama를 사용한 배포 가이드

이 가이드는 Fly.io(또는 다른 클라우드)에 배포한 Spring Boot 앱이 로컬 PC의 Ollama를 호출하도록 설정하는 방법입니다.

⚠️ **경고**: 이 방법은 데모/테스트 용도입니다. 실제 서비스로는 권장하지 않습니다.

## 전제 조건

- ✅ 로컬 PC가 24/7 켜져있어야 함
- ✅ 인터넷 연결이 안정적이어야 함
- ✅ ngrok 무료 계정 (또는 유료)

## 1. ngrok 설정

### 1-1. ngrok 계정 생성

1. https://dashboard.ngrok.com/signup 접속
2. 무료 계정 생성
3. Dashboard에서 Authtoken 복사

### 1-2. ngrok 인증

```bash
# Authtoken 설정
ngrok config add-authtoken YOUR_AUTHTOKEN_HERE
```

## 2. 로컬 Ollama 터널 시작

### 방법 1: 스크립트 사용 (권장)

```bash
# 터널 시작
/tmp/ollama-tunnel.sh
```

실행하면 다음과 같은 화면이 나타납니다:

```
Session Status                online
Account                       your-email@example.com
Forwarding                    https://abc123.ngrok-free.app -> http://localhost:11434
```

**중요**: `https://abc123.ngrok-free.app` 이 URL을 복사하세요!

### 방법 2: 직접 실행

```bash
# Ollama가 실행 중인지 확인
curl http://localhost:11434/api/tags

# ngrok 터널 시작
ngrok http 11434
```

## 3. Spring Boot 설정 변경

### 로컬 개발용 (application.yml)

```yaml
spring:
  ai:
    ollama:
      base-url: http://localhost:11434
```

### 배포용 (application-prod.yml 생성)

```yaml
spring:
  ai:
    ollama:
      base-url: ${OLLAMA_URL:https://abc123.ngrok-free.app}
```

또는 환경 변수로 설정:

```bash
# Fly.io 환경 변수 설정
fly secrets set OLLAMA_URL=https://abc123.ngrok-free.app
```

## 4. 배포 및 테스트

### Fly.io 배포

```bash
# 빌드
./gradlew build

# Fly.io 배포
fly deploy
```

### 테스트

```bash
# 배포된 앱 테스트
curl -X POST https://your-app.fly.dev/api/fortune \
  -H "Content-Type: application/json" \
  -d '{
    "name": "테스트",
    "gender": "male",
    "birthDate": "1990-01-01"
  }'
```

## 주의사항

### ❌ 문제점

1. **로컬 PC를 항상 켜놔야 함**
   - PC 꺼지면 서비스 중단
   - 재부팅 시 ngrok 재시작 필요

2. **ngrok URL 변경**
   - 무료 계정: ngrok 재시작 시마다 URL 변경
   - URL 변경될 때마다 환경 변수 업데이트 필요
   - 유료 계정 ($8/월): 고정 도메인 가능

3. **보안 위험**
   - Ollama가 인터넷에 노출됨
   - 인증 없음 (누구나 접근 가능)

4. **성능 문제**
   - 네트워크 지연 시간 증가
   - 업로드 속도에 제약

5. **안정성 문제**
   - 인터넷 연결 불안정 시 서비스 중단
   - ngrok 무료 계정의 제한

### ✅ 해결 방법

**ngrok URL이 변경되지 않게 하려면**:

1. **ngrok 유료 플랜** ($8/월)
   - 고정 도메인 제공
   - `your-domain.ngrok.io` 형태

2. **자동화 스크립트**
   ```bash
   # ngrok URL을 자동으로 Fly.io에 업데이트
   # (별도 스크립트 필요)
   ```

## 대안

더 안정적인 배포를 원한다면:

1. **OpenAI API 사용** (월 $0.15~$1)
   - 가장 안정적
   - 설정 간단

2. **Hugging Face API** (무료 티어 있음)
   - 제한적이지만 무료

3. **로컬 전용**
   - 현재 상태 유지
   - 배포하지 않음

## ngrok 명령어 요약

```bash
# 터널 시작
ngrok http 11434

# 터널 종료
Ctrl + C

# ngrok 상태 확인
curl http://localhost:4040/api/tunnels

# ngrok 웹 UI
open http://localhost:4040
```

## 트러블슈팅

### 문제: "Session expired"

ngrok 무료 계정은 8시간마다 재시작 필요

**해결**: ngrok 재시작 후 새로운 URL로 환경 변수 업데이트

```bash
fly secrets set OLLAMA_URL=https://NEW_URL.ngrok-free.app
```

### 문제: "Connection refused"

Ollama가 실행되지 않음

**해결**:
```bash
ollama serve
```

### 문제: 응답이 느림

네트워크 지연

**해결**: 이는 정상입니다. 로컬 PC ↔ 클라우드 왕복 시간이 추가됩니다.

## 라이선스

MIT License
