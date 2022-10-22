package com.week06.team01_week06_project.serevice;

import com.week06.team01_week06_project.domain.GamePost;
import com.week06.team01_week06_project.domain.Member;
import com.week06.team01_week06_project.domain.RecruitStatus;
import com.week06.team01_week06_project.dto.GlobalResDto;
import com.week06.team01_week06_project.dto.request.GamepostReqDto;
import com.week06.team01_week06_project.dto.request.RecruitMemberDto;
import com.week06.team01_week06_project.dto.response.GamePostResDto;
import com.week06.team01_week06_project.respository.GamePostRepository;
import com.week06.team01_week06_project.respository.MemberRepository;
import com.week06.team01_week06_project.respository.RecruitStatusRepository;
import com.week06.team01_week06_project.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GamePostService {

    private final GamePostRepository gamePostRepository;
    private final MemberRepository memberRepository;
    private final RecruitStatusRepository recruitStatusRepository;

    @Transactional
    public GlobalResDto<?> generateGamePost(Long memberid, GamepostReqDto gamepostReqDto) {
        Member member = isPresentMember(memberid);
        if (member == null) {
            return GlobalResDto.fail("MEMBER_NOT_FOUND", "사용자가 존재하지 않습니다.");
        }
        GamePost gamePost = new GamePost(member, gamepostReqDto);
        gamePostRepository.save(gamePost);
        return GlobalResDto.success(null);
    }

    //모집글 수정하는 곳

    @Transactional
    public GlobalResDto<?> deleteGamePost(Long gamepostid, UserDetailsImpl userDetails) {

        GamePost gamePost = isPresentGamePost(gamepostid);
        if (gamePost == null) {
            return GlobalResDto.fail("GAMEPOST_NOT_FOUND", "게시물이 존재하지 않습니다.");
        }
        if (!userDetails.getAccount().getMemberId().equals(gamePost.getMember().getMemberId())) {
            return GlobalResDto.fail("NO_PERMISSION", "게시물은 자신이 작성한 게시물만 삭제할 수 있습니다.");
        }
        recruitStatusRepository.deleteAllByGamePost(gamePost);
        gamePostRepository.deleteById(gamePost.getGamePostId());
        return GlobalResDto.success(null);
    }

    public GlobalResDto<?> getAllGamePost() {
        List<GamePost> gamePosts = gamePostRepository.findAll();

        //원하는 dto로 바뀌기 위해 list
        List<GamePostResDto> gamePostResDtos = new ArrayList<>();

        for (GamePost gamePost : gamePosts) {

            if(gamePost.getRecruitStatus()){
                GamePostResDto gamePostResDto = GamePostResDto.toGamePostResDto(gamePost);
                gamePostResDtos.add(gamePostResDto);
            }else{
                List<String> inGameNickname =  isPresentNickname(gamePost);
                inGameNickname.add(0, gamePost.getMyIngameNickname());
                GamePostResDto gamePostResDto = GamePostResDto.toDoneGamePostResDto(gamePost, inGameNickname);
                gamePostResDtos.add(gamePostResDto);
            }
        }
        return GlobalResDto.success(gamePostResDtos);
    }

    public GlobalResDto<?> getGamePost(Long gamepostid) {
        GamePost gamePost = isPresentGamePost(gamepostid);
        if (gamePost == null) {
            return GlobalResDto.fail("GAMEPOST_NOT_FOUND", "게시물이 존재하지 않습니다.");
        }
        if(gamePost.getRecruitStatus()){
            GamePostResDto gamePostResDto = GamePostResDto.toGamePostResDto(gamePost);
            return GlobalResDto.success(gamePostResDto);
        }else{
            List<String> inGameNickname =  isPresentNickname(gamePost);
            inGameNickname.add(0, gamePost.getMyIngameNickname());
            GamePostResDto gamePostResDto = GamePostResDto.toDoneGamePostResDto(gamePost, inGameNickname);
            return GlobalResDto.success(gamePostResDto);
        }
    }

    @Transactional
    public GlobalResDto<?> participationGame(UserDetailsImpl userDetails, RecruitMemberDto recruitMemberDto, Long gamepostid) {
        GamePost gamePost = isPresentGamePost(gamepostid);
        if (gamePost == null) {
            return GlobalResDto.fail("GAMEPOST_NOT_FOUND", "게시물이 존재하지 않습니다.");
        }
        RecruitStatus recruitStatus1 = isPresentRecruitStatus(gamePost, userDetails.getAccount());
        if (recruitStatus1 != null) {
            return GlobalResDto.fail("RECRUITSTATUS_ALREADY", "참가신청은 한번만 가능합니다.");
        }

        RecruitStatus recruitStatus = new RecruitStatus(userDetails, gamePost, recruitMemberDto.getInGameNickname());
        recruitStatusRepository.save(recruitStatus);

        //만약 모집인원이 다찼다면 recruitstatus값을 false로 바꾸기
        if (recruitStatusRepository.countByGamePost(gamePost) == gamePost.getNumberOfPeople()) {
            gamePost.updateRecruitStatus(false);
        }

        return GlobalResDto.success(null);
    }

    @Transactional
    public GlobalResDto<?> cancelParticipationGame(UserDetailsImpl userDetails, Long gamepostid) {
        GamePost gamePost = isPresentGamePost(gamepostid);
        if (gamePost == null) {
            return GlobalResDto.fail("GAMEPOST_NOT_FOUND", "게시물이 존재하지 않습니다.");
        }

        RecruitStatus recruitStatus = isPresentRecruitStatus(gamePost, userDetails.getAccount());
        if (recruitStatus == null) {
            return GlobalResDto.fail("RECRUITSTATUS_NOT_FOUND", "참가신청한 이력이 없습니다.");
        }

        recruitStatusRepository.delete(recruitStatus);

        //gamepost에서 ingamenickname에서 참가취소한 사람 이름빼기
        gamePost.updateRecruitStatus(recruitStatusRepository.countByGamePost(gamePost) != gamePost.getNumberOfPeople());

        return GlobalResDto.success(null);
    }

    public Member isPresentMember(Long memberId) {
        Optional<Member> member = memberRepository.findById(memberId);
        return member.orElse(null);
    }

    public GamePost isPresentGamePost(Long gamepostid) {
        Optional<GamePost> gamePost = gamePostRepository.findById(gamepostid);
        return gamePost.orElse(null);
    }

    public RecruitStatus isPresentRecruitStatus(GamePost gamePost, Member member) {
        Optional<RecruitStatus> recruitStatus = recruitStatusRepository.findByGamePostAndMember(gamePost, member);
        return recruitStatus.orElse(null);
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