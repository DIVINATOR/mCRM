package io.github.divinator;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

//@Lazy
@SpringBootApplication
@EnableScheduling
public class App extends AppFX {
    public static void main(String[] args) {
        launch(App.class, args);
    }
}
