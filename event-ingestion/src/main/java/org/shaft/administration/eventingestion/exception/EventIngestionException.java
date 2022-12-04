package org.shaft.administration.eventingestion.exception;

public class EventIngestionException extends RuntimeException {

    public EventIngestionException() {
        super();
    }

    public EventIngestionException(String message) {
        super(message);
    }

    public EventIngestionException(String message, Throwable cause) {
        super(message, cause);
    }
}
