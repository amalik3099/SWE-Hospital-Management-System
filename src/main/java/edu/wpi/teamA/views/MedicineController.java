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
import lombok.SneakyThrows;

public class MedicineController implements Initializable, InnerServiceController {

  @FXML private JFXButton btnSubmit;
  @FXML private JFXButton btnCancel;
  @FXML private JFXButton btnClear;
  @FXML private JFXComboBox<String> deliveryLocation;
  @FXML private JFXComboBox<String> medsQuantity;
  //  @FXML private JFXCheckBox checkYes;
  //  @FXML private JFXCheckBox checkNo;
  @FXML private JFXTextArea description;
  @FXML private JFXTextField medsType;
  @FXML private JFXDialog dialog;
  @FXML private AnchorPane medicinePane;
  @FXML private StackPane dialogPane;

  @Inject CentralServiceController mainController;
  @Inject HomeState homeState;
  @Inject DatabaseService db;
  @Inject EmailService em;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    medicinePane.setVisible(false);
    deliveryLocation.setItems(homeState.getNodeList());
    medsQuantity.setItems(FXCollections.observableArrayList("1", "2", "3", "4", "5"));
    medsQuantity.setValue("");
    deliveryLocation.setValue("");
  }

  @FXML
  private void handleClearAction(ActionEvent event) {
    clear();
  }

  //  @FXML
  //  private void handleSubmitAction(ActionEvent e) throws MessagingException {
  //
  //    if (handleDialog()) {
  //      em.sendEmail(
  //          "TeamA3733@gmail.com",
  //          "Medicine Request Submitted",
  //          "Your request was submitted successfully!");
  //      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
  //      LocalDateTime now = LocalDateTime.now();
  //
  //      Record r = new Record();
  //      r.addProperty("id", ""); // Gets automatically generated
  //      r.addProperty("date", dtf.format(now));
  //      r.addProperty("type", "Medicine");
  //      r.addProperty("assigned", "");
  //      r.addProperty("complete", "false");
  //      r.addProperty("location", deliveryLocation.getValue());
  //      r.addProperty(
  //          "description",
  //          medsType.getText() + "; " + medsQuantity.getValue() + "; " + description.getText());
  //      r.addProperty("urgency", String.valueOf(isCheckBoxCompleted()));
  //      db.addRecord("REQUESTS", r);
  //      clear();
  //    }
  //  }

  @FXML
  private void handleCancelAction(ActionEvent e) throws IOException {
    mainController.swapPanes("homePane");
    clear();
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

  private void clear() {
    deliveryLocation.setValue("");
    medsQuantity.setValue("");
    description.setText("");
    medsType.clear();
    //    if (checkYes.isSelected()) {
    //      checkYes.setSelected(false);
    //    } else if (checkNo.isSelected()) {
    //      checkNo.setSelected(false);
    //    }
  }

  //  private boolean isCheckBoxCompleted() {
  //    if (checkYes.isSelected()) {
  //      return true;
  //    }
  //    return false;
  //  }

  private boolean isMedsTypeCompleted() {
    return !medsType.getText().equals("");
  }

  private boolean isDescriptionCompleted() {
    return !description.getText().equals("");
  }

  private boolean isMedsQuantityCompleted() {
    return !medsQuantity.getValue().equals("");
  }

  private boolean isDeliveryLocationCompleted() {
    return !deliveryLocation.getValue().equals("");
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
    if (isDeliveryLocationCompleted()) {
      if (isMedsQuantityCompleted()) {
        if (isMedsTypeCompleted()) {
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
                  "The service request is incomplete. Please complete the medication type field"));
          incContent.setActions(btnClose2);
          dialogInc = new JFXDialog(dialogPane, incContent, JFXDialog.DialogTransition.CENTER);
          dialogInc.show();
        }
      } else {
        incContent.setBody(
            new Text(
                "The service request is incomplete. Please complete the medicine quantity field"));
        incContent.setActions(btnClose3);
        dialogInc = new JFXDialog(dialogPane, incContent, JFXDialog.DialogTransition.CENTER);
        dialogInc.show();
      }
    } else {
      incContent.setBody(
          new Text(
              "The service request is incomplete. Please complete the delivery location field"));
      incContent.setActions(btnClose4);
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
  public void clearFields() {
    clear();
  }

  @Override
  public boolean addRequestToDB(String name, String email, boolean urgency)
      throws MessagingException {
    if (handleDialog()) {
      em.sendEmail(email, "Medicine Request Submitted", "Your request was submitted successfully!");
      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
      LocalDateTime now = LocalDateTime.now();

      Record r = new Record();
      r.addProperty("id", ""); // Gets automatically generated
      r.addProperty("Name", name);
      r.addProperty("Date", dtf.format(now));
      r.addProperty("Type", "Medicine");
      r.addProperty("Assigned", "");
      r.addProperty("Complete", "false");
      r.addProperty("Email", email);
      r.addProperty("Name", name);
      r.addProperty("Location", deliveryLocation.getValue());
      r.addProperty("Urgency", String.valueOf(urgency));
      r.addProperty(
          "Description",
          // medsType.getText() + "; " + medsQuantity.getValue() + "; " + description.getText()
          "Type of Medication:"
              + medsType.getText()
              + "|"
              + "Quantity:"
              + medsQuantity.getValue()
              + "|"
              + "Description:"
              + description.getText());

      db.addRecord("REQUESTS", r);
      clear();
      return true;
    }
    return false;
  }
}
