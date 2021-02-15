package io.github.divinator.javafx.view;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.DateCell;

import java.time.LocalDate;
import java.util.function.BiPredicate;

public class FilterDateCell extends DateCell {

    private ObjectProperty<LocalDate> date;
    private BiPredicate<LocalDate, LocalDate> filterPredicate;

    public FilterDateCell(ObjectProperty<LocalDate> date, BiPredicate<LocalDate, LocalDate> filterPredicate) {
        this.date = date;
        this.filterPredicate = filterPredicate;
    }

    @Override
    public void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);
        if (filterPredicate.test(item, date.get())) {
            this.setDisable(true);
            setStyle("-fx-background-color: #7e7e7e;"); // I used a different coloring to see which are disabled.
        }
    }

}
