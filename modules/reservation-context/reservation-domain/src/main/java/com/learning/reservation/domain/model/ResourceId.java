package com.learning.reservation.domain.model;

import com.learning.shared.kernel.domain.ValueObject;

public record ResourceId(Long value) implements ValueObject {

    public ResourceId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("ResourceId는 양수여야 합니다.");
        }
    }
}
