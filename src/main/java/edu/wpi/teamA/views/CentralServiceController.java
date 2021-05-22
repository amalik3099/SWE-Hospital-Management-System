package edu.wpi.teamA.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javax.swing.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CentralServiceController implements Initializable {

  @FXML private JFXButton btnSanitationRequest;
  @FXML private JFXButton btnMedicineRequest;
  @FXML private JFXButton btnLaundryRequest;
  @FXML private JFXButton btnReligiousRequest;
  @FXML private JFXButton btnComputerRequest;
  @FXML private JFXButton btnSecurityRequest;
  @FXML private JFXButton btnFloralRequest;
  @FXML private JFXButton btnGiftRequest;
  @FXML private JFXButton btnLangRequest;
  @FXML private JFXButton btnAVRequest;
  @FXML private JFXListView<JFXButton> serviceList;
  @FXML private AnchorPane homePane;
  @FXML private StackPane contentPane;
  @FXML private StackPane servicePane;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    servicePane.setVisible(false);
  }

  @FXML
  private void handleDepthAction(MouseEvent event) {
    serviceList.depthProperty().set(2);
  }

  @FXML
  public void handleMedicineRequest() {
    swapPanes("medicinePane");
  }

  @FXML
  public void handleSanitationRequest() {
    swapPanes("sanitationPane");
  }

  @FXML
  public void handleComputerRequest() {
    swapPanes("computerPane");
  }

  @FXML
  public void handleLaundryRequest() {
    swapPanes("laundryPane");
  }

  @FXML
  public void handleFloralRequest() {
    swapPanes("floralPane");
  }

  @FXML
  public void handleGiftRequest() {
    swapPanes("giftPane");
  }

  @FXML
  public void handleReligiousRequest() {
    swapPanes("religiousPane");
  }

  @FXML
  public void handleSecurityRequest() {
    swapPanes("securityPane");
  }

  @FXML
  public void handleAVRequest() {
    swapPanes("avPane");
  }

  @FXML
  public void handleLanguageRequest() {
    swapPanes("languagePane");
  }

  public void swapPanes(String paneID) {
    Node child = contentPane.getChildren().filtered(node -> node.isVisible()).get(0);
    child.setVisible(false);
    child = contentPane.getChildren().filtered(x -> x.getId().equals(paneID)).get(0);
    child.setVisible(true);
  }
}
