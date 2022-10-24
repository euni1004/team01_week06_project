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
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final JwtUtil jwtUtil;
    private final MemberService memberService;

    @PostMapping("/idck")
    public GlobalResDto<?> idck(@RequestBody TestDto testDto){
        return memberService.idck(testDto);
    }

    //회원가입
    @PostMapping("/signup")
    public GlobalResDto<?> signup(@RequestBody @Valid MemberReqDto memberReqDto){
        return memberService.signup(memberReqDto);
    }

    //로그인
    @PostMapping("/login")
    public GlobalResDto<?> login(@RequestBody @Valid LoginReqDto loginReqDto, HttpServletResponse response){
        return memberService.login(loginReqDto,response);
    }



    //로그인한상태에서 탈퇴 가능 userid,pw 필요
    //게시물과 참가신청을 하지 않은사람만 탈퇴 가능
    @DeleteMapping("/byemember")
    public GlobalResDto<?> deleMember(@AuthenticationPrincipal UserDetailsImpl userDetails,@RequestBody TestDto testDto){
        return memberService.deleMember(userDetails,testDto);
    }
}
