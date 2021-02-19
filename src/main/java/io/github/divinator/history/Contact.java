package io.github.divinator.history;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Класс описывает сущность вызывающего абонента в журнале звонков Avaya One-X Communicator.
 */
@XmlRootElement(name = "remoteUser")
@XmlAccessorType(XmlAccessType.FIELD)
public class Contact {

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "farEndAddress")
    private String phoneNumber;

    @XmlElement(name = "contactId")
    private String id;

    /**
     * Метод возвращает имя вызывающего абонента.
     *
     * @return Имя вызывающего абонента.
     */
    public String getName() {
        return name;
    }

    /**
     * Метод возвращает телефонный номер вызывающего абонента.
     *
     * @return Телефонный номер вызывающего абонента
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Метод возвращает Id номер контакта.
     *
     * @return Id номер контакта.
     */
    public String getId() {
        return id;
    }

    /**
     * Метод проверяет, что вызывающий абонент настоящий.
     *
     * @return В случае если вызывающий абонент настоящий настоящий возвращается true, в противном случае false.
     */
    public boolean isUser() {
        return !phoneNumber.contains("*") && !phoneNumber.contains("#");
    }
}
