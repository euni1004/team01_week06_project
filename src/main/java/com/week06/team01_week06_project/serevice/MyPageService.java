package com.week06.team01_week06_project.serevice;

import com.week06.team01_week06_project.domain.GamePost;
import com.week06.team01_week06_project.domain.Member;
import com.week06.team01_week06_project.domain.RecruitStatus;
import com.week06.team01_week06_project.dto.GlobalResDto;
import com.week06.team01_week06_project.dto.response.GamePostResDto;
import com.week06.team01_week06_project.dto.response.MyGameList;
import com.week06.team01_week06_project.exception.Error;
import com.week06.team01_week06_project.exception.ErrorCode;
import com.week06.team01_week06_project.respository.RecruitStatusRepository;
import com.week06.team01_week06_project.s3.AmazonS3ResourceStorage;
import com.week06.team01_week06_project.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final AmazonS3ResourceStorage amazonS3ResourceStorage;
    private final GamePostService gamePostService;
    private final RecruitStatusRepository recruitStatusRepository;

    public GlobalResDto<?> getMyPost(UserDetailsImpl userDetails) {
        Member member = gamePostService.isPresentMember(userDetails.getAccount().getMemberId());
        if (member == null) {
            return GlobalResDto.fail(new Error(ErrorCode.NOT_FOUND_MEMBER));
        }

        List<GamePost> myGamePosts = member.getGamePost();
        List<GamePostResDto> myGamePostList = new ArrayList<>();
        for (GamePost gamePost : myGamePosts) {
            Long numberOfRecruited = recruitStatusRepository.countByGamePost(gamePost);
            String countTime = gamePostService.countDate(gamePost.getCreatedAt());
            String postTime = gamePost.getCreatedAt().format(DateTimeFormatter.ofPattern("M월 d일 h시 m분"));
            String imgurl = amazonS3ResourceStorage.getimg(gamePost.getPath());
            if (gamePost.getRecruitStatus()) {
                myGamePostList.add(0, GamePostResDto.toGamePostResDto(postTime, countTime, gamePost, numberOfRecruited + 1, imgurl));
            } else {
                List<String> inGameNickname = gamePostService.isPresentNickname(gamePost);
                inGameNickname.add(0, gamePost.getMyIngameNickname());
                myGamePostList.add(GamePostResDto.toDoneGamePostResDto(postTime, countTime, gamePost, inGameNickname, numberOfRecruited + 1, imgurl));
            }
        }

        List<GamePostResDto> gamePostResDtos = getMyRecruit(member);

        MyGameList myGameList = new MyGameList(myGamePostList, gamePostResDtos);

        return GlobalResDto.success(myGameList);
    }

    public List<GamePostResDto> getMyRecruit(Member member) {

        List<RecruitStatus> myRecruit = member.getRecruitStatus();
        List<GamePostResDto> myGameRecruitList = new ArrayList<>();
        for (RecruitStatus recruit : myRecruit) {
            Long numberOfRecruited = recruitStatusRepository.countByGamePost(recruit.getGamePost());
            String countTime = gamePostService.countDate(recruit.getGamePost().getCreatedAt());
            String postTime = recruit.getGamePost().getCreatedAt().format(DateTimeFormatter.ofPattern("M월 d일 h시 m분"));
            String imgurl = amazonS3ResourceStorage.getimg(recruit.getGamePost().getPath());
            if (recruit.getGamePost().getRecruitStatus()) {
                myGameRecruitList.add(0, GamePostResDto.toGamePostResDto(postTime, countTime, recruit.getGamePost(), numberOfRecruited + 1, imgurl));
            } else {
                List<String> inGameNickname = gamePostService.isPresentNickname(recruit.getGamePost());
                inGameNickname.add(0, recruit.getGamePost().getMyIngameNickname());
                myGameRecruitList.add(GamePostResDto.toDoneGamePostResDto(postTime, countTime, recruit.getGamePost(), inGameNickname, numberOfRecruited + 1, imgurl));
            }
        }
        return myGameRecruitList;
    }
}
