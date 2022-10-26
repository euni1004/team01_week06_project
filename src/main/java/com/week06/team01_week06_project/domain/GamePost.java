package com.week06.team01_week06_project.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.week06.team01_week06_project.dto.request.GamepostReqDto;
import com.week06.team01_week06_project.dto.request.PutGamepostReqDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class GamePost extends Timestamped{

    @Id
    @Column(name = "gamepostid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gamePostId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberid")
    @JsonIgnore
    private Member member;

    @Column(nullable = false)
    private String gameName;

    private String content;

    @OneToMany(mappedBy = "gamePost", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RecruitStatus> recruitStatuses;

    @Column(nullable = false)
    private String myIngameNickname;

    @Column(nullable = false)
    private long numberOfPeople;

    @Column(nullable = false)
    private boolean recruitStatus;


    private String path;

    public GamePost(Member member, GamepostReqDto gamepostReqDto,String path) {
        this.member = member;
        this.gameName = gamepostReqDto.getGamename();
        this.content = gamepostReqDto.getContent();
        this.myIngameNickname = gamepostReqDto.getInGameNickname();
        this.numberOfPeople = gamepostReqDto.getNumberOfPeople();
        this.recruitStatus = true;
        this.path = path;
    }

    public void updatePost(PutGamepostReqDto putGamepostReqDto){
        this.content = putGamepostReqDto.getContent();
        this.numberOfPeople = putGamepostReqDto.getNumberOfPeople();
    }

    public boolean getRecruitStatus() {
        return this.recruitStatus;
    }

    public void updateRecruitStatus(boolean recruitStatus) {
        this.recruitStatus = recruitStatus;
    }

}
