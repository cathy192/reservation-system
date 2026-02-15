package com.learning.shared.kernel.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aggregate Root 베이스 클래스.
 * 도메인 이벤트를 내부에 모아두었다가, 인프라 계층에서 발행한다.
 */
public abstract class AggregateRoot<ID> {

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    public abstract ID getId();

    protected void registerEvent(DomainEvent event) {
        //자식 클래스(Reservation)만 도메인 이벤트를 등록할 수 있도록 protected로 설정
        domainEvents.add(event);
    }

    public List<DomainEvent> getDomainEvents() {
        //도메인 이벤트 리스트를 외부에서 수정할 수 없도록 unmodifiableList로 반환
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        //도메인 이벤트 리스트를 초기화하여, 인프라 계층에서 발행 후 이벤트가 중복되지 않도록 함
        domainEvents.clear();
    }
}
