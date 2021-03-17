package io.github.divinator.service;

import io.github.divinator.datasource.entity.CallHistoryPojo;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class CSVService {

    private final Logger LOG = LoggerFactory.getLogger(CSVService.class);

    /**
     * Метод записывает в файл csv историю звонков.
     *
     * @param csv Файл формата csv.
     * @param values История звонков.
     */
    public void write(File csv, Iterable<CallHistoryPojo> values) {
        try (PrintWriter out = new PrintWriter(csv, "Cp1251")) {
            try (CSVPrinter csvPrinter = new CSVPrinter(
                    out,
                    CSVFormat.EXCEL
                            .withHeader("Дата", "Время", "Дата+Время", "Подтип", "Подтип", "Детали", "TID", "Описание", "Фиксирование сбоя Avaya one-x")
                            .withQuoteMode(QuoteMode.ALL)
                            .withDelimiter(';')
            )) {
                values.forEach(callHistory -> {
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy H:m:s");
                    LocalDateTime dateTime = LocalDateTime.parse(callHistory.getDateTime(), dateTimeFormatter);
                    String date = dateTime.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    String time = String.format("%s:%s", String.valueOf(dateTime.getHour()).length() == 1 ? String.format("0%s", dateTime.getHour()) : dateTime.getHour(), String.valueOf(dateTime.getMinute()).length() == 1 ? String.format("0%s", dateTime.getMinute()) : dateTime.getMinute());
                    String type = callHistory.getType();
                    String subtype = callHistory.getSubtype();
                    String details = callHistory.getDetails();
                    String tid = (callHistory.getTid().isEmpty() || callHistory.getTid().length() == 0) ? null : callHistory.getTid();
                    String title = String.format(
                            "Телефон:%s%s",
                            callHistory.getPhone(),
                            (callHistory.getTitle().isEmpty() || callHistory.getTitle().length() == 0) ? null : " \t" + callHistory.getTitle()
                    );
                    String error = "";
                    try {
                        csvPrinter.printRecord(
                                date,
                                time,
                                callHistory.getDateTime(),
                                type,
                                subtype,
                                details,
                                tid,
                                title,
                                error
                        );
                    } catch (IOException e) {
                        //e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            LOG.error(String.format("Ошибка экспорта журнала звонков (\"%s\")", e.getMessage()));
        }
    }
}
