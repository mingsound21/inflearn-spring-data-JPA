package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Team;

// @Repository 없어도 됨
public interface TeamRepository extends JpaRepository<Team, Long> {
}
