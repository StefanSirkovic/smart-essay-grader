package com.stefan.essaygraderai.exception;

public class EssayNotFoundException extends RuntimeException {
    public EssayNotFoundException(String message) {
        super(message);
    }
}
