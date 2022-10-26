package com.week06.team01_week06_project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MyGameList {

    private List<GamePostResDto> myGamePostList;
    private List<GamePostResDto> myRecruitList;
}
