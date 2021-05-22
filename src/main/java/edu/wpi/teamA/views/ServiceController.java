package edu.wpi.teamA.views;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.teamA.modules.AutofillComboBox;
import edu.wpi.teamA.modules.Record;
import edu.wpi.teamA.services.DatabaseService;
import edu.wpi.teamA.state.HomeState;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ServiceController implements Initializable {

  @FXML ComboBox<String> serviceTypeBox;
  @FXML AnchorPane contentPane;
  @FXML private Button btnClear;
  @FXML private Button btnCancel;
  @FXML private Button btnSubmit;
  @FXML private CheckBox checkYes;
  @FXML private CheckBox checkNo;
  @FXML private JFXComboBox<String> start;
  @FXML private JFXComboBox<String> end;
  @FXML private TextField endEXT;
  @FXML private TextArea description;
  @FXML private Label intLabel;
  @FXML private Label extLabel;
  @FXML private ComboBox<String> extTransportBox;
  @FXML private ComboBox<String> intTransportBox;
  Integer number_of_requests = 0;

  @Inject FXMLLoader loader;
  @Inject DatabaseService db;
  @Inject HomeState homeState;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    ObservableList<String> typeList =
        FXCollections.observableArrayList("External Transport", "Internal Transport");

    serviceTypeBox.setValue("External Transport");
    serviceTypeBox.setItems(typeList);

    ObservableList<String> extTransportList =
        FXCollections.observableArrayList("Ambulance", "Mobile ICU", "Helicopter", "Aeroplane");

    extTransportBox.setValue("Ambulance");
    extTransportBox.setItems(extTransportList);

    ObservableList<String> intTransportList =
        FXCollections.observableArrayList(
            "Wheelchair", "Transport Stretcher", "Surgical Stretcher", "Clinical Recliner");

    intTransportBox.setValue("WheelChair");
    intTransportBox.setItems(intTransportList);

    start.setItems(homeState.getNodeList());
    end.setItems(homeState.getNodeList());
    AutofillComboBox fromAutoCombo = new AutofillComboBox(start);
    AutofillComboBox toAutoCombo = new AutofillComboBox(end);
  }

  @FXML
  private void handleServiceAction() {
    if (serviceTypeBox.getValue().equals("External Transport")) {
      extLabel.setVisible(true);
      intLabel.setVisible(false);
      extTransportBox.setVisible(true);
      intTransportBox.setVisible(false);
      end.setVisible(false);
      endEXT.setVisible(true);
    } else if (serviceTypeBox.getValue().equals("Internal Transport")) {
      intLabel.setVisible(true);
      extLabel.setVisible(false);
      intTransportBox.setVisible(true);
      extTransportBox.setVisible(false);
      end.setVisible(true);
      endEXT.setVisible(false);
    }
  }

  @FXML
  private void handleClearAction(ActionEvent event) {
    Parent root;
    Stage stage = (Stage) btnClear.getScene().getWindow();
    clear();
  }

  @FXML
  private void handleSubmitAction(ActionEvent e) {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
    LocalDateTime now = LocalDateTime.now();
    Record newRequest = new Record();
    newRequest.addProperty("type", serviceTypeBox.getValue());
    newRequest.addProperty("date", dtf.format(now));
    newRequest.addProperty("assigned", "");
    newRequest.addProperty("complete", "false");
    newRequest.addProperty("current_location", start.getValue());
    newRequest.addProperty(
        "end_location",
        serviceTypeBox.getValue().equals("External Transport") ? endEXT.getText() : end.getValue());
    if (checkYes.isSelected()) {
      newRequest.addProperty("Urgency", "true");
    } else {
      newRequest.addProperty("Urgency", "false");
    }
    newRequest.addProperty(
        "Transport",
        serviceTypeBox.getValue().equals("External Transport")
            ? extTransportBox.getValue()
            : intTransportBox.getValue()); // add for intTransportBox
    newRequest.addProperty("description", description.getText());
    clear();
    db.addRecord("REQUESTS", newRequest);
  }

  @FXML
  private void handleCancelAction(ActionEvent e) throws IOException {
    // get reference to the button's stage
    //      Parent root;
    //      Stage stage = (Stage) btnHome.getScene().getWindow();
    //      // load other FXML document
    //      root = loader.load(getClass().getResourceAsStream("HomeView.fxml"));
    //      Scene aScene = new Scene(root);
    //      stage.setScene(aScene);
    //      stage.show();
  }

  @FXML
  private void handleYesBox() {
    if (checkYes.isSelected()) {
      checkNo.setSelected(false);
    }
  }

  @FXML
  private void handleNoBox() {
    if (checkNo.isSelected()) {
      checkYes.setSelected(false);
    }
  }

  private void clear() {
    start.setValue("");
    end.setValue("");
    description.setText("");
    endEXT.clear();
    if (checkYes.isSelected()) {
      checkYes.setSelected(false);
    } else if (checkNo.isSelected()) {
      checkNo.setSelected(false);
    }
  }
}
