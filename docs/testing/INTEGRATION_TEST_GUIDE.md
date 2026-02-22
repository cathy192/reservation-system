# 예약 API 통합 테스트 가이드

## 개요
예약 시스템의 API 엔드포인트에 대한 통합 테스트를 작성하는 방법을 설명합니다.

## 테스트 파일 위치
```
modules/reservation-context/reservation-bootstrap/src/test/java/
  └── com/learning/reservation/
      └── api/
          └── ReservationControllerIntegrationTest.java
```

## 테스트 설정

### 1. 클래스 레벨 어노테이션
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class ReservationControllerIntegrationTest
```

- `@SpringBootTest`: 완전한 Spring 애플리케이션 컨텍스트 로드 (모든 Bean 포함)
- `webEnvironment = RANDOM_PORT`: 실제 서버를 임의의 포트에서 시작
- `@Transactional`: 각 테스트 후 롤백 (DB 정리)

### 2. 의존성 주입
```java
@LocalServerPort
private int port;

@Autowired
private ReservationRepository reservationRepository;
```

- `@LocalServerPort`: 실행 중인 서버의 포트 번호
- `@Autowired`: Spring이 관리하는 빈 주입

## 테스트 케이스별 작성 방식

### 패턴

#### Given-When-Then 패턴
모든 테스트는 아래 구조를 따릅니다:

```java
@Test
void testName() {
    // Given: 테스트 전제 조건 설정
    // -> 예약 생성, 데이터 준비 등
    
    // When: 실제 작업 수행
    // -> API 호출
    
    // Then: 결과 검증
    // -> Status Code 확인, Response Body 검증
}
```

---

## 각 테스트 상세 설명

### 1️⃣ POST /api/v1/reservations → 201 정상 생성

**목표**: 새로운 예약 생성 요청이 201 상태 코드로 응답하는지 확인

```java
@Test
@DisplayName("POST /api/v1/reservations → 201 정상 생성")
void shouldCreateReservationWithStatus201() {
    // Given: 예약 요청 데이터
    CreateReservationRequest request = new CreateReservationRequest(
            1L,  // memberId
            1L,  // resourceId
            LocalDateTime.now().plusHours(24),  // startTime (24시간 후)
            LocalDateTime.now().plusHours(26)   // endTime (26시간 후, 2시간 예약)
    );

    // When & Then: REST Client로 POST 요청 후 응답 검증
    // [RestAssured 또는 TestRestTemplate 사용]
    // - Status: 201 CREATED
    // - Body.id: 생성된 예약 ID (UUID)
    // - Body.status: "PENDING"
}
```

**검증 항목**:
- HTTP Status: `201 Created`
- Response Body: `id`, `status` 필드 존재
- 상태값: `PENDING`

---

### 2️⃣ GET /api/v1/reservations/{id} → 200 정상 조회

**목표**: 특정 예약을 ID로 조회할 수 있는지 확인

```java
@Test
@DisplayName("GET /api/v1/reservations/{id} → 200 정상 조회")
void shouldGetReservationByIdWithStatus200() {
    // Given: DB에 예약 데이터 미리 저장
    Reservation reservation = Reservation.create(
            new MemberId(1L),
            new ResourceId(1L),
            new TimeSlot(futureStart, futureEnd)
    );
    reservationRepository.save(reservation);
    UUID reservationId = reservation.getId().value();

    // When & Then: GET 요청 후 응답 검증
    // [REST 클라이언트로 GET /api/v1/reservations/{id}]
    // - Status: 200 OK
    // - Body: 조회된 예약 정보
}
```

**검증 항목**:
- HTTP Status: `200 OK`
- Response Body: 요청한 ID와 일치하는 예약 데이터

---

### 3️⃣ DELETE /api/v1/reservations/{id} → 204 정상 취소

**목표**: 예약 취소가 정상 작동하는지 확인

```java
@Test
@DisplayName("DELETE /api/v1/reservations/{id} → 204 정상 취소")
void shouldCancelReservationWithStatus204() {
    // Given: 취소 가능한 예약 생성 (최소 2시간 이후 시작)
    Reservation reservation = Reservation.create(
            new MemberId(1L),
            new ResourceId(1L),
            new TimeSlot(futureStart, futureEnd)
    );
    reservationRepository.save(reservation);
    UUID reservationId = reservation.getId().value();

    // When & Then: DELETE 요청 후 검증
    // [REST 클라이언트로 DELETE /api/v1/reservations/{id}]
    // - Status: 204 No Content
    
    // When: DB에서 상태 재확인
    Reservation canceled = reservationRepository
            .findById(reservation.getId()).orElseThrow();
    
    // Then: 상태가 CANCELLED로 변경됨을 확인
    assertThat(canceled.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
}
```

**검증 항목**:
- HTTP Status: `204 No Content`
- 취소 후 DB 상태: `CANCELLED`

---

### 4️⃣ 중복 예약 시도 → 409 Conflict

**목표**: 동일한 자원/시간에 중복 예약 불가 확인

```java
@Test
@DisplayName("중복 예약 시도 → 409 Conflict")
void shouldReturnConflictWhenDoubleBooking() {
    // Given: 첫 번째 예약 생성
    CreateReservationRequest firstRequest = new CreateReservationRequest(
            1L,
            1L,  // 동일한 resourceId
            futureStart,
            futureEnd
    );
    // [REST 클라이언트로 POST /api/v1/reservations]
    // -> 201 Created 반환

    // When: 동일한 시간에 동일 자원으로 두 번째 예약 시도
    CreateReservationRequest duplicateRequest = new CreateReservationRequest(
            2L,  // 다른 memberId (하지만 동일 자원)
            1L,  // 동일한 resourceId
            futureStart,
            futureEnd
    );
    
    // Then: 409 Conflict 응답
    // [REST 클라이언트로 POST /api/v1/reservations]
    // - Status: 409 Conflict
    // - Body.code: "DOUBLE_BOOKING"
}
```

**검증 항목**:
- HTTP Status: `409 Conflict`
- Error Code: `DOUBLE_BOOKING`
- Error Message: 중복 예약 불가 메시지

---

### 5️⃣ 존재하지 않는 예약 취소 → 404 Not Found

**목표**: 존재하지 않는 자원 요청 시 404 반환 확인

```java
@Test
@DisplayName("존재하지 않는 예약 취소 → 404 Not Found")
void shouldReturnNotFoundWhenCancelingNonExistentReservation() {
    // Given: 존재하지 않는 예약 ID
    UUID nonExistentId = UUID.randomUUID();

    // When & Then: DELETE 요청
    // [REST 클라이언트로 DELETE /api/v1/reservations/{id}]
    // 
    // 현재 구현 상태:
    // - 반환 코드: 400 Bad Request
    // - Error Code: "ILLEGAL_ARGUMENT"
    // - Message: "예약을 찾을 수 없습니다"
    //
    // 권장 리팩토링 (아래 참고):
    // - 반환 코드를 404 Not Found로 변경 필요
}
```

**검증 항목** (현재):
- HTTP Status: `400 Bad Request` *(개선 필요)*
- Error Code: `ILLEGAL_ARGUMENT`

**권장 개선사항** (아래 참고):
- HTTP Status를 `404 Not Found`로 변경

---

## REST 클라이언트 옵션

테스트에서 HTTP 요청을 보내기 위한 옵션들:

### 1. **RestAssured** (권장)
```gradle
testImplementation("io.rest-assured:rest-assured:5.4.0")
```

가독성 좋은 BDD 스타일:
```java
given()
    .contentType(ContentType.JSON)
    .body(request)
.when()
    .post("/api/v1/reservations")
.then()
    .statusCode(201)
    .body("id", notNullValue());
```

### 2. **TestRestTemplate** (표준)
```java
@Autowired
private TestRestTemplate restTemplate;

ResponseEntity<ReservationResponse> response = 
    restTemplate.postForEntity("/api/v1/reservations", 
                               request, 
                               ReservationResponse.class);
```

### 3. **WebTestClient** (Reactive)
```java
@Autowired
private WebTestClient webTestClient;

webTestClient.post()
    .uri("/api/v1/reservations")
    .bodyValue(request)
    .exchange()
    .expectStatus().isCreated();
```

---

## 현재 구현 → 404 처리로 개선하기

현재 코드에서 "존재하지 않는 예약 취소"는 400을 반환하지만, RESTful하게 404로 변경하려면:

### Step 1: 커스텀 예외 생성
```java
// domain/exception/ReservationNotFoundException.java
public class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException(String message) {
        super(message);
    }
}
```

### Step 2: Application Service 수정
```java
// application/service/GetReservationService.java
@Override
public ReservationResponse execute(UUID reservationId) {
    Reservation reservation = reservationRepository
        .findById(new ReservationId(reservationId))
        .orElseThrow(() -> new ReservationNotFoundException(
            "예약을 찾을 수 없습니다: " + reservationId
        ));
    return ReservationResponse.from(reservation);
}
```

### Step 3: GlobalExceptionHandler 업데이트
```java
// adapter/in/web/GlobalExceptionHandler.java
@ExceptionHandler(ReservationNotFoundException.class)
@ResponseStatus(HttpStatus.NOT_FOUND)  // ← 404 설정
public ErrorResponse handleNotFound(ReservationNotFoundException ex) {
    return new ErrorResponse("RESERVATION_NOT_FOUND", ex.getMessage());
}
```

### Step 4: 테스트 업데이트
```java
@Test
void shouldReturnNotFoundWhenCancelingNonExistentReservation() {
    UUID nonExistentId = UUID.randomUUID();
    
    given()
    .when()
        .delete("/api/v1/reservations/{id}", nonExistentId)
    .then()
        .statusCode(HttpStatus.NOT_FOUND.value())  // ← 404
        .body("code", equalTo("RESERVATION_NOT_FOUND"));
}
```

---

## 테스트 실행 방법

### 모든 통합 테스트 실행
```bash
cd reservation-system
./gradlew :modules:reservation-context:reservation-bootstrap:test \
    --tests ReservationControllerIntegrationTest
