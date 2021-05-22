package edu.wpi.teamA.views;

import com.jfoenix.controls.JFXButton;
import edu.wpi.teamA.services.AuthService;
import edu.wpi.teamA.state.HomeState;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.inject.Inject;
import javax.swing.*;
import lombok.SneakyThrows;

public class MenuController implements Initializable {

  @FXML AnchorPane mainPane;
  @FXML Button accountButtonMobile;
  @FXML Button savedPlacesButton;
  @FXML Button pathButton;
  @FXML Button requestButton;
  @FXML Button nodeButton;
  @FXML Button edgeButton;
  @FXML JFXButton accountButton;
  @FXML Button btnQt;
  @FXML Button tableButton;
  @FXML Button employeeButton;
  @FXML Button incomingRequestButton;
  @FXML Button mapEditorButton;
  @FXML Separator menuSeparator;
  @FXML Separator menuSeparator1;
  @FXML StackPane contentPane;
  @FXML FlowPane menuPane;
  @FXML FlowPane tableDropdown;
  @FXML Button btnRszSm;
  @FXML Button btnRszLg;
  @FXML Button abtPageBtn;
  @FXML Button covidInfoBtn;
  @FXML Button pathButtonMobile;
  @FXML HBox menuPaneMobile;
  @FXML Button covidButton;
  @FXML JFXButton accountInfoButton;

  @FXML ImageView zero;
  @FXML ImageView slide;
  @FXML ImageView slideB;
  @FXML ImageView slideC;
  @FXML ImageView slideD;

  @FXML AnchorPane slideThin;
  @FXML AnchorPane slideWide;
  @FXML Circle accountCircle;

  @FXML StackPane loginContent;
  @FXML AnchorPane loginAnchorPane;

  Boolean checkUser = false;

  @Inject AuthService as;
  @Inject HomeState homeState;

  private int imageIndex = 0;
  //  private int imageIndexWide = 0;
  private final List<String> imageListThin = new ArrayList<String>();
  //  private List<String> imageListWide = new ArrayList<String>();
  private ImageView imageView;

  private boolean isCollapsed;
  private String currentPaneID;
  private Insets collapsedInsets, expandedInsets;

  @SneakyThrows
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    isCollapsed = true;
    currentPaneID = "none";
    collapsedInsets = new Insets(0, 0, menuPane.getHeight() - 570, 0);
    expandedInsets = new Insets(0, 0, menuPane.getHeight() - 640, 0);
    menuSeparator.setPadding(collapsedInsets);
    addButtonNames();
    resizeMenu(150);

    //    System.out.println("Circle's center : " + accountCircle.getCenterX());
    //    accountCircle.setCenterX(-5.0);
    //    System.out.println("Circle's new center : " + accountCircle.getCenterX());

    EventHandler<MouseEvent> exited =
        new EventHandler<MouseEvent>() {
          @Override
          public void handle(MouseEvent event) {
            resizeMenu(50);
            removeButtonNames();
            if (!isCollapsed) tableToggle(null);
            event.consume();
          }
        };

