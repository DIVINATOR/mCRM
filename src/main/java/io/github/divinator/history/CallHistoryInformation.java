package io.github.divinator.history;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Класс описывает журнал звонков Avaya One-X Communicator из файла "history.xml".
 */
@XmlRootElement(name = "CallHistoryInformation")
public class CallHistoryInformation {

    @XmlElement(name = "callHistoryData")
    private Stack<CallHistoryData> callHistoryDataList;

    @XmlElement(name = "nextSessionId")
    private String nextSessionId;

    /**
     * Метод возвращает стек звонков из журнала.
     *
     * @return Стек звонков из журнала
     */
    public Stack<CallHistoryData> getCallHistoryData() {
        return callHistoryDataList;
    }

    /**
     * Метод возвращает последний звонок.
     *
     * @return Последний звонок.
     */
    public CallHistoryData getLast() {
        return getCallHistoryData().peek();
    }

    /**
     * Метод возвращает последний входящий звонок.
     *
     * @return Последний входящий звонок.
     */
    public CallHistoryData getLastIncoming() {
        return getCallHistoryData().stream()
                .filter(callHistoryData -> callHistoryData.isIncoming() && !callHistoryData.isCommand())
                .collect(Collectors.toCollection(Stack<CallHistoryData>::new))
                .peek();
    }

    /**
     * Метод возвращает Id следующего звонка для записи в журнал.
     *
     * @return Id следующего звонка для записи в журнал.
     */
    public String getNextSessionId() {
        return nextSessionId;
    }
}
