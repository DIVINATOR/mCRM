package io.github.divinator.service;

import io.github.divinator.controller.MainController;
import io.github.divinator.datasource.entity.CallHistoryEntity;
import io.github.divinator.datasource.repository.CallHistoryRepository;
import io.github.divinator.exception.CallLogServiceException;
import io.github.divinator.history.CallHistoryInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Класс описывает сервис для работы с журналом звонков.
 */
@Service
@PropertySource({"classpath:application.properties"})
public class CallLogService {

    @Value("${application.scheduler.history:false}")
    private boolean historyCheck;
    private CallHistoryInformation historyInformation;
    private final Logger LOG = LoggerFactory.getLogger(CallLogService.class);
    private final MainController mainController;
    private final CallHistoryRepository callHistoryRepository;
    private final ZoneId dbZoneId;

    public CallLogService(
            MainController mainController,
            CallHistoryRepository callHistoryRepository,
            @Value("${application.db.zone:Europe/Moscow}") String zoneIdValue
    ) {
        this.mainController = mainController;
        this.callHistoryRepository = callHistoryRepository;
        this.dbZoneId = ZoneId.of(zoneIdValue);

        try {
            this.historyInformation = this.readHistory();
        } catch (CallLogServiceException var4) {
            this.LOG.error(String.format("Журнал истории звонков не найден. %s", this.historyInformation), var4.getLocalizedMessage());
        }
    }

    public void createCallHistoryEntity(String phone, ZonedDateTime dateTime, boolean manually) {
        this.createCallHistoryEntity(phone, dateTime, manually, 0);
    }

    /**
     * Метод создает запись в таблице журнала звонков.
     *
     * @param phone     Номер телефона
     * @param dateTime
     * @param manually
     * @param subtypeId
     */
    public void createCallHistoryEntity(String phone, ZonedDateTime dateTime, boolean manually, long subtypeId) {
        CallHistoryEntity save = this.callHistoryRepository.save(
                new CallHistoryEntity(
                        phone,
                        ZonedDateTime.now(dbZoneId).toLocalDateTime(),
                        dateTime.toLocalDateTime(),
                        manually,
                        subtypeId
                ));
    }

    public long getCountCallHistory() {
        return this.callHistoryRepository.count();
    }

    public CallHistoryEntity getLastCallHistoryEntity() {
        return this.callHistoryRepository.findFirstByOrderByCreateDateTimeDesc();
    }

    public CallHistoryEntity getCallHistoryEntity(LocalDateTime localDateTime) {
        return this.callHistoryRepository.findByDateTime(localDateTime);
    }

    public Iterable<CallHistoryEntity> getCallHistoryBetween(LocalDateTime localDateTimeFrom, LocalDateTime localDateTimeTo) {
        return this.callHistoryRepository.findAllByDateTimeBetween(localDateTimeFrom, localDateTimeTo);
    }

    public CallHistoryEntity saveCallHistory(CallHistoryEntity callHistoryEntity) {
        return this.callHistoryRepository.save(callHistoryEntity);
    }

    /**
     * Метод возвращает файл "history.xml" программы Avaya one-X Communicator.
     *
     * @return файл "history.xml"
     * @throws CallLogServiceException В случае если файл не найден.
     */
    private File getHistoryFile() throws CallLogServiceException {
        File history = Paths.get(System.getenv("APPDATA"), "Avaya", "Avaya one-X Communicator", "history.xml").toFile();
        if (history.exists()) {
            return history;
        } else {
            throw new CallLogServiceException(String.format("Файл \"%s\" не найден", history));
        }
    }

    /**
     * Метод возвращает историю звонков из файла "history.xml" программы Avaya one-X Communicator.
     *
     * @return История звонков.
     * @throws CallLogServiceException В случае если история не прочитана.
     */
    public CallHistoryInformation readHistory() throws CallLogServiceException {

        File historyFile = getHistoryFile();

        if (historyFile.canRead()) {
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(CallHistoryInformation.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                return (CallHistoryInformation) unmarshaller.unmarshal(historyFile);
            } catch (JAXBException var4) {
                this.LOG.error(var4.getLocalizedMessage());
                throw new CallLogServiceException("История звонков не прочитана.");
            }
        }
        throw new CallLogServiceException("История звонков не прочитана.");
    }
}