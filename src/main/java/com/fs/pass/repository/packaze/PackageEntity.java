package com.fs.pass.repository.packaze;

import com.fs.pass.repository.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "package")
public class PackageEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // IDENTITY : 데이터베이스에 위임
    private Integer packageSeq;

    private String packageName;
    private Integer count;
    private Integer period;
}
