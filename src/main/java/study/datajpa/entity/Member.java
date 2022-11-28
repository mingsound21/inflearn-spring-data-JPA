package study.datajpa.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter // setter는 가급적 없는 게 GOOD(실무에서는 꼭 필요할 때만 setter를 쓰거나, 별도의 비즈니스 네이밍된 함수를 사용)
@NoArgsConstructor(access = AccessLevel.PROTECTED) // @Entity 기본 생성자 필요, 아무곳에서나 사용하지 않도록 PROTECTED
public class Member {
    @Id
    @GeneratedValue
    private Long id;
    private String username;

    public Member(String username) {
        this.username = username;
    }

    // setter 대신 이런 메서드 생성하는 것이 GOOD
    public void changeUsername(String username){
        this.username = username;
    }
}
