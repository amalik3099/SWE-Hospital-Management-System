package edu.wpi.teamA;

import edu.wpi.teamA.modules.HospitalNode;
import edu.wpi.teamA.modules.IncrementTable;
import edu.wpi.teamA.modules.Record;
import edu.wpi.teamA.modules.Table;
import edu.wpi.teamA.services.DatabaseService;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DatabaseServiceTest {
  DatabaseService db;

  // Database tests to check that construction with empty database loads in default csvs
  //  @Test
  //  public void testDatabaseServiceConstruction() {
  //    // Clear the database
  //    db = new DatabaseService();
  //    db.dropTable("CSVNODES");
  //    db.dropTable("CSVEDGES");
  //
  //    db = new DatabaseService();
  //    Assertions.assertEquals(34, db.getTableDataNode().size());
  //  }
  //
  //  @Test
  //  public void testDatabaseServiceConstruction2() {
  //    // Clear the database
  //    db = new DatabaseService();
  //    db.dropTable("CSVNODES");
  //    db.dropTable("CSVEDGES");
  //
  //    db = new DatabaseService();
  //    Assertions.assertEquals(252, db.getTableDataEdge().size());
  //  }
  //
  //  @Test
  //  public void testDatabaseServiceUpdateTable() {
  //    db = new DatabaseService();
  //
  //    db.updateTable(
  //        "INSERT INTO CSVNODES VALUES('test', '1', '2', '3', 'test', 'test', 'test', 'test',
  // 'A')");
  //    Assertions.assertEquals(35, db.getTableDataNode().size());
  //    db.dropTable("CSVNODES");
  //    db.dropTable("CSVEDGES");
  //  }
  //
  //  @Test
  //  public void testDatabaseServiceUpdateTable2() throws SQLException {
  //    db = new DatabaseService();
  //    Boolean check = false;
  //    ObservableList<HospitalNode> checkList;
  //    String query =
  //        "INSERT INTO CSVNODES VALUES('test1', '5', '6', 'G', 'test1', 'test1', 'test1', 'test1',
  // 'A')";
  //    db.updateTable(query);
  //    checkList = db.getTableDataNode();
  //    for (HospitalNode nr : checkList) {
  //      if (nr.getNodeID().equals("test1")
  //          && nr.getFloor().equals("G")
  //          && nr.getBuilding().equals("test1")
  //          && nr.getNodeType().equals("test1")
  //          && nr.getLongName().equals("test1")
  //          && nr.getShortName().equals("test1")
  //          && nr.getTeamAssigned().equals("A")) {
  //        check = true;
  //      }
  //    }
  //    Assertions.assertEquals(true, check);
  //    db.dropTable("CSVNODES");
  //    db.dropTable("CSVEDGES");
  //  }
  //
  //  @Test
  //  public void testDatabaseServiceUpdateTable3() throws SQLException {
  //    db = new DatabaseService();
  //    Boolean check = false;
  //    ObservableList<DatabaseService.NodeRecord> checkList;
  //    String query =
  //        "INSERT INTO CSVNODES VALUES('test2', '5', '6', 'G', 'test2', 'test2', 'test2', 'test2',
  // 'A')";
  //    db.updateTable(query);
  //    checkList = db.getTableDataNode();
  //    for (DatabaseService.NodeRecord nr : checkList) {
  //      if (nr.getNodeID().equals("test2")
  //          && nr.getFloor().equals("G")
  //          && nr.getBuilding().equals("test2")
  //          && nr.getNodeType().equals("test2")
  //          && nr.getLongName().equals("test2")
  //          && nr.getShortName().equals("test2")
  //          && nr.getTeamAssigned().equals("A")) {
  //        check = true;
  //      }
  //    }
  //    Assertions.assertEquals(true, check);
  //    db.dropTable("CSVNODES");
  //    db.dropTable("CSVEDGES");
  //  }
  //
  //  @Test
  //  public void testInitSR() throws SQLException {
  //    db = new DatabaseService();
  //    ResultSet rs;
  //    boolean check = false;
  //    //    db.initServiceRequest();
  //    //    String createQuery =
  //    //        "CREATE TABLE SERVICEREQUEST("
  //    //            + "CURR_LOC varchar(255), "
  //    //            + "END_LOC varchar(255), "
  //    //            + "URGENCY varchar(5), "
  //    //            + "TRANSPORT varchar(255), "
  //    //            + "SR_DESC varchar(255))";
  //    //    db.updateTable(createQuery);
  //    String query = "INSERT INTO SERVICEREQUEST VALUES('test1', 'test2', '1', 'ambulance',
  // 'hello')";
  //    boolean checkUpdate = db.updateTable(query);
  //    System.out.println("Update: " + checkUpdate);
  //    String query1 = "SELECT * FROM SERVICEREQUEST";
  //    rs = db.queryTable(query1);
  //    if (rs.next()) {
  //      check = true;
  //    }
  //    Assertions.assertTrue(check);
  //    db.dropTable("SERVICEREQUEST");
  //  }
  //
  //  @Test
  //  public void testReloadFromList() {
  //    db = new DatabaseService();
  //    ObservableList<HospitalNode> basicList = FXCollections.observableArrayList();
  //    HashMap<String, String> blankNodeMap = new HashMap<>();
  //    blankNodeMap.put("nodeid", "test");
  //    blankNodeMap.put("floor", "1");
  //    blankNodeMap.put("building", "Faulkner");
  //    blankNodeMap.put("xcoord", "23");
  //    blankNodeMap.put("ycoord", "23");
  //    blankNodeMap.put("longname", "long");
  //    blankNodeMap.put("shortname", "short");
  //    blankNodeMap.put("nodetype", "that");
  //    blankNodeMap.put("team", "A");
  //    HospitalNode newNode = new HospitalNode(blankNodeMap);
  //    basicList.add(newNode);
  //    db.setItemsNodes(basicList);
  //
  //    db.reloadListData("CSVNODES");
  //  }
  Connection connection;
  String[] nodeCols = {
    "id", "xcoord", "ycoord", "floor", "building", "nodetype", "longname", "shortname", "team"
  };
  String[] nodeAttributes = {
    "AEXIT00101", "1586", "970", "G", "Faulkner", "EXIT", "Atrium Main Entrance", "Emain", "A"
  };
  HospitalNode exNode;

  Table nodesTable, edgesTable;
  IncrementTable employeeMain;

  @BeforeEach
  public void init() throws ClassNotFoundException {
    Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    Statement stmt;

    try {
      connection = DriverManager.getConnection("jdbc:derby:ProjC_DB;create=true;");
      stmt = connection.createStatement();
    } catch (SQLException e) {
      System.out.println("Connection Failed");
      return;
    }

    try {
      stmt.execute("DROP TABLE EDGES");
    } catch (SQLException e) {
      System.out.println("Edges table does not exist");
    }

    try {
      stmt.execute("DROP TABLE NODES");
    } catch (SQLException e) {
      System.out.println("Nodes table does not exist");
    }

    try {
      stmt.execute("DROP TABLE EMPLOYEES");
    } catch (SQLException e) {
      System.out.println("Employees table does not exist");
    }

    edgesTable = new Table(connection, "EDGES", new String[] {"id", "startnode", "endnode"});
    nodesTable = new Table(connection, "NODES", nodeCols);
    employeeMain =
        new IncrementTable(
            connection,
            "EMPLOYEES",
            new String[] {"id", "firstname", "lastname", "email", "skills"});

    HashMap<String, String> map = new HashMap<>();
    int i = 0;
    for (String s : nodeCols) map.put(s, nodeAttributes[i++]);
    exNode = new HospitalNode(map);
  }

  @Test
  public void testNodesTable() throws SQLException {
    nodesTable.addRecord(exNode);
  }

  @Test
  public void testNodesQuery() throws SQLException {
    nodesTable.addRecord(exNode);
    List<HospitalNode> l =
        nodesTable.getRecords().stream().map(HospitalNode::new).collect(Collectors.toList());
    Assertions.assertEquals("AEXIT00101", l.get(0).getId());
  }

  @Test
  public void testForeignKey() throws SQLException {
    edgesTable.addForeignKey("startnode", nodesTable, "id");
    edgesTable.addForeignKey("endnode", nodesTable, "id");

    HashMap<String, String> reTest = new HashMap<>();
    for (int i = 0; i < 3; i++)
      reTest.put(
          new String[] {"id", "startnode", "endnode"}[i],
          new String[] {"AWALK00201_AEXIT00101", "AWALK00201", "AEXIT00101"}[i]);

    HashMap<String, String> noTest = new HashMap<>();
    for (int i = 0; i < nodeCols.length; i++)
      noTest.put(
          nodeCols[i],
          new String[] {
                "AWALK00201", "1199", "1006", "G", "Faulkner", "WALK", "Garage Split", "Wsplit", "A"
              }
              [i]);

    nodesTable.addRecord(exNode);
    nodesTable.addRecord(new HospitalNode(noTest));
    edgesTable.addRecord(new Record(reTest));
    nodesTable.delRecord(exNode);
  }

  @Test
  public void testEmployeeTable() throws SQLException {
    employeeMain.insertValues(
        new String[] {"Mayank", "Govilla", "mgovilla@gmail.com", "sanitation,security"});
    Assertions.assertEquals("1", employeeMain.getRecords().get(0).getFieldAsString("id"));
  }
}
