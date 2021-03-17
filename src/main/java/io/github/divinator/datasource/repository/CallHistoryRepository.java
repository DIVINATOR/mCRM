package io.github.divinator.datasource.repository;

import io.github.divinator.datasource.entity.CallHistoryEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface CallHistoryRepository extends PagingAndSortingRepository<CallHistoryEntity, Long> {
    CallHistoryEntity findFirstByOrderByDateTimeDesc();

    CallHistoryEntity findFirstByOrderByCreateDateTimeDesc();

    CallHistoryEntity findByCreateDateTime(LocalDateTime localDateTime);

    CallHistoryEntity findByDateTime(LocalDateTime localDateTime);

    Iterable<CallHistoryEntity> findAllByCreateDateTimeAfter(LocalDate localDate);

    Iterable<CallHistoryEntity> findAllByCreateDateTimeBetween(LocalDateTime localDateTimeFrom, LocalDateTime localDateTimeTo);

    Long countAllByTransferredFalseAndCreateDateTimeBetween(LocalDateTime localDateTimeFrom, LocalDateTime localDateTimeTo);

    Long countAllByCreateDateTimeBetween(LocalDateTime localDateTimeFrom, LocalDateTime localDateTimeTo);
}
