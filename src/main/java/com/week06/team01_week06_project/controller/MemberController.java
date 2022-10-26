package com.week06.team01_week06_project.controller;

import com.week06.team01_week06_project.dto.GlobalResDto;
import com.week06.team01_week06_project.dto.request.LoginReqDto;
import com.week06.team01_week06_project.dto.request.MemberReqDto;
import com.week06.team01_week06_project.dto.request.TestDto;
import com.week06.team01_week06_project.jwt.JwtUtil;
import com.week06.team01_week06_project.security.UserDetailsImpl;
import com.week06.team01_week06_project.serevice.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final JwtUtil jwtUtil;
    private final MemberService memberService;

    @PostMapping("/member/idck")
    public GlobalResDto<?> idck(@RequestBody TestDto testDto){
        return memberService.idck(testDto);
    }

    //회원가입
    @PostMapping("/member/signup")
    public GlobalResDto<?> signup(@RequestBody @Valid MemberReqDto memberReqDto){
        return memberService.signup(memberReqDto);
    }

    //로그인
    @PostMapping("/member/login")
    public GlobalResDto<?> login(@RequestBody @Valid LoginReqDto loginReqDto, HttpServletResponse response){
        return memberService.login(loginReqDto,response);
    }

    //게임 모집하는 사이트 특성상 유저 탈퇴시 모든 모집글 삭제
    @DeleteMapping("/byemember")
    public GlobalResDto<?> deleMember(@AuthenticationPrincipal UserDetailsImpl userDetails,@RequestBody LoginReqDto loginReqDto){
        return memberService.deleMember(userDetails,loginReqDto);
    }
}
