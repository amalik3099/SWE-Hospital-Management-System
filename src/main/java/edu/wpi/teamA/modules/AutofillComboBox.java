package edu.wpi.teamA.modules;

import com.jfoenix.controls.JFXComboBox;
import java.awt.*;
import java.awt.event.FocusAdapter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javax.swing.*;

public class AutofillComboBox<String> extends FocusAdapter implements EventHandler<KeyEvent> {
  private JFXComboBox<String> comboBox;
  private StringBuilder stringBuilder;
  private ObservableList<String> dropDownData;
  private boolean moveCursorTo = false;
  private int cursorPosition;

  public AutofillComboBox(final JFXComboBox<String> comboBox) {
    this.comboBox = comboBox;
    dropDownData = comboBox.getItems();
    this.comboBox.setEditable(true);
    this.comboBox.setOnKeyPressed(
        event -> {
          if (!event.equals(KeyCode.SHIFT)) {
            comboBox.hide();
          }
        });

    this.comboBox.setOnKeyReleased(AutofillComboBox.this);
  }

  @Override
  public void handle(KeyEvent event) {

    int currentCursor = comboBox.getEditor().getCaretPosition();

    if (event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE) {
      moveCursorTo = true;
      cursorPosition = comboBox.getEditor().getCaretPosition();
    }

    if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.UP) {
      cursorPosition = -1;
      moveCursor(comboBox.getEditor().getText().length());
      return;
    }

    if (event.getCode() == KeyCode.RIGHT
        || event.getCode() == KeyCode.LEFT
        || event.getCode() == KeyCode.SHIFT) {
      return;
    }

    ObservableList<String> list = FXCollections.observableArrayList();

    for (String dropDownData : dropDownData) {
      java.lang.String typedIn = AutofillComboBox.this.comboBox.getEditor().getText().toLowerCase();
      if (dropDownData.toString().toLowerCase().startsWith(typedIn)) {
        list.add(dropDownData);
      }
    }

    java.lang.String currentWord = comboBox.getEditor().getText();

    comboBox.setItems(list);
    comboBox.getEditor().setText(currentWord);

    comboBox.setVisibleRowCount(Math.min(list.size(), 10));

    if (!moveCursorTo) {
      cursorPosition = -1;
    }
    moveCursor(currentWord.length());
    if (!list.isEmpty()) {
      comboBox.show();
    }
  }

  private void moveCursor(int i) {
    if (this.cursorPosition == -1) {
      comboBox.getEditor().positionCaret(i);
    } else {
      comboBox.getEditor().positionCaret(this.cursorPosition);
    }
    moveCursorTo = false;
  }

  private void selectTextIfFocused(JFXComboBox<String> comboBox) {
    if (comboBox.getEditor().isFocused()) {
      comboBox.getEditor().selectAll();
    }
  }
}
