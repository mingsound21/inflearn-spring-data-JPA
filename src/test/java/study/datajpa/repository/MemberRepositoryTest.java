package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext EntityManager em;

    @Test
    public void testMember(){

        System.out.println("memberRepository = " + memberRepository.getClass()); // class com.sun.proxy.$Proxy121 (스프링 데이터 JPA가 인터페이스 보고 프록시 객체(구현체)를 만들어서 꽂음)

        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member); // true (같은 트랜잭션 안에서는 같은 인스턴스임을 보장(참조값이 같음) - 1차 캐시)
    }

    @Test
    public void basicCRUD(){// MemberJpaRepository -> MemberRepository로 변경
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("BBB", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findMemberDto(){
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
        // 실무에서는 assert 사용해야함. System.out으로 콘솔에 출력 X
    }

    @Test
    public void findByNames(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> listFindMember = memberRepository.findListByUsername("AAA"); // 컬렉션
        Member findMember = memberRepository.findMemberByUsername("AAA"); // 단건
        Optional<Member> optionalFindMember = memberRepository.findOptionalMemberByUsername("AAA"); // Optional
        System.out.println("listFindMember = " + listFindMember);
        System.out.println("findMember = " + findMember);
        System.out.println("optionalFindMember = " + optionalFindMember.get());
        
        // WARN) 컬렉션 리턴 값일 때, 데이터를 찾지 못한 경우, null이 아닌 [](empty 컬렉션)반환
    }

    @Test
    public void paging(){
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username")); // PageRequest : 쿼리에 limit하기 위한 것(totalCount 쿼리 여부는 반환 타입으로 결정)
        // PageRequest(구현체)의 부모를 타고가다보면 Pageable interface가 있음.
        // WARN) page 1이 아니라 0부터 시작!!

        // when
        Page<Member> page = memberRepository.findPageByAge(age, pageRequest);
        Slice<Member> slice = memberRepository.findSliceByAge(age, pageRequest);
        List<Member> list = memberRepository.findListByAge(age, pageRequest);

        // then
        // 1) 반환 타입 Page
        List<Member> content = page.getContent();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

        // 2. 반환 타입 Slice
        // Slice는 total 관련 함수들이 없음
        List<Member> content_slice = slice.getContent();

        assertThat(content_slice.size()).isEqualTo(3);
//        assertThat(slice.getTotalElements()).isEqualTo(5);
        assertThat(slice.getNumber()).isEqualTo(0);
//        assertThat(slice.getTotalPages()).isEqualTo(2);
        assertThat(slice.isFirst()).isTrue();
        assertThat(slice.hasNext()).isTrue();

        // 3. 반환 타입 List
        // 위의 함수들 동작 안함

        // 실무 팁! Entity는 외부로 절대 반환 금지!! DTO로 변환해서 반환!!
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null)); // Page유지하면서 DTO: api로 반환 가능
        // Page 유지하면서 JSON생성될 때, totalPage, totalElement 등이 JSON으로 반환됨
    }

    @Test
    public void bulkUpdate(){
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        // when
        int resultCount = memberRepository.bulkAgePlus(20);// 20살 이상인 사람들의 나이 + 1
        em.flush();
        em.clear();

        // WARN) JPA 영속성 컨텍스트 개념 때문에 벌크 연산 수행하면 영속성 컨텍스트 속 Entity 정보 != DB의 Entity 정보
        // 벌크 연산: 영속성 컨텍스트 무시하고 DB에 바로 쿼리 날림
        // 결론) 벌크 연산 이후에는 영속성 컨텍스트를 날려야 함. => 벌크연산 직후 em.flush(), em.clear();
        //       >> 영속성 컨텍스트 날리면, 영속성 컨텍스트 초기화되어서 다시 DB에서 Entity 조회해옴.
        Member member5 = memberRepository.findMemberByUsername("member5");
        System.out.println("member5.age = " + member5.getAge()); // 콘솔 출력 40, BUT DB에서는 41 (영속성 컨텍스트 초기화 안했을 경우)

        // then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy(){
        // given
        // member1 -> teamA
        // member2 -> teamB
        // 관계 : Member - Team : @ManyToOne, LAZY
        // FetchType = LAZY >> Member 조회 시, Team은 "프록시 객체"로 조회. 실제 Team 사용시 실제 쿼리 날라가면서 Team 객체 초기화됨.
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when
         List<Member> members = memberRepository.findAll();
        // findAll override하기 전 : 쿼리 수: 1 + N
        // findAll override한 후 : 쿼리 수: 1
        // List<Member> members = memberRepository.findMemberFetchJoin(); // 쿼리 수: 1
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass()); // team 프록시 객체 확인
            // team 객체 프록시 초기화
            System.out.println("member.team = " + member.getTeam().getName()); // 쿼리 수: member 수
        }
    }

    @Test
    public void queryHint(){
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        // when
//        Member findMember = memberRepository.findById(member1.getId()).get();
//        findMember.setUsername("member2");

        Member findMember_readOnly = memberRepository.findReadOnlyByUsername("member1");
        findMember_readOnly.setUsername("member2");
        // readOnly = true이면, 변경감지 체크를 안함 = update 쿼리 안날라감
        
        em.flush(); // 변경감지 - update
    }

    @Test
    public void lock(){
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        // when

        List<Member> result= memberRepository.findLockByUsername("member1");
    }

}