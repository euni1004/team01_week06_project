package com.week06.team01_week06_project.serevice;

import com.week06.team01_week06_project.domain.GamePost;
import com.week06.team01_week06_project.domain.Member;
import com.week06.team01_week06_project.domain.RecruitStatus;
import com.week06.team01_week06_project.dto.GlobalResDto;
import com.week06.team01_week06_project.dto.request.GamepostReqDto;
import com.week06.team01_week06_project.dto.request.PutGamepostReqDto;
import com.week06.team01_week06_project.dto.request.RecruitMemberDto;
import com.week06.team01_week06_project.dto.response.GamePostResDto;
import com.week06.team01_week06_project.respository.GamePostRepository;
import com.week06.team01_week06_project.respository.MemberRepository;
import com.week06.team01_week06_project.respository.RecruitStatusRepository;
import com.week06.team01_week06_project.s3.AmazonS3ResourceStorage;
import com.week06.team01_week06_project.s3.MultipartUtil;
import com.week06.team01_week06_project.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GamePostService {

    private final GamePostRepository gamePostRepository;
    private final MemberRepository memberRepository;
    private final RecruitStatusRepository recruitStatusRepository;
    private final AmazonS3ResourceStorage amazonS3ResourceStorage;


    @Transactional
    public GlobalResDto<?> generateGamePost(Long memberid, GamepostReqDto gamepostReqDto, MultipartFile multipartFile) {
        Member member = isPresentMember(memberid);
        if (member == null) {
            return GlobalResDto.fail("MEMBER_NOT_FOUND", "사용자가 존재하지 않습니다.");
        }

        String path = MultipartUtil.createPath(MultipartUtil.createFileId(), MultipartUtil.getFormat(multipartFile.getContentType()));

        int num = amazonS3ResourceStorage.store(path, multipartFile);

        if(num==0){
            path = "/images/normal_game.png";
        }

        GamePost gamePost = new GamePost(member, gamepostReqDto, path);
        gamePostRepository.save(gamePost);
        return GlobalResDto.success(null);
    }

    //모집글 수정하는 곳
    @Transactional
    public GlobalResDto<GamePostResDto> putGamePost(UserDetailsImpl userDetails, PutGamepostReqDto putGamepostReqDto, Long gamepostid) {

        GamePost gamePost = isPresentGamePost(gamepostid);
        if (gamePost == null) {
            return GlobalResDto.fail("GAMEPOST_NOT_FOUND", "게시물이 존재하지 않습니다.");
        }
        if (!userDetails.getAccount().getMemberId().equals(gamePost.getMember().getMemberId())) {
            return GlobalResDto.fail("NO_PERMISSION", "게시물은 자신이 작성한 게시물만 수정할 수 있습니다.");
        }
        gamePost.updatePost(putGamepostReqDto);

        return GlobalResDto.success(getGamePost(gamepostid).getData());

    }

    @Transactional
    public GlobalResDto<?> deleteGamePost(Long gamepostid, UserDetailsImpl userDetails) {

        GamePost gamePost = isPresentGamePost(gamepostid);
        if (gamePost == null) {
            return GlobalResDto.fail("GAMEPOST_NOT_FOUND", "게시물이 존재하지 않습니다.");
        }
        if (!userDetails.getAccount().getMemberId().equals(gamePost.getMember().getMemberId())) {
            return GlobalResDto.fail("NO_PERMISSION", "게시물은 자신이 작성한 게시물만 삭제할 수 있습니다.");
        }

        if(!gamePost.getPath().equals("/images/normal_game.png")){
            amazonS3ResourceStorage.delimg(gamePost.getPath());
        }

        recruitStatusRepository.deleteAllByGamePost(gamePost);
        gamePostRepository.deleteById(gamePost.getGamePostId());
        return GlobalResDto.success(null);
    }

    public GlobalResDto<List<GamePostResDto>> getAllGamePost() {
        List<GamePost> gamePosts = gamePostRepository.findAll();

        //원하는 dto로 바뀌기 위해 list
        List<GamePostResDto> gamePostResDtos = new ArrayList<>();

        for (GamePost gamePost : gamePosts) {
            String imgurl = amazonS3ResourceStorage.getimg(gamePost.getPath());
            if (gamePost.getRecruitStatus()) {
                GamePostResDto gamePostResDto = GamePostResDto.toGamePostResDto(gamePost,imgurl);
                gamePostResDtos.add(gamePostResDto);
            } else {
                List<String> inGameNickname = isPresentNickname(gamePost);
                inGameNickname.add(0, gamePost.getMyIngameNickname());
                GamePostResDto gamePostResDto = GamePostResDto.toDoneGamePostResDto(gamePost, inGameNickname,imgurl);
                gamePostResDtos.add(gamePostResDto);
            }
        }
        return GlobalResDto.success(gamePostResDtos);
    }

    public GlobalResDto<GamePostResDto> getGamePost(Long gamepostid) {
        GamePost gamePost = isPresentGamePost(gamepostid);
        if (gamePost == null) {
            return GlobalResDto.fail("GAMEPOST_NOT_FOUND", "게시물이 존재하지 않습니다.");
        }
        String imgurl = amazonS3ResourceStorage.getimg(gamePost.getPath());
        if (gamePost.getRecruitStatus()) {
            GamePostResDto gamePostResDto = GamePostResDto.toGamePostResDto(gamePost,imgurl);
            return GlobalResDto.success(gamePostResDto);
        } else {
            List<String> inGameNickname = isPresentNickname(gamePost);
            inGameNickname.add(0, gamePost.getMyIngameNickname());
            GamePostResDto gamePostResDto = GamePostResDto.toDoneGamePostResDto(gamePost, inGameNickname,imgurl);
            return GlobalResDto.success(gamePostResDto);
        }
    }


    public Member isPresentMember(Long memberId) {
        Optional<Member> member = memberRepository.findById(memberId);
        return member.orElse(null);
    }

    public GamePost isPresentGamePost(Long gamepostid) {
        Optional<GamePost> gamePost = gamePostRepository.findById(gamepostid);
        return gamePost.orElse(null);
    }

    public List<String> isPresentNickname(GamePost gamePost) {
        List<RecruitStatus> recruitStatuses = gamePost.getRecruitStatuses();
        List<String> inGamenickname = new ArrayList<>();
        assert recruitStatuses != null;
        for (RecruitStatus recruitStatus : recruitStatuses) {
            inGamenickname.add(recruitStatus.getInGameNickname());
        }
        return inGamenickname;
    }

    public GlobalResDto<?> searchPost(String searchKeyword) {
        if(searchKeyword == null) {
            return GlobalResDto.fail ( "DATA_NOT_FOUND", "글자를 입력해주세요" );
        }
        List<GamePost> findGamePosts = gamePostRepository.findAllByGameNameContaining ( searchKeyword );
        if(findGamePosts == null) {
            return GlobalResDto.fail ( "SEARCH_NOT_FOUND", "게시글을 찾을 수 없습니다." );
        }
        return GlobalResDto.success ( findGamePosts );
    }


//    public GlobalResDto<GamePost> checkValidation(Long gamepostid, UserDetailsImpl userDetails){
//        GamePost gamePost = isPresentGamePost(gamepostid);
//        if(gamePost==null){
//            return GlobalResDto.fail("GAMEPOST_NOT_FOUND","게시물이 존재하지 않습니다.");
//        }
//        if(!userDetails.getAccount().getMemberId().equals(gamePost.getMember().getMemberId())){
//            return GlobalResDto.fail("NO_PERMISSION", "게시물은 자신이 작성한 게시물만 삭제할 수 있습니다.");
//        }
//        return GlobalResDto.success(gamePost);
//    }

}
