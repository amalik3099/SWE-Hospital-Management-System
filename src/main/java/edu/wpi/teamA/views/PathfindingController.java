package edu.wpi.teamA.views;

import static javafx.util.Duration.millis;

import com.google.inject.Inject;
import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import edu.wpi.teamA.modules.AutofillComboBox;
import edu.wpi.teamA.modules.Fading;
import edu.wpi.teamA.modules.HospitalNode;
import edu.wpi.teamA.services.DatabaseService;
import edu.wpi.teamA.services.PathfindingService;
import edu.wpi.teamA.state.HomeState;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.print.*;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.scene.transform.Scale;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.util.Duration;
import org.json.JSONException;
import org.json.JSONObject;

public class PathfindingController implements Initializable {
  private int parkingLot = 0;

  @Inject PathfindingService pathfindingService;
  @Inject HomeState homeState;

  @FXML private Label takeSurveyLabel;

  @FXML private JFXButton btnParking;
  @FXML private Button btnPrint;

  @FXML private JFXButton btnShowText;
  @FXML private HBox buttonRow;
  @FXML private FlowPane buttonRow2;
  @FXML private JFXButton btnNext;
  @FXML private JFXButton btnBack;
  @FXML private JFXButton btnToInterior;
  @FXML private JFXButton btnToWebView;
  @FXML public VBox directions;

  @FXML private TextFlow infoTF;
  @FXML private TextFlow errorTF;

  @FXML private ScrollPane scrollPane;
  @FXML private StackPane stackPane;
  @FXML private Pane overlayPaneTwo;
  @FXML private AnchorPane mapAnchor;

  @FXML private ImageView mapImg;
  @FXML private StackPane floorOneStack;
  @FXML private StackPane floorTwoStack;
  @FXML private StackPane floorThreeStack;
  @FXML private StackPane floorFourStack;
  @FXML private StackPane floorFiveStack;
  @FXML private ImageView floorOneMain;
  @FXML private Group group;
  @FXML private GridPane surveyGridPane;
  @FXML private JFXCheckBox restCheck;
  @FXML private JFXCheckBox elevCheck;
  @FXML private JFXCheckBox stairCheck;

  @FXML private JFXButton btnExpand;
  @FXML private MaterialDesignIconView chevronIcon;
  @FXML private JFXButton groundButton;
  @FXML private JFXButton f1Button;
  @FXML private JFXButton f2Button;
  @FXML private JFXButton f3Button;
  @FXML private JFXButton f4Button;
  @FXML private JFXButton f5Button;
  @FXML private JFXNodesList floorSelector;
  @FXML private WebView webView;
  @FXML private HBox wheelchairButton;
  @FXML private JFXComboBox<String> parkingBox;
  @FXML private JFXComboBox<String> destinationBox;
  @FXML private JFXComboBox<String> historyBox;
  @FXML private JFXButton btnParkingGo;
  @FXML private JFXButton btnHistoryGo;
  @FXML AnchorPane parkingPopup;
  @FXML AnchorPane historyPane;
  @FXML JFXNodesList filterList;
  private final ObservableList<String> parkingSpots = FXCollections.observableArrayList();
  private final ObservableList<String> historyLocations = FXCollections.observableArrayList();
  @FXML private JFXButton searchPathToggle;
  @FXML private AnchorPane contentPane;
  @FXML private VBox showTextMobile;
  @FXML private JFXTextField originInput;
  @FXML private JFXTextField destinationInput;
  @FXML private VBox gmapsVBox;
  @FXML private VBox resultAccordion;
  @FXML private MaterialDesignIconView originClear;
  @FXML private MaterialDesignIconView destClear;

  private boolean lastBigWidth = true;

  @FXML private ScrollPane directoryScrollPane;
  @FXML private AnchorPane searchPane;
  @FXML private VBox directory;
  @FXML private TitledPane entranceDir;
  @FXML private TitledPane parkingDir;
  @FXML private TitledPane restroomsDir;
  @FXML private TitledPane servicesDir;
  @FXML private TitledPane conferenceDir;
  @FXML private TitledPane labsDir;
  @FXML private TitledPane departmentsDir;
  @FXML private ImageView gmapsImage;

  private JFXButton selectedDirectory;

  private final ObservableList<ObservableList> instructionsSuper =
      FXCollections.observableArrayList();
  private final ObservableList<String> instructions = FXCollections.observableArrayList();

  // private final ObservableList<Label> steps = FXCollections.observableArrayList();
  @FXML private ListView<Label> directionList;
  private ArrayList<ObservableList<String>> value;
  private WebEngine webEngine;

  private final double SCALE_DELTA = 1.05;
  private double SCALE_TOTAL = 1.0;
  private double pressedX, pressedY;
  private final DoubleProperty zoomProperty = new SimpleDoubleProperty(200);
  private final DoubleProperty deltaY = new SimpleDoubleProperty(0.0d);
  private final String[] algorithms = {"AStar", "BFS", "DFS", "DJSTR"};
  private ArrayList<HospitalNode> path;

  private ArrayList<StringBuilder> textPaths;
  private ArrayList<Path> lines;
  private ArrayList<Path> linesSolid;
  private ArrayList<Integer> indices;
  private ArrayList<String> history;
  private ArrayList<FontAwesomeIconView> markers;

  private FilteredList<JFXButton> filteredBtns;

  HashMap<String, Node> panes;
  HashMap<String, JFXButton> buttons;

  private String active = "1";
  private int current_path;
  private int lastRClickX, lastRClickY;
  private boolean selectingFromNode, selectingEndNode;

  private String globalOrigin = "Parking Garage 1";
  private String globalPlaceID = null;

  private int wheelchair;
  private double xScale, yScale;
  private double minRatio = 1.1;
  private final double maxRatio = 4.0;
  private int floorValue;

  double ogHeight = 960;
  double ogWidth = 1078.0;
  private ArrayList<Node> originalChildrenSearchPane;
  EventHandler<MouseEvent> originClickedEvent;
  EventHandler<MouseEvent> destClickedEvent;

  @Inject DatabaseService db;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    homeState
        .getIsMobile()
        .addListener(
            (ob, o, n) -> {
              if (n) {
                surveyGridPane.setVisible(!homeState.getCanPathFind().get());
                showTextMobile.setVisible(homeState.getCanPathFind().get());
              } else {
                surveyGridPane.setVisible(false);
                showTextMobile.setVisible(false);
              }
            });

    SimpleListProperty gmapsLP = new SimpleListProperty(gmapsVBox.getChildren());

    infoTF.getChildren().add(1, new Text(System.lineSeparator()));
    infoTF.getChildren().add(1, new Text(System.lineSeparator()));
    errorTF.getChildren().add(1, new Text(System.lineSeparator()));
    errorTF.getChildren().add(1, new Text(System.lineSeparator()));
    errorTF.getChildren().add(4, new Text(System.lineSeparator()));
    errorTF.getChildren().add(4, new Text(System.lineSeparator()));

    infoTF
        .visibleProperty()
        .bind(
            gmapsImage
                .visibleProperty()
                .not()
                .and(homeState.getIsMobile().not().or(homeState.getCanPathFind())));
    errorTF
        .visibleProperty()
        .bind(
            infoTF
                .visibleProperty()
                .not()
                .and(homeState.getIsMobile())
                .and(gmapsImage.visibleProperty().not())
                .and(homeState.getCanPathFind().not()));

    gmapsImage.visibleProperty().bind(gmapsLP.sizeProperty().isNotEqualTo(1));

    homeState
        .surveyTakenProperty()
        .addListener(
            (ob, o, n) -> {
              takeSurveyLabel.setGraphic(null);
              takeSurveyLabel.setText("Awaiting Covid Survey Submission & Approval");
            });

    value = new ArrayList<>();
    selectingFromNode = true;

    webEngine = webView.getEngine();
    webEngine.load(this.getClass().getResource("gmaps.html").toExternalForm());
    webEngine.setOnAlert(
        new EventHandler<WebEvent<String>>() {
          @Override
          public void handle(WebEvent<String> e) {
            if (e.getData().equals("dest-garage")) {
              globalOrigin = "Parking Garage 1";
              if (originInput.getId().equals("GLOBAL")) originInput.setUserData(globalOrigin);
              checkFilledAndRoute();
            } else if (e.getData().equals("dest-valet")) {
              globalOrigin = "Valet Parking";
              if (originInput.getId().equals("GLOBAL")) originInput.setUserData(globalOrigin);
              checkFilledAndRoute();
            } else {
              System.out.println(e);
              String msg =
                  e.getData().equals("Directions request failed due to ZERO_RESULTS")
                      ? "Invalid Origin! No path to destination!"
                      : e.getData();
              JFXDialogLayout layout = new JFXDialogLayout();
              layout.setBody((new Text(msg)));
              JFXButton ok = new JFXButton("OK");
              layout.setActions(ok);
              JFXAlert<WebView> alert = new JFXAlert<>(webView.getScene().getWindow());
              alert.setOverlayClose(true);
              alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
              alert.setContent(layout);
              alert.initModality(Modality.WINDOW_MODAL);
              ok.setOnAction(v -> alert.close());
              alert.show();
            }
          }
        });

    //    originInput.idProperty().addListener((observable, oldValue, newValue) -> {
    //      if (newValue.equals("GLOBAL") && destinationInput.getUserData() != null)
    //    });

    homeState.refreshNodeList();

    wheelchair = 1;

    mapImg.setPreserveRatio(true);
    scrollPane.setPannable(true);
    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    searchPathToggle.setVisible(false);
    directionList.setVisible(false);

    scrollPane.setOnMousePressed(
        new EventHandler<MouseEvent>() {
          public void handle(MouseEvent event) {
            pressedX = event.getX();
            pressedY = event.getY();
            parkingPopup.setVisible(false);
          }
        });

    stackPane.setOnScroll(
        (ScrollEvent event) -> {
          event.consume();
          double scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA : 1 / SCALE_DELTA;
          rescale(scaleFactor);
        });

    //    AutofillComboBox fromAutoCombo = new AutofillComboBox(fromText);
    //    AutofillComboBox toAutoCombo = new AutofillComboBox(toText);

    //    originInput
    //        .focusedProperty()
    //        .addListener(
    //            ((observable, oldValue, newValue) -> {
    //              if (newValue) Platform.runLater(originInput::selectAll);
    //
    //              System.out.println("Focused changed");
    //            }));

    //    destinationInput
    //        .focusedProperty()
    //        .addListener(
    //            ((observable, oldValue, newValue) -> {
    //              if (newValue) Platform.runLater(destinationInput::selectAll);
    //
    //              System.out.println("Focused changed");
    //            }));

    //    floorOneStack.setCache(true);
    //    floorOneStack.setCacheHint(CacheHint.QUALITY);
    //    floorTwoStack.setCache(true);
    //    floorTwoStack.setCacheHint(CacheHint.SPEED);
    //    floorThreeStack.setCache(true);
    //    floorThreeStack.setCacheHint(CacheHint.SPEED);
    //    floorFourStack.setCache(true);
    //    floorFourStack.setCacheHint(CacheHint.SPEED);
    //    floorFiveStack.setCache(true);
    //    floorFiveStack.setCacheHint(CacheHint.SPEED);

    filterList.setRotate(180);

    panes = new HashMap<>();
    panes.put("G", mapImg);
    panes.put("1", floorOneStack);
    panes.put("2", floorTwoStack);
    panes.put("3", floorThreeStack);
    panes.put("4", floorFourStack);
    panes.put("5", floorFiveStack);

    buttons = new HashMap<>();
    buttons.put("G", groundButton);
    buttons.put("1", f1Button);
    buttons.put("2", f2Button);
    buttons.put("3", f3Button);
    buttons.put("4", f4Button);
    buttons.put("5", f5Button);

    path = new ArrayList<>();

    mapImg.setOpacity(0.7);
    floorOneStack.setOpacity(1.0);
    floorSelector.setRotate(-90);
    f1Button.setStyle(
        "-fx-background-color: #f6bd38; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #ffffff;");

    textPaths = new ArrayList<>();
    lines = new ArrayList<>();
    indices = new ArrayList<>();

    //    toText.getStyleClass().add(0, "mapFlowpaneComboBox");
    //    fromText.getStyleClass().add(0, "mapFlowpaneComboBox");

    //    AnchorPane.setTopAnchor(directions, 90.0);

    xScale = mapImg.getBoundsInLocal().getWidth() / 2989;
    yScale = mapImg.getBoundsInLocal().getHeight() / 2457;

    stackPane.setOnMouseReleased(
        e -> {
          if (e.getButton().equals(MouseButton.PRIMARY) && e.isDragDetect()) {
            lastRClickX = (int) e.getX();
            lastRClickY = (int) e.getY();

            // They clicked on the map somewhere -> if we were selecting a node, then we can find
            // the closest and autofill the combo box
            HospitalNode closest =
                getClosestNode((int) (lastRClickX / xScale), (int) (lastRClickY / yScale));

            if (selectingFromNode) {
              originInput.setId("LOCAL");
              originInput.setText(closest.toString());
              originInput.setUserData(closest.toString());
              addPinAtNode(closest, "startNode");
              checkFilledAndRoute();
            } else {
              destinationInput.setText(closest.toString());
              destinationInput.setUserData(closest.toString());
              addPinAtNode(closest, "endNode");
              checkFilledAndRoute();
            }
            System.out.println(closest.toString());
            e.consume();
          }
        });

    originClickedEvent =
        e -> {
          selectingFromNode = true;
          selectingEndNode = false;
          originInput.setPromptText("Click Map or Start Typing");
          destinationInput.setPromptText("Enter desired location");
          gmapsVBox.getChildren().removeIf(n -> n instanceof Accordion);
          gmapsVBox.setVisible(false);
          directory.getChildren().forEach(n -> ((TitledPane) n).setExpanded(false));
          if (homeState.getCanPathFind().get() || !homeState.isMobile()) resetDirectory();
        };

    originInput.setOnMouseClicked(originClickedEvent);

    destClickedEvent =
        event -> {
          selectingFromNode = false;
          selectingEndNode = true;
          destinationInput.setPromptText("Click Map or Start Typing");
          originInput.setPromptText("Enter your current location");
          gmapsVBox.getChildren().removeIf(n -> n instanceof Accordion);
          gmapsVBox.setVisible(false);
          directory.getChildren().forEach(n -> ((TitledPane) n).setExpanded(false));
          if (homeState.getCanPathFind().get() || !homeState.isMobile()) resetDirectory();
        };

    destinationInput.setOnMouseClicked(destClickedEvent);

    parkingSpots.clear();
    historyLocations.clear();

    loadDirectory();

    parkingBox.setItems(parkingSpots);
    destinationBox.setItems(homeState.getNodeList());

    parkingPopup.setOnMousePressed(Event::consume);
    AutofillComboBox parkingAutoCombo = new AutofillComboBox(parkingBox);
    AutofillComboBox toParkingAutoCombo = new AutofillComboBox(destinationBox);

    AutofillComboBox historyAutoCombo = new AutofillComboBox(historyBox);

    sizeListeners();
    displayCustomNodes(null);

    originInput
        .textProperty()
        .addListener(
            (obs, oldValue, newValue) -> {
              if (newValue.length() > 0) {
                originClear.setVisible(true);
                // if (originInput.getId().equals("LOCAL")) return;
                gmapsVBox.setVisible(true);
                directory.getChildren().forEach(n -> ((TitledPane) n).setExpanded(true));
                if (!homeState.getCanPathFind().get() && homeState.isMobile())
                  filteredBtns.setPredicate(n -> false);
                else
                  filteredBtns.setPredicate(
                      n ->
                          n.getText()
                                  .toLowerCase()
                                  .startsWith(newValue.toLowerCase().substring(0, 1))
                              && dist(
                                      n.getText()
                                          .toLowerCase()
                                          .substring(
                                              0, Math.min(newValue.length(), n.getText().length()))
                                          .toCharArray(),
                                      newValue.toLowerCase().toCharArray())
                                  < Math.min(newValue.length(), 3));
                Task<Void> t =
                    new Task<Void>() {
                      @Override
                      protected Void call() {
                        Accordion predictions = getACPlaceList(newValue);
                        Platform.runLater(
                            new Runnable() {
                              @Override
                              public void run() {
                                gmapsVBox.getChildren().removeIf(n -> n instanceof Accordion);
                                gmapsVBox.getChildren().add(predictions);
                              }
                            });
                        return null;
                      }
                    };
                new Thread(t).start();
              } else {
                if (webView.isVisible()) changeWebVisibility(false);
                overlayPaneTwo.getChildren().removeIf(x -> x instanceof Path);
                if (directions.isVisible()) toggleTextDirections(null);
                originInput.setUserData(null);
                originInput.setId("");
                if (homeState.getCanPathFind().get() || !homeState.isMobile()) resetDirectory();
                originClear.setVisible(false);
              }
            });

