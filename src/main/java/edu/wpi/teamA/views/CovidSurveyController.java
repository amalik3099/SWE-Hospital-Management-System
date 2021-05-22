package edu.wpi.teamA.views;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import edu.wpi.teamA.modules.Record;
import edu.wpi.teamA.services.DatabaseService;
import edu.wpi.teamA.state.HomeState;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class CovidSurveyController implements Initializable {

  @Inject MenuController menuController;
  @Inject DatabaseService db;
  @Inject HomeState homeState;

  @FXML private JFXButton qOneYes;
  @FXML private JFXButton qOneNo;
  @FXML private JFXButton qTwoYes;
  @FXML private JFXButton qTwoNo;
  @FXML private JFXButton qThreeYes;
  @FXML private JFXButton qThreeNo;
  @FXML private JFXButton qFourYes;
  @FXML private JFXButton qFourNo;
  @FXML private JFXButton qFiveYes;
  @FXML private JFXButton qFiveNo;
  @FXML private JFXButton submitButton;
  @FXML private JFXButton cancelButton;
  @FXML private ScrollPane covidScrollPane;
  @FXML private StackPane dialogPane;
  private final int[] surveyResults = new int[5];

  @FXML
  private void qOneYesPress() {
    qOneYes.setStyle(
        "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #f6bd38;-fx-background-color: #002d59; -fx-text-fill: #ffffff");
    qOneNo.setStyle(
        "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #082c5c;-fx-background-color: #ffffff; -fx-text-fill: #000000");
    surveyResults[0] = 1;
  }

  @FXML
  private void qOneNoPress() {
    qOneYes.setStyle(
        "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #082c5c;-fx-background-color: #ffffff; -fx-text-fill: #000000");
    qOneNo.setStyle(
        "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #f6bd38;-fx-background-color: #002d59; -fx-text-fill: #ffffff");
    surveyResults[0] = 2;
  }

  @FXML
  private void qTwoYesPress() {
    qTwoYes.setStyle(
        "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #f6bd38;-fx-background-color: #002d59; -fx-text-fill: #ffffff");
    qTwoNo.setStyle(
        "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #082c5c;-fx-background-color: #ffffff; -fx-text-fill: #000000");
    surveyResults[1] = 1;
  }

  @FXML
  private void qTwoNoPress() {
    qTwoYes.setStyle(
        "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #082c5c;-fx-background-color: #ffffff; -fx-text-fill: #000000");
    qTwoNo.setStyle(
        "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #f6bd38;-fx-background-color: #002d59; -fx-text-fill: #ffffff");
    surveyResults[1] = 2;
  }

  @FXML
  private void qThreeYesPress() {
    qThreeYes.setStyle(
        "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #f6bd38;-fx-background-color: #002d59; -fx-text-fill: #ffffff");
    qThreeNo.setStyle(
        "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #082c5c;-fx-background-color: #ffffff; -fx-text-fill: #000000");
    surveyResults[2] = 1;
  }

  @FXML
  private void qThreeNoPress() {
    qThreeYes.setStyle(
        "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #082c5c;-fx-background-color: #ffffff; -fx-text-fill: #000000");
    qThreeNo.setStyle(
        "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #f6bd38;-fx-background-color: #002d59; -fx-text-fill: #ffffff");
    surveyResults[2] = 2;
  }

  @FXML
  private void qFourYesPress() {
    qFourYes.setStyle(
        "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #f6bd38;-fx-background-color: #002d59; -fx-text-fill: #ffffff");
    qFourNo.setStyle(
        "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #082c5c;-fx-background-color: #ffffff; -fx-text-fill: #000000");
    surveyResults[3] = 1;
  }

  @FXML
  private void qFourNoPress() {
    qFourYes.setStyle(
        "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #082c5c;-fx-background-color: #ffffff; -fx-text-fill: #000000");
    qFourNo.setStyle(
        "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #f6bd38;-fx-background-color: #002d59; -fx-text-fill: #ffffff");
    surveyResults[3] = 2;
  }

  @FXML
  private void qFiveYesPress() {
    qFiveYes.setStyle(
        "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #f6bd38;-fx-background-color: #002d59; -fx-text-fill: #ffffff");
    qFiveNo.setStyle(
        "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #082c5c;-fx-background-color: #ffffff; -fx-text-fill: #000000");
    surveyResults[4] = 1;
  }

  @FXML
  private void qFiveNoPress() {
    qFiveYes.setStyle(
        "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #082c5c;-fx-background-color: #ffffff; -fx-text-fill: #000000");
    qFiveNo.setStyle(
        "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #f6bd38;-fx-background-color: #002d59; -fx-text-fill: #ffffff");
    surveyResults[4] = 2;
  }

  @FXML
  private void cancelPress() {
    homeState.setCurrentPane("pathfindingPane");
    homeState.setSurveyTaken(false);
    clear();
  }

  @FXML
  private void submitPress() {
    if (handleDialog()) {
      // create dialog box for error handling on submit
      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
      LocalDateTime now = LocalDateTime.now();

      Record r = new Record();
      r.addProperty("id", ""); // Gets automatically generated
      r.addProperty("Date", dtf.format(now));
      r.addProperty("Type", "Hospital Entry");
      r.addProperty("Assigned", "");
      r.addProperty("approved", "false");
      r.addProperty("entry", "");
      r.addProperty("Description", getDescription());
      db.addRecord("REQUESTS", r);

      homeState.setSurveyTaken(true);
    }
  }

  private void clear() {
    qOneYes.setStyle(
        "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #082c5c;-fx-background-color: #ffffff; -fx-text-fill: #000000");
    qOneNo.setStyle(
        "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #082c5c;-fx-background-color: #ffffff; -fx-text-fill: #000000");
    qTwoYes.setStyle(
        "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #082c5c;-fx-background-color: #ffffff; -fx-text-fill: #000000");
    qTwoNo.setStyle(
        "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #082c5c;-fx-background-color: #ffffff; -fx-text-fill: #000000");
    qThreeYes.setStyle(
        "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #082c5c;-fx-background-color: #ffffff; -fx-text-fill: #000000");
    qThreeNo.setStyle(
        "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #082c5c;-fx-background-color: #ffffff; -fx-text-fill: #000000");
    qFourYes.setStyle(
        "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #082c5c;-fx-background-color: #ffffff; -fx-text-fill: #000000");
    qFourNo.setStyle(
        "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #082c5c;-fx-background-color: #ffffff; -fx-text-fill: #000000");
    qFiveYes.setStyle(
        "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #082c5c;-fx-background-color: #ffffff; -fx-text-fill: #000000");
    qFiveNo.setStyle(
        "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #082c5c;-fx-background-color: #ffffff; -fx-text-fill: #000000");
  }

  private Boolean isQOneComplete() {
    return surveyResults[0] == 1 || surveyResults[0] == 2;
  }

  private Boolean isQTwoComplete() {
    return surveyResults[1] == 1 || surveyResults[1] == 2;
  }

  private Boolean isQThreeComplete() {
    return surveyResults[2] == 1 || surveyResults[2] == 2;
  }

  private Boolean isQFourComplete() {
    return surveyResults[3] == 1 || surveyResults[3] == 2;
  }

  private Boolean isQFiveComplete() {
    return surveyResults[4] == 1 || surveyResults[4] == 2;
  }

  private String getDescription() {
    String q1 = null, q2 = null, q3 = null, q4 = null, q5 = null;
    if (surveyResults[0] == 1) {
      q1 = "Symptomatic?:Y|";
    } else if (surveyResults[0] == 2) {
      q1 = "Symptomatic?:N|";
    }
    if (surveyResults[1] == 1) {
      q2 = "Had Close Contact?:Y|";
    } else if (surveyResults[1] == 2) {
      q2 = "Had Close Contact?:N|";
    }
    if (surveyResults[2] == 1) {
      q3 = "In Isolation?:Y|";
    } else if (surveyResults[2] == 2) {
      q3 = "In Isolation:N|";
    }
    if (surveyResults[3] == 1) {
      q4 = "Awaiting Test Result?:Y|";
    } else if (surveyResults[3] == 2) {
      q4 = "Awaiting Test Result?:N|";
    }
    if (surveyResults[4] == 1) {
      q5 = "Agrees to Mask + Distance?:Y";
    } else if (surveyResults[4] == 2) {
      q5 = "Agrees to Mask + Distance?:N";
    }
    return q1 + q2 + q3 + q4 + q5;
  }

  private Boolean handleDialog() {
    System.out.println("Handling dialog");
    Boolean check = false;
    JFXDialog dialogComp = new JFXDialog();
    JFXDialog dialogInc = new JFXDialog();
    JFXDialogLayout incContent = new JFXDialogLayout();
    incContent.setHeading(new Text("Incomplete Covid Survey"));
    JFXButton btnClose = new JFXButton("Close");
    JFXButton btnClose1 = new JFXButton("Close");
    JFXButton btnClose2 = new JFXButton("Close");
    JFXButton btnClose3 = new JFXButton("Close");
    JFXButton btnClose4 = new JFXButton("Close");
    JFXButton btnClose5 = new JFXButton("Close");
    if (isQOneComplete()) {
      if (isQTwoComplete()) {
        if (isQThreeComplete()) {
          if (isQFourComplete()) {
            if (isQFiveComplete()) {
              //              JFXDialogLayout recLay = new JFXDialogLayout();
              //              recLay.setHeading(new Text("Recording Needed!"));
              //              VBox content = new VBox(20);
              //              TextFlow tf = new TextFlow();
              //              Text t1 = new Text("Please take a breath and hold the vowel sound ");
              //              Text t2 = new Text("'Ahhh'");
              //              t2.setStyle("-fx-font-style: italic");
              //              Text t3 = new Text("like in the word ‘father’.\n" + "Please hold that
              // sound for ");
              //              Text t4 = new Text("6 seconds");
              //              t4.setUnderline(true);
              //              t4.setStyle("-fx-font-weight: bold");
              //              Text t5 = new Text("or until you run out of breath.");
              //              tf.getChildren().addAll(t1, t2, t3, t4, t5);
              //              content.getChildren().add(tf);
              //              JFXButton recordButton =
              //                  new JFXButton(
              //                      "Record", new
              // MaterialDesignIconView(MaterialDesignIcon.RECORD_REC));
              //              recordButton.setBackground(
              //                  new Background(new BackgroundFill(Color.FIREBRICK, null, null)));
              //              recordButton.setRipplerFill(Color.WHITESMOKE);
              //              content.getChildren().add(recordButton);
              //              content.setAlignment(Pos.TOP_CENTER);
              //              recLay.setBody(content);
              //              recordButton.setOnAction(
              //                  e -> {
              //                    System.out.println("RECORD BUTTON PRESSED");
              //                    recordButton.setDisable(true);
              //                    JavaSoundRecorder javaSoundRecorder = new JavaSoundRecorder();
              //                    Thread thread = new Thread(javaSoundRecorder);
              //                    Thread timer =
              //                        new Thread(
              //                            new Runnable() {
              //                              @Override
              //                              public void run() {
              //                                try {
              //                                  Thread.sleep(JavaSoundRecorder.RECORD_TIME);
              //                                } catch (InterruptedException ex) {
              //                                  ex.printStackTrace();
              //                                }
              //                                recordButton.setDisable(false);
              //                                javaSoundRecorder.finish();
              //                              }
              //                            });
              //                    timer.start();
              //                    thread.start();
              //                  });
              //              JFXButton closeRecording = new JFXButton("Close");
              //              recLay.setActions(closeRecording);
              //              JFXDialog recordDialog =
              //                  new JFXDialog(dialogPane, recLay,
              // JFXDialog.DialogTransition.CENTER);
              //              closeRecording.setOnAction(e -> recordDialog.close());
              //              recordDialog.show();

              JFXDialogLayout compContent = new JFXDialogLayout();
              compContent.setHeading(new Text("Covid Survey Completed"));
              compContent.setBody(
                  new Text(
                      "Thank you for completing your covid screening survey! \n"
                          + "Your request is currently being reviewed \n"
                          + "Please wait in your car until you have been approved to enter the hospital"));
              compContent.setActions(btnClose);
              dialogComp =
                  new JFXDialog(dialogPane, compContent, JFXDialog.DialogTransition.CENTER);
              dialogComp.show();
              //              clear(); // on successful submission clear form
              //              menuController.swapPanes("pathfindingPane");
              check = true;
            } else {
              incContent.setBody(
                  new Text(
                      "Please indicate if you agree to wear facial masks and practice "
                          + "social distancing while at the hospital"));
              incContent.setActions(btnClose1);
              dialogInc = new JFXDialog(dialogPane, incContent, JFXDialog.DialogTransition.CENTER);
              dialogInc.show();
            }
          } else {
            incContent.setBody(
                new Text("Please indicate if you are currently awaiting covid test results"));
            incContent.setActions(btnClose2);
            dialogInc = new JFXDialog(dialogPane, incContent, JFXDialog.DialogTransition.CENTER);
            dialogInc.show();
          }
        } else {
          incContent.setBody(
              new Text(
                  "Please indicate if you are currently isolating due to "
                      + "possible covid-19 exposure"));
          incContent.setActions(btnClose3);
          dialogInc = new JFXDialog(dialogPane, incContent, JFXDialog.DialogTransition.CENTER);
          dialogInc.show();
        }
      } else {
        incContent.setBody(
            new Text(
                "Please indicate if you have recently been in close contact "
                    + "with someone who has tested positive for covid-19"));
        incContent.setActions(btnClose4);
        dialogInc = new JFXDialog(dialogPane, incContent, JFXDialog.DialogTransition.CENTER);
        dialogInc.show();
      }
    } else {
      incContent.setBody(
          new Text(
              "Please indicate if have experienced any of the covid symptoms listed \n"
                  + "in the last 48 hours"));
      incContent.setActions(btnClose5);
      dialogInc = new JFXDialog(dialogPane, incContent, JFXDialog.DialogTransition.CENTER);
      dialogInc.show();
    }
    JFXDialog finalDialogComp = dialogComp;
    JFXDialog finalDialogInc = dialogInc;
    btnClose.setOnAction(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            finalDialogComp.close();
            clear(); // on successful submission clear form
            menuController.swapPanes("pathfindingPane");
          }
        });
    btnClose1.setOnAction(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            finalDialogInc.close();
          }
        });
    btnClose2.setOnAction(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            finalDialogInc.close();
          }
        });
    btnClose3.setOnAction(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            finalDialogInc.close();
          }
        });
    btnClose4.setOnAction(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            finalDialogInc.close();
          }
        });
    btnClose5.setOnAction(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            finalDialogInc.close();
          }
        });
    return check;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    covidScrollPane.setFitToWidth(true);
    for (int i = 0; i < 5; i++) {
      surveyResults[i] = 0;
    }
  }
}
