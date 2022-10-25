package com.week06.team01_week06_project.serevice;

import com.week06.team01_week06_project.domain.GamePost;
import com.week06.team01_week06_project.domain.Member;
import com.week06.team01_week06_project.domain.RecruitStatus;
import com.week06.team01_week06_project.dto.GlobalResDto;
import com.week06.team01_week06_project.dto.response.GamePostResDto;
import com.week06.team01_week06_project.exception.CustomException;
import com.week06.team01_week06_project.exception.ErrorCode;
import com.week06.team01_week06_project.respository.MemberRepository;
import com.week06.team01_week06_project.respository.RecruitStatusRepository;
import com.week06.team01_week06_project.s3.AmazonS3ResourceStorage;
import com.week06.team01_week06_project.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final MemberRepository memberRepository;
    private final AmazonS3ResourceStorage amazonS3ResourceStorage;
    private final GamePostService gamePostService;
    private final RecruitStatusRepository recruitStatusRepository;

    public GlobalResDto<List<GamePostResDto>> getMyPost(UserDetailsImpl userDetails) throws ParseException {
        Member member = memberRepository.findByUserid(userDetails.getAccount().getUserid()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );
        List<GamePost> myGamePosts = member.getGamePost();
        List<GamePostResDto> myGamePostList = new ArrayList<>();
        for(GamePost gamePost : myGamePosts){
            Long numberOfRecruited = recruitStatusRepository.countByGamePost(gamePost);
            String countTime = gamePostService.countDate(gamePost.getCreatedAt());
            String postTime = gamePost.getCreatedAt().format(DateTimeFormatter.ofPattern("M월 d일 h시 m분"));
            String imgurl = amazonS3ResourceStorage.getimg(gamePost.getPath());
            if (gamePost.getRecruitStatus()){
                myGamePostList.add(GamePostResDto.toGamePostResDto(postTime, countTime,gamePost,numberOfRecruited,imgurl));
            } else {
                List<String> inGameNickname = gamePostService.isPresentNickname(gamePost);
                inGameNickname.add(0, gamePost.getMyIngameNickname());
                myGamePostList.add(GamePostResDto.toDoneGamePostResDto(postTime, countTime,gamePost,inGameNickname,numberOfRecruited,imgurl));
            }
        }
        return GlobalResDto.success(myGamePostList);
    }

    public GlobalResDto<List<GamePostResDto>> getMyRecruit(UserDetailsImpl userDetails) throws ParseException {
        Member member = memberRepository.findByUserid(userDetails.getAccount().getUserid()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );
        List<RecruitStatus> myRecruit = member.getRecruitStatus();
        List<GamePostResDto> myGameRecruitList = new ArrayList<>();
        for(RecruitStatus recruit : myRecruit){
            Long numberOfRecruited = recruitStatusRepository.countByGamePost(recruit.getGamePost());
            String countTime = gamePostService.countDate(recruit.getGamePost().getCreatedAt());
            String postTime = recruit.getGamePost().getCreatedAt().format(DateTimeFormatter.ofPattern("M월 d일 h시 m분"));
            String imgurl = amazonS3ResourceStorage.getimg(recruit.getGamePost().getPath());
            if (recruit.getGamePost().getRecruitStatus()){
                myGameRecruitList.add(GamePostResDto.toGamePostResDto(postTime, countTime,recruit.getGamePost(),numberOfRecruited,imgurl));
            } else {
                List<String> inGameNickname = gamePostService.isPresentNickname(recruit.getGamePost());
                inGameNickname.add(0, recruit.getGamePost().getMyIngameNickname());
                myGameRecruitList.add(GamePostResDto.toDoneGamePostResDto(postTime, countTime,recruit.getGamePost(),inGameNickname,numberOfRecruited,imgurl));
            }
        }
        return GlobalResDto.success(myGameRecruitList);
    }
}
