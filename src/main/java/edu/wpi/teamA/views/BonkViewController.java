package edu.wpi.teamA.views;

import com.google.inject.Inject;
import edu.wpi.teamA.services.DatabaseService;
import edu.wpi.teamA.state.HomeState;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BonkViewController implements Initializable {

  @Inject DatabaseService db;
  @Inject HomeState homeState;

  @FXML private Text unseeLabel;
  @FXML private Text agreeLabel;
  @FXML private CheckBox agreeBox;
  @FXML private Button bonkButton;
  @FXML private Button homeButton;
  @FXML private Button resetButton;
  @FXML private ImageView bonkImage;
  @FXML private AnchorPane anchorPane;

  private AudioClip bonkSound;

  @Inject FXMLLoader loader;

  @FXML
  private void allowBonkButton(ActionEvent event) {
    agreeBox.setSelected(true);
    agreeBox.setDisable(true);
    agreeLabel.setOpacity(0.5);
    unseeLabel.setVisible(false);
    bonkButton.setDisable(false);
    resetButton.setDisable(false);
  }

  @FXML
  private void handleBonkButton(ActionEvent event) {
    String location = getClass().getResource("/edu/wpi/teamA/assets/img/bonk.gif").toExternalForm();
    System.out.println(location);
    File file = new File(location);
    System.out.println(file.exists());
    log.info("Bonk button was pressed!");
    Image i =
        new Image(getClass().getResource("/edu/wpi/teamA/assets/img/bonk.gif").toExternalForm());
    bonkImage.setImage(i);
    bonkImage.setVisible(true);
    agreeLabel.setVisible(false);
    bonkSound.play(1.0);
    Timeline timeline =
        new Timeline(
            new KeyFrame(
                Duration.seconds(2.5),
                new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent event) {
                    AnchorPane pane = (AnchorPane) bonkButton.getParent();
                    pane.setStyle("-fx-background-color: #fbef91");
                  }
                }));
    timeline.setCycleCount(1);
    timeline.play();
  }

  @FXML
  private void handleResetButton(ActionEvent event) {
    bonkSound.stop();
    agreeBox.setSelected(false);
    agreeBox.setDisable(false);
    agreeLabel.setOpacity(1);
    agreeLabel.setVisible(true);
    unseeLabel.setVisible(true);
    bonkButton.setDisable(true);
    resetButton.setDisable(true);
    bonkImage.setVisible(false);
    AnchorPane pane = (AnchorPane) bonkButton.getParent();
    pane.setStyle("");
  }

  @FXML
  private void goHome(ActionEvent event) throws IOException {
    Stage stage = (Stage) homeButton.getScene().getWindow();
    // load other FXML document
    Parent root = loader.load(getClass().getResourceAsStream("HomeView.fxml"));
    // create a new scene with root and set the stage
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // auto-generated method stub
    bonkSound =
        new AudioClip(
            getClass().getResource("/edu/wpi/teamA/assets/sounds/bonk.mp3").toExternalForm());
  }
}
