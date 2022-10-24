package com.week06.team01_week06_project.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class Exception {

    private String errorMessage;
    private HttpStatus httpStatus;
}
