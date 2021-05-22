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
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HomeViewController implements Initializable {

  @FXML private Button btnExt;
  @FXML private Button btnInt;
  @FXML private Button btnSzn;
  @FXML private Button btnEM;
  @FXML private Button btnBonk;
  @FXML private Button btnMNT;
  @FXML private Button btnPfd;
  @FXML private Button btnMET;
  @FXML private Button btnQt;

  //  @Inject Injector injector;
  @Inject FXMLLoader loader;

  @FXML
  private void handleSwitchAction(ActionEvent e) throws IOException {
    //    FXMLLoader loader = injector.getInstance(FXMLLoader.class);

    Stage stage;
    Parent root;
    if (e.getSource() == btnExt) {
      // stage = new Stage();
      // get reference to the button's stage
      stage = (Stage) btnExt.getScene().getWindow();
      // load other FXML document
      root = loader.load(getClass().getResourceAsStream("ExternalTransportREQ.fxml"));
    } else if (e.getSource() == btnSzn) {
      // stage = new Stage();
      // get reference to the button's stage
      stage = (Stage) btnSzn.getScene().getWindow();
      // load other FXML document
      root = loader.load(getClass().getResourceAsStream("madiSanitation.fxml"));
    } else if (e.getSource() == btnInt) {
      // stage = new Stage();
      // get reference to the button's stage
      stage = (Stage) btnInt.getScene().getWindow();
      // load other FXML document
      root = loader.load(getClass().getResourceAsStream("InternalTransportReq.fxml"));
    } else if (e.getSource() == btnBonk) {
      // stage = new Stage();
      // get reference to the button's stage
      stage = (Stage) btnBonk.getScene().getWindow();
      // load other FXML document
      root = loader.load(getClass().getResourceAsStream("BonkView.fxml"));
    } else if (e.getSource() == btnMNT) {
      // stage = new Stage();
      // get reference to the button's stage
      stage = (Stage) btnMNT.getScene().getWindow();
      // load other FXML document
      root = loader.load(getClass().getResourceAsStream("MapNodesTable.fxml"));
    } else if (e.getSource() == btnPfd) {
      // stage = new Stage();
      // get reference to the button's stage
      stage = (Stage) btnPfd.getScene().getWindow();
      // load other FXML document
      root = loader.load(getClass().getResourceAsStream("PathfindingView.fxml"));
    } else if (e.getSource() == btnMET) {
      // stage = new Stage();
      // get reference to the button's stage
      stage = (Stage) btnMET.getScene().getWindow();
      // load other FXML document
      root = loader.load(getClass().getResourceAsStream("MapEdgesTable.fxml"));
    } else {
      // get reference to the button's stage
      stage = (Stage) btnEM.getScene().getWindow();
      // load other FXML document
      root = loader.load(getClass().getResourceAsStream("InternalEmergencyRoute.fxml"));
    }
    // create a new scene with root and set the stage

    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  @FXML
  private void handleQuitAction() {
    // get a handle to the stage
    Stage stage;
    stage = (Stage) btnQt.getScene().getWindow();
    // do what you have to do
    stage.close();
  }

  @Override
  public void initialize(URL url, ResourceBundle rb) {}
}
