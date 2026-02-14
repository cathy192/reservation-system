# ADR-001: Hexagonal Architecture 적용

## 상태
승인됨

## 맥락
예약 시스템의 아키텍처 패턴을 선택해야 한다.
학습 목표는 Clean Architecture/DDD이며, 테스트 용이성과 프레임워크 독립성이 중요하다.

## 결정
Hexagonal Architecture (Ports & Adapters)를 적용한다.

- Domain 계층은 순수 Java로 작성하여 외부 프레임워크 의존성을 0으로 유지
- Application 계층에 Inbound/Outbound Port(인터페이스)를 정의
- Adapter 계층에서 Port를 구현 (JPA, REST 등)
- Bootstrap 모듈에서 Spring Bean으로 조립

## 근거
- **테스트 용이성**: Domain/Application 테스트에 Spring Context가 불필요 → 밀리초 단위 실행
- **프레임워크 독립성**: JPA → jOOQ, REST → gRPC 전환 시 domain/application 코드 변경 없음
- **의존성 방향 강제**: domain이 adapter를 모르므로 비즈니스 로직이 인프라에 오염되지 않음

## 트레이드오프
- Domain ↔ JPA Entity 변환 코드가 필요 (boilerplate 증가)
- 단순 CRUD에는 과한 구조일 수 있음
- 학습 곡선 존재
