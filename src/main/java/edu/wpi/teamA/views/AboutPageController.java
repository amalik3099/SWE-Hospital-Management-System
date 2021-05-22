package edu.wpi.teamA.views;

import com.google.inject.Inject;
import com.jfoenix.controls.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

public class AboutPageController implements Initializable {

  @FXML public AnchorPane aboutPagePane;
  @FXML private JFXButton btnBack;
  @FXML private ScrollPane aboutPageScrollPane;

  @Inject MenuController mainController;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    aboutPagePane.setVisible(false);
    aboutPageScrollPane.setFitToWidth(true);
  }

  @FXML
  private void handleCancelAction(ActionEvent e) throws IOException {
    // mainController.swapPanes("homePane");
  }
}
