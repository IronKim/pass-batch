package com.fs.pass.job.pass;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class AddPassesJobConfig {

    private final AddPassesTasklet addPassesTasklet;

    public AddPassesJobConfig(AddPassesTasklet addPassesTasklet) {
        this.addPassesTasklet = addPassesTasklet;
    }

    @Bean
    public Job addPassesJob(JobRepository jobRepository, Step addPassesStep) {
        return new JobBuilder("addPassesJob", jobRepository)
                .start(addPassesStep)
                .build();
    }

    @Bean
    public Step addPassesStep(final JobRepository jobRepository,
                              final PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("addPassesStep", jobRepository)
                .tasklet(addPassesTasklet, platformTransactionManager)
                .build();
    }
}
