# System Overview

## 의존성 방향

```
domain (순수 Java) ← application (UseCase) ← adapter (Spring/JPA) ← bootstrap (조립/실행)
```

모든 의존성은 안쪽(domain)을 향한다. domain은 외부 프레임워크를 모른다.

## Bounded Context

```
┌─────────────────┐      ┌──────────────────┐      ┌─────────────────┐
│   Reservation   │ ────▶│    Resource      │      │    Member       │
│ 예약 생성/취소/조회  │      │ 자원(회의실 등) 관리 │      │    회원/등급 관리   │
└────────┬────────┘      └──────────────────┘      └─────────────────┘
         │ Event
         ▼
┌─────────────────┐     ┌──────────────────┐
│  Notification   │     │    Payment       │
│ 알림 발송 (이벤트)  │     │ 결제 처리 (stub)   │
└─────────────────┘     └──────────────────┘
```

## 모듈 구조 (Hexagonal Architecture)

각 Bounded Context는 4개 모듈로 구성:

| 모듈 | 역할 | Spring 의존 |
|------|------|------------|
| domain | Aggregate, VO, Domain Event, Repository 인터페이스 | X |
| application | UseCase 인터페이스, Service 구현, DTO | X |
| adapter | Controller, JPA Entity, Persistence Adapter | O |
| bootstrap | Spring Boot 진입점, Bean 조립, 설정 파일 | O |

### DDD 관련 용어 설명
- 도메인: 디비, 네트워크등은 모르고 오직 비지니스 로직만 가짐.(레시피)
    - VO(Value Object): 속성들로만 정의되는 객체
    - Aggregate: 연관된 객체들을 하나로 묶은 단위
- 애플리캐이션: 사용자가 요청한 작업을 어떻게 처리할 것인가. (주방장)
    - 주요역할: 트랜잭션 관리, 보안 체크, 도메인간의 흐름 제어 
- 어댑터: 외부 시스템(디비, 웹브라우저, 외부 API)사이의 연결 고리
    - Inbound Adapter: 외부 요청을 받아 내부로 전달(ex - Controller, RestAPI)
    - Outbound Adapter: 내부 요청을 외부 시스템으로 전달(ex- db저장, 메일 발송 클라이언트)
    - 특정 기술과 강하게 결합됨
- 바운더리 컨텍스트 간의 관계
    - 각 도메인 별 모델에 맞춰 쪼개서 경계를 가지는 것
    - shared Kernel: 여러 컨텍스트가 공유하는 공통된 모델
    - Customer -Supplier: 상급(공급자)가 하류(고객)에게 요구사항을 반영 
    - ACL(Anti-Corruption Layer): 외부 시스템의 모델이 내 도메인을 오염시키지 않도록 하는 변환기 
    - Published Language: JSON,XML같은 표준화된 형식을 통해 데이터를 주고받음
    - 준수자(Conformist):상류팀의 모델을 하류팀이 군말 없이 따르는 관계
    - 파트너십: 두 컨텍스트가 함께 성공하거나 함꼐 실패
    - 오픈 호스트 서비스: 상류가 여러 하류 컨텍스트를 위해 표준화된 API 제공
    
### 파일 구조

 reservation-system/
  ├── build.gradle.kts                          # Root 빌드 설정
  ├── settings.gradle.kts                       # 멀티모듈 정의
  ├── gradlew
  ├── shared/shared-kernel/                     # DDD 베이스 클래스
  ├── modules/reservation-context/
  │   ├── reservation-domain/                   # 순수 Java 도메인 (+ 테스트 2개)
  │   ├── reservation-application/              # Use Case, Port, Service
  │   ├── reservation-adapter/                  # JPA Entity, Controller
  │   └── reservation-bootstrap/                # Spring Boot 진입점, 설정
  └── infrastructure/docker/docker-compose.yml  # PostgreSQL + Redis

- Java 21, Spring Boot 4.0.2, Gradle 8.14
- PostgreSQL (운영) / H2 (테스트)
- Flyway (DB 마이그레이션)
