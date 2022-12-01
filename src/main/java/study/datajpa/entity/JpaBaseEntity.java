package study.datajpa.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass // 진짜 상속관계는 아니고, 속성만 내려서 테이블에 넣어서 사용할 수 있게하는 것.
public class JpaBaseEntity {
    
    @Column(updatable = false) // 등록일은 실수로라도 수정할 수 없도록
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist // persist하기 전에 이벤트가 발생
    public void prePersist(){
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now;
    }

    @PreUpdate
    public void preUpdate(){
        updatedDate = LocalDateTime.now();
    }
}
