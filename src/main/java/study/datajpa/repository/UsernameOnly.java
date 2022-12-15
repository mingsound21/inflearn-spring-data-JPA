package study.datajpa.repository;


import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {
    @Value("#{target.username + ' ' + target.age}")// target = Member
    String getUsername();// 위에 @Value에 정의한 문자열
}
