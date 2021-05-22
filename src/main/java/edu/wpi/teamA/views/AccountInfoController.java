package edu.wpi.teamA.views;

import com.google.inject.Inject;
import com.jfoenix.controls.*;
import edu.wpi.teamA.modules.Record;
import edu.wpi.teamA.services.AuthService;
import edu.wpi.teamA.services.DatabaseService;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class AccountInfoController implements Initializable {

  @FXML JFXTextField usernameField;
  @FXML JFXTextField emailField;
  @FXML JFXPasswordField passwordField;

  @FXML JFXButton btnSave;
  @FXML JFXButton btnSignOut;
  @FXML JFXButton btnEdit;

  @FXML AnchorPane accountPane;
  @FXML JFXListView<JFXButton> appointments;

  @Inject AuthService as;
  @Inject DatabaseService db;

  @FXML Label whenLabel;
  @FXML Label whereLabel;
  @FXML Label staffLabel;
  @FXML Label detailLabel;

  private ObservableList<JFXButton> selectedItems;

  private ObservableList<SimpleStringProperty> info;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    appointments.setVerticalGap(2.0);
    appointments.setMaxWidth(450);

    selectedItems = appointments.getSelectionModel().getSelectedItems();
    selectedItems.addListener(
        new ListChangeListener<JFXButton>() {
          @Override
          public void onChanged(Change<? extends JFXButton> c) {
            if (selectedItems.get(0) != null) {
              selectedItems.get(0).fire();
            }
          }
        });

    info = as.getUserData();
    usernameField.setText(info.get(0).getValue());
    emailField.setText(info.get(1).getValue());

    btnSave.setDisable(true);

    info.get(0)
        .addListener(
            (observable, oldValue, newValue) -> {
              usernameField.setText(newValue);
            });

    info.get(1)
        .addListener(
            (observable, oldValue, newValue) -> {
              emailField.setText(newValue);
              passwordField.setText("sample");
              addAppointments();
            });
  }

  private void addAppointments() {
    appointments.getItems().removeAll(appointments.getItems());
    if (emailField.getText() == "") {
      return;
    }

    List<String> appoints =
        db.getStringList(
            "APPOINTMENTS",
            new String[] {"date", "time", "reason"},
            new String[] {"email"},
            new String[] {emailField.getText()},
            new String[] {"date"});

    ObservableList<Record> appointRecords = db.getItemsAppoints(emailField.getText());

    appoints.add(0, "Upcoming Appointments(" + appoints.size() + ")");
    for (String s : appoints) {
      JFXButton newButton = new JFXButton();
      newButton.setText(s);
      newButton.setPrefWidth(400);
      newButton.setStyle(
          "-fx-background-color: #dadada;\n"
              + "    -fx-text-fill: #000000;\n"
              + "    -fx-border-width: 0;\n"
              + "    -fx-background-radius: 5;\n"
              + "    -fx-font-family: Verdana;\n"
              + "    -fx-font-size: 15;\n"
              + "    -jfx-button-type: RAISED;");
      newButton.setOnAction(
          new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
              whenLabel.setVisible(true);
              whereLabel.setVisible(true);
              staffLabel.setVisible(true);
              detailLabel.setVisible(true);

              // Quickly get date and time
              String appointString[] = newButton.getText().split(", ");
              // Find the appointment record using date and time (already filtered to our email
              // before)
              Record r =
                  appointRecords
                      .filtered(
                          record ->
                              record.getFieldAsString("date").equals(appointString[0])
                                  && record.getFieldAsString("time").equals(appointString[1]))
                      .get(0);

              whenLabel.setText(
                  "Date: "
                      + newButton.getText().substring(0, newButton.getText().lastIndexOf("M,")));

              whereLabel.setText("Location: " + r.getFieldAsString("location"));
              staffLabel.setText(r.getFieldAsString("staff"));
              detailLabel.setText(r.getFieldAsString("description"));
            }
          });

      appointments.getItems().add(newButton);
    }

    ((JFXButton) appointments.getItems().get(0))
        .setOnAction(
            new EventHandler<ActionEvent>() {
              @Override
              public void handle(ActionEvent event) {
                whenLabel.setVisible(false);
                whereLabel.setVisible(false);
                staffLabel.setVisible(false);
                detailLabel.setVisible(false);

                whenLabel.setText("");
                whereLabel.setText("");
                staffLabel.setText("");
                detailLabel.setText("");
              }
            });
  }

  @FXML
  public void handleSignOut(ActionEvent actionEvent) {
    btnEdit.setDisable(true);
    btnSignOut.setDisable(true);
    btnSave.setDisable(true);

    usernameField.clear();
    emailField.clear();
    passwordField.clear();

    as.signOut();
  }

  @FXML
  public void handleEdit(ActionEvent actionEvent) {
    usernameField.setEditable(true);
    emailField.setEditable(true);
    passwordField.setEditable(true);

    passwordField.clear();

    btnSignOut.setDisable(true);
    btnSave.setDisable(false);

    btnEdit.setText("Cancel");
    btnEdit.setId("cBtn");
    btnEdit.setOnAction(event -> handleCancel(actionEvent));
  }

  @FXML
  public void handleCancel(ActionEvent actionEvent) {
    usernameField.setEditable(false);
    emailField.setEditable(false);
    passwordField.setEditable(false);

    btnSignOut.setDisable(false);
    btnSave.setDisable(true);

    passwordField.setText("sample");

    btnEdit.setText("Edit");
    btnEdit.setId("btnSubmit");
    btnEdit.setOnAction(event -> handleEdit(actionEvent));
  }

  @FXML
  public void handleSave(ActionEvent actionEvent) {
    usernameField.setEditable(false);
    emailField.setEditable(false);
    passwordField.setEditable(false);

    Record r = as.getUser();
    db.updateRecord("USERS", r, "id", usernameField.getText());
    db.updateRecord("USERS", r, "email", emailField.getText());
    db.updateRecord("USERS", r, "password", passwordField.getText());

    btnSignOut.setDisable(false);
    btnSave.setDisable(true);

    passwordField.setText("sample");

    btnEdit.setText("Edit");
    btnEdit.setId("btnSubmit");
    btnEdit.setOnAction(event -> handleEdit(actionEvent));
  }
}
