package edu.wpi.teamA.modules;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import edu.wpi.teamA.services.DatabaseService;
import edu.wpi.teamA.services.PathfindingService;
import edu.wpi.teamA.state.HomeState;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.commons.lang3.StringUtils;

public class RecordPane {

  private final Pane parent;
  private final Record record;
  private final TitledPane recordPane;
  private static final int SERVICE_REQUEST = 1;
  private static final int COVID_SURVEY = 2;
  private final ArrayList<String> editIDs = new ArrayList<String>();
  private final DatabaseService db;
  private final HomeState homeState;
  private final PathfindingService pathfindingService;
  private final ArrayList<JFXButton> buttons1 = new ArrayList<JFXButton>();
  private final ArrayList<JFXButton> buttons2 = new ArrayList<JFXButton>();
  private boolean flag = false;

  public RecordPane(
      Record r,
      Pane parent,
      int type,
      DatabaseService db,
      HomeState homeState,
      PathfindingService pathfindingService) {
    this.parent = parent;
    this.record = r;
    this.db = db;
    this.homeState = homeState;
    this.pathfindingService = pathfindingService;
    recordPane = new TitledPane();
    Platform.runLater(
        () -> {
          Pane title = (Pane) recordPane.lookup(".title");
          title.setPrefHeight(60);
          title.setPadding(new Insets(20));
        });

    if (type == SERVICE_REQUEST) {
      createSRBox();
    } else {
      createCovidBox();
    }
    addDescContent();
  }

  private void addDescContent() {
    HBox contentBox = new HBox(0);
    AnchorPane contentPane = new AnchorPane(contentBox);
    AnchorPane.setTopAnchor(contentBox, 0d);
    AnchorPane.setBottomAnchor(contentBox, 0d);
    AnchorPane.setLeftAnchor(contentBox, 0d);
    AnchorPane.setRightAnchor(contentBox, 0d);
    VBox leftBox = (VBox) recordPane.getContent();
    VBox rightBox = new VBox(10);
    leftBox.setPrefWidth(10000);
    rightBox.setPrefWidth(10000);
    leftBox.setPadding(new Insets(10));
    rightBox.setPadding(new Insets(10));
    rightBox.setAlignment(Pos.TOP_LEFT);
    // contentPane.setBackground(new Background(new BackgroundFill(Color.web("#3a5586"), null,
    // null)));
    // null)));
    // contentBox.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
    // leftBox.setBackground(new Background(new BackgroundFill(Color.web("#0a8cd5"), null, null)));
    contentBox.getChildren().addAll(leftBox, new Separator(Orientation.VERTICAL), rightBox);
    Label topInfo = new Label("Additional Information:");
    topInfo.setFont(new Font(23));
    rightBox.getChildren().add(topInfo);
    TextFlow tf = new TextFlow();
    tf.setPrefWidth(10000);
    tf.setPadding(new Insets(10));
    rightBox.getChildren().add(tf);
    GridPane gp = new GridPane();
    rightBox.getChildren().add(gp);
    int rows = 0;
    tf.setLineSpacing(10);
    // tf.setBackground(new Background(new BackgroundFill(Color.GOLD, null, null)));
    String description = record.getFieldAsString("Description");
    String[] questions = description.split("\\|");
    for (String q : questions) {
      System.out.println("QUESTION: " + q);
      if (q.length() > 0) {
        String a;
        if (q.contains("AM") || q.contains("PM")) a = q.split(":")[1] + ":" + q.split(":")[2];
        else a = q.split(":")[1];
        if (a.equals("Y") || a.equals("N")) {
          MaterialDesignIconView icn =
              new MaterialDesignIconView(
                  a.equals("Y")
                      ? MaterialDesignIcon.CHECKBOX_MARKED
                      : MaterialDesignIcon.CLOSE_BOX);
          icn.setGlyphSize(20);
          icn.setFill(a.equals("Y") ? Color.LIMEGREEN : Color.web("#d72828"));
          Label lb = new Label("   " + q.split(":")[0]);
          gp.add(lb, 0, rows);
          gp.add(icn, 1, rows);
          rows += 1;
          lb.setPadding(new Insets(10));
          // rightBox.getChildren().add(lb);
        } else {
          Text ques = new Text(q.split(":")[0] + ":");
          Text answer = new Text("    " + a);
          ques.setUnderline(true);
          ques.setStyle("-fx-font-weight: bold");
          answer.setStyle("-fx-font-style: italic");
          tf.getChildren().addAll(ques, answer, new Text(System.lineSeparator()));
        }
      }
    }
    recordPane.setContent(contentPane);
  }

