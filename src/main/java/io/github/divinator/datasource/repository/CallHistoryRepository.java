package io.github.divinator.datasource.repository;

import io.github.divinator.datasource.entity.CallHistory;
import java.time.LocalDateTime;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CallHistoryRepository extends PagingAndSortingRepository<CallHistory, Long> {
    CallHistory findFirstByOrderByDateTimeDesc();

    CallHistory findFirstByOrderByCreateDateTimeDesc();

    CallHistory findByCreateDateTime(LocalDateTime localDateTime);

    CallHistory findByDateTime(LocalDateTime localDateTime);

    Iterable<CallHistory> findAllByDateTimeAfter(LocalDateTime localDateTime);

    Iterable<CallHistory> findAllByDateTimeBetween(LocalDateTime localDateTimeFrom, LocalDateTime localDateTimeTo);
}
