package edu.wpi.teamA.views;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import edu.wpi.teamA.services.AuthService;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class LoginController implements Initializable {

  @FXML private AnchorPane loginPane;
  @FXML private GridPane formPane;
  @FXML private JFXButton buttonSubmit;
  @FXML private JFXButton btnSignUp;

  @FXML private JFXTextField usernameTxt;
  @FXML private JFXTextField emailTxt;
  @FXML private JFXPasswordField passwordTxt;
  @FXML private JFXTextField codeTxt;
  @FXML private JFXTextField resetTxt;

  @FXML private MaterialDesignIconView passIcon;
  @FXML private MaterialDesignIconView emailIcon;
  @FXML private Label smallLabel;
  @FXML private Label welcome;
  @FXML private Label codeLabel;
  @FXML private Label resetLabel;
  @FXML private StackPane contentPane;

  @Inject AuthService as;

  private int state = 1;
  private String username = "";
  private String pass = "";
  private String currentPaneID;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    loginPane.setVisible(false);
  }

  @FXML
  public void handleLoginAction() {
    if (state == 0) {
      createAccount();
      return;
    }
    Boolean success = as.signIn(usernameTxt.getText(), passwordTxt.getText());

    if (success) {
      // Pop-up to tell people we succeeded
      usernameTxt.setStyle("-fx-border-color: red ; -fx-border-width: 0px ;");
      passwordTxt.setStyle("-fx-border-color: red ; -fx-border-width: 0px ;");
      smallLabel.setText("Logged in as " + usernameTxt.getText());
      usernameTxt.setText("");
      passwordTxt.setText("");
      smallLabel.setVisible(true);
      smallLabel.setStyle("-fx-text-fill: blue");
    } else {
      usernameTxt.setStyle("-fx-border-color: #ff0000 ; -fx-border-width: 2px ;");
      passwordTxt.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
      smallLabel.setText("Incorrect Username or Password");
      smallLabel.setVisible(true);
      smallLabel.setStyle("-fx-text-fill: red");
    }
  }

  private void createAccount() {
    boolean emailCheck = false;
    boolean passwordCheck = false;
    String username = "";
    String email = "";
    String password = "";

    // Check if all fields are filled
    boolean fieldsCheck =
        usernameTxt.getText().equals("")
            || passwordTxt.getText().equals("")
            || emailTxt.getText().equals("");
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
        swapPanes("certificationPane");
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

  // there is no cancel why is this here?
  @FXML
  public void handleCancelAction() throws IOException {
    swapPanes("formPane");
  }

  @FXML
  public void handleSignUpAction(ActionEvent actionEvent) {
    GridPane.setRowIndex(passwordTxt, 2 + state);
    GridPane.setRowIndex(passIcon, 2 + state);
    GridPane.setRowIndex(btnSignUp, 3 + state);
    GridPane.setRowIndex(buttonSubmit, 3 + state);

    emailTxt.setVisible(state == 1);
    emailIcon.setVisible(state == 1);

    resetLabel.setVisible(state == 0);

    btnSignUp.setText((state == 1) ? "Cancel" : "Sign Up");
    buttonSubmit.setText((state == 1) ? "Create" : "Login");

    usernameTxt.setStyle("-fx-border-color: red ; -fx-border-width: 0px ;");
    passwordTxt.setStyle("-fx-border-color: red ; -fx-border-width: 0px ;");
    smallLabel.setVisible(false);

    state = (state == 1) ? 0 : 1;
  }

  public void swapPanes(String paneID) {
    usernameTxt.clear();
    passwordTxt.clear();
    emailTxt.clear();
    codeTxt.clear();
    resetTxt.clear();

    usernameTxt.setStyle("-fx-border-color: red ; -fx-border-width: 0px ;");
    passwordTxt.setStyle("-fx-border-color: red ; -fx-border-width: 0px ;");
    smallLabel.setVisible(false);

    currentPaneID = paneID;
    Node child = contentPane.getChildren().filtered(node -> node.isVisible()).get(0);
    child.setVisible(false);
    child = contentPane.getChildren().filtered(x -> x.getId().equals(paneID)).get(0);
    child.setVisible(true);

    //    for (Node c : contentPane.getChildren()) {
    //      c.setVisible(false);
    //    }
    //
    //    Node child = contentPane.getChildren().filtered(x -> x.getId().equals(paneID)).get(0);
    //    child.setVisible(true);
    //    if (paneID == "formPane") {
    //      welcome.setVisible(true);
    //    } else {
    //      welcome.setVisible(false);
    //    }
  }

  public void handleForgotAction(ActionEvent actionEvent) {
    swapPanes("resetPane");
  }

  public void handleVerifyAction(ActionEvent actionEvent) {
    boolean check = as.validate(codeTxt.getText());
    if (check) {
      swapPanes("formPane");
      as.signIn(username, pass);
    } else {
      codeTxt.clear();
      codeLabel.setText("Incorrect code, please double-check your code");
    }
  }

  public void handleResetAction(ActionEvent actionEvent) {
    boolean check = false;
    if (resetTxt.getText() != null) {
      check = as.resetPassword(resetTxt.getText());
    } else {
      return;
    }

    if (check) {
      resetLabel.setText("Enter username or email");
      resetLabel.setStyle("-fx-text-fill: black");
      resetTxt.clear();
      swapPanes("formPane");
    } else {
      resetLabel.setText("We cannot find a matching account");
      resetLabel.setStyle("-fx-text-fill: red");
    }
  }
}
