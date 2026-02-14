package com.learning.reservation.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TimeSlot 값 객체 테스트")
class TimeSlotTest {

    private final LocalDateTime base = LocalDateTime.of(2025, 1, 1, 10, 0);

    @Test
    @DisplayName("종료 시간이 시작 시간보다 이전이면 예외")
    void shouldRejectInvalidTimeSlot() {
        assertThatThrownBy(() -> new TimeSlot(base, base.minusHours(1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("겹치는 시간대 감지")
    void shouldDetectOverlap() {
        TimeSlot slot1 = new TimeSlot(base, base.plusHours(2));          // 10:00 ~ 12:00
        TimeSlot slot2 = new TimeSlot(base.plusHours(1), base.plusHours(3)); // 11:00 ~ 13:00

        assertThat(slot1.overlapsWith(slot2)).isTrue();
        assertThat(slot2.overlapsWith(slot1)).isTrue();
    }

    @Test
    @DisplayName("겹치지 않는 시간대")
    void shouldDetectNoOverlap() {
        TimeSlot slot1 = new TimeSlot(base, base.plusHours(2));          // 10:00 ~ 12:00
        TimeSlot slot2 = new TimeSlot(base.plusHours(2), base.plusHours(4)); // 12:00 ~ 14:00

        assertThat(slot1.overlapsWith(slot2)).isFalse();
    }
}
