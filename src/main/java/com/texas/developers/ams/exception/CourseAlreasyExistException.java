package com.texas.developers.ams.exception;

public class CourseAlreasyExistException extends ApplicationException {


    private final Object data;

    public CourseAlreasyExistException() {
        super("NOT_FOUND");
        this.data = null;
    }

    public CourseAlreasyExistException(Object account) {
        super("NOT_FOUND");
        this.data = account;
    }

    public Object getData() {
        return data;
    }
}
