package com.week06.team01_week06_project.domain;

import com.week06.team01_week06_project.dto.request.MemberReqDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Member extends Timestamped{

    @Id
    @Column(name = "memberid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false)
    private String userid;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String pw;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<GamePost> gamePost = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<RecruitStatus> recruitStatus = new ArrayList<>();

    public Member(MemberReqDto memberReqDto) {
        this.userid = memberReqDto.getUserid();
        this.name = memberReqDto.getName();
        this.pw = memberReqDto.getPw();
    }

    public boolean validatePassword(PasswordEncoder passwordEncoder, String pw) {
        return passwordEncoder.matches(pw, this.pw);
    }
}
