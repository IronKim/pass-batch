package com.fs.pass.repository.user;

import com.fs.pass.repository.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "user_group_mapping")
@IdClass(UserGroupMappingId.class) // 복합키를 사용하기 위해 IdClass를 정의합니다.
public class UserGroupMappingEntity extends BaseEntity {
    @Id
    private String userGroupId;
    @Id
    private String userId;

    private String userGroupName;
    private String description;

}
