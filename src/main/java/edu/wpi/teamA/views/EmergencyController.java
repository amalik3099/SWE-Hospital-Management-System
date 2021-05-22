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
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EmergencyController implements Initializable {

  @FXML private Button btnHome;
  @FXML private Button btnClear;
  @FXML private Button btnCancel;
  @FXML private TextField start;
  @FXML private TextField end;
  @FXML private TextArea description;
  @FXML private CheckBox confirmCheck;

  @Inject FXMLLoader loader;

  @FXML
  private void handleHomeAction(ActionEvent e) throws IOException {
    // get reference to the button's stage
    Parent root;
    Stage stage = (Stage) btnHome.getScene().getWindow();
    // load other FXML document
    root = loader.load(getClass().getResourceAsStream("HomeView.fxml"));
    Scene aScene = new Scene(root);
    stage.setScene(aScene);
    stage.show();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // auto-generated method stub
  }

  @FXML
  private void handleHome(ActionEvent e) throws IOException {
    Stage stage;
    Parent root;

    // get reference to the button's stage
    if (e.getSource() == btnHome) {
      stage = (Stage) btnHome.getScene().getWindow();
      root = loader.load(getClass().getResourceAsStream("HomeView.fxml"));
      Scene scene = new Scene(root);
      stage.setScene(scene);
      stage.show();
    }
  }

  @FXML
  private void handleClearAction(ActionEvent event) {
    Parent root;
    Stage stage = (Stage) btnClear.getScene().getWindow();
    start.setText("");
    end.setText("");
    description.setText("");
    confirmCheck.setSelected(false);
  }

  @FXML
  private void handleCancelAction(ActionEvent e) throws IOException {
    // get reference to the button's stage
    Parent root;
    Stage stage = (Stage) btnHome.getScene().getWindow();
    // load other FXML document
    root = loader.load(getClass().getResourceAsStream("HomeView.fxml"));
    Scene aScene = new Scene(root);
    stage.setScene(aScene);
    stage.show();
  }
}
