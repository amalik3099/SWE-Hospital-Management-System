package edu.wpi.teamA.views;

import com.google.inject.Inject;
import edu.wpi.teamA.modules.EditCell;
import edu.wpi.teamA.modules.Record;
import edu.wpi.teamA.services.DatabaseService;
import edu.wpi.teamA.services.PathfindingService;
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

public class MapEdgesTableController implements Initializable {

  @FXML private Label editLabel;
  @FXML private Button btnEdit;
  @FXML private Button btnUpload;
  @FXML private Button btnSaveChanges;
  @FXML private Button btnDownload;
  @FXML private Button btnCancel;
  @FXML private Button btnDelete;
  @FXML private Button btnAddRow;
  @FXML private TableView<Record> edge_table;
  @FXML private TableColumn<Record, String> id;
  @FXML private TableColumn<Record, String> startNode;
  @FXML private TableColumn<Record, String> endNode;
  @FXML private Button btnHome;
  @FXML private Button btnPfd;
  @FXML private Button btnMNT;

  @Inject DatabaseService db;
  @Inject PathfindingService pathfindingService;
  //  @Inject Injector injector;
  @Inject FXMLLoader loader;

  // Remove this HashMap later in refactoring
  private final HashMap<Integer, Integer> edits = new HashMap<Integer, Integer>();
  private final ObservableList<Record> deepCopy = FXCollections.observableArrayList();

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    setValueFactories();
    setEditFactory(startNode, "startnode");
    setEditFactory(endNode, "endnode");

    edge_table.setItems(db.getItemsEdges());
  }

  private void setEditFactory(TableColumn<Record, String> col, String sCol) {
    col.setCellFactory(column -> EditCell.createStringEditCell());
    col.setOnEditCommit(
        new EventHandler<TableColumn.CellEditEvent<Record, String>>() {
          @Override
          public void handle(TableColumn.CellEditEvent<Record, String> t) {
            Record var = t.getTableView().getItems().get(t.getTablePosition().getRow());
            db.updateRecord("EDGES", var, sCol, t.getNewValue());
            if (sCol.equals("startnode") || sCol.equals("endnode")) {
              db.updateRecord(
                  "EDGES",
                  var,
                  "id",
                  var.getFieldAsString("startnode") + "_" + var.getFieldAsString("endnode"));
            }
          }
        });
  }

  @FXML
  public void handleDownloadAction(ActionEvent event) throws IOException {
    Stage stage = (Stage) btnUpload.getScene().getWindow();
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
    File csv = fileChooser.showSaveDialog(stage);
    if (csv != null) db.saveCSV("EDGES", csv);
  }

  @FXML
  public void handleUploadAction(ActionEvent event) throws IOException {
    Stage stage = (Stage) btnUpload.getScene().getWindow();
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
    File csv = fileChooser.showOpenDialog(stage);
    if (csv != null) {
      db.loadCSV("EDGES", csv);
      pathfindingService.updateEdges();
    }
  }

  @FXML
  public void handleEditAction(ActionEvent event) {
    editMode();
    edge_table.setEditable(true);
    db.toggleEditMode(false);

    deepCopy.clear();
    int i = 0;
    for (Record r : edge_table.getItems()) {
      deepCopy.add(new Record());
      for (String s : r.getKeys()) deepCopy.get(i).addProperty(s, r.getFieldAsString(s));
      i++;
    }
  }

  @FXML
  public void handleDeleteAction(ActionEvent actionEvent) {
    Record selectedItem = edge_table.getSelectionModel().getSelectedItem();
    db.deleteRecord("EDGES", selectedItem);
    edge_table.getItems().remove(selectedItem);
  }

  @FXML
  public void handleAddRowAction(ActionEvent actionEvent) {
    HashMap<String, String> blankEdgeMap = new HashMap<>();
    blankEdgeMap.put("id", "NEW");
    blankEdgeMap.put("startnode", "");
    blankEdgeMap.put("endnode", "");
    Record newNode = new Record(blankEdgeMap);
    db.addRecord("EDGES", newNode);
    edge_table.getItems().add(newNode);
    edge_table.scrollTo(edge_table.getItems().size());
  }

  @FXML
  public void handleSaveChangesAction(ActionEvent event) {
    viewMode();
    edge_table.setEditable(false);
    db.toggleEditMode(true);
    pathfindingService.updateEdges();
  }

  @FXML
  public void handleCancelAction(ActionEvent event) {
    viewMode();
    edge_table.setEditable(false);
    db.toggleEditMode(false);
    edge_table.getItems().setAll(deepCopy);
  }

  private void setValueFactories() {
    id.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getFieldAsString("id")));
    startNode.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getFieldAsString("startnode")));
    endNode.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getFieldAsString("endnode")));
  }

  private void editMode() {
    btnMNT.setVisible(false);
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
    btnMNT.setVisible(true);
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
