package io.github.divinator.service;

import io.github.divinator.history.CallHistoryInformation;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.nio.file.Paths;

public class AvayaCallHistoryServiceTest {

    //@Test
    public void isFileHistoryExists() {
        File file = Paths.get(System.getenv("APPDATA"), "Avaya", "Avaya one-X Communicator", "history.xml").toFile();
        Assert.assertTrue("Файл history.xml не найден", file.exists());
    }

    @Test
    public void readFileHistory() {
        File file = Paths.get(System.getenv("APPDATA"), "Avaya", "Avaya one-X Communicator", "history.xml").toFile();

        try {
            CallHistoryInformation callHistoryInformation = readFileHistory(file);
            Assert.assertNotNull(String.format("Информация %s не загружена", callHistoryInformation),  callHistoryInformation);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void readTempFileHistory() {
        File file = new File(getClass().getClassLoader().getResource("history.xml").getFile());
        try {
            CallHistoryInformation callHistoryInformation = readFileHistory(file);
            Assert.assertNotNull(String.format("Информация %s не загружена", callHistoryInformation),  callHistoryInformation);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void writeFileHistory() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(CallHistoryInformation.class);
        CallHistoryInformation information = new CallHistoryInformation();
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(information, System.out);
    }

    private CallHistoryInformation readFileHistory(File history) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(CallHistoryInformation.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return  (CallHistoryInformation) unmarshaller.unmarshal(history);
    }
}