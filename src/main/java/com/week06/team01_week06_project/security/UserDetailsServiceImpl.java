package com.week06.team01_week06_project.security;

import com.week06.team01_week06_project.domain.Member;
import com.week06.team01_week06_project.exception.CustomException;
import com.week06.team01_week06_project.exception.ErrorCode;
import com.week06.team01_week06_project.respository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String userid) throws UsernameNotFoundException {

        Member member = memberRepository.findByUserid(userid).orElseThrow(
                () -> new RuntimeException("NOT FOUNT ACCOUNT")
        );

//        Member member = memberRepository.findByUserid(userid).orElseThrow(
//                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
//        );
//
//        Member member = isPresentMember(userid);
//        if (member == null){
//            throw new CustomException(ErrorCode.NOT_FOUND_MEMBER);
//        }

        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setAccount(member);

        return userDetails;
    }

    @Transactional(readOnly = true)
    public Member isPresentMember(String userid) {
        Optional<Member> member = memberRepository.findByUserid(userid);
        return member.orElse(null);
    }
}
