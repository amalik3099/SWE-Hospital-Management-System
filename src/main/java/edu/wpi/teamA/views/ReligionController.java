package edu.wpi.teamA.views;

import com.google.inject.Inject;
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
import javax.mail.MessagingException;

public class ReligionController implements Initializable, InnerServiceController {
  @FXML private AnchorPane religiousPane;
  @FXML private JFXComboBox<String> serviceComboBox;
  @FXML private JFXComboBox<String> locationCombo;
  @FXML private StackPane dialogPane;
  @FXML private JFXTextArea description;

  @Inject DatabaseService db;
  @Inject CentralServiceController mainController;
  @Inject HomeState homeState;
  @Inject EmailService em;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    locationCombo.setItems(homeState.getNodeList());
    locationCombo.setValue("");
    religiousPane.setVisible(false);
    serviceComboBox.setItems(
        FXCollections.observableArrayList(
            "Interfaith Chaplin Request",
            "Preoperative Visit",
            "Blessing",
            "Communion",
            "Spirituality Group"));
  }

  private void clear() {
    serviceComboBox.setValue("");
    description.setText("");
    locationCombo.setValue("");
  }

  @FXML
  private void handleClearAction() {
    clear();
  }

  @FXML
  private void handleCancelAction() {
    mainController.swapPanes("homePane");
    clear();
  }

  //  @FXML
  //  private void handleSubmitAction(ActionEvent e) throws MessagingException {
  //
  //    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
  //    LocalDateTime now = LocalDateTime.now();
  //
  //    Record newRequest = new Record();
  //    newRequest.addProperty("id", "");
  //    newRequest.addProperty("type", "Religion Service");
  //    newRequest.addProperty("assigned", "");
  //    newRequest.addProperty("complete", "false");
  //    newRequest.addProperty("email", emailText.getText());
  //    newRequest.addProperty("date", dtf.format(now));
  //
  //    if (handleDialog()) {
  //      newRequest.addProperty(
  //          "description",
  //          roomNumber.getText()
  //              + "; "
  //              + fullName.getText()
  //              + "; "
  //              + serviceComboBox.getValue()
  //              + ";");
  //      db.addRecord("REQUESTS", newRequest);
  //      em.sendEmail(
  //          emailText.getText(),
  //          "Religious Request Submitted",
  //          "Your request was submitted successfully!");
  //    }
  //  }
  //
  private boolean isLocationCompleted() {
    return !locationCombo.getValue().equals("");
  }

  private boolean isServiceFilled() {
    return !serviceComboBox.getSelectionModel().isEmpty();
  }

  private boolean isDescriptionCompleted() {
    return !description.getText().equals("");
  }

  private boolean handleDialog() {
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
    if (isServiceFilled()) {
      if (isLocationCompleted()) {
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
              new Text("The service request is incomplete. Please complete the description field"));
          incContent.setActions(btnClose1);
          dialogInc = new JFXDialog(dialogPane, incContent, JFXDialog.DialogTransition.CENTER);
          dialogInc.show();
        }
      } else {
        incContent.setBody(new Text("The service request is incomplete. Please select a location"));
        incContent.setActions(btnClose2);
        dialogInc = new JFXDialog(dialogPane, incContent, JFXDialog.DialogTransition.CENTER);
        dialogInc.show();
      }
    } else {
      incContent.setBody(
          new Text("The service request is incomplete. Please enter your type of service"));
      incContent.setActions(btnClose3);
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

      Record newRequest = new Record();
      newRequest.addProperty("id", "");
      newRequest.addProperty("Type", "Religion");
      newRequest.addProperty("Assigned", "");
      newRequest.addProperty("Complete", "false");
      newRequest.addProperty("Email", email);
      newRequest.addProperty("Name", name);
      newRequest.addProperty("Urgency", String.valueOf(urgency));
      newRequest.addProperty("Date", dtf.format(now));
      newRequest.addProperty("Location", locationCombo.getValue());

      newRequest.addProperty(
          "Description",
          "Service Type:"
              + serviceComboBox.getValue()
              + "|"
              + "Additional Details:"
              + description.getText());
      db.addRecord("REQUESTS", newRequest);
      em.sendEmail(
          email, "Religious Request Submitted", "Your request was submitted successfully!");
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
