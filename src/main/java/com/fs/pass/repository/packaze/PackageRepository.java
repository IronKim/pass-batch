package com.fs.pass.repository.packaze;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface PackageRepository extends JpaRepository<PackageEntity, Integer> {

    List<PackageEntity> findByCreatedAtAfter(LocalDateTime dateTime, Pageable pageable);

    @Transactional // @Query 어노테이션을 사용하여 JPQL을 사용할 때는 @Transactional 어노테이션을 사용하여 해당 쿼리가 하나의 트랜잭션으로 묶이도록 해야 한다.
    @Modifying // @Query 어노테이션을 사용하여 JPQL을 사용할 때는 @Modifying 어노테이션을 사용하여 해당 쿼리가 INSERT, UPDATE, DELETE 쿼리임을 명시해야 한다.
    @Query(value = "UPDATE PackageEntity p" +
                   " SET p.count = :count," +
                   "     p.period = :period" +
                   " WHERE p.packageSeq = :packageSeq") // JPQL 사용 시 @Query 어노테이션을 사용하여 쿼리를 작성할 수 있다.
    int updateCountAndPeriod(Integer packageSeq, Integer count, Integer period);
}
