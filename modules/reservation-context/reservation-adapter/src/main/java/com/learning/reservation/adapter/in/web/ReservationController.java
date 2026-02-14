package com.learning.reservation.adapter.in.web;

import com.learning.reservation.application.dto.CreateReservationCommand;
import com.learning.reservation.application.dto.ReservationResponse;
import com.learning.reservation.application.port.in.CancelReservationUseCase;
import com.learning.reservation.application.port.in.CreateReservationUseCase;
import com.learning.reservation.application.port.in.GetReservationUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final CreateReservationUseCase createReservationUseCase;
    private final GetReservationUseCase getReservationUseCase;
    private final CancelReservationUseCase cancelReservationUseCase;

    public ReservationController(CreateReservationUseCase createReservationUseCase,
                                 GetReservationUseCase getReservationUseCase,
                                 CancelReservationUseCase cancelReservationUseCase) {
        this.createReservationUseCase = createReservationUseCase;
        this.getReservationUseCase = getReservationUseCase;
        this.cancelReservationUseCase = cancelReservationUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse create(@RequestBody CreateReservationRequest request) {
        CreateReservationCommand command = new CreateReservationCommand(
                request.memberId(),
                request.resourceId(),
                request.startTime(),
                request.endTime()
        );
        return createReservationUseCase.execute(command);
    }

    @GetMapping("/{id}")
    public ReservationResponse getById(@PathVariable UUID id) {
        return getReservationUseCase.execute(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable UUID id) {
        cancelReservationUseCase.execute(id);
    }
}
