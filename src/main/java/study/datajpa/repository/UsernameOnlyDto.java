package study.datajpa.repository;


public class UsernameOnlyDto {
    private final String username;

    // 생성자의 파라미터 이름으로 매칭
    public UsernameOnlyDto(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
