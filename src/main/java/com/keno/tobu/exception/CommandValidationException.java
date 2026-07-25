package com.keno.tobu.exception;

public class CommandValidationException extends RuntimeException {
    public CommandValidationException(String message) {
        super(message);
    }
}