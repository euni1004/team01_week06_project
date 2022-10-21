package com.week06.team01_week06_project.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GamepostReqDto {

    private String gameName;
    private String content;
    private String inGameNickname;
    private int numberOfPeople;
}
