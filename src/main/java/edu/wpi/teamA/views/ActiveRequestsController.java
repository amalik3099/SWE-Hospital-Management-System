package edu.wpi.teamA.views;

import com.google.inject.Inject;
import com.jfoenix.controls.*;
import edu.wpi.teamA.modules.Record;
import edu.wpi.teamA.modules.RecordPane;
import edu.wpi.teamA.services.DatabaseService;
import edu.wpi.teamA.services.PathfindingService;
import edu.wpi.teamA.state.HomeState;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class ActiveRequestsController implements Initializable {
  private JFXComboBox<String> assignBox;
  private JFXComboBox<String> entryBox;
  @FXML private VBox requestBox;
  @FXML private ScrollPane scrollPane;

  @Inject DatabaseService db;
  @Inject PathfindingService pathfindingService;
  @Inject HomeState homeState;

  private ObservableList<Record> selectedItems;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    ObservableList<String> employeeList = FXCollections.observableArrayList();
    db.getItemsSR()
        .addListener(
            new ListChangeListener<Record>() {

              @Override
              public void onChanged(Change<? extends Record> c) {
                while (c.next()) {
                  if (c.wasAdded()) {
                    requestBox
                        .getChildren()
                        .addAll(
                            c.getAddedSubList().stream()
                                .map(record -> createServicePane(record))
                                .collect(Collectors.toList()));
                  }
                  if (c.wasRemoved()) {
                    requestBox
                        .getChildren()
                        .removeIf(
                            child -> {
                              return c.getRemoved().stream()
                                  .map(record -> record.getFieldAsString("id"))
                                  .collect(Collectors.toList())
                                  .contains((child.getId()));
                            });
                  }
                }
                ArrayList<Node> sort = new ArrayList<>(requestBox.getChildren());
                sort.sort(
                    (o1, o2) -> {
                      String o1Type = getProperty((TitledPane) o1, "Type");
                      String o2Type = getProperty((TitledPane) o2, "Type");
                      if (o1Type.equals("Hospital Entry") && o2Type.equals("Hospital Entry")) {
                        return 0;
                      } else if (o1Type.equals("Hospital Entry")) {
                        return -1;
                      } else if (o2Type.equals("Hospital Entry")) {
                        return 1;
                      }

                      String o1Urgent = getProperty((TitledPane) o1, "Urgency");
                      String o2Urgent = getProperty((TitledPane) o2, "Urgency");
                      if (o1Urgent.equals(o2Urgent)) {
                        return 0;
                      } else if (o1Urgent.equals("true")) {
                        return -1;
                      } else {
                        return 1;
                      }
                    });
                sort.forEach(n -> System.out.println(((TitledPane) n).getText()));
                requestBox.getChildren().clear();
                requestBox.getChildren().addAll(sort);
              }
            });
    requestBox.getChildren().removeAll(requestBox.getChildren());
    requestBox.getChildren().addAll(returnServicePane());
  }

  private ObservableList<TitledPane> returnServicePane() {
    ObservableList<TitledPane> requests = FXCollections.observableArrayList();
    SortedList<Record> sort =
        db.getItemsSR()
            .sorted(
                ((o1, o2) -> {
                  if (o1.getFieldAsString("Type").equals("Hospital Entry")
                      && o2.getFieldAsString("Type").equals("Hospital Entry")) {
                    return 0;
                  } else if (o1.getFieldAsString("Type").equals("Hospital Entry")) {
                    return -1;
                  } else if (o2.getFieldAsString("Type").equals("Hospital Entry")) {
                    return 1;
                  }

                  if (o1.getFieldAsString("Urgency").equals(o2.getFieldAsString("Urgency"))) {
                    return 0;
                  } else if (o1.getFieldAsString("Urgency").equals("true")) {
                    return -1;
                  } else {
                    return 1;
                  }
                }));
    sort.forEach(
        Request -> {
          requests.add(createServicePane(Request));
        });

    /*TitledPane t = requests.get(0);
    ((Pane)t.getContent()).getChildren()*/
    return requests;
  }

  private TitledPane createServicePane(Record record) {
    RecordPane servicePane = null;
    if (!record.getFieldAsString("Type").equals("Hospital Entry"))
      servicePane = new RecordPane(record, requestBox, 1, db, homeState, pathfindingService);
    else servicePane = new RecordPane(record, requestBox, 2, db, homeState, pathfindingService);
    return servicePane.getRecordPane();
  }

  private String getProperty(TitledPane t, String key) {
    return ((JFXTextField)
            ((HBox)
                    ((VBox)
                            ((HBox) ((AnchorPane) t.getContent()).getChildren().get(0))
                                .getChildren()
                                .get(0))
                        .getChildren()
                        .filtered(node -> node.getId().equals(key + "HBox"))
                        .get(0))
                .getChildren()
                .get(1))
        .getText();
  }
}
