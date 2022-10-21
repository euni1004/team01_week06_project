package com.week06.team01_week06_project.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberReqDto {

    private String userid;
    private String name;
    private String pw;

    public void setEncodePwd(String encodePwd) {
        this.pw = encodePwd;
    }
}
