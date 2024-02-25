package com.fs.pass.job.notification;

import com.fs.pass.adapter.message.KakaoTalkMessageAdapter;
import com.fs.pass.config.KakaoTalkMessageConfig;
import com.fs.pass.config.TestBatchConfig;
import com.fs.pass.repository.booking.BookingEntity;
import com.fs.pass.repository.booking.BookingRepository;
import com.fs.pass.repository.booking.BookingStatus;
import com.fs.pass.repository.pass.PassEntity;
import com.fs.pass.repository.pass.PassRepository;
import com.fs.pass.repository.pass.PassStatus;
import com.fs.pass.repository.user.UserEntity;
import com.fs.pass.repository.user.UserRepository;
import com.fs.pass.repository.user.UserStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
@Import(SendNotificationBeforeClassJobConfigTest.TestJpaConfig.class)
@ContextConfiguration(classes = {
        SendNotificationBeforeClassJobConfig.class,
        TestBatchConfig.class,
        SendNotificationItemWriter.class,
        KakaoTalkMessageConfig.class,
        KakaoTalkMessageAdapter.class
})
public class SendNotificationBeforeClassJobConfigTest {

    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private PassRepository passRepository;
    @Autowired
    private UserRepository userRepository;

    SendNotificationBeforeClassJobConfigTest(@Autowired JobLauncherTestUtils jobLauncherTestUtils) {
        this.jobLauncherTestUtils = jobLauncherTestUtils;
    }


    @Test
    public void test_addNotificationStep() throws Exception {
        // given
        addBookingEntity();

        // when

        JobExecution jobExecution = jobLauncherTestUtils.launchStep("addNotificationStep");

        // then
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());

    }

    private void addBookingEntity() {
        final LocalDateTime now = LocalDateTime.now();
        final String userId = "A100" + RandomStringUtils.randomNumeric(4); //RandomStringUtils는 Apache Commons Lang 라이브러리에 포함된 클래스로, 랜덤한 문자열을 생성하는데 사용된다.

        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(userId);
        userEntity.setUserName("김영희");
        userEntity.setStatus(UserStatus.ACTIVE);
        userEntity.setPhone("01033334444");
        userEntity.setMeta(Map.of("uuid", "abcd1234"));
        userRepository.save(userEntity);

        PassEntity passEntity = new PassEntity();
        passEntity.setPackageSeq(1);
        passEntity.setUserId(userId);
        passEntity.setStatus(PassStatus.PROGRESSED);
        passEntity.setRemainingCount(10);
        passEntity.setStartedAt(now.minusDays(60));
        passEntity.setEndedAt(now.minusDays(1));
        passRepository.save(passEntity);

        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setPassSeq(passEntity.getPassSeq());
        bookingEntity.setUserId(userId);
        bookingEntity.setStatus(BookingStatus.READY);
        bookingEntity.setStartedAt(now.plusMinutes(10));
        bookingEntity.setEndedAt(bookingEntity.getStartedAt().plusMinutes(50));
        bookingRepository.save(bookingEntity);

    }

    @EnableJpaAuditing
    @TestConfiguration
    static class TestJpaConfig { }

}