# ADR-002: Optimistic Locking으로 동시성 제어

## 상태
승인됨

## 맥락
동일 시간대에 동일 자원에 대해 여러 사용자가 동시에 예약을 시도할 수 있다.
이중 예약(Double Booking)을 방지해야 한다.

## 결정
1차적으로 Optimistic Locking(`@Version`)을 적용한다.
충돌 빈도가 높아지면 Pessimistic Locking 또는 Distributed Lock으로 전환을 검토한다.

## 근거
- 예약 시스템 특성상 같은 자원의 같은 시간대에 동시 요청이 빈번하지는 않음
- Optimistic Locking은 충돌이 적을 때 성능이 가장 좋음 (락 대기 없음)
- JPA `@Version`으로 간단하게 구현 가능

## 트레이드오프
- 충돌 시 `OptimisticLockException` 발생 → 재시도 로직 필요
- 인기 자원/시간대에서 충돌이 빈번하면 성능 저하 가능
- Phase 2에서 JMeter 부하 테스트로 Pessimistic Locking과 비교 예정
