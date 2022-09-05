package com.gui_java;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;

public class TableCellCheckBox extends TableCell<Article, Boolean> {
    private CheckBox checkBox;
    private ObservableValue ov;

    public TableCellCheckBox() {
        this.checkBox = new CheckBox();
        /*this.checkBox.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (isEditing())
                commitEdit(t1 != null && t1);

            //Log.print(String.valueOf(this.checkBox.isSelected()));
        });*/

        /*this.checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                commitEdit(newValue);
            }
        });*/
        this.setGraphic(checkBox);
        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.setEditable(true);
    }

    public boolean isTrue() {
        return checkBox.isSelected();
    }

    public boolean changeDetected() {
        final boolean[] change = {false};
        this.checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                change[0] = true;
            }
        });
        return change[0];
    }


    @Override
    public void updateIndex(int i) {
        super.updateIndex(i);
    }

    @Override
    public void startEdit() {
        super.startEdit();
        if (isEmpty())
            return;

        checkBox.requestFocus();
    }

    @Override
    public void commitEdit(Boolean aBoolean) {
        super.commitEdit(aBoolean);
        Log.print("Commit: " + aBoolean.toString());
    }

    @Override
    protected void updateItem(Boolean aBoolean, boolean b) {
        super.updateItem(aBoolean, b);
        if (!isEmpty()) {
            setGraphic(checkBox);
            checkBox.setSelected(aBoolean);
            /*if (ov instanceof BooleanProperty) {
                checkBox.selectedProperty().unbindBidirectional((BooleanProperty) ov);
            }*/
        } else {
            setGraphic(null);
            setText(null);
        }
    }

    @Override
    protected boolean isItemChanged(Boolean aBoolean, Boolean t1) {
        if (super.isItemChanged(aBoolean, t1))
            Log.print("The item has been changed");
        return super.isItemChanged(aBoolean, t1);
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }
}
