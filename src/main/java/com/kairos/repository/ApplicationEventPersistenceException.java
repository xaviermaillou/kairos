package com.kairos.repository;

public class ApplicationEventPersistenceException extends RuntimeException {

    public ApplicationEventPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
