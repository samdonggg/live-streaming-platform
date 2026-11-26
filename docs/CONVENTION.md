# 코드 컨벤션

## 1. Git

### 1.1 커밋 메시지

**규칙**

- 아래 형식으로 쓴다. 제목과 본문, 본문과 꼬리말은 각각 빈 줄로 나눈다.

  ```
  <Type>: <제목>

  - 변경 내용 1
  - 변경 내용 2

  Ref: #이슈번호
  ```

- 커밋 유형(Type)은 영어 대문자로 시작하고 아래에서 고른다.

  | 커밋 유형 | 의미 |
  |---|---|
  | `Feat` | 새로운 기능 추가 |
  | `Fix` | 버그 수정 |
  | `Docs` | 문서 수정 |
  | `Style` | 코드 포매팅, 세미콜론 누락 등 코드 동작이 바뀌지 않는 변경 |
  | `Refactor` | 코드 리팩토링 |
  | `Test` | 테스트 코드 추가, 테스트 코드 리팩토링 |
  | `Chore` | 패키지 매니저·빌드 설정 수정, 그 외 기타 수정 (예: `.gitignore`) |
  | `Design` | CSS 등 사용자 UI 디자인 변경 |
  | `Comment` | 필요한 주석 추가 및 변경 |
  | `Rename` | 파일 또는 폴더 이름을 바꾸거나 옮기는 작업만 한 경우 |
  | `Remove` | 파일을 삭제하는 작업만 한 경우 |
  | `!BREAKING CHANGE` | 큰 API 변경 |
  | `!HOTFIX` | 치명적인 버그를 급하게 고치는 경우 |

- 유형 뒤의 제목과 본문은 한글로 쓴다. 본문에는 어떻게 바꿨는지보다 **무엇을 왜** 바꿨는지를 쓴다. 자기 코드가 읽기만 해도 이해될 거라고 생각하지 않는다.
- 제목 끝에는 `.`을 쓰지 않고, 영문 기준 50자 이내로 쓴다.
- 여러 항목은 글머리 기호(`-`)로 쓴다.
- 이슈 번호는 본문 아래 꼬리말에 `Ref: #이슈번호` 형식으로 쓴다. 관련 이슈가 여러 개면 한 줄에 하나씩 쓴다. 제목 끝의 `(#N)`은 GitHub이 병합할 때 붙이는 PR 번호이므로 직접 쓰지 않는다.
- 커밋은 변경의 성격별로 나눈다. `.env`, 비밀번호, 토큰은 커밋하지 않는다(`.env.example`만 올린다).

**이유**: 과거 변경을 쉽게 추적하고, 실무의 협업 방식에 익숙해지기 위해서다.

```
// 좋은 예
Feat: 방송 종료 시 MediaMTX 송출 세션 강제 종료

- 방송 종료 API에서 MediaMTX 세션 종료 API 호출
- 송출자 연결이 끊기면 방송 상태를 ENDED로 변경

Ref: #12
Ref: #15

// 나쁜 예: 유형이 소문자이다
feat: 방송 목록 조회 API 추가

// 나쁜 예: 유형 대신 도메인을 대괄호로 쓰고, 제목 끝에 PR 번호를 직접 썼다
[방송] Broadcast 엔티티 및 상태 전이 구현 (#5)
```

### 1.2 브랜치 전략

**규칙**

- 브랜치는 `main`과 `dev`를 둔다. `dev`는 개발 중인 코드를 모으는 곳이고, `main`은 배포 서버에서 실행하는 코드다. `main`과 `dev`에는 직접 커밋하지 않는다.
- 개발은 다음 순서로 진행한다.
  1. 이슈를 만든다.
  2. `dev`에서 이슈별 브랜치를 만든다.
  3. 기능을 구현하고 `dev`로 PR을 보내 병합한다.
  4. `dev`에 모인 코드를 `main`으로 PR을 보내 병합한다.
