package com.learning.shared.kernel.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * 모든 도메인 이벤트의 베이스 인터페이스.
 * 이벤트는 불변(immutable)이며, 발생 시각과 고유 ID를 가진다.
 */
public interface DomainEvent {

    UUID eventId();

    Instant occurredAt();
}
