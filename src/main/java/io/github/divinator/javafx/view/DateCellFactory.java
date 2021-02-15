package io.github.divinator.javafx.view;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.text.SimpleDateFormat;

public class DateCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {
    @Override
    public TableCell<S, T> call(TableColumn<S, T> param) {
        return new TableCell<S, T>(){
            @Override
            protected void updateItem(T param, boolean empty) {
                super.updateItem(param, empty);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy H:mm");

                //Здесь необходимо установить текст ячейки
                //И заодно текст всплывающей подсказки
                if (param==null){
                    setText(null);
                }else {
                    setText(simpleDateFormat.format(param));
                }
            }
        };
    }
}