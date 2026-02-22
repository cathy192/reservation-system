package com.learning.shared.kernel.domain;

/**
 * 도메인 규칙 위반 시 발생하는 예외의 베이스 클래스.
 * 필요 이유: Presntation(Controller) 계층에서 도메인 예외를 잡아서 적절한 HTTP 응답으로 변환하기 위함.
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }
}
