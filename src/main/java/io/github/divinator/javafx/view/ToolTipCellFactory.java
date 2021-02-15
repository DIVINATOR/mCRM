package io.github.divinator.javafx.view;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;

public class ToolTipCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {
    @Override
    public TableCell<S, T> call(TableColumn<S, T> param) {
        return new TableCell<S, T>(){
            @Override
            protected void updateItem(T param, boolean empty) {
                super.updateItem(param, empty);
                //Здесь необходимо установить текст ячейки
                //И заодно текст всплывающей подсказки
                if (param==null){
                    setTooltip(null);
                    setText(null);
                }else {
                    setTooltip(new Tooltip(param.toString()));
                    setText(param.toString());
                }
            }
        };
    }
}
