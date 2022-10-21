package com.week06.team01_week06_project.respository;

import com.week06.team01_week06_project.domain.GamePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GamePostRepository extends JpaRepository<GamePost,Long> {
}
