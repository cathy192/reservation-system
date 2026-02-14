package com.learning.reservation.config;

import com.learning.reservation.application.port.in.CancelReservationUseCase;
import com.learning.reservation.application.port.in.CreateReservationUseCase;
import com.learning.reservation.application.port.in.GetReservationUseCase;
import com.learning.reservation.application.service.CancelReservationService;
import com.learning.reservation.application.service.CreateReservationService;
import com.learning.reservation.application.service.GetReservationService;
import com.learning.reservation.domain.repository.ReservationRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Application Service를 Spring Bean으로 등록.
 * Application 계층은 Spring에 의존하지 않으므로, 여기서 수동 등록한다.
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public CreateReservationUseCase createReservationUseCase(ReservationRepository repository) {
        return new CreateReservationService(repository);
    }

    @Bean
    public CancelReservationUseCase cancelReservationUseCase(ReservationRepository repository) {
        return new CancelReservationService(repository);
    }

    @Bean
    public GetReservationUseCase getReservationUseCase(ReservationRepository repository) {
        return new GetReservationService(repository);
    }
}
