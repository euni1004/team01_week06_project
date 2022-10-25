package com.week06.team01_week06_project.serevice;

import com.week06.team01_week06_project.domain.GamePost;
import com.week06.team01_week06_project.domain.Member;
import com.week06.team01_week06_project.domain.RecruitStatus;
import com.week06.team01_week06_project.dto.GlobalResDto;
import com.week06.team01_week06_project.dto.response.GamePostResDto;
import com.week06.team01_week06_project.exception.CustomException;
import com.week06.team01_week06_project.exception.ErrorCode;
import com.week06.team01_week06_project.respository.MemberRepository;
import com.week06.team01_week06_project.s3.AmazonS3ResourceStorage;
import com.week06.team01_week06_project.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final MemberRepository memberRepository;
    private final AmazonS3ResourceStorage amazonS3ResourceStorage;
    private final GamePostService gamePostService;

    public GlobalResDto<List<GamePostResDto>> getMyPost(UserDetailsImpl userDetails){
        Member member = memberRepository.findByUserid(userDetails.getAccount().getUserid()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );
        List<GamePost> myGamePosts = member.getGamePost();
        List<GamePostResDto> myGamePostList = new ArrayList<>();
        for(GamePost gamePost : myGamePosts){
            String imgurl = amazonS3ResourceStorage.getimg(gamePost.getPath());
            if (gamePost.getRecruitStatus()){
                myGamePostList.add(GamePostResDto.toGamePostResDto(gamePost,imgurl));
            } else {
                List<String> inGameNickname = gamePostService.isPresentNickname(gamePost);
                inGameNickname.add(0, gamePost.getMyIngameNickname());
                myGamePostList.add(GamePostResDto.toDoneGamePostResDto(gamePost,inGameNickname,imgurl));
            }
        }
        return GlobalResDto.success(myGamePostList);
    }

    public GlobalResDto<List<GamePostResDto>> getMyRecruit(UserDetailsImpl userDetails) {
        Member member = memberRepository.findByUserid(userDetails.getAccount().getUserid()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );
        List<RecruitStatus> myRecruit = member.getRecruitStatus();
        List<GamePostResDto> myGameRecruitList = new ArrayList<>();
        for(RecruitStatus recruit : myRecruit){
            String imgurl = amazonS3ResourceStorage.getimg(recruit.getGamePost().getPath());
            if (recruit.getGamePost().getRecruitStatus()){
                myGameRecruitList.add(GamePostResDto.toGamePostResDto(recruit.getGamePost(),imgurl));
            } else {
                List<String> inGameNickname = gamePostService.isPresentNickname(recruit.getGamePost());
                inGameNickname.add(0, recruit.getGamePost().getMyIngameNickname());
                myGameRecruitList.add(GamePostResDto.toDoneGamePostResDto(recruit.getGamePost(),inGameNickname,imgurl));
            }
        }
        return GlobalResDto.success(myGameRecruitList);
    }
}