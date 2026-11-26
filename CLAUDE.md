# 라이브 방송 플랫폼

개인 프로젝트로 만드는 실시간 방송 웹 서비스(치지직형).
브라우저에서 WHIP로 송출(PC 화면 공유 / 모바일 카메라)하고 WHEP로 시청한다.
미디어 서버는 MediaMTX이고, 백엔드는 방송 정보와 인증·권한만 맡는다.

코드 스타일, Javadoc, 커밋 메시지, 브랜치 규칙은 docs/CONVENTION.md를 따른다.
main과 dev에 직접 커밋하지 않는다. 이슈별 브랜치를 만들어 dev로 PR을 보낸다.

## 구조
- apps/api — Spring Boot 4.2, Java 25, Gradle, Spring Security, JPA, Flyway, PostgreSQL
- apps/web — React + Vite + TypeScript, pnpm
- infra/ — docker-compose.yml, mediamtx.yml
- docs/ — CONVENTION.md 등 문서

## 명령
- 환경변수: 처음 한 번 `cp .env.example .env`. 백엔드는 루트 .env를 직접 읽는다.
- 인프라: `docker compose --env-file .env -f infra/docker-compose.yml up -d` (postgres, mediamtx)
- 백엔드 실행: `cd apps/api && ./gradlew bootRun` (http://localhost:8080, 헬스 체크 /api/health)
- 백엔드 테스트: `cd apps/api && ./gradlew test` (Testcontainers를 쓰므로 Docker가 켜져 있어야 한다)
- 프론트 실행: `cd apps/web && pnpm install && pnpm dev` (http://localhost:5173)
- 프론트 검사: `cd apps/web && pnpm lint && pnpm build`, 포매팅 `pnpm format`
- API 타입 생성: `cd apps/web && pnpm gen:api` (백엔드 실행 중일 때. /v3/api-docs에서 TS 타입 생성)

## 규칙
- 권한은 서버에서 검사한다. 프론트에서 버튼을 숨기는 건 UX일 뿐 보안이 아니다.
  - 인증이 필요한 경로는 SecurityConfig에서 지정한다.
  - 소유자 검사는 서비스 계층에서 한다. 소유자가 아니면 `BusinessException(ErrorCode.BROADCAST_FORBIDDEN)`을 던진다.
- 401(비로그인)과 403(소유자 아님)을 구분한다. 둘 다 공통 에러 형식 JSON으로 응답한다.
- 방송 상태 전이는 Broadcast 엔티티 메서드(start, end)에서만 한다. setter로 상태를 바꾸지 않는다.
  READY → LIVE → ENDED, 역방향 없음.
- MediaMTX publish/read 인증은 `POST /internal/mediamtx/auth`에서 처리한다.
  publish는 소유자이고 방송이 ENDED가 아닐 때만 허용한다. /internal 경로는 외부에 공개하지 않는다.
- 에러 응답 형식: `{ "error": { "code": "...", "message": "..." } }`. GlobalExceptionHandler 한 곳에서 만든다.
- DB 스키마 변경은 Flyway 마이그레이션 파일로만 한다. `ddl-auto`는 `validate`.
- 권한 관련 코드를 바꾸면 권한 매트릭스 테스트(AuthorizationMatrixTest)도 갱신한다.
- 프론트의 API 타입은 생성된 타입을 쓴다. 응답 타입을 손으로 다시 쓰지 않는다.
- 비밀값은 .env에만 두고 커밋하지 않는다. 환경변수를 추가하면 .env.example도 갱신한다.
- 오픈소스 코드를 가져오면 README의 "출처" 절에 추가한다.

## 헷갈리기 쉬운 점
- getUserMedia/getDisplayMedia는 HTTPS 또는 localhost에서만 동작한다.
  모바일 테스트는 cloudflared 터널을 쓰고, 휴대폰은 PC와 같은 Wi-Fi에 연결한다.
- 화면 공유 소리는 Chrome/Edge에서만 된다.
  탭/시스템 소리와 마이크를 AudioContext로 합쳐 오디오 트랙 하나로 송출한다.
- iOS 시청과 녹화 재생 호환을 위해 H.264를 우선 협상한다.
- Spring Security CSRF가 켜져 있다. 프론트는 XSRF-TOKEN 쿠키 값을 X-XSRF-TOKEN 헤더로 보낸다.
  /internal/** 경로만 CSRF 예외다.
- 개발 중에는 Vite proxy가 /api를 localhost:8080으로 넘긴다. 같은 출처가 되어야 세션 쿠키가 동작한다.
