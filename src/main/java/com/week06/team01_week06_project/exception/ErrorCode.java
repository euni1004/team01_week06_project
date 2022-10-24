package com.week06.team01_week06_project.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND.value(),"M001", "사용자가 존재하지 않습니다.");
//    DUPLICATED_NICKNAME(Http)

    private final int httpStatus;
    private final String code;
    private final String message;
}
