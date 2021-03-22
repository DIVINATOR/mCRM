package io.github.divinator.controller;

import io.github.divinator.datasource.entity.*;
import io.github.divinator.history.CallHistoryData;
import io.github.divinator.javafx.FXMLController;
import io.github.divinator.javafx.view.FxLoggerArea;
import io.github.divinator.service.CSVService;
import io.github.divinator.service.CallHistoryService;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@FXMLController(value = "mainController", view = "main.fxml")
public class MainController implements Initializable {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private Text username, calls, svo, fcr;
    @FXML
    private TextArea logArea;
    @FXML
    private TextField phone, tid, title, settingsLogin, settingsPath;
    @FXML
    private TitledPane history;
    @FXML
    private DatePicker date, callhistorydate;
    @FXML
    private ComboBox<String> hour, minute, type, subtype, details;
    @FXML
    private TableView<CallHistoryPojo> callhistorytable;
    @FXML
    private Circle indicator;
    @FXML
    private CheckBox followCheckBox, transferredCheckBox, sharedCheckBox;

    private final Logger LOG = LoggerFactory.getLogger(MainController.class);
    private final SettingsService settingsService;
    private final ApplicationContext applicationContext;
    private final CatalogService catalogService;
    private final CallHistoryService callHistoryService;
    private final CSVService csvService;
    private final ZoneId zoneId;

    private ResourceBundle resources;
    private CallHistoryData callHistoryData;
    private boolean manually;

    public MainController(
            SettingsService settingsService,
            ApplicationContext applicationContext,
            CatalogService catalogService,
            CallHistoryService callHistoryService,
            CSVService csvService
    ) {
        this.settingsService = settingsService;
        this.csvService = csvService;
        this.applicationContext = applicationContext;
        this.catalogService = catalogService;
        this.callHistoryService = callHistoryService;
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
        this.initializeLogger();
        this.initializeUsernameSettings();
        this.initializeFollowCheckBox();
        this.initializeSharedCheckBox();
        this.initializeDateTime();
        this.initializeType();
        this.initializeCallHistoryDate();
        this.initializeCallHistoryTable();
        this.initializeManually();
        this.initializeExportPath();
    }

    /**
     * <h2>Метод проводит инициализацию вывода логов приложения.</h2>
     */
    private void initializeLogger() {
        String loglevel = (String) settingsService.getSettings("logging.level.root").getValue();

        if (loglevel.length() > 0 && !loglevel.equals("OFF")) {
            FxLoggerArea.in(this.logArea);
        }
    }

    /**
     * Метод инициализирует значения даты, часа и минут на форме.
     */
    private void initializeDateTime() {
        this.hour.setItems(FXCollections.observableArrayList(createUnitTimeRange(24)));
        this.minute.setItems(FXCollections.observableArrayList(createUnitTimeRange(60)));
        this.updateDateTime(ZonedDateTime.now(zoneId));
    }

    /**
     * Метод инициализирует флаг ручного заполнения формы.
     */
    private void initializeManually() {
        this.manually = true;
    }

    /**
     * Метод инициализирует имя пользователя из ОС.
     */
    private void initializeUsernameSettings() {
        String login = (String) settingsService.getSettings("user.name").getValue();
        this.username.setText(login);
        this.settingsLogin.setText(login);
    }

    /**
     * Метод инициализирует значение чек-бокса для слежения за журналом Avaya One-X.
     */
    private void initializeFollowCheckBox() {
        String value = (String) settingsService.getSettings("application.history.follow").getValue();
        this.followCheckBox.setSelected(Boolean.parseBoolean(value));
    }

    private void initializeSharedCheckBox() {
        String value = (String) settingsService.getSettings("application.shared.export").getValue();
        this.sharedCheckBox.setSelected(Boolean.parseBoolean(value));
    }

    /**
     * Метод инициализирует список "Тип" тематики обращения.
     */
    private void initializeType() {
        List<String> collect = ((List<CatalogType>) this.catalogService.getAllCatalogType()).stream()
                .map(CatalogType::getName)
                .collect(Collectors.toList());

        this.type.setItems(FXCollections.observableArrayList(collect));
        this.type.setVisibleRowCount(collect.size());
    }

    private void initializeCallHistoryDate() {
        this.callhistorydate.setValue(LocalDate.now());
    }

