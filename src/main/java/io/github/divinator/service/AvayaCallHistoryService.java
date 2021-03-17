package io.github.divinator.service;

import io.github.divinator.controller.MainController;
import io.github.divinator.datasource.entity.SettingsEntity;
import io.github.divinator.exception.CallLogServiceException;
import io.github.divinator.history.CallHistoryData;
import io.github.divinator.history.CallHistoryInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Класс описывает сервис для работы с журналом звонков Avaya OneX.
 */
@Service
@PropertySource({"classpath:application.properties"})
public class AvayaCallHistoryService {

    private final Logger LOG = LoggerFactory.getLogger(AvayaCallHistoryService.class);
    private final SettingsService settingsService;
    private final MainController mainController;

    private String nextSessionId;

    public AvayaCallHistoryService(
            SettingsService settingsService,
            MainController mainController) {
        this.settingsService = settingsService;
        this.mainController = mainController;

        try {
            this.nextSessionId = this.getCallHistoryInformation().getNextSessionId();
        } catch (CallLogServiceException e) {
            this.LOG.error("Журнал истории звонков не найден.");
        }
    }

    /**
     * Метод возвращает историю звонков из файла "history.xml" программы Avaya one-X Communicator.
     *
     * @return История звонков.
     * @throws CallLogServiceException В случае если история не прочитана.
     */
    public CallHistoryInformation getCallHistoryInformation() throws CallLogServiceException {
        File historyFile = getHistoryFile();

        if (historyFile.canRead()) {
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(CallHistoryInformation.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                return (CallHistoryInformation) unmarshaller.unmarshal(historyFile);
            } catch (Exception e) {
                throw new CallLogServiceException("История звонков не прочитана.");
            }
        }

        throw new CallLogServiceException("История звонков не прочитана.");
    }

    /**
     * Метод описывает задание для просмотра файла "history.xml" программы Avaya one-X Communicator на наличие изменений.
     */
    @Scheduled(fixedRate = 1000L)
    public void follow() {
        if (Boolean.parseBoolean((String) settingsService.getSettings("application.history.follow").getValue())) {
            try {
                CallHistoryInformation callHistoryInformation = this.getCallHistoryInformation();

                if (!this.nextSessionId.equals(callHistoryInformation.getNextSessionId())) {

                    if (!callHistoryInformation.getLast().isCommand()) {
                        CallHistoryData lastIncoming = callHistoryInformation.getLastIncoming();

                        mainController.updatePhone(lastIncoming.getFirstContact().getPhoneNumber());

                        ZonedDateTime startTime = Instant.ofEpochSecond(
                                Long.parseLong(lastIncoming.getStartTime())).atZone(ZoneId.of("UTC"));

                        mainController.updateDateTime(
                                startTime.withZoneSameInstant(
                                        ZoneId.of((String) settingsService.getSettings("application.zone").getValue())));

                        mainController.updateManually(false);

                        LOG.info(String.format("Последний входящий звонок: %s", lastIncoming.getFirstContact().getPhoneNumber()));
                    }

                    this.nextSessionId = callHistoryInformation.getNextSessionId();
                }

            } catch (CallLogServiceException e) {
                LOG.error(e.getLocalizedMessage());
                SettingsEntity follow = this.settingsService.getSettings("application.history.follow");
                follow.setValue(false);
                settingsService.setSettings(follow);
            }
        }
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
}