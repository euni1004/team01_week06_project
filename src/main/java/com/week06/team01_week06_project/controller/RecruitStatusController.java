package com.week06.team01_week06_project.controller;

import com.week06.team01_week06_project.dto.GlobalResDto;
import com.week06.team01_week06_project.dto.request.RecruitMemberDto;
import com.week06.team01_week06_project.security.UserDetailsImpl;
import com.week06.team01_week06_project.serevice.RecruitStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gamePost/recruit")
public class RecruitStatusController {

    private final RecruitStatusService recruitStatusService;

    //게임 신청
    @PutMapping("/{gamePostId}")
    public GlobalResDto<?> participationGame(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @RequestBody RecruitMemberDto recruitMemberDto,
                                             @PathVariable Long gamePostId) {
        return recruitStatusService.participationGame(userDetails, recruitMemberDto, gamePostId);
    }

    //참가신청 취소
    @DeleteMapping("/{gamePostId}")
    public GlobalResDto<?> cancelParticipationGame(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                   @PathVariable Long gamePostId) {
        return recruitStatusService.cancelParticipationGame(userDetails, gamePostId);
    }
}
