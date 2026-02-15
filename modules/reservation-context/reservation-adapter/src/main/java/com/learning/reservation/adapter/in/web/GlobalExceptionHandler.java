package com.learning.reservation.adapter.in.web;

import com.learning.reservation.domain.exception.AlreadyCancelledException;
import com.learning.reservation.domain.exception.CancellationNotAllowedException;
import com.learning.reservation.domain.exception.DoubleBookingException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DoubleBookingException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDoubleBooking(DoubleBookingException ex){
        return new ErrorResponse("DOUBLE_BOOKING",ex.getMessage());
    }
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgument(IllegalArgumentException ex){
        return new ErrorResponse("ILLEGAL_ARGUMENT",ex.getMessage());
    }
    @ExceptionHandler(AlreadyCancelledException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleAlreadyCancelled(AlreadyCancelledException ex){
        return new ErrorResponse("ALREADY_CANCELLED", ex.getMessage());
    }
    @ExceptionHandler(CancellationNotAllowedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCancelledNotAllowed(CancellationNotAllowedException ex){
        return new ErrorResponse("CANCELLED_NOT_ALLOWED", ex.getMessage());
    }
}
