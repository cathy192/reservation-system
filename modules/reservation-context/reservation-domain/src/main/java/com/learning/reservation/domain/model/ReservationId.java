package com.learning.reservation.domain.model;

import com.learning.shared.kernel.domain.ValueObject;

import java.util.UUID;

public record ReservationId(UUID value) implements ValueObject {

//recode를 사용함으로써 생성자, equals, hashCode, toString이 자동으로 생성되지만, value가 null인 경우를 방지하기 위해 생성자를 명시적으로 정의
    public ReservationId {
        if (value == null) {
            throw new IllegalArgumentException("ReservationId는 null일 수 없습니다.");
        }
    }

    public static ReservationId generate() {
        return new ReservationId(UUID.randomUUID());
    }
}
