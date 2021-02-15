package io.github.divinator.service;

import io.github.divinator.controller.MainController;
import io.github.divinator.datasource.entity.CallHistory;
import io.github.divinator.datasource.repository.CallHistoryRepository;
import io.github.divinator.exception.CallLogServiceException;
import io.github.divinator.history.CallHistoryData;
import io.github.divinator.history.CallHistoryInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@PropertySource({"classpath:application.properties"})
public class CallLogService {
    private final Logger LOG = LoggerFactory.getLogger(CallLogService.class);
    @Value("${application.scheduler.history:false}")
    private boolean historyCheck;
    private CallHistoryInformation historyInformation;
    private final MainController mainController;
    private final CallHistoryRepository callHistoryRepository;

    public CallLogService(MainController mainController, CallHistoryRepository callHistoryRepository) {
        this.mainController = mainController;
        this.callHistoryRepository = callHistoryRepository;

        try {
            this.historyInformation = this.readHistory(this.getHistoryFile());
        } catch (CallLogServiceException var4) {
            this.LOG.error(String.format("Журнал истории звонков не найден. %s", this.historyInformation), var4.getLocalizedMessage());
        }

    }

    public void createCallHistory(String phone, LocalDateTime dateTime, boolean manually) {
        this.callHistoryRepository.save(new CallHistory(phone, dateTime, manually));
    }

    public long getCountCallHistory() {
        return this.callHistoryRepository.count();
    }

    public CallHistory getLastCallHistory() {
        return this.callHistoryRepository.findFirstByOrderByCreateDateTimeDesc();
    }

    public CallHistory getCallHistory(LocalDateTime localDateTime) {
        return this.callHistoryRepository.findByDateTime(localDateTime);
    }

    public Iterable<CallHistory> getCallHistoryAfter(LocalDateTime localDateTime) {
        return this.callHistoryRepository.findAllByDateTimeAfter(localDateTime);
    }

    public Iterable<CallHistory> getCallHistoryBetween(LocalDateTime localDateTimeFrom, LocalDateTime localDateTimeTo) {
        return this.callHistoryRepository.findAllByDateTimeBetween(localDateTimeFrom, localDateTimeTo);
    }

    public CallHistory saveCallHistory(CallHistory callHistory) {
        return (CallHistory)this.callHistoryRepository.save(callHistory);
    }

    private File getHistoryFile() throws CallLogServiceException {
        File history = Paths.get(System.getenv("APPDATA"), "Avaya", "Avaya one-X Communicator", "history.xml").toFile();
        if (!history.exists()) {
            throw new CallLogServiceException(String.format("Файл \"%s\" не найден", history));
        } else {
            return history;
        }
    }

    private CallHistoryInformation readHistory(File historyFile) throws CallLogServiceException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(new Class[]{CallHistoryInformation.class});
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return (CallHistoryInformation)unmarshaller.unmarshal(historyFile);
        } catch (JAXBException var4) {
            this.LOG.error(var4.getLocalizedMessage());
            throw new CallLogServiceException("История звонков не прочитана.");
        }
    }

    @Scheduled(
            fixedRate = 1000L
    )
    public void checkHistory() {
        if (this.historyCheck) {
            try {
                CallHistoryInformation callHistoryInformationNew = this.readHistory(this.getHistoryFile());
                if (!callHistoryInformationNew.getNextSessionId().equals(this.historyInformation.getNextSessionId())) {
                    CallHistoryData call = (CallHistoryData)callHistoryInformationNew.getCallHistoryData().peek();
                    if (call.isIncoming()) {
                        LocalDateTime localDateTime = Instant.ofEpochSecond(Long.parseLong(call.getStartTime())).atZone(ZoneId.of("Europe/Moscow")).toLocalDateTime();
                        this.createCallHistory(call.getFirstContact().getPhoneNumber(), localDateTime, false);
                    }
                }

                this.historyInformation = callHistoryInformationNew;
            } catch (CallLogServiceException var4) {
                var4.printStackTrace();
            }
        }

    }
}