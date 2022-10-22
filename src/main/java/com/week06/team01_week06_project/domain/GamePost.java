package com.week06.team01_week06_project.domain;

import com.week06.team01_week06_project.dto.request.GamepostReqDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class GamePost {

    @Id
    @Column(name = "gamepostid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gamePostId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberid")
    private Member member;

    @Column(nullable = false)
    private String gameName;

    private String content;

    @OneToMany(mappedBy = "gamePost", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RecruitStatus> recruitStatuses;

    @Column(nullable = false)
    private String myIngameNickname;

    @Column(nullable = false)
    private int numberOfPeople;

    @Column(nullable = false)
    private boolean recruitStatus;

    public GamePost(Member member, GamepostReqDto gamepostReqDto) {
        this.member = member;
        this.gameName = gamepostReqDto.getGameName();
        this.content = gamepostReqDto.getContent();
        this.myIngameNickname = gamepostReqDto.getInGameNickname();
        this.numberOfPeople = gamepostReqDto.getNumberOfPeople();
        this.recruitStatus = true;
    }

    public boolean getRecruitStatus() {
        return this.recruitStatus;
    }

    public void updateRecruitStatus(boolean recruitStatus) {
        this.recruitStatus = recruitStatus;
    }

}
