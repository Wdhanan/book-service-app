package com.nounours.book.handler;

import lombok.Getter;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Getter
public enum BusinessErrorCodes {
    //all our business errors are setted up according to our constructor
    NO_CODE(0, HttpStatus.NOT_IMPLEMENTED, "No code"),
    INCORRECT_CURRENT_PASSWORD(300, BAD_REQUEST, "Current password is incorrect"),
    NEW_PASSWORD_DOES_NOT_MATCH(301, BAD_REQUEST, "The new password does not match"),
    ACCOUNT_LOCKED(302,FORBIDDEN, "User account is locked"),
    ACCOUNT_DISABLED(303,FORBIDDEN, "User account is disabled"),
    BAD_CREDENTIALS(302,FORBIDDEN, "Login and / or password is incorrect")
    ;

    private int code;
    private final String description;
    private final HttpStatus httpStatus;

    // constructor to say how the error code should look like
    BusinessErrorCodes(int code,HttpStatus httpStatus, String description) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }
}