  private void createCovidBox() {
    recordPane.setId(record.getFieldAsString("id"));
    VBox.setMargin(recordPane, new Insets(20, 30, 0, 30));

    // Add our items to it
    MaterialDesignIconView iconView = new MaterialDesignIconView();
    iconView.setIcon(MaterialDesignIcon.RUN);
    iconView.setSize("2em");

    JFXButton btnEdit = new JFXButton("Edit");
    btnEdit.getStyleClass().add("editButton");

    JFXButton btnDelete = new JFXButton("Close");
    btnDelete.getStyleClass().add("deleteButton");

    buttons1.add(btnEdit);
    buttons1.add(btnDelete);

    JFXButton btnCancel = new JFXButton("Cancel");
    btnCancel.getStyleClass().add("deleteButton");

    JFXButton btnSave = new JFXButton("Save");
    btnSave.getStyleClass().add("editButton");

    //    JFXButton btnMarkComp = new JFXButton("Mark Complete");
    //    btnMarkComp.getStyleClass().add("markCompButton");

    buttons2.add(btnSave);
    buttons2.add(btnCancel);
    // buttons2.add(btnMarkComp);

    addButtonHandlers(btnEdit, btnSave, btnCancel, btnDelete, null);
    Label entry = new Label("Atrium Main Entrance");

    recordPane.setGraphic(iconView);
    recordPane.setText(
        record.getFieldAsString("Type")
            + "\t\t"
            + "Reviewer: "
            + record.getFieldAsString("Assigned")
            + "\t\t"
            + "Entrance: "
            + entry.getText());

    recordPane.setAlignment(Pos.CENTER_LEFT);
    recordPane.setStyle("-fx-min-height: 65; -fx-font: system; -fx-font: bold; -fx-font-size: 15");
    recordPane.setExpanded(false);

    VBox contentVbox = new VBox();

    HBox idHbox = new HBox();
    idHbox.setId("idHBox");
    Label id = new Label("ID");
    JFXTextField idTxt = new JFXTextField(record.getFieldAsString("id"));
    idTxt.setEditable(false);
    idHbox.getChildren().addAll(id, idTxt);
    contentVbox.getChildren().add(idHbox);
    idHbox.setAlignment(Pos.CENTER_LEFT);
    HBox.setMargin(id, new Insets(0, 0, 0, 20));
    HBox.setMargin(idTxt, new Insets(0, 0, 0, 80));
    ((Label) idHbox.getChildren().get(0))
        .widthProperty()
        .addListener(
            new ChangeListener<Number>() {
              @Override
              public void changed(
                  ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                HBox.setMargin(
                    idHbox.getChildren().get(1),
                    new Insets(0, 0, 0, 120 - ((Label) idHbox.getChildren().get(0)).getWidth()));
              }
            });

    HBox entryHbox = new HBox();
    entryHbox.setId("entryHBox");
    Label entryLabel = new Label("Entry:");
    entryLabel.setMinWidth(80);
    JFXTextField entryTxt = new JFXTextField();
    entryLabel.setId("entryTxt");
    entryTxt.setEditable(false);
    JFXComboBox<String> entryList = new JFXComboBox<>();
    entryList.setStyle("-fx-text-fill: #000");
    entryList.setVisible(false);
    entryList.setId("employeeBox");
    entryHbox.getChildren().addAll(entryLabel, entryTxt, entryList);
    contentVbox.getChildren().add(entryHbox);
    entryHbox.setAlignment(Pos.CENTER_LEFT);
    HBox.setMargin(entryLabel, new Insets(0, 0, 0, 20));
    HBox.setMargin(entryTxt, new Insets(0, 0, 0, 80));
    HBox.setMargin(entryList, new Insets(0, 0, 0, 80));
    ((Label) entryHbox.getChildren().get(0))
        .widthProperty()
        .addListener(
            new ChangeListener<Number>() {
              @Override
              public void changed(
                  ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                HBox.setMargin(
                    entryHbox.getChildren().get(1),
                    new Insets(0, 0, 0, 120 - ((Label) entryHbox.getChildren().get(0)).getWidth()));
                HBox.setMargin(
                    entryHbox.getChildren().get(2),
                    new Insets(0, 0, 0, 120 - ((Label) entryHbox.getChildren().get(0)).getWidth()));
              }
            });

    HBox assignedHbox = new HBox();
    assignedHbox.setId("assignedHBox");
    Label assigned = new Label("Assigned");
    assigned.setMinWidth(80);
    JFXTextField assignedTxt = new JFXTextField(record.getFieldAsString("Assigned"));
    assigned.setId("AssignedTxt");
    assignedTxt.setEditable(false);
    JFXComboBox<String> employeeList = new JFXComboBox<>();
    employeeList.setStyle("-fx-text-fill: #000");
    employeeList.setVisible(false);
    employeeList.setId("employeeBox");
    assignedHbox.getChildren().addAll(assigned, assignedTxt, employeeList);
    contentVbox.getChildren().add(assignedHbox);
    assignedHbox.setAlignment(Pos.CENTER_LEFT);
    HBox.setMargin(assigned, new Insets(0, 0, 0, 20));
    HBox.setMargin(assignedTxt, new Insets(0, 0, 0, 80));
    HBox.setMargin(employeeList, new Insets(0, 0, 0, 80));
    ((Label) assignedHbox.getChildren().get(0))
        .widthProperty()
        .addListener(
            new ChangeListener<Number>() {
              @Override
              public void changed(
                  ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                HBox.setMargin(
                    assignedHbox.getChildren().get(1),
                    new Insets(
                        0, 0, 0, 120 - ((Label) assignedHbox.getChildren().get(0)).getWidth()));
                HBox.setMargin(
                    assignedHbox.getChildren().get(2),
                    new Insets(
                        0, 0, 0, 120 - ((Label) assignedHbox.getChildren().get(0)).getWidth()));
              }
            });

    for (String s : record.getKeys()) {
      if (!s.equals("id")
          && !s.equals("entry")
          && !s.equals("Assigned")
          && !s.equals("Urgency")
          && !s.equals("Description")
          && !s.equals("Name")
          && !s.equals("Email")
          && !s.equals("Location")) {
        // Do we want to make the key into something more visibly appealing?
        // Ex: urgency -> Urgent:
        // s = parseKey(s);
        // Add s to list of editable stuff so we know what we can edit
        editIDs.add(s);
        HBox newH = new HBox();
        newH.setId(s + "HBox");
        Label newLabel = new Label(StringUtils.capitalize(s));
        newLabel.setId(s + "Label");
        JFXTextField newText = new JFXTextField(record.getFieldAsString(s));
        newText.setEditable(false);
        newText.setId(s + "Text");
        newH.getChildren().addAll(newLabel, newText);
        newH.setAlignment(Pos.CENTER_LEFT);
        HBox.setMargin(newH.getChildren().get(0), new Insets(0, 0, 0, 20));
        HBox.setMargin(newH.getChildren().get(1), new Insets(0, 0, 0, 80));
        ((Label) newH.getChildren().get(0))
            .widthProperty()
            .addListener(
                new ChangeListener<Number>() {
                  @Override
                  public void changed(
                      ObservableValue<? extends Number> observable,
                      Number oldValue,
                      Number newValue) {
                    HBox.setMargin(
                        newH.getChildren().get(1),
                        new Insets(0, 0, 0, 120 - ((Label) newH.getChildren().get(0)).getWidth()));
                  }
                });
        contentVbox.getChildren().add(newH);
      }
    }

    HBox buttonHbox = new HBox();
    buttonHbox.setId("buttonHBox");
    buttonHbox.setVisible(true);
    buttonHbox.getChildren().addAll(buttons1);
    HBox.setMargin(buttonHbox.getChildren().get(0), new Insets(15, 0, 0, 20));
    HBox.setMargin(buttonHbox.getChildren().get(1), new Insets(15, 0, 0, 20));

    contentVbox.getChildren().addAll(buttonHbox);

    // set margins for all children in content vbox
    for (Node node : contentVbox.getChildren()) {
      VBox.setMargin(node, new Insets(10, 0, 0, 0));
    }

    recordPane.setContent(contentVbox);
  }

