package com.week06.team01_week06_project.controller;

import com.google.gson.Gson;
import com.week06.team01_week06_project.dto.GlobalResDto;
import com.week06.team01_week06_project.dto.request.GamepostReqDto;
import com.week06.team01_week06_project.dto.request.PutGamepostReqDto;
import com.week06.team01_week06_project.security.UserDetailsImpl;
import com.week06.team01_week06_project.serevice.GamePostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
public class GamePostController {

    private final GamePostService gamePostService;

    //게임모집글 작성
    @PostMapping(value = "/gamepost", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public GlobalResDto<?> generateGamePost(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @RequestParam(required = false) String content,
                                            @RequestPart MultipartFile multipartFile) {

        Long memberid = userDetails.getAccount().getMemberId();

        Gson gson = new Gson();
        GamepostReqDto gamepostReqDto = gson.fromJson(content,GamepostReqDto.class);

        return gamePostService.generateGamePost(memberid, gamepostReqDto, multipartFile);
    }


    //게임모집글 수정
    @PutMapping("/gamepost/{gamepostid}")
    public GlobalResDto<?> putGamePost(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                       @RequestBody PutGamepostReqDto putGamepostReqDto,
                                       @PathVariable Long gamepostid)   {
        return gamePostService.putGamePost(userDetails, putGamepostReqDto, gamepostid);
    }

    //게임모집글 삭제
    @DeleteMapping("/gamepost/{gamepostid}")
    public GlobalResDto<?> deleteGamePost(@PathVariable Long gamepostid, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return gamePostService.deleteGamePost(gamepostid, userDetails);
    }

    //모든 게임 모집글
    @GetMapping("/showpost/recruittrue")
    public GlobalResDto<?> getAllGamePostTrue()   {
        return gamePostService.getAllGamePostTrue();
    }

    @GetMapping("/showpost/recruitfalse")
    public GlobalResDto<?> getAllGamePostFalse()   {
        return gamePostService.getAllGamePostFalse();
    }

    //모집글 1개
    @GetMapping("/showpost/{gamepostid}")
    public GlobalResDto<?> getGamePost(@PathVariable Long gamepostid)   {
        return gamePostService.getGamePost(gamepostid);
    }

    @GetMapping("/showpost/search")
    public GlobalResDto<?> searchPost(@RequestParam(name = "gameName") String gameName)   {
        return gamePostService.searchPost(gameName);
    }

}

