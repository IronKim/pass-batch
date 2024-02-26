package com.fs.pass.job.pass;

import com.fs.pass.repository.booking.BookingEntity;
import com.fs.pass.repository.booking.BookingRepository;
import com.fs.pass.repository.booking.BookingStatus;
import com.fs.pass.repository.pass.PassEntity;
import com.fs.pass.repository.pass.PassRepository;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.Future;

@Configuration
public class UsePassesJobConfig {

    private final int CHUNK_SIZE = 10;

    private final EntityManagerFactory entityManagerFactory;

    private final PassRepository passRepository;
    private final BookingRepository bookingRepository;

    public UsePassesJobConfig(EntityManagerFactory entityManagerFactory, PassRepository passRepository, BookingRepository bookingRepository) {
        this.entityManagerFactory = entityManagerFactory;
        this.passRepository = passRepository;
        this.bookingRepository = bookingRepository;
    }

    @Bean
    public Job userPassesJob(JobRepository jobRepository, Step usePassesStep) {
        return new JobBuilder("usePassesJob", jobRepository)
                .start(usePassesStep)
                .build();
    }

    @Bean
    public Step usePassesStep(final JobRepository jobRepository,
                              final PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("usePassesStep", jobRepository)
                .<BookingEntity, Future<BookingEntity>>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(usePassesItemReader())
                .processor(usePassesAsyncItemProcessor())
                .writer(usePassesAsyncItemWriter())
                .build();
    }

    @Bean
    public JpaCursorItemReader<BookingEntity> usePassesItemReader() {
        return new JpaCursorItemReaderBuilder<BookingEntity>()
                .name("usePassesItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select b from BookingEntity b join fetch b.passEntity where b.status = :status and b.usedPass = false and b.endedAt < :endedAt")
                .parameterValues(Map.of("status", BookingStatus.COMPLETED, "endedAt", LocalDateTime.now()))
                .build();
    }

    // 이 프로젝트에서는 적합하지 않지만, ItemProcessor의 수행이 오래걸려 병목이 생기는 경우에 AsyncItemProcessor, AsyncItemWriter를 사용하면 성능을 향상시킬 수 있습니다.
    @Bean
    public AsyncItemProcessor<BookingEntity, BookingEntity> usePassesAsyncItemProcessor() {
        AsyncItemProcessor<BookingEntity, BookingEntity> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(usePassesItemProcessor()); // usePassesItemProcessor로 위임하고 결과를 Future에 저장합니다.
        asyncItemProcessor.setTaskExecutor(new SimpleAsyncTaskExecutor()); // 비동기로 처리합니다. (기본적으로 SimpleAsyncTaskExecutor를 사용합니다.)
        return asyncItemProcessor;

    }

    @Bean
    public ItemProcessor<BookingEntity, BookingEntity> usePassesItemProcessor() {
        return bookingEntity -> {
            // 이용권 잔여 횟수는 차감합니다.
            PassEntity passEntity = bookingEntity.getPassEntity();
            passEntity.setRemainingCount(passEntity.getRemainingCount() - 1);
            bookingEntity.setPassEntity(passEntity);

            // 이용권 사용 여부를 업데이트합니다.
            bookingEntity.setUsedPass(true);
            return bookingEntity;

        };
    }

    @Bean
    public AsyncItemWriter<BookingEntity> usePassesAsyncItemWriter() {
        AsyncItemWriter<BookingEntity> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(usePassesItemWriter()); // usePassesItemWriter 최종 결과값을 넘겨주고 작업을 위임합니다.
        return asyncItemWriter;
    }

    @Bean
    public ItemWriter<BookingEntity> usePassesItemWriter() { //CustomItemWriter를 사용합니다.
        return bookingEntities -> {
            for (BookingEntity bookingEntity : bookingEntities) {
                // 잔여 횟수를 업데이트 합니다.
                int updatedCount = passRepository.updateRemainingCount(bookingEntity.getPassSeq(), bookingEntity.getPassEntity().getRemainingCount());
                // 잔여 횟수가 업데이트 완료되면, 이용권 사용 여부를 업데이트합니다.
                if (updatedCount > 0) {
                    bookingRepository.updateUsedPass(bookingEntity.getPassSeq(), bookingEntity.isUsedPass());
                }
            }
        };
    }
}
