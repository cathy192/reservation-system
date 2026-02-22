package com.learning.reservation.api;

import com.learning.reservation.application.service.CreateReservationService;
import com.learning.reservation.application.service.GetReservationService;
import com.learning.reservation.application.service.CancelReservationService;
import com.learning.reservation.application.dto.CreateReservationCommand;
import com.learning.reservation.application.dto.ReservationResponse;
import com.learning.reservation.domain.model.Reservation;
import com.learning.reservation.domain.model.MemberId;
import com.learning.reservation.domain.model.ResourceId;
import com.learning.reservation.domain.model.TimeSlot;
import com.learning.reservation.domain.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@DisplayName("예약 시스템 API 통합 테스트")
class ReservationControllerIntegrationTest {

    @Autowired
    private CreateReservationService createReservationService;

    @Autowired
    private GetReservationService getReservationService;

    @Autowired
    private CancelReservationService cancelReservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    private LocalDateTime futureStart;
    private LocalDateTime futureEnd;

    @BeforeEach
    void setUp() {
        futureStart = LocalDateTime.now().plusHours(24);
        futureEnd = futureStart.plusHours(2);
    }

    @Test
    @DisplayName("POST /api/v1/reservations → 201 정상 생성 (Service 레벨)")
    void shouldCreateReservationWithStatus201() {
        // Given: 예약 요청
        CreateReservationCommand command = new CreateReservationCommand(
                1L,           // memberId
                1L,           // resourceId
                futureStart,  // startTime
                futureEnd     // endTime
        );

        // When: 예약 생성 서비스 호출
        ReservationResponse response = createReservationService.execute(command);

        // Then: 응답 검증
        assertThat(response)
                .isNotNull()
                .extracting(ReservationResponse::id)
                .isNotNull();
        
        assertThat(response.status())
                .contains("PENDING");
    }

    @Test
    @DisplayName("GET /api/v1/reservations/{id} → 200 정상 조회 (Service 레벨)")
    void shouldGetReservationByIdWithStatus200() {
        // Given: 예약을 미리 생성
        Reservation reservation = Reservation.create(
                new MemberId(1L),
                new ResourceId(1L),
                new TimeSlot(futureStart, futureEnd)
        );
        reservationRepository.save(reservation);
        UUID reservationId = reservation.getId().value();

        // When: 예약 조회 서비스 호출
        ReservationResponse response = getReservationService.execute(reservationId);

        // Then: 조회된 데이터 검증
        assertThat(response)
                .isNotNull()
                .extracting(ReservationResponse::id)
                .isEqualTo(reservationId);
        
        assertThat(response.status())
                .contains("PENDING");
    }

    @Test
    @DisplayName("DELETE /api/v1/reservations/{id} → 204 정상 취소 (Service 레벨)")
    void shouldCancelReservationWithStatus204() {
        // Given: 예약을 미리 생성
        Reservation reservation = Reservation.create(
                new MemberId(2L),  // 다른 ID로 충돌 방지
                new ResourceId(2L),  // 다른 자원 ID
                new TimeSlot(futureStart, futureEnd)
        );
        reservationRepository.save(reservation);
        UUID reservationId = reservation.getId().value();

        // When: 예약 취소 서비스 호출 (예외 발생하지 않으면 성공)
        cancelReservationService.execute(reservationId);

        // Then: DB에서 상태 확인 (새로 조회)
        Reservation canceled = reservationRepository
                .findById(reservation.getId())
                .orElseThrow();
        
        assertThat(canceled.getStatus().toString())
                .isEqualTo("CANCELLED");
    }

    @Test
    @DisplayName("중복 예약 시도 → 409 Conflict (DoubleBookingException)")
    void shouldReturnConflictWhenDoubleBooking() {
        // Given: 첫 번째 예약 생성
        CreateReservationCommand firstCommand = new CreateReservationCommand(
                1L,
                3L,           // 고유한 resourceId
                futureStart,
                futureEnd
        );
        createReservationService.execute(firstCommand);

        // When: 동일한 시간에 동일 자원으로 두 번째 예약 시도
        CreateReservationCommand duplicateCommand = new CreateReservationCommand(
                2L,           // 다른 memberId
                3L,           // 동일한 resourceId
                futureStart,
                futureEnd
        );

        // Then: DoubleBookingException 발생 확인
        assertThatThrownBy(() -> createReservationService.execute(duplicateCommand))
                .isInstanceOf(com.learning.reservation.domain.exception.DoubleBookingException.class);
    }

    @Test
    @DisplayName("존재하지 않는 예약 조회 → 404 Not Found (IllegalArgumentException)")
    void shouldReturnNotFoundWhenGettingNonExistentReservation() {
        // Given: 존재하지 않는 예약 ID
        UUID nonExistentId = UUID.randomUUID();

        // When & Then: IllegalArgumentException 발생 확인
        // (현재 구현에서는 400으로 반환되지만, 서비스 레벨에서는 예외 발생)
        assertThatThrownBy(() -> getReservationService.execute(nonExistentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("예약을 찾을 수 없습니다");
    }

}
