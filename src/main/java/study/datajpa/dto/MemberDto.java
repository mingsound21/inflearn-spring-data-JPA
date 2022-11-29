package study.datajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data// getter, setter 다 들어있는 어노테이션이라 되도록 사용X, 하지만 DTO고, 연습이니까
@AllArgsConstructor
public class MemberDto {
    private Long id;
    private String username;
    private String teamName;
}
