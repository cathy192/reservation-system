package com.learning.reservation.domain.exception;

import com.learning.shared.kernel.domain.DomainException;

public class CancellationNotAllowedException extends DomainException {

    public CancellationNotAllowedException() {
        super("예약 시작 1시간 전에는 취소할 수 없습니다.");
    }
}
