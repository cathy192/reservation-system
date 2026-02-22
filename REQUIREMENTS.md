# 예약 시스템 요구사항

## 1. 프로젝트 개요
- 목적: Clean Architecture(헥사고날)와 DDD 기반의 학습용 예약 시스템 구현
- 범위: Reservation 중심으로 Resource, Member, Notification, Payment(Stub)까지 단계적 확장

## 2. 핵심 기능 요구사항
- 예약 생성/조회/취소: REST API `/api/v1/reservations`로 제공
- 중복 예약 방지: 동일 자원·시간대 중복 예약 금지
- 취소 정책: 예약 시작 1시간 전까지만 취소 허용
- 회원 제한: 회원별 동시 예약 수 제한(기본: 3건), 등급별 제한 지원
- 자원 관리: 회의실/테이블 등 자원 CRUD 및 가용성 확인 포트 제공
- 이벤트 발행: 예약 생성/취소 시 도메인 이벤트 발행(초기: in-process)

## 3. 비기능 요구사항
- 아키텍처: Hexagonal (domain 순수, application 포트, adapter 구현)
- 동시성: 기본은 Optimistic Locking(`@Version`), 필요시 Pessimistic/Distributed Lock
- 성능·확장성: Redis 캐시, Caffeine 2-level 캐시 고려; Kafka는 Phase 2 도입
- 보안·신뢰성: 입력 검증, 민감정보 로그 제한, Outbox Pattern(Phase 2) 고려

## 4. 데이터 및 운영
- DB: PostgreSQL(운영), H2(테스트)
- 마이그레이션: Flyway
- 영속성 패턴: Domain에 Repository 인터페이스(Port) 선언, Adapter에서 JPA 구현

## 5. 테스트 요구사항
- 단위 테스트: Domain 레이어 90% 목표(Framework 독립)
- 통합 테스트: Testcontainers(Postgres, Kafka, Redis) 사용
- 동시성 테스트: ExecutorService/CountDownLatch 또는 JMeter로 N명 동시 예약 시나리오 검증
- 아키텍처 검증: ArchUnit으로 계층 의존성 검사

## 6. 우선순위(권장 작업 흐름)
1. `shared-kernel` 완성(AggregateRoot, DomainEvent, VO)
2. Reservation domain + application 완전 구현 및 단위 테스트
3. Adapter: JPA Persistence + REST Controller + Integration Test
4. Optimistic Locking 구현 및 동시성 테스트 → 필요시 Pessimistic 비교
5. Resource / Member 컨텍스트 추가 → 이벤트 기반 연동

---

## 7. 진행 현황

### Phase 1 — Reservation 핵심 기능

| 항목 | 상태 | 비고 |
|------|------|------|
| shared-kernel (AggregateRoot, DomainEvent, ValueObject, DomainException) | ✅ 완료 | |
| Reservation 도메인 (aggregate, value objects, 예외) | ✅ 완료 | |
| 도메인 이벤트 정의 (ReservationCreated, ReservationCancelled) | ✅ 완료 | |
| Application 유스케이스 (Create / Cancel / Get) | ✅ 완료 | |
| REST API 컨트롤러 + 글로벌 예외 핸들러 | ✅ 완료 | |
| JPA 영속성 어댑터 + Flyway 마이그레이션 | ✅ 완료 | |
| Optimistic Locking (`@Version`) | ✅ 완료 | |
| 도메인 이벤트 발행 (in-process, SpringDomainEventPublisher) | ✅ 완료 | |
| 이벤트 리스너 (ReservationEventListener) | ✅ 완료 | |
| 도메인 단위 테스트 (ReservationTest, TimeSlotTest) | ✅ 완료 | |
| 통합 테스트 (ReservationControllerIntegrationTest) | ✅ 완료 | H2 사용 |
| Application 서비스 단위 테스트 | ❌ 미완료 | CreateReservationService 등 |
| 회원별 동시 예약 수 제한 (기본 3건) | ❌ 미완료 | |
| 동시성 테스트 (ExecutorService / JMeter) | ❌ 미완료 | |
| ArchUnit 아키텍처 계층 검증 | ❌ 미완료 | |
| Testcontainers (PostgreSQL) | ❌ 미완료 | 현재 H2로 대체 중 |

### Phase 2 — 컨텍스트 확장

| 항목 | 상태 |
|------|------|
| Resource 컨텍스트 (자원 CRUD, 가용성 확인) | ❌ 미완료 |
| Member 컨텍스트 + 이벤트 기반 연동 | ❌ 미완료 |
| Kafka 도입 (이벤트 외부 발행) | ❌ 미완료 |
| Outbox Pattern | ❌ 미완료 |
| Redis / Caffeine 캐시 | ❌ 미완료 |
