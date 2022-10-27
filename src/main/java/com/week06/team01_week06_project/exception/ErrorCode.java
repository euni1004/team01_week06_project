package com.week06.team01_week06_project.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND.value(),"M001", "유저가 존재하지 않습니다."),
    DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST.value(),"M002","아이디를 사용하는 유저가 존재합니다."),
    WRONG_PASSWORD(HttpStatus.BAD_REQUEST.value(),"M003","비밀번호가 틀렸습니다."),

    NOT_FOUND_GAMEPOST(HttpStatus.NOT_FOUND.value(),"P001","게시물이 존재하지 않습니다."),
    NO_PERMISSION_CHANGE(HttpStatus.UNAUTHORIZED.value(), "P002","자신이 작성한 게시물만 수정가능합니다."),
    NO_PERMISSION_DELETE(HttpStatus.UNAUTHORIZED.value(), "P003","자신이 작성한 게시물만 삭제가능합니다."),
    NUMBER_OF_PEOPLE_ERROR(HttpStatus.BAD_REQUEST.value(),"P004","모집인원은 1명이상 100이하입니다."),

    RECRUITSTATUS_ALREADY(HttpStatus.BAD_REQUEST.value(), "R001","참가신청은 한번만 가능합니다."),
    RECRUITSTATUS_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "R002","참가신청한 이력이 없습니다."),
    NOT_RECRUIT_MYPOST(HttpStatus.BAD_REQUEST.value(), "R003","자신의 게시물에는 참가신청이 불가능 합니다."),

    NICKNAME_MUST_HAVE(HttpStatus.BAD_REQUEST.value(),"R001","닉네임은 반드시 입력해야합니다.");


    private final int status;
    private final String code;
    private final String message;
}
