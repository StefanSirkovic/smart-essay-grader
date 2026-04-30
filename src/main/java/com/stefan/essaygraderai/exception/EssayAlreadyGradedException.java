package com.stefan.essaygraderai.exception;

public class EssayAlreadyGradedException extends RuntimeException {
    public EssayAlreadyGradedException(String message) {
        super(message);
    }
}
