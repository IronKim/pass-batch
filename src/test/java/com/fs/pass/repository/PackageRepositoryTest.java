package com.fs.pass.repository;

import com.fs.pass.repository.packaze.PackageEntity;
import com.fs.pass.repository.packaze.PackageRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//@DataJpaTest // JPA 관련 설정만 로드하여 테스트에 사용할 수 있다.

@Slf4j // 로깅을 위한 어노테이션
@SpringBootTest // 스프링부트 테스트 어노테이션 -> 이 어노테이션을 사용하면 별도의 설정 없이 테스트에 사용할 수 있다.
@ActiveProfiles("test") // test 환경에서만 사용할 수 있는 설정을 사용하기 위해 사용
public class PackageRepositoryTest {

    @Autowired
    private PackageRepository packageRepository;

    @Test
    public void test_save() {
        // given
        PackageEntity packageEntity = new PackageEntity();
        packageEntity.setPackageName("바디 챌린지 PT 12주");
        packageEntity.setPeriod(84);

        // when
        packageRepository.save(packageEntity);

        // then
        assertNotNull(packageEntity.getPackageSeq());

    }

    @Test
    public void test_findByCreatedAtAfter() {
        // given
        LocalDateTime dateTime = LocalDateTime.now().minusMinutes(1);

        PackageEntity packageEntity0 = new PackageEntity();
        packageEntity0.setPackageName("학생 전용 3개월");
        packageEntity0.setPeriod(90);
        packageRepository.save(packageEntity0);

        PackageEntity packageEntity1 = new PackageEntity();
        packageEntity1.setPackageName("학생 전용 6개월");
        packageEntity1.setPeriod(180);
        packageRepository.save(packageEntity1);

        // when
        final List<PackageEntity> packageEntities = packageRepository.findByCreatedAtAfter(dateTime,
                PageRequest.of(0, 1, Sort.by("packageSeq").descending())); //pageNumber이란 현재 페이지, pageSize는 한 페이지에 보여줄 데이터의 개수 -> 0번째 페이지, 1개의 데이터를 가져온다.

        // then
        assertEquals(1, packageEntities.size());
        assertEquals(packageEntity1.getPackageSeq(), packageEntities.get(0).getPackageSeq());
    }

    @Test
    public void test_updateCountAndPeriod() {
        // given
        PackageEntity packageEntity = new PackageEntity();
        packageEntity.setPackageName("바디프로필 이벤트 4개월");
        packageEntity.setPeriod(90);
        packageRepository.save(packageEntity);

        // when
        int updatedCount = packageRepository.updateCountAndPeriod(packageEntity.getPackageSeq(), 30, 120);
        final PackageEntity updatedPackageEntity = packageRepository.findById(packageEntity.getPackageSeq()).get();

        // then
        assertEquals(1, updatedCount);
        assertEquals(30, updatedPackageEntity.getCount());
        assertEquals(120, updatedPackageEntity.getPeriod());
    }

    @Test
    public void test_delete() {
        // given
        PackageEntity packageEntity = new PackageEntity();
        packageEntity.setPackageName("제거할 이용권");
        packageEntity.setCount(1);
        PackageEntity newPackageEntity = packageRepository.save(packageEntity);

        // when
        packageRepository.deleteById(newPackageEntity.getPackageSeq());

        // then
        assertTrue(packageRepository.findById(newPackageEntity.getPackageSeq()).isEmpty());
    }
}
