package edu.wpi.teamA.views;

import com.google.inject.Inject;
import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import edu.wpi.teamA.modules.*;
import edu.wpi.teamA.services.DatabaseService;
import edu.wpi.teamA.services.PathfindingService;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javax.swing.*;

public class PathfindingEditorController implements Initializable {

  @Inject PathfindingService pathfindingService;
  @Inject PathfindingController pc;
  @Inject DatabaseService db;

  @FXML private StackPane topStack;
  @FXML private ScrollPane scrollPane;
  @FXML private AnchorPane anchorPane;
  @FXML private StackPane stackPane;
  @FXML private Pane overlayPaneTwo;
  @FXML private Label nodeLongName, nodeCoordinates, nodeFloor, nodeType;
  @FXML private FlowPane hoverInfo, topFlowPane;
  @FXML private ImageView mapImg;
  @FXML private JFXButton saveEdits;
  @FXML private JFXCheckBox editedCheck;
  @FXML private JFXListView<Label> historyList;
  @FXML private ContextMenu listContextMenu;
  @FXML
  private MenuItem addNode,
      addHiddenNode,
      deleteNode,
      undoNodeMove,
      newNodeEdge,
      newXFloorEdge,
      removeNodeEdge,
      alignNodes;

  @FXML private StackPane floorOneStack;
  @FXML private StackPane floorTwoStack;
  @FXML private StackPane floorThreeStack;
  @FXML private StackPane floorFourStack;
  @FXML private StackPane floorFiveStack;
  @FXML private Group group;
  @FXML private JFXButton groundButton;
  @FXML private JFXButton f1Button;
  @FXML private JFXButton f2Button;
  @FXML private JFXButton f3Button;
  @FXML private JFXButton f4Button;
  @FXML private JFXButton f5Button;
  @FXML private JFXNodesList floorSelector;
  @FXML private MaterialDesignIconView chevronIcon;
  @FXML private JFXButton btnAlgo;
  private final double SCALE_DELTA = 1.1;
  private double SCALE_TOTAL = SCALE_DELTA;

  private Double currentXValue;
  private Double currentYValue;
  private SimpleDoubleProperty hoveredNodeX, hoveredNodeY;
  private AnchorNode hoveredNode, selectedNode;
  private Insets insets;
  private ObservableList<Edit<HospitalNode>> edits;
  private Line hoveredEdge, selectedEdge;
  private int lastRClickX, lastRClickY;
  private double pressedX, pressedY;
  private String currentFloor = "G";
  private HashMap<HospitalNode, AnchorNode> map;
  private HashMap<String, JFXButton> buttons;
  private HashMap<String, Node> panes;

  private final String[] algorithms = {"AStar", "BFS", "DFS", "DJSTR"};
  private int algo = 0;

  private ArrayList<AnchorNode> selected;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    floorOneStack.setCache(true);
    floorOneStack.setCacheHint(CacheHint.QUALITY);
    floorTwoStack.setCache(true);
    floorTwoStack.setCacheHint(CacheHint.SPEED);
    floorThreeStack.setCache(true);
    floorThreeStack.setCacheHint(CacheHint.SPEED);
    floorFourStack.setCache(true);
    floorFourStack.setCacheHint(CacheHint.SPEED);
    floorFiveStack.setCache(true);
    floorFiveStack.setCacheHint(CacheHint.SPEED);

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

    mapImg.setOpacity(0.7);
    floorOneStack.setOpacity(1.0);
    floorSelector.setRotate(-90);
    groundButton.setStyle(
        "-fx-background-color: #f6bd38; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #ffffff;");

    currentXValue = stackPane.getScaleX();
    currentYValue = stackPane.getScaleY();
    hoveredNodeX = new SimpleDoubleProperty();
    hoveredNodeY = new SimpleDoubleProperty();
    edits = FXCollections.observableArrayList();
    map = new HashMap<>();
    insets = new Insets(0, topFlowPane.getWidth() - 415, 0, 0);

    selected = new ArrayList<>();

    mapImg.setPreserveRatio(true);
    scrollPane.setPannable(false);
    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    //    scrollPane.setOnMousePressed(
    //        new EventHandler<MouseEvent>() {
    //          public void handle(MouseEvent event) {
    //            pressedX = event.getX();
    //            pressedY = event.getY();
    //          }
    //        });

    stackPane.setOnScroll(
        (ScrollEvent event) -> {
          event.consume();
          double scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA : 1 / SCALE_DELTA;
          rescale(scaleFactor);
        });

    stackPane.setOnMousePressed(
        e -> {
          scrollPane.setPannable(e.isShiftDown());

          if (!e.isShiftDown()) {
            selected.forEach(n -> n.setSelected(false));
            selected.clear();
          }

          if (e.getButton().equals(MouseButton.SECONDARY)) {
            System.out.println("Secondary Click");
            lastRClickX = (int) e.getX();
            lastRClickY = (int) e.getY();

            deleteNode.setVisible(false);
            addNode.setVisible(true);
            addHiddenNode.setVisible(true);
            newNodeEdge.setVisible(false);
            newXFloorEdge.setVisible(false);
            removeNodeEdge.setVisible(false);
            undoNodeMove.setVisible(false);
            alignNodes.setVisible(false);
          }
        });

