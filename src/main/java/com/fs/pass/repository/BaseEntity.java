package com.fs.pass.repository;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass // JPA Entity 클래스들이 BaseTimeEntity를 상속할 경우 필드들도 칼럼으로 인식하도록 한다.
@EntityListeners(AuditingEntityListener.class) // BaseTimeEntity 클래스에 Auditing 기능을 포함시킨다.
public abstract class BaseEntity {
    @CreatedDate // Entity가 생성되어 저장될 때 시간이 자동 저장된다.
    @Column(updatable = false, nullable = false) // 생성일자는 수정이 불가능하도록 설정
    private LocalDateTime createdAt;
    @LastModifiedDate // 조회한 Entity의 값을 변경할 때 시간이 자동 저장된다.
    private LocalDateTime modifiedAt;
}
