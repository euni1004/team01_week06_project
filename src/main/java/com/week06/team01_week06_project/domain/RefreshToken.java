package com.week06.team01_week06_project.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Getter
@Entity
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refreshid;
    @NotBlank
    private String refreshToken;
    @NotBlank
    private String accountNickname;

    public RefreshToken(String token, String nickname) {
        this.refreshToken = token;
        this.accountNickname = nickname;
    }

    public RefreshToken updateToken(String token) {
        this.refreshToken = token;
        return this;
    }

}
