package io.github.divinator.service;

import io.github.divinator.exception.CallLogServiceException;
import io.github.divinator.history.CallHistoryData;
import io.github.divinator.history.CallHistoryInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@PropertySource({"classpath:application.properties"})
public class ScheduledService {

    @Value("${application.scheduler.listen:false}")
    private boolean listen;

    private final Logger LOG = LoggerFactory.getLogger(CallLogService.class);
    private final CallLogService callLogService;
    private final SettingsService settingsService;
    private CallHistoryInformation callHistoryInformation;

    public ScheduledService(CallLogService callLogService, SettingsService settingsService) {
        this.callLogService = callLogService;
        this.settingsService = settingsService;
        try {
            callHistoryInformation = callLogService.readHistory();
        } catch (CallLogServiceException e) {
            LOG.error(e.getLocalizedMessage());
        }
    }

    @Scheduled(fixedRate = 1000L)
    public void checkHistory() {
        if (Boolean.parseBoolean((String) settingsService.getSettings("application.scheduler.history").getValue())) {
            try {
                CallHistoryInformation callHistoryInformation = callLogService.readHistory();

                if (!callHistoryInformation.getNextSessionId().equals(this.callHistoryInformation.getNextSessionId())) {
                    LOG.info("Есть новая запись в журнале звонков Avaya one-X Communicator");
                    CallHistoryData last = (callHistoryInformation.getLast().isIncoming())
                            ? callHistoryInformation.getLast()
                            : callHistoryInformation.getLastIncoming();

                    if (!last.isCommand()) {
                        LOG.info("Запись в журнале звонков Avaya one-X Communicator является звонком");
                        ZonedDateTime startTime = Instant.ofEpochSecond(Long.parseLong(last.getStartTime())).atZone(ZoneId.of("UTC"));
                        ZonedDateTime dateTime = startTime.withZoneSameInstant(ZoneId.of((String) settingsService.getSettings("application.db.zone").getValue()));
                        this.callLogService.createCallHistoryEntity(
                                last.getFirstContact().getPhoneNumber(),
                                dateTime,
                                false
                        );
                    }

                    this.callHistoryInformation = callHistoryInformation;
                }

            } catch (CallLogServiceException e) {
                LOG.error("Журнал истории звонков не найден.");
            }
        }
    }
}