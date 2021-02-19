package io.github.divinator.history;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Класс описывает запись в журнале звонков Avaya One-X Communicator
 */
@XmlRootElement(name = "callHistoryData")
@XmlAccessorType(XmlAccessType.FIELD)
public class CallHistoryData {

    @XmlElement(name = "callHistoryId")
    private String id;

    @XmlElement(name = "remoteUser")
    @XmlElementWrapper(name = "remoteUsers")
    private List<Contact> contacts;

    @XmlElement(name = "startTime")
    private String startTime;

    @XmlElement(name = "duration")
    private int duration;

    @XmlElement(name = "type")
    private String type;

    @XmlElement(name = "incoming")
    private boolean isIncoming;

    @XmlElement(name = "missed")
    private boolean isMissed;

    /**
     * Метод возвращает Id записи о звонке.
     *
     * @return Id записи о звонке.
     */
    public String getId() {
        return id;
    }

    /**
     * Метод возвращает список контактов участвующих в звонке.
     *
     * @return Список контактов участвующих в звонке.
     */
    public List<Contact> getContacts() {
        return contacts;
    }

    /**
     * Метод возвращает первый контакт участвующий в звонке.
     *
     * @return Первый контакт участвующий в звонке.
     */
    public Contact getFirstContact() {
        return contacts.get(0);
    }

    /**
     * Метод возвращает начало звонка в UNIX-TIME.
     *
     * @return Начало звонка в UNIX-TIME.
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Метод возвращает конец звонка в UNIX-TIME.
     *
     * @return Конец звонка в UNIX-TIME
     */
    public String getEndTime() {
        return startTime + duration;
    }

    /**
     * Метод возвращает продолжительность звонка в секундах.
     *
     * @return Продолжительность звонка в секундах.
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Метод возвращает тип звонка.
     *
     * @return Тип звонка.
     */
    public String getType() {
        return type;
    }

    /**
     * Метод указывает является ли звонок входящим.
     *
     * @return В случае входящего звонка возвращается true, в случае исходящего звонка возвращается false.
     */
    public boolean isIncoming() {
        return isIncoming;
    }

    /**
     * Метод указывает является ли звонок пропущенным.
     *
     * @return В случае пропущенного звонка возвращается true, в случае исходящего звонка возвращается false.
     */
    public boolean isMissed() {
        return isMissed;
    }

    /**
     * Метод указывает является ли звонок командой.
     * @return В случае команды возвращается true, в противном случае возвращается false.
     */
    public boolean isCommand() {
        return !getContacts().get(0).isUser();
    }
}