- 브랜치 이름은 `<유형>/<이슈번호>`로 쓴다. 기능 구현은 `feature/`를 쓰고, 나머지는 1.1의 커밋 유형을 소문자로 쓴다. 이슈의 type 라벨과 같은 체계다.

  | 브랜치 유형 | 커밋 유형 | 이슈 라벨 | 예 |
  |---|---|---|---|
  | `feature/` | `Feat` | `type: feat` | `feature/12` 방송 종료 API |
  | `fix/` | `Fix` | `type: bug` | `fix/18` 모바일 송출 시 소리 끊김 |
  | `docs/` | `Docs` | `type: docs` | `docs/20` README 기능 확인 절차 |
  | `refactor/` | `Refactor` | `type: refactor` | `refactor/22` |
  | `test/` | `Test` | `type: test` | `test/24` 권한 매트릭스 테스트 |
  | `chore/` | `Chore` | `type: chore` | `chore/26` |
  | `design/` | `Design` | `type: design` | `design/28` 방송 상세 화면 |

**이유**: 기능 브랜치와 `dev`에서 개발하고 확인한 변경만 `main`으로 올려서, 배포 서버에서 실행하는 코드를 안정적으로 유지한다.

```
feature/12 ─┐
fix/18     ─┼─ ③ PR → dev ─ ④ PR → main
docs/20    ─┘   (이슈별 브랜치)    (dev에 모인 코드)
```

## 2. 백엔드 (Java 21 / Spring Boot)

### 2.1 패키지 구조

기본 패키지는 `com.livestream`이다. 먼저 **도메인별**로 나누고, 각 도메인 안을 **계층별**로 나눈다.

```
com.livestream
├─ user/          회원
├─ auth/          로그인, 세션, SecurityConfig
├─ broadcast/     방송 정보, 상태 관리
├─ streaming/     MediaMTX 연동 (인증 훅, 세션 종료)
├─ comment/       댓글
├─ donation/      도네이션
├─ recording/     녹화·다시보기
└─ global/        공통 예외, 에러 응답, 설정
```

각 도메인 안은 `controller`, `service`, `repository`, `domain`, `dto`로 나눈다. 파일이 없는 계층의 폴더는 만들지 않는다.

```
broadcast/
├─ controller/   BroadcastController
├─ service/      BroadcastService
├─ repository/   BroadcastRepository
├─ domain/       Broadcast, BroadcastStatus
└─ dto/          BroadcastCreateRequest, BroadcastResponse
```

### 2.2 계층 규칙

- Controller는 DTO만 주고받는다. 엔티티를 응답으로 내보내지 않는다.
- 트랜잭션은 Service에 건다. 조회 메서드는 `@Transactional(readOnly = true)`.
- Repository는 Service에서만 호출한다. 다른 도메인의 Repository를 직접 부르지 말고 그 도메인의 Service를 거친다.

### 2.3 엔티티

- `@Setter`를 쓰지 않는다. 상태 변경은 의미 있는 이름의 메서드로 한다. 예: `broadcast.end(now)`
- 기본 생성자는 `protected`로 둔다.
- 연관관계는 `fetch = LAZY`가 기본이다.
- enum은 `@Enumerated(EnumType.STRING)`으로 저장한다.

### 2.4 DTO

- `record`로 만든다.
- 이름은 `BroadcastCreateRequest`, `BroadcastResponse`처럼 `도메인 + 동작 + Request/Response`.
- 요청 검증은 Bean Validation(`@NotBlank`, `@Size` 등)과 Controller의 `@Valid`로 한다.

### 2.5 예외

- 비즈니스 예외는 `BusinessException`에 `ErrorCode`를 담아 던진다. 예: `throw new BusinessException(ErrorCode.BROADCAST_NOT_FOUND)`
- `ErrorCode` enum이 HTTP 상태, 코드 문자열, 기본 메시지를 가진다.
- `try-catch`로 예외를 삼키지 않는다. 처리할 수 없으면 그대로 올린다.

### 2.6 Javadoc

**규칙**

