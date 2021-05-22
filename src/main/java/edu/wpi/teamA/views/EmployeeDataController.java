package edu.wpi.teamA.views;

import com.jfoenix.controls.JFXListView;
import edu.wpi.teamA.modules.Record;
import edu.wpi.teamA.services.DatabaseService;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;
import javax.inject.Inject;

public class EmployeeDataController implements Initializable {
  @FXML private TableView<Record> employeeTable;
  @FXML private TableColumn<Record, String> id;
  @FXML private TableColumn<Record, String> name;
  @FXML private TableColumn<Record, String> occupation;
  @FXML private TableColumn<Record, String> department;
  @FXML private TableColumn<Record, String> age;
  @FXML private TableColumn<Record, String> sex;
  @FXML private TableColumn<Record, String> email;
  @FXML private TableColumn<Record, String> requestTypes;
  @FXML private JFXListView<String> list1;
  @FXML private JFXListView<String> list2;
  @FXML private JFXListView<String> list3;
  @FXML private JFXListView<String> list4;
  private ObservableList<Record> selectedItems;
  private Record selected;
  private String[] meta = {
    "name", "occupation", "department", "email", "age", "sex", "requestTypes"
  };

  @Inject DatabaseService db;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    employeeTable.setItems(db.getItemsEmployees());
    setValueFactories();

    selectedItems = employeeTable.getSelectionModel().getSelectedItems();
    selectedItems.addListener(
        new ListChangeListener<Record>() {
          @Override
          public void onChanged(Change<? extends Record> c) {
            if (selectedItems.size() > 0) {
              // TODO add stuff here
              selected = selectedItems.get(0);
              updateFields();
            } else {
              clearFields();
            }
          }
        });

    list1.getItems().addAll(new String[] {"Name: ", "Occupation: ", "Department: ", "Email: "});
    list3.getItems().addAll(new String[] {"Age: ", "Sex: ", "Request Types: "});
  }

  public static final StringConverter<String> IDENTITY_CONVERTER =
      new StringConverter<String>() {

        @Override
        public String toString(String object) {
          return object;
        }

        @Override
        public String fromString(String string) {
          return string;
        }
      };

  private void setValueFactories() {
    id.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getFieldAsString("id")));
    name.setCellValueFactory(
        cellData ->
            new SimpleStringProperty(
                cellData.getValue().getFieldAsString("lastname")
                    + ", "
                    + cellData.getValue().getFieldAsString("firstname")));
    occupation.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getFieldAsString("occupation")));
    department.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getFieldAsString("department")));
    age.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getFieldAsString("age")));
    sex.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getFieldAsString("sex")));
    email.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getFieldAsString("email")));
    requestTypes.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getFieldAsString("requestTypes")));
    listCellFactory(list1);
    listCellEditFactory(list2, 0);
    listCellFactory(list3);
    listCellEditFactory(list4, 4);
  }

  private void listCellFactory(JFXListView<String> list1) {
    list1.setCellFactory(
        l1 -> {
          // FXListCell<String> cell = new JFXListCell<String>();
          TextFieldListCell<String> cell = new TextFieldListCell<String>(IDENTITY_CONVERTER);
          cell.setEditable(true);
          return cell;
        });
  }

  private void listCellEditFactory(JFXListView<String> listX, int n) {
    listCellFactory(listX);
    listX.setOnEditCommit(
        new EventHandler<ListView.EditEvent<String>>() {
          @Override
          public void handle(ListView.EditEvent<String> e) {
            int i = e.getIndex();
            selected.addProperty(meta[i + n], e.getNewValue());
            db.updateRecord("EMPLOYEES", selected, meta[i + n], e.getNewValue());
            employeeTable.refresh();
            listX.getItems().set(i, e.getNewValue());
            listX.refresh();
          }
        });
  }

  private void updateFields() {
    clearFields();
    if (selectedItems.size() == 0) {
      return;
    } else {
      list2
          .getItems()
          .addAll(
              new String[] {
                (selected.getFieldAsString("lastname")
                    + ", "
                    + selected.getFieldAsString("firstname")),
                selected.getFieldAsString("occupation"),
                selected.getFieldAsString("department"),
                selected.getFieldAsString("email")
              });
      list4
          .getItems()
          .addAll(
              new String[] {
                selected.getFieldAsString("age"),
                selected.getFieldAsString("sex"),
                selected.getFieldAsString("requestTypes")
              });
    }
  }

  private void clearFields() {
    list2.getItems().removeAll(list2.getItems());
    list4.getItems().removeAll(list4.getItems());
  }
}
