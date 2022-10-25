package com.week06.team01_week06_project.controller;

import com.week06.team01_week06_project.domain.Member;
import com.week06.team01_week06_project.dto.GlobalResDto;
import com.week06.team01_week06_project.dto.response.GamePostResDto;
import com.week06.team01_week06_project.security.UserDetailsImpl;
import com.week06.team01_week06_project.serevice.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService myPageService;

    @GetMapping("/myPost")
    public GlobalResDto<?> getMyPost(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return myPageService.getMyPost(userDetails);
    }

    @GetMapping("/myRecruit")
    public GlobalResDto<?> getMyRecruit(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return myPageService.getMyRecruit(userDetails);
    }
}
