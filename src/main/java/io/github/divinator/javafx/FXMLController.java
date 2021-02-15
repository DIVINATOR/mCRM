package io.github.divinator.javafx;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Controller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Controller
public @interface FXMLController {
    @AliasFor(
            annotation = Controller.class
    )

    String value() default "";

    String view();
}
