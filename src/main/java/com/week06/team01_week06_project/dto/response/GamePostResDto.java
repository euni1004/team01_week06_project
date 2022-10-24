package com.week06.team01_week06_project.dto.response;

import com.week06.team01_week06_project.domain.GamePost;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GamePostResDto {

    private Long gamePostId;
    private String gameName;
    private String content;
    private List<String> inGameNickname;
    private int numberOfPeople;
    private boolean recruitStatus;
    private String imgurl;

    public static GamePostResDto myPost(GamePost gamePost) {
        return new GamePostResDto(
                gamePost.getGamePostId(),
                gamePost.getGameName(),
                gamePost.getContent(),
                null,
                gamePost.getNumberOfPeople(),
                gamePost.getRecruitStatus(),
                gamePost.getPath()
                );
    }

    public static GamePostResDto toDoneGamePostResDto(GamePost gamePost, List<String> inGameNickname,String imgurl) {
        return new GamePostResDto(
                gamePost.getGamePostId(),
                gamePost.getGameName(),
                gamePost.getContent(),
                inGameNickname,
                gamePost.getNumberOfPeople(),
                gamePost.getRecruitStatus(),
                imgurl);
    }

    public static GamePostResDto toGamePostResDto(GamePost gamePost, String imgurl) {
        return new GamePostResDto(
                gamePost.getGamePostId(),
                gamePost.getGameName(),
                gamePost.getContent(),
                null,
                gamePost.getNumberOfPeople(),
                gamePost.getRecruitStatus(),
                imgurl
        );
    }
}
