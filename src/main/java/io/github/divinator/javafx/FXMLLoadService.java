package io.github.divinator.javafx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.ResourceBundle;


public class FXMLLoadService implements FXMLViewService {

    private final ApplicationContext applicationContext;
    private final ResourceBundle resourceBundle;
    private String prefix = "fxml/";

    public FXMLLoadService(ConfigurableApplicationContext applicationContext, ResourceBundle resourceBundle) {
        this.applicationContext = applicationContext;
        this.resourceBundle = resourceBundle;
    }

    /**
     * <h2>Метод возвращает узла графа сцены по его имени.</h2>
     *
     * @param name Имя узла графа сцены.
     * @return Узел графа сцены по его имени.
     */
    @Override
    public Node getView(String name) {
        return loadView(name, getController(name));
    }

    @Override
    public Node getView(Class<?> controller) {
        return loadView(controller.getAnnotation(FXMLController.class).view(), getController(controller));
    }

    /**
     * <h2>Метод устанавливает префикс (директория в ресурсах) для поиска узлов графа сцены.</h2>
     *
     * @param prefix Префикс (директория в ресурсах).
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * <h2>Метод возвращает все объявленные FXMLController.</h2>
     *
     * @return Все объявленные FXMLController.
     */
    private Collection<Object> getControllers() {
        return applicationContext.getBeansWithAnnotation(FXMLController.class).values();
    }

    /**
     * @param controller
     * @return
     */
    private Object getController(Class<?> controller) {
        return getControllers()
                .stream()
                .filter(o -> o.getClass().equals(controller))
                .findAny()
                .orElseThrow(() -> new FXMLLoadException(String.format("Controller \"%s\" not found.", controller.getName())));
    }

    /**
     * @param view
     * @return
     */
    private Object getController(String view) {
        return getControllers()
                .stream()
                .filter(o -> o.getClass().getDeclaredAnnotation(FXMLController.class).view().equals(view))
                .findAny()
                .orElseThrow(() -> new FXMLLoadException(String.format("Controller not found for the \"%s\" view", view)));
    }

    private <V> V loadView(String view, Object controller) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setResources(resourceBundle);
            if (!hasController(getViewStream(view))) {
                fxmlLoader.setController(controller);
            }
            return fxmlLoader.load(getViewStream(view));
        } catch (IOException e) {
            throw new FXMLLoadException(String.format("Unable load FXML file \"%s\"", view), e);
        }
    }

    /**
     * Метод возвращает входной поток для узла графа сцены.
     *
     * @param view Имя файла узла графа сцены.
     * @return Входной поток для узла графа сцены.
     */
    private InputStream getViewStream(String view) {
        return getClass().getClassLoader().getResourceAsStream(String.format("%s%s", prefix, view));
    }

    /**
     * <h2>Метод проверяет поток для узла графа сцены на наличие указанного в нем контроллера.</h2>
     *
     * @param viewStream Поток для узла графа сцены.
     * @return true - в случае если контроллер определен, в противном случае false;
     * @throws FXMLLoadException В случае ошибки чтения потока.
     */
    private boolean hasController(InputStream viewStream) throws FXMLLoadException {
        try {
            XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(viewStream);
            while (reader.hasNext()) {
                int tag = reader.next();
                if (tag == XMLStreamReader.START_ELEMENT) {
                    return reader.getAttributeValue("http://javafx.com/fxml/1", "controller") != null;
                }
            }
            return false;
        } catch (XMLStreamException e) {
            throw new FXMLLoadException("");
        }
    }
}
