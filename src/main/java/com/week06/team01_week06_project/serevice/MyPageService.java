package com.week06.team01_week06_project.serevice;

import com.week06.team01_week06_project.domain.GamePost;
import com.week06.team01_week06_project.domain.Member;
import com.week06.team01_week06_project.domain.RecruitStatus;
import com.week06.team01_week06_project.dto.GlobalResDto;
import com.week06.team01_week06_project.dto.response.GamePostResDto;
import com.week06.team01_week06_project.exception.CustomException;
import com.week06.team01_week06_project.exception.ErrorCode;
import com.week06.team01_week06_project.respository.MemberRepository;
import com.week06.team01_week06_project.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final MemberRepository memberRepository;

    public GlobalResDto<List<GamePostResDto>> getMyPost(UserDetailsImpl userDetails){
        Member member = memberRepository.findByUserid(userDetails.getAccount().getUserid()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );
        List<GamePost> myGamePosts = member.getGamePost();
        List<GamePostResDto> myGamePostList = new ArrayList<>();
        for(GamePost gamePost : myGamePosts){
            myGamePostList.add(GamePostResDto.myPost(gamePost));
        }
        return GlobalResDto.success(myGamePostList);
    }

    public GlobalResDto<List<GamePostResDto>> getMyRecruit(UserDetailsImpl userDetails) {
        Member member = memberRepository.findByUserid(userDetails.getAccount().getUserid()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );
        List<RecruitStatus> myRecruit = member.getRecruitStatus();
        List<GamePostResDto> myGameRecruitList = new ArrayList<>();
        for(RecruitStatus gamePost : myRecruit){
            myGameRecruitList.add(GamePostResDto.myPost(gamePost.getGamePost()));
        }
        return GlobalResDto.success(myGameRecruitList);
    }
}