```

### 특정 테스트만 실행
```bash
./gradlew :modules:reservation-context:reservation-bootstrap:test \
    --tests ReservationControllerIntegrationTest::shouldCreateReservationWithStatus201
```

### 테스트 결과 보기
```
build/reports/tests/test/index.html
```

---

## 주요 패턴 정리

| 요소 | 설정 | 설명 |
|------|------|------|
| **범위** | `@SpringBootTest` | 전체 컨텍스트 로드 |
| **포트** | `RANDOM_PORT` | 테스트 서버 실행 |
| **DB 정리** | `@Transactional` | 각 테스트 후 롤백 |
| **Given** | Repository 저장 | 테스트 데이터 준비 |
| **When** | REST 클라이언트 | HTTP 요청 발송 |
| **Then** | Assertions | 응답 검증 |

---

## 파일 구조 완성

```
modules/reservation-context/reservation-bootstrap/
├── src/
│   ├── main/
│   │   └── java/com/learning/reservation/
│   │       └── ReservationBootstrapApplication.java
│   └── test/
│       └── java/com/learning/reservation/
│           ├── api/
│           │   └── ReservationControllerIntegrationTest.java  ✅ NEW
│           ├── ReservationApplicationTest.java
│           └── [기타 단위 테스트]
└── build.gradle.kts  ← io.rest-assured 의존성 추가됨
```

---

## 참고 아키텍처

```
┌─────────────────────────────────────────┐
│           Test Layer                    │
│  ReservationControllerIntegrationTest   │
└──────────────────┬──────────────────────┘
                   │ HTTP 요청
┌──────────────────▼──────────────────────┐
│        Adapter (Web) Layer              │
│    ReservationController                │
│    @PostMapping("/..." ) etc            │
├─────────────────────────────────────────┤
│     Application Service Layer           │
│   CreateReservationService, etc         │
├─────────────────────────────────────────┤
│         Domain Layer                    │
│    Reservation, ReservationId, etc      │
├─────────────────────────────────────────┤
│    Infrastructure (DB, Persistence)    │
│    ReservationRepository (JPA)          │
│    Flyway 마이그레이션 (H2 / PostgreSQL)  │
└─────────────────────────────────────────┘
```
