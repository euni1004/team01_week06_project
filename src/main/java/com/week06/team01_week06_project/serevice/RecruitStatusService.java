package com.week06.team01_week06_project.serevice;

import com.week06.team01_week06_project.domain.GamePost;
import com.week06.team01_week06_project.domain.Member;
import com.week06.team01_week06_project.domain.RecruitStatus;
import com.week06.team01_week06_project.dto.GlobalResDto;
import com.week06.team01_week06_project.dto.request.RecruitMemberDto;
import com.week06.team01_week06_project.respository.GamePostRepository;
import com.week06.team01_week06_project.respository.MemberRepository;
import com.week06.team01_week06_project.respository.RecruitStatusRepository;
import com.week06.team01_week06_project.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecruitStatusService {

    private final MemberRepository memberRepository;
    private final GamePostRepository gamePostRepository;
    private final RecruitStatusRepository recruitStatusRepository;


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

    public GamePost isPresentGamePost(Long gamepostid) {
        Optional<GamePost> gamePost = gamePostRepository.findById(gamepostid);
        return gamePost.orElse(null);
    }

    public RecruitStatus isPresentRecruitStatus(GamePost gamePost, Member member) {
        Optional<RecruitStatus> recruitStatus = recruitStatusRepository.findByGamePostAndMember(gamePost, member);
        return recruitStatus.orElse(null);
    }
}
