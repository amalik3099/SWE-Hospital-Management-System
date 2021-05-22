package edu.wpi.teamA.views;

import com.google.inject.Inject;
import com.jfoenix.controls.*;
import edu.wpi.teamA.modules.HospitalNode;
import edu.wpi.teamA.modules.Record;
import edu.wpi.teamA.services.DatabaseService;
import edu.wpi.teamA.services.PathfindingService;
import edu.wpi.teamA.state.HomeState;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class StatusController implements Initializable {
  private JFXButton btnMarkComp;
  private JFXButton btnAssign;
  private JFXButton btnEntry;
  private JFXComboBox<String> assignBox;
  private JFXComboBox<String> entryBox;
  @FXML private TableView<Record> requestsTable;
  @FXML private TableColumn<Record, String> id;
  @FXML private TableColumn<Record, String> date;
  @FXML private TableColumn<Record, String> reqType;
  @FXML private TableColumn<Record, String> assignedTo;
  @FXML private TableColumn<Record, String> status;
  @FXML private TableColumn<Record, String> description;
  @FXML private TableColumn<Record, String> urgency;
  @FXML private MenuItem btnEdit;
  @FXML private MenuItem btnDelete;
  @FXML private StackPane dialogPane;

  @Inject DatabaseService db;
  @Inject PathfindingService pathfindingService;
  @Inject HomeState homeState;

  private ObservableList<Record> selectedItems;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    setValueFactories();
    ObservableList<String> employeeList = FXCollections.observableArrayList();
    assignBox = new JFXComboBox<>();
    assignBox.setItems(employeeList);
    requestsTable.setItems(db.getItemsSR());
    requestsTable.getSelectionModel().select(1);
    selectedItems = requestsTable.getSelectionModel().getSelectedItems();
    selectedItems.addListener(
        new ListChangeListener<Record>() {
          @Override
          public void onChanged(Change<? extends Record> c) {
            if (selectedItems.size() > 0) {
              assignBox.setItems(
                  db.getStringList(
                      "EMPLOYEES",
                      new String[] {"lastname", "firstname"},
                      new String[] {"requestTypes"},
                      new String[] {selectedItems.get(0).getFieldAsString("type")},
                      new String[] {"lastname"}));
            }
          }
        });
  }

  private void setValueFactories() {
    id.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getFieldAsString("id")));
    date.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getFieldAsString("date")));
    reqType.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getFieldAsString("type")));
    assignedTo.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getFieldAsString("assigned")));
    status.setCellValueFactory(
        cellData ->
            new SimpleStringProperty(
                cellData.getValue().getFieldAsString("complete").equals("true")
                    ? "Completed"
                    : "Open"));
    description.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getFieldAsString("description")));
    urgency.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getFieldAsString("urgency")));
  }

  @FXML
  void handleMenuEdit(ActionEvent e) throws IOException {
    Record selectedItem = requestsTable.getSelectionModel().getSelectedItem();
    if (selectedItem != null) {
      JFXButton btnClose = new JFXButton("Close");
      Label assignTo = new Label("Assign To:");
      btnAssign = new JFXButton("Assign");
      HBox hbox1 = new HBox();
      hbox1.getChildren().addAll(assignBox, btnAssign);
      Label entryLabel = new Label("Pick Entry Point");
      JFXDialogLayout editContent = new JFXDialogLayout();
      entryBox = new JFXComboBox<>();
      ObservableList<String> entryChoices =
          FXCollections.observableArrayList("Atrium Main Entrance", "Emergency Entrance");
      entryBox.setItems(entryChoices);
      btnEntry = new JFXButton("Set Entry");
      HBox hbox2 = new HBox();
      hbox2.getChildren().addAll(entryBox, btnEntry);
      btnMarkComp = new JFXButton("Mark Complete");
      btnMarkComp.setPadding(new Insets(15, 0, 0, 0));
      VBox vboxReg = new VBox();
      vboxReg.setSpacing(20.0);
      vboxReg.getChildren().addAll(assignTo, hbox1, entryLabel, hbox2, btnMarkComp);
      VBox vboxEntr = new VBox();
      vboxEntr.setSpacing(20.0);
      vboxEntr.getChildren().addAll(assignTo, hbox1, btnMarkComp);
      editContent.setHeading(new Text("Edit Survey Request"));
      editContent.setBody(vboxReg);
      vboxReg.setAlignment(Pos.CENTER);
      editContent.setActions(btnClose);
      JFXDialog editDialog =
          new JFXDialog(dialogPane, editContent, JFXDialog.DialogTransition.CENTER);
      editDialog.show();
      btnClose.setOnAction(
          new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
              editDialog.close();
            }
          });
      btnAssign.setOnAction(
          new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
              Record selectedItem = requestsTable.getSelectionModel().getSelectedItem();
              if (selectedItem != null)
                db.updateRecord("REQUESTS", selectedItem, "assigned", assignBox.getValue());
              requestsTable.refresh();
            }
          });
      btnMarkComp.setOnAction(
          new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
              Record selectedItem = requestsTable.getSelectionModel().getSelectedItem();
              if (selectedItem != null)
                db.updateRecord("REQUESTS", selectedItem, "complete", "true");
              requestsTable.refresh();
              assert selectedItem != null;
            }
          });
      btnEntry.setOnAction(
          new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
              if (entryBox.getValue() == null || !homeState.getEntrance().getValue().equals(""))
                return;
              homeState.setEntrance(entryBox.getValue());
              homeState.refreshNodeList();
              HospitalNode temp = new HospitalNode();
              temp.addProperty(
                  "id",
                  entryBox.getValue().equals("Atrium Main Entrance") ? "AEXIT00201" : "AEXIT00101");
              pathfindingService.addImpossibleNode(temp);
            }
          });
    }
  }

  @FXML
  void handleMenuDelete(ActionEvent e) {
    Record selectedItem = requestsTable.getSelectionModel().getSelectedItem();
    if (selectedItem != null) {
      JFXDialogLayout deleteContent = new JFXDialogLayout();
      JFXButton btnDelete = new JFXButton("Delete");
      JFXButton btnCancel = new JFXButton("Cancel");
      HBox hbox = new HBox();
      hbox.getChildren().addAll(btnCancel, btnDelete);
      deleteContent.setHeading(new Text("Delete Survey Request"));
      deleteContent.setBody(new Text("Are you sure you want to Delete the Request?"));
      deleteContent.setActions(hbox);
      JFXDialog deleteDialog =
          new JFXDialog(dialogPane, deleteContent, JFXDialog.DialogTransition.CENTER);
      deleteDialog.show();
      btnCancel.setOnAction(
          new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
              deleteDialog.close();
            }
          });
      btnDelete.setOnAction(
          new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
              db.deleteRecord("REQUESTS", selectedItem);
              requestsTable.getItems().remove(selectedItem);
              deleteDialog.close();
            }
          });
    }
  }
}
