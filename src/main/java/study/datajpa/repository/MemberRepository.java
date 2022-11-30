package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.NamedEntityGraph;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

    // @Query, 리포지토리에 메소드에 쿼리 정의하기 - 엔티티 조회
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    // @Query - 값 조회
    @Query("select m.username from Member m")
    List<String> findUsernameList();

    // @Query - DTO 조회
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    // 컬렉션 파라미터 바인딩
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    // 다양한 반환 타입 지원
    List<Member> findListByUsername(String username); // 컬렉션
    Member findMemberByUsername(String username); // 단건
    Optional<Member> findOptionalMemberByUsername(String username); // 단건, Optional

    // 페이징과 정렬
    @Query(value = "select m from Member m left join m.team t", countQuery = "select count(m.username) from Member m") // totalCount 성능 최적화를 위한, 쿼리 분리(값 가져오는 쿼리, totalCount 쿼리)
    Page<Member> findPageByAge(int age, Pageable pageable); // 파라미터 pageable, 반환타입 Page
    Slice<Member> findSliceByAge(int age, Pageable pageable); // 파라미터 pageable, 반환타입 Slice
    List<Member> findListByAge(int age, Pageable pageable); // 파라미터 pageable, 반환타입 Page

    // 벌크성 수정 쿼리
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);
    // @Modifying: JPA의 executeUpdate를 실행 - 작성 안하면 getResultList, getSingleResult호출함
    // clearAutomatically = true: 벌크 연산 이후에 em.clear()자동으로 해줌

    // Fetch Join
    // member 조회시 연관된 team을 같이 "한 방 쿼리"로 가져옴
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    // Entity Graph
    @Override
    @EntityGraph(attributePaths = {"team"}) // 객체의 필드명
    List<Member> findAll();

    // JPQL에 Entity Graph추가(fetch join)
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    // Member 객체에 선언한 @NamedEntityGraph를 사용
//    @EntityGraph("Member.all")
//    List<Member> findEntityGraphByUsername(@Param("username") String username);
}
