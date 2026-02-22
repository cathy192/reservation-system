package com.learning.reservation.domain.event;

import com.learning.shared.kernel.domain.DomainEvent;

public interface DomainEventPublisher{
    //도메인 이벤트 발행 인터페이스. 
    // 인프라 계층에서 구현하여, 도메인 이벤트를 메시지 브로커나 이벤트 버스에 전달하는 역할을 한다.
    public void publish(DomainEvent event);/// 단일 이벤트 발행 메서드
    public void publishAll(Iterable<? extends DomainEvent> events);// 여러 이벤트를 한 번에 발행하는 메서드
}


