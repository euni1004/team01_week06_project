package com.week06.team01_week06_project.serevice;

import com.week06.team01_week06_project.domain.GamePost;
import com.week06.team01_week06_project.domain.Member;
import com.week06.team01_week06_project.domain.RecruitStatus;
import com.week06.team01_week06_project.dto.GlobalResDto;
import com.week06.team01_week06_project.dto.request.GamepostReqDto;
import com.week06.team01_week06_project.dto.request.PutGamepostReqDto;
import com.week06.team01_week06_project.dto.response.GamePostResDto;
import com.week06.team01_week06_project.exception.Error;
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

import java.time.*;
import java.time.format.DateTimeFormatter;
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
    public GlobalResDto<?> generateGamePost(Long memberId, GamepostReqDto gamepostReqDto, MultipartFile multipartFile) {
        System.out.println(gamepostReqDto.getInGameNickname());
        if (gamepostReqDto.getInGameNickname().isEmpty()) {
            return GlobalResDto.fail(new Error(ErrorCode.NICKNAME_MUST_HAVE));
        }
        Member member = isPresentMember(memberId);

        if (member == null) {
            return GlobalResDto.fail(new Error(ErrorCode.NOT_FOUND_MEMBER));
        }

        if (gamepostReqDto.getNumberOfPeople() < 2 || gamepostReqDto.getNumberOfPeople() > 100) {
            return GlobalResDto.fail(new Error(ErrorCode.NUMBER_OF_PEOPLE_ERROR));
        }

        String path;

        if (multipartFile == null) {
            path = "images/normal.jpg";
        } else {
            path = MultipartUtil.createPath(MultipartUtil.createFileId(), MultipartUtil.getFormat(multipartFile.getContentType()));
            amazonS3ResourceStorage.store(path, multipartFile);
        }

        GamePost gamePost = new GamePost(member, gamepostReqDto, path);
        gamePostRepository.save(gamePost);
        return GlobalResDto.success(null);
    }

    //모집글 수정하는 곳
    @Transactional
    public GlobalResDto<?> putGamePost(UserDetailsImpl userDetails, PutGamepostReqDto putGamepostReqDto, Long gamePostId) {

        GamePost gamePost = isPresentGamePost(gamePostId);
        if (gamePost == null) {
            return GlobalResDto.fail(new Error(ErrorCode.NOT_FOUND_MEMBER));
        }
        if (!userDetails.getAccount().getMemberId().equals(gamePost.getMember().getMemberId())) {
            return GlobalResDto.fail(new Error(ErrorCode.NO_PERMISSION_CHANGE));
        }
        gamePost.updatePost(putGamepostReqDto);

        return GlobalResDto.success(getGamePost(gamePostId).getData());

    }

    @Transactional
    public GlobalResDto<?> deleteGamePost(Long gamePostId, UserDetailsImpl userDetails) {

        GamePost gamePost = isPresentGamePost(gamePostId);
        if (gamePost == null) {
            return GlobalResDto.fail(new Error(ErrorCode.NOT_FOUND_GAMEPOST));
        }
        if (!userDetails.getAccount().getMemberId().equals(gamePost.getMember().getMemberId())) {
            return GlobalResDto.fail(new Error(ErrorCode.NO_PERMISSION_DELETE));
        }

        if (!gamePost.getPath().equals("images/normal.jpg")) {
            amazonS3ResourceStorage.delimg(gamePost.getPath());
        }

        gamePostRepository.deleteById(gamePost.getGamePostId());
        return GlobalResDto.success(null);
    }

    public GlobalResDto<List<GamePostResDto>> getAllGamePostTrue() {
        List<GamePost> gamePosts = gamePostRepository.findAllByRecruitStatus(true);

        //원하는 dto로 바뀌기 위해 list
        List<GamePostResDto> gamePostResDtos = new ArrayList<>();

        for (GamePost gamePost : gamePosts) {
            Long numberOfRecruited = recruitStatusRepository.countByGamePost(gamePost);
            String countTime = countDate(gamePost.getCreatedAt());
            String postTime = gamePost.getCreatedAt().format(DateTimeFormatter.ofPattern("M월 d일 h시 m분"));
            String imgUrl = amazonS3ResourceStorage.getimg(gamePost.getPath());
            GamePostResDto gamePostResDto = GamePostResDto.toGamePostResDto(postTime, countTime, gamePost, numberOfRecruited + 1, imgUrl);
            gamePostResDtos.add(0, gamePostResDto);
        }
        return GlobalResDto.success(gamePostResDtos);
    }

    public GlobalResDto<List<GamePostResDto>> getAllGamePostFalse() {
        List<GamePost> gamePosts = gamePostRepository.findAllByRecruitStatus(false);

        //원하는 dto로 바뀌기 위해 list
        List<GamePostResDto> gamePostResDtos = new ArrayList<>();
        for (GamePost gamePost : gamePosts) {
            String countTime = countDate(gamePost.getCreatedAt());
            String postTime = gamePost.getCreatedAt().format(DateTimeFormatter.ofPattern("M월 d일 h시 m분"));
            String imgUrl = amazonS3ResourceStorage.getimg(gamePost.getPath());
            List<String> inGameNickname = isPresentNickname(gamePost);
            inGameNickname.add(0, gamePost.getMyIngameNickname());
            GamePostResDto gamePostResDto = GamePostResDto.toDoneGamePostResDto(postTime, countTime, gamePost, inGameNickname, gamePost.getNumberOfPeople(), imgUrl);
            gamePostResDtos.add(0, gamePostResDto);
        }
        return GlobalResDto.success(gamePostResDtos);
    }

    public GlobalResDto<?> getGamePost(Long gamePostId) {
        GamePost gamePost = isPresentGamePost(gamePostId);
        if (gamePost == null) {
            return GlobalResDto.fail(new Error(ErrorCode.NOT_FOUND_GAMEPOST));
        }
        Long numberOfRecruited = recruitStatusRepository.countByGamePost(gamePost);
        String countTime = countDate(gamePost.getCreatedAt());
        String postTime = gamePost.getCreatedAt().format(DateTimeFormatter.ofPattern("M월 d일 h시 m분"));
        String imgUrl = amazonS3ResourceStorage.getimg(gamePost.getPath());
        if (gamePost.getRecruitStatus()) {
            GamePostResDto gamePostResDto = GamePostResDto.toGamePostResDto(postTime, countTime, gamePost, numberOfRecruited + 1, imgUrl);
            return GlobalResDto.success(gamePostResDto);
        } else {
            List<String> inGameNickname = isPresentNickname(gamePost);
            inGameNickname.add(0, gamePost.getMyIngameNickname());
            GamePostResDto gamePostResDto = GamePostResDto.toDoneGamePostResDto(postTime, countTime, gamePost, inGameNickname, gamePost.getNumberOfPeople(), imgUrl);
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

    public String countDate(LocalDateTime localDateTime) {
        String countTime = "";

        LocalDateTime now = LocalDateTime.now();//현재날짜 시간
        LocalDate nowDate = now.toLocalDate();//현재 날짜
        LocalTime nowTime = now.toLocalTime();//현재 시간

        LocalDate postDate = localDateTime.toLocalDate();//포스팅 날짜
        LocalTime postTime = localDateTime.toLocalTime();//포스팅 시간

        Period period = Period.between(postDate, nowDate);

        Duration duration = Duration.between(postTime, nowTime);
        long betweenTime = duration.getSeconds();

        if (period.getDays() < 1) {
            if (betweenTime <= 60) {
                countTime = "1분 전";
            } else if (betweenTime <= 6000) {
                countTime = (betweenTime / 60) + "분 전";
            } else if (betweenTime <= 86400) {
                countTime = (betweenTime / 60 / 60) + "시간 전";
            }
        } else if (period.getDays() < 7) {
            countTime = period.getDays() + "일 전";
        } else {
            countTime = localDateTime.format(DateTimeFormatter.ofPattern("MM월 dd일 HH시 mm분"));
        }

        return countTime;
    }

    public GlobalResDto<List<GamePostResDto>> searchPost(String searchKeyword) {

        List<GamePost> gamePosts = gamePostRepository.findAllByGameNameContaining(searchKeyword);

        //원하는 dto로 바뀌기 위해 list
        List<GamePostResDto> gamePostResDtos = new ArrayList<>();

        for (GamePost gamePost : gamePosts) {
            Long numberOfRecruited = recruitStatusRepository.countByGamePost(gamePost);
            String countTime = countDate(gamePost.getCreatedAt());
            String postTime = gamePost.getCreatedAt().format(DateTimeFormatter.ofPattern("M월 d일 h시 m분"));
            String imgurl = amazonS3ResourceStorage.getimg(gamePost.getPath());
            if (gamePost.getRecruitStatus()) {
                GamePostResDto gamePostResDto = GamePostResDto.toGamePostResDto(postTime, countTime, gamePost, numberOfRecruited, imgurl);
                gamePostResDtos.add(gamePostResDto);
            } else {
                List<String> inGameNickname = isPresentNickname(gamePost);
                inGameNickname.add(0, gamePost.getMyIngameNickname());
                GamePostResDto gamePostResDto = GamePostResDto.toDoneGamePostResDto(postTime, countTime, gamePost, inGameNickname, numberOfRecruited, imgurl);
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
