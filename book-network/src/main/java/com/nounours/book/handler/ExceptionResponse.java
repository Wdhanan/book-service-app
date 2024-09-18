package com.nounours.book.handler;

// a wrapper which handle the response if we have an exception

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY) // we just want to include non Empty attributes
public class ExceptionResponse {
    private Integer businessErrorCode;// we create an enum which contains all the possible business error that could happen
    private String businessErrorDescription;
    private String error;
    private Set<String> validationErrors;
    private Map<String, String> errors;
}
