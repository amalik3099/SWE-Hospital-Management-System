package edu.wpi.teamA.views;

import com.google.inject.Inject;
import com.jfoenix.controls.*;
import edu.wpi.teamA.modules.Record;
import edu.wpi.teamA.services.DatabaseService;
import edu.wpi.teamA.services.EmailService;
import edu.wpi.teamA.state.HomeState;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javax.mail.*;
import lombok.SneakyThrows;

public class SecurityReqController implements Initializable, InnerServiceController {

  @FXML public AnchorPane securityPane;
  @FXML private JFXComboBox<String> locationBox;
  @FXML private JFXTextArea descriptionArea;
  @FXML private StackPane dialogPane;
  @FXML private JFXCheckBox pa;
  @FXML private JFXCheckBox bg;
  @FXML private JFXCheckBox other;
  @FXML private JFXCheckBox hostage;

  @Inject CentralServiceController mainController;
  @Inject HomeState homeState;
  @Inject DatabaseService db;
  @Inject EmailService em;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    securityPane.setVisible(false);
    locationBox.setItems(homeState.getNodeList());
    locationBox.setValue("");
  }

  @FXML
  private void handleClearAction(ActionEvent event) {
    clear();
  }

  //  @FXML
  //  private void handleSubmitAction(ActionEvent e) throws MessagingException {
  //    // TODO Add error handling
  //    if (handleDialog()) {
  //      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
  //      LocalDateTime now = LocalDateTime.now();
  //
  //      Record r = new Record();
  //      r.addProperty("id", ""); // Gets automatically generated
  //      r.addProperty("date", dtf.format(now));
  //      r.addProperty("type", "Security Service Request");
  //      r.addProperty("assigned", "");
  //      r.addProperty("complete", "false");
  //      // r.addProperty("location", locationBox.getValue());
  //      r.addProperty("description", locationBox.getValue() + "; " + descriptionArea.getText());
  //      r.addProperty("urgency", String.valueOf(urgencyToggle.isSelected()));
  //      db.addRecord("REQUESTS", r);
  //
  //      em.sendEmail(
  //          emailField.getText(),
  //          "Medicine Request Submitted",
  //          "Your request was submitted successfully!");
  //      clear();
  //    }
  //    // Add popup window that successfully submitted
  //  }

  @FXML
  private void handleCancelAction(ActionEvent e) throws IOException {
    mainController.swapPanes("homePane");
    clear();
  }

  private void clear() {
    locationBox.setValue("");
    descriptionArea.setText("");
    pa.setSelected(false);
    hostage.setSelected(false);
    bg.setSelected(false);
    other.setSelected(false);
  }

  private boolean isSomethingChecked() {
    return pa.isSelected() || bg.isSelected() || hostage.isSelected() || other.isSelected();
  }

  private boolean isLocationCompleted() {
    return !locationBox.getValue().equals("");
  }

  private boolean isDescCompleted() {
    return !descriptionArea.getText().equals("");
  }

  private Boolean handleDialog() throws MessagingException {
    Boolean check = false;
    JFXDialog dialogComp = new JFXDialog();
    JFXDialog dialogInc = new JFXDialog();
    JFXDialogLayout incContent = new JFXDialogLayout();
    incContent.setHeading(new Text("Incomplete Service Request"));
    JFXButton btnClose = new JFXButton("Close");
    JFXButton btnClose1 = new JFXButton("Close");
    JFXButton btnClose2 = new JFXButton("Close");
    JFXButton btnClose3 = new JFXButton("Close");
    JFXButton btnClose4 = new JFXButton("Close");

    if (isLocationCompleted()) {
      if (isSomethingChecked()) {
        if (isDescCompleted()) {
          JFXDialogLayout compContent = new JFXDialogLayout();
          compContent.setHeading(new Text(" Service Request Received"));
          compContent.setBody(new Text("Thank you for submitting your request form! "));
          compContent.setActions(btnClose);
          dialogComp = new JFXDialog(dialogPane, compContent, JFXDialog.DialogTransition.CENTER);
          dialogComp.show();
          check = true;
        } else {
          incContent.setBody(
              new Text("The service request is incomplete. Please complete the description field"));
          incContent.setActions(btnClose1);
          dialogInc = new JFXDialog(dialogPane, incContent, JFXDialog.DialogTransition.CENTER);
          dialogInc.show();
        }
      } else {
        incContent.setBody(
            new Text("The service request is incomplete. Please select a type of security risk"));
        incContent.setActions(btnClose2);
        dialogInc = new JFXDialog(dialogPane, incContent, JFXDialog.DialogTransition.CENTER);
        dialogInc.show();
      }
    } else {
      incContent.setBody(new Text("The service request is incomplete. Please select a location"));
      incContent.setActions(btnClose3);
      dialogInc = new JFXDialog(dialogPane, incContent, JFXDialog.DialogTransition.CENTER);
      dialogInc.show();
    }

    JFXDialog finalDialogComp = dialogComp;
    JFXDialog finalDialogInc = dialogInc;
    btnClose.setOnAction(
        new EventHandler<ActionEvent>() {
          @SneakyThrows
          @Override
          public void handle(ActionEvent event) {
            finalDialogComp.close();
          }
        });
    btnClose1.setOnAction(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            finalDialogInc.close();
          }
        });
    btnClose2.setOnAction(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            finalDialogInc.close();
          }
        });
    btnClose3.setOnAction(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            finalDialogInc.close();
          }
        });
    btnClose4.setOnAction(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            finalDialogInc.close();
          }
        });
    return check;
  }

  @Override
  public boolean addRequestToDB(String name, String email, boolean urgency)
      throws MessagingException {
    if (handleDialog()) {
      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
      LocalDateTime now = LocalDateTime.now();

      Record r = new Record();
      r.addProperty("id", ""); // Gets automatically generated
      r.addProperty("Date", dtf.format(now));
      r.addProperty("Type", "Security");
      r.addProperty("Assigned", "");
      r.addProperty("Complete", "false");
      r.addProperty("Name", name);
      r.addProperty("Email", email);
      r.addProperty("Location", locationBox.getValue());
      r.addProperty("Urgency", String.valueOf(urgency));
      r.addProperty(
          "Description",
          //              descriptionArea.getText() + desc
          "Physical Assault?:"
              + (pa.isSelected() ? "Y" : "N")
              + "|"
              + "Burglary/Thievery?:"
              + (bg.isSelected() ? "Y" : "N")
              + "|"
              + "Hostage?:"
              + (hostage.isSelected() ? "Y" : "N")
              + "|"
              + "Other Situation?:"
              + (other.isSelected() ? "Y" : "N")
              + "|"
              + "Additional Details:"
              + descriptionArea.getText());

      db.addRecord("REQUESTS", r);

      em.sendEmail(email, "Security Request Submitted", "Your request was submitted successfully!");
      clear();
      return true;
    }
    return false;
  }

  @Override
  public void clearFields() {
    clear();
  }
}
