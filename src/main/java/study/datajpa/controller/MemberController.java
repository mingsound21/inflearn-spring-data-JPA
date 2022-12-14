package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

    // 도메인 클래스 컨버터
    // 파라미터 : id
    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id){
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    // 파라미터 : Member 객체
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member){ // 도메인 클래스 컨버터
        return member.getUsername();
    }

    // 페이징과 정렬
    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size=12, sort = "username", direction = Sort.Direction.DESC) Pageable pageable){
        Page<Member> page = memberRepository.findAll(pageable);// 모든 함수 pagable 파라미터 넘겨주면 페이징
        Page<MemberDto> map = page.map(MemberDto::new); // Dto로 변환한 뒤 반환
        return map;
    }

    @PostConstruct
    public void init(){
        for(int i = 0; i< 100; i++){
            memberRepository.save(new Member("user" + i, i));
        }
    }
}
