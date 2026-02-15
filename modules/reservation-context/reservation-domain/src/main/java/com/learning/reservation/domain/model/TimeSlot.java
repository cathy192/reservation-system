package com.learning.reservation.domain.model;

import com.learning.shared.kernel.domain.ValueObject;

import java.time.LocalDateTime;

public record TimeSlot( //예약 시간대를 나타내는 Value Object
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
//두 TimeSlot이 겹치는지 확인하는 비지니스 로직이 TimeSlot 내부에 존재하도록 함으로써, 도메인 모델이 스스로의 일관성을 유지할 수 있도록 함
    public boolean overlapsWith(TimeSlot other) {
        return this.startTime.isBefore(other.endTime)
                && other.startTime.isBefore(this.endTime);
    }
}
