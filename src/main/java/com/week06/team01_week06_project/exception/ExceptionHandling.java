package com.week06.team01_week06_project.exception;

import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ExceptionHandling {

    @ExceptionHandler(value = { CustomException.class })
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(value = {SizeLimitExceededException.class})
    public Object sizeErrorException(SizeLimitExceededException e){
        long errorMessage = e.getActualSize()%1000;
        return new Error(400,"F001","파일사이즈의 최대사이즈는 256KB입니다. 현재 파일 크기는 "+errorMessage+"KB입니다.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();

        return ResponseEntity.badRequest().body(errorMessage);
    }
}
