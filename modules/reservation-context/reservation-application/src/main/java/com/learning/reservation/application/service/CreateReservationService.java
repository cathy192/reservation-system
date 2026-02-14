package com.learning.reservation.application.service;

import com.learning.reservation.application.dto.CreateReservationCommand;
import com.learning.reservation.application.dto.ReservationResponse;
import com.learning.reservation.application.port.in.CreateReservationUseCase;
import com.learning.reservation.domain.exception.DoubleBookingException;
import com.learning.reservation.domain.model.*;
import com.learning.reservation.domain.repository.ReservationRepository;

import java.util.List;

public class CreateReservationService implements CreateReservationUseCase {

    private final ReservationRepository reservationRepository;

    public CreateReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public ReservationResponse execute(CreateReservationCommand command) {
        MemberId memberId = new MemberId(command.memberId());
        ResourceId resourceId = new ResourceId(command.resourceId());
        TimeSlot timeSlot = new TimeSlot(command.startTime(), command.endTime());

        // 이중 예약 검증
        List<Reservation> overlapping =
                reservationRepository.findByResourceIdAndTimeSlotOverlapping(resourceId, timeSlot);
        if (!overlapping.isEmpty()) {
            throw new DoubleBookingException();
        }

        Reservation reservation = Reservation.create(memberId, resourceId, timeSlot);
        Reservation saved = reservationRepository.save(reservation);

        return ReservationResponse.from(saved);
    }
}
