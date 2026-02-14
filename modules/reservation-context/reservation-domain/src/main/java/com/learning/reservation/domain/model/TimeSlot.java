package com.learning.reservation.domain.model;

import com.learning.shared.kernel.domain.ValueObject;

import java.time.LocalDateTime;

public record TimeSlot(
        LocalDateTime startTime,
        LocalDateTime endTime
) implements ValueObject {

    public TimeSlot {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("시작/종료 시간은 null일 수 없습니다.");
        }
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("종료 시간은 시작 시간보다 이후여야 합니다.");
        }
    }

    public boolean overlapsWith(TimeSlot other) {
        return this.startTime.isBefore(other.endTime)
                && other.startTime.isBefore(this.endTime);
    }
}
