package edu.wpi.teamA.views;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.teamA.services.AuthService;
import edu.wpi.teamA.services.DatabaseService;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class RealAccountInfoController implements Initializable {

  @FXML private JFXButton closeButton;
  @FXML private JFXButton uploadPfp;
  @FXML private JFXTextField nameEdit;
  @FXML private JFXTextField emailEdit;
  @FXML private JFXTextField usernameEdit;
  @FXML public Label accountType;
  @FXML public Label name;
  @FXML private Circle pfpCircle;
  @FXML public Label email;
  @FXML public Label username;
  @FXML private JFXButton settingsButton;
  @FXML private JFXButton logoutButton;
  @FXML private AnchorPane accountInfoPane;
  boolean isEditing;

  @Inject MenuController menuController;
  @Inject DatabaseService db;
  @Inject AuthService as;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    accountInfoPane.setVisible(false);
    uploadPfp.setVisible(false);
    nameEdit.setVisible(false);
    emailEdit.setVisible(false);
    nameEdit.setVisible(false);
    isEditing = false;
    pfpCircle.setFill(new ImagePattern(new Image("edu/wpi/teamA/assets/img/defaultUser.png")));
    menuController.accountCircle.setFill(
        new ImagePattern(new Image("edu/wpi/teamA/assets/img/defaultUser.png")));

    username.textProperty().bind(as.getUserData().get(0));
    email.textProperty().bind(as.getUserData().get(1));
    name.textProperty().bind(as.getUserData().get(2));
    as.getUserData()
        .get(0)
        .addListener(
            new ChangeListener<String>() {
              @Override
              public void changed(
                  ObservableValue<? extends String> observable, String oldValue, String newValue) {

                if (as.getAuthLevel() == 3) {
                  accountType.textProperty().set("Admin");
                } else if (as.getAuthLevel() == 2) {
                  accountType.textProperty().set("Employee");
                } else if (as.getAuthLevel() == 1) {
                  accountType.textProperty().set("Patient");
                } else {
                  accountType.textProperty().set("Guest");
                }
              }
            });
  }

  public void handleLogoutAction(ActionEvent event) {
    menuController.setCheckUser(false);
    menuController.swapAccountPanes("loginPane");
    menuController.swapPanes("pathfindingPane");
    menuController.slideThin.setVisible(true);
    pfpCircle.setFill(new ImagePattern(new Image("edu/wpi/teamA/assets/img/defaultUser.png")));
    menuController.accountCircle.setFill(
        new ImagePattern(new Image("edu/wpi/teamA/assets/img/defaultUser.png")));
  }

  public void handleSettingsButton(ActionEvent event) {
    if (!isEditing) {

      settingsButton.setText("Save");
      logoutButton.setVisible(false);
      closeButton.setVisible(false);
      uploadPfp.setVisible(true);

      usernameEdit.setText(username.getText());
      username.setVisible(false);
      usernameEdit.setVisible(true);

      emailEdit.setText(email.getText());
      email.setVisible(false);
      emailEdit.setVisible(true);

      nameEdit.setText(name.getText());
      name.setVisible(false);
      nameEdit.setVisible(true);

      isEditing = true;

    } else if (isEditing) {

      settingsButton.setText("Settings");
      logoutButton.setVisible(true);
      closeButton.setVisible(true);
      uploadPfp.setVisible(false);

      as.setUserData("id", usernameEdit.getText());
      usernameEdit.setVisible(false);
      username.setVisible(true);

      as.setUserData("email", emailEdit.getText());
      emailEdit.setVisible(false);
      email.setVisible(true);

      as.setUserData("name", nameEdit.getText());
      nameEdit.setVisible(false);
      name.setVisible(true);

      isEditing = false;
    }
  }

  public void handleUploadPfp(ActionEvent event) {
    Stage stage = (Stage) uploadPfp.getScene().getWindow();
    FileChooser fileChooser = new FileChooser();
    fileChooser
        .getExtensionFilters()
        .add(new FileChooser.ExtensionFilter("IMG files", "*.jpg", "*.png", "*.gif"));
    File img = fileChooser.showOpenDialog(stage);
    pfpCircle.setFill(new ImagePattern(new Image(img.toURI().toString())));
    menuController.accountCircle.setFill(new ImagePattern(new Image(img.toURI().toString())));
  }

  public void handleCloseAction(ActionEvent event) {
    menuController.loginContent.setVisible(false);
    menuController.slideThin.setVisible(false);
    menuController.accountInfoButton.setVisible(true);
    menuController.removeLoginPage();
  }
}
