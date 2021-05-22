package edu.wpi.teamA.views;

import com.google.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javax.swing.*;

public class UploadController implements Initializable {

  @Inject FXMLLoader loader;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    uploadTxt.setVisible(false);
  }

  @FXML private Button selectFile;
  @FXML private Button canUpload;
  @FXML private Button confirmUpload;
  @FXML private Button returnButton;
  @FXML private Text displayTxt;
  @FXML private Text uploadTxt;
  @FXML private Text fileTxt;
  private String returnRoot;

  @FXML
  private void handleConfirmAction(ActionEvent e) throws IOException {
    // get reference to the button's stage
    Parent root;
    Stage stage = (Stage) confirmUpload.getScene().getWindow();
    handleConfirm();
  }

  @FXML
  private void handleReturnAction(ActionEvent e) throws IOException {
    // get reference to the button's stage
    Parent root;
    Stage stage = (Stage) returnButton.getScene().getWindow();
    // load other FXML document
    root = loader.load(getClass().getResourceAsStream(returnRoot));
    Scene aScene = new Scene(root);
    stage.setScene(aScene);
    stage.show();
  }

  public void setReturnRoot(String aRoot) {
    this.returnRoot = aRoot;
  }

  private void handleConfirm() {
    returnButton.setLayoutX(200);
    returnButton.setLayoutY(150);
    canUpload.setVisible(false);
    confirmUpload.setVisible(false);
    displayTxt.setVisible(false);
    uploadTxt.setVisible(true);
    fileTxt.setVisible(false);
    selectFile.setVisible(false);
  }
}
