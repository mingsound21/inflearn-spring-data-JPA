package study.datajpa.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> { // 인터페이스 상속 extends

    // 메소드 이름으로 쿼리 생성 - 필드이름 정확히 적어야함
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
    List<Member> findHelloBy();

    // Named Query
    @Query(name = "Member.findByUsername") // NamedQuery의 name
    List<Member> findByUsername(@Param("username") String username);// @Param : JPQL parameter setting
    // 단, @Query 주석처리해도 정상 실행됨.
    // Member.findByUsername(엔티티.함수명)을 이용해서 NamedQuery에서 이러한 이름의 NamedQuery있는지 먼저 확인함.
    // 해당 이름의 NamedQuery 발견하면 NamedQuery 실행하고, 없으면 쿼리 이름으로 쿼리 생성하는 방식 사용
    // 즉, 우선 순위가 NamedQuery > 쿼리 이름으로 쿼리 생성

    // @Query, 리포지토리에 메소드에 쿼리 정의하기
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);
}
