package com.nounours.book.exception;

public class OperationNotPermittedException extends RuntimeException { //need to handle the exception in the handler package(class "GlobalExceptionHandler")
    public OperationNotPermittedException(String msg) {
        super(msg);
    }
}
