package edu.wpi.teamA.views;

// import com.google.firebase.auth.FirebaseAuth;
// import com.google.firebase.auth.FirebaseAuthException;
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

public class SignupViewController implements Initializable {

  @FXML private JFXPasswordField confirmPasswordField;
  @FXML private Label passwordMatch;
  @FXML private Label passwordNoMatch;
  @FXML private JFXTextField emailTxt;
  @FXML private JFXButton cancelButton;
  @FXML private JFXButton continueButton;
  @FXML private JFXPasswordField passwordTxt;
  @FXML private JFXTextField usernameTxt;
  @FXML private AnchorPane signupPane;
  @FXML private Label smallLabel;

  @Inject MenuController menuController;
  @Inject DatabaseService db;
  @Inject AuthService as;

  //  private FirebaseAuth mAuth;

  private final int state = 1;
  private String username = "";
  private String pass = "";
  private String currentPaneID;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    signupPane.setVisible(false);

    confirmPasswordField.setOnKeyReleased(
        keyEvent -> {
          System.out.println(
              "does " + passwordTxt.getText() + " equal " + confirmPasswordField.getText() + "?");
          if (passwordTxt.getText().equals(confirmPasswordField.getText())) {
            passwordMatch.setVisible(true);
            passwordNoMatch.setVisible(false);
          } else {
            passwordNoMatch.setVisible(true);
            passwordMatch.setVisible(false);
          }
        });
  }

  public void handleCreateAccount(ActionEvent event) {
    boolean emailCheck = false;
    boolean passwordCheck = false;
    String username = "";
    String email = "";
    String password = "";

    // Check if all fields are filled
    boolean fieldsCheck =
        usernameTxt.getText().equals("")
            || passwordTxt.getText().equals("")
            || emailTxt.getText().equals("")
            || confirmPasswordField.getText().equals("");

    if (fieldsCheck) {
      smallLabel.setText("Please fill out all the fields");
      smallLabel.setVisible(true);
      smallLabel.setStyle("-fx-text-fill: red");
      return;
    } else {
      username = usernameTxt.getText();
      email = emailTxt.getText();
      password = passwordTxt.getText();

      int i = email.indexOf('@');
      emailCheck = (i != -1);
      emailCheck = emailCheck && !email.substring(0, i).matches("[^a-zA-Z0-9!#$%&'*+/=?^_`{|}~.-]");
    }

    if (!confirmPasswordField.getText().equals(passwordTxt.getText())) {
      smallLabel.setText("Passwords do not match!");
      smallLabel.setVisible(true);
      smallLabel.setStyle("-fx-text-fill: red");
      return;
    }

    // Check if we have a semi-valid email
    if (!emailCheck) {
      smallLabel.setText(
          "Email address may only contains letters, numbers, or the following special characters: !#$%&'*+/=?^_`{|}~.-");
      smallLabel.setVisible(true);
      smallLabel.setStyle("-fx-text-fill: red");
      return;
    } else {
      passwordCheck = !password.matches("[^a-zA-Z0-9!#$%&'*+/=?^_`{|}~.-]");
    }

    // Check if we have a password with only valid characters
    if (!passwordCheck) {
      smallLabel.setText("Password may only contains letters, numbers, or special characters");
      smallLabel.setVisible(true);
      smallLabel.setStyle("-fx-text-fill: red");
      return;
    } else {
      System.out.println("trying to make an account");
      smallLabel.setVisible(false);
      int create = as.createUser(username, email, password);
      if (create == 0) {
        this.username = username;
        this.pass = password;
        menuController.swapAccountPanes("VerificationPane");
        menuController.slideThin.setVisible(true);
        //        menuController.slideWide.setVisible(false);
        clear();
        //        menuController.swapAccountPanes("loginPane");
      } else if (create == 1) {
        smallLabel.setText("Username already in use");
        smallLabel.setVisible(true);
        smallLabel.setStyle("-fx-text-fill: red");

        return;
      } else if (create == 2) {
        smallLabel.setText("Email already in use");
        smallLabel.setVisible(true);
        smallLabel.setStyle("-fx-text-fill: red");

        return;
      }
    }
  }

  public void handleCancel(ActionEvent event) {
    //    menuController.swapAccountPanes("VerificationPane");
    menuController.swapAccountPanes("loginPane");
    menuController.slideThin.setVisible(true);
    menuController.slideWide.setVisible(false);
    clear();
  }

  public void clear() {
    usernameTxt.setText("");
    emailTxt.setText("");
    passwordTxt.setText("");
    confirmPasswordField.setText("");
  }
}
