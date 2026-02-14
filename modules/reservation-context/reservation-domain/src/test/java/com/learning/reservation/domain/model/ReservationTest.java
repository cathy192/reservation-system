package com.learning.reservation.domain.model;

import com.learning.reservation.domain.event.ReservationCreated;
import com.learning.reservation.domain.exception.AlreadyCancelledException;
import com.learning.reservation.domain.exception.CancellationNotAllowedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Reservation 도메인 테스트")
class ReservationTest {

    private final MemberId memberId = new MemberId(1L);
    private final ResourceId resourceId = new ResourceId(1L);

    private TimeSlot futureSlot(int hoursFromNow, int durationHours) {
        LocalDateTime start = LocalDateTime.now().plusHours(hoursFromNow);
        return new TimeSlot(start, start.plusHours(durationHours));
    }

    @Nested
    @DisplayName("예약 생성")
    class Create {

        @Test
        @DisplayName("생성 시 상태는 PENDING이고 ReservationCreated 이벤트가 발행된다")
        void shouldCreateWithPendingStatus() {
            Reservation reservation = Reservation.create(memberId, resourceId, futureSlot(24, 2));

            assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PENDING);
            assertThat(reservation.getId()).isNotNull();
            assertThat(reservation.getDomainEvents())
                    .hasSize(1)
                    .first()
                    .isInstanceOf(ReservationCreated.class);
        }
    }

    @Nested
    @DisplayName("예약 취소")
    class Cancel {

        @Test
        @DisplayName("시작 2시간 전이면 취소 가능")
        void shouldCancelWhenMoreThanOneHourBefore() {
            Reservation reservation = Reservation.create(memberId, resourceId, futureSlot(3, 2));

            reservation.cancel(LocalDateTime.now());

            assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        }

        @Test
        @DisplayName("시작 30분 전이면 취소 불가")
        void shouldNotCancelWhenLessThanOneHourBefore() {
            TimeSlot soonSlot = new TimeSlot(
                    LocalDateTime.now().plusMinutes(30),
                    LocalDateTime.now().plusHours(2)
            );
            Reservation reservation = Reservation.create(memberId, resourceId, soonSlot);

            assertThatThrownBy(() -> reservation.cancel(LocalDateTime.now()))
                    .isInstanceOf(CancellationNotAllowedException.class);
        }

        @Test
        @DisplayName("이미 취소된 예약은 다시 취소 불가")
        void shouldNotCancelAlreadyCancelled() {
            Reservation reservation = Reservation.create(memberId, resourceId, futureSlot(3, 2));
            reservation.cancel(LocalDateTime.now());

            assertThatThrownBy(() -> reservation.cancel(LocalDateTime.now()))
                    .isInstanceOf(AlreadyCancelledException.class);
        }
    }
}
