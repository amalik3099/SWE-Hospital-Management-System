package edu.wpi.teamA.views;

// import com.google.firebase.auth.FirebaseAuth;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.teamA.services.AuthService;
import edu.wpi.teamA.services.DatabaseService;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javax.inject.Inject;

public class LoginViewController implements Initializable {

  @FXML private AnchorPane loginPane;
  @FXML private JFXButton guestButton;
  @FXML private JFXButton forgotPassword;
  @FXML private JFXButton signUp;
  @FXML private JFXButton loginButton;
  @FXML private Label smallLabel;
  @FXML private JFXPasswordField passwordTxt;
  @FXML private JFXTextField usernameTxt;

  @Inject MenuController menuController;
  @Inject DatabaseService db;
  @Inject AuthService as;
  //  @Inject RealAccountInfoController info;

  //  private FirebaseAuth mAuth;
  private final int state = 1;
  private final String username = "";
  private final String pass = "";
  private String currentPaneID;

  @Override
  public void initialize(URL location, ResourceBundle resources) {}

  public void handleLogin(ActionEvent event) {

    displayCompleteMenuBar();

    Boolean success = as.signIn(usernameTxt.getText(), passwordTxt.getText());

    if (success) {
      smallLabel.setText("Logged in as " + usernameTxt.getText());
      usernameTxt.setText("");
      passwordTxt.setText("");
      smallLabel.setVisible(true);
      smallLabel.setStyle("-fx-text-fill: blue");
      menuController.swapAccountPanes("accountInfoPane");
      menuController.accountInfoButton.setText(as.getUser().getFieldAsString("id"));

      if (as.getUser().getFieldAsString("authlevel").equals("3")) {
        menuController.setCheckUser(true);
        menuController.removeLoginPage();
        menuController.accountInfoButton.setVisible(true);
        menuController.slideThin.setVisible(false);
        displayCompleteMenuBar();
        clear();
      } else if (as.getUser().getFieldAsString("authlevel").equals("2")) {
        menuController.setCheckUser(true);
        menuController.removeLoginPage();
        menuController.requestButton.setVisible(true);
        menuController.employeeButton.setVisible(false);
        menuController.incomingRequestButton.setVisible(true);
        menuController.tableButton.setVisible(false);
        menuController.mapEditorButton.setVisible(false);
        menuController.menuSeparator.setVisible(true);
        menuController.accountInfoButton.setVisible(true);
        menuController.slideThin.setVisible(false);
        clear();
      } else if (as.getUser().getFieldAsString("authlevel").equals("1")) {
        menuController.setCheckUser(true);
        menuController.removeLoginPage();
        menuController.accountInfoButton.setVisible(true);
        menuController.slideThin.setVisible(false);
        setMenuControllerToGuest();
        clear();
      }
    } else {
      usernameTxt.setStyle(
          "-fx-border-color: #ff0000 ; -fx-border-width: 2px ; -fx-background-color: white;");
      passwordTxt.setStyle(
          "-fx-border-color: red ; -fx-border-width: 2px ; -fx-background-color: white;");
      smallLabel.setText("Incorrect Username or Password");
      smallLabel.setVisible(true);
      smallLabel.setStyle("-fx-text-fill: red");
      menuController.accountInfoButton.setVisible(true);
      setMenuControllerToGuest();
    }
  }

  private void setMenuControllerToGuest() {
    menuController.menuSeparator.setVisible(false);
    menuController.menuSeparator1.setVisible(true);
    menuController.requestButton.setVisible(false);
    menuController.employeeButton.setVisible(false);
    menuController.incomingRequestButton.setVisible(false);
    menuController.tableButton.setVisible(false);
    menuController.mapEditorButton.setVisible(false);
  }

  public void handleSignUp(ActionEvent event) {
    menuController.swapAccountPanes("signupPane");
    menuController.slideThin.setVisible(true);
    //    menuController.slideWide.setVisible(false);
    clear();
  }

  public void handleForgotPassword(ActionEvent event) {}

  public void handleGuestButton(ActionEvent event) {
    menuController.removeLoginPage();
    menuController.accountInfoButton.setVisible(true);
    menuController.accountInfoButton.setText("Log in");
    menuController.slideThin.setVisible(false);
    menuController.slideThin.setMouseTransparent(true);
    setMenuControllerToGuest();
    clear();
  }

  public void clear() {
    usernameTxt.setText("");
    passwordTxt.setText("");
    smallLabel.setVisible(false);
    usernameTxt.setStyle(
        "-fx-border-color: white ; -fx-border-width: 2px ; -fx-background-color: white;");
    passwordTxt.setStyle(
        "-fx-border-color: white ; -fx-border-width: 2px ; -fx-background-color: white;");
  }

  public void displayCompleteMenuBar() {
    menuController.requestButton.setVisible(true);
    menuController.employeeButton.setVisible(true);
    menuController.incomingRequestButton.setVisible(true);
    menuController.tableButton.setVisible(true);
    menuController.mapEditorButton.setVisible(true);
    menuController.menuSeparator.setVisible(true);
  }
}
