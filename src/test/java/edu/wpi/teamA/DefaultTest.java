/*-------------------------*/
/* DO NOT DELETE THIS TEST */
/*-------------------------*/

package edu.wpi.teamA;

import edu.wpi.teamA.interfaces.CostFunction;
import edu.wpi.teamA.modules.EuclideanCostFunction;
import edu.wpi.teamA.modules.HospitalNode;
import edu.wpi.teamA.modules.ManhattanCostFunction;

public class DefaultTest {

  CostFunction<HospitalNode> ManhattanCostFunction = new ManhattanCostFunction();
  CostFunction<HospitalNode> EuclideanCostFunction = new EuclideanCostFunction();
  HospitalNode costTestNode1 = new HospitalNode();
  HospitalNode costTestNode2 = new HospitalNode();
  HospitalNode costTestNode3 = new HospitalNode();

  //  @Test
  //  public void manhattanCostTest1() {
  //    Assertions.assertEquals(ManhattanCostFunction.computeCost(costTestNode1, costTestNode2),
  // 9d);
  //  }
  //
  //  @Test
  //  public void manhattanCostTestSameLocation() {
  //    Assertions.assertEquals(ManhattanCostFunction.computeCost(costTestNode1, costTestNode3),
  // 0d);
  //  }
  //
  //  @Test
  //  public void euclideanCostFunction() {
  //    Assertions.assertEquals(
  //        EuclideanCostFunction.computeCost(costTestNode1, costTestNode2), Math.sqrt(41));
  //  }
  //
  //  @Test
  //  public void euclideanCostFunctionSameLocation() {
  //    Assertions.assertEquals(EuclideanCostFunction.computeCost(costTestNode1, costTestNode3),
  // 0d);
  //  }

  //  @Override
  //  public void start(Stage stage) throws Exception {
  //    HomeViewController homeViewController = new HomeViewController();
  //    FXMLLoader loader = new FXMLLoader(getClass().getResource("views/HomeView.fxml"));
  //    loader.setControllerFactory(
  //        controllerClass -> {
  //          if (controllerClass.equals(EmergencyController.class)) {
  //            return new FXMLLoader(
  //                getClass().getResource("views/InternalEmergencyRoute.fxml"));
  //          } else if (controllerClass.equals(ExtTransportController.class)) {
  //            return new
  // FXMLLoader(getClass().getResource("views/ExternalTransportREQ.fxml"));
  //          } else if (controllerClass.equals(InternalTransportReqController.class)) {
  //            return new
  // FXMLLoader(getClass().getResource("views/InternalTransportReq.fxml"));
  //          } else if (controllerClass.equals(MapEdgesTableController.class)) {
  //            return new FXMLLoader(getClass().getResource("views/MapEdgesTable.fxml"));
  //          } else if (controllerClass.equals(MapNodesTableController.class)) {
  //            return new FXMLLoader(getClass().getResource("views/MapNodesTable.fxml"));
  //          } else if (controllerClass.equals(PathfindingController.class)) {
  //            return new FXMLLoader(getClass().getResource("views/PathfindingView.fxml"));
  //          } else if (controllerClass.equals(SanitationController.class)) {
  //            return new FXMLLoader(getClass().getResource("views/madiSanitation.fxml"));
  //          } else {
  //            return new FXMLLoader(getClass().getResource("views/HomeView.fxml"));
  //          }
  //        });
  //    Parent root = loader.load();
  //    Scene scene = new Scene(root);
  //    stage.setScene(scene);
  //    stage.setAlwaysOnTop(true);
  //    stage.show();
  //  }
  //  @BeforeAll
  //  public static void setup() throws Exception {
  //    if (Boolean.getBoolean("headless")) {
  //      System.setProperty("testfx.robot", "glass");
  //      System.setProperty("testfx.headless", "true");
  //      System.setProperty("prism.order", "sw");
  //      System.setProperty("prism.text", "t2k");
  //    }
  //    FxToolkit.registerPrimaryStage();
  //    FxToolkit.setupApplication(App.class);
  //  }
  //
  //  @AfterAll
  //  public static void cleanup() {}
  //
  //  @Test
  //  public void testButton() {
  //      HomeViewController controller;
  //      controller
  //  }
  //
  //  }
}
