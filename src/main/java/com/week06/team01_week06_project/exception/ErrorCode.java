package com.week06.team01_week06_project.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ErrorCode {

    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND.value(), "M001","해당 유저 id를 찾을 수 없습니다"),

    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND.value(), "M002","해당 댓글을 찾을 수 없습니다"),

    NOT_FOUND_COMMWNTUSER(HttpStatus.BAD_REQUEST.value(), "M003","해당 댓글 작성자가 아닙니다"),

    // Account
    OVERLAP_LOGINID(HttpStatus.NOT_FOUND.value(), "L001","이미 존재하는 아이디 입니다."),

    NOT_MATCHED_LOGINID(HttpStatus.NOT_FOUND.value(), "L002","아이디가 일치하지 않습니다."),

    NOT_MATCHED_PASSWORD(HttpStatus.NOT_FOUND.value(), "L003","비밀번호가 일치하지 않습니다."),


    // Heart
    NOT_FOUND_POST(HttpStatus.NOT_FOUND.value(), "H001","해당 게시글을 찾을 수 없습니다"),

    NOT_OVERLAP_HEART(HttpStatus.BAD_REQUEST.value(), "H002","좋아요는 한번만 가능합니다");

    private final int httpStatus;
    private final String code;
    private final String message;
}
