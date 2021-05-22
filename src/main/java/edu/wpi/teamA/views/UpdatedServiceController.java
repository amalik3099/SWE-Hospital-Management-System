package edu.wpi.teamA.views;

import com.google.inject.Inject;
import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.swing.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UpdatedServiceController implements Initializable {

  //  @FXML private JFXButton btnSanitationRequest;
  //  @FXML private JFXButton btnMedicineRequest;
  //  @FXML private JFXButton btnLaundryRequest;
  //  @FXML private JFXButton btnReligiousRequest;
  //  @FXML private JFXButton btnComputerRequest;
  //  @FXML private JFXButton btnSecurityRequest;
  //  @FXML private JFXButton btnFloralRequest;
  //  @FXML private JFXButton btnGiftRequest;
  //  @FXML private JFXButton btnLangRequest;
  //  @FXML private JFXButton btnAVRequest;
  @FXML private JFXListView<JFXButton> serviceList;
  @FXML private AnchorPane homePane;
  @FXML private StackPane contentPane;
  @FXML private StackPane updatedServicePane;
  @FXML private JFXComboBox<Label> services;
  @FXML private JFXButton submitBtn;
  @FXML private JFXButton cancelBtn;
  @FXML private JFXButton clearBtn;
  @FXML private JFXTextField nameField;
  @FXML private JFXTextField emailField;
  @FXML private JFXToggleButton urgencyBtn;
  @FXML private JFXButton helpBtn;

  @Inject MenuController mainController;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    MaterialDesignIconView medIcon = new MaterialDesignIconView(MaterialDesignIcon.PILL);
    medIcon.setSize("20");
    Label medicine = new Label("Medicine", medIcon);
    medicine.setId("medicinePane");

    MaterialDesignIconView laundryIcon = new MaterialDesignIconView(MaterialDesignIcon.BASKET);
    laundryIcon.setSize("20");
    Label laundry = new Label("Laundry", laundryIcon);
    laundry.setId("laundryPane");

    MaterialDesignIconView computerIcon = new MaterialDesignIconView(MaterialDesignIcon.LAPTOP);
    computerIcon.setSize("20");
    Label computer = new Label("Computer Repair", computerIcon);
    computer.setId("computerPane");

    MaterialDesignIconView religionIcon = new MaterialDesignIconView(MaterialDesignIcon.CHURCH);
    religionIcon.setSize("20");
    Label religion = new Label("Religous", religionIcon);
    religion.setId("religiousPane");

    MaterialDesignIconView securityIcon = new MaterialDesignIconView(MaterialDesignIcon.ALERT);
    securityIcon.setSize("20");
    Label security = new Label("Security", securityIcon);
    security.setId("securityPane");

    MaterialDesignIconView floralIcon = new MaterialDesignIconView(MaterialDesignIcon.FLOWER);
    floralIcon.setSize("20");
    Label floral = new Label("Floral", floralIcon);
    floral.setId("floralPane");

    MaterialDesignIconView giftIcon = new MaterialDesignIconView(MaterialDesignIcon.GIFT);
    giftIcon.setSize("20");
    Label gift = new Label("Gift", giftIcon);
    gift.setId("giftPane");

    MaterialDesignIconView sanitIcon = new MaterialDesignIconView(MaterialDesignIcon.BROOM);
    sanitIcon.setSize("20");
    Label sanitation = new Label("Sanitation", sanitIcon);
    sanitation.setId("sanitationPane");

    MaterialDesignIconView langIcon = new MaterialDesignIconView(MaterialDesignIcon.COMMENT);
    langIcon.setSize("20");
    Label langauge = new Label("Language", langIcon);
    langauge.setId("languagePane");

    MaterialDesignIconView audvisIcon = new MaterialDesignIconView(MaterialDesignIcon.REMOTE);
    audvisIcon.setSize("20");
    Label audvis = new Label("Tech Repair", audvisIcon);
    audvis.setId("avPane");

    services.getItems().add(computer);
    services.getItems().add(floral);
    services.getItems().add(gift);
    services.getItems().add(langauge);
    services.getItems().add(laundry);
    services.getItems().add(medicine);
    services.getItems().add(religion);
    services.getItems().add(sanitation);
    services.getItems().add(security);
    services.getItems().add(audvis);

    updatedServicePane.setVisible(false);
  }

  //  @FXML
  //  private void handleDepthAction(MouseEvent event) {
  //    serviceList.depthProperty().set(2);
  //  }

  @FXML
  private void onAction() {
    swapPanes(services.getValue().getId());
  }

  @FXML
  private void handleHelpBtn() {
    JFXDialog dialogInc = new JFXDialog();
    JFXDialogLayout incContent = new JFXDialogLayout();
    JFXButton btnClose1 = new JFXButton("Close");
    incContent.setHeading(new Text("Service Help Page"));
    incContent.setBody(
        new Text(
            "Select a service and enter the appropriate information. \n"
                + "Please only select the urgency button in truly urgent situations. \n"
                + "This can include: sanitation risks or broken hospital equipment."));
    incContent.setActions(btnClose1);
    dialogInc = new JFXDialog(updatedServicePane, incContent, JFXDialog.DialogTransition.CENTER);
    dialogInc.show();
    JFXDialog finalDialogInc = dialogInc;
    btnClose1.setOnAction(
        new EventHandler<ActionEvent>() {
          @SneakyThrows
          @Override
          public void handle(ActionEvent event) {
            finalDialogInc.close();
          }
        });
  }

  private boolean isUrgencySelected() {
    return !urgencyBtn.isSelected();
  }

  private boolean isNameCompleted() {
    return !nameField.getText().equals("");
  }

  private boolean isEmailCompleted() {
    return !emailField.getText().equals("");
  }

  @FXML
  private void handleUrgencyBtn() {
    // TODO
    // swapPanes("navigation");
  }

  private static boolean isValidEmailAddress(String email) {
    boolean result = true;
    try {
      InternetAddress emailAddr = new InternetAddress(email);
      emailAddr.validate();
    } catch (AddressException ex) {
      result = false;
    }
    System.out.println(result);
    return result;
  }

  private void clear() {
    nameField.setText("");
    emailField.setText("");
    urgencyBtn.setSelected(false);
    //    Label empty = new Label(" ");
    //    services.setValue(empty);
    // clear inner pane
    if (contentPane.getChildren().filtered(node -> node.isVisible()).size() > 0) {
      AnchorPane innerPane =
          (AnchorPane) contentPane.getChildren().filtered(node -> node.isVisible()).get(0);
      ((InnerServiceController) innerPane.getUserData()).clearFields();
    }
  }

  @FXML
  private void handleSubmitAction() throws MessagingException {
    JFXDialog dialogComp = new JFXDialog();
    JFXDialog dialogInc = new JFXDialog();
    JFXDialogLayout incContent = new JFXDialogLayout();
    JFXButton btnClose1 = new JFXButton("Close");
    JFXButton btnClose2 = new JFXButton("Close");
    JFXButton btnClose3 = new JFXButton("Close");
    incContent.setHeading(new Text("Incomplete Service Request"));
    if (isNameCompleted()) {
      if (isEmailCompleted() && isValidEmailAddress(emailField.getText())) {
        if (contentPane.getChildren().filtered(node -> node.isVisible()).size() > 0) {
          AnchorPane innerPane =
              (AnchorPane) contentPane.getChildren().filtered(node -> node.isVisible()).get(0);
          if (((InnerServiceController) innerPane.getUserData())
              .addRequestToDB(nameField.getText(), emailField.getText(), urgencyBtn.isSelected())) {
            clear();
          }
        } else {
          incContent.setBody(
              new Text("The service request is incomplete, please select a service"));
          incContent.setActions(btnClose1);
          dialogInc =
              new JFXDialog(updatedServicePane, incContent, JFXDialog.DialogTransition.CENTER);
          dialogInc.show();
        }
      } else {
        // TODO
        incContent.setBody(
            new Text("The service request is incomplete, please fill out the email field"));
        incContent.setActions(btnClose2);
        dialogInc =
            new JFXDialog(updatedServicePane, incContent, JFXDialog.DialogTransition.CENTER);
        dialogInc.show();
      }
    } else {
      incContent.setBody(
          new Text("The service request is incomplete, please fill out the name field"));
      incContent.setActions(btnClose3);
      dialogInc = new JFXDialog(updatedServicePane, incContent, JFXDialog.DialogTransition.CENTER);
      dialogInc.show();
    }
    JFXDialog finalDialogComp = dialogComp;
    JFXDialog finalDialogInc = dialogInc;
    btnClose1.setOnAction(
        new EventHandler<ActionEvent>() {
          @SneakyThrows
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
  }

  @FXML
  private void handleCancelAction() {
    // TODO
    mainController.swapPanes("pathfindingPane");
  }

  @FXML
  private void handleClearAction() {
    clear();
  }

  public void swapPanes(String paneID) {
    if (contentPane.getChildren().filtered(node -> node.isVisible()).size() > 0) {
      Node child = contentPane.getChildren().filtered(node -> node.isVisible()).get(0);
      child.setVisible(false);
    }
    Node child = contentPane.getChildren().filtered(x -> x.getId().equals(paneID)).get(0);
    child.setVisible(true);
  }
}
