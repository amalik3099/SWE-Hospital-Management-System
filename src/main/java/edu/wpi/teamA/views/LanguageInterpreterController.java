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

public class LanguageInterpreterController implements Initializable, InnerServiceController {
  @FXML private AnchorPane languagePane;
  @FXML private JFXToggleButton spanish;
  @FXML private JFXToggleButton german;
  @FXML private JFXToggleButton chinese;
  @FXML private JFXToggleButton french;
  @FXML private JFXToggleButton other;
  @FXML private JFXTextArea details;
  @FXML private JFXDatePicker date;
  @FXML private JFXTimePicker time;

  @FXML private JFXCheckBox checkWheelchair;
  @FXML private StackPane dialogPane;

  @Inject FXMLLoader loader;
  @Inject CentralServiceController mainController;
  @Inject HomeState homeState;
  @Inject DatabaseService db;
  @Inject EmailService em;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    languagePane.setVisible(false);
  }

  //  @FXML
  //  private void handleClearAction(ActionEvent event) {
  //    Parent root;
  //    Stage stage = (Stage) btnClear.getScene().getWindow();
  //    clear();
  //  }

  //  @FXML
  //  private void handleCancelAction(ActionEvent e) throws IOException {
  //    mainController.swapPanes("homePane");
  //    clear();
  //  }

  private boolean isLanguageCheckComplete() {
    return chinese.isSelected()
        || french.isSelected()
        || german.isSelected()
        || other.isSelected()
        || spanish.isSelected();
  }

  private boolean isTimeComplete() {
    return time.getValue() != null;
  }

  private boolean isDateCompete() {
    return date.getValue() != null;
  }

  private boolean isDescCompleted() {
    return !details.getText().equals("");
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
    if (isLanguageCheckComplete()) {
      if (isDateCompete()) {
        if (isTimeComplete()) {
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
              new Text("The service request is incomplete. Please complete the time field"));
          incContent.setActions(btnClose3);
          dialogInc = new JFXDialog(dialogPane, incContent, JFXDialog.DialogTransition.CENTER);
          dialogInc.show();
        }
      } else {
        incContent.setBody(
            new Text("The service request is incomplete. Please complete the date field"));
        incContent.setActions(btnClose4);
        dialogInc = new JFXDialog(dialogPane, incContent, JFXDialog.DialogTransition.CENTER);
        dialogInc.show();
      }
    } else {
      incContent.setBody(
          new Text("The service request is incomplete. Please complete the language field"));
      incContent.setActions(btnClose5);
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

  //  @FXML
  //  private void handleSubmitAction(ActionEvent e) throws MessagingException {
  //    if (handleDialog()) {
  //      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
  //      LocalDateTime now = LocalDateTime.now();
  //
  //      Record r = new Record();
  //      r.addProperty("id", "");
  //      r.addProperty("date", dtf.format(now));
  //      r.addProperty("type", "Language Interpreter Request");
  //      r.addProperty("assigned", "");
  //      r.addProperty(
  //          "description",
  //          chooseInterpreter() + " " + wheelChairOption() + " " + otherLanguages.getText());
  //
  //      db.addRecord("REQUESTS", r);
  //      em.sendEmail(
  //          email.getText(),
  //          "Interpreter Request Submitted",
  //          "Your request was submitted successfully!");
  //      clear();
  //    }
  //  }
  //
  @FXML
  private String chooseInterpreter() {
    if (french.isSelected()) {
      return "French";
    } else if (german.isSelected()) {
      return "German";
    } else if (spanish.isSelected()) {
      return "Spanish";
    } else if (chinese.isSelected()) {
      return "Chinese";
    } else {
      return "Other Language";
    }
  }

  @FXML
  private String wheelChairOption() {
    if (checkWheelchair.isSelected()) {
      return "Y";
    } else {
      return "N";
    }
  }

  @FXML
  private void clear() {
    if (checkWheelchair.isSelected()) {
      checkWheelchair.setSelected(false);
    }
    chinese.setSelected(false);
    french.setSelected(false);
    other.setSelected(false);
    spanish.setSelected(false);
    german.setSelected(false);
    date.valueProperty().setValue(null);
    time.valueProperty().setValue(null);
  }

  @Override
  public void clearFields() {
    clear();
  }

  @Override
  public boolean addRequestToDB(String name, String email, boolean urgency)
      throws MessagingException {
    if (handleDialog()) {
      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
      LocalDateTime now = LocalDateTime.now();

      Record r = new Record();
      r.addProperty("id", "");
      r.addProperty("Date", dtf.format(now));
      r.addProperty("Urgency", String.valueOf(urgency));
      r.addProperty("Name", name);
      r.addProperty("Email", email);
      r.addProperty("Complete", "false");
      r.addProperty("Location", "not applicable");
      r.addProperty("Type", "Language");
      r.addProperty("Assigned", "");
      r.addProperty(
          "Description",
          "Interpreter Speciality:"
              + chooseInterpreter()
              + "|"
              + "Wheelchair Needed?:"
              + wheelChairOption()
              + "|"
              + "Additional Details:"
              + details.getText()
              + "|"
              + "Arrival Date:"
              + date.getEditor().getText()
              + "|"
              + "Arrival Time:"
              + time.getEditor().getText());

      db.addRecord("REQUESTS", r);
      em.sendEmail(
          email, "Interpreter Request Submitted", "Your request was submitted successfully!");
      clear();
      return true;
    }
    return false;
  }
}
