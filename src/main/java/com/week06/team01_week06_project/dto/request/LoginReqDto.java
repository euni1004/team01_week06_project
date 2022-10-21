package com.week06.team01_week06_project.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class LoginReqDto {

    @NotBlank
    private String userid;
    @NotBlank
    private String pw;

    public LoginReqDto(String userid, String pw) {
        this.userid = userid;
        this.pw = pw;
    }

}