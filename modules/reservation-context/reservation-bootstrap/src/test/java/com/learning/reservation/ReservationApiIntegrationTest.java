package com.learning.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.reservation.adapter.out.persistence.ReservationJpaEntity;
import com.learning.reservation.adapter.out.persistence.ReservationJpaRepository;
import com.learning.reservation.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DisplayName("예약 API 통합 테스트")
class ReservationApiIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ReservationJpaRepository reservationJpaRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private LocalDateTime futureStart;
    private LocalDateTime futureEnd;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        reservationJpaRepository.deleteAll();
        futureStart = LocalDateTime.now().plusDays(1);
        futureEnd = futureStart.plusHours(2);
    }

    private String toJson(Long memberId, Long resourceId,
                          LocalDateTime startTime, LocalDateTime endTime) throws Exception {
        return objectMapper.writeValueAsString(Map.of(
                "memberId", memberId,
                "resourceId", resourceId,
                "startTime", startTime.toString(),
                "endTime", endTime.toString()
        ));
    }

    private UUID saveReservation(Long memberId, Long resourceId,
                                 LocalDateTime startTime, LocalDateTime endTime,
                                 ReservationStatus status) {
        Reservation reservation = Reservation.reconstitute(
                ReservationId.generate(),
                new MemberId(memberId),
                new ResourceId(resourceId),
                new TimeSlot(startTime, endTime),
                status
        );
        ReservationJpaEntity saved = reservationJpaRepository.saveAndFlush(
                ReservationJpaEntity.fromDomain(reservation)
        );
        return saved.getId();
    }

    // ──────────────────────────────────────────────
    // 예약 생성 (POST /api/v1/reservations)
    // ──────────────────────────────────────────────

    @Nested
    @DisplayName("예약 생성 (POST /api/v1/reservations)")
    class CreateReservation {

        @Test
        @DisplayName("정상 예약 생성 → 201 Created, PENDING 상태")
        void shouldCreateReservation() throws Exception {
            mockMvc.perform(post("/api/v1/reservations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(1L, 1L, futureStart, futureEnd)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.memberId").value(1))
                    .andExpect(jsonPath("$.resourceId").value(1))
                    .andExpect(jsonPath("$.status").value("PENDING"));
        }

        @Test
        @DisplayName("동일 자원/시간 중복 예약 → 409 Conflict, DOUBLE_BOOKING")
        void shouldReturn409WhenDoubleBooking() throws Exception {
            saveReservation(1L, 1L, futureStart, futureEnd, ReservationStatus.PENDING);

            mockMvc.perform(post("/api/v1/reservations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(2L, 1L, futureStart, futureEnd)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value("DOUBLE_BOOKING"));
        }

        @Test
        @DisplayName("다른 자원에 같은 시간 예약 → 201 Created (허용)")
        void shouldAllowSameTimeOnDifferentResource() throws Exception {
            saveReservation(1L, 1L, futureStart, futureEnd, ReservationStatus.PENDING);

            mockMvc.perform(post("/api/v1/reservations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(1L, 2L, futureStart, futureEnd)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value("PENDING"));
        }

        @Test
        @DisplayName("취소된 예약 시간대에 재예약 → 201 Created (허용)")
        void shouldAllowBookingOnCancelledSlot() throws Exception {
            saveReservation(1L, 1L, futureStart, futureEnd, ReservationStatus.CANCELLED);

            mockMvc.perform(post("/api/v1/reservations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(2L, 1L, futureStart, futureEnd)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value("PENDING"));
        }
    }

    // ──────────────────────────────────────────────
    // 예약 조회 (GET /api/v1/reservations/{id})
    // ──────────────────────────────────────────────

    @Nested
    @DisplayName("예약 조회 (GET /api/v1/reservations/{id})")
    class GetReservation {

        @Test
        @DisplayName("존재하는 예약 조회 → 200 OK, 예약 정보")
        void shouldReturnReservation() throws Exception {
            UUID id = saveReservation(1L, 1L, futureStart, futureEnd, ReservationStatus.PENDING);

            mockMvc.perform(get("/api/v1/reservations/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.memberId").value(1))
                    .andExpect(jsonPath("$.resourceId").value(1))
                    .andExpect(jsonPath("$.status").value("PENDING"));
        }

        @Test
        @DisplayName("존재하지 않는 ID 조회 → 400 Bad Request, ILLEGAL_ARGUMENT")
        void shouldReturn400WhenNotFound() throws Exception {
            mockMvc.perform(get("/api/v1/reservations/{id}", UUID.randomUUID()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("ILLEGAL_ARGUMENT"));
        }
    }

    // ──────────────────────────────────────────────
    // 예약 취소 (DELETE /api/v1/reservations/{id})
    // ──────────────────────────────────────────────

    @Nested
    @DisplayName("예약 취소 (DELETE /api/v1/reservations/{id})")
    class CancelReservation {

        @Test
        @DisplayName("정상 취소 (1시간 이상 전) → 204 No Content, DB 상태 CANCELLED")
        void shouldCancelReservation() throws Exception {
            UUID id = saveReservation(1L, 1L, futureStart, futureEnd, ReservationStatus.PENDING);

            mockMvc.perform(delete("/api/v1/reservations/{id}", id))
                    .andExpect(status().isNoContent());

            ReservationJpaEntity entity = reservationJpaRepository.findById(id).orElseThrow();
            assertThat(entity.toDomain().getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        }

        @Test
        @DisplayName("존재하지 않는 예약 취소 → 400 Bad Request, ILLEGAL_ARGUMENT")
        void shouldReturn400WhenCancellingNonExistent() throws Exception {
            mockMvc.perform(delete("/api/v1/reservations/{id}", UUID.randomUUID()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("ILLEGAL_ARGUMENT"));
        }

        @Test
        @DisplayName("이미 취소된 예약 재취소 → 400 Bad Request, ALREADY_CANCELLED")
        void shouldReturn400WhenAlreadyCancelled() throws Exception {
            UUID id = saveReservation(1L, 1L, futureStart, futureEnd, ReservationStatus.CANCELLED);

            mockMvc.perform(delete("/api/v1/reservations/{id}", id))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("ALREADY_CANCELLED"));
        }

        @Test
        @DisplayName("시작 1시간 이내 취소 시도 → 400 Bad Request, CANCELLED_NOT_ALLOWED")
        void shouldReturn400WhenCancellingWithinOneHour() throws Exception {
            LocalDateTime soonStart = LocalDateTime.now().plusMinutes(30);
            LocalDateTime soonEnd = soonStart.plusHours(2);
            UUID id = saveReservation(1L, 1L, soonStart, soonEnd, ReservationStatus.PENDING);

            mockMvc.perform(delete("/api/v1/reservations/{id}", id))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("CANCELLED_NOT_ALLOWED"));
        }
    }
}
