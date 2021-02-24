package io.github.divinator.controller;

import io.github.divinator.config.AppConfig;
import io.github.divinator.datasource.entity.CallHistoryEntity;
import io.github.divinator.datasource.entity.CallHistoryPojo;
import io.github.divinator.datasource.entity.CatalogDetails;
import io.github.divinator.datasource.entity.CatalogSubtype;
import io.github.divinator.javafx.FXMLController;
import io.github.divinator.javafx.view.FxLoggerArea;
import io.github.divinator.service.CSVService;
import io.github.divinator.service.CallLogService;
import io.github.divinator.service.CatalogService;
import io.github.divinator.service.SettingsService;
import io.github.divinator.tray.AppTrayMouseListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.awt.MenuItem;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.time.*;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@FXMLController(value = "mainController", view = "main.fxml")
public class MainController implements Initializable {

    @FXML private AnchorPane rootPane;
    @FXML private Text username, countcalls;
    @FXML private TextArea logArea;
    @FXML private TextField phone, tid, title, settingsLogin;
    @FXML private TitledPane history;
    @FXML private DatePicker date, callhistorydate;
    @FXML private ComboBox<String> hour, minute, second, subtype, details;
    @FXML private TableView<CallHistoryPojo> callhistorytable;
    @FXML private Circle indicator;

    private final Logger LOG = LoggerFactory.getLogger(MainController.class);
    private final SettingsService settingsService;
    private final ApplicationContext applicationContext;
    private final CatalogService catalogService;
    private final CSVService csvService;
    private final ZoneId zoneId;
    private long countHistory;
    private ResourceBundle resources;

    public MainController(
            SettingsService settingsService,
            ApplicationContext applicationContext,
            CatalogService catalogService,
            CSVService csvService
    ) {
        this.settingsService = settingsService;
        this.csvService = csvService;
        this.applicationContext = applicationContext;
        this.catalogService = catalogService;
        this.zoneId = ZoneId.systemDefault();
    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     *                  the root object was not localized.
     */
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        //this.initializeLogger();
        this.initializeUsername();
        this.initializeDateTime();
        this.initializeSubtype();
        this.initializeCountCallHistory();
        this.initializeCallHistoryDate();
        this.initializeCallHistoryTable();
        this.initializeScheduler();
    }

    /**
     * <h2>Метод проводит инициализацию вывода логов приложения.</h2>
     */
    private void initializeLogger() {
        String loglevel = applicationContext.getBean(AppConfig.class).getLoglevel();

        if (loglevel.length() > 0 && !loglevel.equals("OFF")) {
            FxLoggerArea.in(this.logArea);
        }
    }

    /**
     * Метод инициализирует количество записей в журнале.
     */
    private void initializeCountCallHistory() {
        this.countHistory = this.applicationContext.getBean(CallLogService.class).getCountCallHistory();
    }

    /**
     * Метод инициализирует значения даты, часа и минут на форме.
     */
    private void initializeDateTime() {
        this.hour.setItems(FXCollections.observableArrayList(createUnitTimeRange(24)));
        this.minute.setItems(FXCollections.observableArrayList(createUnitTimeRange(60)));
        this.updateDateTime();
    }

    /**
     * Метод инициализирует имя пользователя из ОС.
     */
    private void initializeUsername() {
        String login = (String) settingsService.getSettings("user.name").getValue();
        this.username.setText(login);
        this.settingsLogin.setText(login);
    }

    /**
     * Метод инициализирует список тематик для Combobox "Подтип".
     */
    private void initializeSubtype() {
        List<String> collect = ((List<CatalogSubtype>) this.catalogService.getSubtypes())
                .stream()
                .sorted()
                .map(CatalogSubtype::getName)
                .collect(Collectors.toList());
        this.subtype.setItems(FXCollections.observableArrayList(collect));
        this.subtype.setVisibleRowCount(collect.size());
    }

