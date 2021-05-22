package edu.wpi.teamA.views;

import com.google.inject.Inject;
import com.jfoenix.controls.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

public class CovidDeathController implements Initializable {

  @Inject MenuController mainController;

  @FXML public AnchorPane covidDeathPane;
  @FXML private JFXButton refreshBtn;
  @FXML private ScrollPane covidDeathScrollPane;
  @FXML private GridPane covidGrid;

  @FXML private Label cDeaths;
  @FXML private Label cHospitalized;
  @FXML private Label cOnVentilatorTotal;

  @FXML private Label cHospitalizedCurr;
  @FXML private Label cOnVentilatorCurr;

  @FXML private Label cNewDeaths;
  @FXML private Label cNewHospitalized;

  @FXML private Label cDateChecked;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    covidDeathScrollPane.setFitToWidth(true);
    covidDeathPane.setVisible(false);
    collectInfo();
  }

  @FXML
  private void collectInfo() {
    String json = null;
    try {
      json = readUrl("https://api.covidtracking.com/v1/us/current.json");

      // info
      String dateChecked = json.split("\"dateChecked\":")[1].split(",")[0];
      cDateChecked.setText(dateChecked);

      // total stats
      int deaths = Integer.parseInt(json.split("\"death\":")[1].split(",")[0]);
      int hospitalized = Integer.parseInt(json.split("\"hospitalized\":")[1].split(",")[0]);
      int onVentilatorTotal =
          Integer.parseInt(json.split("\"onVentilatorCumulative\":")[1].split(",")[0]);
      cDeaths.setText(String.valueOf(deaths));
      cHospitalized.setText(String.valueOf(hospitalized));
      cOnVentilatorTotal.setText(String.valueOf(onVentilatorTotal));

      // current stats
      int hospitalizedcurr =
          Integer.parseInt(json.split("\"hospitalizedCurrently\":")[1].split(",")[0]);
      int onVentilatorCurr =
          Integer.parseInt(json.split("\"onVentilatorCurrently\":")[1].split(",")[0]);
      cHospitalizedCurr.setText(String.valueOf(hospitalizedcurr));
      cOnVentilatorCurr.setText(String.valueOf(onVentilatorCurr));

      // daily stats
      int newDeaths = Integer.parseInt(json.split("\"deathIncrease\":")[1].split(",")[0]);
      int newHospitalized =
          Integer.parseInt(json.split("\"hospitalizedIncrease\":")[1].split(",")[0]);
      cNewDeaths.setText(String.valueOf(newDeaths));
      cNewHospitalized.setText(String.valueOf(newHospitalized));

    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Cannot open api. Try to refresh it in app");
      cDateChecked.setText("error");
      cDeaths.setText("error");
      cHospitalized.setText("error");
      cOnVentilatorTotal.setText("error");
      cHospitalizedCurr.setText("error");
      cOnVentilatorCurr.setText("error");
      cNewDeaths.setText("error");
      cNewHospitalized.setText("error");
      return;
    }
  }

  private static String readUrl(String urlString) throws Exception {
    BufferedReader reader = null;
    try {
      URL url = new URL(urlString);
      reader = new BufferedReader(new InputStreamReader(url.openStream()));
      StringBuffer buffer = new StringBuffer();
      int read;
      char[] chars = new char[1024];
      while ((read = reader.read(chars)) != -1) buffer.append(chars, 0, read);

      return buffer.toString();
    } finally {
      if (reader != null) reader.close();
    }
  }
}
