package io.github.divinator.service;

import io.github.divinator.datasource.entity.CallHistoryEntity;
import io.github.divinator.datasource.repository.CallHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class CallHistoryService {

    public final CallHistoryRepository callHistoryRepository;
    public final SettingsService settingsService;

    public CallHistoryService(CallHistoryRepository callHistoryRepository, SettingsService settingsService) {
        this.callHistoryRepository = callHistoryRepository;
        this.settingsService = settingsService;
    }

    public CallHistoryEntity saveCallHistoryEntity(CallHistoryEntity callHistoryEntity) {
        return callHistoryRepository.save(callHistoryEntity);
    }

    public Iterable<CallHistoryEntity> getCallHistoryEntity(LocalDate localDate) {
        return callHistoryRepository.findAllByCreateDateTimeAfter(localDate);
    }

    public Iterable<CallHistoryEntity> getCallHistoryEntity(LocalDate localDateFrom, LocalDate localDateTo) {
        return callHistoryRepository.findAllByCreateDateTimeBetween(
                localDateFrom.atTime(LocalTime.MIDNIGHT),
                localDateTo.atTime(LocalTime.MAX)
        );
    }

    public float getFCR(LocalDate localDateFrom, LocalDate localDateTo) {
        LocalDateTime localDateTimeFrom = localDateFrom.atTime(LocalTime.MIDNIGHT);
        LocalDateTime localDateTimeTo = localDateFrom.atTime(LocalTime.MAX);

        Long transferred = callHistoryRepository.countAllByTransferredFalseAndCreateDateTimeBetween(localDateTimeFrom, localDateTimeTo);
        Long all = callHistoryRepository.countAllByCreateDateTimeBetween(localDateTimeFrom, localDateTimeTo);

        return Math.round((transferred * 100.0f) / all);
    }
}