    private void initializeScheduler() {
        CallLogService callLogService = this.applicationContext.getBean(CallLogService.class);

        Runnable runnable = () -> {

            try {
                if (this.countHistory < callLogService.getCountCallHistory()) {
                    CallHistoryEntity lastCallHistoryEntity = callLogService.getLastCallHistoryEntity();

                    if (!lastCallHistoryEntity.isManually()) {
                        this.phone.setText(lastCallHistoryEntity.getPhone());
                        ZonedDateTime dateTime = ZonedDateTime.of(lastCallHistoryEntity.getDateTime(), ZoneId.of((String) settingsService.getSettings("application.db.zone").getValue()));
                        ZonedDateTime systemDateTime = dateTime.withZoneSameInstant(zoneId);
                        this.date.setValue(systemDateTime.toLocalDate());
                        this.hour.setValue(convertUnitTimeFromInteger(systemDateTime.getHour()));
                        this.minute.setValue(convertUnitTimeFromInteger(systemDateTime.getMinute()));
                        this.second.setValue(convertUnitTimeFromInteger(systemDateTime.getSecond()));

                        if (lastCallHistoryEntity.getSubtypeId() != 0) {
                            this.subtype.getSelectionModel().select(
                                    catalogService.getSubtypeById(lastCallHistoryEntity.getSubtypeId())
                                            .get()
                                            .getName()
                            );
                        }

                        this.LOG.info(String.format("[%s %s:%s] Входящий звонок: %s. Подтип:%s",
                                dateTime.toLocalDate(),
                                dateTime.getHour(),
                                dateTime.getMinute(),
                                lastCallHistoryEntity.getPhone(),
                                subtype.getValue()
                        ));
                    }

                    this.updateCountHistory();
                }
            } catch (IllegalStateException e) {
                LOG.error("База данных занята");
            }
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(runnable, 0L, 1L, TimeUnit.SECONDS);
    }

    private void initializeCallHistoryDate() {
        this.callhistorydate.setValue(LocalDate.now());
    }

    private void initializeCallHistoryTable() {
        TableColumn<CallHistoryPojo, String> dateCol = new TableColumn<>();
        dateCol.setText(this.resources.getString("gui.tab.0.label.date"));
        dateCol.setCellValueFactory(new PropertyValueFactory("date"));
        TableColumn<CallHistoryPojo, String> phoneCol = new TableColumn();
        phoneCol.setText(this.resources.getString("gui.tab.0.label.phone"));
        phoneCol.setCellValueFactory(new PropertyValueFactory("phone"));
        TableColumn<CallHistoryPojo, String> subtypeCol = new TableColumn();
        subtypeCol.setText(this.resources.getString("gui.tab.0.label.subtype"));
        subtypeCol.setCellValueFactory(new PropertyValueFactory("subtype"));
        TableColumn<CallHistoryPojo, String> detailsCol = new TableColumn();
        detailsCol.setText(this.resources.getString("gui.tab.0.label.details"));
        detailsCol.setCellValueFactory(new PropertyValueFactory("details"));
        TableColumn<CallHistoryPojo, String> tidCol = new TableColumn();
        tidCol.setText(this.resources.getString("gui.tab.0.label.tid"));
        tidCol.setCellValueFactory(new PropertyValueFactory("tid"));
        TableColumn<CallHistoryPojo, String> titleCol = new TableColumn();
        titleCol.setText(this.resources.getString("gui.tab.0.label.title"));
        titleCol.setCellValueFactory(new PropertyValueFactory("title"));
        this.callhistorytable.getColumns().addAll(new TableColumn[]{dateCol, phoneCol, tidCol, subtypeCol, detailsCol, titleCol});
        this.loadCallHistoryTable();
    }

    private void loadCallHistoryTable() {
        CallLogService callLogService = this.applicationContext.getBean(CallLogService.class);
        LocalDateTime localDateTimeFrom = (this.callhistorydate.getValue()).atStartOfDay();
        LocalDateTime localDateTimeTo = localDateTimeFrom.toLocalDate().atTime(LocalTime.MAX);

        List<CallHistoryPojo> collect = ((List<CallHistoryEntity>) callLogService.getCallHistoryBetween(localDateTimeFrom, localDateTimeTo))
                .stream()
                .map(callHistory -> new CallHistoryPojo(callHistory, catalogService))
                .collect(Collectors.toList());

        this.callhistorytable.getItems().setAll(FXCollections.observableArrayList(collect));
        this.countcalls.setText(String.valueOf(this.callhistorytable.getItems().size()));
    }

    private void updateCountHistory() {
        this.countHistory = (this.applicationContext.getBean(CallLogService.class)).getCountCallHistory();
        LOG.info("Счетчик количества записей обновлен.");
    }

    private void updatePhone() {
        this.phone.setText("");
    }

    /**
     * Метод обновляет значение даты, часов и минут на форме.
     */
    public void updateDateTime() {
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        this.date.setValue(now.toLocalDate());
        this.hour.setValue(convertUnitTimeFromInteger(now.getHour()));
        this.minute.setValue(convertUnitTimeFromInteger(now.getMinute()));
        //this.second.setValue(now.getSecond());
    }

    private void updateSubtype() {
        this.subtype.setValue("");
    }

    private void updateDetails() {
        this.details.setValue("");
    }

    private void updateTid() {
        this.tid.setText("");
    }

    private void updateTitle() {
        this.title.setText("");
    }

    private void setOkStatus() {
    }

    private void setNewStatus() {
        this.indicator.setFill(Color.GREEN);
        this.indicator.setEffect(new InnerShadow());
    }

    private void setOffStatus() {
    }

    /**
     * Метод указывает на событие при нажатии на кнопку "Обновить".
     */
    @FXML
    public void onMouseClickedUpdate() {
        this.updatePhone();
        this.updateDateTime();
        this.updateSubtype();
        this.updateDetails();
        this.updateTid();
        this.updateTitle();
    }

    /**
     * Метод указывает на событие при завершении выбора поля "Подтип".
     */
    @FXML
    public void onHidingSubtype() {
        this.updateDetails();
        String noneBorder = "-fx-border: none;";
        if (this.subtype.getValue() != null) {
            this.subtype.setStyle(noneBorder);
        }
    }

    @FXML
    public void onSelectDate() {
        this.loadCallHistoryTable();
    }

    @FXML
    private void onInputPhone() {
        String noneBorder = "-fx-border: none;";
        if (!this.phone.getText().isEmpty()) {
            this.phone.setStyle(noneBorder);
        }
    }

    @FXML
    public void onShowingDetails() {
        String noneBorder = "-fx-border: none;";

        if (this.subtype.getValue() != null) {
            CatalogSubtype byName = this.catalogService.getSubtypeByName((String) this.subtype.getValue());
            if (byName != null) {
                List<String> collect = byName.getDetails().stream()
                        .sorted()
                        .map(CatalogDetails::getName)
                        .collect(Collectors.toList());

                this.details.setItems(FXCollections.observableArrayList(collect));
            }

            this.details.setStyle(noneBorder);
        }

    }

    @FXML
    private void onInputTID() {
        String noneBorder = "-fx-border: none;";
        if (!this.tid.getText().isEmpty()) {
            this.tid.setStyle(noneBorder);
        }
    }

    /**
     * Метод выполняет валидацию заполнения полей ввода.
     *
     * @return true в случае если все заполнено корректно, в противном случае false;
     */
    private boolean validationInputFields() {
        String redBorder = "-fx-border-color: red;";

        try {
            if (this.phone.getText() == null || this.phone.getText().length() == 0) {
                this.phone.setStyle(redBorder);
                LOG.error("Не заполнен номер телефона.");
            } else if (this.subtype.getValue() == null || this.subtype.getValue().isEmpty()) {
                this.subtype.setStyle(redBorder);
                LOG.error("Не заполнен \"Подтип\".");
            } else if (catalogService.getSubtypeByName(this.subtype.getValue()).getDetails().size() > 0 && (this.details.getValue() == null || this.details.getValue().isEmpty())) {
                this.details.setStyle(redBorder);
                LOG.error("Не заполнены \"Детали\".");
            } else if (this.tid.getText().isEmpty()) {
                this.tid.setStyle(redBorder);
                LOG.error("Не заполнен \"TID\".");
            } else {
                return true;
            }
        } catch (NullPointerException e) {
            LOG.error("Ошибка валидации формы.");
        }

        return false;
    }

    /**
     * Метод указывает на событие при нажатии на кнопку "Сохранить"
     */
    @FXML
    public void onActionSave() {
        if (validationInputFields()) {

            ZonedDateTime dateTime = (this.date.getValue()).atTime(
                    Integer.parseInt(String.valueOf(this.hour.getValue())),
                    Integer.parseInt(String.valueOf(this.minute.getValue())),
                    Integer.parseInt(String.valueOf((this.second.getValue() != null ? this.second.getValue() : "00")))
            ).atZone(ZoneId.systemDefault())
                    .withZoneSameInstant(ZoneId.of(
                            (String) settingsService.getSettings("application.db.zone").getValue()
                    ));

            CallLogService callLogService = this.applicationContext.getBean(CallLogService.class);

            CallHistoryEntity callHistoryEntity = callLogService.getCallHistoryEntity(dateTime.toLocalDateTime());

            if (callHistoryEntity == null) {
                callHistoryEntity = new CallHistoryEntity(Instant.now().atZone(zoneId).toLocalDateTime(), true);
            }

            callHistoryEntity.setPhone(phone.getText());
            callHistoryEntity.setDateTime(dateTime.toLocalDateTime());

            CatalogSubtype subtype = this.catalogService.getSubtypeByName(this.subtype.getValue());
            CatalogDetails editDetails = subtype.getDetails()
                    .stream()
                    .filter(o -> details.getValue().equals(o.getName()))
                    .findFirst().orElse(new CatalogDetails(null));

            callHistoryEntity.setSubtypeId(subtype.getSubtypeId());
            callHistoryEntity.setDetailsId(editDetails.getId());
            callHistoryEntity.setTid(tid.getText());
            callHistoryEntity.setTitle(title.getText());

            callLogService.saveCallHistory(callHistoryEntity);

            this.loadCallHistoryTable();
            this.onMouseClickedUpdate();
        }
    }

    /**
     * Метод указывает на событие при нажатии кнопки "Выгрузить".
     */
    @FXML
    private void onExportHistory() {
        File export = this.directoryChooser(resources.getString("gui.tab.1.button.export.dialog.title"));
        if (export != null) {
            CallLogService callLogService = this.applicationContext.getBean(CallLogService.class);

            LocalDateTime localDateTimeFrom = this.callhistorydate.getValue().atStartOfDay();
            LocalDateTime localDateTimeTo = localDateTimeFrom.toLocalDate().atTime(LocalTime.MAX);

            Iterable<CallHistoryEntity> callHistoryBetween = callLogService.getCallHistoryBetween(localDateTimeFrom, localDateTimeTo);
            File file = new File(export.getPath().concat("\\export.csv"));
            csvService.write(file, callHistoryBetween);
        }
    }

    /**
     * Метод инициализации окна выбора директории.
     *
     * @param title Название заголовка окна выбора директории.
     * @return Выбранная директория.
     */
    private File directoryChooser(String title) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(title);
        return directoryChooser.showDialog(this.rootPane.getScene().getWindow());
    }

