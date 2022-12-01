package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing
@SpringBootApplication
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

	// 등록자, 수정자 설정 필요
	@Bean
	public AuditorAware<String> auditorProvider(){
		return new AuditorAware<String>() {
			@Override
			public Optional<String> getCurrentAuditor() {
				return Optional.of(UUID.randomUUID().toString()); // 지금은 랜덤으로
			}
		};
		// 참고:
		// 1. 인터페이스에서 메서드 1개이면 람다로 변경 가능
		// 2. 실제로는 SpringSecurity 사용하시면 세션정보에서 ID 꺼내셔야함	
	}
}