    stackPane.setOnMouseReleased(e -> scrollPane.setPannable(false));

    final KeyCombination keyComb1 = new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN);
    scrollPane
        .getParent()
        .addEventHandler(
            KeyEvent.KEY_RELEASED,
            event -> {
              if (keyComb1.match(event)) {
                if (edits.size() > 0) {
                  Edit lastEdit = edits.get(0);
                  handleUndoEdit(lastEdit);
                }
              }
            });

    refreshNodes();

    hoveredNodeX.addListener(
        x ->
            nodeCoordinates.setText(
                "X: "
                    + (int) hoveredNodeX.get()
                    + " |"
                    + nodeCoordinates.getText().split("\\|")[1]));
    hoveredNodeY.addListener(
        y ->
            nodeCoordinates.setText(
                nodeCoordinates.getText().split("\\|")[0] + "| Y: " + (int) hoveredNodeY.get()));

    edits.addListener(
        new ListChangeListener<Edit<HospitalNode>>() {
          @Override
          public void onChanged(Change<? extends Edit<HospitalNode>> c) {
            saveEdits.setDisable(edits.size() == 0);
            floorSelector.setDisable(edits.size() != 0);
          }
        });

    anchorPane
        .visibleProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue) refreshNodes();
            });

    historyList.setCellFactory(
        lv -> {
          JFXListCell<Label> cell =
              new JFXListCell<Label>() {
                @Override
                protected void updateItem(Label item, boolean empty) {
                  super.updateItem(item, empty);

                  if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                  } else {
                    this.setUserData(item.getUserData());
                    this.getContextMenu().setUserData(item.getUserData());
                  }
                }
              };

          MenuItem undoCurrent = new MenuItem("Undo Edit");
          undoCurrent.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.UNDO));
          undoCurrent.setOnAction(this::handleUndoListEdit);
          ContextMenu editMenu = new ContextMenu(undoCurrent);
          cell.setContextMenu(editMenu);
          return cell;
        });
  }

  private void refreshNodes() {
    overlayPaneTwo.getChildren().clear();
    pathfindingService.updateGraph();
    Graph<HospitalNode> graph = pathfindingService.getGraph();
    System.out.println("CURRENT FLOOR: " + currentFloor);
    Set<HospitalNode> nodeList =
        graph.getNodes().stream()
            .filter(n -> n.getFieldAsString("floor").equals(currentFloor))
            .collect(Collectors.toSet());

    double xScale = mapImg.getBoundsInLocal().getWidth() / 2989;
    double yScale = mapImg.getBoundsInLocal().getHeight() / 2457;

    // get all connections, recursively call until no more exist,

    nodeList.forEach(node -> addAnchorNode(node, xScale, yScale));
    HashSet<List<HospitalNode>> edges = new HashSet<>();
    nodeList.forEach(
        node ->
            graph
                .getConnections(node)
                .forEach(
                    e -> {
                      List<HospitalNode> edge = Arrays.asList(node, e);
                      if (!edges.contains(Arrays.asList(e, node))) {
                        addEdge(node, e, xScale, yScale);
                        edges.add(edge);
                      }
                    }));
  }

  @FXML
  private void handleSaveButton() {
    edits.sort((e1, e2) -> !e2.isEdge() && e2.getBefore() == null ? 1 : 0);
    edits.forEach(this::saveEditHelper);
    edits.clear();
    historyList.getItems().clear();
    hoveredNodeX = new SimpleDoubleProperty();
    hoveredNodeY = new SimpleDoubleProperty();
    hoveredNode = null;
    selectedNode = null;
    selectedEdge = null;
    hoveredEdge = null;
    refreshNodes();
    pc.clearCustomNodes();
    pc.displayCustomNodes(null);
  }

  private void saveEditHelper(Edit edit) {
    System.out.println("EDIT SAVE");
    if (!edit.isEdge()) {
      if (edit.getAfter() == null) {
        db.deleteRecord("NODES", ((HospitalNode) edit.getBefore()));
        edits.removeIf(
            e -> {
              if (e.getBefore() != null) {
                return edit.getBefore().getId().equals(e.getBefore().getId());
              } else if (e.getAfter() != null) {
                return edit.getBefore().getId().equals(e.getAfter().getId());
              }
              return false;
            });
        historyList
            .getItems()
            .removeIf(
                h -> {
                  Label l = h;
                  return l.getText().contains(edit.getBefore().toString())
                      || l.getTooltip().getText().contains(edit.getBefore().toString());
                });
      } else if (edit.getBefore() == null) {
        System.out.println("ADDING NODE");
        db.addRecord("NODES", ((HospitalNode) edit.getAfter()));
      } else {
        db.updateRecord(
            "NODES",
            ((HospitalNode) edit.getBefore()),
            "xcoord",
            String.valueOf(edit.getAfter().getXCoord()));
        db.updateRecord(
            "NODES",
            ((HospitalNode) edit.getBefore()),
            "ycoord",
            String.valueOf(edit.getAfter().getYCoord()));
      }

    } else {
      HospitalNode fromNode = (HospitalNode) edit.getBefore();
      HospitalNode toNode = (HospitalNode) edit.getAfter();
      if (edit.isDelete()) {
        HashMap<String, String> data = new HashMap<>();
        data.put("id", fromNode.getId() + "_" + toNode.getId());
        db.deleteRecord("EDGES", new Record(data));
        data.replace("id", toNode.getId() + "_" + fromNode.getId());
        db.deleteRecord("EDGES", new Record(data));
      } else {
        System.out.println("Adding line");
        HashMap<String, String> data = new HashMap<>();
        data.put("id", fromNode.getId() + "_" + toNode.getId());
        data.put("startnode", fromNode.getId());
        data.put("endnode", toNode.getId());
        db.addRecord("EDGES", new Record(data));
      }
    }
  }

  private AnchorNode addAnchorNode(HospitalNode node, double xScale, double yScale) {
    System.out.println("Elem at: (" + node.getXCoord() + ", " + node.getYCoord() + ")");
    SimpleDoubleProperty x = new SimpleDoubleProperty(node.getXCoord() * xScale);
    SimpleDoubleProperty y = new SimpleDoubleProperty(node.getYCoord() * yScale);

    AnchorNode anchorNode =
        new AnchorNode(
            "STAIHALLELEVWALKHIDN".contains(node.getFieldAsString("nodetype"))
                ? Color.MEDIUMAQUAMARINE
                : Color.VIOLET,
            x,
            y,
            node,
            historyList,
            edits,
            selected);

    anchorNode
        .hoverProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue) {
                nodeLongName.setText(node.toString());
                nodeCoordinates.setText(
                    "X: " + (int) anchorNode.getX() + " | Y:" + (int) anchorNode.getY());
                nodeFloor.setText("Floor: " + node.getFieldAsString("floor"));
                nodeType.setText("Type: " + node.getFieldAsString("nodetype"));
                hoveredNodeX.bind(anchorNode.getXProperty());
                hoveredNodeY.bind(anchorNode.getYProperty());
                editedCheck.selectedProperty().bind(anchorNode.isEdited());
                hoverInfo.setVisible(true);
                hoveredNode = anchorNode;

                if (!anchorNode.getSelected())
                  anchorNode.setEffect(
                      new DropShadow(BlurType.TWO_PASS_BOX, Color.TURQUOISE, 2, 10, 0, 0));

              } else if (!anchorNode.beingDragged()) {
                hoverInfo.setVisible(false);
                hoveredNode = null;
                if (!anchorNode.getSelected()) anchorNode.setEffect(null);
              }
            });

    anchorNode.setOnMousePressed(
        e -> {
          selectedNode = anchorNode;

          if (e.isShortcutDown()) {
            anchorNode.setSelected(true);
            selected.add(anchorNode);
          }

          if (e.isSecondaryButtonDown()) {
            System.out.println("Node that invoked this menu: " + anchorNode.getNode().toString());
            alignNodes.setVisible(selected.size() > 1);
            deleteNode.setVisible(true);
            addNode.setVisible(false);
            addHiddenNode.setVisible(false);
            newNodeEdge.setVisible(true);
            newXFloorEdge.setVisible(true);
            removeNodeEdge.setVisible(false);
            undoNodeMove.setVisible(anchorNode.isEdited().getValue());
          }

          e.consume();
        });

    overlayPaneTwo.getChildren().add(anchorNode);
    map.put(node, anchorNode);

    if ("STAIELEVREST".contains(node.getType())) {
      ImageView nv =
          new ImageView(
              new Image(
                  getClass()
                      .getResource(
                          "/edu/wpi/teamA/assets/img/Map_Icons/"
                              + (node.getType().equals("REST")
                                  ? "Restroom"
                                  : node.getType().equals("ELEV") ? "Elevator" : "Stairs")
                              + "Icon.png")
                      .toExternalForm()));
      nv.xProperty().bind(anchorNode.getXProperty().subtract(5));
      nv.yProperty().bind(anchorNode.getYProperty().subtract(5));
      nv.setFitWidth(10);
      nv.visibleProperty().bind(anchorNode.visibleProperty());

      nv.setPreserveRatio(true);
      nv.setMouseTransparent(true);
      nv.setId(node.getId());
      overlayPaneTwo.getChildren().add(nv);
    }

    return anchorNode;
  }

  private void addEdge(HospitalNode fromNode, HospitalNode toNode, double xScale, double yScale) {
    boolean xFloor = !fromNode.getFieldAsString("floor").equals(toNode.getFieldAsString("floor"));
    AnchorNode fromAnchor;
    if (xFloor && fromNode.getFieldAsString("floor").equals(currentFloor)) {
      HospitalNode _tmp = toNode;
      toNode = fromNode;
      fromNode = _tmp;
    }
    if (xFloor) {
      SimpleDoubleProperty fromX = new SimpleDoubleProperty(fromNode.getXCoord() * xScale);
      SimpleDoubleProperty fromY = new SimpleDoubleProperty(fromNode.getYCoord() * yScale);
      fromAnchor = new AnchorNode(fromX, fromY, fromNode);
      overlayPaneTwo.getChildren().add(fromAnchor);
    } else fromAnchor = map.get(fromNode);
    AnchorNode toAnchor = map.get(toNode);

    Line line = new Line(fromAnchor.getX(), fromAnchor.getY(), toAnchor.getX(), toAnchor.getY());
    line.startXProperty().bind(fromAnchor.getXProperty());
    line.startYProperty().bind(fromAnchor.getYProperty());
    line.endXProperty().bind(toAnchor.getXProperty());
    line.endYProperty().bind(toAnchor.getYProperty());
    if (xFloor) {
      line.setStroke(Color.GOLD);
      line.getStrokeDashArray().addAll(7d, 4d);
    } else line.setStroke(Color.BLACK);
    line.setStrokeWidth(2);

    line.hoverProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue) {
                hoveredEdge = line;
                line.setEffect(new DropShadow(BlurType.TWO_PASS_BOX, Color.TURQUOISE, 2, 10, 0, 0));
              } else {
                hoveredEdge = null;
                line.setEffect(null);
              }
            });

    line.setOnMousePressed(
        e -> {
          selectedEdge = line;
          if (e.isSecondaryButtonDown()) {
            System.out.println("Delete this line");
            deleteNode.setVisible(false);
            addNode.setVisible(false);
            addHiddenNode.setVisible(false);
            newNodeEdge.setVisible(false);
            newXFloorEdge.setVisible(false);
            removeNodeEdge.setVisible(true);
            undoNodeMove.setVisible(false);
            alignNodes.setVisible(false);
            e.consume();
          }
        });

    overlayPaneTwo.getChildren().add(0, line);
  }

  //  private AnchorNode addGraphElement(
  //      HospitalNode node,
  //      final HospitalNode startNode,
  //      HashMap<HospitalNode, AnchorNode> map,
  //      double xScale,
  //      double yScale,
  //      boolean addEdges) {
  //
  //
  //
  //    if (map == null) {
  //      map = new HashMap<>();
  //    }
  //
  //    System.out.println("Elem at: (" + node.getXCoord() + ", " + node.getYCoord() + ")");
  //    SimpleDoubleProperty x = new SimpleDoubleProperty(node.getXCoord() * xScale);
  //    SimpleDoubleProperty y = new SimpleDoubleProperty(node.getYCoord() * yScale);
  //
  //    if (addEdges) {
  //      Set<HospitalNode> edgeConnections = pathfindingService.getGraph().getConnections(node);
  //
  //      HashMap<HospitalNode, AnchorNode> finalMap = map;
  //      edgeConnections.forEach(
  //          edgeNode -> {
  //            if (edgeNode == startNode) return;
  //            AnchorNode anchorNode;
  //            if (finalMap.containsKey(edgeNode)) {
  //              anchorNode = finalMap.get(edgeNode);
  //            } else {
  //              anchorNode = addGraphElement(edgeNode, startNode, finalMap, xScale, yScale, true);
  //            }
  //            if (anchorNode != null) {
  //              Line line =
  //                  new Line(x.doubleValue(), y.doubleValue(), anchorNode.getX(),
  // anchorNode.getY());
  //              line.startXProperty().bind(x);
  //              line.startYProperty().bind(y);
  //              line.endXProperty().bind(anchorNode.getXProperty());
  //              line.endYProperty().bind(anchorNode.getYProperty());
  //              line.setStroke(Color.BLACK);
  //              line.setStrokeWidth(2);
  //              overlayPaneTwo.getChildren().add(0, line);
  //              line.hoverProperty()
  //                  .addListener(
  //                      (observable, oldValue, newValue) -> {
  //                        if (newValue) {
  //                          hoveredEdge = line;
  //                          removeNodeEdge.setVisible(true);
  //                          addNode.setVisible(false);
  //                          line.setEffect(
  //                              new DropShadow(BlurType.TWO_PASS_BOX, Color.TURQUOISE, 2, 10, 0,
  // 0));
  //                        } else {
  //                          hoveredEdge = null;
  //                          removeNodeEdge.setVisible(false);
  //                          addNode.setVisible(true);
  //                          line.setEffect(null);
  //                        }
  //                      });
  //            }
  //          });
  //    }
  //
  //    AnchorNode anchorNode =
  //        new AnchorNode(Color.VIOLET, x, y, node, historyList, edits, listContextMenu);
  //    overlayPaneTwo.getChildren().add(anchorNode);
  //    map.put(node, anchorNode);
  //
  //    anchorNode
  //        .hoverProperty()
  //        .addListener(
  //            (observable, oldValue, newValue) -> {
  //              if (newValue) {
  //                nodeLongName.setText(node.toString());
  //                nodeCoordinates.setText(
  //                    "X: " + (int) anchorNode.getX() + " | Y:" + (int) anchorNode.getY());
  //                nodeFloor.setText("Floor: " + node.getFieldAsString("floor"));
  //                nodeType.setText("Type: " + node.getFieldAsString("nodetype"));
  //                hoveredNodeX.bind(anchorNode.getXProperty());
  //                hoveredNodeY.bind(anchorNode.getYProperty());
  //                editedCheck.selectedProperty().bind(anchorNode.isEdited());
  //                hoverInfo.setVisible(true);
  //                deleteNode.setVisible(true);
  //                addNode.setVisible(false);
  //                newNodeEdge.setVisible(true);
  //                if (anchorNode.isEdited().get()) undoNodeMove.setVisible(true);
  //                hoveredNode = anchorNode;
  //              } else if (!anchorNode.beingDragged()) {
  //                addNode.setVisible(true);
  //                hoverInfo.setVisible(false);
  //                deleteNode.setVisible(false);
  //                undoNodeMove.setVisible(false);
  //                newNodeEdge.setVisible(false);
  //                hoveredNode = null;
  //              }
  //            });
  //
  //    return anchorNode;
  //  }

  @FXML
  private void handleUndoNodeMove(ActionEvent e) {
    selectedNode.undoMove();
  }

  @FXML
  private void handleUndoListEdit(ActionEvent e) {
    if (e.getSource() == null) System.out.println("OOF!!!");
    else System.out.println(e.getSource().toString());

    Edit edit = (Edit) ((MenuItem) e.getSource()).getParentPopup().getUserData();
    System.out.println("Is this edit an edge? :" + edit.isEdge());
    handleUndoEdit(edit);
  }

  private void handleUndoEdit(Edit edit) {
    if (edit == null) return;
    if (!edit.isEdge()) {
      AnchorNode anchorNode = (AnchorNode) edit.getObject();
      if (edit.getBefore() == null) {
        nodeDeleteHelper(anchorNode);
      } else if (edit.getAfter() == null) {
        // deleted node
      } else anchorNode.undoMove();
    } else {
      Line line = (Line) edit.getObject();
      if (edit.isDelete()) {
        overlayPaneTwo.getChildren().add(0, line);
      } else {
        deleteEdge(line, false);
      }
    }
    edits.remove(edit);
    historyList.getItems().removeIf(i -> i.getId().equals(String.valueOf(edit.hashCode())));
  }

  //
  @FXML
  private void handleAddNode(ActionEvent e) {
    addNodeHelper(false);
  }

  @FXML
  private void handleAddHiddenNode(ActionEvent e) {
    addNodeHelper(true);
  }

  private void addNodeHelper(boolean isHidden) {
    JFXDialogLayout layout = new JFXDialogLayout();
    Label head = new Label("Add New Node");
    head.setId("heading");
    layout.setHeading(head);
    layout.setAlignment(Pos.CENTER);

    GridPane gridPane = new GridPane();
    gridPane.setAlignment(Pos.CENTER);

    JFXTextField building = new JFXTextField();
    JFXTextField nodeTypeField = new JFXTextField();
    JFXTextField longName = new JFXTextField();
    JFXTextField shortName = new JFXTextField();
    JFXTextField team = new JFXTextField();

    if (isHidden) {
      gridPane.add(new Label("HIDN"), 1, 3);
    } else {
      nodeTypeField.setTextFormatter(
          new TextFormatter<>(
              (change) -> {
                change.setText(change.getText().toUpperCase());
                return change;
              }));
      gridPane.add(nodeTypeField, 1, 3);
    }

    gridPane.add(new Label(String.format("(%s,%s)", lastRClickX, lastRClickY)), 1, 0);
    gridPane.add(new Label("Coordinates"), 0, 0);
    gridPane.add(new Label("Floor"), 0, 1);
    gridPane.add(new Label("Building"), 0, 2);

    gridPane.add(new Label(currentFloor), 1, 1);
    gridPane.add(building, 1, 2);
    gridPane.add(longName, 1, 4);
    gridPane.add(shortName, 1, 5);
    gridPane.add(team, 1, 6);

    gridPane.add(new Label("Node Type"), 0, 3);
    gridPane.add(new Label("Long Name"), 0, 4);
    gridPane.add(new Label("Short Name"), 0, 5);
    gridPane.add(new Label("Team"), 0, 6);

    for (int j = 0; j < 2; j++) {
      ColumnConstraints constraints = new ColumnConstraints();
      constraints.setHgrow(Priority.ALWAYS);
      gridPane.getColumnConstraints().add(constraints);
    }

    for (int j = 0; j < 7; j++) {
      RowConstraints rc = new RowConstraints();
      rc.setVgrow(Priority.ALWAYS);
      gridPane.getRowConstraints().add(rc);
    }

    layout.setBody(gridPane);

    JFXDialog addNodeDialog = new JFXDialog(stackPane, layout, JFXDialog.DialogTransition.CENTER);

    JFXButton submitButton = new JFXButton("Submit");
    submitButton.setId("dialogButton");
    submitButton.setButtonType(JFXButton.ButtonType.RAISED);

    submitButton.setOnAction(
        i -> {
          HashMap<String, String> nodeData = new HashMap<>();
          double xScale = mapImg.getBoundsInLocal().getWidth() / 2989;
          double yScale = mapImg.getBoundsInLocal().getHeight() / 2457;
          String type = isHidden ? "HIDN" : nodeTypeField.getText();
          nodeData.put(
              "id",
              team.getText() + type + "0" + currentFloor + "00" + lastRClickX + "00" + lastRClickY);
          nodeData.put("xcoord", String.valueOf((int) (lastRClickX / xScale)));
          nodeData.put("ycoord", String.valueOf((int) (lastRClickY / yScale)));
          nodeData.put("floor", currentFloor);
          nodeData.put("building", building.getText());
          nodeData.put("nodetype", type);
          nodeData.put("longname", longName.getText());
          nodeData.put("shortname", shortName.getText());
          nodeData.put("team", team.getText());
          Record newRecord = new Record(nodeData);
          HospitalNode newNode = new HospitalNode(newRecord);
          AnchorNode anchorNode = addAnchorNode(newNode, xScale, yScale);
          Edit edit = new Edit(null, newNode, anchorNode);
          edits.add(edit);
          Label label =
              new Label(
                  newNode.toString(),
                  new MaterialDesignIconView(MaterialDesignIcon.MAP_MARKER_RADIUS));
          Tooltip tooltip = new Tooltip();
          tooltip.setText(
              String.format(
                  "New Node Placed at: (%s, %s)",
                  (int) (lastRClickX / xScale), (int) (lastRClickY / yScale)));
          // tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_BOTTOM_RIGHT);
          label.setTooltip(tooltip);
          label.setId(anchorNode.getNode().getId());
          label.setUserData(edit);
          historyList.getItems().add(0, label);
          addNodeDialog.close();
        });

    JFXButton cancelButton = new JFXButton("Cancel");
    cancelButton.setId("dialogButton");
    cancelButton.setButtonType(JFXButton.ButtonType.FLAT);
    cancelButton.setOnAction(i -> addNodeDialog.close());
    layout.setActions(cancelButton, submitButton);
    addNodeDialog.setDialogContainer(topStack);
    addNodeDialog.show();
  }

  @FXML
  private void handleDeleteNode() {
    AnchorNode node = selectedNode;
    JFXDialogLayout layout = new JFXDialogLayout();
    layout.setPrefSize(150, 300);
    JFXDialog dialog = new JFXDialog(stackPane, layout, JFXDialog.DialogTransition.CENTER);
    layout.setHeading(new Text("Node Delete Confirmation"));
    JFXTextArea area =
        new JFXTextArea(
            "Are you sure you want to delete this node?\n\nNode Deletion is PERMANENT!");
    area.setEditable(false);
    layout.setBody(area);

    JFXButton submitButton = new JFXButton("Submit");
    submitButton.setButtonType(JFXButton.ButtonType.RAISED);

    submitButton.setOnAction(
        i -> {
          dialog.close();
          nodeDeleteHelper(node);
        });

    JFXButton cancelButton = new JFXButton("Cancel");
    cancelButton.setButtonType(JFXButton.ButtonType.FLAT);
    cancelButton.setOnAction(i -> dialog.close());
    layout.setActions(cancelButton, submitButton);
    dialog.setDialogContainer(topStack);
    dialog.show();
  }

  private void nodeDeleteHelper(AnchorNode node) {
    ArrayList<Line> lines = new ArrayList<>();
    overlayPaneTwo
        .getChildren()
        .forEach(
            n -> {
              if (n instanceof Line) {
                Line l = (Line) n;
                if ((l.getEndX() == node.getX() && l.getEndY() == node.getY())
                    || (l.getStartX() == node.getX() && l.getStartY() == node.getY())) lines.add(l);
              }
            });
    lines.forEach(l -> deleteEdge(l, false));
    overlayPaneTwo
        .getChildren()
        .removeIf(n -> n.getId() != null && n.getId().equals(node.getNode().getId()));
    overlayPaneTwo.getChildren().remove(node);
    saveEditHelper(new Edit(node.getNode(), null, node));
    pathfindingService.updateGraph();
  }

  @FXML
  private void handleDeleteEdge() {
    deleteEdge(selectedEdge, true);
  }

  private void deleteEdge(Line edge, boolean addEdit) {
    AnchorNode fromNode = getANfromCoords(edge.getStartX(), edge.getStartY());
    AnchorNode toNode = getANfromCoords(edge.getEndX(), edge.getEndY());
    System.out.println(
        "removing line object from: "
            + fromNode.getNode().toString()
            + " to: "
            + toNode.getNode().toString());
    overlayPaneTwo.getChildren().remove(edge);
    if (addEdit) {
      Edit edit = new Edit(fromNode.getNode(), toNode.getNode(), edge, true, true);
      Label label =
          new Label(
              fromNode.getNode().toString(),
              new MaterialDesignIconView(MaterialDesignIcon.LINK_VARIANT_OFF));
      Tooltip nodeTooltip = new Tooltip();
      nodeTooltip.setText(
          String.format(
              "Deleted Edge from: %s -> %s ",
              fromNode.getNode().toString(), toNode.getNode().toString()));
      // tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_BOTTOM_RIGHT);
      label.setTooltip(nodeTooltip);
      label.setId(String.valueOf(edit.hashCode()));
      label.setUserData(edit);
      edits.add(0, edit);
      historyList.getItems().add(0, label);
    }
  }

  private AnchorNode getANfromCoords(double x, double y) {
    return (AnchorNode)
        overlayPaneTwo.getChildren().stream()
            .filter(
                n -> {
                  if (n instanceof AnchorNode) {
                    return ((AnchorNode) n).getX() == x && ((AnchorNode) n).getY() == y;
                  }
                  return false;
                })
            .findFirst()
            .orElse(null);
  }

  @FXML
  private void handleAddEdge() {
    AnchorNode startNode = selectedNode;
    addEdgeHelper(startNode, false, "");
  }

  @FXML
  private void handleAddXFloorEdge(ActionEvent e) {
    AnchorNode node = selectedNode;
    JFXDialogLayout layout = new JFXDialogLayout();
    layout.setHeading(new Text("Choose floor to connect this Node to:"));
    GridPane grid = new GridPane();
    grid.add(new Text("Select Floor:"), 0, 0);
    ComboBox<String> combo = new ComboBox<>();
    combo.getItems().addAll("G 1 2 3 4 5".split(" "));
    grid.add(combo, 0, 1);
    layout.setBody(grid);

    JFXButton goButton = new JFXButton("Go!");
    JFXButton cancelButton = new JFXButton("Cancel");
    goButton.setButtonType(JFXButton.ButtonType.RAISED);
    cancelButton.setButtonType(JFXButton.ButtonType.FLAT);
    layout.setActions(cancelButton, goButton);

    JFXDialog dialog = new JFXDialog(stackPane, layout, JFXDialog.DialogTransition.CENTER);

    goButton.setOnAction(
        i -> {
          dialog.close();
          addXFloorEdgeHelper(node, combo.getValue());
        });
    cancelButton.setOnAction(i -> dialog.close());

    dialog.show();
  }

  private void addXFloorEdgeHelper(AnchorNode from, String toFloor) {
    String oldFloor = currentFloor;
    switchFloors(toFloor);
    from.setFill(Color.GOLD.deriveColor(1, 1, 1, 0.5));
    from.setStroke(Color.GOLD);
    overlayPaneTwo.getChildren().add(from);
    addEdgeHelper(from, true, oldFloor);
  }

  private void returnFloor(String toFloor, Line line) {
    switchFloors(toFloor);
    if (line != null) overlayPaneTwo.getChildren().add(0, line);
  }

  private void addEdgeHelper(AnchorNode startNode, boolean crossFloor, String oldFloor) {
    Line line = new Line(startNode.getX(), startNode.getY(), startNode.getX(), startNode.getY());
    line.startXProperty().bind(startNode.getXProperty());
    line.startYProperty().bind(startNode.getYProperty());
    line.setStroke(Color.TURQUOISE);
    if (crossFloor) line.getStrokeDashArray().addAll(7d, 4d);
    line.setStrokeWidth(2);
    overlayPaneTwo.getChildren().add(0, line);
    Tooltip tooltip = new Tooltip("Click to Place!");
    EventHandler oldMoveEvent = overlayPaneTwo.getOnMouseMoved();
    EventHandler oldClickEvent = overlayPaneTwo.getOnMouseClicked();
    EventHandler oldKeyHandler = scrollPane.getParent().getOnKeyReleased();

    overlayPaneTwo.setOnMouseMoved(
        e -> {
          if (hoveredNode != null) {
            line.setEndX(hoveredNode.getX());
            line.setEndY(hoveredNode.getY());
            tooltip.show(hoveredNode, e.getScreenX(), e.getScreenY() + 20);
          } else {
            line.setEndX(e.getX());
            line.setEndY(e.getY());
            tooltip.hide();
          }
        });

    // In case of escape, cancel the operation and do not draw anything
    scrollPane
        .getParent()
        .setOnKeyReleased(
            e -> {
              if (e.getCode().equals(KeyCode.ESCAPE)) {
                overlayPaneTwo.getChildren().remove(line);
                overlayPaneTwo.setOnMouseMoved(oldMoveEvent);
                overlayPaneTwo.setOnMouseClicked(oldClickEvent);
                scrollPane.getParent().setOnKeyReleased(oldKeyHandler);
                if (crossFloor) returnFloor(oldFloor, null);
              }
            });

    overlayPaneTwo.setOnMouseClicked(
        e -> {
          if (hoveredNode != null) {
            System.out.println("CLIKKKK");
            if (pathfindingService.getGraph().getConnections(hoveredNode.getNode()).stream()
                .anyMatch(n -> n.getId().equals(startNode.getNode().getId()))) return;

            overlayPaneTwo.setOnMouseMoved(oldMoveEvent);

            line.endXProperty().bind(hoveredNode.getXProperty());
            line.endYProperty().bind(hoveredNode.getYProperty());
            Edit edit = new Edit(startNode.getNode(), hoveredNode.getNode(), line, true, false);
            Label label =
                new Label(
                    startNode.getNode().toString(),
                    new MaterialDesignIconView(MaterialDesignIcon.LINK_VARIANT));
            Tooltip nodeTooltip = new Tooltip();
            nodeTooltip.setText(
                String.format(
                    "New Edge from: %s -> %s ",
                    startNode.getNode().toString(), hoveredNode.getNode().toString()));
            // tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_BOTTOM_RIGHT);
            label.setTooltip(nodeTooltip);
            label.setId(String.valueOf(edit.hashCode()));
            label.setUserData(edit);

            line.setStroke(Color.LIMEGREEN);
            tooltip.hide();
            historyList.getItems().add(0, label);
            edits.add(0, edit);
            overlayPaneTwo.setOnMouseClicked(oldClickEvent);
            if (crossFloor) returnFloor(oldFloor, line);
          }
        });
  }

  @FXML
  private void handleOgRatio(MouseEvent e) throws IOException {
    stackPane.setScaleX(1.0);
    stackPane.setScaleY(1.0);
    scrollPane.setHvalue(0.0);
    scrollPane.setVvalue(0.0);
    SCALE_TOTAL = SCALE_DELTA;
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
    if (scaleFactor * SCALE_TOTAL >= 1 && SCALE_TOTAL * scaleFactor < 2.5) {
      stackPane.setScaleX(stackPane.getScaleX() * scaleFactor);
      stackPane.setScaleY(stackPane.getScaleY() * scaleFactor);
      SCALE_TOTAL *= scaleFactor;

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
  private void handleAlign(ActionEvent e) {
    double meanX = selected.stream().mapToDouble(AnchorNode::getX).average().orElse(-1);
    double meanY = selected.stream().mapToDouble(AnchorNode::getY).average().orElse(-1);

    double deviationX =
        selected.stream().mapToDouble(n -> Math.pow(n.getX() - meanX, 2)).sum()
            / (selected.size() - 1);
    double deviationY =
        selected.stream().mapToDouble(n -> Math.pow(n.getY() - meanY, 2)).sum()
            / (selected.size() - 1);

    selected.forEach(
        n -> {
          if (deviationX < deviationY) n.move(meanX, n.getY());
          else n.move(n.getX(), meanY);
        });
    selected.forEach(n -> n.setSelected(false));
    selected.clear();
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
    switchFloors("G");
  }

  @FXML
  private void handleFloor1(ActionEvent e) {
    switchFloors("1");
  }

  @FXML
  private void handleFloor2(ActionEvent e) {
    switchFloors("2");
  }

  @FXML
  private void handleFloor3(ActionEvent e) {
    switchFloors("3");
  }

  @FXML
  private void handleFloor4(ActionEvent e) {
    switchFloors("4");
  }

  @FXML
  private void handleFloor5(ActionEvent e) {
    switchFloors("5");
  }

  private void switchFloors(String floor) {
    if (currentFloor.equals(floor)) return;
    resetButtonStyle();
    buttons
        .get(floor)
        .setStyle(
            "-fx-background-color: #f6bd38; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #ffffff;");

    Fading f = new Fading();
    f.fadeOut(panes.get(currentFloor), currentFloor.equals("G") ? 0.7 : 0.0);
    f.fadeIn(panes.get(floor), 1.0);
    currentFloor = floor;
    refreshNodes();
  }

  @FXML
  public void handleExpand(ActionEvent actionEvent) {
    chevronIcon.setRotate(floorSelector.isExpanded() ? 90 : 0);
  }

  @FXML
  private void handleAlgoChange(ActionEvent e) {
    algo = (algo + 1) % algorithms.length;
    btnAlgo.setText(algorithms[algo]);
    pathfindingService.setAlgorithm(algorithms[algo]);
  }
}
