package com.texas.developers.ams.exception;

public class ApplicationException extends RuntimeException {
    private final Object[] args;

    public ApplicationException(String messageCode, Object... args) {
        super(messageCode);
        this.args = args;
    }

    public Object[] getArgs() {
        return args;
    }
}
