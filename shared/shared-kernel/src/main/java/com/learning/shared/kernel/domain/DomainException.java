package com.learning.shared.kernel.domain;

/**
 * 도메인 규칙 위반 시 발생하는 예외의 베이스 클래스.
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }
}
