package edu.wpi.teamA.views;

import com.google.inject.Inject;
import edu.wpi.teamA.modules.EditCell;
import edu.wpi.teamA.modules.Record;
import edu.wpi.teamA.services.DatabaseService;
import edu.wpi.teamA.services.PathfindingService;
import edu.wpi.teamA.state.HomeState;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MapNodesTableController implements Initializable {

  @FXML private Label editLabel;
  @FXML private Button btnCancel;
  @FXML private Button btnUpload;
  @FXML private Button btnDownload;
  @FXML private Button btnSaveChanges;
  @FXML private Button btnEdit;
  @FXML private Button btnDelete;
  @FXML private Button btnAddRow;
  @FXML private TableView<Record> node_table;
  @FXML private TableColumn<Record, String> id;
  @FXML private TableColumn<Record, String> floor;
  @FXML private TableColumn<Record, String> building;
  @FXML private TableColumn<Record, String> node_type;
  @FXML private TableColumn<Record, String> long_name;
  @FXML private TableColumn<Record, String> short_name;
  @FXML private TableColumn<Record, String> team_assigned;
  @FXML private TableColumn<Record, String> y;
  @FXML private TableColumn<Record, String> x;
  @FXML private Button btnHome;
  @FXML private Button btnPfd;
  @FXML private Button btnMET;

  @Inject DatabaseService db;
  @Inject PathfindingService pathfindingService;
  @Inject FXMLLoader loader;
  @Inject HomeState homeState;

  private final ObservableList<Record> deepCopy = FXCollections.observableArrayList();

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    setValueFactories();
    setEditFactory(floor, "floor");
    setEditFactory(building, "building");
    setEditFactory(node_type, "nodetype");
    setEditFactory(long_name, "longname");
    setEditFactory(short_name, "shortname");
    setEditFactory(team_assigned, "team");
    setEditFactory(y, "ycoord");
    setEditFactory(x, "xcoord");

    node_table.setItems(db.getItemsNodes());
  }

  private void setEditFactory(TableColumn<Record, String> col, String sCol) {
    col.setCellFactory(column -> EditCell.createStringEditCell());
    col.setOnEditCommit(
        new EventHandler<TableColumn.CellEditEvent<Record, String>>() {
          @Override
          public void handle(TableColumn.CellEditEvent<Record, String> t) {
            Record var = t.getTableView().getItems().get(t.getTablePosition().getRow());
            db.updateRecord("NODES", var, sCol, t.getNewValue());
          }
        });
  }

  @FXML
  private void handleDownloadAction(ActionEvent event) throws IOException {
    Stage stage = (Stage) btnUpload.getScene().getWindow();
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
    File csv = fileChooser.showSaveDialog(stage);
    if (csv != null) db.saveCSV("NODES", csv);
  }

  @FXML
  private void handleUploadAction(ActionEvent event) throws IOException {
    Stage stage = (Stage) btnUpload.getScene().getWindow();
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
    File csv = fileChooser.showOpenDialog(stage);
    if (csv != null) {
      db.loadCSV("NODES", csv);
      pathfindingService.updateNodes();
      pathfindingService.updateEdges();
      homeState.refreshNodeList();
    }
  }

  @FXML
  private void handleEditAction(ActionEvent event) throws IOException {
    editMode();
    node_table.setEditable(true);
    db.toggleEditMode(false);

    deepCopy.clear();
    int i = 0;
    for (Record r : node_table.getItems()) {
      deepCopy.add(new Record());
      for (String s : r.getKeys()) deepCopy.get(i).addProperty(s, r.getFieldAsString(s));
      i++;
    }
  }

  @FXML
  public void handleDeleteAction(ActionEvent actionEvent) {
    Record selectedItem = node_table.getSelectionModel().getSelectedItem();
    db.deleteRecord("NODES", selectedItem);
    node_table.getItems().remove(selectedItem);
  }

  @FXML
  public void handleAddRowAction(ActionEvent actionEvent) {
    HashMap<String, String> blankNodeMap = new HashMap<>();
    blankNodeMap.put("id", "NEW");
    blankNodeMap.put("floor", "");
    blankNodeMap.put("building", "");
    blankNodeMap.put("xcoord", "");
    blankNodeMap.put("ycoord", "");
    blankNodeMap.put("longname", "");
    blankNodeMap.put("shortname", "");
    blankNodeMap.put("nodetype", "NEW");
    blankNodeMap.put("team", "");
    Record newNode = new Record(blankNodeMap);
    db.addRecord("NODES", newNode);
    node_table.getItems().add(newNode);
    node_table.scrollTo(node_table.getItems().size());
  }

  @FXML
  private void handleSaveChangesAction(ActionEvent event) throws IOException {
    viewMode();
    node_table.setEditable(false);
    db.toggleEditMode(true);
    pathfindingService.updateNodes();
    pathfindingService.updateEdges();
    homeState.refreshNodeList();
  }

  @FXML
  private void handleCancelAction(ActionEvent event) {
    viewMode();
    node_table.setEditable(false);
    db.toggleEditMode(false);
    node_table.getItems().setAll(deepCopy);
  }

  private void setValueFactories() {
    id.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getFieldAsString("id")));
    floor.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getFieldAsString("floor")));
    building.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getFieldAsString("building")));
    x.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getFieldAsString("xcoord")));
    y.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getFieldAsString("ycoord")));
    long_name.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getFieldAsString("longname")));
    short_name.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getFieldAsString("shortname")));
    node_type.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getFieldAsString("nodetype")));
    team_assigned.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getFieldAsString("team")));
  }

  private void editMode() {
    btnMET.setVisible(false);
    btnHome.setVisible(false);
    btnEdit.setVisible(false);
    btnPfd.setVisible(false);
    btnDownload.setVisible(true);
    btnSaveChanges.setVisible(true);
    btnUpload.setVisible(true);
    btnCancel.setVisible(true);
    editLabel.setVisible(true);
    btnDelete.setVisible(true);
    btnAddRow.setVisible(true);
  }

  private void viewMode() {
    btnMET.setVisible(true);
    btnHome.setVisible(true);
    btnEdit.setVisible(true);
    btnPfd.setVisible(true);
    btnDownload.setVisible(false);
    btnSaveChanges.setVisible(false);
    btnUpload.setVisible(false);
    btnCancel.setVisible(false);
    editLabel.setVisible(false);
    btnDelete.setVisible(false);
    btnAddRow.setVisible(false);
  }
}
