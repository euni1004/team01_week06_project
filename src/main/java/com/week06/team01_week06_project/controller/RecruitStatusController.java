package com.week06.team01_week06_project.controller;

import com.week06.team01_week06_project.dto.GlobalResDto;
import com.week06.team01_week06_project.dto.request.RecruitMemberDto;
import com.week06.team01_week06_project.security.UserDetailsImpl;
import com.week06.team01_week06_project.serevice.RecruitStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gamepost/recruit")
public class RecruitStatusController {

    private final RecruitStatusService recruitStatusService;

    //게임 신청
    @PatchMapping("/{postId}")
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    public GlobalResDto<?> participationGame(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @RequestBody(required = false) RecruitMemberDto recruitMemberDto,
                                             @PathVariable Long postId) {
//        System.out.println(recruitMemberDto);
//        System.out.println(postId);
//        System.out.println(recruitMemberDto.getInGameNickname());
        return recruitStatusService.participationGame(userDetails, recruitMemberDto, postId);
    }

    //참가신청 취소
    @DeleteMapping("/{gamePostId}")
    public GlobalResDto<?> cancelParticipationGame(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                   @PathVariable Long gamePostId) {
        return recruitStatusService.cancelParticipationGame(userDetails, gamePostId);
    }
}
