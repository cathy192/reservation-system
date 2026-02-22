package  com.learning.reservation.adapter.in.event;

import com.learning.reservation.domain.event.ReservationCancelled;
import com.learning.reservation.domain.event.ReservationCreated;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ReservationEventListener {
    @EventListener
    public void handleReservationCreated(ReservationCreated event){
        //예약 생성 이벤트 처리 로직 구현
        //TODO: 예약 생성 시 알림 발송, 통계 업데이트 등
        System.out.println("예약이 생성되었습니다: " + event.reservationId());
    }
    @EventListener
    public void handleReservationCancelled(ReservationCancelled event){
        //예약 취소 이벤트 처리 로직 구현

        //TODO: 예약 취소 시 알림 발송, 통계 업데이트 등
        
        System.out.println("예약이 취소되었습니다: " + event.reservationId());
    }
}