  private void createSRBox() {
    recordPane.setId(record.getFieldAsString("id"));
    VBox.setMargin(recordPane, new Insets(20, 30, 0, 30));

    // Add our items to it
    MaterialDesignIconView iconView = new MaterialDesignIconView();
    iconView.setIcon(switchIcon(record.getFieldAsString("Type")));
    iconView.setSize("2em");

    JFXButton btnEdit = new JFXButton("Edit");
    btnEdit.getStyleClass().add("editButton");

    JFXButton btnDelete = new JFXButton("Close");
    btnDelete.getStyleClass().add("deleteButton");

    buttons1.add(btnEdit);
    buttons1.add(btnDelete);

    JFXButton btnCancel = new JFXButton("Cancel");
    btnCancel.getStyleClass().add("deleteButton");

    JFXButton btnSave = new JFXButton("Save");
    btnSave.getStyleClass().add("editButton");

    JFXButton btnMarkComp = new JFXButton("Mark Complete");
    btnMarkComp.getStyleClass().add("markCompButton");

    buttons2.add(btnSave);
    buttons2.add(btnCancel);
    buttons2.add(btnMarkComp);

    addButtonHandlers(btnEdit, btnSave, btnCancel, btnDelete, btnMarkComp);

    recordPane.setGraphic(iconView);
    recordPane.setText(
        record.getFieldAsString("Type")
            + "\t\t"
            + "Status: "
            + record.getFieldAsString("Complete")
            + "\t\t"
            + "Assignee: "
            + record.getFieldAsString("Assigned"));

    recordPane.setAlignment(Pos.CENTER_LEFT);
    recordPane.setStyle("-fx-min-height: 65; -fx-font: system; -fx-font: bold; -fx-font-size: 15");
    recordPane.setExpanded(false);

    VBox contentVbox = new VBox();

    HBox idHbox = new HBox();
    idHbox.setId("idHBox");
    Label id = new Label("ID");
    JFXTextField idTxt = new JFXTextField(record.getFieldAsString("id"));
    idTxt.setEditable(false);
    idHbox.getChildren().addAll(id, idTxt);
    contentVbox.getChildren().add(idHbox);
    idHbox.setAlignment(Pos.CENTER_LEFT);
    HBox.setMargin(id, new Insets(0, 0, 0, 20));
    HBox.setMargin(idTxt, new Insets(0, 0, 0, 80));
    ((Label) idHbox.getChildren().get(0))
        .widthProperty()
        .addListener(
            new ChangeListener<Number>() {
              @Override
              public void changed(
                  ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                HBox.setMargin(
                    idHbox.getChildren().get(1),
                    new Insets(0, 0, 0, 120 - ((Label) idHbox.getChildren().get(0)).getWidth()));
              }
            });

    HBox locationHbox = new HBox();
    locationHbox.setId("locationHBox");
    Label location = new Label("Location");
    JFXTextField locationTxt = new JFXTextField(record.getFieldAsString("Location"));
    locationTxt.setEditable(false);
    locationHbox.getChildren().addAll(location, locationTxt);
    contentVbox.getChildren().add(locationHbox);
    locationHbox.setAlignment(Pos.CENTER_LEFT);
    HBox.setMargin(locationHbox.getChildren().get(0), new Insets(0, 0, 0, 20));
    HBox.setMargin(locationHbox.getChildren().get(1), new Insets(0, 0, 0, 80));
    ((Label) locationHbox.getChildren().get(0))
        .widthProperty()
        .addListener(
            new ChangeListener<Number>() {
              @Override
              public void changed(
                  ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                HBox.setMargin(
                    locationHbox.getChildren().get(1),
                    new Insets(
                        0, 0, 0, 120 - ((Label) locationHbox.getChildren().get(0)).getWidth()));
              }
            });

    HBox assignedHbox = new HBox();
    assignedHbox.setId("assignedHBox");
    Label assigned = new Label("Assigned");
    assigned.setMinWidth(80);
    JFXTextField assignedTxt = new JFXTextField(record.getFieldAsString("Assigned"));
    assigned.setId("AssignedTxt");
    assignedTxt.setEditable(false);
    JFXComboBox<String> employeeList = new JFXComboBox<>();
    employeeList.setStyle("-fx-text-fill: #000");
    employeeList.setVisible(false);
    employeeList.setId("employeeBox");
    assignedHbox.getChildren().addAll(assigned, assignedTxt, employeeList);
    contentVbox.getChildren().add(assignedHbox);
    assignedHbox.setAlignment(Pos.CENTER_LEFT);
    HBox.setMargin(assigned, new Insets(0, 0, 0, 20));
    HBox.setMargin(assignedTxt, new Insets(0, 0, 0, 80));
    HBox.setMargin(employeeList, new Insets(0, 0, 0, 80));
    ((Label) assignedHbox.getChildren().get(0))
        .widthProperty()
        .addListener(
            new ChangeListener<Number>() {
              @Override
              public void changed(
                  ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                HBox.setMargin(
                    assignedHbox.getChildren().get(1),
                    new Insets(
                        0, 0, 0, 120 - ((Label) assignedHbox.getChildren().get(0)).getWidth()));
                HBox.setMargin(
                    assignedHbox.getChildren().get(2),
                    new Insets(
                        0, 0, 0, 120 - ((Label) assignedHbox.getChildren().get(0)).getWidth()));
              }
            });

    for (String s : record.getKeys()) {
      if (!s.equals("id")
          && !s.equals("Location")
          && !s.equals("Assigned")
          && !s.equals("Description")) {
        // Do we want to make the key into something more visibly appealing?
        // Ex: urgency -> Urgent:
        // s = parseKey(s);
        // Add s to list of editable stuff so we know what we can edit
        editIDs.add(s);
        HBox newH = new HBox();
        newH.setId(s + "HBox");
        Label newLabel = new Label(StringUtils.capitalize(s));
        newLabel.setId(s + "Label");
        JFXTextField newText = new JFXTextField(record.getFieldAsString(s));
        newText.setEditable(false);
        newText.setId(s + "Text");
        newH.getChildren().addAll(newLabel, newText);
        newH.setAlignment(Pos.CENTER_LEFT);
        HBox.setMargin(newH.getChildren().get(0), new Insets(0, 0, 0, 20));
        HBox.setMargin(newH.getChildren().get(1), new Insets(0, 0, 0, 80));
        ((Label) newH.getChildren().get(0))
            .widthProperty()
            .addListener(
                new ChangeListener<Number>() {
                  @Override
                  public void changed(
                      ObservableValue<? extends Number> observable,
                      Number oldValue,
                      Number newValue) {
                    HBox.setMargin(
                        newH.getChildren().get(1),
                        new Insets(0, 0, 0, 120 - ((Label) newH.getChildren().get(0)).getWidth()));
                  }
                });
        contentVbox.getChildren().add(newH);
      }
    }

    HBox buttonHbox = new HBox();
    buttonHbox.setId("buttonHBox");
    buttonHbox.setVisible(true);
    buttonHbox.getChildren().addAll(buttons1);
    HBox.setMargin(buttonHbox.getChildren().get(0), new Insets(15, 0, 0, 20));
    HBox.setMargin(buttonHbox.getChildren().get(1), new Insets(15, 0, 0, 20));

    contentVbox.getChildren().addAll(buttonHbox);

    // set margins for all children in content vbox
    for (Node node : contentVbox.getChildren()) {
      VBox.setMargin(node, new Insets(10, 0, 0, 0));
    }

    recordPane.setContent(contentVbox);

    // TODO remove this when done debugging
    recordPane.setOnMouseClicked(
        new EventHandler<MouseEvent>() {
          @Override
          public void handle(MouseEvent event) {
            System.out.println("Yeah");
          }
        });
  }

