package com.week06.team01_week06_project.serevice;

import com.week06.team01_week06_project.domain.Member;
import com.week06.team01_week06_project.domain.RefreshToken;
import com.week06.team01_week06_project.dto.GlobalResDto;
import com.week06.team01_week06_project.dto.request.LoginReqDto;
import com.week06.team01_week06_project.dto.request.MemberReqDto;
import com.week06.team01_week06_project.dto.response.LoginResDto;
import com.week06.team01_week06_project.jwt.JwtUtil;
import com.week06.team01_week06_project.jwt.TokenDto;
import com.week06.team01_week06_project.respository.MemberRepository;
import com.week06.team01_week06_project.respository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    public GlobalResDto<?> signup(MemberReqDto memberReqDto) {
        if(null!=isPresentMember(memberReqDto.getUserid())){
            return GlobalResDto.fail("DUPLICATED_NICKNAME", "이미 사용중인 닉네임입니다.");
        }

        memberReqDto.setEncodePwd(passwordEncoder.encode(memberReqDto.getPw()));
        Member member = new Member(memberReqDto);

        memberRepository.save(member);
        return GlobalResDto.success(null);
    }

    public GlobalResDto<?> login(LoginReqDto loginReqDto, HttpServletResponse response) {
        Member member = isPresentMember(loginReqDto.getUserid());
        if(null==member){
            return GlobalResDto.fail("MEMBER_NOT_FOUND", "사용자를 찾을 수 없습니다.");
        }
        if(!member.validatePassword(passwordEncoder,loginReqDto.getPw())){
            return GlobalResDto.fail("WRONG_PASSWORD", "비밀번호가 틀렸습니다.");
        }

        TokenDto tokenDto = jwtUtil.createAllToken(loginReqDto.getUserid());

        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByAccountuserid(loginReqDto.getUserid());

        if (refreshToken.isPresent()) {
            refreshTokenRepository.save(refreshToken.get().updateToken(tokenDto.getRefreshToken()));
        } else {
            RefreshToken newToken = new RefreshToken(tokenDto.getRefreshToken(), loginReqDto.getUserid());
            refreshTokenRepository.save(newToken);
        }

        setHeader(response,tokenDto);

        return GlobalResDto.success(new LoginResDto(member.getUserid(),member.getName()));
    }

    @Transactional(readOnly = true)
    public Member isPresentMember(String userid) {
        Optional<Member> member = memberRepository.findByUserid(userid);
        return member.orElse(null);
    }

    private void setHeader(HttpServletResponse response, TokenDto tokenDto) {
        response.addHeader(JwtUtil.ACCESS_TOKEN, tokenDto.getAccessToken());
        response.addHeader(JwtUtil.REFRESH_TOKEN, tokenDto.getRefreshToken());
    }


}