    destinationInput
        .textProperty()
        .addListener(
            (obs, oldValue, newValue) -> {
              gmapsVBox.setVisible(false);
              if (newValue.length() > 0) {
                directory.getChildren().forEach(n -> ((TitledPane) n).setExpanded(true));
                if (!homeState.getCanPathFind().get() && homeState.isMobile())
                  filteredBtns.setPredicate(n -> false);
                else
                  filteredBtns.setPredicate(
                      n ->
                          n.getText()
                                  .toLowerCase()
                                  .startsWith(newValue.toLowerCase().substring(0, 1))
                              && dist(
                                      n.getText()
                                          .toLowerCase()
                                          .substring(
                                              0, Math.min(newValue.length(), n.getText().length()))
                                          .toCharArray(),
                                      newValue.toLowerCase().toCharArray())
                                  < Math.min(newValue.length(), 3));
                destClear.setVisible(true);
              } else {
                overlayPaneTwo.getChildren().removeIf(x -> x instanceof Path);
                if (directions.isVisible()) toggleTextDirections(null);
                if (homeState.getCanPathFind().get() || !homeState.isMobile()) resetDirectory();
                destClear.setVisible(false);
                destinationInput.setUserData(null);
                destinationInput.setId("");
              }
              //                      Task<Void> t =
              //                              new Task<Void>() {
              //                                @Override
              //                                protected Void call() {
              //                                  Accordion predictions = getACPlaceList(newValue);
              //                                  Platform.runLater(
              //                                          new Runnable() {
              //                                            @Override
              //                                            public void run() {
              //                                              gmapsVBox.getChildren().removeIf(n ->
              // n instanceof Accordion);
              //
              // gmapsVBox.getChildren().add(predictions);
              //                                            }
              //                                          });
              //                                  return null;
              //                                }
              //                              };
              //                      t.run();
            });

    originClear.setOnMouseClicked(
        e -> {
          originInput.clear();
          overlayPaneTwo.getChildren().removeIf(x -> x instanceof Path);
          overlayPaneTwo
              .getChildren()
              .removeIf(c -> c.getId() != null && c.getId().equals("startNode"));
          directions.setVisible(false);
        });

    destClear.setOnMouseClicked(
        e -> {
          destinationInput.clear();
          overlayPaneTwo.getChildren().removeIf(x -> x instanceof Path);
          overlayPaneTwo
              .getChildren()
              .removeIf(c -> c.getId() != null && c.getId().equals("endNode"));
          directions.setVisible(false);
        });

    Platform.runLater(
        () -> {
          floorSelector.animateList();
          handleExpand(new ActionEvent());
        });

