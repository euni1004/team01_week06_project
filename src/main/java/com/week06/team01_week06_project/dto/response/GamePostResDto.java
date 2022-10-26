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
    private long numberOfPeople;
    private Long numberOdRecruited;
    private boolean recruitStatus;
    private String imgUrl;

    public static GamePostResDto toDoneGamePostResDto(String postTime, String  countTime,GamePost gamePost, List<String> inGameNickname,Long  numberOdRecruited,String imgUrl) {
        return new GamePostResDto(
                postTime,
                countTime,
                gamePost.getGamePostId(),
                gamePost.getGameName(),
                gamePost.getContent(),
                inGameNickname,
                gamePost.getNumberOfPeople(),
                numberOdRecruited,
                gamePost.getRecruitStatus(),
                imgUrl);
    }

    public static GamePostResDto toGamePostResDto(String postTime, String  countTime,GamePost gamePost,Long  numberOdRecruited, String imgUrl) {
        return new GamePostResDto(
                postTime,
                countTime,
                gamePost.getGamePostId(),
                gamePost.getGameName(),
                gamePost.getContent(),
                null,
                gamePost.getNumberOfPeople(),
                numberOdRecruited,
                gamePost.getRecruitStatus(),
                imgUrl
        );
    }
}
