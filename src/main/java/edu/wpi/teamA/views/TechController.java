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
import javax.mail.MessagingException;
import lombok.SneakyThrows;

public class TechController implements Initializable, InnerServiceController {

  @FXML private AnchorPane avPane;
  @FXML private JFXCheckBox checkTV;
  @FXML private JFXCheckBox checkComputer;
  @FXML private JFXCheckBox checkInternet;
  @FXML private JFXCheckBox checkHospital;
  @FXML private JFXCheckBox checkOther;
  @FXML private JFXTextArea textAreaDescription;
  @FXML private JFXComboBox<String> comboLocation;
  @FXML private StackPane dialogPane;

  @Inject CentralServiceController mainController;
  @Inject HomeState homeState;
  @Inject DatabaseService db;
  @Inject EmailService em;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    avPane.setVisible(false);
    comboLocation.setItems(homeState.getNodeList());
    comboLocation.setValue("");
  }

  @FXML
  private void handleClearAction(ActionEvent event) {
    clear();
  }

  private void clear() {
    checkTV.setSelected(false);
    checkComputer.setSelected(false);
    checkInternet.setSelected(false);
    checkHospital.setSelected(false);
    checkOther.setSelected(false);
    textAreaDescription.setText("");
    comboLocation.setValue("");
    // comboLocation.setValue("Select a location...");
  }

  @FXML
  private void handleCancelAction(ActionEvent e) throws IOException {
    mainController.swapPanes("homePane");
    clear();
  }

  private boolean isCheckBoxSelected() {
    return checkComputer.isSelected()
        || checkInternet.isSelected()
        || checkTV.isSelected()
        || checkHospital.isSelected()
        || checkOther.isSelected();
  }

  private boolean isDescCompleted() {
    return !textAreaDescription.getText().equals("");
  }

  private boolean isLocationCompleted() {
    return !comboLocation.getValue().equals("");
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
      if (isCheckBoxSelected()) {
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
            new Text("The service request is incomplete. Please select atleast one checkbox"));
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

  private String getSelected() {
    String selcted = "";
    if (checkOther.isSelected()) {
      selcted = selcted.concat(" Other ");
    }
    if (checkHospital.isSelected()) {
      selcted = selcted.concat("Hospital ");
    }
    if (checkTV.isSelected()) {
      selcted = selcted.concat(" TV ");
    }
    if (checkComputer.isSelected()) {
      selcted = selcted.concat(" Computer ");
    }
    if (checkInternet.isSelected()) {
      selcted = selcted.concat(" Internet ");
    }
    System.out.println(selcted);
    return selcted;
  }

  //  @FXML
  //  private void handleSubmitAction(ActionEvent e) throws MessagingException {
  //    if (handleDialog()) {
  //      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
  //      LocalDateTime now = LocalDateTime.now();
  //      Record r = new Record();
  //      r.addProperty("id", ""); // Gets automatically generated
  //      r.addProperty("date", dtf.format(now));
  //      r.addProperty("type", "Tech Service Request");
  //      r.addProperty("assigned", "");
  //      r.addProperty("complete", "false");
  //      r.addProperty("location", (String) comboLocation.getValue());
  //
  //      r.addProperty(
  //          "description", "type: " + getSelected() + " desc, " + textAreaDescription.getText());
  //
  //      db.addRecord("REQUESTS", r);
  //      em.sendEmail(
  //          email.getText(), "Tech Request Submitted", "Your request was submitted
  // successfully!");
  //      clear();
  //    }

  //    if (comboLocation.getValue().equals("Select a location...")) {
  //      System.out.println("Missing location data!");
  //      comboLocation.requestFocus();
  //    }
  //
  //    if (textAreaDescription.getText().equals("")) {
  //      System.out.println("Missing problem description!");
  //      textAreaDescription.requestFocus();
  //    }
  //
  //    if (!checkTV.isSelected()
  //        && !checkComputer.isSelected()
  //        && !checkInternet.isSelected()
  //        && !checkHospital.isSelected()
  //        && !checkOther.isSelected()) {
  //      System.out.println("No check box selected!");
  //      //      checkTV.setBorder();
  //      //      checkComputer.setBorder();
  //      //      checkInternet.setBorder();
  //      //      checkHospital.setBorder();
  //      //      checkHospital.setBorder();
  //    }
  // }

  @Override
  public boolean addRequestToDB(String name, String email, boolean urgency)
      throws MessagingException {
    if (handleDialog()) {
      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
      LocalDateTime now = LocalDateTime.now();
      Record r = new Record();
      r.addProperty("id", ""); // Gets automatically generated
      r.addProperty("Date", dtf.format(now));
      r.addProperty("Type", "Audio/Visual");
      r.addProperty("Assigned", "");
      r.addProperty("Complete", "false");
      r.addProperty("Location", comboLocation.getValue());
      r.addProperty("Name", name);
      r.addProperty("Urgency", String.valueOf(urgency));
      r.addProperty("Email", email);

      r.addProperty(
          // "description", "type: " + getSelected() + " desc, " + textAreaDescription.getText());
          "Description",
          "TV?:"
              + (checkTV.isSelected() ? "Y" : "N")
              + "|"
              + "Computer?:"
              + (checkComputer.isSelected() ? "Y" : "N")
              + "|"
              + "Internet?:"
              + (checkInternet.isSelected() ? "Y" : "N")
              + "|"
              + "Hospital Equipment?:"
              + (checkHospital.isSelected() ? "Y" : "N")
              + "|"
              + "Other?:"
              + (checkOther.isSelected() ? "Y" : "N")
              + "|"
              + "Additional Details:"
              + textAreaDescription.getText());

      db.addRecord("REQUESTS", r);
      em.sendEmail(email, "Tech Request Submitted", "Your request was submitted successfully!");
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
