# Render 배포 가이드

이 가이드는 Spring AI 운세 앱을 Render에 배포하는 방법을 설명합니다.

## 준비 사항

1. ✅ GitHub 저장소에 코드 푸시
2. ✅ Render 계정 (https://render.com)
3. ✅ ngrok으로 로컬 Ollama 터널 실행 중

## 배포 방법

### 옵션 1: render.yaml 사용 (권장)

1. **GitHub에 푸시**
   ```bash
   git add .
   git commit -m "Add Render config"
   git push origin main
   ```

2. **Render Dashboard**
   - https://dashboard.render.com 접속
   - "New" → "Blueprint" 클릭
   - GitHub 저장소 연결
   - `today-fortune-telling` 저장소 선택
   - Render가 자동으로 `render.yaml` 감지
   - "Apply" 클릭

3. **환경 변수 확인/수정**
   - Dashboard에서 생성된 서비스 클릭
   - "Environment" 탭
   - `OLLAMA_URL` 값 확인 (ngrok URL)

### 옵션 2: 수동 설정

1. **New Web Service 생성**
   - Render Dashboard → "New +" → "Web Service"
   - GitHub 저장소 연결

2. **설정 입력**

   **Name**: `today-fortune-telling`

   **Environment**: `Docker`

   **Region**: `Oregon` 또는 `Singapore`

   **Branch**: `main`

   **Dockerfile Path**: `./Dockerfile`

   **Plan**: `Free`

3. **Environment Variables 설정**

   "Advanced" 클릭 후 추가:

   | Key | Value |
   |-----|-------|
   | `OLLAMA_URL` | `https://your-ngrok-url.ngrok-free.dev` |
   | `SPRING_PROFILES_ACTIVE` | `prod` |

4. **Create Web Service** 클릭

## Gradle Native Build (Dockerfile 없이)

Dockerfile을 사용하지 않으려면:

**Environment**: `Native`

**Build Command**:
```bash
./gradlew clean build -x test
```

**Start Command**:
```bash
java -Dserver.port=$PORT -jar build/libs/spring-ai-rag-agent-1.0.0.jar
```

**주의**: Render는 Java를 기본 지원하지 않으므로 Docker 사용 권장

## 배포 확인

배포 완료 후:

1. **URL 확인**
   - Render가 제공하는 URL (예: `https://today-fortune-telling.onrender.com`)

2. **Health Check**
   ```bash
   curl https://your-app.onrender.com/api/chat/health
   # 응답: OK
   ```

3. **운세 테스트**
   ```bash
   curl -X POST https://your-app.onrender.com/api/fortune \
     -H "Content-Type: application/json" \
     -d '{
       "name": "테스트",
       "gender": "male",
       "birthDate": "1990-01-01"
     }'
   ```

## ngrok URL 업데이트

ngrok 무료 버전은 재시작 시 URL이 변경됩니다.

### URL 변경 시:

1. **새로운 ngrok URL 확인**
   ```bash
   curl http://localhost:4040/api/tunnels | jq '.tunnels[0].public_url'
   ```

2. **Render 환경 변수 업데이트**
   - Dashboard → 서비스 선택
   - "Environment" 탭
   - `OLLAMA_URL` 수정
   - "Save Changes"
   - 자동으로 재배포됨

## 주의사항

### ⚠️ 로컬 PC 요구사항

- 로컬 PC가 **24/7 켜져있어야** 함
- Ollama 서버 실행 중
- ngrok 터널 실행 중

### ⚠️ Render 무료 플랜 제한

- **15분 비활성 시 슬립 모드**
  - 첫 요청 시 재시작 (30초~1분 소요)

- **월 750시간 무료**
  - 충분함 (한 달 = 720시간)

- **메모리 512MB**
  - Spring Boot는 가능하지만 여유 없음

### ⚠️ ngrok 무료 플랜 제한

- **URL이 매번 변경**
  - 재시작할 때마다 Render 환경 변수 업데이트 필요

- **고정 도메인 불가**
  - 유료 플랜 필요 ($8/월)

## 트러블슈팅

### 문제: "Application failed to start"

**원인**: PORT 환경 변수 미설정

**해결**:
- Dockerfile의 ENTRYPOINT에 `-Dserver.port=${PORT:-8080}` 포함됨
- Render가 자동으로 PORT 설정

### 문제: "Connection refused to Ollama"

**원인**:
1. ngrok 터널 중단
2. 로컬 PC 꺼짐
3. Ollama 서버 중단

**해결**:
```bash
# 로컬 PC에서
ollama serve &
ngrok http 11434
```

### 문제: 첫 요청이 매우 느림

**원인**: Render 무료 플랜의 슬립 모드

**해결**:
- 정상 동작입니다
- 유료 플랜으로 업그레이드하면 해결
- 또는 헬스 체크 핑을 주기적으로 보내서 슬립 방지

## 비용 절감 팁

### ngrok 고정 URL ($8/월)

```bash
# ngrok 유료 플랜 가입 후
ngrok http 11434 --domain=your-domain.ngrok.io
```

Render 환경 변수:
```
OLLAMA_URL=https://your-domain.ngrok.io
```

이제 URL이 변경되지 않습니다!

### Render 유료 플랜 ($7/월)

- 슬립 모드 없음
- 메모리 1GB
- 더 빠른 빌드

## 대안

완전 무료를 원한다면:

1. **Railway** (무료 티어 500시간)
2. **Fly.io** (무료 티어 있음)
3. **Heroku** (더 이상 무료 아님)

또는 OpenAI API로 전환 ($0.15/월):
- 로컬 PC 불필요
- ngrok 불필요
- 안정적

## 참고

- [Render 공식 문서](https://render.com/docs)
- [ngrok 공식 문서](https://ngrok.com/docs)