- 첫 문장은 무엇을 하는지 한 문장으로 요약하고 "~한다"로 끝낸다. 클래스는 맡은 책임을 한 문장으로 쓴다.
- 첫 문장으로 부족한 내용(왜 그렇게 하는지, 제약, 외부 호출 같은 부수 효과)은 빈 줄 없이 다음 줄에 이어서 쓴다.
- 태그는 필요한 것만 쓴다.

  | 태그 | 쓰는 경우 |
  |---|---|
  | `@param` | 이름만으로 의미, 단위, 제약을 알기 어려운 매개변수만 쓴다. 모든 매개변수를 쓸 필요는 없다 |
  | `@return` | 반환값의 의미가 이름만으로 분명하지 않을 때 쓴다 |
  | `@throws` | 호출하는 쪽이 알아야 하는 예외를 쓴다. `BusinessException`은 던지는 에러 코드 이름을 적는다 |

- 쓰지 않아도 되는 곳: getter, 이름만으로 충분한 메서드, `@Override`한 메서드, private 메서드.
- 코드나 이름은 `{@code ...}`로, 다른 클래스를 가리킬 때는 `{@link ...}`로 쓴다.

**이유**: 이름을 되풀이할 뿐인 태그를 모두 채우면 읽는 사람에게 도움이 되지 않고, 코드가 바뀔 때 설명과 어긋난다. 필요한 정보만 쓰면 작성 부담도 줄고 읽기 쉽다. IntelliJ에서 `/**`를 입력하고 Enter를 누르면 틀이 자동으로 만들어지므로, 필요 없는 태그를 지우면 된다.

```java
// 템플릿: 필요 없는 줄은 지운다
/**
 * 무엇을 하는지 한 문장으로 요약한다.
 * 필요하면 왜 그렇게 하는지, 제약이나 부수 효과를 이어서 쓴다.
 *
 * @param 매개변수 이름만으로 알기 어려운 의미나 단위 (필요할 때만)
 * @return 반환값의 의미 (필요할 때만)
 * @throws BusinessException 던지는 에러 코드 (호출하는 쪽이 알아야 할 때만)
 */

// 좋은 예: 알기 어려운 매개변수에만 @param을 쓴다 (MediaMtxAuthService.authorize)
/**
 * MediaMTX의 송출·시청 요청을 허용할지 판단한다.
 * 송출은 방송 소유자이고 방송이 종료되지 않았을 때만 허용하고, 시청은 방송 중일 때 누구에게나 허용한다.
 *
 * @param action MediaMTX가 보내는 동작 이름 ({@code publish} 또는 {@code read})
 * @param path   MediaMTX 스트림 경로. 방송의 {@code streamPath}와 같다
 * @param token  송출 페이지에서 발급한 일회용 송출 토큰. 시청 요청이면 {@code null}
 */

// 좋은 예: 던지는 에러 코드를 적는다 (Broadcast.end)
/**
 * 방송을 종료 상태로 바꾼다.
 * 종료한 방송은 다시 시작할 수 없고, 이후 MediaMTX 송출 인증도 거절된다.
 *
 * @param now 종료 시각(UTC). 테스트에서 고정할 수 있도록 {@link java.time.Clock}에서 얻은 값을 넘긴다
 * @throws BusinessException 이미 종료된 방송이면 {@code BROADCAST_ALREADY_ENDED}
 */

// 좋은 예: 서비스 메서드의 예외 (BroadcastService.update)
/**
 * 방송 제목과 설명을 수정한다.
 *
 * @throws BusinessException 방송이 없으면 {@code BROADCAST_NOT_FOUND}, 소유자가 아니면 {@code BROADCAST_FORBIDDEN}
 */

// 나쁜 예: 이름을 되풀이하는 태그를 모두 채운다
/**
 * 방송을 생성한다.
 *
 * @param ownerId 소유자 아이디
 * @param title 제목
 * @param description 설명
 */
```

### 2.7 Lombok

