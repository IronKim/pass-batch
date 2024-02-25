package com.fs.pass.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration // 스프링 IoC 컨테이너에게 해당 클래스를 Bean 구성 Class임을 알려주는 어노테이션
@EnableJpaAuditing // JPA Auditing 활성화를 위해 추가| Auditing이란 생성시간/수정시간등 공통 필드를 자동으로 관리해주는 기능
@EnableAutoConfiguration // 스프링 부트의 자동 설정을 활성화하는 어노테이션
@EnableBatchProcessing // Spring Batch 기능을 활성화하고 배치 작업을 설정하기 위한 기본 구성을 제공합니다. JobBuilderFactory, StepBuilderFactory를 빈으로 등록되어 Job, Step 구현 시 사용할 수 있습니다.
@EntityScan("com.fs.pass.repository") // 엔티티 클래스를 찾을 패키지를 지정하는 어노테이션
@EnableJpaRepositories("com.fs.pass.repository") // JPA Repository를 스캔할 패키지를 지정하는 어노테이션
@EnableTransactionManagement // 트랜잭션 관리를 위한 어노테이션
public class TestBatchConfig {
}
