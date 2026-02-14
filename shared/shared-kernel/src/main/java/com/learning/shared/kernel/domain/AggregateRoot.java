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
        domainEvents.add(event);
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }
}
