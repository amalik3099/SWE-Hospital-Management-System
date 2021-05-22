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
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javax.mail.MessagingException;
import lombok.SneakyThrows;

public class FloralController implements Initializable, InnerServiceController {

  @FXML public AnchorPane floralPane;
  @FXML private JFXTextField recipient;
  @FXML private JFXComboBox<String> locationBox;
  @FXML private JFXTextArea specialMessage;
  @FXML private JFXComboBox<String> flowerBox;
  @FXML private JFXToggleButton anonToggle;

  @FXML private StackPane dialogPane;

  @Inject CentralServiceController mainController;
  @Inject HomeState homeState;
  @Inject DatabaseService db;
  @Inject EmailService em;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    floralPane.setVisible(false);
    locationBox.setItems(homeState.getNodeList());

    ObservableList<String> flowerChoices =
        FXCollections.observableArrayList(
            "Roses", "Tulips", "Sunflowers", "Carnations", "Combination");
    flowerBox.setItems(flowerChoices);
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
  //      Record r = new Record();
  //      r.addProperty("id", ""); // Gets automatically generated
  //      r.addProperty("date", dtf.format(now));
  //      r.addProperty("type", "Floral Service Request");
  //      r.addProperty("assigned", "");
  //      r.addProperty("complete", "false");
  //      r.addProperty("location", (String) recLoc.getValue());
  //
  //      r.addProperty(
  //          "description",
  //          "to: "
  //              + recipient.getText()
  //              + " from: "
  //              + yourName.getText()
  //              + " message: "
  //              + specialMessage.getText());
  //
  //      db.addRecord("REQUESTS", r);
  //      em.sendEmail(
  //          email.getText(), "Floral Request Submitted", "Your request was submitted
  // successfully!");
  //      clear();
  //    }
  //  }

  //  @FXML
  //  private void handleYesBox() {
  //    if (yesCheck.isSelected()) {
  //      noCheck.setSelected(false);
  //    }
  //  }
  //
  //  @FXML
  //  private void handleNoBox() {
  //    if (noCheck.isSelected()) {
  //      yesCheck.setSelected(false);
  //    }
  //  }

  private void clear() {
    anonToggle.setSelected(false);
    recipient.setText("");
    specialMessage.setText("");
    flowerBox.setValue("");
    locationBox.setValue("");
  }

  private boolean isRecipientCompleted() {
    return !recipient.getText().equals("");
  }

  private boolean isRecLocCompleted() {
    return !(locationBox.getValue() != null && locationBox.getValue().equals(""));
  }

  private boolean isFlowerSelected() {
    return !(flowerBox.getValue() != null && flowerBox.getValue().equals(""));
  }

  private boolean isDescCompleted() {
    return !specialMessage.getText().equals("");
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

    if (isRecipientCompleted()) {
      if (isFlowerSelected()) {
        if (isRecLocCompleted()) {
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
                new Text(
                    "The service request is incomplete. Please complete the description field"));
            incContent.setActions(btnClose1);
            dialogInc = new JFXDialog(dialogPane, incContent, JFXDialog.DialogTransition.CENTER);
            dialogInc.show();
          }
        } else {
          incContent.setBody(
              new Text("The service request is incomplete. Please choose a location"));
          incContent.setActions(btnClose2);
          dialogInc = new JFXDialog(dialogPane, incContent, JFXDialog.DialogTransition.CENTER);
          dialogInc.show();
        }
      } else {
        incContent.setBody(
            new Text("The service request is incomplete. Please complete a flower type"));
        incContent.setActions(btnClose3);
        dialogInc = new JFXDialog(dialogPane, incContent, JFXDialog.DialogTransition.CENTER);
        dialogInc.show();
      }
    } else {
      incContent.setBody(
          new Text("The service request is incomplete. Please complete the recipient field"));
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
  private void handleCancelAction(ActionEvent e) throws IOException {
    mainController.swapPanes("homePane");
    clear();
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
      r.addProperty("Type", "Floral");
      r.addProperty("Assigned", "");
      r.addProperty("Complete", "false");
      r.addProperty("Location", locationBox.getValue());
      r.addProperty("Email", email);
      r.addProperty("Urgency", String.valueOf(urgency));

      if (anonToggle.isSelected()) {
        name = "Anonymous";
      }
      r.addProperty("Name", name);
      r.addProperty(
          "Description",
          "Recipient:"
              + recipient.getText()
              + "|"
              + "Sender:"
              + name
              + "|"
              + "Type of Flowers:"
              + flowerBox.getValue()
              + "|"
              + "Message:"
              + specialMessage.getText());

      db.addRecord("REQUESTS", r);
      em.sendEmail(email, "Floral Request Submitted", "Your request was submitted successfully!");
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
