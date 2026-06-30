package com.texas.developers.ams.exception;

public class CourseAlreasyExistException extends ApplicationException {


    private final Object data;

    public CourseAlreasyExistException() {
        super("COURSE_ALREADY_EXISTS");
        this.data = null;
    }

    public CourseAlreasyExistException(Object account) {
        super("COURSE_ALREADY_EXISTS");
        this.data = account;
    }

    public Object getData() {
        return data;
    }
}
