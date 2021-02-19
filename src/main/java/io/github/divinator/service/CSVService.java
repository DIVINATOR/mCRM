package io.github.divinator.service;

import io.github.divinator.datasource.entity.CallHistory;
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

    private final CatalogService catalogService;
    private final Logger LOG = LoggerFactory.getLogger(CSVService.class);

    public CSVService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    /**
     * Метод записывает в файл csv историю звонков.
     *
     * @param csv Файл формата csv.
     * @param values История звонков.
     */
    public void write(File csv, Iterable<CallHistory> values) {
        try (PrintWriter out = new PrintWriter(csv, "Cp1251")) {
            try (CSVPrinter csvPrinter = new CSVPrinter(
                    out,
                    CSVFormat.EXCEL
                            .withHeader("Дата", "время", "Подтип", "Детали", "TID", "Описание", "Фиксирование сбоя Avaya one-x")
                            .withQuoteMode(QuoteMode.ALL)
                            .withDelimiter(';')
            )) {
                values.forEach(callHistory -> {
                    try {
                        LocalDateTime dateTime = callHistory.getDateTime();
                        String date = dateTime.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                        String time = String.format("%s:%s", String.valueOf(dateTime.getHour()).length() == 1 ? String.format("0%s", dateTime.getHour()) : dateTime.getHour(), String.valueOf(dateTime.getMinute()).length() == 1 ? String.format("0%s", dateTime.getMinute()) : dateTime.getMinute());
                        String subtype = (catalogService.getSubtypeById(callHistory.getSubtypeId()).get()).getName();
                        String details = (catalogService.getCatalogDetailsById(callHistory.getDetailsId()).get()).getName();
                        String tid = callHistory.getTid();
                        String title = String.format("Телефон:%s", callHistory.getPhone());
                        String error = "";
                        csvPrinter.printRecord(date, time, subtype, details, tid, title, error);
                    } catch (IOException e) {
                        LOG.error(String.format("Ошибка записи звонка в файл %s (\"%s\")", csv, e.getMessage()));
                    }
                });
            }
        } catch (IOException e) {
            LOG.error(String.format("Ошибка экспорта журнала звонков (\"%s\")", e.getMessage()));
        }
    }
}
