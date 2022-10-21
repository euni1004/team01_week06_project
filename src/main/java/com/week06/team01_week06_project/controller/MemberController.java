package com.week06.team01_week06_project.controller;

import com.week06.team01_week06_project.jwt.JwtUtil;
import com.week06.team01_week06_project.serevice.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final JwtUtil jwtUtil;
    private final MemberService memberService;

//    @PostMapping("/signup")
//    public Res
}
