package com.week06.team01_week06_project.dto.response;

import com.week06.team01_week06_project.domain.GamePost;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GamePostResDto {

    private String gameName;
    private String content;
    private List<String> inGameNickname;
    private int numberOfPeople;
    private boolean recruitStatus;

    public static GamePostResDto toDoneGamePostResDto(GamePost gamePost, List<String> inGameNickname) {
        return new GamePostResDto(
                gamePost.getGameName(),
                gamePost.getContent(),
                inGameNickname,
                gamePost.getNumberOfPeople(),
                gamePost.getRecruitStatus());
    }

    public static GamePostResDto toGamePostResDto(GamePost gamePost) {
        return new GamePostResDto(
                gamePost.getGameName(),
                gamePost.getContent(),
                null,
                gamePost.getNumberOfPeople(),
                gamePost.getRecruitStatus()
        );
    }
}
