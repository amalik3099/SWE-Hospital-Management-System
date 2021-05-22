package edu.wpi.teamA.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.teamA.services.AuthService;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javax.inject.Inject;

public class AccountVerification implements Initializable {

  @FXML private AnchorPane VerificationPane;
  @FXML private JFXButton btnVerify;
  @FXML private JFXButton btnCancel;
  @FXML private StackPane dialogPane;
  @FXML private JFXTextField verificationTxt;

  @Inject AuthService as;
  @Inject MenuController menuController;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    VerificationPane.setVisible(false);
  }

  public void handleVerifyAction(ActionEvent e) {
    if (as.validate(verificationTxt.getText())) {
      JFXDialogLayout successDialog = new JFXDialogLayout();
      successDialog.setHeading(new Text("Verification successful"));
      successDialog.setBody(new Text("Click close to return to the login page"));
      JFXDialog dialog =
          new JFXDialog(dialogPane, successDialog, JFXDialog.DialogTransition.CENTER);
      JFXButton close = new JFXButton("Close");
      successDialog.setActions(close);
      dialog.show();
      close.setOnAction(
          new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
              dialog.close();
              clear();
              menuController.swapAccountPanes("loginPane");
              menuController.slideThin.setVisible(true);
              menuController.slideWide.setVisible(false);
            }
          });
    } else {
      JFXDialogLayout failDialog = new JFXDialogLayout();
      failDialog.setHeading(new Text("Verification failed"));
      failDialog.setBody(new Text("Click close and try again"));
      JFXDialog dialog = new JFXDialog(dialogPane, failDialog, JFXDialog.DialogTransition.CENTER);
      JFXButton close = new JFXButton("Close");
      failDialog.setActions(close);
      dialog.show();
      close.setOnAction(
          new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
              dialog.close();
              clear();
            }
          });
    }
  }

  public void handleCancelAction(ActionEvent e) {
    menuController.swapAccountPanes("signupPane");
    clear();
  }

  public void clear() {
    verificationTxt.setText("");
  }
}
