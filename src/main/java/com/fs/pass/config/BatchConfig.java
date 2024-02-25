package com.fs.pass.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/* * @EnableBatchProcessing * Spring Batch 기능을 활성화하고 배치 작업을 설정하기 위한 기본 구성을 제공합니다. * JobBuilderFactory, StepBuilderFactory를 빈으로 등록되어 Job, Step 구현 시 사용할 수 있습니다. */

@Configuration
public class BatchConfig {

    @Bean
    public Job passJob(JobRepository jobRepository, Step passStep) {
        return new JobBuilder("passJob", jobRepository)
                .start(passStep)
                .build();
    }

    @Bean
    public Step passStep(final JobRepository jobRepository,
                         final Tasklet passTasklet,
                         final PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("passStep", jobRepository)
                .tasklet(passTasklet, platformTransactionManager)
                .build();
    }

    @Bean
    public Tasklet passTasklet() {
        return ((contribution, chunkContext) -> {
            System.out.println("Execute PassStep");
            return RepeatStatus.FINISHED;
        });
    }

}
