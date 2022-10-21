package com.week06.team01_week06_project.domain;

import com.week06.team01_week06_project.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class RecruitStatus {

    @Id
    @Column(name = "recruitstatusid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recruitStatusId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gamepostid")
    private GamePost gamePost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberid")
    private Member member;

    @Column(nullable = false)
    private String inGameNickname;

    public RecruitStatus(UserDetailsImpl userDetails, GamePost gamePost, String inGameNickname) {
        this.gamePost = gamePost;
        this.member = userDetails.getAccount();
        this.inGameNickname = inGameNickname;
    }

}
