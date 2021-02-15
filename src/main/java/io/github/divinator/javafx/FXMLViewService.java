package io.github.divinator.javafx;

import javafx.scene.Node;

/**
 * Интерфейс описывает сервис создания узла графа сцены.
 */
public interface FXMLViewService {

    /**
     * <h2>Метод возвращает узла графа сцены по его имени.</h2>
     *
     * @param name Имя узла графа сцены.
     * @return Узел графа сцены по его имени.
     */
    Node getView(String name);

    /**
     * <h2>Метод возвращает узла графа сцены по его контроллеру.</h2>
     *
     * @param controller Контроллер графа сцены.
     * @return Узел графа сцены по его контроллеру.
     */
    Node getView(Class<?> controller);
}
