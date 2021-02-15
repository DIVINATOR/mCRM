package io.github.divinator.javafx;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;
import java.util.ResourceBundle;

@Configuration
public class FXMLConfiguration {

    @Bean
    public ResourceBundle resourceBundle(
            @Value(value = "${application.fx.resource.bundle:lang/lang}") final String bundle,
            @Value(value = "${application.fx.resource.bundle.locale:ru}") final String locale
    ) {
        return ResourceBundle.getBundle(bundle, new Locale(locale), FXMLResourceControl.UTF_8);
    }

    @Bean
    public FXMLViewService fxmlViewService(ConfigurableApplicationContext applicationContext, ResourceBundle resourceBundle) {
        FXMLLoadService fxmlLoadService = new FXMLLoadService(applicationContext, resourceBundle);
        return fxmlLoadService;
    }
}