    private void initializeCallHistoryTable() {

        TableColumn<CallHistoryPojo, String> dateCol = new TableColumn<>();
        dateCol.setText(this.resources.getString("gui.tab.0.label.time"));
        dateCol.setCellValueFactory(new PropertyValueFactory("time"));

        TableColumn<CallHistoryPojo, String> phoneCol = new TableColumn();
        phoneCol.setText(this.resources.getString("gui.tab.0.label.phone"));
        phoneCol.setCellValueFactory(new PropertyValueFactory("phone"));

        TableColumn<CallHistoryPojo, String> typeCol = new TableColumn();
        typeCol.setText(this.resources.getString("gui.tab.0.label.type"));
        typeCol.setCellValueFactory(new PropertyValueFactory("type"));

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

        this.callhistorytable.getColumns().addAll(new TableColumn[]{dateCol, phoneCol, tidCol, typeCol, subtypeCol, detailsCol, titleCol});

        this.loadCallHistoryTable();
    }

    private void initializeExportPath() {
        this.settingsPath.setText((String) settingsService.getSettings("application.shared.export.file").getValue());
    }

    /**
     * Метод инициализирует показатели на линии.
     */
    private void initializeStatisticsValue() {

    }

    private void loadCallHistoryTable() {
        List<CallHistoryPojo> collect = loadCallHistory();
        this.callhistorytable.getItems().setAll(FXCollections.observableArrayList(collect));
        this.updateCalls(this.callhistorytable.getItems().size());
        this.updateFcr(callHistoryService.getFCR(this.callhistorydate.getValue(), this.callhistorydate.getValue()));
    }

    private List<CallHistoryPojo> loadCallHistory() {
        return ((List<CallHistoryEntity>) this.callHistoryService.getCallHistoryEntity(this.callhistorydate.getValue(), this.callhistorydate.getValue()))
                .stream()
                .map(callHistoryEntity -> new CallHistoryPojo(callHistoryEntity, catalogService))
                .collect(Collectors.toList());
    }

    /**
     * Метод указывает на событие при нажатии на кнопку "Обновить".
     */
    @FXML
    public void onMouseClickedUpdate() {
        this.updatePhone(null);
        this.updateDateTime(ZonedDateTime.now(zoneId));
        this.updateType(null);
        this.updateSubtype(null);
        this.updateDetails(null);
        this.updateTid(null);
        this.updateTitle(null);
        this.updateManually(true);
        this.updateTransferred(false);
    }

