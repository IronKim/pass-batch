package com.fs.pass.job.notification;

import com.fs.pass.repository.booking.BookingEntity;
import com.fs.pass.repository.booking.BookingStatus;
import com.fs.pass.repository.notification.NotificationEntity;
import com.fs.pass.repository.notification.NotificationEvent;
import com.fs.pass.repository.notification.NotificationModelMapper;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Map;

//@Configuration
public class SendNotificationBeforeClassJobConfig {
    private final int CHUNK_SIZE = 10;

    private final EntityManagerFactory entityManagerFactory;
    private final SendNotificationItemWriter sendNotificationItemWriter;

    public SendNotificationBeforeClassJobConfig(EntityManagerFactory entityManagerFactory, SendNotificationItemWriter sendNotificationItemWriter) {
        this.entityManagerFactory = entityManagerFactory;
        this.sendNotificationItemWriter = sendNotificationItemWriter;
    }

    @Bean
    public Job sendNotificationBeforeClassJob(JobRepository jobRepository, Step addNotificationStep, Step sendNotificationStep) {
        return new JobBuilder("sendNotificationBeforeClassJob", jobRepository)
                .start(addNotificationStep)
                .next(sendNotificationStep)
                .build();
    }

    @Bean
    public Step addNotificationStep(final JobRepository jobRepository,
                                    final PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("addNotificationStep", jobRepository)
                .<BookingEntity, NotificationEntity>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(addNotificationItemReader())
                .processor(addNotificationItemProcessor())
                .writer(addNotificationItemWriter())
                .build();
    }

    /**
     * JpaPagingItemReader: JPA에서 사용하는 페이징 기법
     * 쿼리 당 pageSize만큼 가져오며 다른 PagingItemReader와 마찬가지로 Thread-safe 하다.
     */
    @Bean
    public JpaPagingItemReader<BookingEntity> addNotificationItemReader() {
        // 상태(status)가 준비중이며, 시작일시(startedAt)이 10분 후 시작하는 예약이 알람 대상이 된다.
        return new JpaPagingItemReaderBuilder<BookingEntity>()
                .name("addNotificationItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("select b from BookingEntity b join fetch b.userEntity where b.status = :status and b.startedAt <= :startedAt order by b.bookingSeq")
                .parameterValues(Map.of("status", BookingStatus.READY, "startedAt", LocalDateTime.now().plusMinutes(10)))
                .build();
    }

    @Bean
    public ItemProcessor<BookingEntity, NotificationEntity> addNotificationItemProcessor() {
        return bookingEntity -> NotificationModelMapper.INSTANCE.toNotificationEntity(bookingEntity, NotificationEvent.BEFORE_CLASS);
    }

    @Bean
    public JpaItemWriter<NotificationEntity> addNotificationItemWriter() {
        return new JpaItemWriterBuilder<NotificationEntity>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public Step sendNotificationStep(final JobRepository jobRepository,
                                     final PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("sendNotificationStep", jobRepository)
                .<NotificationEntity, NotificationEntity>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(sendNotificationItemReader())
                .writer(sendNotificationItemWriter)
                .taskExecutor(new SimpleAsyncTaskExecutor()) // 비동기로 알람을 보낸다. (SimpleAsyncTaskExecutor: 간단한 비동기 작업을 수행하는 TaskExecutor)
                .build();
    }

    @Bean
    public SynchronizedItemStreamReader<NotificationEntity> sendNotificationItemReader() { //Cursor기반의 방식은 thread-safe하지 않기 때문에 SynchronizedItemStreamReader를 사용하여 thread-safe하게 만든다.
        // 이벤트(event)가 수업 전이며, 발송 여부(sent)가 미발송인 알람이 조회 대상이 된다.
        JpaCursorItemReader<NotificationEntity> itemReader = new JpaCursorItemReaderBuilder<NotificationEntity>()
                .name("sendNotificationItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select n from NotificationEntity n where n.event = :event and n.sent = :sent")
                .parameterValues(Map.of("event", NotificationEvent.BEFORE_CLASS, "sent", false)) // parameterValues: 쿼리에 바인딩할 파라미터를 설정한다.
                .build();

        return new SynchronizedItemStreamReaderBuilder<NotificationEntity>()
                .delegate(itemReader)
                .build();

    }

}
