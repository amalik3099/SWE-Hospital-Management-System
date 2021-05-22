package edu.wpi.teamA.state;

import com.google.inject.Inject;
import edu.wpi.teamA.services.DatabaseService;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;

@Data
public class HomeState {
  private SimpleStringProperty entrance;
  private ObservableList<String> nodeList = FXCollections.observableArrayList();
  @Inject DatabaseService db;

  private SimpleBooleanProperty canPathfind;
  private SimpleStringProperty currentPane;
  private SimpleBooleanProperty isMobile;

  private SimpleBooleanProperty surveyTaken;

  public HomeState() {
    entrance = new SimpleStringProperty("");
    canPathfind = new SimpleBooleanProperty(false);
    isMobile = new SimpleBooleanProperty(false);
    surveyTaken = new SimpleBooleanProperty(false);
    currentPane = new SimpleStringProperty("");
  }

  public SimpleStringProperty getEntrance() {
    return entrance;
  }

  public SimpleBooleanProperty getCanPathFind() {
    return canPathfind;
  }

  public void setEntrance(String entrance) {
    this.entrance.set(entrance);
  }

  public void setCanPathfind(Boolean canPathfind) {
    this.canPathfind.set(canPathfind);
  }

  public String getCurrentPane() {
    return currentPane.get();
  }

  public SimpleStringProperty currentPaneProperty() {
    return currentPane;
  }

  public void setCurrentPane(String currentPane) {
    this.currentPane.set(currentPane);
  }

  public boolean isMobile() {
    return isMobile.get();
  }

  public SimpleBooleanProperty isMobileProperty() {
    return isMobile;
  }

  public void setIsMobile(boolean isMobile) {
    this.isMobile.set(isMobile);
  }

  public boolean isSurveyTaken() {
    return surveyTaken.get();
  }

  public SimpleBooleanProperty surveyTakenProperty() {
    return surveyTaken;
  }

  public void setSurveyTaken(boolean surveyTaken) {
    this.surveyTaken.set(surveyTaken);
  }

  public void refreshNodeList() {
    nodeList.clear();
    if (this.entrance.get().equals("Atrium Main Entrance")) {
      setCanPathfind(true);
      nodeList.addAll(
          db.getItemsNodes().stream()
              .filter(x -> !"STAIHALLELEVWALKHIDN".contains(x.getFieldAsString("nodetype")))
              .filter(x -> !"AEXIT00201".contains(x.getFieldAsString("id")))
              .map(x -> x.getFieldAsString("longname"))
              .collect(Collectors.toList()));
    } else if (this.entrance.get().equals("Emergency Entrance")) {
      setCanPathfind(true);
      nodeList.addAll(
          db.getItemsNodes().stream()
              .filter(x -> !"STAIHALLELEVWALKHIDN".contains(x.getFieldAsString("nodetype")))
              .filter(x -> !"AEXIT00101".contains(x.getFieldAsString("id")))
              .map(x -> x.getFieldAsString("longname"))
              .collect(Collectors.toList()));
    } else {
      nodeList.addAll(
          db.getItemsNodes().stream()
              .filter(x -> !"STAIHALLELEVWALKHIDN".contains(x.getFieldAsString("nodetype")))
              .map(x -> x.getFieldAsString("longname"))
              .collect(Collectors.toList()));
    }
    System.out.println("Refreshed Nodes");
  }

  public ObservableList<String> getNodeList() {
    return this.nodeList;
  }
}
