package io.github.divinator;

import io.github.divinator.javafx.FXMLViewService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ResourceBundle;

public abstract class AppFX extends Application {
    private final Logger LOG = LoggerFactory.getLogger(AppFX.class);
    private Parameters parameters;
    private ConfigurableApplicationContext context;
    //private Parent rootNode;
    @Autowired
    private FXMLViewService fxmlViewService;
    @Autowired
    ResourceBundle resourceBundle;

    public AppFX() {
    }

    public void init() {
        this.parameters = this.getParameters();
        this.context = SpringApplication.run(this.getClass());
        this.context.getAutowireCapableBeanFactory().autowireBean(this);
    }

    public void start(Stage primaryStage) {

        try {
            Scene scene = new Scene((Parent)this.fxmlViewService.getView("main.fxml"));
            scene.getStylesheets().add("css/app.css");
            primaryStage.setTitle(String.format("%s %s", resourceBundle.getString("app.name"), resourceBundle.getString("app.version")));
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(800.0D);
            primaryStage.setMaxWidth(800.0D);
            primaryStage.setMinHeight(600.0D);
            primaryStage.setHeight(600.0D);
            primaryStage.getIcons().add(new Image("img/ico_64_64.png"));
            //primaryStage.initStyle(StageStyle.UNIFIED);
            primaryStage.show();
            this.LOG.info("Приложение запущено");

        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    public static void launch(Class<? extends Application> appClass, String... args) {
        Application.launch(appClass, args);
    }

    public void stop() {
        SpringApplication.exit(this.context);
        Platform.exit();
        System.exit(0);
    }
}