    originalChildrenSearchPane = new ArrayList<>();
    originalChildrenSearchPane.addAll(searchPane.getChildren());
  }

  private void resetDirectory() {
    gmapsVBox.getChildren().removeIf(n -> n instanceof Accordion);
    gmapsVBox.setVisible(false);
    directory.getChildren().forEach(n -> ((TitledPane) n).setExpanded(false));
    filteredBtns.setPredicate(n -> true);
  }

  // Adapted from:
  // https://stackoverflow.com/questions/13564464/problems-with-levenshtein-algorithm-in-java
  public static int dist(char[] s1, char[] s2) {

    // memoize only previous line of distance matrix
    int[] prev = new int[s2.length + 1];

    for (int j = 0; j < s2.length + 1; j++) {
      prev[j] = j;
    }

    for (int i = 1; i < s1.length + 1; i++) {

      // calculate current line of distance matrix
      int[] curr = new int[s2.length + 1];
      curr[0] = i;

      for (int j = 1; j < s2.length + 1; j++) {
        int d1 = prev[j] + 1;
        int d2 = curr[j - 1] + 1;
        int d3 = prev[j - 1];
        if (s1[i - 1] != s2[j - 1]) {
          d3 += 1;
        }
        curr[j] = Math.min(Math.min(d1, d2), d3);
      }

      // define current line of distance matrix as previous
      prev = curr;
    }
    return prev[s2.length];
  }

  private void checkFilledAndRoute() {
    if (originInput.getUserData() != null
        && destinationInput.getUserData() != null
        && originInput.getUserData() != destinationInput.getUserData()) {
      resetDirectory();
      changeWebVisibility(false);
      handleGoAction(null);
    }
  }

  private Accordion getACPlaceList(String input) {
    try {
      input = URLEncoder.encode(input, StandardCharsets.UTF_8.toString());
      JSONObject json =
          readJsonFromUrl(
              "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="
                  + input
                  + "&key=AIzaSyB_srKE0wfKrqIWtuzewVKnuJKnnC3mMSQ"
                  + "&location=42.30177919657677,-71.12740383118982"
                  + "&radius=50000&components=country:us");
      if (json.getString("status").equals("OK")) {
        Accordion predictions = new Accordion();
        predictions.maxWidth(210);
        json.getJSONArray("predictions")
            .forEach(x -> predictions.getPanes().add(makePredictedGooglePane((JSONObject) x)));

        System.out.println(
            json.getJSONArray("predictions").getJSONObject(0).getString("description"));

        return predictions;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private TitledPane makePredictedGooglePane(JSONObject prediction) {
    MaterialDesignIconView iv = new MaterialDesignIconView(MaterialDesignIcon.MAP_MARKER);
    iv.setGlyphSize(20);
    Label lb = new Label(prediction.getString("description"), iv);
    lb.setWrapText(true);
    lb.setPrefWidth(210);
    lb.setMinWidth(210);
    lb.setMaxWidth(210);
    lb.setMinHeight(Region.USE_PREF_SIZE);
    lb.setPrefHeight(Region.USE_COMPUTED_SIZE);
    lb.setTextAlignment(TextAlignment.LEFT);
    VBox vb = new VBox(5, lb);
    vb.setAlignment(Pos.TOP_CENTER);
    JFXButton mapBtn = new JFXButton("Set Origin");
    mapBtn.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(3), null)));
    String title =
        prediction.getJSONObject("structured_formatting").getString("main_text")
            + ", "
            + prediction
                .getJSONArray("terms")
                .getJSONObject(prediction.getJSONArray("terms").length() - 2)
                .getString("value");
    mapBtn.setOnAction(
        x -> {
          originInput.setId("GLOBAL");
          globalPlaceID = prediction.getString("place_id");
          originInput.setText(title);
          originInput.setUserData(globalOrigin);
          handleGMapGoAction();
        });
    mapBtn.setButtonType(JFXButton.ButtonType.FLAT);
    vb.getChildren().add(mapBtn);
    TitledPane tp = new TitledPane(title, vb);
    tp.setMinWidth(1);
    tp.setMaxWidth(210);
    return tp;
  }

  private void handleGMapGoAction() {
    // TODO FILL OUT
    System.out.println("Handle GMapGo Action");
    webEngine.executeScript("goToID('" + globalPlaceID + "')");

    handleWebView();
    if (!lastBigWidth) {
      searchPane.setVisible(false);
      btnToInterior.setText("Arrived?");
    }
  }

  private static String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }

  public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
    InputStream is = new URL(url).openStream();
    try {
      BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
      String jsonText = readAll(rd);
      JSONObject json = new JSONObject(jsonText);
      return json;
    } finally {
      is.close();
    }
  }

  public void clearCustomNodes() {
    overlayPaneTwo.getChildren().removeIf(n -> n instanceof ImageView);
  }

  public void displayCustomNodes(String filterKey) {
    Image stair =
        new Image(
            getClass()
                .getResource("/edu/wpi/teamA/assets/img/Map_Icons/StairsIcon.png")
                .toExternalForm());
    Image elevator =
        new Image(
            getClass()
                .getResource("/edu/wpi/teamA/assets/img/Map_Icons/ElevatorIcon.png")
                .toExternalForm());
    Image restroom =
        new Image(
            getClass()
                .getResource("/edu/wpi/teamA/assets/img/Map_Icons/RestroomIcon.png")
                .toExternalForm());
    filterKey = (filterKey == null) ? "STAIELEVREST" : filterKey;
    String finalFilterKey = filterKey;
    double xScale = mapImg.getBoundsInLocal().getWidth() / 2989;
    double yScale = mapImg.getBoundsInLocal().getHeight() / 2457;
    db.getItemsNodes()
        .filtered(x -> finalFilterKey.contains(x.getFieldAsString("nodetype")))
        .forEach(
            n -> {
              ImageView nv =
                  new ImageView(
                      n.getFieldAsString("nodetype").equals("STAI")
                          ? stair
                          : n.getFieldAsString("nodetype").equals("ELEV") ? elevator : restroom);
              nv.setUserData(n.getFieldAsString("floor"));
              nv.setX((Double.parseDouble(n.getFieldAsString("xcoord")) * xScale) - 7);
              nv.setY((Double.parseDouble(n.getFieldAsString("ycoord")) * yScale) - 7);
              nv.setFitWidth(14);
              nv.setVisible(n.getFieldAsString("floor").equals(active));
              nv.setPreserveRatio(true);
              nv.setMouseTransparent(true);
              // nv.setFitWidth();
              overlayPaneTwo.getChildren().add(nv);
            });
  }

  @FXML
  private void swapLocations() {
    overlayPaneTwo.getChildren().removeIf(c -> c.getId() != null && c.getId().equals("startNode"));
    overlayPaneTwo.getChildren().removeIf(c -> c.getId() != null && c.getId().equals("endNode"));

    String destText = destinationInput.getText();
    Object destUData = destinationInput.getUserData();
    if (originInput.getId().equals("GLOBAL")) originInput.clear();
    destinationInput.setText(originInput.getText());
    destinationInput.setUserData(originInput.getUserData());
    originInput.setText(destText);
    originInput.setUserData(null);
    originInput.setUserData(destUData);
    if (!originInput.getText().equals("")) addPinAtNode(originInput.getText(), "startNode");
    if (!destinationInput.getText().equals("")) addPinAtNode(destinationInput.getText(), "endNode");
    checkFilledAndRoute();
  }

  @FXML
  private void handleMapFilter(ActionEvent e) {
    String filter = restCheck.isSelected() ? "REST" : "";
    filter += (elevCheck.isSelected() ? "ELEV" : "");
    filter += (stairCheck.isSelected() ? "STAI" : "");
    System.out.println("Updating filter to: " + filter);
    clearCustomNodes();
    displayCustomNodes(filter);
  }

  private void addPinAtNode(HospitalNode closest, String id) {
    overlayPaneTwo.getChildren().removeIf(c -> c.getId() != null && c.getId().equals(id));

    FontAwesomeIconView icon =
        new FontAwesomeIconView(
            id.equals("endNode") ? FontAwesomeIcon.MAP_MARKER : FontAwesomeIcon.CIRCLE);

    icon.relocate(
        closest.getXCoord() * xScale - (id.equals("endNode") ? 8 : 8),
        closest.getYCoord() * yScale - (id.equals("endNode") ? 10 : 5));
    icon.setFill(Color.valueOf("#f6bd38"));
    icon.setSize(id.equals("endNode") ? "26" : "18");
    icon.setId(id);
    icon.setUserData(closest.getFieldAsString("floor"));
    overlayPaneTwo.getChildren().add(icon);
  }

  private void addPinAtNode(String locationName, String id) {
    HospitalNode node =
        db.getItemsNodes().stream()
            .map(HospitalNode::new)
            .filter(n -> n.toString().equals(locationName))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Destination was set poorly"));

    addPinAtNode(node, id);
  }

  // Go through entire list to get the closest node
  private HospitalNode getClosestNode(int x, int y) {
    return db.getItemsNodes().stream()
        .map(HospitalNode::new)
        .filter(
            n ->
                !"STAIHALLELEVWALKHIDN".contains(n.getFieldAsString("nodetype"))
                    && ("G" + active).contains(n.getFieldAsString("floor")))
        .min(
            (o1, o2) ->
                (int)
                    (computeDistance(x, y, o1.getXCoord(), o1.getYCoord())
                        - computeDistance(x, y, o2.getXCoord(), o2.getYCoord())))
        .orElseThrow(NoSuchElementException::new);
  }

  // Calculate the distance between 2 points
  private double computeDistance(int x1, int y1, int x2, int y2) {
    return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
  }

  @FXML
  private void handleWebView() {
    if (homeState.isMobile()) searchPane.setVisible(webView.isVisible());
    changeWebVisibility(!webView.isVisible());
    if (homeState.isMobile() && homeState.getCanPathFind().get())
      showTextMobile.setVisible(!showTextMobile.isVisible());
    System.out.println("Handling web view");
  }

  private void changeWebVisibility(boolean setVisible) {
    if (setVisible) {
      webView.setVisible(true);
      btnToInterior.setVisible(true);
    } else {
      btnToInterior.setVisible(false);
      webView.setVisible(false);
    }
  }

  private void sizeListeners() {
    scrollPane
        //            .getScene()
        //            .getWindow()
        .widthProperty()
        .addListener(
            new ChangeListener<Number>() {
              @Override
              public void changed(
                  ObservableValue<? extends Number> observableValue,
                  Number oldSceneWidth,
                  Number newSceneWidth) {

                if (oldSceneWidth.doubleValue() != 0.0)
                  rescale((newSceneWidth.doubleValue() / oldSceneWidth.doubleValue()));
                minRatio = Math.max(minRatio, newSceneWidth.doubleValue() / ogWidth);
                if (contentPane.getScene().getWindow().getWidth() < 475 && lastBigWidth) {
                  lastBigWidth = false;
                  //                  textDirectionsToggle.setVisible(true);

                  AnchorPane.setLeftAnchor(mapAnchor, 8.0);
                  AnchorPane.setRightAnchor(mapAnchor, 8.0);
                  AnchorPane.setTopAnchor(mapAnchor, 20.0);
                  AnchorPane.setBottomAnchor(mapAnchor, 20.0);

                  AnchorPane.setLeftAnchor(floorSelector, 0.0);
                  AnchorPane.setBottomAnchor(floorSelector, 300.0);

                  AnchorPane.setBottomAnchor(btnToInterior, 60.0);
                  AnchorPane.setLeftAnchor(btnToInterior, 20.0);
                  AnchorPane.setBottomAnchor(btnToWebView, 60.0);
                  AnchorPane.setLeftAnchor(btnToWebView, 20.0);

                  // IDK why this does not resize as expected when all are set to 0
                  AnchorPane.setLeftAnchor(searchPane, 8.0);
                  AnchorPane.setRightAnchor(searchPane, 8.0);
                  AnchorPane.setTopAnchor(searchPane, 20.0);
                  AnchorPane.setBottomAnchor(searchPane, 730.0);

                  directions.getChildren().removeAll(directionList, buttonRow);
                  directionList.setMaxHeight(200.0);

                  buttonRow2.setVisible(false);
                  floorSelector.setRotate(-180.0);

                  searchPane.setVisible(true);
                  searchPane.setStyle("-fx-background-color: #002d59");
                  searchPane.getChildren().clear();
                  searchPane.getChildren().add(originInput);

                  filterList.setVisible(false);
                  // Remove the existing line
                  if (!homeState.getCanPathFind().get())
                    overlayPaneTwo
                        .getChildren()
                        .removeIf(n -> n instanceof Path || n instanceof FontAwesomeIconView);

                  if (!homeState.getCanPathFind().get()) filteredBtns.setPredicate(n -> false);
                  else resetDirectory();

                  btnToInterior.setOnAction(
                      e -> {
                        if (!homeState.isSurveyTaken()) goToSurvey(null);
                        handleWebView();
                        collapseSearchMobile(null);
                      });

                  originInput.setOnMouseClicked(
                      e -> {
                        expandSearchMobile(null);
                        originClickedEvent.handle(e);
                      });

                  scrollPane.requestFocus();
                } else if (contentPane.getScene().getWindow().getWidth() > 475 && !lastBigWidth) {
                  lastBigWidth = true;
                  //                  textDirectionsToggle.setVisible(false);

                  AnchorPane.setLeftAnchor(mapAnchor, 250.0);
                  AnchorPane.setRightAnchor(mapAnchor, 10.0);
                  AnchorPane.setTopAnchor(mapAnchor, 10.0);
                  AnchorPane.setBottomAnchor(mapAnchor, 10.0);

                  AnchorPane.setLeftAnchor(floorSelector, 20.0);
                  AnchorPane.setBottomAnchor(floorSelector, 20.0);

                  AnchorPane.setBottomAnchor(btnToInterior, 20.0);
                  AnchorPane.setLeftAnchor(btnToInterior, 70.0);
                  AnchorPane.setBottomAnchor(btnToWebView, 20.0);
                  AnchorPane.setLeftAnchor(btnToWebView, 70.0);

                  AnchorPane.setRightAnchor(directions, 10.0);
                  AnchorPane.setTopAnchor(directions, 10.0);

                  AnchorPane.setLeftAnchor(searchPane, 0.0);
                  AnchorPane.setRightAnchor(searchPane, null);
                  AnchorPane.setTopAnchor(searchPane, 20.0);
                  AnchorPane.setBottomAnchor(searchPane, 40.0);

                  searchPane.setVisible(true);
                  searchPane.getChildren().clear();
                  searchPane.getChildren().addAll(originalChildrenSearchPane);
                  searchPane.setStyle("-fx-background-color: transparent");
                  showTextMobile.getChildren().removeAll(buttonRow, directionList);
                  showTextMobile.setVisible(false);
                  directions.getChildren().addAll(directionList, buttonRow);

                  directionList.setMaxWidth(Double.POSITIVE_INFINITY);
                  directionList.setMaxHeight(400.0);

                  originInput.setOnMouseClicked(originClickedEvent);

                  buttonRow2.setVisible(true);
                  floorSelector.setRotate(-90.0);
                  buttonRow2.setStyle(
                      "-fx-padding: 0 0 0 0;"
                          + "-fx-border-insets: 0 0 0 0;"
                          + "-fx-background-insets: 0 0 0 0;");

                  filterList.setVisible(true);
                  scrollPane.requestFocus();
                  handleOgRatio(null);
                }
              }
            });

    scrollPane
        //            .getScene()
        //            .getWindow()
        .heightProperty()
        .addListener(
            new ChangeListener<Number>() {
              @Override
              public void changed(
                  ObservableValue<? extends Number> observableValue,
                  Number oldSceneHeight,
                  Number newSceneHeight) {

                if (oldSceneHeight.doubleValue() != 0.0)
                  rescale((newSceneHeight.doubleValue() / oldSceneHeight.doubleValue()));
                minRatio = Math.max(minRatio, newSceneHeight.doubleValue() / ogHeight);
              }
            });
  }

  @FXML
  private void goToSurvey(ActionEvent e) {
    homeState.setCurrentPane("CovidPane");
  }

  @FXML
  private void handleShowText(ActionEvent e) {
    if (showTextMobile.getChildren().contains(buttonRow)
        || showTextMobile.getChildren().contains(directionList)) return;
    showTextMobile.getChildren().addAll(buttonRow, directionList);
    btnShowText.setOnAction(this::handleHideText);
  }

  @FXML
  private void handleHideText(ActionEvent e) {
    if (!showTextMobile.getChildren().contains(buttonRow)
        || !showTextMobile.getChildren().contains(directionList)) return;
    showTextMobile.getChildren().removeAll(buttonRow, directionList);
    btnShowText.setOnAction(this::handleShowText);
  }

  private void resetButtonStyle() {
    groundButton.setStyle(
        "-fx-background-color: #045393; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #ffffff;");
    f1Button.setStyle(
        "-fx-background-color: #045393; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #ffffff;");
    f2Button.setStyle(
        "-fx-background-color: #045393; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #ffffff;");
    f3Button.setStyle(
        "-fx-background-color: #045393; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #ffffff;");
    f4Button.setStyle(
        "-fx-background-color: #045393; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #ffffff;");
    f5Button.setStyle(
        "-fx-background-color: #045393; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #ffffff;");
  }

  @FXML
  private void handleGroundFloor(ActionEvent e) {
    overlayPaneTwo.getChildren().removeIf(x -> x instanceof Path);
    if (directions.isVisible()) toggleTextDirections(null);
    btnBack.setDisable(true);
    btnNext.setDisable(false);
    buttonRow.setVisible(false);
    switchFloors("G");
  }

  @FXML
  private void handleFloor1(ActionEvent e) {
    overlayPaneTwo.getChildren().removeIf(x -> x instanceof Path);
    if (directions.isVisible()) toggleTextDirections(null);
    btnBack.setDisable(true);
    btnNext.setDisable(false);
    buttonRow.setVisible(false);
    switchFloors("1");
  }

  @FXML
  private void handleFloor2(ActionEvent e) {
    overlayPaneTwo.getChildren().removeIf(x -> x instanceof Path);
    if (directions.isVisible()) toggleTextDirections(null);
    btnBack.setDisable(true);
    btnNext.setDisable(false);
    buttonRow.setVisible(false);
    switchFloors("2");
  }

  @FXML
  private void handleFloor3(ActionEvent e) {
    overlayPaneTwo.getChildren().removeIf(x -> x instanceof Path);
    if (directions.isVisible()) toggleTextDirections(null);
    btnBack.setDisable(true);
    btnNext.setDisable(false);
    buttonRow.setVisible(false);
    switchFloors("3");
  }

  @FXML
  private void handleFloor4(ActionEvent e) {
    overlayPaneTwo.getChildren().removeIf(x -> x instanceof Path);
    if (directions.isVisible()) toggleTextDirections(null);
    btnBack.setDisable(true);
    btnNext.setDisable(false);
    buttonRow.setVisible(false);
    switchFloors("4");
  }

  @FXML
  private void handleFloor5(ActionEvent e) {
    overlayPaneTwo.getChildren().removeIf(x -> x instanceof Path);
    if (directions.isVisible()) toggleTextDirections(null);
    btnBack.setDisable(true);
    btnNext.setDisable(false);
    buttonRow.setVisible(false);
    switchFloors("5");
  }

  private void switchFloors(String floor) {
    overlayPaneTwo.getChildren().stream()
        .filter(n -> (n instanceof FontAwesomeIconView || n instanceof ImageView))
        .forEach(
            n ->
                n.setVisible(
                    n.getUserData().equals(floor)
                        || (floor.equals("1") && n.getUserData().equals("G"))));

    if (active.equals(floor)) return;

    resetButtonStyle();
    buttons
        .get(floor)
        .setStyle(
            "-fx-background-color: #f6bd38; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #ffffff;");

    Fading f = new Fading();
    f.fadeOut(panes.get(active), active.equals("G") ? 0.7 : 0.0);
    f.fadeIn(panes.get(floor), 1.0);
    active = floor;
  }

  @FXML
  private void handlePrint(ActionEvent e) {
    Scene scene = ((Node) e.getSource()).getScene();
    Node node = ((Node) e.getSource()).getParent().getParent();
    // Select printer
    final PrinterJob job =
        Objects.requireNonNull(PrinterJob.createPrinterJob(), "Cannot create printer job");

    if (!job.showPrintDialog(scene.getWindow())) return;

    // Get Screenshot
    WritableImage screenshot = new WritableImage((int) scene.getWidth(), (int) scene.getHeight());
    scene.snapshot(screenshot);

    // Scale image to full page
    Printer printer = job.getPrinter();
    Paper paper = job.getJobSettings().getPageLayout().getPaper();
    PageLayout pageLayout =
        printer.createPageLayout(paper, PageOrientation.LANDSCAPE, Printer.MarginType.DEFAULT);
    double scale =
        Math.min(
            pageLayout.getPrintableWidth() / screenshot.getWidth(),
            pageLayout.getPrintableHeight() / screenshot.getHeight());
    ImageView print_node = new ImageView(screenshot);

    print_node.getTransforms().add(new Scale(scale, scale));
    job.getJobSettings().setPageLayout(pageLayout);

    job.printPage(print_node);
    job.endJob();
  }

  @FXML
  private void handleOgRatio(MouseEvent e) {
    stackPane.setScaleX(1);
    stackPane.setScaleY(1);
    scrollPane.relocate(scrollPane.getHmax() / 2, scrollPane.getVmax() / 2);
    SCALE_TOTAL = 1;
  }

  public void setGoAction() {
    //    for (Record r : db.getItemsSR()) {
    //      if (r.getFieldAsString("type").equals("Hospital Entry")) {
    //        if (r.getFieldAsString("complete").equals("true")) {
    //          goButton.setDisable(false);
    //
    //        } else if (r.getFieldAsString("complete").equals("false")) {
    //          goButton.setDisable(true);
    //          // handleDialog();
    //        }
    //      }
    //    }
  }

  @FXML
  private void handleParkingGo() {
    // check for empty;
    checkParking();

    value.clear();

    originInput.setId("LOCAL");
    originInput.setText(destinationBox.getValue());
    originInput.setUserData(destinationBox.getUserData());
    destinationInput.setText(parkingBox.getValue());
    destinationInput.setUserData(
        parkingBox.getUserData() == null ? parkingBox.getValue() : parkingBox.getUserData());

    HospitalNode from =
        db.getItemsNodes().stream()
            .map(HospitalNode::new)
            .filter(
                n ->
                    !"STAIHALLELEVWALKHIDN".contains(n.getFieldAsString("nodetype"))
                        && n.toString().equals(originInput.getText()))
            .findFirst()
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Destination was set poorly " + originInput.getText()));

    HospitalNode to =
        db.getItemsNodes().stream()
            .map(HospitalNode::new)
            .filter(
                n ->
                    !"STAIHALLELEVWALKHIDN".contains(n.getFieldAsString("nodetype"))
                        && n.toString().equals(destinationInput.getText()))
            .findFirst()
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Destination was set poorly " + originInput.getText()));

    parkingPopup.setVisible(false);
    addPinAtNode(to, "endNode");
    addPinAtNode(from, "startNode");
    switchFloors(active);

    handleGoAction(new ActionEvent());
  }

  @FXML
  private void handleHistoryGo() {
    checkHistory();
    originInput.setUserData(destinationBox.getValue());
    destinationInput.setUserData(historyBox.getValue());

    historyPane.setVisible(false);
    historyPane.setDisable(true);
    switchFloors(active);
    //    handleGoAction(null);
    checkFilledAndRoute();
  }

  @FXML
  private void handleParkingSaved() {
    checkParking();
    parkingLot++;
  }

  private void checkParking() {
    btnParkingGo.setDisable(
        parkingBox.getSelectionModel().isEmpty() || destinationBox.getSelectionModel().isEmpty());
  }

  private void checkHistory() {
    btnHistoryGo.setDisable(historyBox.getSelectionModel().isEmpty());
  }

  @FXML
  private void handleDestinationSaved() {
    checkParking();
  }

  @FXML
  private void handleHistorySaved() {
    checkHistory();
  }

  @FXML
  private void handleWheelchair() {
    wheelchair = wheelchair == 0 ? 1 : 0;
  }

  private ArrayList<ObservableList<String>> createInstructionsSuper() {
    ObservableList<String> newList = FXCollections.observableArrayList();

    for (int i = 0; i < instructions.size(); i++) {
      if (instructions.get(i).toLowerCase().contains("stair")
          || instructions.get(i).toLowerCase().contains("elevator")
          || i == instructions.size() - 1) {
        if (i == instructions.size() - 1) {
          newList.add(instructions.get(i));
          value.add(makeList(newList));
          newList.clear();
        } else if (instructions.get(i + 1).toLowerCase().contains("stair")
            || instructions.get(i + 1).toLowerCase().contains("elevator")) {
        } else {
          newList.add(instructions.get(i));
          value.add(makeList(newList));
          newList.clear();
        }
      } else {
        newList.add(instructions.get(i));
      }
    }

    return value;
  }

  private ObservableList<String> makeList(ObservableList<String> l) {
    ObservableList<String> l2 = FXCollections.observableArrayList();
    l2.addAll(l);
    return l2;
  }

  private void createInstructions(ObservableList<String> instructions) {
    directionList.getItems().clear();
    directionList.setFixedCellSize(50);
    for (int i = 0; i < instructions.size(); i++) {
      MaterialDesignIconView icon;
      if (instructions.get(i).toLowerCase().contains("right")
          && instructions.get(i).toLowerCase().contains("left")) {
        icon = new MaterialDesignIconView(MaterialDesignIcon.TRENDING_UP);
      } else if (instructions.get(i).toLowerCase().contains("right")) {
        icon = new MaterialDesignIconView(MaterialDesignIcon.ARROW_RIGHT);
      } else if (instructions.get(i).toLowerCase().contains("left")) {
        icon = new MaterialDesignIconView(MaterialDesignIcon.ARROW_LEFT);
      } else {
        icon = new MaterialDesignIconView(MaterialDesignIcon.ARROW_UP);
      }

      icon.setSize("20");
      Label step = new Label(instructions.get(i), icon);
      // step.setMinHeight((step.getText().length() / 20.0) * 50);
      step.setMaxWidth(200);
      step.setWrapText(true);
      //      step.setMinHeight(50);
      //      step.setMaxHeight(60);
      // steps.add(step);
      directionList.getItems().add(step);
      // directionList.size(60);
      if (instructions.get(i).length() > 40) {
        directionList.setFixedCellSize(75);
      }
      System.out.println(instructions.get(i));
    }
    directionList.setVisible(true);
  }

  @FXML
  private void handleGoAction(ActionEvent e) {
    if (!homeState.getCanPathFind().get() && homeState.isMobile()) return;

    value = new ArrayList<>();
    floorValue = 0;
    instructions.clear();
    if (!lastBigWidth) {
      collapseSearchMobile(null);
    }

    historyPane.setVisible(false);
    historyPane.setDisable(true);
    parkingPopup.setVisible(false);

    // Remove the old path from the view
    overlayPaneTwo.getChildren().removeIf(x -> x instanceof Path);

    btnPrint.disableProperty().set(false);

    if (!historyLocations.contains(destinationInput.getText())) {
      historyLocations.add(destinationInput.getText());
    }
    historyBox.setItems(historyLocations);

    List<HospitalNode> nodes =
        db.getItemsNodes().stream().map(HospitalNode::new).collect(Collectors.toList());

    System.out.println(originInput.getUserData());

    HospitalNode fromNode =
        nodes.stream()
            .filter(node -> node.toString().equals(originInput.getUserData()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid Node ID"));

    addPinAtNode(fromNode, "startNode");

    System.out.println(destinationInput.getText());
    HospitalNode toNode =
        nodes.stream()
            .filter(node -> node.toString().equals(destinationInput.getUserData()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid Node ID"));

    addPinAtNode(toNode, "endNode");

    if (parkingLot < 1 && fromNode.getType().equals("PARK")) {
      parkingBox.setValue((String) originInput.getUserData());
      parkingBox.setUserData(originInput.getUserData());
      parkingLot++;
    }
    destinationBox.setValue(destinationInput.getText());
    destinationBox.setUserData(destinationInput.getUserData());

    System.out.println(
        "Attempting Pathfinding from: " + fromNode.toString() + " to: " + toNode.toString());

    boolean newFloor = false;
    try {
      // Generate a path for the nodes
      path = pathfindingService.findPath(fromNode.getId(), toNode.getId(), wheelchair);
      path.remove(0);
      // Switch to the current floor if they aren't both on the main floor
      if ("G1".contains(fromNode.getFieldAsString("floor"))) switchFloors("1");
      else switchFloors(fromNode.getFieldAsString("floor"));

    } catch (IllegalStateException error) {
      // directions.setText(
      // "No path found from " + fromNode.toString() + " to: " + toNode.toString() + "!");

      path.clear();
      directions.setVisible(true);
      btnBack.setDisable(true);
      btnNext.setDisable(false);
      buttonRow.setVisible(false);
      return;
    }

    // Create the first point of the path
    Double xRatio = mapImg.getBoundsInLocal().getWidth() / 2989;
    Double yRatio = mapImg.getBoundsInLocal().getHeight() / 2457;
    lines = new ArrayList<>();
    linesSolid = new ArrayList<>();
    textPaths = new ArrayList<>();
    indices = new ArrayList<>();

    lines.add(new Path());
    linesSolid.add(new Path());
    textPaths.add(new StringBuilder());
    indices.add(0);

    int checkWalk = 0;
    Path line = lines.get(0);
    Path lineSolid = linesSolid.get(0);

    Boolean handleGround = false;
    Boolean handleFirst = false;
    Boolean handleSecond = false;
    Boolean handleThird = false;
    Boolean handleFourth = false;
    Boolean handleFifth = false;

    MoveTo startPoint = new MoveTo((fromNode.getXCoord() * xRatio), fromNode.getYCoord() * yRatio);
    line.getElements().add(startPoint);
    lineSolid.getElements().add(startPoint);

    HospitalNode AtriumNode =
        nodes.stream()
            .filter(
                node ->
                    node.toString()
                        .toLowerCase(Locale.ROOT)
                        .equals("ATRIUM MAIN ENTRANCE".toLowerCase(Locale.ROOT)))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid Node ID"));
    StringBuilder textPath = textPaths.get(0);
    // instructions.add("Directions:\n\n");
    if (fromNode.toString().equals(toNode.toString())) {
      instructions.add("You're already at the " + fromNode.toString());
    } else if (fromNode
                .toString()
                .toLowerCase(Locale.ROOT)
                .contains("PARKING GARAGE".toLowerCase(Locale.ROOT))
            && toNode
                .toString()
                .toLowerCase(Locale.ROOT)
                .contains("PARKING GARAGE".toLowerCase(Locale.ROOT))
        || fromNode
                .toString()
                .toLowerCase(Locale.ROOT)
                .contains("PARKING MAIN".toLowerCase(Locale.ROOT))
            && toNode
                .toString()
                .toLowerCase(Locale.ROOT)
                .contains("PARKING MAIN".toLowerCase(Locale.ROOT))) {
      instructions.add("From your Parking spot walk straight towards " + toNode.toString() + "\n");
    } else if (fromNode.getFieldAsString("nodetype").equals("PARK")) {
      instructions.add("From your Parking spot walk towards the sidewalk \n");
      // instructions.add("-------------------- \n");
    } else if (fromNode.getFieldAsString("nodetype").equals("EXIT")) {
      instructions.add("From the Entrance ");
    }

    int checkHidden = 0;
    int currentIndex = 0;
    String currentFloor = " ";
    Boolean multipleFloors = false;
    for (int i = 0; i < path.size(); i++) {
      HospitalNode n = path.get(i);

      line.getElements().add(new LineTo((n.getXCoord() * xRatio), (n.getYCoord() * yRatio)));
      lineSolid.getElements().add(new LineTo((n.getXCoord() * xRatio), (n.getYCoord() * yRatio)));

      HospitalNode nextNode = n;
      HospitalNode previousNode = n;
      if (i + 1 < path.size()) {
        nextNode = path.get(i + 1);
      }
      if (i - 1 >= 0) {
        previousNode = path.get(i - 1);
      }
      if (i == 0) {
        previousNode = fromNode;
      }
      if (i + 1 == path.size() - 1) {
        nextNode = toNode;
      }

      line.getElements().add(new LineTo((n.getXCoord() * xRatio), (n.getYCoord() * yRatio)));

      if (n.getFieldAsString("nodetype").equals("STAI")
          || n.getFieldAsString("nodetype").equals("ELEV")) {
        //        instructions.add("current floor is: " + n.getFieldAsString("floor") + "\n");
        currentFloor = n.getFieldAsString("floor");
        // End the path here for now, but the iterator will have the next location
        while (n.getFieldAsString("nodetype").equals("STAI")
            || n.getFieldAsString("nodetype").equals("ELEV")) n = path.get(++i);

        if (fromNode.getFieldAsString("floor").equals("G")
            && !handleGround
            && fromNode != AtriumNode) {
          handleGround = true;
          HandleG(textPath, fromNode, AtriumNode, path);
          HandleFloorOne(textPath, AtriumNode, toNode, path, currentIndex, i);
        } else if (fromNode.getFieldAsString("floor").equals("G")
            && !handleGround
            && fromNode == AtriumNode) {
          HandleFloorOne(textPath, fromNode, toNode, path, currentIndex, i);
        } else if (currentFloor.equals("1")) {
          HandleFloorOne(textPath, fromNode, toNode, path, currentIndex, i);
        } else if (currentFloor.equals("2")) {
          HandleFloorTwo(textPath, fromNode, toNode, path, currentIndex, i);
        } else if (currentFloor.equals("3")) {
          HandleFloorThree(textPath, fromNode, toNode, path, currentIndex, i);
        } else if (currentFloor.equals("4")) {
          HandleFloorFour(textPath, fromNode, toNode, path, currentIndex, i);
        } else if (currentFloor.equals("5")) {
          HandleFloorFour(textPath, fromNode, toNode, path, currentIndex, i);
        }
        currentFloor = n.getFieldAsString("floor");

        i--;
        n = path.get(i);
        currentIndex = i;
        if (n.getFieldAsString("nodetype").equals("STAI")) {
          instructions.add("Head onto the stairway \n");
        } else {
          instructions.add("Use the elevator \n");
        }
        indices.add(i);
        lines.add(new Path());
        linesSolid.add(new Path());
        textPaths.add(new StringBuilder());
        line = lines.get(lines.size() - 1);
        lineSolid = linesSolid.get(linesSolid.size() - 1);
        textPath = textPaths.get(textPaths.size() - 1);

        //        instructions.add("Directions:\n\n");

        if (n.getFieldAsString("nodetype").equals("STAI")) {
          instructions.add("Take the stairs to floor " + n.getFieldAsString("floor") + "\n");
          // instructions.add("-------------------- \n");
        } else {
          instructions.add("Take the elevator to floor " + n.getFieldAsString("floor") + "\n");
          // instructions.add("-------------------- \n");
        }
        MoveTo splitPoint = new MoveTo((n.getXCoord() * xRatio), n.getYCoord() * yRatio);
        line.getElements().add(splitPoint);
        lineSolid.getElements().add(splitPoint);

        if (toNode.getFieldAsString("floor").equals("G")
            && currentFloor.equals("1")
            && toNode != AtriumNode) {
          HandleFloorOne(textPath, fromNode, AtriumNode, path, currentIndex, path.size());
          HandleG(textPath, AtriumNode, toNode, path);
        } else if (currentFloor.equals("1")) {
          HandleFloorOne(textPath, fromNode, toNode, path, currentIndex, path.size());
          currentIndex = i;
        } else if (currentFloor.equals("2")) {
          HandleFloorTwo(textPath, fromNode, toNode, path, currentIndex, path.size());
          currentIndex = i;
        } else if (currentFloor.equals("3")) {
          HandleFloorThree(textPath, fromNode, toNode, path, currentIndex, path.size());
          currentIndex = i;
        } else if (currentFloor.equals("4")) {
          HandleFloorFour(textPath, fromNode, toNode, path, currentIndex, path.size());
          currentIndex = i;
        } else if (currentFloor.equals("5")) {
          HandleFloorFour(textPath, fromNode, toNode, path, currentIndex, path.size());
          currentIndex = i;
        }
        multipleFloors = true;
      }
    }

    if (!multipleFloors) {
      if (fromNode.getFieldAsString("floor").equals("G")
          && !toNode.getFieldAsString("floor").equals("G")) {
        if (fromNode.getFieldAsString("floor").equals("G") && fromNode != AtriumNode) {
          HandleG(textPath, fromNode, AtriumNode, path);
        }
        if (toNode.getFieldAsString("floor").equals("1") && !handleFirst) {
          handleFirst = true;
          HandleFloorOne(textPath, AtriumNode, toNode, path, 0, path.size());
        }
      } else if (!fromNode.getFieldAsString("floor").equals("G")
          && toNode.getFieldAsString("floor").equals("G")
          && toNode != AtriumNode) {
        HandleFloorOne(textPath, fromNode, AtriumNode, path, 0, path.size());
        HandleG(textPath, AtriumNode, toNode, path);
      } else if (fromNode.getFieldAsString("floor").equals("G") && !handleGround) {
        handleGround = true;
        HandleG(textPath, fromNode, toNode, path);
      } else if (fromNode.getFieldAsString("floor").equals("1") && !handleFirst) {
        handleFirst = true;
        HandleFloorOne(textPath, fromNode, toNode, path, 0, path.size());
      } else if (fromNode.getFieldAsString("floor").equals("2") && !handleSecond) {
        handleSecond = true;
        HandleFloorTwo(textPath, fromNode, toNode, path, 0, path.size());
      } else if (fromNode.getFieldAsString("floor").equals("3") && !handleThird) {
        handleThird = true;
        HandleFloorThree(textPath, fromNode, toNode, path, 0, path.size());
      } else if (fromNode.getFieldAsString("floor").equals("4") && !handleFourth) {
        handleFourth = true;
        HandleFloorFour(textPath, fromNode, toNode, path, 0, path.size());
      } else if (fromNode.getFieldAsString("floor").equals("5") && !handleFifth) {
        handleFifth = true;
        HandleFloorFour(textPath, fromNode, toNode, path, 0, path.size());
      }
    }

    // We need to have the next option
    //      originInput.setDisable(true);
    //      destinationInput.setDisable(true);
    buttonRow.setVisible(lines.size() > 1);

    btnBack.setDisable(true);
    btnNext.setDisable(false);

    current_path = 0;

    Path layer = linesSolid.get(current_path);
    layer.setStroke(Color.valueOf("#002D59"));
    layer.setStrokeWidth(6);
    overlayPaneTwo.getChildren().add(0, layer);

    Path current = lines.get(current_path);
    current.setStroke(Color.valueOf("#f6bd38"));
    current.setStrokeWidth(4);

    //    if (scrollPane.getScene().getWindow().getWidth() < 475) {
    //      directionsOut();
    //    } else {
    directions.setVisible(true);
    //    }
    if (scrollPane.getScene().getWindow().getWidth() < 475) handleShowText(new ActionEvent());

    // directionList.getItems().clear();textPaths.get(0).toString());
    directionList.getItems().clear();
    createInstructionsSuper();
    createInstructions(value.get(floorValue));

    zoomToPath(current);

    overlayPaneTwo.getChildren().add(lines.get(current_path));
    movingDashes(lines.get(current_path));
    //    movingCircle(lines.get(current_path));

  }

  private void zoomToPath(Path line) {
    double newScale =
        Math.min(
            scrollPane.getViewportBounds().getWidth() / (line.getBoundsInLocal().getWidth() + 150),
            scrollPane.getViewportBounds().getHeight()
                / (line.getBoundsInLocal().getHeight() + 150));

    rescale(newScale / SCALE_TOTAL);

    double pathX =
        (line.getBoundsInParent().getMaxX() * SCALE_TOTAL
                + line.getBoundsInParent().getMinX() * SCALE_TOTAL)
            / 2.0;

    double pathY =
        (line.getBoundsInParent().getMaxY() * SCALE_TOTAL
                + line.getBoundsInParent().getMinY() * SCALE_TOTAL)
            / 2.0;

    double rangeX =
        group.getBoundsInParent().getWidth() - scrollPane.getViewportBounds().getWidth();

    double rangeY =
        group.getBoundsInParent().getHeight() - scrollPane.getViewportBounds().getHeight();

    Animation animationY =
        new Timeline(
            new KeyFrame(
                Duration.seconds(0.25),
                new KeyValue(
                    scrollPane.vvalueProperty(),
                    (pathY - scrollPane.getViewportBounds().getHeight() / 2.0) / rangeY)));

    Animation animationX =
        new Timeline(
            new KeyFrame(
                Duration.seconds(0.25),
                new KeyValue(
                    scrollPane.hvalueProperty(),
                    (pathX - scrollPane.getViewportBounds().getWidth() / 2.0) / rangeX)));

    animationX.play();
    animationY.play();

    scrollPane.setHvalue(
        rangeX == 0 ? 0.5 : (pathX - scrollPane.getViewportBounds().getWidth() / 2.0) / rangeX);

    scrollPane.setVvalue(
        rangeY == 0 ? 0.5 : (pathY - scrollPane.getViewportBounds().getHeight() / 2.0) / rangeY);
  }

  @FXML
  private void movingDashes(Path line) {
    line.getStrokeDashArray().setAll(10d, 25d, 10d, 25d);
    line.setStrokeWidth(2);

    final double maxOffset = line.getStrokeDashArray().stream().reduce(0d, (a, b) -> a + b);

    Timeline timeline =
        new Timeline(
            new KeyFrame(
                Duration.ZERO,
                new KeyValue(line.strokeDashOffsetProperty(), 0, Interpolator.LINEAR)),
            new KeyFrame(
                Duration.seconds(2),
                new KeyValue(line.strokeDashOffsetProperty(), maxOffset, Interpolator.LINEAR)));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.setRate(-1);
    timeline.play();
  }

  @FXML
  private void movingCircle(Path line) {
    overlayPaneTwo.getChildren().removeIf(x -> x instanceof Circle);
    Circle circle = new Circle();
    circle.setCenterX(line.getLayoutX());
    circle.setCenterY(line.getLayoutY());
    circle.setRadius(5.0f);
    circle.setFill(Color.valueOf("#002D59"));
    circle.setStrokeWidth(20);

    circle.setVisible(true);

    PathTransition pathTransition = new PathTransition();
    pathTransition.setDuration(millis(4000));
    pathTransition.setPath(line);

    pathTransition.setNode(circle);
    pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
    pathTransition.setCycleCount(Timeline.INDEFINITE);
    // Setting auto reverse value to false
    pathTransition.setAutoReverse(false);

    // Playing the animation
    pathTransition.play();

    overlayPaneTwo.getChildren().add(circle);
    // circle.setVisible(false);
  }

  private void HandleG(
      StringBuilder textPath,
      HospitalNode fromNode,
      HospitalNode toNode,
      ArrayList<HospitalNode> path) {
    for (int i = 0; i < path.size(); i++) {
      HospitalNode n = path.get(i);
      HospitalNode nextNode = n;
      HospitalNode previousNode = n;
      if (i + 1 < path.size()) {
        nextNode = path.get(i + 1);
      }
      if (i - 1 >= 0) {
        previousNode = path.get(i - 1);
      }
      if (i == 0) {
        previousNode = fromNode;
      }
      if (i + 1 == path.size() - 1) {
        nextNode = toNode;
      }

      if (previousNode
              .toString()
              .toLowerCase(Locale.ROOT)
              .contains("PARKING GARAGE".toLowerCase(Locale.ROOT))
          && !toNode
              .toString()
              .toLowerCase(Locale.ROOT)
              .contains("PARKING GARAGE".toLowerCase(Locale.ROOT))) {
        if (nextNode.getXCoord() - n.getXCoord() < 30) {
          instructions.add("Turn left and continue walking \n");
        }
      }

      if (previousNode
              .toString()
              .toLowerCase(Locale.ROOT)
              .equals("DIALYSIS MAIN ENTRANCE".toLowerCase(Locale.ROOT))
          && !toNode
              .toString()
              .toLowerCase(Locale.ROOT)
              .equals("DIALYSIS RAMP".toLowerCase(Locale.ROOT))) {
        instructions.add("Turn right and walk down the sidewalk \n");
        // instructions.add("-------------------- \n");
      } else if (previousNode
              .toString()
              .toLowerCase(Locale.ROOT)
              .equals("DIALYSIS MAIN ENTRANCE".toLowerCase(Locale.ROOT))
          && toNode
              .toString()
              .toLowerCase(Locale.ROOT)
              .equals("DIALYSIS RAMP".toLowerCase(Locale.ROOT))) {
        instructions.add("Turn left and walk down the sidewalk \n");
        // instructions.add("-------------------- \n");
      }

      if (previousNode
          .toString()
          .toLowerCase(Locale.ROOT)
          .equals("DIALYSIS RAMP".toLowerCase(Locale.ROOT))) {
        instructions.add("Continue down the sidewalk \n");
        // instructions.add("-------------------- \n");
      }

      if (previousNode
              .toString()
              .toLowerCase(Locale.ROOT)
              .equals("ATRIUM MAIN ENTRANCE".toLowerCase(Locale.ROOT))
          && !toNode
              .toString()
              .toLowerCase(Locale.ROOT)
              .contains("PARKING MAIN".toLowerCase(Locale.ROOT))) {
        instructions.add("Head left and walk down the sidewalk \n");
        // instructions.add("-------------------- \n");
      } else if (previousNode
              .toString()
              .toLowerCase(Locale.ROOT)
              .equals("ATRIUM MAIN ENTRANCE".toLowerCase(Locale.ROOT))
          && toNode
              .toString()
              .toLowerCase(Locale.ROOT)
              .contains("PARKING MAIN".toLowerCase(Locale.ROOT))) {
        instructions.add("Head straight into the parking area \n");
        // instructions.add("-------------------- \n");
      }

      if (previousNode
              .toString()
              .toLowerCase(Locale.ROOT)
              .equals("EMERGENCY ENTRANCE".toLowerCase(Locale.ROOT))
          && n.toString().toLowerCase(Locale.ROOT).equals("SIDEWALK".toLowerCase(Locale.ROOT))) {
        instructions.add("Bear right \n");
        // instructions.add("-------------------- \n");
      }

      if (n.toString().toLowerCase(Locale.ROOT).equals("GARAGE SPLIT".toLowerCase(Locale.ROOT))) {
        if (previousNode
            .toString()
            .toLowerCase(Locale.ROOT)
            .equals("EMERGENCY ENTRANCE".toLowerCase(Locale.ROOT))) {
          instructions.add("Head straight and bear left \n");
          // instructions.add("-------------------- \n");
        } else if (previousNode.getXCoord() - n.getXCoord() < -10 && nextNode != toNode) {
          instructions.add("Bear right and then bear left \n");
          // instructions.add("-------------------- \n");
        } else if (previousNode.getXCoord() - n.getXCoord() > 10 && nextNode != toNode) {
          instructions.add("Bear right \n");
          // instructions.add("-------------------- \n");
        }
      }

      if (nextNode
          .toString()
          .toLowerCase(Locale.ROOT)
          .equals("CROSSWALK".toLowerCase(Locale.ROOT))) {
        instructions.add("Cross the road \n");
        // instructions.add("-------------------- \n");
      }

      if (n.toString().toLowerCase(Locale.ROOT).equals("SIDEWALK".toLowerCase(Locale.ROOT))
          && nextNode
              .toString()
              .toLowerCase(Locale.ROOT)
              .equals("SIDEWALK".toLowerCase(Locale.ROOT))) {
        if (!previousNode
            .toString()
            .toLowerCase(Locale.ROOT)
            .equals("SIDEWALK".toLowerCase(Locale.ROOT))) {
          instructions.add("Continue down the sidewalk \n");
          // instructions.add("-------------------- \n");
        }
      }

      if (nextNode == toNode) {

        if (nextNode.getYCoord() - n.getYCoord() > 10) {
          if (previousNode.getXCoord() > n.getXCoord()) {
            instructions.add("Head left towards " + toNode.toString() + ". \n");
          } else if (previousNode.getXCoord() < n.getXCoord()) {
            instructions.add("Head right towards " + toNode.toString() + ". \n");
          }
        } else if (nextNode.getYCoord() - n.getYCoord() < -10) {
          if (previousNode.getXCoord() > n.getXCoord()) {
            instructions.add("Head right towards " + toNode.toString() + ". \n");
          } else if (previousNode.getXCoord() < n.getXCoord()) {
            instructions.add("Head left towards " + toNode.toString() + ". \n");
          }
        } else if (nextNode.getXCoord() - n.getXCoord() < -10) {
          if (previousNode.getYCoord() > n.getYCoord()) {
            instructions.add("Head left towards " + toNode.toString() + ". \n");
          } else if (previousNode.getYCoord() < n.getYCoord()) {
            instructions.add("Head right towards " + toNode.toString() + ". \n");
          }
        } else if (nextNode.getXCoord() - n.getXCoord() > 10) {
          if (previousNode.getYCoord() > n.getYCoord()) {
            instructions.add("Head right towards " + toNode.toString() + ". \n");
          } else if (previousNode.getYCoord() < n.getYCoord()) {
            instructions.add("Head left towards " + toNode.toString() + ". \n");
          }
        }
      }
    }
  }

  private void HandleFloorOne(
      StringBuilder textPath,
      HospitalNode fromNode,
      HospitalNode toNode,
      ArrayList<HospitalNode> path,
      int startIndex,
      int endIndex) {
    int hallCount = 1;
    for (int i = startIndex; i < endIndex; i++) {
      HospitalNode n = path.get(i);
      HospitalNode nextNode = n;
      HospitalNode previousNode = n;
      HospitalNode nextHall = n;
      if (i + 1 < path.size()) {
        nextNode = path.get(i + 1);
      }
      if (i - 1 >= 0) {
        previousNode = path.get(i - 1);
      }
      if (i == 0) {
        previousNode = fromNode;
      }
      if (i + 1 == path.size() - 1) {
        nextNode = toNode;
      }

      if (n.getFieldAsString("nodetype").equals("STAI")
          || n.getFieldAsString("nodetype").equals("ELEV")) {
        if (!(nextNode.getFieldAsString("nodetype").equals("STAI")
            || nextNode.getFieldAsString("nodetype").equals("ELEV"))) {
          nextHall = path.get(i + 2);
          if (n.getXCoord() - nextNode.getXCoord() < -5) {
            if (nextHall.getYCoord() < nextNode.getYCoord()) {
              instructions.add("Exit and head left \n");
              // instructions.add("-------------------- \n");
            } else if (nextHall.getYCoord() > nextNode.getYCoord()) {
              instructions.add("Exit and head right \n");
              // instructions.add("-------------------- \n");
            }
          } else if (n.getXCoord() - nextNode.getXCoord() > 5) {
            if (nextHall.getYCoord() < nextNode.getYCoord()) {
              instructions.add("Exit and head right \n");
              // instructions.add("-------------------- \n");
            } else if (nextHall.getYCoord() > nextNode.getYCoord()) {
              instructions.add("Exit and head left \n");
              // instructions.add("-------------------- \n");
            }
          } else {
            if (nextHall.getXCoord() > n.getXCoord()) {
              if (nextHall.getYCoord() > n.getYCoord()) {
                instructions.add("Exit and head left \n");
                // instructions.add("-------------------- \n");
              } else if (nextHall.getYCoord() < n.getYCoord()) {
                instructions.add("Exit and head right \n");
                // instructions.add("-------------------- \n");
              }
            } else if (nextHall.getXCoord() < n.getXCoord()) {
              if (nextHall.getYCoord() > n.getYCoord()) {
                instructions.add("Exit and head right \n");
                // instructions.add("-------------------- \n");
              } else if (nextHall.getYCoord() < n.getYCoord()) {
                instructions.add("Exit and head left \n");
                // instructions.add("-------------------- \n");
              }
            }
          }
        }
      } else if (n.getFieldAsString("nodetype").equals("HALL")) {
        if (previousNode
            .toString()
            .toLowerCase(Locale.ROOT)
            .equals("ATRIUM MAIN ENTRANCE".toLowerCase(Locale.ROOT))) {
          if (nextNode.getYCoord() - n.getYCoord() > -5
              && nextNode.getYCoord() - n.getYCoord() < 5) {
            if (nextNode.getXCoord() > n.getXCoord()) {
              instructions.add("Head left \n");
              // instructions.add("-------------------- \n");
            } else if (nextNode.getXCoord() < n.getXCoord()) {
              instructions.add("Head right towards " + toNode.toString() + "\n");
            }
          } else if (nextNode.getXCoord() - n.getXCoord() > -5
              && nextNode.getXCoord() - n.getXCoord() < 5) {
            instructions.add("Head straight \n");
            // instructions.add("-------------------- \n");
          }
        } else if (previousNode == fromNode) {
          if (previousNode.getXCoord() - n.getXCoord() > 10
              && previousNode.getYCoord() - n.getYCoord() > -5
              && previousNode.getYCoord() - n.getYCoord() < 5) {
            if (nextNode.getYCoord() - n.getYCoord() > 10
                && nextNode.getXCoord() - n.getXCoord() > -5
                && nextNode.getXCoord() - n.getXCoord() < 5) {
              instructions.add("Head straight and turn left \n");
              // instructions.add("-------------------- \n");
            } else if (nextNode.getYCoord() - n.getYCoord() < -10
                && nextNode.getXCoord() - n.getXCoord() > -5
                && nextNode.getXCoord() - n.getXCoord() < 5) {
              instructions.add("Head straight and turn right \n");
              // instructions.add("-------------------- \n");
            }
          } else if (previousNode.getXCoord() - n.getXCoord() < -10
              && previousNode.getYCoord() - n.getYCoord() > -5
              && previousNode.getYCoord() - n.getYCoord() < 5) {
            if (nextNode.getYCoord() - n.getYCoord() > 10
                && nextNode.getXCoord() - n.getXCoord() > -5
                && nextNode.getXCoord() - n.getXCoord() < 5) {
              instructions.add("Head straight and turn right \n");
              // instructions.add("-------------------- \n");
            } else if (nextNode.getYCoord() - n.getYCoord() < -10
                && nextNode.getXCoord() - n.getXCoord() > -5
                && nextNode.getXCoord() - n.getXCoord() < 5) {
              instructions.add("Head straight and turn left \n");
              // instructions.add("-------------------- \n");
            }
          } else {
            if (previousNode.getXCoord() - n.getXCoord() > -3
                && previousNode.getXCoord() - n.getXCoord() < 3) {
              if (nextNode.getYCoord() > previousNode.getYCoord()) {
                instructions.add("Head left \n");
                // instructions.add("-------------------- \n");
              } else if (nextNode.getYCoord() < previousNode.getYCoord()) {
                instructions.add("Head right \n");
                // instructions.add("-------------------- \n");
              }
            } else if (previousNode.getXCoord() > n.getXCoord()) {
              if (nextNode.getYCoord() > previousNode.getYCoord()) {
                instructions.add("Head left \n");
                // instructions.add("-------------------- \n");
              } else if (nextNode.getYCoord() < previousNode.getYCoord()) {
                instructions.add("Head right \n");
                // instructions.add("-------------------- \n");
              }
            } else if (previousNode.getXCoord() < n.getXCoord()) {
              if (nextNode.getYCoord() > previousNode.getYCoord()) {
                instructions.add("Head right \n");
                // instructions.add("-------------------- \n");
              } else if (nextNode.getYCoord() < previousNode.getYCoord()) {
                instructions.add("Head left \n");
                // instructions.add("-------------------- \n");
              }
            }
          }
        } else if (previousNode.getFieldAsString("nodetype").equals("HALL")
            && nextNode.getFieldAsString("nodetype").equals("HALL")) {
          if ((previousNode.getXCoord() - nextNode.getXCoord() > -3
                  && previousNode.getXCoord() - nextNode.getXCoord() < 5)
              || (previousNode.getYCoord() - nextNode.getYCoord() > -3
                  && previousNode.getYCoord() - nextNode.getYCoord() < 5)) {
            hallCount++;
            if (hallCount % 6 == 0) {
              instructions.add("Keep going further \n");
              // instructions.add("-------------------- \n");
              hallCount = 1;
            } else if (hallCount % 4 == 0) {
              instructions.add("Keep walking down the hallway \n");
              // instructions.add("-------------------- \n");
            } else if (hallCount % 2 == 0) {
              instructions.add("Walk straight down the hallway \n");
              // instructions.add("-------------------- \n");
            }
          } else if (previousNode.getXCoord() - n.getXCoord() > -3
              && previousNode.getXCoord() - n.getXCoord() < 5
              && n.getYCoord() - previousNode.getYCoord() > 10) {
            if (nextNode.getXCoord() > n.getXCoord()) {
              instructions.add("Turn left \n");
              // instructions.add("-------------------- \n");
              hallCount = 1;
            } else if (nextNode.getXCoord() < n.getXCoord()) {
              instructions.add("Turn right \n");
              // instructions.add("-------------------- \n");
              hallCount = 1;
            }
          } else if (previousNode.getXCoord() - n.getXCoord() > -3
              && previousNode.getXCoord() - n.getXCoord() < 5
              && n.getYCoord() - previousNode.getYCoord() < -10) {
            if (nextNode.getXCoord() > n.getXCoord()) {
              instructions.add("Turn right \n");
              // instructions.add("-------------------- \n");
              hallCount = 1;
            } else if (nextNode.getXCoord() < n.getXCoord()) {
              instructions.add("Turn left \n");
              // instructions.add("-------------------- \n");
              hallCount = 1;
            }
          } else if (previousNode.getYCoord() - n.getYCoord() > -3
              && previousNode.getYCoord() - n.getYCoord() < 5
              && n.getXCoord() > previousNode.getXCoord()) {
            if (nextNode.getXCoord() - n.getXCoord() > -3
                && nextNode.getXCoord() - n.getXCoord() < 5) {
              if (nextNode.getYCoord() < n.getYCoord()) {
                instructions.add("Turn left \n");
                // instructions.add("-------------------- \n");
                hallCount = 1;
              } else if (nextNode.getYCoord() > n.getYCoord()) {
                instructions.add("Turn right \n");
                // instructions.add("-------------------- \n");
                hallCount = 1;
              }
            }
          } else if (previousNode.getYCoord() - n.getYCoord() > -3
              && previousNode.getYCoord() - n.getYCoord() < 5
              && n.getXCoord() < previousNode.getXCoord()) {
            if (nextNode.getXCoord() - n.getXCoord() > -3
                && nextNode.getXCoord() - n.getXCoord() < 5) {
              if (nextNode.getYCoord() < n.getYCoord()) {
                instructions.add("Turn right \n");
                // instructions.add("-------------------- \n");
                hallCount = 1;
              } else if (nextNode.getYCoord() > n.getYCoord()) {
                instructions.add("Turn left \n");
                // instructions.add("-------------------- \n");
                hallCount = 1;
              }
            }
          }
        }
        if (nextNode == toNode) {
          if ((nextNode.getYCoord() - n.getYCoord() > -3
                  && nextNode.getYCoord() - n.getYCoord() < 5)
              || (nextNode.getXCoord() - n.getXCoord() > -3
                  && nextNode.getXCoord() - n.getXCoord() < 5)) {
            instructions.add(
                "You should now be right next to the "
                    + toNode.toString()
                    + ", you've arrived! \n");
          }
        }
      }
    }
  }

  private void HandleFloorTwo(
      StringBuilder textPath,
      HospitalNode fromNode,
      HospitalNode toNode,
      ArrayList<HospitalNode> path,
      int startIndex,
      int endIndex) {
    int hallCount = 1;
    for (int i = startIndex; i < endIndex; i++) {
      HospitalNode n = path.get(i);
      HospitalNode nextNode = n;
      HospitalNode previousNode = n;
      HospitalNode nextHall = n;
      if (i + 1 < path.size()) {
        nextNode = path.get(i + 1);
      }
      if (i - 1 >= 0) {
        previousNode = path.get(i - 1);
      }
      if (i == 0) {
        previousNode = fromNode;
      }
      if (i + 1 == path.size() - 1) {
        nextNode = toNode;
      }

      if (n.getFieldAsString("nodetype").equals("STAI")
          || n.getFieldAsString("nodetype").equals("ELEV")) {
        if (!(nextNode.getFieldAsString("nodetype").equals("STAI")
            || nextNode.getFieldAsString("nodetype").equals("ELEV"))) {
          nextHall = path.get(i + 2);
          if (n.getXCoord() - nextNode.getXCoord() < -5) {
            if (nextHall.getYCoord() < nextNode.getYCoord()) {
              instructions.add("Exit and head left \n");
              // instructions.add("-------------------- \n");
            } else if (nextHall.getYCoord() > nextNode.getYCoord()) {
              instructions.add("Exit and head right \n");
              // instructions.add("-------------------- \n");
            }
          } else if (n.getXCoord() - nextNode.getXCoord() > 5) {
            if (nextHall.getYCoord() < nextNode.getYCoord()) {
              instructions.add("Exit and head right \n");
              // instructions.add("-------------------- \n");
            } else if (nextHall.getYCoord() > nextNode.getYCoord()) {
              instructions.add("Exit and head left \n");
              // instructions.add("-------------------- \n");
            }
          } else {
            if (nextHall.getXCoord() > n.getXCoord()) {
              if (nextHall.getYCoord() > n.getYCoord()) {
                instructions.add("Exit and head left \n");
                // instructions.add("-------------------- \n");
              } else if (nextHall.getYCoord() < n.getYCoord()) {
                instructions.add("Exit and head right \n");
                // instructions.add("-------------------- \n");
              }
            } else if (nextHall.getXCoord() < n.getXCoord()) {
              if (nextHall.getYCoord() > n.getYCoord()) {
                instructions.add("Exit and head right \n");
                // instructions.add("-------------------- \n");
              } else if (nextHall.getYCoord() < n.getYCoord()) {
                instructions.add("Exit and head left \n");
                // instructions.add("-------------------- \n");
              }
            }
          }
        }
      } else if (n.getFieldAsString("nodetype").equals("HALL")) {
        if (previousNode == fromNode) {
          if (previousNode.getXCoord() - n.getXCoord() > 10
              && previousNode.getYCoord() - n.getYCoord() > -5
              && previousNode.getYCoord() - n.getYCoord() < 5) {
            if (nextNode.getYCoord() - n.getYCoord() > 10
                && nextNode.getXCoord() - n.getXCoord() > -5
                && nextNode.getXCoord() - n.getXCoord() < 5) {
              instructions.add("Head straight and turn left \n");
              // instructions.add("-------------------- \n");
            } else if (nextNode.getYCoord() - n.getYCoord() < -10
                && nextNode.getXCoord() - n.getXCoord() > -5
                && nextNode.getXCoord() - n.getXCoord() < 5) {
              instructions.add("Head straight and turn right \n");
              // instructions.add("-------------------- \n");
            }
          } else if (previousNode.getXCoord() - n.getXCoord() < -10
              && previousNode.getYCoord() - n.getYCoord() > -5
              && previousNode.getYCoord() - n.getYCoord() < 5) {
            if (nextNode.getYCoord() - n.getYCoord() > 10
                && nextNode.getXCoord() - n.getXCoord() > -5
                && nextNode.getXCoord() - n.getXCoord() < 5) {
              instructions.add("Head straight and turn right \n");
              // instructions.add("-------------------- \n");
            } else if (nextNode.getYCoord() - n.getYCoord() < -10
                && nextNode.getXCoord() - n.getXCoord() > -5
                && nextNode.getXCoord() - n.getXCoord() < 5) {
              instructions.add("Head straight and turn left \n");
              // instructions.add("-------------------- \n");
            }
          } else {
            if (previousNode.getXCoord() - n.getXCoord() > -3
                && previousNode.getXCoord() - n.getXCoord() < 3) {
              if (nextNode.getYCoord() > previousNode.getYCoord()) {
                instructions.add("Head left \n");
                // instructions.add("-------------------- \n");
              } else if (nextNode.getYCoord() < previousNode.getYCoord()) {
                instructions.add("Head right \n");
                // instructions.add("-------------------- \n");
              }
            } else if (previousNode.getXCoord() > n.getXCoord()) {
              if (nextNode.getYCoord() > previousNode.getYCoord()) {
                instructions.add("Head left \n");
                // instructions.add("-------------------- \n");
              } else if (nextNode.getYCoord() < previousNode.getYCoord()) {
                instructions.add("Head right \n");
                // instructions.add("-------------------- \n");
              }
            } else if (previousNode.getXCoord() < n.getXCoord()) {
              if (nextNode.getYCoord() > previousNode.getYCoord()) {
                instructions.add("Head right \n");
                // instructions.add("-------------------- \n");
              } else if (nextNode.getYCoord() < previousNode.getYCoord()) {
                instructions.add("Head left \n");
                // instructions.add("-------------------- \n");
              }
            }
          }
        } else if (previousNode.getFieldAsString("nodetype").equals("HALL")
            && nextNode.getFieldAsString("nodetype").equals("HALL")) {
          if ((previousNode.getXCoord() - nextNode.getXCoord() > -3
                  && previousNode.getXCoord() - nextNode.getXCoord() < 5)
              || (previousNode.getYCoord() - nextNode.getYCoord() > -3
                  && previousNode.getYCoord() - nextNode.getYCoord() < 5)) {
            hallCount++;
            if (hallCount % 6 == 0) {
              instructions.add("Keep going further \n");
              // instructions.add("-------------------- \n");
              hallCount = 1;
            } else if (hallCount % 4 == 0) {
              instructions.add("Keep walking down the hallway \n");
              // instructions.add("-------------------- \n");
            } else if (hallCount % 2 == 0) {
              instructions.add("Walk straight down the hallway \n");
              // instructions.add("-------------------- \n");
            }
          } else if (previousNode.getXCoord() - n.getXCoord() > -3
              && previousNode.getXCoord() - n.getXCoord() < 5
              && n.getYCoord() - previousNode.getYCoord() > 10) {
            if (nextNode.getXCoord() > n.getXCoord()) {
              instructions.add("Turn left \n");
              // instructions.add("-------------------- \n");
              hallCount = 1;
            } else if (nextNode.getXCoord() < n.getXCoord()) {
              instructions.add("Turn right \n");
              // instructions.add("-------------------- \n");
              hallCount = 1;
            }
          } else if (previousNode.getXCoord() - n.getXCoord() > -3
              && previousNode.getXCoord() - n.getXCoord() < 5
              && n.getYCoord() - previousNode.getYCoord() < -10) {
            if (nextNode.getXCoord() > n.getXCoord()) {
              instructions.add("Turn right \n");
              // instructions.add("-------------------- \n");
              hallCount = 1;
            } else if (nextNode.getXCoord() < n.getXCoord()) {
              instructions.add("Turn left \n");
              // instructions.add("-------------------- \n");
              hallCount = 1;
            }
          } else if (previousNode.getYCoord() - n.getYCoord() > -3
              && previousNode.getYCoord() - n.getYCoord() < 5
              && n.getXCoord() > previousNode.getXCoord()) {
            if (nextNode.getXCoord() - n.getXCoord() > -3
                && nextNode.getXCoord() - n.getXCoord() < 5) {
              if (nextNode.getYCoord() < n.getYCoord()) {
                instructions.add("Turn left \n");
                // instructions.add("-------------------- \n");
                hallCount = 1;
              } else if (nextNode.getYCoord() > n.getYCoord()) {
                instructions.add("Turn right \n");
                // instructions.add("-------------------- \n");
                hallCount = 1;
              }
            }
          } else if (previousNode.getYCoord() - n.getYCoord() > -3
              && previousNode.getYCoord() - n.getYCoord() < 5
              && n.getXCoord() < previousNode.getXCoord()) {
            if (nextNode.getXCoord() - n.getXCoord() > -3
                && nextNode.getXCoord() - n.getXCoord() < 5) {
              if (nextNode.getYCoord() < n.getYCoord()) {
                instructions.add("Turn right \n");
                // instructions.add("-------------------- \n");
                hallCount = 1;
              } else if (nextNode.getYCoord() > n.getYCoord()) {
                instructions.add("Turn left \n");
                // instructions.add("-------------------- \n");
                hallCount = 1;
              }
            }
          }
        } else if (nextNode == toNode) {
          if ((nextNode.getYCoord() - n.getYCoord() > -10
                  && nextNode.getYCoord() - n.getYCoord() < 10)
              || (nextNode.getXCoord() - n.getXCoord() > -10
                  && nextNode.getXCoord() - n.getXCoord() < 10)) {
            instructions.add(
                "You should now be right next to the "
                    + toNode.toString()
                    + ", you've arrived! \n");
          }
        }
      }
    }
  }

  private void HandleFloorThree(
      StringBuilder textPath,
      HospitalNode fromNode,
      HospitalNode toNode,
      ArrayList<HospitalNode> path,
      int startIndex,
      int endIndex) {
    int hallCount = 1;
    for (int i = startIndex; i < endIndex; i++) {
      HospitalNode n = path.get(i);
      HospitalNode nextNode = n;
      HospitalNode previousNode = n;
      HospitalNode nextHall = n;
      if (i + 1 < path.size()) {
        nextNode = path.get(i + 1);
      }
      if (i - 1 >= 0) {
        previousNode = path.get(i - 1);
      }
      if (i == 0) {
        previousNode = fromNode;
      }
      if (i + 1 == path.size() - 1) {
        nextNode = toNode;
      }

      if (n.getFieldAsString("nodetype").equals("STAI")
          || n.getFieldAsString("nodetype").equals("ELEV")) {
        if (!(nextNode.getFieldAsString("nodetype").equals("STAI")
            || nextNode.getFieldAsString("nodetype").equals("ELEV"))) {
          nextHall = path.get(i + 2);
          if (n.getXCoord() - nextNode.getXCoord() < -5) {
            if (nextHall.getYCoord() < nextNode.getYCoord()) {
              instructions.add("Exit and head left \n");
              // instructions.add("-------------------- \n");
            } else if (nextHall.getYCoord() > nextNode.getYCoord()) {
              instructions.add("Exit and head right \n");
              // instructions.add("-------------------- \n");
            }
          } else if (n.getXCoord() - nextNode.getXCoord() > 5) {
            if (nextHall.getYCoord() < nextNode.getYCoord()) {
              instructions.add("Exit and head right \n");
              // instructions.add("-------------------- \n");
            } else if (nextHall.getYCoord() > nextNode.getYCoord()) {
              instructions.add("Exit and head left \n");
              // instructions.add("-------------------- \n");
            }
          } else {
            if (nextHall.getXCoord() > n.getXCoord()) {
              if (nextHall.getYCoord() > n.getYCoord()) {
                instructions.add("Exit and head left \n");
                // instructions.add("-------------------- \n");
              } else if (nextHall.getYCoord() < n.getYCoord()) {
                instructions.add("Exit and head right \n");
                // instructions.add("-------------------- \n");
              }
            } else if (nextHall.getXCoord() < n.getXCoord()) {
              if (nextHall.getYCoord() > n.getYCoord()) {
                instructions.add("Exit and head right \n");
                // instructions.add("-------------------- \n");
              } else if (nextHall.getYCoord() < n.getYCoord()) {
                instructions.add("Exit and head left \n");
                // instructions.add("-------------------- \n");
              }
            }
          }
        }
      } else if (n.getFieldAsString("nodetype").equals("HALL")) {
        if (previousNode == fromNode) {
          if (previousNode.getXCoord() - n.getXCoord() > 10
              && previousNode.getYCoord() - n.getYCoord() > -5
              && previousNode.getYCoord() - n.getYCoord() < 5) {
            if (nextNode.getYCoord() - n.getYCoord() > 10
                && nextNode.getXCoord() - n.getXCoord() > -5
                && nextNode.getXCoord() - n.getXCoord() < 5) {
              instructions.add("Head straight and turn left \n");
              // instructions.add("-------------------- \n");
            } else if (nextNode.getYCoord() - n.getYCoord() < -10
                && nextNode.getXCoord() - n.getXCoord() > -5
                && nextNode.getXCoord() - n.getXCoord() < 5) {
              instructions.add("Head straight and turn right \n");
              // instructions.add("-------------------- \n");
            }
          } else if (previousNode.getXCoord() - n.getXCoord() < -10
              && previousNode.getYCoord() - n.getYCoord() > -5
              && previousNode.getYCoord() - n.getYCoord() < 5) {
            if (nextNode.getYCoord() - n.getYCoord() > 10
                && nextNode.getXCoord() - n.getXCoord() > -5
                && nextNode.getXCoord() - n.getXCoord() < 5) {
              instructions.add("Head straight and turn right \n");
              // instructions.add("-------------------- \n");
            } else if (nextNode.getYCoord() - n.getYCoord() < -10
                && nextNode.getXCoord() - n.getXCoord() > -5
                && nextNode.getXCoord() - n.getXCoord() < 5) {
              instructions.add("Head straight and turn left \n");
              // instructions.add("-------------------- \n");
            }
          } else {
            if (previousNode.getXCoord() - n.getXCoord() > -3
                && previousNode.getXCoord() - n.getXCoord() < 3) {
              if (nextNode.getYCoord() > previousNode.getYCoord()) {
                instructions.add("Head left \n");
                // instructions.add("-------------------- \n");
              } else if (nextNode.getYCoord() < previousNode.getYCoord()) {
                instructions.add("Head right \n");
                // instructions.add("-------------------- \n");
              }
            } else if (previousNode.getXCoord() > n.getXCoord()) {
              if (nextNode.getYCoord() > previousNode.getYCoord()) {
                instructions.add("Head left \n");
                // instructions.add("-------------------- \n");
              } else if (nextNode.getYCoord() < previousNode.getYCoord()) {
                instructions.add("Head right \n");
                // instructions.add("-------------------- \n");
              }
            } else if (previousNode.getXCoord() < n.getXCoord()) {
              if (nextNode.getYCoord() > previousNode.getYCoord()) {
                instructions.add("Head right \n");
                // instructions.add("-------------------- \n");
              } else if (nextNode.getYCoord() < previousNode.getYCoord()) {
                instructions.add("Head left \n");
                // instructions.add("-------------------- \n");
              }
            }
          }
        } else if (previousNode.getFieldAsString("nodetype").equals("HALL")
            && nextNode.getFieldAsString("nodetype").equals("HALL")) {
          if ((previousNode.getXCoord() - nextNode.getXCoord() > -3
                  && previousNode.getXCoord() - nextNode.getXCoord() < 5)
              || (previousNode.getYCoord() - nextNode.getYCoord() > -3
                  && previousNode.getYCoord() - nextNode.getYCoord() < 5)) {
            hallCount++;
            if (hallCount % 6 == 0) {
              instructions.add("Keep going further \n");
              // instructions.add("-------------------- \n");
              hallCount = 1;
            } else if (hallCount % 4 == 0) {
              instructions.add("Keep walking down the hallway \n");
              // instructions.add("-------------------- \n");
            } else if (hallCount % 2 == 0) {
              instructions.add("Walk straight down the hallway \n");
              // instructions.add("-------------------- \n");
            }
          } else if (previousNode.getXCoord() - n.getXCoord() > -3
              && previousNode.getXCoord() - n.getXCoord() < 5
              && n.getYCoord() - previousNode.getYCoord() > 10) {
            if (nextNode.getXCoord() > n.getXCoord()) {
              instructions.add("Turn left \n");
              // instructions.add("-------------------- \n");
              hallCount = 1;
            } else if (nextNode.getXCoord() < n.getXCoord()) {
              instructions.add("Turn right \n");
              // instructions.add("-------------------- \n");
              hallCount = 1;
            }
          } else if (previousNode.getXCoord() - n.getXCoord() > -3
              && previousNode.getXCoord() - n.getXCoord() < 5
              && n.getYCoord() - previousNode.getYCoord() < -10) {
            if (nextNode.getXCoord() > n.getXCoord()) {
              instructions.add("Turn right \n");
              // instructions.add("-------------------- \n");
              hallCount = 1;
            } else if (nextNode.getXCoord() < n.getXCoord()) {
              instructions.add("Turn left \n");
              // instructions.add("-------------------- \n");
              hallCount = 1;
            }
          } else if (previousNode.getYCoord() - n.getYCoord() > -3
              && previousNode.getYCoord() - n.getYCoord() < 5
              && n.getXCoord() > previousNode.getXCoord()) {
            if (nextNode.getXCoord() - n.getXCoord() > -3
                && nextNode.getXCoord() - n.getXCoord() < 5) {
              if (nextNode.getYCoord() < n.getYCoord()) {
                instructions.add("Turn left \n");
                // instructions.add("-------------------- \n");
                hallCount = 1;
              } else if (nextNode.getYCoord() > n.getYCoord()) {
                instructions.add("Turn right \n");
                // instructions.add("-------------------- \n");
                hallCount = 1;
              }
            }
          } else if (previousNode.getYCoord() - n.getYCoord() > -3
              && previousNode.getYCoord() - n.getYCoord() < 5
              && n.getXCoord() < previousNode.getXCoord()) {
            if (nextNode.getXCoord() - n.getXCoord() > -3
                && nextNode.getXCoord() - n.getXCoord() < 5) {
              if (nextNode.getYCoord() < n.getYCoord()) {
                instructions.add("Turn right \n");
                // instructions.add("-------------------- \n");
                hallCount = 1;
              } else if (nextNode.getYCoord() > n.getYCoord()) {
                instructions.add("Turn left \n");
                // instructions.add("-------------------- \n");
                hallCount = 1;
              }
            }
          }
        } else if (nextNode == toNode) {
          if ((nextNode.getYCoord() - n.getYCoord() > -10
                  && nextNode.getYCoord() - n.getYCoord() < 10)
              || (nextNode.getXCoord() - n.getXCoord() > -10
                  && nextNode.getXCoord() - n.getXCoord() < 10)) {
            instructions.add(
                "You should now be right next to the "
                    + toNode.toString()
                    + ", you've arrived! \n");
          }
        }
      }
    }
  }

  private void HandleFloorFour(
      StringBuilder textPath,
      HospitalNode fromNode,
      HospitalNode toNode,
      ArrayList<HospitalNode> path,
      int startIndex,
      int endIndex) {
    int hallCount = 1;
    for (int i = startIndex; i < endIndex; i++) {
      HospitalNode n = path.get(i);
      HospitalNode nextNode = n;
      HospitalNode previousNode = n;
      HospitalNode nextHall = n;
      if (i + 1 < path.size()) {
        nextNode = path.get(i + 1);
      }
      if (i - 1 >= 0) {
        previousNode = path.get(i - 1);
      }
      if (i == 0) {
        previousNode = fromNode;
      }
      if (i + 1 == path.size() - 1) {
        nextNode = toNode;
      }

      if (n.getFieldAsString("nodetype").equals("STAI")
          || n.getFieldAsString("nodetype").equals("ELEV")) {
        if (!(nextNode.getFieldAsString("nodetype").equals("STAI")
            || nextNode.getFieldAsString("nodetype").equals("ELEV"))) {
          nextHall = path.get(i + 2);
          if (n.getXCoord() - nextNode.getXCoord() < -5) {
            if (nextHall.getYCoord() < nextNode.getYCoord()) {
              instructions.add("Exit and head left \n");
              // instructions.add("-------------------- \n");
            } else if (nextHall.getYCoord() > nextNode.getYCoord()) {
              instructions.add("Exit and head right \n");
              // instructions.add("-------------------- \n");
            }
          } else if (n.getXCoord() - nextNode.getXCoord() > 5) {
            if (nextHall.getYCoord() < nextNode.getYCoord()) {
              instructions.add("Exit and head right \n");
              // instructions.add("-------------------- \n");
            } else if (nextHall.getYCoord() > nextNode.getYCoord()) {
              instructions.add("Exit and head left \n");
              // instructions.add("-------------------- \n");
            }
          } else {
            if (nextHall.getXCoord() > n.getXCoord()) {
              if (nextHall.getYCoord() > n.getYCoord()) {
                instructions.add("Exit and head left \n");
                // instructions.add("-------------------- \n");
              } else if (nextHall.getYCoord() < n.getYCoord()) {
                instructions.add("Exit and head right \n");
                // instructions.add("-------------------- \n");
              }
            } else if (nextHall.getXCoord() < n.getXCoord()) {
              if (nextHall.getYCoord() > n.getYCoord()) {
                instructions.add("Exit and head right \n");
                // instructions.add("-------------------- \n");
              } else if (nextHall.getYCoord() < n.getYCoord()) {
                instructions.add("Exit and head left \n");
                // instructions.add("-------------------- \n");
              }
            }
          }
        }
      } else if (n.getFieldAsString("nodetype").equals("HALL")) {
        if (previousNode == fromNode) {
          if (previousNode.getXCoord() - n.getXCoord() > 10
              && previousNode.getYCoord() - n.getYCoord() > -5
              && previousNode.getYCoord() - n.getYCoord() < 5) {
            if (nextNode.getYCoord() - n.getYCoord() > 10
                && nextNode.getXCoord() - n.getXCoord() > -5
                && nextNode.getXCoord() - n.getXCoord() < 5) {
              instructions.add("Head straight and turn left \n");
              // instructions.add("-------------------- \n");
            } else if (nextNode.getYCoord() - n.getYCoord() < -10
                && nextNode.getXCoord() - n.getXCoord() > -5
                && nextNode.getXCoord() - n.getXCoord() < 5) {
              instructions.add("Head straight and turn right \n");
              // instructions.add("-------------------- \n");
            }
          } else if (previousNode.getXCoord() - n.getXCoord() < -10
              && previousNode.getYCoord() - n.getYCoord() > -5
              && previousNode.getYCoord() - n.getYCoord() < 5) {
            if (nextNode.getYCoord() - n.getYCoord() > 10
                && nextNode.getXCoord() - n.getXCoord() > -5
                && nextNode.getXCoord() - n.getXCoord() < 5) {
              instructions.add("Head straight and turn right \n");
              // instructions.add("-------------------- \n");
            } else if (nextNode.getYCoord() - n.getYCoord() < -10
                && nextNode.getXCoord() - n.getXCoord() > -5
                && nextNode.getXCoord() - n.getXCoord() < 5) {
              instructions.add("Head straight and turn left \n");
              // instructions.add("-------------------- \n");
            }
          } else {
            if (previousNode.getXCoord() - n.getXCoord() > -3
                && previousNode.getXCoord() - n.getXCoord() < 3) {
              if (nextNode.getYCoord() > previousNode.getYCoord()) {
                instructions.add("Head left \n");
                // instructions.add("-------------------- \n");
              } else if (nextNode.getYCoord() < previousNode.getYCoord()) {
                instructions.add("Head right \n");
                // instructions.add("-------------------- \n");
              }
            } else if (previousNode.getXCoord() > n.getXCoord()) {
              if (nextNode.getYCoord() > previousNode.getYCoord()) {
                instructions.add("Head left \n");
                // instructions.add("-------------------- \n");
              } else if (nextNode.getYCoord() < previousNode.getYCoord()) {
                instructions.add("Head right \n");
                // instructions.add("-------------------- \n");
              }
            } else if (previousNode.getXCoord() < n.getXCoord()) {
              if (nextNode.getYCoord() > previousNode.getYCoord()) {
                instructions.add("Head right \n");
                // instructions.add("-------------------- \n");
              } else if (nextNode.getYCoord() < previousNode.getYCoord()) {
                instructions.add("Head left \n");
                // instructions.add("-------------------- \n");
              }
            }
          }
        } else if (previousNode.getFieldAsString("nodetype").equals("HALL")
            && nextNode.getFieldAsString("nodetype").equals("HALL")) {
          if ((previousNode.getXCoord() - nextNode.getXCoord() > -3
                  && previousNode.getXCoord() - nextNode.getXCoord() < 5)
              || (previousNode.getYCoord() - nextNode.getYCoord() > -3
                  && previousNode.getYCoord() - nextNode.getYCoord() < 5)) {
            hallCount++;
            if (hallCount % 6 == 0) {
              instructions.add("Keep going further \n");
              // instructions.add("-------------------- \n");
              hallCount = 1;
            } else if (hallCount % 4 == 0) {
              instructions.add("Keep walking down the hallway \n");
              // instructions.add("-------------------- \n");
            } else if (hallCount % 2 == 0) {
              instructions.add("Walk straight down the hallway \n");
              // instructions.add("-------------------- \n");
            }
          } else if (previousNode.getXCoord() - n.getXCoord() > -3
              && previousNode.getXCoord() - n.getXCoord() < 5
              && n.getYCoord() - previousNode.getYCoord() > 10) {
            if (nextNode.getXCoord() > n.getXCoord()) {
              instructions.add("Turn left \n");
              // instructions.add("-------------------- \n");
              hallCount = 1;
            } else if (nextNode.getXCoord() < n.getXCoord()) {
              instructions.add("Turn right \n");
              // instructions.add("-------------------- \n");
              hallCount = 1;
            }
          } else if (previousNode.getXCoord() - n.getXCoord() > -3
              && previousNode.getXCoord() - n.getXCoord() < 5
              && n.getYCoord() - previousNode.getYCoord() < -10) {
            if (nextNode.getXCoord() > n.getXCoord()) {
              instructions.add("Turn right \n");
              // instructions.add("-------------------- \n");
              hallCount = 1;
            } else if (nextNode.getXCoord() < n.getXCoord()) {
              instructions.add("Turn left \n");
              // instructions.add("-------------------- \n");
              hallCount = 1;
            }
          } else if (previousNode.getYCoord() - n.getYCoord() > -3
              && previousNode.getYCoord() - n.getYCoord() < 5
              && n.getXCoord() > previousNode.getXCoord()) {
            if (nextNode.getXCoord() - n.getXCoord() > -3
                && nextNode.getXCoord() - n.getXCoord() < 5) {
              if (nextNode.getYCoord() < n.getYCoord()) {
                instructions.add("Turn left \n");
                // instructions.add("-------------------- \n");
                hallCount = 1;
              } else if (nextNode.getYCoord() > n.getYCoord()) {
                instructions.add("Turn right \n");
                // instructions.add("-------------------- \n");
                hallCount = 1;
              }
            }
          } else if (previousNode.getYCoord() - n.getYCoord() > -3
              && previousNode.getYCoord() - n.getYCoord() < 5
              && n.getXCoord() < previousNode.getXCoord()) {
            if (nextNode.getXCoord() - n.getXCoord() > -3
                && nextNode.getXCoord() - n.getXCoord() < 5) {
              if (nextNode.getYCoord() < n.getYCoord()) {
                instructions.add("Turn right \n");
                // instructions.add("-------------------- \n");
                hallCount = 1;
              } else if (nextNode.getYCoord() > n.getYCoord()) {
                instructions.add("Turn left \n");
                // instructions.add("-------------------- \n");
                hallCount = 1;
              }
            }
          }
        } else if (nextNode == toNode) {
          if ((nextNode.getYCoord() - n.getYCoord() > -10
                  && nextNode.getYCoord() - n.getYCoord() < 10)
              || (nextNode.getXCoord() - n.getXCoord() > -10
                  && nextNode.getXCoord() - n.getXCoord() < 10)) {
            instructions.add(
                "You should now be right next to the "
                    + toNode.toString()
                    + ", you've arrived! \n");
          }
        }
      }
    }
  }

  //  private void HandleFloorFive(
  //      StringBuilder textPath,
  //      HospitalNode fromNode,
  //      HospitalNode toNode,
  //      ArrayList<HospitalNode> path,
  //      int startIndex,
  //      int endIndex) {
  //    int hallCount = 1;
  //    for (int i = startIndex; i < endIndex; i++) {
  //      HospitalNode n = path.get(i);
  //      HospitalNode nextNode = n;
  //      HospitalNode previousNode = n;
  //      HospitalNode nextHall = n;
  //      if (i + 1 < path.size()) {
  //        nextNode = path.get(i + 1);
  //      }
  //      if (i - 1 >= 0) {
  //        previousNode = path.get(i - 1);
  //      }
  //      if (i == 0) {
  //        previousNode = fromNode;
  //      }
  //      if (i + 1 == path.size() - 1) {
  //        nextNode = toNode;
  //      }
  //
  //      if (n.getFieldAsString("nodetype").equals("STAI")
  //          || n.getFieldAsString("nodetype").equals("ELEV")) {
  //        if (!(nextNode.getFieldAsString("nodetype").equals("STAI")
  //            || nextNode.getFieldAsString("nodetype").equals("ELEV"))) {
  //          nextHall = path.get(i + 2);
  //          if (n.getXCoord() - nextNode.getXCoord() < -5) {
  //            if (nextHall.getYCoord() < nextNode.getYCoord()) {
  //              instructions.add("Exit and head left \n");
  //              // instructions.add("-------------------- \n");
  //            } else if (nextHall.getYCoord() > nextNode.getYCoord()) {
  //              instructions.add("Exit and head right \n");
  //              // instructions.add("-------------------- \n");
  //            }
  //          } else if (n.getXCoord() - nextNode.getXCoord() > 5) {
  //            if (nextHall.getYCoord() < nextNode.getYCoord()) {
  //              instructions.add("Exit and head right \n");
  //              // instructions.add("-------------------- \n");
  //            } else if (nextHall.getYCoord() > nextNode.getYCoord()) {
  //              instructions.add("Exit and head left \n");
  //              // instructions.add("-------------------- \n");
  //            }
  //          } else {
  //            if (nextHall.getXCoord() > n.getXCoord()) {
  //              if (nextHall.getYCoord() > n.getYCoord()) {
  //                instructions.add("Exit and head left \n");
  //                // instructions.add("-------------------- \n");
  //              } else if (nextHall.getYCoord() < n.getYCoord()) {
  //                instructions.add("Exit and head right \n");
  //                // instructions.add("-------------------- \n");
  //              }
  //            } else if (nextHall.getXCoord() < n.getXCoord()) {
  //              if (nextHall.getYCoord() > n.getYCoord()) {
  //                instructions.add("Exit and head right \n");
  //                // instructions.add("-------------------- \n");
  //              } else if (nextHall.getYCoord() < n.getYCoord()) {
  //                instructions.add("Exit and head left \n");
  //                // instructions.add("-------------------- \n");
  //              }
  //            }
  //          }
  //        }
  //      } else if (n.getFieldAsString("nodetype").equals("HALL")) {
  //        if (previousNode == fromNode) {
  //          if (previousNode.getXCoord() - n.getXCoord() > 10
  //              && previousNode.getYCoord() - n.getYCoord() > -5
  //              && previousNode.getYCoord() - n.getYCoord() < 5) {
  //            if (nextNode.getYCoord() - n.getYCoord() > 10
  //                && nextNode.getXCoord() - n.getXCoord() > -5
  //                && nextNode.getXCoord() - n.getXCoord() < 5) {
  //              instructions.add("Head straight and turn left \n");
  //              // instructions.add("-------------------- \n");
  //            } else if (nextNode.getYCoord() - n.getYCoord() < -10
  //                && nextNode.getXCoord() - n.getXCoord() > -5
  //                && nextNode.getXCoord() - n.getXCoord() < 5) {
  //              instructions.add("Head straight and turn right \n");
  //              // instructions.add("-------------------- \n");
  //            }
  //          } else if (previousNode.getXCoord() - n.getXCoord() < -10
  //              && previousNode.getYCoord() - n.getYCoord() > -5
  //              && previousNode.getYCoord() - n.getYCoord() < 5) {
  //            if (nextNode.getYCoord() - n.getYCoord() > 10
  //                && nextNode.getXCoord() - n.getXCoord() > -5
  //                && nextNode.getXCoord() - n.getXCoord() < 5) {
  //              instructions.add("Head straight and turn right \n");
  //              // instructions.add("-------------------- \n");
  //            } else if (nextNode.getYCoord() - n.getYCoord() < -10
  //                && nextNode.getXCoord() - n.getXCoord() > -5
  //                && nextNode.getXCoord() - n.getXCoord() < 5) {
  //              instructions.add("Head straight and turn left \n");
  //              // instructions.add("-------------------- \n");
  //            }
  //          } else {
  //            if (previousNode.getXCoord() - n.getXCoord() > -3
  //                && previousNode.getXCoord() - n.getXCoord() < 3) {
  //              if (nextNode.getYCoord() > previousNode.getYCoord()) {
  //                instructions.add("Head left \n");
  //                // instructions.add("-------------------- \n");
  //              } else if (nextNode.getYCoord() < previousNode.getYCoord()) {
  //                instructions.add("Head right \n");
  //                // instructions.add("-------------------- \n");
  //              }
  //            } else if (previousNode.getXCoord() > n.getXCoord()) {
  //              if (nextNode.getYCoord() > previousNode.getYCoord()) {
  //                instructions.add("Head left \n");
  //                // instructions.add("-------------------- \n");
  //              } else if (nextNode.getYCoord() < previousNode.getYCoord()) {
  //                instructions.add("Head right \n");
  //                // instructions.add("-------------------- \n");
  //              }
  //            } else if (previousNode.getXCoord() < n.getXCoord()) {
  //              if (nextNode.getYCoord() > previousNode.getYCoord()) {
  //                instructions.add("Head right \n");
  //                // instructions.add("-------------------- \n");
  //              } else if (nextNode.getYCoord() < previousNode.getYCoord()) {
  //                instructions.add("Head left \n");
  //                // instructions.add("-------------------- \n");
  //              }
  //            }
  //          }
  //        } else if (previousNode.getFieldAsString("nodetype").equals("HALL")
  //            && nextNode.getFieldAsString("nodetype").equals("HALL")) {
  //          if ((previousNode.getXCoord() - nextNode.getXCoord() > -3
  //                  && previousNode.getXCoord() - nextNode.getXCoord() < 5)
  //              || (previousNode.getYCoord() - nextNode.getYCoord() > -3
  //                  && previousNode.getYCoord() - nextNode.getYCoord() < 5)) {
  //            hallCount++;
  //            if (hallCount % 6 == 0) {
  //              instructions.add("Keep going further \n");
  //              // instructions.add("-------------------- \n");
  //              hallCount = 1;
  //            } else if (hallCount % 4 == 0) {
  //              instructions.add("Keep walking down the hallway \n");
  //              // instructions.add("-------------------- \n");
  //            } else if (hallCount % 2 == 0) {
  //              instructions.add("Walk straight down the hallway \n");
  //              // instructions.add("-------------------- \n");
  //            }
  //          } else if (previousNode.getXCoord() - n.getXCoord() > -3
  //              && previousNode.getXCoord() - n.getXCoord() < 5
  //              && n.getYCoord() - previousNode.getYCoord() > 10) {
  //            if (nextNode.getXCoord() > n.getXCoord()) {
  //              instructions.add("Turn left \n");
  //              // instructions.add("-------------------- \n");
  //              hallCount = 1;
  //            } else if (nextNode.getXCoord() < n.getXCoord()) {
  //              instructions.add("Turn right \n");
  //              // instructions.add("-------------------- \n");
  //              hallCount = 1;
  //            }
  //          } else if (previousNode.getXCoord() - n.getXCoord() > -3
  //              && previousNode.getXCoord() - n.getXCoord() < 5
  //              && n.getYCoord() - previousNode.getYCoord() < -10) {
  //            if (nextNode.getXCoord() > n.getXCoord()) {
  //              instructions.add("Turn right \n");
  //              // instructions.add("-------------------- \n");
  //              hallCount = 1;
  //            } else if (nextNode.getXCoord() < n.getXCoord()) {
  //              instructions.add("Turn left \n");
  //              // instructions.add("-------------------- \n");
  //              hallCount = 1;
  //            }
  //          } else if (previousNode.getYCoord() - n.getYCoord() > -3
  //              && previousNode.getYCoord() - n.getYCoord() < 5
  //              && n.getXCoord() > previousNode.getXCoord()) {
  //            if (nextNode.getXCoord() - n.getXCoord() > -3
  //                && nextNode.getXCoord() - n.getXCoord() < 5) {
  //              if (nextNode.getYCoord() < n.getYCoord()) {
  //                instructions.add("Turn left \n");
  //                // instructions.add("-------------------- \n");
  //                hallCount = 1;
  //              } else if (nextNode.getYCoord() > n.getYCoord()) {
  //                instructions.add("Turn right \n");
  //                // instructions.add("-------------------- \n");
  //                hallCount = 1;
  //              }
  //            }
  //          } else if (previousNode.getYCoord() - n.getYCoord() > -3
  //              && previousNode.getYCoord() - n.getYCoord() < 5
  //              && n.getXCoord() < previousNode.getXCoord()) {
  //            if (nextNode.getXCoord() - n.getXCoord() > -3
  //                && nextNode.getXCoord() - n.getXCoord() < 5) {
  //              if (nextNode.getYCoord() < n.getYCoord()) {
  //                instructions.add("Turn right \n");
  //                // instructions.add("-------------------- \n");
  //                hallCount = 1;
  //              } else if (nextNode.getYCoord() > n.getYCoord()) {
  //                instructions.add("Turn left \n");
  //                // instructions.add("-------------------- \n");
  //                hallCount = 1;
  //              }
  //            }
  //          }
  //        } else if (nextNode == toNode) {
  //          if ((nextNode.getYCoord() - n.getYCoord() > -10
  //                  && nextNode.getYCoord() - n.getYCoord() < 10)
  //              || (nextNode.getXCoord() - n.getXCoord() > -10
  //                  && nextNode.getXCoord() - n.getXCoord() < 10)) {
  //            instructions.add(
  //                "You should now be right next to the "
  //                    + toNode.toString()
  //                    + ", you've arrived! \n");
  //          }
  //        }
  //      }
  //    }
  //  }

  @FXML
  private void handleParkingButton(ActionEvent e) {
    checkParking();
    parkingPopup.setVisible(!parkingPopup.isVisible());
  }

  @FXML
  private void handleHistoryAction(ActionEvent e) {
    checkHistory();
    if (historyPane.isDisabled()) {
      historyPane.setVisible(true);
      historyPane.setDisable(false);
      parkingPopup.setVisible(false);
    } else {
      historyPane.setVisible(false);
      historyPane.setDisable(true);
    }
  }

  @FXML
  private void handleNext() {
    current_path++;
    floorValue++;
    createInstructions(value.get(floorValue));
    switchFloors(
        "G1".contains(path.get(indices.get(current_path)).getFieldAsString("floor"))
            ? "1"
            : path.get(indices.get(current_path)).getFieldAsString("floor"));

    Path current = lines.get(current_path);

    overlayPaneTwo.getChildren().removeIf(x -> x instanceof Path);
    directions.setVisible(true);

    Path layer = linesSolid.get(current_path);
    layer.setStroke(Color.valueOf("#002D59"));
    layer.setStrokeWidth(6);
    overlayPaneTwo.getChildren().add(0, layer);

    current.setStroke(Color.valueOf("#f6bd38"));
    current.setStrokeWidth(4);
    overlayPaneTwo.getChildren().add(current);

    //    Line layer = new Line();
    //    layer.setStroke(Color.valueOf("#002D59"));
    //    layer.setStrokeWidth(2);
    //    overlayPaneTwo.getChildren().add(layer);

    // movingCircle(lines.get(current_path));
    movingDashes(current);

    // directions.setText(textPaths.get(current_path).toString());

    // If we have rendered the last one
    if (current_path == lines.size() - 1) {
      originInput.setDisable(false);
      destinationInput.setDisable(false);
      btnBack.setDisable(false);
      btnNext.setDisable(true);
    } else {
      btnBack.setDisable(false);
      btnNext.setDisable(false);
    }

    zoomToPath(current);
  }

  @FXML
  public void handleBack(ActionEvent actionEvent) {
    current_path--;
    floorValue--;
    createInstructions(value.get(floorValue));
    switchFloors(
        "G1".contains(path.get(indices.get(current_path)).getFieldAsString("floor"))
            ? "1"
            : path.get(indices.get(current_path)).getFieldAsString("floor"));

    Path current = lines.get(current_path);

    overlayPaneTwo.getChildren().removeIf(x -> x instanceof Path);
    directions.setVisible(true);

    Path layer = linesSolid.get(current_path);
    layer.setStroke(Color.valueOf("#002D59"));
    layer.setStrokeWidth(6);
    overlayPaneTwo.getChildren().add(0, layer);

    current.setStroke(Color.valueOf("#f6bd38"));
    current.setStrokeWidth(4);
    overlayPaneTwo.getChildren().add(current);

    // directions.setText(textPaths.get(current_path).toString());

    // If we have rendered the first one
    if (current_path == 0) {
      btnBack.setDisable(true);
      btnNext.setDisable(false);
    } else if (current_path > 0 && current_path < lines.size() - 1) {
      btnBack.setDisable(false);
      btnNext.setDisable(false);
    } else {
      btnBack.setDisable(false);
      btnNext.setDisable(true);
    }
    movingDashes(current);
    // movingCircle(lines.get(current_path));

    zoomToPath(current);
  }

  @FXML
  private void handleIncreaseSize(MouseEvent e) {
    rescale(SCALE_DELTA);
  }

  @FXML
  private void handleDecreaseSize(MouseEvent e) {
    rescale(1 / SCALE_DELTA);
  }

  private void rescale(double scaleFactor) {
    double newScale = Math.max(minRatio, Math.min(maxRatio, scaleFactor * SCALE_TOTAL));

    if (newScale != SCALE_TOTAL) {
      stackPane.setScaleX(newScale);
      stackPane.setScaleY(newScale);
      SCALE_TOTAL = newScale;

      Bounds viewPort = scrollPane.getViewportBounds();
      Bounds contentSize = group.getBoundsInParent();

      double centerPosX =
          (contentSize.getWidth() - viewPort.getWidth()) * scrollPane.getHvalue()
              + viewPort.getWidth() / 2;
      double centerPosY =
          (contentSize.getHeight() - viewPort.getHeight()) * scrollPane.getVvalue()
              + viewPort.getHeight() / 2;

      double newCenterX = centerPosX * scaleFactor;
      double newCenterY = centerPosY * scaleFactor;

      scrollPane.setHvalue(
          (newCenterX - viewPort.getWidth() / 2) / (contentSize.getWidth() - viewPort.getWidth()));
      scrollPane.setVvalue(
          (newCenterY - viewPort.getHeight() / 2)
              / (contentSize.getHeight() - viewPort.getHeight()));
    }
  }

  @FXML
  public void handleExpand(ActionEvent actionEvent) {
    chevronIcon.setRotate(floorSelector.isExpanded() ? 90 : 0);
  }

  @FXML
  private void expandSearchMobile(ActionEvent actionEvent) {
    // Add the original items and reset the anchor pane
    AnchorPane.setBottomAnchor(searchPane, 0.0);
    AnchorPane.setBottomAnchor(directoryScrollPane, 50.0);
    searchPane.getChildren().clear();
    searchPane.getChildren().addAll(originalChildrenSearchPane);
    searchPane.getChildren().add(searchPathToggle);
    searchPathToggle.setVisible(true);
  }

  @FXML
  private void collapseSearchMobile(ActionEvent actionEvent) {
    // Only have the search bar and make the pane tiny
    AnchorPane.setBottomAnchor(searchPane, 730.0);
    searchPane.getChildren().clear();
    searchPane.getChildren().add(originInput);
  }

  public void toggleTextDirections(ActionEvent event) {
    if (directions.isVisible()) {
      directionsIn();
    } else {
      directionsOut();
    }
  }

  private void directionsIn() {
    //    directions.setVisible(false);
    //    buttonRow2.setStyle(
    //        "-fx-padding: 0 0 0 0;"
    //            + "-fx-border-insets: 0 0 0 0;"
    //            + "-fx-background-insets: 0 0 0 0;");
    //    textDirectionsToggle.setStyle(
    //        "-fx-padding: 0 0 0 0;"
    //            + "-fx-border-insets: 0 0 0 0;"
    //            + "-fx-background-insets: 0 0 0 0;");
  }

  private void directionsOut() {
    //    pathTextArea.setVisible(true);
    //    buttonRow2.setStyle(
    //        "-fx-padding: 0 155 0 0;"
    //            + "-fx-border-insets: 0 155 0 0;"
    //            + "-fx-background-insets: 0 155 0 0;");
    //    textDirectionsToggle.setStyle(
    //        "-fx-padding: 0 150 0 200;"
    //            + "-fx-border-insets: 0 150 0 200;"
    //            + "-fx-background-insets: 0 150 0 200;");
  }

  private void loadDirectory() {

    ObservableList<String> exitNodes = FXCollections.observableArrayList();
    ObservableList<String> servNodes = FXCollections.observableArrayList();
    ObservableList<String> restNodes = FXCollections.observableArrayList();
    ObservableList<String> labNodes = FXCollections.observableArrayList();
    ObservableList<String> deptNodes = FXCollections.observableArrayList();
    ObservableList<String> confNodes = FXCollections.observableArrayList();

    db.getItemsNodes()
        .forEach(
            Node -> {
              if (Node.getFieldAsString("nodetype").equals("EXIT")) {
                exitNodes.add(Node.getFieldAsString("longname"));
              }
              if (Node.getFieldAsString("nodetype").equals("SERV")
                  || Node.getFieldAsString("nodetype").equals("KIOSK")
                  || Node.getFieldAsString("nodetype").equals("FOOD")
                  || Node.getFieldAsString("nodetype").equals("RETL")) {
                servNodes.add(Node.getFieldAsString("longname"));
              }
              if (Node.getFieldAsString("nodetype").equals("REST")) {
                restNodes.add(Node.getFieldAsString("longname"));
              }
              if (Node.getFieldAsString("nodetype").equals("LAB")) {
                labNodes.add(Node.getFieldAsString("longname"));
              }
              if (Node.getFieldAsString("nodetype").equals("PARK")) {
                parkingSpots.add(Node.getFieldAsString("longname"));
              }
              if (Node.getFieldAsString("nodetype").equals("DEPT")) {
                deptNodes.add(Node.getFieldAsString("longname"));
              }
              if (Node.getFieldAsString("nodetype").equals("CONF")) {
                confNodes.add(Node.getFieldAsString("longname"));
              }
            });

    // make megalist of buttons (W/ ID as nodetype)
    // filteredlist of items with pred set to pred of megalist + individual Pred?
    //

    ScrollPane parkingScrollin = new ScrollPane();
    // parkingScrollin.setMaxHeight(400);
    VBox parkingDirContent = new VBox();
    parkingDirContent.setFillWidth(true);
    parkingDirContent.setStyle("-fx-background-color: #ffffff");
    parkingScrollin.setStyle("-fx-background-color: #ffffff");
    parkingDirContent.setPrefWidth(200);

    ScrollPane exitScrollin = new ScrollPane();
    // exitScrollin.setMaxHeight(400);
    VBox exitDirContent = new VBox();
    exitDirContent.setFillWidth(true);
    exitDirContent.setStyle("-fx-background-color: #ffffff");
    exitScrollin.setStyle("-fx-background-color: #ffffff");
    exitDirContent.setPrefWidth(200);

    ScrollPane restScrollin = new ScrollPane();
    // restScrollin.setMaxHeight(400);
    VBox restDirContent = new VBox();
    restDirContent.setFillWidth(true);
    restDirContent.setStyle("-fx-background-color: #ffffff");
    restScrollin.setStyle("-fx-background-color: #ffffff");
    restDirContent.setPrefWidth(200);

    ScrollPane servScrollin = new ScrollPane();
    // servScrollin.setMaxHeight(400);
    VBox servDirContent = new VBox();
    servDirContent.setFillWidth(true);
    servDirContent.setStyle("-fx-background-color: #ffffff");
    servScrollin.setStyle("-fx-background-color: #ffffff");
    servDirContent.setPrefWidth(200);

    ScrollPane confScrollin = new ScrollPane();
    // confScrollin.setMaxHeight(400);
    VBox confDirContent = new VBox();
    confDirContent.setFillWidth(true);
    confDirContent.setStyle("-fx-background-color: #ffffff");
    confScrollin.setStyle("-fx-background-color: #ffffff");
    confDirContent.setPrefWidth(200);

    ScrollPane labScrollin = new ScrollPane();
    // labScrollin.setMaxHeight(400);
    VBox labDirContent = new VBox();
    labDirContent.setFillWidth(true);
    labDirContent.setStyle("-fx-background-color: #ffffff");
    labScrollin.setStyle("-fx-background-color: #ffffff");
    labDirContent.setPrefWidth(200);

    ScrollPane deptScrollin = new ScrollPane();
    // deptScrollin.setMaxHeight(400);
    VBox deptDirContent = new VBox();
    deptDirContent.setFillWidth(true);
    deptDirContent.setStyle("-fx-background-color: #ffffff");
    deptScrollin.setStyle("-fx-background-color: #ffffff");
    deptDirContent.setPrefWidth(200);

    ObservableList<JFXButton> allBtns = FXCollections.observableArrayList();
    filteredBtns = new FilteredList<>(allBtns);

    Platform.runLater(
        () -> {
          setPlaceActions(allBtns, "p", parkingSpots, parkingDirContent);
          setPlaceActions(allBtns, "e", exitNodes, exitDirContent);
          setPlaceActions(allBtns, "r", restNodes, restDirContent);
          setPlaceActions(allBtns, "s", servNodes, servDirContent);
          setPlaceActions(allBtns, "c", confNodes, confDirContent);
          setPlaceActions(allBtns, "l", labNodes, labDirContent);
          setPlaceActions(allBtns, "d", deptNodes, deptDirContent);
        });

    parkingScrollin.setContent(parkingDirContent);
    exitScrollin.setContent(exitDirContent);
    restScrollin.setContent(restDirContent);
    servScrollin.setContent(servDirContent);
    confScrollin.setContent(confDirContent);
    labScrollin.setContent(labDirContent);
    deptScrollin.setContent(deptDirContent);

    parkingDir.setContent(parkingScrollin);
    entranceDir.setContent(exitScrollin);
    restroomsDir.setContent(restScrollin);
    servicesDir.setContent(servScrollin);
    conferenceDir.setContent(confScrollin);
    labsDir.setContent(labScrollin);
    departmentsDir.setContent(deptScrollin);
  }

  private void setPlaceActions(
      ObservableList<JFXButton> allBtns, String id, ObservableList<String> places, VBox content) {
    for (int i = 0; i < places.size(); i++) {
      JFXButton place = new JFXButton();
      place.setText(places.get(i));
      place.setId(id);
      place.setOnAction(
          new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
              clearColor();
              if (selectingFromNode) {
                originInput.setId("LOCAL");
                originInput.setText(place.getText());
                originInput.setUserData(place.getText());
                addPinAtNode(place.getText(), "startNode");
                checkFilledAndRoute();
                selectingFromNode = false;
              } else {
                destinationInput.setText(place.getText());
                destinationInput.setUserData(place.getText());
                addPinAtNode(place.getText(), "endNode");
                checkFilledAndRoute();
              }
              place.setStyle("-fx-background-color: #6db4d6");
              selectedDirectory = place;
            }
          });
      allBtns.add(place);
    }
    filteredBtns.addListener(
        (ListChangeListener<JFXButton>)
            c -> {
              content.getChildren().setAll(filteredBtns.filtered(n -> n.getId().equals(id)));
              Node n = content.getParent().getParent().getParent().getParent().getParent();
              if (content.getChildren().size() == 0) {
                n.setVisible(false);
                n.setManaged(false);
              } else {
                n.setVisible(true);
                n.setManaged(true);
              }
            });
  }

  private void clearColor() {
    if (selectedDirectory != null) {
      selectedDirectory.setStyle("-fx-background-color: #ffffff");
      // selectedDirectory.setBackground(Background.EMPTY);
      selectedDirectory = null;
    }
  }
}
