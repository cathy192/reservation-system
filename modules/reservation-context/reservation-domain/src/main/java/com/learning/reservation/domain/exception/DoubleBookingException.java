package com.learning.reservation.domain.exception;

import com.learning.shared.kernel.domain.DomainException;

public class DoubleBookingException extends DomainException {

    public DoubleBookingException() {
        super("해당 시간대에 이미 예약이 존재합니다.");
    }
}
