package com.nounours.book.handler;

import com.nounours.book.exception.OperationNotPermittedException;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;

@RestControllerAdvice // to mark this class as a global exception handler
public class GlobalExceptionHandler {

    @ExceptionHandler(LockedException.class)// LockedException is from spring
    public ResponseEntity<ExceptionResponse> handleException(LockedException exp){
        return  ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.ACCOUNT_LOCKED.getCode())
                        .businessErrorDescription(BusinessErrorCodes.ACCOUNT_LOCKED.getDescription())
                        .error(exp.getMessage())

                        .build());


    }

    @ExceptionHandler(DisabledException.class)// DisabledException is from spring
    public ResponseEntity<ExceptionResponse> handleException(DisabledException exp){
        return  ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.ACCOUNT_DISABLED.getCode())
                        .businessErrorDescription(BusinessErrorCodes.ACCOUNT_DISABLED.getDescription())
                        .error(exp.getMessage())

                        .build());


    }

    @ExceptionHandler(BadCredentialsException.class)// BadCredentialsException is from spring
    public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException exp){
        return  ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.BAD_CREDENTIALS.getCode())
                        .businessErrorDescription(BusinessErrorCodes.BAD_CREDENTIALS.getDescription())
                        .error(BusinessErrorCodes.BAD_CREDENTIALS.getDescription())

                        .build());


    }

    @ExceptionHandler(MessagingException.class)// MessagingException is from spring
    public ResponseEntity<ExceptionResponse> handleException(MessagingException exp){
        return  ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.builder()

                        .error(exp.getMessage())

                        .build());


    }

    // error for validation requestbody with @Valid annotation
    @ExceptionHandler(MethodArgumentNotValidException.class)// MethodArgumentNotValidException is from spring
    public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException exp){
        Set<String> errors = new HashSet<>(); // Sets do not allow duplicates
        exp.getBindingResult().getAllErrors()
                .forEach(error ->{
                    var errorMessage = error.getDefaultMessage();
                    errors.add(errorMessage);
                });
        return  ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .validationErrors(errors)

                        .build());


    }

    @ExceptionHandler(Exception.class)// Any other Type of Exceptions which are not handled from our Application
    public ResponseEntity<ExceptionResponse> handleException(Exception exp){

        //log the Exception
        exp.printStackTrace();// print in the console

        return  ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.builder()
                        .businessErrorDescription("Internal error, contact the admin")
                        .error(exp.getMessage())

                        .build());


    }

    @ExceptionHandler(OperationNotPermittedException.class)// OperationNotPermittedException is an Exception created in the package "Exception"
    public ResponseEntity<ExceptionResponse> handleException(OperationNotPermittedException exp){
        return  ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .error(exp.getMessage())

                        .build());


    }
}
