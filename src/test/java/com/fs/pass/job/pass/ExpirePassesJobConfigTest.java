package com.fs.pass.job.pass;

import com.fs.pass.config.TestBatchConfig;
import com.fs.pass.repository.pass.PassEntity;
import com.fs.pass.repository.pass.PassRepository;
import com.fs.pass.repository.pass.PassStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBatchTest // Spring Batch 테스트를 위한 어노테이션
@SpringBootTest
@ActiveProfiles("test")
@Import(ExpirePassesJobConfigTest.TestJpaConfig.class)
@ContextConfiguration(classes = {ExpirePassesJobConfig.class, TestBatchConfig.class})
class ExpirePassesJobConfigTest {

    private final JobLauncherTestUtils jobLauncherTestUtils; // Job을 실행하기 위한 유틸리티 클래스

    @Autowired
    private PassRepository passRepository;

    ExpirePassesJobConfigTest(@Autowired JobLauncherTestUtils jobLauncherTestUtils) {
            this.jobLauncherTestUtils = jobLauncherTestUtils;
    }

    @Test
    public void test_expirePassesStep() throws Exception {
        // given
        addPassEntities(10);

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        JobInstance jobInstance = jobExecution.getJobInstance();

        // then
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
        assertEquals("expirePassesJob", jobInstance.getJobName());
    }

    private void addPassEntities(int size) {
        final LocalDateTime now = LocalDateTime.now();
        final Random random = new Random();

        List<PassEntity> passEntityList = new ArrayList<>();
        for(int i = 0; i < size; ++i) {
            PassEntity passEntity = new PassEntity();
            passEntity.setPackageSeq(1);
            passEntity.setUserId("A" + 1000000 + i);
            passEntity.setStatus(PassStatus.PROGRESSED);
            passEntity.setRemainingCount(random.nextInt(11));
            passEntity.setStartedAt(now.minusDays(60));
            passEntity.setEndedAt(now.minusDays(1));
            passEntityList.add(passEntity);

        }
        passRepository.saveAll(passEntityList);

    }
    @EnableJpaAuditing
    @TestConfiguration
    static class TestJpaConfig { }
}