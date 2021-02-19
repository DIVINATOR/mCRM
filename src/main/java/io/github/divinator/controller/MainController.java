package io.github.divinator.controller;

import io.github.divinator.config.AppConfig;
import io.github.divinator.datasource.entity.CallHistory;
import io.github.divinator.datasource.entity.CallHistoryPojo;
import io.github.divinator.datasource.entity.CatalogDetails;
import io.github.divinator.datasource.entity.CatalogSubtype;
import io.github.divinator.javafx.FXMLController;
import io.github.divinator.javafx.view.FxLoggerArea;
import io.github.divinator.service.CSVService;
import io.github.divinator.service.CallLogService;
import io.github.divinator.service.CatalogService;
import io.github.divinator.tray.AppTrayMouseListener;
import javafx.application.Platform;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@FXMLController(value = "mainController", view = "main.fxml")
public class MainController implements Initializable {

    @FXML
    private Text username;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private TextArea logArea;
    @FXML
    private TitledPane history;
    @FXML
    private TextField phone;
    @FXML
    private DatePicker date;
    @FXML
    private ComboBox<Integer> hour;
    @FXML
    private ComboBox<Integer> minute;
    @FXML
    private ComboBox<Integer> second;
    @FXML
    ComboBox<String> subtype;
    @FXML
    ComboBox<String> details;
    @FXML
    TextField tid;
    @FXML
    TextField title;
    @FXML
    TableView<CallHistoryPojo> callhistorytable;
    @FXML
    Text countcalls;
    @FXML
    DatePicker callhistorydate;
    @FXML
    Circle indicator;

    private final Logger LOG = LoggerFactory.getLogger(MainController.class);
    private final AppConfig config;
    private final ApplicationContext applicationContext;
    private final CatalogService catalogService;
    private final CSVService csvService;
    private long countHistory;
    private ResourceBundle resources;

    public MainController(AppConfig config, ApplicationContext applicationContext, CatalogService catalogService, CSVService csvService) {
        this.config = config;
        this.csvService = csvService;
        this.applicationContext = applicationContext;
        this.catalogService = catalogService;
    }

    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        this.initializeLogger();
        this.initializeUsername();
        this.initializeDate();
        this.initializeTime();
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
     * Метод инициализирует текущую дату.
     */
    private void initializeDate() {
        this.date.setValue(LocalDate.now());
    }

    /**
     * Метод инициализирует значения для выбора времени.
     * <p>Часы и минуты.
     */
    private void initializeTime() {
        List<Integer> hourCollection = IntStream.range(0, 24).boxed().collect(Collectors.toList());
        List<Integer> minuteCollection = IntStream.range(0, 60).boxed().collect(Collectors.toList());
        this.hour.setItems(FXCollections.observableArrayList(hourCollection));
        this.minute.setItems(FXCollections.observableArrayList(minuteCollection));
        this.updateTime();
    }

    /**
     * Метод инициализирует имя пользователя из ОС.
     */
    private void initializeUsername() {
        this.username.setText(System.getProperty("user.name"));
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
    }

