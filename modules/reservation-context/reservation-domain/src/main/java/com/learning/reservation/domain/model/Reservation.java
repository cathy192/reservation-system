package com.learning.reservation.domain.model;

import com.learning.reservation.domain.event.ReservationCancelled;
import com.learning.reservation.domain.event.ReservationCreated;
import com.learning.reservation.domain.exception.AlreadyCancelledException;
import com.learning.reservation.domain.exception.CancellationNotAllowedException;
import com.learning.shared.kernel.domain.AggregateRoot;

import java.time.LocalDateTime;

public class Reservation extends AggregateRoot<ReservationId> {

    private final ReservationId id;
    private final MemberId memberId;
    private final ResourceId resourceId;
    private final TimeSlot timeSlot;
    private ReservationStatus status;

    private Reservation(ReservationId id, MemberId memberId, ResourceId resourceId, TimeSlot timeSlot) {
        this.id = id;
        this.memberId = memberId;
        this.resourceId = resourceId;
        this.timeSlot = timeSlot;
        this.status = ReservationStatus.PENDING;
    }

    /** 팩토리 메서드: 예약 생성 */
    public static Reservation create(MemberId memberId, ResourceId resourceId, TimeSlot timeSlot) {
        ReservationId id = ReservationId.generate();
        Reservation reservation = new Reservation(id, memberId, resourceId, timeSlot);
        reservation.registerEvent(new ReservationCreated(id, memberId, resourceId, timeSlot));
        return reservation;
    }

    /** 영속성 복원용 팩토리 (이벤트 발행 없음) */
    public static Reservation reconstitute(
            ReservationId id, MemberId memberId, ResourceId resourceId,
            TimeSlot timeSlot, ReservationStatus status) {
        Reservation reservation = new Reservation(id, memberId, resourceId, timeSlot);
        reservation.status = status;
        return reservation;
    }

    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
    }

    public void cancel(LocalDateTime now) {
        if (this.status == ReservationStatus.CANCELLED) {
            throw new AlreadyCancelledException();
        }
        if (now.plusHours(1).isAfter(timeSlot.startTime())) {
            throw new CancellationNotAllowedException();
        }
        this.status = ReservationStatus.CANCELLED;
        registerEvent(new ReservationCancelled(this.id));
    }

    @Override
    public ReservationId getId() {
        return id;
    }

    public MemberId getMemberId() {
        return memberId;
    }

    public ResourceId getResourceId() {
        return resourceId;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public ReservationStatus getStatus() {
        return status;
    }
}
