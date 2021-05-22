package edu.wpi.teamA;

import com.google.inject.Guice;
import com.google.inject.Injector;
import edu.wpi.teamA.modules.GuiceModule;
import edu.wpi.teamA.services.DatabaseService;
import edu.wpi.teamA.services.PathfindingService;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App extends Application {

  @Override
  public void init() {
    log.info("Starting Up");
  }

  @Override
  public void start(Stage primaryStage) throws IOException {
    Injector injector = Guice.createInjector(new GuiceModule());
    FXMLLoader fxmlLoader = injector.getInstance(FXMLLoader.class);

    PathfindingService pathfindingService = injector.getInstance(PathfindingService.class);
    pathfindingService.updateGraph();

    Parent root = fxmlLoader.load(getClass().getResourceAsStream("views/GodView.fxml"));

    Scene scene = new Scene(root);
    primaryStage.setScene(scene);
    primaryStage.setMinWidth(1050);
    primaryStage.setMinHeight(600);
    primaryStage.setMaxWidth(1920);
    primaryStage.setMaxHeight(1080);
    primaryStage
        .getScene()
        .getStylesheets()
        .add(
            this.getClass()
                .getResource("/edu/wpi/teamA/assets/style_sheets/desktop.css")
                .toExternalForm());
    primaryStage
        .getIcons()
        .add(
            new Image(
                this.getClass()
                    .getResource("/edu/wpi/teamA/assets/img/logo.icns")
                    .toExternalForm()));
    primaryStage.setTitle("Brigham and Women's Faulkner Hospital");
    primaryStage.show();
  }

  @Override
  public void stop() {
    DatabaseService.shutdown();
    log.info("Shutting Down");
  }
}