    private void initializeScheduler() {
        final CallLogService callLogService = this.applicationContext.getBean(CallLogService.class);

        Runnable runnable = () -> {
            if (this.countHistory < callLogService.getCountCallHistory()) {

                CallHistory lastCallHistory = callLogService.getLastCallHistory();

                this.LOG.info(String.format("Входящий звонок: %s", lastCallHistory.getPhone()));

                if (!lastCallHistory.isManually()) {
                    this.phone.setText(lastCallHistory.getPhone());
                    this.date.setValue(lastCallHistory.getDateTime().toLocalDate());
                    this.hour.setValue(lastCallHistory.getDateTime().getHour());
                    this.minute.setValue(lastCallHistory.getDateTime().getMinute());
                    this.second.setValue(lastCallHistory.getDateTime().getSecond());

                    if (lastCallHistory.getSubtypeId() != 0) {
                        this.subtype.getSelectionModel().select(catalogService.getSubtypeById(lastCallHistory.getSubtypeId()).get().getName());
                    }
                }

                this.updateCountHistory();
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

        List<CallHistoryPojo> collect = ((List<CallHistory>) callLogService.getCallHistoryBetween(localDateTimeFrom, localDateTimeTo))
                .stream()
                .map(callHistory -> new CallHistoryPojo(callHistory, catalogService))
                .collect(Collectors.toList());

        this.callhistorytable.getItems().setAll(FXCollections.observableArrayList(collect));
        this.countcalls.setText(String.valueOf(this.callhistorytable.getItems().size()));
    }

    private void updateCountHistory() {
        this.countHistory = (this.applicationContext.getBean(CallLogService.class)).getCountCallHistory();
    }

    private void updatePhone() {
        this.phone.setText("");
    }

    private void updateDate() {
        this.initializeDate();
    }

    private void updateTime() {
        LocalDateTime now = LocalDateTime.now();
        this.hour.setValue(now.getHour());
        this.minute.setValue(now.getMinute());
        this.second.setValue(now.getSecond());
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
        this.updateDate();
        this.updateTime();
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
    public void onHidingSelectDate() {

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
     * Метод указывает на событие при нажатии на кнопку "Сохранить"
     */
    @FXML
    public void onActionSave() {
        String redBorder = "-fx-border-color: red;";
        if (this.phone.getText().isEmpty()) {
            this.phone.setStyle(redBorder);
        } else if (this.subtype.getValue() == null || this.subtype.getValue().isEmpty()) {
            this.subtype.setStyle(redBorder);
        } else if (this.details.getValue() == null || this.details.getValue().isEmpty()) {
            this.details.setStyle(redBorder);
        } else if (this.tid.getText().isEmpty()) {
            this.tid.setStyle(redBorder);
        } else {
            LocalDateTime localDateTime = (this.date.getValue()).atTime(
                    Integer.parseInt(String.valueOf(this.hour.getValue())),
                    Integer.parseInt(String.valueOf(this.minute.getValue())),
                    Integer.parseInt(String.valueOf(this.second.getValue()))
            );

            CallLogService callLogService = this.applicationContext.getBean(CallLogService.class);

            CallHistory lastCallHistory = null;
            if (callLogService.getCallHistory(localDateTime) != null) {
                lastCallHistory = callLogService.getLastCallHistory();
            } else {
                lastCallHistory = new CallHistory();
                lastCallHistory.setManually(true);
            }

            lastCallHistory.setPhone(this.phone.getText());
            lastCallHistory.setDateTime(localDateTime);

            CatalogSubtype subtypeByName = this.catalogService.getSubtypeByName(this.subtype.getValue());

            CatalogDetails editDetails = subtypeByName.getDetails()
                    .stream()
                    .filter(o -> details.getValue().equals(o.getName()))
                    .findFirst().get();

            lastCallHistory.setSubtypeId(subtypeByName.getSubtypeId());
            lastCallHistory.setDetailsId(editDetails.getId());
            lastCallHistory.setTid(this.tid.getText());
            lastCallHistory.setTitle(this.title.getText());
            CallHistory callHistory = callLogService.saveCallHistory(lastCallHistory);
            this.loadCallHistoryTable();
            if (callHistory != null) {
                this.onMouseClickedUpdate();
            }
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

            Iterable<CallHistory> callHistoryBetween = callLogService.getCallHistoryBetween(localDateTimeFrom, localDateTimeTo);
            File file = new File(export.getPath().concat("\\export.csv"));
            csvService.write(file, callHistoryBetween);
        }
    }

    /**
     * Метод выбора директории.
     *
     * @param title Название заголовка.
     * @return Директория
     */
    private File directoryChooser(String title) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(title);
        return directoryChooser.showDialog(this.rootPane.getScene().getWindow());
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

    public void onActionClose() {
        Platform.exit();
    }
}