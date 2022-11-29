package study.datajpa.entity;

import lombok.*;
import org.springframework.context.annotation.EnableMBeanExport;

import javax.persistence.*;

@Entity
@Getter @Setter // setter는 가급적 없는 게 GOOD(실무에서는 꼭 필요할 때만 setter를 쓰거나, 별도의 비즈니스 네이밍된 함수를 사용)
@NoArgsConstructor(access = AccessLevel.PROTECTED) // @Entity 기본 생성자 필요, 아무곳에서나 사용하지 않도록 PROTECTED
@ToString(of = {"id", "username", "age"}) // "team" 추가하면 안됨(양방향 연관관계 일 때, ToString 무한 루프 주의)
@NamedQuery( // Named Query 큰 장점: 정적 쿼리라서 애플리케이션 로딩 시점에 쿼리 파싱해 문법 오류 발견시 에러 발생
        name = "Member.findByUsername", // 관례: 엔티티명.함수명
        query = "select m from Member m where m.username = :username"
)
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    // 생성자
    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if(team != null){ // 일단은 team == null이면 무시
            changeTeam(team);
        }
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    // setter 대신 이런 메서드 생성하는 것이 GOOD
    public void changeUsername(String username){
        this.username = username;
    }

    // 연관관계 편의 메서드
    public void changeTeam(Team team){
        this.team = team;
        team.getMembers().add(this);
    }


}
