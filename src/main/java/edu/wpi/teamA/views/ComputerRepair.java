package edu.wpi.teamA.views;

import com.jfoenix.controls.*;
import edu.wpi.teamA.modules.Record;
import edu.wpi.teamA.services.DatabaseService;
import edu.wpi.teamA.services.EmailService;
import edu.wpi.teamA.state.HomeState;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javax.inject.Inject;
import javax.mail.MessagingException;
import lombok.SneakyThrows;

public class ComputerRepair implements Initializable, InnerServiceController {
  @FXML private AnchorPane computerPane;
  @FXML private JFXComboBox<String> locationBox;
  @FXML private JFXComboBox<String> reqTypeBox;
  @FXML private JFXTextArea descReqBox;
  @FXML private StackPane dialogPane;

  @Inject HomeState homeState;
  @Inject DatabaseService db;
  @Inject CentralServiceController mainController;
  @Inject EmailService em;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    computerPane.setVisible(false);
    locationBox.setItems(homeState.getNodeList());
    locationBox.setValue("");
    reqTypeBox.setItems(FXCollections.observableArrayList("Hardware", "Software"));
    reqTypeBox.setValue("");
  }

  //  @SneakyThrows
  //  @FXML
  //  private void handleSubmitAction(ActionEvent event) throws MessagingException {
  //    if (handleDialog()) {
  //      em.sendEmail(
  //          emailBox.getText(),
  //          "Computer Repair Request Submitted",
  //          "Your request was submitted successfully!");
  //      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
  //      LocalDateTime now = LocalDateTime.now();
  //
  //      String urgency = "false";
  //
  //      if (checkYes.isSelected()) urgency = "true";
  //      if (checkNo.isSelected()) urgency = "false";
  //
  //      Record r = new Record();
  //      r.addProperty("id", ""); // Gets automatically generated
  //      r.addProperty("date", dtf.format(now));
  //      r.addProperty("type", "Computer Repair Service Request");
  //      r.addProperty("assigned", "");
  //      r.addProperty("complete", "false");
  //      r.addProperty("location", (String) locationBox.getValue());
  //      // r.addProperty("employee name", nameBox.getText());
  //      // r.addProperty("repair type", (String) reqTypeBox.getValue());
  //      r.addProperty("urgency", urgency);
  //      // r.addProperty("email", emailBox.getText());
  //      r.addProperty(
  //          "description",
  //          "desc; "
  //              + descReqBox.getText()
  //              + "|"
  //              + " Name; "
  //              + nameBox.getText()
  //              + "|"
  //              + " RepairType; "
  //              + (String) reqTypeBox.getValue());
  //      db.addRecord("REQUESTS", r);
  //      clear(); // on successful submission clear form
  //    }
  //  }

  @FXML
  private void handleClearAction(ActionEvent event) {
    clear();
  }

  private void clear() {
    descReqBox.setText("");
    locationBox.setValue("");
    reqTypeBox.setValue("");
  }

  //  @FXML
  //  private void handleYesBox() {
  //    if (checkYes.isSelected()) {
  //      checkNo.setSelected(false);
  //    }
  //  }
  //
  //  @FXML
  //  private void handleNoBox() {
  //    if (checkNo.isSelected()) {
  //      checkYes.setSelected(false);
  //    }
  //  }

  private boolean isLocationCompleted() {
    return !locationBox.getValue().equals("");
  }

  private boolean isRepairTypeCompleted() {
    return !reqTypeBox.getValue().equals("");
  }

  private boolean isDescCompleted() {
    return !descReqBox.getText().equals("");
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
    JFXButton btnClose5 = new JFXButton("Close");

    if (isLocationCompleted()) {
      if (isRepairTypeCompleted()) {
        if (isDescCompleted()) {
          JFXDialogLayout compContent = new JFXDialogLayout();
          compContent.setHeading(new Text(" Service Request Received"));
          compContent.setBody(
              new Text(
                  "Thank you for submitting your request form! "
                      + "You will receive a confirmation email shortly."));
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
            new Text("The service request is incomplete. Please complete the repair type field"));
        incContent.setActions(btnClose1);
        dialogInc = new JFXDialog(dialogPane, incContent, JFXDialog.DialogTransition.CENTER);
        dialogInc.show();
      }
    } else {
      incContent.setBody(
          new Text("The service request is incomplete. Please complete the location field"));
      incContent.setActions(btnClose2);
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
    btnClose5.setOnAction(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            finalDialogInc.close();
          }
        });
    return check;
  }

  @FXML
  private void handleCancelAction(ActionEvent e) {
    mainController.swapPanes("homePane");
    clear();
  }

  @Override
  public boolean addRequestToDB(String name, String email, boolean urgency)
      throws MessagingException {
    if (handleDialog()) {
      em.sendEmail(
          email, "Computer Repair Request Submitted", "Your request was submitted successfully!");
      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
      LocalDateTime now = LocalDateTime.now();

      Record r = new Record();
      r.addProperty("id", ""); // Gets automatically generated
      r.addProperty("Date", dtf.format(now));
      r.addProperty("Type", "Computer");
      r.addProperty("Assigned", "");
      r.addProperty("Complete", "false");
      r.addProperty("Location", locationBox.getValue());
      r.addProperty("Name", name);
      r.addProperty("Urgency", String.valueOf(urgency));
      r.addProperty("Email", email);
      r.addProperty(
          "Description",
          "Repair Type:" + reqTypeBox.getValue() + "|" + "Description:" + descReqBox.getText());
      db.addRecord("REQUESTS", r);
      clear(); // on successful submission clear form
      return true;
    }
    return false;
  }

  @Override
  public void clearFields() {
    clear();
  }
}
