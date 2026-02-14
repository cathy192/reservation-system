package com.learning.reservation.domain.model;

import com.learning.shared.kernel.domain.ValueObject;

public record MemberId(Long value) implements ValueObject {

    public MemberId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("MemberId는 양수여야 합니다.");
        }
    }
}
