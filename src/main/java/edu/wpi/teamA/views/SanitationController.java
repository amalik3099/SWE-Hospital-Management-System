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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javax.mail.MessagingException;
import lombok.SneakyThrows;

public class SanitationController implements Initializable, InnerServiceController {

  @Inject FXMLLoader loader;
  @Inject CentralServiceController mainController;
  @Inject DatabaseService db;
  @Inject EmailService em;
  @Inject HomeState homeState;

  @FXML private AnchorPane sanitationPane;
  @FXML private JFXTextArea descriptionText;
  @FXML private JFXComboBox<String> locationBox;
  @FXML private JFXCheckBox checkBio;
  @FXML private JFXCheckBox checkGlass;
  @FXML private JFXCheckBox checkDisinfect;
  @FXML private JFXCheckBox checkLiquid;
  @FXML private StackPane dialogPane;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    sanitationPane.setVisible(false);
    locationBox.setItems(homeState.getNodeList());
  }

  @FXML
  private void handleClearAction(ActionEvent event) {
    clear();
  }

  @FXML
  private void handleCancelAction(ActionEvent e) throws IOException {
    mainController.swapPanes("homePane");
    clear();
  }

  //  @FXML
  //  private void handleSubmitAction(ActionEvent e) throws MessagingException {
  //    if (handleDialog()) {
  //      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
  //      LocalDateTime now = LocalDateTime.now();
  //
  //      Record sanitationR = new Record();
  //      sanitationR.addProperty("type", "Sanitation");
  //      sanitationR.addProperty("date", dtf.format(now));
  //      sanitationR.addProperty("assigned", "");
  //      sanitationR.addProperty("complete", "false");
  //      sanitationR.addProperty("location", locationText.getText());
  //      sanitationR.addProperty(
  //          "description",
  //          "desc: "
  //              + descriptionText.getText()
  //              + "; Bio: "
  //              + Boolean.toString(checkBio.isSelected())
  //              + "; Glass: "
  //              + Boolean.toString(checkGlass.isSelected())
  //              + "; Disinfect: "
  //              + Boolean.toString(checkGlass.isSelected())
  //              + "; Liquid: "
  //              + Boolean.toString(checkLiquid.isSelected()));
  //
  //      db.addRecord("REQUESTS", sanitationR);
  //
  //      em.sendEmail(
  //          emailText.getText(),
  //          "Sanitation Request Submitted",
  //          "Your request was submitted successfully!");
  //      clear();
  //    }
  //  }

  private boolean isLocationCompleted() {
    return !(locationBox.getValue() != null && locationBox.getValue().equals(""));
  }

  private boolean isDescCompleted() {
    return !descriptionText.getText().equals("");
  }

  private boolean isCheckBoxCompleted() {
    return (checkBio.isSelected()
        || checkGlass.isSelected()
        || checkDisinfect.isSelected()
        || checkLiquid.isSelected());
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
      if (isCheckBoxCompleted()) {
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
            new Text(
                "The service request is incomplete. Please select one of the sanitation options"));
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

  private void clear() {
    descriptionText.setText("");
    locationBox.setValue("");
    checkBio.setSelected(false);
    checkGlass.setSelected(false);
    checkDisinfect.setSelected(false);
    checkLiquid.setSelected(false);
  }

  @Override
  public boolean addRequestToDB(String name, String email, boolean urgency)
      throws MessagingException {
    if (handleDialog()) {
      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
      LocalDateTime now = LocalDateTime.now();

      Record sanitationR = new Record();
      sanitationR.addProperty("id", "");
      sanitationR.addProperty("Type", "Sanitation");
      sanitationR.addProperty("Date", dtf.format(now));
      sanitationR.addProperty("Assigned", "");
      sanitationR.addProperty("Name", name);
      sanitationR.addProperty("Email", email);
      sanitationR.addProperty("Urgency", String.valueOf(urgency));
      sanitationR.addProperty("Complete", "false");
      sanitationR.addProperty("Location", locationBox.getValue());
      sanitationR.addProperty(
          "Description",

          //              + descriptionText.getText()
          //              + "; Bio: "
          //              + Boolean.toString(checkBio.isSelected())
          //              + "; Glass: "
          //              + Boolean.toString(checkGlass.isSelected())
          //              + "; Disinfect: "
          //              + Boolean.toString(checkGlass.isSelected())
          //              + "; Liquid: "
          //              + Boolean.toString(checkLiquid.isSelected())
          "Biological Materials?:"
              + (checkBio.isSelected() ? "Y" : "N")
              + "|"
              + "Glass?:"
              + (checkGlass.isSelected() ? "Y" : "N")
              + "|"
              + "Disinfectant?:"
              + (checkDisinfect.isSelected() ? "Y" : "N")
              + "|"
              + "Liquid Spill?:"
              + (checkLiquid.isSelected() ? "Y" : "N")
              + "|"
              + "Additional Details:"
              + descriptionText.getText());

      db.addRecord("REQUESTS", sanitationR);

      em.sendEmail(
          email, "Sanitation Request Submitted", "Your request was submitted successfully!");
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
