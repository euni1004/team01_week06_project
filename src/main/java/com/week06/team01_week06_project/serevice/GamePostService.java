package com.week06.team01_week06_project.serevice;

import com.week06.team01_week06_project.domain.GamePost;
import com.week06.team01_week06_project.domain.Member;
import com.week06.team01_week06_project.domain.RecruitStatus;
import com.week06.team01_week06_project.dto.GlobalResDto;
import com.week06.team01_week06_project.dto.request.GamepostReqDto;
import com.week06.team01_week06_project.dto.request.PutGamepostReqDto;
import com.week06.team01_week06_project.dto.response.GamePostResDto;
import com.week06.team01_week06_project.exception.CustomException;
import com.week06.team01_week06_project.exception.ErrorCode;
import com.week06.team01_week06_project.respository.GamePostRepository;
import com.week06.team01_week06_project.respository.MemberRepository;
import com.week06.team01_week06_project.respository.RecruitStatusRepository;
import com.week06.team01_week06_project.s3.AmazonS3ResourceStorage;
import com.week06.team01_week06_project.s3.MultipartUtil;
import com.week06.team01_week06_project.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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
    public GlobalResDto<?> generateGamePost(Long memberId, GamepostReqDto gamepostReqDto, MultipartFile multipartFile) {
        Member member = isPresentMember(memberId);

        if (member == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_MEMBER);
        }

        String path = MultipartUtil.createPath(MultipartUtil.createFileId(), MultipartUtil.getFormat(multipartFile.getContentType()));

        int num = amazonS3ResourceStorage.store(path, multipartFile);

        if (num == 0) {
            path = "images/normal.jpg";
        }

        GamePost gamePost = new GamePost(member, gamepostReqDto, path);
        gamePostRepository.save(gamePost);
        return GlobalResDto.success(null);
    }

    //모집글 수정하는 곳
    @Transactional
    public GlobalResDto<GamePostResDto> putGamePost(UserDetailsImpl userDetails, PutGamepostReqDto putGamepostReqDto, Long gamePostId) throws ParseException {

        GamePost gamePost = isPresentGamePost(gamePostId);
        if (gamePost == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_GAMEPOST);
        }
        if (!userDetails.getAccount().getMemberId().equals(gamePost.getMember().getMemberId())) {
            throw new CustomException(ErrorCode.NO_PERMISSION_CHANGE);
        }
        gamePost.updatePost(putGamepostReqDto);

        return GlobalResDto.success(getGamePost(gamePostId).getData());

    }

    @Transactional
    public GlobalResDto<?> deleteGamePost(Long gamePostId, UserDetailsImpl userDetails) {

        GamePost gamePost = isPresentGamePost(gamePostId);
        if (gamePost == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_GAMEPOST);
        }
        if (!userDetails.getAccount().getMemberId().equals(gamePost.getMember().getMemberId())) {
            throw new CustomException(ErrorCode.NO_PERMISSION_DELETE);
        }

        if (!gamePost.getPath().equals("images/normal.jpg")) {
            amazonS3ResourceStorage.delimg(gamePost.getPath());
        }

        recruitStatusRepository.deleteAllByGamePost(gamePost);
        gamePostRepository.deleteById(gamePost.getGamePostId());
        return GlobalResDto.success(null);
    }

    public GlobalResDto<List<GamePostResDto>> getAllGamePostTrue() throws ParseException {
        List<GamePost> gamePosts = gamePostRepository.findAllByRecruitStatus(true);

        //원하는 dto로 바뀌기 위해 list
        List<GamePostResDto> gamePostResDtos = new ArrayList<>();

        for (GamePost gamePost : gamePosts) {
            String countTime = countDate(gamePost.getCreatedAt());
            String postTime = gamePost.getCreatedAt().format(DateTimeFormatter.ofPattern("M월 d일 h시 m분"));
            String imgUrl = amazonS3ResourceStorage.getimg(gamePost.getPath());
            GamePostResDto gamePostResDto = GamePostResDto.toGamePostResDto(postTime, countTime, gamePost, imgUrl);
            gamePostResDtos.add(0, gamePostResDto);
        }
        return GlobalResDto.success(gamePostResDtos);
    }

    public GlobalResDto<List<GamePostResDto>> getAllGamePostFalse() throws ParseException {
        List<GamePost> gamePosts = gamePostRepository.findAllByRecruitStatus(false);

        //원하는 dto로 바뀌기 위해 list
        List<GamePostResDto> gamePostResDtos = new ArrayList<>();
        for (GamePost gamePost : gamePosts) {
            String countTime = countDate(gamePost.getCreatedAt());
            String postTime = gamePost.getCreatedAt().format(DateTimeFormatter.ofPattern("M월 d일 h시 m분"));
            String imgUrl = amazonS3ResourceStorage.getimg(gamePost.getPath());
            List<String> inGameNickname = isPresentNickname(gamePost);
            inGameNickname.add(0, gamePost.getMyIngameNickname());
            GamePostResDto gamePostResDto = GamePostResDto.toDoneGamePostResDto(postTime, countTime, gamePost, inGameNickname, imgUrl);
            gamePostResDtos.add(0, gamePostResDto);
        }
        return GlobalResDto.success(gamePostResDtos);
    }

    public GlobalResDto<GamePostResDto> getGamePost(Long gamePostId) throws ParseException {
        GamePost gamePost = isPresentGamePost(gamePostId);
        if (gamePost == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_GAMEPOST);
        }
        String countTime = countDate(gamePost.getCreatedAt());
        String postTime = gamePost.getCreatedAt().format(DateTimeFormatter.ofPattern("M월 d일 h시 m분"));
        String imgUrl = amazonS3ResourceStorage.getimg(gamePost.getPath());
        if (gamePost.getRecruitStatus()) {
            GamePostResDto gamePostResDto = GamePostResDto.toGamePostResDto(postTime, countTime, gamePost, imgUrl);
            return GlobalResDto.success(gamePostResDto);
        } else {
            List<String> inGameNickname = isPresentNickname(gamePost);
            inGameNickname.add(0, gamePost.getMyIngameNickname());
            GamePostResDto gamePostResDto = GamePostResDto.toDoneGamePostResDto(postTime, countTime, gamePost, inGameNickname, imgUrl);
            return GlobalResDto.success(gamePostResDto);
        }
    }

    public Member isPresentMember(Long memberId) {
        Optional<Member> member = memberRepository.findById(memberId);
        return member.orElse(null);
    }

    public GamePost isPresentGamePost(Long gamePostId) {
        Optional<GamePost> gamePost = gamePostRepository.findById(gamePostId);
        return gamePost.orElse(null);
    }

    public List<String> isPresentNickname(GamePost gamePost) {
        List<RecruitStatus> recruitStatuses = gamePost.getRecruitStatuses();
        List<String> inGameNickName = new ArrayList<>();
        assert recruitStatuses != null;
        for (RecruitStatus recruitStatus : recruitStatuses) {
            inGameNickName.add(recruitStatus.getInGameNickname());
        }
        return inGameNickName;
    }

    public String countDate(LocalDateTime localDateTime) throws ParseException {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYYMMddHHmm");
        String nowTime = simpleDateFormat.format(date);
        Date nowDate = simpleDateFormat.parse(nowTime);
        long now = nowDate.getTime();

        String postTime = localDateTime.format(DateTimeFormatter.ofPattern("YYYYMMddHHmm"));
        Date postDate = simpleDateFormat.parse(postTime);
        long post = postDate.getTime();

        long time = (now - post) / 60000;
        String countTime = "";

        if(time<=1){
            countTime = "1분 전";
        } else if (time < 10) {
            countTime = time+"분 전";
        }else if(time<60){
            countTime = (time/10)+"0분 전";
        }else if(time< 720){
            countTime = (time/60)+"시간 전";
        }else{
            countTime = localDateTime.format(DateTimeFormatter.ofPattern("MM월 dd일 HH시 mm분"));
        }

        return countTime;
    }

    public GlobalResDto<?> searchPost(String searchKeyword) {

        List<GamePost> gamePosts = gamePostRepository.findAllByGameNameContaining (searchKeyword);

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
