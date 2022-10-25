package com.week06.team01_week06_project.dto.response;

import com.week06.team01_week06_project.domain.GamePost;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GamePostResDto {

    private String postTime;
    private String countTime;
    private Long gamePostId;
    private String gameName;
    private String content;
    private List<String> inGameNickname;
    private int numberOfPeople;
    private boolean recruitStatus;
    private String imgUrl;

    public static GamePostResDto toDoneGamePostResDto(String postTime, String  countTime,GamePost gamePost, List<String> inGameNickname, String imgUrl) {
        return new GamePostResDto(
                postTime,
                countTime,
                gamePost.getGamePostId(),
                gamePost.getGameName(),
                gamePost.getContent(),
                inGameNickname,
                gamePost.getNumberOfPeople(),
                gamePost.getRecruitStatus(),
                imgUrl);
    }

    public static GamePostResDto toGamePostResDto(String postTime, String  countTime,GamePost gamePost, String imgUrl) {
        return new GamePostResDto(
                postTime,
                countTime,
                gamePost.getGamePostId(),
                gamePost.getGameName(),
                gamePost.getContent(),
                null,
                gamePost.getNumberOfPeople(),
                gamePost.getRecruitStatus(),
                imgUrl
        );
    }
}
