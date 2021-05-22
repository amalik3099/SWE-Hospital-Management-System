package edu.wpi.teamA.modules;

import com.google.inject.AbstractModule;
import edu.wpi.teamA.services.AuthService;
import edu.wpi.teamA.services.DatabaseService;
import edu.wpi.teamA.services.EmailService;
import edu.wpi.teamA.services.PathfindingService;
import edu.wpi.teamA.state.HomeState;
import edu.wpi.teamA.views.CentralServiceController;
import edu.wpi.teamA.views.MenuController;
import edu.wpi.teamA.views.PathfindingController;
import java.awt.*;
import javafx.fxml.FXMLLoader;

public class GuiceModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(FXMLLoader.class).toProvider(FXMLLoaderProvider.class);
    bind(DatabaseService.class).asEagerSingleton();
    bind(PathfindingService.class).asEagerSingleton();
    bind(HomeState.class).asEagerSingleton();
    bind(CentralServiceController.class).asEagerSingleton();
    bind(MenuController.class).asEagerSingleton();
    bind(AuthService.class).asEagerSingleton();
    bind(EmailService.class).asEagerSingleton();
    bind(PathfindingController.class).asEagerSingleton();
  }
}
