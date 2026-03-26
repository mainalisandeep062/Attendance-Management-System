package com.texas.developers.ams.exception;

public class UserAlreadyExistException  extends ApplicationException {


    private final Object data;

    public UserAlreadyExistException() {
        super("USER_ALREADY_EXISTS");
        this.data = null;
    }

    public UserAlreadyExistException(Object account) {
        super("USER_ALREADY_EXISTS");
        this.data = account;
    }

    public Object getData() {
        return data;
    }
}