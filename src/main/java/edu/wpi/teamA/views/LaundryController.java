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
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javax.mail.*;

public class LaundryController implements Initializable, InnerServiceController {

  @FXML public AnchorPane laundryPane;
  @FXML private JFXComboBox<String> loadSize;
  @FXML private JFXComboBox<String> locationDropdown;
  @FXML private JFXCheckBox isDelicate;
  @FXML private JFXCheckBox isWashFold;
  @FXML private JFXCheckBox isSteamPress;
  @FXML private JFXTextArea otherConcerns;
  @FXML private StackPane dialogPane;

  @Inject CentralServiceController mainController;
  @Inject HomeState homeState;
  @Inject DatabaseService db;
  @Inject EmailService em;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    laundryPane.setVisible(false);
    loadSize.setItems(FXCollections.observableArrayList("Small", "Medium", "Large"));
    locationDropdown.setItems(homeState.getNodeList());
    loadSize.setValue("");
    locationDropdown.setValue("");
  }

  @FXML
  private void handleClearAction(ActionEvent event) {
    clear();
  }

  //  @FXML
  //  private void handleSubmitAction(ActionEvent e) throws MessagingException {
  //    if (handleDialog()) {
  //      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
  //      LocalDateTime now = LocalDateTime.now();
  //
  //      String desc = String.format("%s load of laundry", loadSize.getValue());
  //
  //      if (isDelicate.isSelected()) desc += "; delicates involved";
  //      if (isWashFold.isSelected()) desc += "; requires wash and fold";
  //      if (isSteamPress.isSelected()) desc += "; steam press afterward";
  //
  //      Record r = new Record();
  //      r.addProperty("id", ""); // Gets automatically generated
  //      r.addProperty("date", dtf.format(now));
  //      r.addProperty("type", "Laundry");
  //      r.addProperty("assigned", "");
  //      r.addProperty("complete", "false");
  //      r.addProperty("location", locationDropdown.getValue());
  //      r.addProperty("description", desc + "; " + otherConcerns.getText());
  //      db.addRecord("REQUESTS", r);
  //
  //      em.sendEmail(
  //          "TeamA3733@gmail.com",
  //          "Laundry Request Submitted",
  //          "Your request was submitted successfully!");
  //      clear();
  //    }
  //  }

  @FXML
  private void handleCancelAction(ActionEvent e) throws IOException {
    mainController.swapPanes("homePane");
    clear();
  }

  private void clear() {
    locationDropdown.setValue("");
    loadSize.setValue("");
    otherConcerns.setText("");
    isDelicate.setSelected(false);
    isSteamPress.setSelected(false);
    isWashFold.setSelected(false);
  }

  private boolean isCheckBoxCompleted() {
    return (isDelicate.isSelected() || isWashFold.isSelected() || isSteamPress.isSelected());
  }

  private boolean isDescriptionCompleted() {
    return !otherConcerns.getText().equals("");
  }

  private boolean isLoadSizeCompleted() {
    return !loadSize.getValue().equals("");
  }

  private boolean isDeliveryLocationCompleted() {
    return !locationDropdown.getValue().equals("");
  }

  private Boolean handleDialog() throws MessagingException {
    JFXDialogLayout incContent = new JFXDialogLayout();
    incContent.setHeading(new Text("Incomplete Service Request"));
    JFXDialog dialogComp = new JFXDialog();
    JFXDialog dialogInc = new JFXDialog();
    JFXButton btnClose = new JFXButton("Close");
    JFXButton btnClose1 = new JFXButton("Close");
    JFXButton btnClose2 = new JFXButton("Close");
    JFXButton btnClose3 = new JFXButton("Close");
    JFXButton btnClose4 = new JFXButton("Close");
    Boolean check = false;
    if (isDeliveryLocationCompleted()) {
      if (isLoadSizeCompleted()) {
        if (isCheckBoxCompleted()) {
          if (isDescriptionCompleted()) {
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
                new Text(
                    "The service request is incomplete. Please complete the description field"));
            incContent.setActions(btnClose1);
            dialogInc = new JFXDialog(dialogPane, incContent, JFXDialog.DialogTransition.CENTER);
            dialogInc.show();
          }
        } else {
          incContent.setBody(
              new Text(
                  "The service request is incomplete. Please complete the additional information field"));
          incContent.setActions(btnClose2);
          dialogInc = new JFXDialog(dialogPane, incContent, JFXDialog.DialogTransition.CENTER);
          dialogInc.show();
        }
      } else {
        incContent.setBody(
            new Text("The service request is incomplete. Please complete the load size field"));
        incContent.setActions(btnClose3);
        dialogInc = new JFXDialog(dialogPane, incContent, JFXDialog.DialogTransition.CENTER);
        dialogInc.show();
      }
    } else {
      incContent.setBody(
          new Text("The service request is incomplete. Please complete the location field"));
      incContent.setActions(btnClose4);
      dialogInc = new JFXDialog(dialogPane, incContent, JFXDialog.DialogTransition.CENTER);
      dialogInc.show();
    }
    JFXDialog finalDialogComp = dialogComp;
    JFXDialog finalDialogInc = dialogInc;
    btnClose.setOnAction(
        new EventHandler<ActionEvent>() {
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
      r.addProperty("Type", "Laundry");
      r.addProperty("Assigned", "");
      r.addProperty("Complete", "false");
      r.addProperty("Email", email);
      r.addProperty("Name", name);
      r.addProperty("Location", locationDropdown.getValue());
      r.addProperty(
          "Description",
          "Size of Load:"
              + loadSize.getValue()
              + "|"
              + "Delicates?:"
              + (isDelicate.isSelected() ? "Y" : "N")
              + "|"
              + "Wash and Fold?:"
              + (isWashFold.isSelected() ? "Y" : "N")
              + "|"
              + "SteamPress?:"
              + (isSteamPress.isSelected() ? "Y" : "N")
              + "|"
              + "Additional Details:"
              + otherConcerns.getText());
      r.addProperty("Urgency", String.valueOf(urgency));
      db.addRecord("REQUESTS", r);

      em.sendEmail(email, "Laundry Request Submitted", "Your request was submitted successfully!");
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
