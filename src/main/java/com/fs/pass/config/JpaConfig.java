package com.fs.pass.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing // JPA Auditing 활성화를 위해 추가| Auditing이란 생성시간/수정시간등 공통 필드를 자동으로 관리해주는 기능
@Configuration
public class JpaConfig {
}
