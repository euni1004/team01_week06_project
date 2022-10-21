package com.week06.team01_week06_project.controller;

import com.week06.team01_week06_project.dto.GlobalResDto;
import com.week06.team01_week06_project.dto.request.GamepostReqDto;
import com.week06.team01_week06_project.dto.request.RecruitMemberDto;
import com.week06.team01_week06_project.security.UserDetailsImpl;
import com.week06.team01_week06_project.serevice.GamePostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class GamePostController {

    private final GamePostService gamePostService;

    //게임모집글 작성
    @PostMapping("/gamepost")
    public GlobalResDto<?> generateGamePost(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody GamepostReqDto gamepostReqDto) {
        Long memberid = userDetails.getAccount().getMemberId();
        return gamePostService.generateGamePost(memberid, gamepostReqDto);
    }

    //게임모집글 수정

    //게임모집글 삭제
    @DeleteMapping("/gamepost/{gamepostid}")
    public GlobalResDto<?> deleteGamePost(@PathVariable Long gamepostid, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return gamePostService.deleteGamePost(gamepostid, userDetails);
    }

    //모든 게임 모집글
    @GetMapping("/showpost")
    public GlobalResDto<?> getAllGamePost() {
        return gamePostService.getAllGamePost();
    }

    //모집글 1개
    @GetMapping("/showpost/{gamepostid}")
    public GlobalResDto<?> getGamePost(@PathVariable Long gamepostid) {
        return gamePostService.getGamePost(gamepostid);
    }

    //게임 신청
    @PutMapping("/gamepost/recruit/{gamepostid}")
    public GlobalResDto<?> participationGame(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                       @RequestBody RecruitMemberDto recruitMemberDto,
                                       @PathVariable Long gamepostid){
        return gamePostService.participationGame(userDetails, recruitMemberDto,gamepostid);
    }

    //참가신청 취소
    @DeleteMapping("/gamepost/recruit/{gamepostid}")
    public GlobalResDto<?> cancelParticipationGame(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                   @PathVariable Long gamepostid){
        return gamePostService.cancelParticipationGame(userDetails,gamepostid);
    }
}
