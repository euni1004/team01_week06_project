package com.week06.team01_week06_project.respository;

import com.week06.team01_week06_project.domain.GamePost;
import com.week06.team01_week06_project.domain.Member;
import com.week06.team01_week06_project.domain.RecruitStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecruitStatusRepository extends JpaRepository<RecruitStatus,Long> {

    Optional<RecruitStatus> findByGamePostAndMember(GamePost gamePost, Member member);

    Long countByGamePost(GamePost gamePost);

    Optional<List<RecruitStatus>> findAllBygamePost(GamePost gamePost);

    void deleteAllByGamePost(GamePost gamePost);

}