- 허용: `@Getter`, `@RequiredArgsConstructor`, `@Slf4j`
- 금지: `@Setter`, `@Data`, `@AllArgsConstructor`(엔티티)

### 2.8 기타

- 시간은 `Instant`(UTC)로 저장하고, `Clock`을 주입받아 테스트에서 고정할 수 있게 한다.
- 들여쓰기는 공백 4칸이다. 포매팅은 IntelliJ 기본 설정과 `.editorconfig`를 따른다.
- 와일드카드 import(`import java.util.*`)를 쓰지 않는다.

## 3. API

- 경로는 `/api/` 아래 복수형 명사, kebab-case로 쓴다. 예: `/api/broadcasts/{id}`
- 상태를 바꾸는 동작은 하위 경로 + POST로 한다. 예: `POST /api/broadcasts/{id}/end`
- JSON 필드는 camelCase, 시간은 ISO-8601 UTC 문자열이다.
- 목록은 `?page=0&size=20`으로 페이지를 나눈다.
- 에러 응답은 항상 `{ "error": { "code": "...", "message": "..." } }` 형식이다.

| 상황 | 상태 코드 |
|---|---|
| 생성 성공 | 201 |
| 삭제 성공 | 204 |
| 입력값 오류 | 400 |
| 비로그인 | 401 |
| 권한 없음 (소유자 아님) | 403 |
| 없는 리소스 | 404 |
| 상태 충돌 (예: 이미 종료된 방송 종료) | 409 |

## 4. 프론트엔드 (React / TypeScript)

### 4.1 폴더 구조

```
src/
├─ pages/          라우트 단위 화면
├─ features/       도메인 기능 (auth, broadcast, streaming, comment ...)
└─ shared/
   ├─ api/         API 클라이언트, 생성된 타입
   ├─ ui/          공용 컴포넌트
   └─ lib/         공용 유틸
```

### 4.2 규칙

- 컴포넌트 파일과 이름은 PascalCase, 훅은 `useXxx`.
- API 호출은 `shared/api`를 통해서만 한다. 컴포넌트에서 `fetch`를 직접 부르지 않는다.
- 서버 데이터는 TanStack Query로 관리한다. 전역 상태 라이브러리는 필요해질 때 도입한다.
- `any`를 쓰지 않는다. 불가피하면 `unknown`을 쓰고 좁힌다.
- WebRTC·미디어 코드는 `features/streaming`에 모은다. 컴포넌트가 언마운트되면 `MediaStreamTrack.stop()`과 `RTCPeerConnection.close()`를 반드시 호출한다.
- 포매팅과 린트는 Prettier + ESLint를 따른다.

## 5. 테스트

- 테스트 메서드 이름은 무엇을 검증하는지 한글 문장으로 쓰고, 띄어쓰기는 `_`로 쓴다.
  `@DisplayName`은 공백 외의 특수문자(괄호, 쉼표, `→` 등)가 꼭 필요할 때만 쓴다.
  리포트에서 `_`가 공백으로 보이도록 `src/test/resources/junit-platform.properties`에
  `junit.jupiter.displayname.generator.default=org.junit.jupiter.api.DisplayNameGenerator$ReplaceUnderscores`를 둔다.

  ```java
  @Test
  void 이미_종료된_방송을_종료하면_예외가_발생한다() { ... }
  ```

  **이유**: 영어 이름과 `@DisplayName`을 함께 쓰면 같은 설명을 두 번 쓰게 되고, 동작이 바뀔 때 한쪽만 고쳐져 어긋나기 쉽다.
- 본문은 given / when / then 순서로 나눈다.
- 도메인 로직(상태 전이 등)은 스프링 없이 단위 테스트한다.
- API와 권한은 `@SpringBootTest` + MockMvc로 통합 테스트한다. DB는 Testcontainers PostgreSQL을 쓴다.
- 권한 매트릭스(비로그인 / 다른 사용자 / 소유자 × 각 API)는 `AuthorizationMatrixTest` 한 곳에서 관리한다.