    /**
     * Метод генерирует список значений единиц времени.
     *
     * @param endExclusive Конечное значение(исключительно).
     * @return Список значений единиц времени
     */
    private List<String> createUnitTimeRange(int endExclusive) {
        return IntStream.range(0, endExclusive)
                .boxed()
                .map(this::convertUnitTimeFromInteger)
                .collect(Collectors.toList());
    }

    /**
     * Метод конвертирует тип единицы времени из Integer в String.
     *
     * @param unitTime Единица времени.
     * @return Единица времени преобразованная к типу String.
     */
    private String convertUnitTimeFromInteger(Integer unitTime) {
        return (String.valueOf(unitTime).length() < 2) ? String.format("0%s", unitTime) : String.valueOf(unitTime);
    }

    /**
     * Метод конвертирует тип единицы времени из String в Integer.
     *
     * @param unitTime Единица времени.
     * @return Единица времени преобразованная к типу Integer.
     */
    private Integer convertUnitTimeFromString(String unitTime) {
        return Integer.parseInt(unitTime);
    }

    private void initializeTray() {
        System.setProperty("java.awt.headless", "false");
        if (SystemTray.isSupported()) {
            this.LOG.info("Tray supported");
            SystemTray tray = SystemTray.getSystemTray();
            URL resource = this.getClass().getClassLoader().getResource("img/ico_24_24.png");
            Image image = Toolkit.getDefaultToolkit().getImage(resource);
            PopupMenu popup = new PopupMenu();
            MenuItem showAppItem = new MenuItem(this.resources.getString("gui.button.open"));
            MenuItem exitAppItem = new MenuItem(this.resources.getString("gui.button.exit"));
            MenuItem addEntry = new MenuItem(this.resources.getString("gui.button.new"));
            exitAppItem.addActionListener((e) -> {
                System.exit(0);
            });
            popup.insert(showAppItem, 0);
            popup.insert(addEntry, 1);
            popup.addSeparator();
            popup.insert(exitAppItem, 3);
            TrayIcon trayIcon = new TrayIcon(image, this.resources.getString("app.name"), popup);
            trayIcon.setImageAutoSize(true);
            trayIcon.addMouseListener(new AppTrayMouseListener());

            try {
                tray.add(trayIcon);
            } catch (AWTException var10) {
                this.LOG.error("TrayIcon could not be added.");
            }
        } else {
            this.LOG.info("Tray not supported");
        }
    }
}