    /**
     * Метод указывает на событие при завершении выбора поля "Подтип".
     */
    @FXML
    public void onHidingSubtype() {
        this.updateDetails(null);
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
    public void onShowingTypes() {
        String noneBorder = "-fx-border: none;";
        updateSubtype(null);
        updateDetails(null);
        this.type.setStyle(noneBorder);
    }

    @FXML
    public void onShowingSubtypes() {
        String noneBorder = "-fx-border: none;";
        updateDetails(null);

        if (this.type.getValue() != null) {
            CatalogType catalogType = this.catalogService.getCatalogType(String.valueOf(this.type.getValue()));

            if (catalogType != null) {
                final List<String> catalogSubtypes = catalogType.getSubtypes().stream()
                        .map(CatalogSubtype::getName)
                        .collect(Collectors.toList());

                this.subtype.setItems(FXCollections.observableArrayList(catalogSubtypes));
                this.subtype.setVisibleRowCount(catalogSubtypes.size());
            }

            this.subtype.setStyle(noneBorder);
        }

    }

    @FXML
    public void onShowingDetails() {
        String noneBorder = "-fx-border: none;";

        if (this.type.getValue() != null && this.subtype.getValue() != null) {
            CatalogType catalogType = this.catalogService.getCatalogType(String.valueOf(this.type.getValue()));
            if (catalogType != null) {
                try {
                    String subtypeValue = this.subtype.getValue();

                    CatalogSubtype subtype = catalogType.getSubtypes().stream().filter(new Predicate<CatalogSubtype>() {
                        @Override
                        public boolean test(CatalogSubtype subtype) {
                            return subtype.getName().equals(subtypeValue);
                        }
                    }).findFirst().orElseThrow(Exception::new);

                    final List<String> details = subtype.getDetails().stream()
                            .map(CatalogDetails::getName)
                            .collect(Collectors.toList());

                    this.details.setItems(FXCollections.observableArrayList(details));
                    this.details.setVisibleRowCount(details.size());

                } catch (Exception e) {
                    //
                }

                this.details.setStyle(noneBorder);
            }
        }
    }

    @FXML
    public void onMousePressed(MouseEvent mouseEvent) {
        String noneBorder = "-fx-border: none;";
        ((Control) mouseEvent.getSource()).setStyle(noneBorder);
    }

    /**
     * Метод указывает на событие при нажатии на кнопку "Сохранить".
     */
    @FXML
    public void onActionSave() {
        if (validationInputFields()) {
            CallHistoryEntity callHistoryEntity = new CallHistoryEntity(
                    getPhone(),
                    ZonedDateTime.now()
                            .withZoneSameInstant(
                                    ZoneId.of((String) settingsService.getSettings("application.db.zone").getValue()))
                            .toLocalDateTime(),
                    getDateTime().toLocalDateTime(),
                    getType().getTypeId(),
                    getSubtype().getSubtypeId(),
                    (getDetails().isPresent()) ? getDetails().get().getId() : 0,
                    getTid(),
                    (getTitle() != null && getTitle().length() != 0) ? getTitle() : null,
                    getManually(),
                    getTransferred()
            );

            CallHistoryEntity save = callHistoryService.saveCallHistoryEntity(callHistoryEntity);

            if (save != null) {
                LOG.info(String.format("Входящий звонок %s сохранен.", getPhone()));
                this.onMouseClickedUpdate();
                this.loadCallHistoryTable();
            }
        }
    }

    /**
     * Метод указывает на событие при нажатии на кнопку "Сохранить" в настройках.
     */
    @FXML
    public void onSaveSettings() {
        settingsService.setAllSettings(Arrays.asList(
                settingsService.getSettings("application.history.follow")
                        .setValue(String.valueOf(this.followCheckBox.isSelected()).toUpperCase(Locale.ROOT)),
                settingsService.getSettings("application.shared.export")
                        .setValue(String.valueOf(this.sharedCheckBox.isSelected()).toUpperCase(Locale.ROOT)),
                settingsService.getSettings("user.name").setValue(settingsLogin.getText())
        ));
    }

    /**
     * Метод указывает на событие при нажатии кнопки "Выгрузить".
     */
    @FXML
    public void onExportHistory() {
        if (Boolean.parseBoolean((String) settingsService.getSettingsValue("application.shared.export"))) {
            this.onSharedHistory();
        } else {
            File export = this.directoryChooser(resources.getString("gui.tab.1.button.export.dialog.title"));
            if (export != null) {
                List<CallHistoryPojo> callHistoryPojo = loadCallHistory();
                String date = getCallHistoryDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                File file = new File(export.getPath().concat(String.format("\\%s-%s-export.csv", date, getUsername())));

                csvService.write(file, callHistoryPojo);
            }
        }
    }

    public void onSharedHistory() {
        File shared = new File((String) settingsService.getSettingsValue("application.shared.export.file"));

        if (!shared.exists()) {
            csvService.writeInShared(shared, getUsername(), loadCallHistory());
        } else {
            while(shared.canWrite()) {
                csvService.writeAppendInShared(shared, getUsername(), loadCallHistory());
            }
        }
    }

    public String getUsername() {
        return this.username.getText();
    }

    /**
     * Метод возвращает значение поля "Телефон".
     *
     * @return Значение поля "Телефон".
     */
    public String getPhone() {
        return phone.getText();
    }

    /**
     * Метод обновляет значение поля "Телефон".
     *
     * @param phone Номер телефона.
     */
    public void updatePhone(String phone) {
        this.phone.setText(phone);
    }

    /**
     * Метод возвращает указанную дату и время звонка поясе из настроек (по умолчанию GMT+3).
     *
     * @return Дата и Время звонка в часовом поясе из настроек.
     */
    public ZonedDateTime getDateTime() {
        return ZonedDateTime.of(
                LocalDateTime.of(
                        this.date.getValue(),
                        LocalTime.parse(String.format("%s:%s", this.hour.getValue(), this.minute.getValue()))
                ), this.zoneId
        ).withZoneSameInstant(ZoneId.of((String) settingsService.getSettings("application.db.zone").getValue()));
    }

    /**
     * Метод обновляет значение даты и времени на форме в часовом поясе операционной системы.
     */
    public void updateDateTime(ZonedDateTime zonedDateTime) {
        ZonedDateTime dateTime = zonedDateTime.withZoneSameInstant(this.zoneId);
        this.date.setValue(dateTime.toLocalDate());
        this.hour.setValue(convertUnitTimeFromInteger(dateTime.getHour()));
        this.minute.setValue(convertUnitTimeFromInteger(dateTime.getMinute()));
    }

    public LocalDate getCallHistoryDate() {
        return this.callhistorydate.getValue();
    }

    private CatalogType getType() {
        return catalogService.getCatalogType(this.type.getValue());
    }

    public void updateType(String value) {
        this.type.setValue(value);
    }

    public CatalogSubtype getSubtype() {
        final String subtypeValue = this.subtype.getValue();
        return getType().getSubtypes()
                .stream()
                .filter(subtype -> subtype.getName().equals(subtypeValue))
                .findFirst()
                .orElse(null);
    }

    public void updateSubtype(String value) {
        this.subtype.setValue(value);
    }


    public Optional<CatalogDetails> getDetails() {
        final String detailsValue = this.details.getValue();

        return getSubtype().getDetails()
                .stream()
                .filter(details -> details.getName().equals(detailsValue))
                .findFirst();
    }

    public void updateDetails(String value) {
        this.details.setValue(value);
    }

    /**
     * Метод возвращает значение поля "Номер терминала".
     *
     * @return Значение поля "Номер терминала".
     */
    public String getTid() {
        return this.tid.getText();
    }

    /**
     * Метод обновляет значение поля "Номер терминала".
     *
     * @param value Значение поля "Номер терминала".
     */
    public void updateTid(String value) {
        this.tid.setText(value);
    }

    /**
     * Метод обновляет значение поля "Описание".
     *
     * @return Значение поля "Описание".
     */
    public String getTitle() {
        return this.title.getText();
    }

    /**
     * Метод возвращает значение поля "Описание".
     *
     * @param value Значение поля "Описание".
     */
    public void updateTitle(String value) {
        this.title.setText(value);
    }

    /**
     * Метод возвращает флаг ручного заполнения формы.
     *
     * @return Флаг ручного заполнения формы.
     */
    public boolean getManually() {
        return manually;
    }

    /**
     * Метод обновляет флаг ручного заполнения формы.
     *
     * @param value true - заполнено в ручную, false - заполнено с помощью задания.
     */
    public void updateManually(boolean value) {
        this.manually = value;
    }

    /**
     * Метод возвращает флаг переведенного звонка.
     *
     * @return Флаг переведенного звонка.
     */
    public boolean getTransferred() {
        return transferredCheckBox.isSelected();
    }

    public void updateTransferred(boolean value) {
        transferredCheckBox.setSelected(value);
    }

    /**
     * Метод обновляет количество звонков.
     *
     * @param value Количество звонков.
     */
    public void updateCalls(Number value) {
        this.calls.setText(String.format("%s", value));
    }

    public void updateSvo(Number value) {
        this.svo.setText(
                String.format(
                        "Текущий результат: %s / Необходимый %s",
                        value,
                        settingsService.getSettingsValue("application.statistics.svo"))
        );
    }

    public void updateFcr(Number value) {
        this.fcr.setText(
                String.format(
                        "Текущий результат: %s / Необходимый %s", value,
                        settingsService.getSettingsValue("application.statistics.fcr"))
        );
    }


    /**
     * Метод выполняет валидацию заполнения полей ввода.
     *
     * @return true в случае если все заполнено корректно, в противном случае false;
     */
    private boolean validationInputFields() {
        String redBorder = "-fx-border-color: red;";

        if (this.phone.getText() == null || this.phone.getText().length() == 0) {
            this.phone.setStyle(redBorder);
            LOG.error("Не заполнен номер телефона.");
            return false;
        }

        if (this.type.getValue() == null || this.type.getValue().length() == 0) {
            this.type.setStyle(redBorder);
            LOG.error("Не заполнен \"Тип\".");
            return false;
        }

        if (this.subtype.getValue() == null || this.subtype.getValue().length() == 0) {
            this.subtype.setStyle(redBorder);
            LOG.error("Не заполнен \"Подтип\".");
            return false;
        }

        if (this.details.getValue() == null || this.details.getValue().length() == 0) {
            String subtypeValue = this.subtype.getValue();

            final int size = catalogService.getCatalogType(this.type.getValue()).getSubtypes().stream().filter(new Predicate<CatalogSubtype>() {
                @Override
                public boolean test(CatalogSubtype subtype) {
                    return subtype.getName().equals(subtypeValue);
                }
            }).findFirst().get().getDetails().size();

            if (size > 0) {
                this.details.setStyle(redBorder);
                LOG.error("Не заполнены \"Детали\".");
                return false;
            }
        }

        if (this.tid.getText() == null || this.tid.getText().length() == 0) {
            this.tid.setStyle(redBorder);
            LOG.error("Не заполнен \"Номер терминала\".");
            return false;
        }

        return true;
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