  private void addButtonHandlers(
      JFXButton edit, JFXButton save, JFXButton cancel, JFXButton delete, JFXButton markComp) {
    edit.setOnAction(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {

            toggleButtons();
            for (String s : editIDs) {
              FilteredList boxList =
                  ((VBox)
                          ((HBox) ((AnchorPane) recordPane.getContent()).getChildren().get(0))
                              .getChildren()
                              .get(0))
                      .getChildren()
                      .filtered(node -> node.getId().equals(s + "HBox"));

              if (boxList.size() > 0) {
                HBox temp = (HBox) boxList.get(0);
                for (Node node : temp.getChildren()) {
                  if (node instanceof JFXTextField) {
                    ((JFXTextField) node).setEditable(true);
                  }
                }
              }
            }

            ObservableList<Node> assignedNodes =
                ((HBox)
                        ((VBox)
                                ((HBox) ((AnchorPane) recordPane.getContent()).getChildren().get(0))
                                    .getChildren()
                                    .get(0))
                            .getChildren()
                            .filtered(node -> node.getId().equals("assignedHBox"))
                            .get(0))
                    .getChildren();
            Node tempNode = assignedNodes.get(1);
            assignedNodes.remove(1);
            assignedNodes.add(tempNode);

            assignedNodes.get(1).setVisible(true);
            assignedNodes.get(2).setVisible(false);

            ((ComboBox<String>) assignedNodes.get(1))
                .setItems(
                    db.getStringList(
                        "EMPLOYEES",
                        new String[] {"lastname", "firstname"},
                        new String[] {"requestTypes"},
                        new String[] {record.getFieldAsString("Type")},
                        new String[] {"lastname"}));
            ((ComboBox<String>) assignedNodes.get(1))
                .getSelectionModel()
                .select(((JFXTextField) assignedNodes.get(2)).getText());

            ////////// -------------------------------------------------
            ObservableList<Node> entranceNodes =
                ((VBox)
                        ((HBox) ((AnchorPane) recordPane.getContent()).getChildren().get(0))
                            .getChildren()
                            .get(0))
                    .getChildren()
                    .filtered(node -> node.getId().equals("entryHBox"));

            if (entranceNodes.size() == 0) {
              return;
            }

            entranceNodes = ((HBox) entranceNodes.get(0)).getChildren();

            Node tempEntryNode = entranceNodes.get(1);
            entranceNodes.remove(1);
            entranceNodes.add(tempEntryNode);

            entranceNodes.get(1).setVisible(true);
            entranceNodes.get(2).setVisible(false);

            ObservableList<String> entryChoices =
                FXCollections.observableArrayList("Atrium Main Entrance", "Emergency Entrance");
            ((ComboBox<String>) entranceNodes.get(1)).setItems(entryChoices);
            ((ComboBox<String>) entranceNodes.get(1))
                .getSelectionModel()
                .select(((JFXTextField) entranceNodes.get(2)).getText());
            /////////// ------------------------------------------------------
          }
        });
    if (markComp != null)
      markComp.setOnAction(
          new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
              ObservableList<Node> list =
                  ((VBox)
                          ((HBox) ((AnchorPane) recordPane.getContent()).getChildren().get(0))
                              .getChildren()
                              .get(0))
                      .getChildren()
                      .filtered(node -> node.getId().equals("CompleteHBox"));
              if (list.size() > 0) {
                ((JFXTextField) ((HBox) list.get(0)).getChildren().get(1)).setText("completed");
              }
            }
          });

    save.setOnAction(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            Record temp = new Record();

            toggleButtons();

            ObservableList<Node> assignedNodes =
                ((HBox)
                        ((VBox)
                                ((HBox) ((AnchorPane) recordPane.getContent()).getChildren().get(0))
                                    .getChildren()
                                    .get(0))
                            .getChildren()
                            .filtered(node -> node.getId().equals("assignedHBox"))
                            .get(0))
                    .getChildren();
            Node tempNode = assignedNodes.get(1);
            assignedNodes.remove(1);
            assignedNodes.add(tempNode);

            assignedNodes.get(1).setVisible(true);
            assignedNodes.get(2).setVisible(false);

            ((JFXTextField) assignedNodes.get(1))
                .setText(((ComboBox<String>) assignedNodes.get(2)).getValue());
            record.addProperty("assigned", ((ComboBox<String>) assignedNodes.get(2)).getValue());

            ///////////////// ------------------------
            ObservableList<Node> entryNodes =
                ((VBox)
                        ((HBox) ((AnchorPane) recordPane.getContent()).getChildren().get(0))
                            .getChildren()
                            .get(0))
                    .getChildren()
                    .filtered(node -> node.getId().equals("entryHBox"));

            if (entryNodes.size() != 0) {
              entryNodes = ((HBox) entryNodes.get(0)).getChildren();

              Node tempEntryNode = entryNodes.get(1);
              entryNodes.remove(1);
              entryNodes.add(tempEntryNode);

              entryNodes.get(1).setVisible(true);
              entryNodes.get(2).setVisible(false);

              ((JFXTextField) entryNodes.get(1))
                  .setText(((ComboBox<String>) entryNodes.get(2)).getValue());
              record.addProperty("entry", ((ComboBox<String>) entryNodes.get(2)).getValue());
            }
            ////////////////// ----------------------------

            // Create new record from current data
            for (Node node :
                ((VBox)
                        ((HBox) ((AnchorPane) recordPane.getContent()).getChildren().get(0))
                            .getChildren()
                            .get(0))
                    .getChildren()) {
              HBox hbox = (HBox) node;
              if (hbox.getChildren().get(0) instanceof Label
                  && hbox.getChildren().get(1) instanceof JFXTextField) {
                if (((Label) hbox.getChildren().get(0)).getText().equals("ID")) {
                  temp.addProperty("id", ((JFXTextField) hbox.getChildren().get(1)).getText());
                }
                if (((JFXTextField) hbox.getChildren().get(1)).getText() != null) {
                  temp.addProperty(
                      ((Label) hbox.getChildren().get(0)).getText(),
                      ((JFXTextField) hbox.getChildren().get(1)).getText());
                } else {
                  temp.addProperty(((Label) hbox.getChildren().get(0)).getText(), "");
                }
              }
            }

            // Update the database
            for (String s : record.getKeys()) {
              if (record.getKeys().contains(s) && temp.getKeys().contains(s))
                if (!record.getFieldAsString(s).equals(temp.getFieldAsString(s))) {
                  db.updateRecord("REQUESTS", record, s, temp.getFieldAsString(s));
                }
            }

            // Set stuff to not be editable without resetting values
            for (String s : editIDs) {
              FilteredList boxList =
                  ((VBox)
                          ((HBox) ((AnchorPane) recordPane.getContent()).getChildren().get(0))
                              .getChildren()
                              .get(0))
                      .getChildren()
                      .filtered(node -> node.getId().equals(s + "HBox"));
              System.out.println(boxList);
              if (boxList.size() > 0) {
                HBox tempH = (HBox) boxList.get(0);
                for (Node node : tempH.getChildren()) {
                  if (node instanceof JFXTextField) {
                    ((JFXTextField) node).setEditable(false);
                  }
                }
              }
            }
            if (entryNodes.size() == 0) {
              recordPane.setText(
                  record.getFieldAsString("Type")
                      + "\t\t"
                      + "Status: "
                      + record.getFieldAsString("Complete")
                      + "\t\t"
                      + "Assignee: "
                      + record.getFieldAsString("Assigned"));
            } else {
              if (((ComboBox<String>) entryNodes.get(2)).getValue() == null
                  || !homeState.getEntrance().getValue().equals("")) return;
              homeState.setEntrance(((ComboBox<String>) entryNodes.get(2)).getValue());
              homeState.setCanPathfind(true);
              homeState.refreshNodeList();
              HospitalNode tempEntrance = new HospitalNode();
              tempEntrance.addProperty(
                  "id",
                  ((ComboBox<String>) entryNodes.get(2)).getValue().equals("Atrium Main Entrance")
                      ? "AEXIT00201"
                      : "AEXIT00101");
              pathfindingService.addImpossibleNode(tempEntrance);
              recordPane.setText(
                  record.getFieldAsString("Type")
                      + "\t\t"
                      + "Status: "
                      + record.getFieldAsString("Complete")
                      + "\t\t"
                      + "Reviewer: "
                      + record.getFieldAsString("Assigned")
                      + "\t\t"
                      + "Entrance: "
                      + record.getFieldAsString("entry"));
            }
          }
        });

    cancel.setOnAction(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {

            toggleButtons();
            ObservableList<Node> assignedNodes =
                ((HBox)
                        ((VBox)
                                ((HBox) ((AnchorPane) recordPane.getContent()).getChildren().get(0))
                                    .getChildren()
                                    .get(0))
                            .getChildren()
                            .filtered(node -> node.getId().equals("assignedHBox"))
                            .get(0))
                    .getChildren();
            Node tempNode = assignedNodes.get(1);
            assignedNodes.remove(1);
            assignedNodes.add(tempNode);

            assignedNodes.get(1).setVisible(true);
            assignedNodes.get(2).setVisible(false);

            for (String s : editIDs) {
              FilteredList boxList =
                  ((VBox)
                          ((HBox) ((AnchorPane) recordPane.getContent()).getChildren().get(0))
                              .getChildren()
                              .get(0))
                      .getChildren()
                      .filtered(node -> node.getId().equals(s + "HBox"));
              System.out.println(boxList);
              if (boxList.size() > 0) {
                HBox temp = (HBox) boxList.get(0);
                for (Node node : temp.getChildren()) {
                  if (node instanceof JFXTextField) {
                    ((JFXTextField) node).setEditable(false);
                    ((JFXTextField) node).setText(record.getFieldAsString(s));
                  }
                }
              }
            }
          }
        });

    delete.setOnAction(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            parent.getChildren().remove(recordPane);
            db.deleteRecord("REQUESTS", record);
          }
        });
  }

  // Gets the reference to this that other objects see
  // Use the RecordBox returned by this function instead of this
  public TitledPane getRecordPane() {
    return recordPane;
  }

  // Gets the Icon we want to use based off request type
  private MaterialDesignIcon switchIcon(String type) {
    switch (type) {
      case "Medicine":
        return MaterialDesignIcon.PILL;
      case "Laundry":
        return MaterialDesignIcon.BASKET;
      case "Computer":
        return MaterialDesignIcon.LAPTOP;
      case "Religion":
        return MaterialDesignIcon.CHURCH;
      case "Security":
        return MaterialDesignIcon.ALERT;
      case "Floral":
        return MaterialDesignIcon.FLOWER;
      case "Gift":
        return MaterialDesignIcon.GIFT;
      case "Sanitation":
        return MaterialDesignIcon.BROOM;
      case "Language":
        return MaterialDesignIcon.COMMENT;
      case "Audio/Visual":
        return MaterialDesignIcon.REMOTE;
      default:
        // Default icon should probably be something else if we can't match the type somehow
        return MaterialDesignIcon.COMMENT_QUESTION_OUTLINE;
    }
  }

  private void toggleButtons() {
    HBox temp1 =
        ((HBox)
            ((VBox)
                    ((HBox) ((AnchorPane) recordPane.getContent()).getChildren().get(0))
                        .getChildren()
                        .get(0))
                .getChildren()
                .filtered(node -> node.getId().equals("buttonHBox"))
                .get(0));
    if (flag) {
      temp1.getChildren().removeAll(buttons2);
      temp1.getChildren().addAll(buttons1);
      flag = false;
    } else {
      temp1.getChildren().removeAll(buttons1);
      temp1.getChildren().addAll(buttons2);
      flag = true;
    }
    for (Node node : temp1.getChildren()) {
      HBox.setMargin(node, new Insets(15, 0, 0, 20));
    }
  }
}