    menuPane
        .heightProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              collapsedInsets = new Insets(0, 0, menuPane.getHeight() - 570, 0);
              expandedInsets = new Insets(0, 0, menuPane.getHeight() - 640, 0);
              collapsedInsets = new Insets(0, 0, menuPane.getHeight() - 570, 0); // 320
              expandedInsets = new Insets(0, 0, menuPane.getHeight() - 640, 0); // 390
              menuSeparator.setPadding(isCollapsed ? collapsedInsets : expandedInsets);
            });

    menuPane.setOnMouseEntered(
        event -> {
          resizeMenu(150);
          addButtonNames();
          menuPane.setOnMouseExited(exited);
          event.consume();
        });

    tableButton.setOnMouseClicked(this::tableToggle);

    accountInfoButton.setVisible(false);

    btnRszLg.setVisible(false);
    pathButton.requestFocus();

    slide.fitWidthProperty().bind(slideThin.widthProperty());
    slide.fitHeightProperty().bind(slideThin.heightProperty());
    //    wideSlide.fitWidthProperty().bind(slideWide.widthProperty());
    //    wideSlide.fitHeightProperty().bind(slideWide.heightProperty());

    imageListThin.add(
        getClass()
            .getResource("/edu/wpi/teamA/assets/img/slideShowThin/frontView.png")
            .toExternalForm());
    imageListThin.add(
        getClass()
            .getResource("/edu/wpi/teamA/assets/img/slideShowThin/sideView.png")
            .toExternalForm());
    imageListThin.add(
        getClass()
            .getResource("/edu/wpi/teamA/assets/img/slideShowThin/front.png")
            .toExternalForm());
    imageListThin.add(
        getClass().getResource("/edu/wpi/teamA/assets/img/slideShowThin/bed.png").toExternalForm());

    Image[] images = new Image[imageListThin.size()];

    for (int i = 0; i < imageListThin.size(); i++) {
      images[i] = new Image(imageListThin.get(i));
    }

    slide.setImage(images[0]);
    //    slideB.setImage(images[1]);
    //    slideC.setImage(images[2]);
    //    slideB.setVisible(false);
    //    slideC.setVisible(false);

    ImageView[] imageView = new ImageView[4];
    imageView[0] = slide;
    imageView[1] = slideB;
    imageView[2] = slideC;
    imageView[3] = slideD;

    ActionListener listener =
        new ActionListener() {
          @Override
          public void actionPerformed(java.awt.event.ActionEvent e) {
            //            slide.setImage(images[imageIndex]);

            imageIndex++;
            if (imageIndex > (imageListThin.size() - 1)) {
              imageIndex = 0;
              slideB.setVisible(false);
              slideC.setVisible(false);
              slideD.setVisible(false);
              zero.setImage(images[3]);
            }
            slideFadeIn(imageView[imageIndex]);
          }
        };

    slide.setImage(images[imageIndex]);
    //    wideSlide.setPreserveRatio(false);
    //
    //    imageView = new ImageView(images[imageIndex]);
    //
    //    imageView.setFitHeight(100);
    //    imageView.setFitWidth(300);
    Timer timer;
    timer = new Timer(6000, listener);
    // Timer timer = new Timer(2000, listener);
    timer.start();

    homeState
        .currentPaneProperty()
        .addListener(
            (ob, o, n) -> {
              if (n.equals("pathfindingPane")) pathButton.getStyleClass().add("active");
              swapPanes(n);
            });
  }

  private void slideFadeIn(ImageView slide) {
    FadeTransition fadeIn = new FadeTransition(Duration.seconds(3), slide);

    fadeIn.setFromValue(0);
    fadeIn.setToValue(1);
    fadeIn.setCycleCount(1);

    fadeIn.play();

    Image[] images = new Image[imageListThin.size()];

    for (int i = 0; i < imageListThin.size(); i++) {
      images[i] = new Image(imageListThin.get(i));
    }

    slide.setVisible(true);
    slide.setImage(images[imageIndex]);
  }

  private void tableToggle(MouseEvent event) {
    if (!currentPaneID.contains("Table")) {
      if (isCollapsed) {
        tableDropdown.setPrefHeight(120);
        nodeButton.setPrefHeight(35);
        edgeButton.setPrefHeight(35);
        nodeButton.setVisible(true);
        edgeButton.setVisible(true);
        tableButton.setText("\u2715    ");
        employeeButton.setVisible(true);
        menuSeparator.setPadding(expandedInsets);
        tableDropdown.setStyle("-fx-background-color: #045393");
        isCollapsed = false;
      } else {
        tableDropdown.setPrefHeight(50);
        nodeButton.setPrefHeight(0);
        edgeButton.setPrefHeight(0);
        nodeButton.setVisible(false);
        edgeButton.setVisible(false);
        tableButton.setText("\u2630    ");
        menuSeparator.setPadding(collapsedInsets);
        tableDropdown.setStyle("-fx-background-color: #0067b1");
        isCollapsed = true;
      }
    }
  }

  private void addButtonNames() {
    pathButton.setText("Navigation");
    requestButton.setText("Service Request");
    employeeButton.setText("Employees");
    incomingRequestButton.setText("Active Requests");
    tableButton.setText(isCollapsed ? "\u2630    " : "\u2715    ");
    nodeButton.setText("Add Node");
    edgeButton.setText("Add Edge");
    mapEditorButton.setText("Map Editor");
    if (checkUser) {
      accountInfoButton.setText(as.getUser().getFieldAsString("id"));
    } else {
      accountInfoButton.setText("Log in");
    }
    btnQt.setText("Quit Application");
    btnRszSm.setText("Mobile Mode");
    abtPageBtn.setText("About Page");
    covidInfoBtn.setText("Covid Info");
  }

  private void removeButtonNames() {
    pathButton.setText("");
    requestButton.setText("");
    employeeButton.setText("");
    tableButton.setText("");
    nodeButton.setText("");
    edgeButton.setText("");
    accountInfoButton.setText("");
    btnQt.setText("");
    mapEditorButton.setText("");
    incomingRequestButton.setText("");
    btnRszSm.setText("");
    abtPageBtn.setText("");
    covidInfoBtn.setText("");
  }

  public void resizeMenu(int newSize) {
    menuPane.setPrefWidth(newSize);
    pathButton.setPrefWidth(newSize);
    requestButton.setPrefWidth(newSize);
    employeeButton.setPrefWidth(newSize);
    nodeButton.setPrefWidth(newSize);
    edgeButton.setPrefWidth(newSize);
    accountInfoButton.setPrefWidth(newSize);
    mapEditorButton.setPrefWidth(newSize);
    tableButton.setPrefWidth(newSize);
    tableDropdown.setPrefWidth(newSize);
    incomingRequestButton.setPrefWidth(newSize);
    menuSeparator.setPrefWidth(newSize - 20);
    menuSeparator1.setPrefWidth(newSize - 20);
    btnQt.setPrefWidth(newSize);
    btnRszSm.setPrefWidth(newSize);
    abtPageBtn.setPrefWidth(newSize);
    covidInfoBtn.setPrefWidth(newSize);
  }

  @FXML
  public void handlePathButton() {
    swapPanes("pathfindingPane");
    pathButton.getStyleClass().add("active");
  }

  @FXML
  public void handleRequestButton() {
    swapPanes("updatedServicePane");
    requestButton.getStyleClass().add("active");
  }

  @FXML
  public void handleEmployeeButton() {
    swapPanes("employeePane");
    employeeButton.getStyleClass().add("active");
  }

  @FXML
  public void handleIncomingRequestsButton() {
    swapPanes("completionPane");
    incomingRequestButton.getStyleClass().add("active");
  }

  @FXML
  private void handleLoginAction() {
    if (as.getAuthLevel() > 0) {
      swapPanes("accountPane");

    } else {
      swapPanes("loginPane");
    }
    accountInfoButton.getStyleClass().add("active");
    //    slideWide.setVisible(true);
  }

  @FXML
  public void handleNodeButton() {
    swapPanes("mapNodesTablePane");
    nodeButton.getStyleClass().add("active");
  }

  @FXML
  public void handleEdgeButton() {
    swapPanes("mapEdgesTablePane");
    edgeButton.getStyleClass().add("active");
  }

  @FXML
  public void handleMapEditor() {
    swapPanes("pathfindingEditorPane");
    mapEditorButton.getStyleClass().add("active");
  }

  @FXML
  public void handleAbtPageBtn(ActionEvent actionEvent) {
    swapPanes("aboutPagePane");
    abtPageBtn.getStyleClass().add("active");
  }

  @FXML
  public void handleCovidInfoBtn(ActionEvent actionEvent) {
    swapPanes("covidDeathPane");
    covidInfoBtn.getStyleClass().add("active");
  }

  public void swapPanes(String paneID) {
    slideWide.setVisible(false);
    slideThin.setVisible(false);
    currentPaneID = paneID;
    Node child = contentPane.getChildren().filtered(node -> node.isVisible()).get(0);
    child.setVisible(false);
    child = contentPane.getChildren().filtered(x -> x.getId().equals(paneID)).get(0);
    child.setVisible(true);
    menuPane
        .getChildren()
        .forEach(
            node -> {
              if (node instanceof Button) {
                node.setMouseTransparent(false);
                node.getStyleClass().remove("active");
              } else if (node instanceof FlowPane) {
                ((FlowPane) node)
                    .getChildren()
                    .forEach(
                        innerNode -> {
                          if (!innerNode.getId().equals("tableButton")) {
                            innerNode.setMouseTransparent(false);
                            innerNode.setStyle("-fx-background-color: inherit");
                          }
                        });
              }
            });
  }

  public void swapAccountPanes(String paneID) {
    slideWide.setVisible(false);
    slideThin.setVisible(false);
    currentPaneID = paneID;
    Node child = loginContent.getChildren().filtered(node -> node.isVisible()).get(0);
    child.setVisible(false);
    child = loginContent.getChildren().filtered(x -> x.getId().equals(paneID)).get(0);
    child.setVisible(true);
  }

  @FXML
  private void handleQuitAction() {
    Stage stage;
    stage = (Stage) btnQt.getScene().getWindow();
    stage.close();
  }

  @FXML
  public void handleResizeAction(javafx.event.ActionEvent e) {
    Stage primaryStage;
    primaryStage = (Stage) menuPane.getScene().getWindow();
    if (e.getSource() == btnRszSm) {
      homeState.setIsMobile(true);

      primaryStage.setMinWidth(450);
      primaryStage.setMinHeight(800);
      primaryStage.setMaxWidth(450);
      primaryStage.setMaxHeight(800);

      AnchorPane.setLeftAnchor(contentPane, 0.0);
      AnchorPane.setRightAnchor(contentPane, 0.0);
      AnchorPane.setTopAnchor(contentPane, 0.0);
      AnchorPane.setBottomAnchor(contentPane, 0.0);

      menuPane.setVisible(false);
      btnRszLg.setVisible(true);
      primaryStage
          .getScene()
          .getStylesheets()
          .remove(
              this.getClass()
                  .getResource("/edu/wpi/teamA/assets/style_sheets/desktop.css")
                  .toExternalForm());
      primaryStage
          .getScene()
          .getStylesheets()
          .add(
              0,
              this.getClass()
                  .getResource("/edu/wpi/teamA/assets/style_sheets/mobile.css")
                  .toExternalForm());
      swapPanes("pathfindingPane");
      pathButton.getStyleClass().add("active");

    } else if (e.getSource() == btnRszLg) {
      homeState.setIsMobile(false);

      primaryStage.setMinWidth(1050);
      primaryStage.setMinHeight(600);
      primaryStage.setMaxWidth(1920);
      primaryStage.setMaxHeight(1080);

      AnchorPane.setLeftAnchor(contentPane, 50.0);
      AnchorPane.setRightAnchor(contentPane, 0.0);
      AnchorPane.setTopAnchor(contentPane, 0.0);
      AnchorPane.setBottomAnchor(contentPane, 0.0);

      menuPane.setVisible(true);
      btnRszLg.setVisible(false);
      primaryStage
          .getScene()
          .getStylesheets()
          .remove(
              this.getClass()
                  .getResource("/edu/wpi/teamA/assets/style_sheets/mobile.css")
                  .toExternalForm());
      primaryStage
          .getScene()
          .getStylesheets()
          .add(
              0,
              this.getClass()
                  .getResource("/edu/wpi/teamA/assets/style_sheets/desktop.css")
                  .toExternalForm());
      swapPanes("pathfindingPane");
      pathButton.getStyleClass().add("active");
      pathButton.requestFocus();
    }
  }

  void removeLoginPage() {
    loginContent.setVisible(false);
    //    loginContent.disableProperty().set(true);
    loginAnchorPane.setMouseTransparent(true);
    contentPane.setEffect(null);
    contentPane.setMouseTransparent(false);
    menuPane.setEffect(null);
    menuPane.setMouseTransparent(false);
  }

  public void handleCovidButton(ActionEvent event) {
    swapPanes("CovidPane");
  }

  public void handleSavedPlaces(ActionEvent event) {}

  public void handleAccountButton(ActionEvent event) {
    if (as.getAuthLevel() == 0) slideThin.setVisible(false);
    loginContent.setVisible(true);
    contentPane.setEffect(new GaussianBlur(10));
    contentPane.setMouseTransparent(true);
    menuPane.setEffect(new GaussianBlur(10));
    menuPane.setMouseTransparent(true);
    //      loginContent.disableProperty().set(false);
    loginAnchorPane.setMouseTransparent(false);
  }

  //  public void handleGuestAction() {
  //    accountDrawer.close();
  //    accountInfoButton.setVisible(true);
  //  }

  //  public void wideSlideDisplay() {
  //    imageListWide.add(
  //            getClass()
  //                    .getResource("/edu/wpi/teamA/assets/img/slideShowWide/frontDesk.png")
  //                    .toExternalForm());
  //    imageListWide.add(
  //            getClass()
  //                    .getResource("/edu/wpi/teamA/assets/img/slideShowWide/room.png")
  //                    .toExternalForm());
  //    imageListWide.add(
  //            getClass()
  //                    .getResource("/edu/wpi/teamA/assets/img/slideShowWide/chair.png")
  //                    .toExternalForm());
  //
  //    Image images[] = new Image[imageListWide.size()];
  //
  //    for (int i = 0; i < imageListWide.size(); i++) {
  //      images[i] = new Image(imageListWide.get(i));
  //    }
  //
  //    ActionListener listener =
  //            new ActionListener() {
  //              @Override
  //              public void actionPerformed(java.awt.event.ActionEvent e) {
  //                imageIndexWide++;
  //                if (imageIndexWide > (imageListWide.size() - 1)) imageIndexWide = 0;
  //                wideSlide.setImage(images[imageIndexWide]);
  //                wideSlide.setPreserveRatio(false);
  //              }
  //            };
  //
  //    wideSlide.setImage(images[imageIndexWide]);
  //    //
  //    //    imageView = new ImageView(images[imageIndex]);
  //    //
  //    //    imageView.setFitHeight(100);
  //    //    imageView.setFitWidth(300);
  //    javax.swing.Timer timer = new Timer(5000, listener);
  //    // Timer timer = new Timer(2000, listener);
  //    timer.start();
  //
  //    //    Image images[] = new Image[imageListWide.size()];
  //    //
  //    //    for (int i = 0; i < imageListWide.size(); i++) {
  //    //      images[i] = new Image(imageListWide.get(i));
  //    //    }
  //    //
  //    //    ActionListener listener =
  //    //        new ActionListener() {
  //    //          @Override
  //    //          public void actionPerformed(java.awt.event.ActionEvent e) {
  //    //            imageIndex++;
  //    //            if (imageIndex > (imageListWide.size() - 1)) imageIndex = 0;
  //    //            wideSlide.setImage(images[imageIndex]);
  //    //          }
  //    //        };
  //    //
  //    //    wideSlide.setImage(images[imageIndex]);
  //    //    //
  //    //    //    imageView = new ImageView(images[imageIndex]);
  //    //    //
  //    //    //    imageView.setFitHeight(100);
  //    //    //    imageView.setFitWidth(300);
  //    //    javax.swing.Timer timer = new Timer(5000, listener);
  //    //    // Timer timer = new Timer(2000, listener);
  //    //    timer.start();
  //  }

  public void setCheckUser(Boolean check) {
    this.checkUser = check;
  }
}
