package com.learning.reservation.adapter.out.event;
import com.learning.reservation.domain.event.DomainEventPublisher;
import com.learning.shared.kernel.domain.DomainEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

//DomainEventPublisher 인터페이스의 Spring Framework 기반 구현체.
@Component
public class SpringDomainEventPublisher implements DomainEventPublisher {
    
    // Spring의 ApplicationEventPublisher를 활용하여 도메인 이벤트를 애플리케이션 내에서 발행하는 역할을 한다.
    //  DomainEventPublisher → SpringDomainEventPublisher → ApplicationEventPublisher
    // (인터페이스)            (번역기)                     (실제 발행 엔진)
    private final ApplicationEventPublisher applicationEventPublisher;
    
    public SpringDomainEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
       // Spring의 ApplicationEventPublisher를 주입받아, 도메인 이벤트 발행에 활용
       // ApplicationEventPublisher는 Spring의 이벤트 발행 메커니즘을 활용하여, 도메인 이벤트를 애플리케이션 내에서 전달하는 역할을 한다.
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(DomainEvent event) {
        // Spring의 ApplicationEventPublisher를 사용하여 이벤트 발행 로직 구현
        // 예: applicationEventPublisher.publishEvent(event);
        applicationEventPublisher.publishEvent(event);

    }

    @Override
    public void publishAll(Iterable<? extends DomainEvent> events) {
        // 여러 이벤트를 한 번에 발행하는 로직 구현
        // 예: events.forEach(this::publish);
        events.forEach(this::publish);
    }

}
