package com.learning.reservation.domain.exception;

import com.learning.shared.kernel.domain.DomainException;

public class AlreadyCancelledException extends DomainException {

    public AlreadyCancelledException() {
        super("이미 취소된 예약입니다.");
    }
}
