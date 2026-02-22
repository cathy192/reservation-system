# CLAUDE.md

이 파일은 Claude Code(claude.ai/code)가 이 저장소에서 작업할 때 참고하는 프로젝트 가이드입니다.

## 진행 현황
요구사항 및 구현 진행 상황은 `REQUIREMENTS.md` 참고.

## 프로젝트 개요

Java 21 + Spring Boot 4.0.2 기반의 예약 시스템 학습 프로젝트. 헥사고날 아키텍처(Ports & Adapters)와 DDD 원칙을 따르며, Gradle 멀티 모듈 구조로 관심사를 명확히 분리한다.

## 명령어

```bash
# 빌드
./gradlew build
./gradlew clean build

# 애플리케이션 실행 (PostgreSQL 필요)
./gradlew :modules:reservation-context:reservation-bootstrap:bootRun

# 전체 테스트 실행
./gradlew test

# 특정 모듈 테스트
./gradlew :modules:reservation-context:reservation-bootstrap:test

# 특정 테스트 클래스 실행
./gradlew test --tests "com.learning.reservation.domain.model.ReservationTest"

# DB 마이그레이션 (Flyway — 애플리케이션 시작 시 자동 실행)
# 마이그레이션 파일 위치: modules/reservation-context/reservation-bootstrap/src/main/resources/db/migration/
```

## 모듈 구조 및 의존성 규칙

```
shared/
└── shared-kernel/          # 공통 기반 클래스: AggregateRoot, DomainEvent, ValueObject, DomainException
                            # 외부 의존성 없음

modules/reservation-context/
├── reservation-domain/     # 순수 비즈니스 로직 — Spring, JPA 사용 금지
├── reservation-application/# 유스케이스 & 포트 — Spring, JPA 사용 금지
├── reservation-adapter/    # Spring Web + JPA 구현체
└── reservation-bootstrap/  # Spring Boot 진입점, 설정, Flyway 마이그레이션
```

**의존성 방향**: `bootstrap → adapter → application → domain ← shared-kernel`

domain과 application 레이어는 프레임워크에 의존하면 안 된다. Spring/JPA 코드는 반드시 `reservation-adapter` 또는 `reservation-bootstrap`에만 위치해야 한다.

## 아키텍처

### 레이어별 책임

- **domain**: 애그리게이트 루트(`Reservation`), 값 객체(`TimeSlot`, `ReservationId`), 도메인 이벤트(`ReservationCreated`, `ReservationCancelled`), 도메인 예외, 리포지토리 인터페이스(포트)
- **application**: 유스케이스 인터페이스(`CreateReservationUseCase` 등)와 도메인 객체를 조율하는 서비스 구현체 — 비즈니스 로직은 여기에 두지 않음
- **adapter/in**: REST 컨트롤러(`ReservationController`), 글로벌 예외 핸들러, 이벤트 리스너
- **adapter/out**: JPA 영속성 어댑터(도메인 리포지토리 포트 구현), Spring 이벤트 퍼블리셔, `fromDomain()`/`toDomain()` 변환을 담당하는 JPA 엔티티
- **bootstrap**: `UseCaseConfig`에서 애플리케이션 서비스를 수동으로 빈 등록; `application.yml`; Flyway SQL 마이그레이션

### 핵심 패턴

**도메인 이벤트**: `registerEvent()`로 애그리게이트에 등록 → `ReservationPersistenceAdapter`가 `jpaRepository.save()` 후 발행 → `clearDomainEvents()`로 초기화.

**낙관적 잠금**: `ReservationJpaEntity`의 `@Version` 필드로 동시 요청 시 이중 예약 방지. 스키마에 `version BIGINT NOT NULL DEFAULT 0` 포함.

**빈 등록 방식**: 애플리케이션 서비스는 Spring 컴포넌트가 아니다. bootstrap의 `UseCaseConfig`에서 `@Bean`으로 수동 생성하며, 어댑터 구현체를 주입한다.

## API

| 메서드 | 경로 | 상태코드 | 설명 |
|--------|------|----------|------|
| POST | `/api/v1/reservations` | 201 | 예약 생성 |
| GET | `/api/v1/reservations/{id}` | 200 | 예약 조회 |
| DELETE | `/api/v1/reservations/{id}` | 204 | 예약 취소 |

비즈니스 규칙: 시작 시간 1시간 전까지만 취소 가능; 동일 자원/시간 중복 예약은 409로 거부.

## 테스트

- **도메인 테스트** (Spring 없음): `reservation-domain/src/test/`에서 비즈니스 규칙 단위 테스트
- **통합 테스트** (전체 Spring + H2): `reservation-bootstrap/src/test/`에서 `@SpringBootTest` 사용, PostgreSQL 대신 H2로 실행

## 인프라

PostgreSQL은 Docker로 실행. `application.yml` 기본 설정:
- DB: `jdbc:postgresql://localhost:5432/reservation_db`
- 계정: `reservation / reservation`
- 애플리케이션 포트: `8080`
