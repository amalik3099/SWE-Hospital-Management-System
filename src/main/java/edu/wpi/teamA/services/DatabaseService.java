package edu.wpi.teamA.services;

import edu.wpi.teamA.modules.IncrementTable;
import edu.wpi.teamA.modules.Record;
import edu.wpi.teamA.modules.Table;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DatabaseService {
  private Connection connection;
  private final String[] tables = {
    "NODES", "EDGES", "EMPLOYEES", "REQUESTS", "USERS", "APPOINTMENTS"
  };
  private final String[][] metadata = {
    {"id", "xcoord", "ycoord", "floor", "building", "nodetype", "longname", "shortname", "team"},
    {"id", "startnode", "endnode"},
    {
      "id",
      "lastname",
      "firstname",
      "occupation",
      "department",
      "age",
      "sex",
      "email",
      "requestTypes"
    },
    {
      "id",
      "Urgency",
      "Date",
      "Type",
      "Assigned",
      "Complete",
      "Description",
      "Email",
      "Name",
      "Location"
    },
    {"id", "email", "password", "authlevel", "name"},
    {"id", "email", "date", "time", "reason", "location", "staff", "description"}
  };

  private DatabaseMetaData metaDB;

  HashMap<String, String[]> tableColumns;
  HashMap<String, Table> tableInstances;
  Table nodesTable, edgesTable, usersTable;
  IncrementTable employeesTable, srTable, appointTable;

  DatabaseService() throws ClassNotFoundException {
    Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    // Create a database or connect if one already exists with the same name
    boolean flagFK = true;
    try {
      connection = DriverManager.getConnection("jdbc:derby:BWHDB;create=true;");
    } catch (SQLException e) {
      System.out.println("Connection failed");
    }

    tableColumns = new HashMap<>();
    tableInstances = new HashMap<>();

    int i = 0;
    for (String s : tables) tableColumns.put(s, metadata[i++]);

    nodesTable = new Table(connection, "NODES", metadata[0]);
    edgesTable = new Table(connection, "EDGES", metadata[1]);

    edgesTable.addForeignKey("startnode", nodesTable, "id");
    edgesTable.addForeignKey("endnode", nodesTable, "id");

    employeesTable = new IncrementTable(connection, "EMPLOYEES", metadata[2]);
    srTable = new IncrementTable(connection, "REQUESTS", metadata[3]);

    usersTable = new Table(connection, "USERS", metadata[4]);

    appointTable = new IncrementTable(connection, "APPOINTMENTS", metadata[5]);

    tableInstances.put(tables[0], nodesTable);
    tableInstances.put(tables[1], edgesTable);
    tableInstances.put(tables[2], employeesTable);
    tableInstances.put(tables[3], srTable);
    tableInstances.put(tables[4], usersTable);
    tableInstances.put(tables[5], appointTable);

    loadDefaultData();
  }

  /**
   * Upload a given CSV file to the table denoted by tableName TODO: Check if the file is valid CSV
   *
   * @param tableName valid table name
   * @param file file object pointing to a CSV file
   */
  public void loadCSV(String tableName, File file) {
    FileReader fileReader = null;
    BufferedReader csvReader = null;

    try {
      fileReader = new FileReader(file);
      csvReader = new BufferedReader(fileReader);
      loadCSV(tableInstances.get(tableName), csvReader);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void loadCSV(String tableName, InputStream inputStream) {
    try {
      InputStreamReader ir = new InputStreamReader(inputStream);
      BufferedReader br = new BufferedReader(ir);
      loadCSV(tableInstances.get(tableName), br);
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
  }

  private void loadCSV(Table table, BufferedReader csvReader) {
    String line = null;

    try {
      // TODO: Add check to make sure that the heading matches column names
      csvReader.readLine(); // First line of CSV is column names, we don't want to insert that
      line = csvReader.readLine();
    } catch (IOException e) {
    }

    try {
      // Remove all the items in the list currently
      //      table.getItems().removeAll(table.getItems());
      ArrayList<Record> temp = new ArrayList<>();
      while (line != null) {
        // Get the values in the line in an array
        String[] lineSplit = line.split(",");

        // Create the attributes of the record
        HashMap<String, String> attributes = new HashMap<>();
        int i = 0;
        for (String s : tableColumns.get(table.getTableName())) attributes.put(s, lineSplit[i++]);

        Record r = new Record(attributes);

        // Add the record to the table and the list
        temp.add(r);

        line = csvReader.readLine();
      }

      table.addRecords(temp);

    } catch (IOException e) {
    }
  }

  /**
   * Function to Save from a table to a CSV formatted file in csvFile
   *
   * @param tableName name of the table
   * @param csvFile file object to write to
   * @throws IOException
   */
  public void saveCSV(String tableName, File csvFile) throws IOException {
    FileWriter writer = new FileWriter(csvFile);

    // Header line for the CSV
    String line = "";
    for (int i = 0; i < tableColumns.get(tableName).length - 1; i++)
      line += tableColumns.get(tableName)[i] + ",";

    line += tableColumns.get(tableName)[tableColumns.get(tableName).length - 1] + "\n";
    writer.write(line);

    // Add the records as strings
    line = "";
    for (Record r : tableInstances.get(tableName).getRecords()) {
      for (int i = 0; i < tableColumns.get(tableName).length - 1; i++)
        line += r.getFieldAsString(tableColumns.get(tableName)[i]) + ",";

      line +=
          r.getFieldAsString(tableColumns.get(tableName)[tableColumns.get(tableName).length - 1])
              + "\n";
      writer.write(line);
      line = "";
    }

    writer.close();
  }

  // Private function to update the table
  private void reloadListData(Table table) {
    table.updateTable();
  }

  public void reloadSR() {
    reloadListData(srTable);
  }

  //  public void reloadNodes() {
  //    reloadListData(nodesTable);
  //    reloadListData(edgesTable);
  //  }
  //
  //  public void reloadEdges() {
  //    reloadListData(edgesTable);
  //  }
  //
  //  public void reloadEmployees() {
  //    reloadListData(employeesTable);
  //  }

  /** Getters for lists */
  public ObservableList<Record> getItemsNodes() {
    return nodesTable.getItems();
  }

  public ObservableList<Record> getItemsEdges() {
    return edgesTable.getItems();
  }

  public ObservableList<Record> getItemsEmployees() {
    return employeesTable.getItems();
  }

  public ObservableList<Record> getItemsSR() {
    return srTable.getItems();
  }

  public ObservableList<Record> getItemsAppoints(String email) {
    return appointTable
        .getItems()
        .filtered(record -> record.getFieldAsString("email").equals(email));
  }

  // Private function to get locally saved example data as CSV
  private void loadDefaultData() {
    if (nodesTable.getItems().size() == 0
        && edgesTable.getItems().size() == 0
        && employeesTable.getItems().size() == 0) {
      loadCSV(
          "NODES", this.getClass().getResourceAsStream("/edu/wpi/teamA/assets/data/MapANodes.csv"));
      loadCSV(
          "EDGES", this.getClass().getResourceAsStream("/edu/wpi/teamA/assets/data/MapAEdges.csv"));
      loadCSV(
          "EMPLOYEES",
          this.getClass().getResourceAsStream("/edu/wpi/teamA/assets/data/Employees - Sheet1.csv"));
      loadCSV("USERS", this.getClass().getResourceAsStream("/edu/wpi/teamA/assets/data/Users.csv"));
      loadCSV(
          "APPOINTMENTS",
          this.getClass().getResourceAsStream("/edu/wpi/teamA/assets/data/Appointments.csv"));
    }
  }

  /**
   * Update a record in the table denoted by tableName
   *
   * @param tableName valid name of a table
   * @param r Instance of a record to be changed
   * @param sCol column name of attribute change
   * @param newValue new value of the attribute
   */
  public void updateRecord(String tableName, Record r, String sCol, String newValue) {
    tableInstances.get(tableName).updateRecord(r, sCol, newValue);
  }

  /**
   * Delete a record from the table
   *
   * @param tableName valid name of a table
   * @param r Instance of record to be deleted
   */
  public void deleteRecord(String tableName, Record r) {
    tableInstances.get(tableName).delRecord(r);
  }

  /**
   * Add a record to the table
   *
   * @param tableName valid name of a table
   * @param r Instance of record to be added
   */
  public void addRecord(String tableName, Record r) {
    tableInstances.get(tableName).addRecord(r);
    tableInstances.get(tableName).getRecords();
  }

  /**
   * Method to switch to and from edit mode
   *
   * @param save True if we want to commit otherwise rollback
   * @return true if commit/rollback was successful otherwise false
   */
  public boolean toggleEditMode(Boolean save) {
    boolean retFlag = true;
    try {
      if (!connection.getAutoCommit()) {

        if (save) {
          try {
            connection.commit();
          } catch (SQLException e) {
            System.out.println("Failed to save, undoing changes");
            connection.rollback();
            retFlag = false;
          }
        } else {
          connection.rollback();
        }
      }

      if (connection.getAutoCommit()) {
        connection.setAutoCommit(false);
      } else {
        connection.setAutoCommit(true);
      }
    } catch (SQLException e) {
      System.out.println("Autocommit status unavailable");
      retFlag = false;
    }

    return retFlag;
  }

  public static void shutdown() {
    try {
      DriverManager.getConnection("jdbc:derby:BWHDB;shutdown=true;");
    } catch (SQLException e) {
    }
  }

  /**
   * Generates a list of strings, where each string represents a record returned by the SELECT
   * statement produced by {@link Table#genSelectStatement(String[], String[], String[], String[])}
   *
   * @param table name of the table in the database to query
   * @param sCols param for genSelectStatement
   * @param wCols param for genSelectStatement
   * @param like param for genSelectStatement
   * @param oCols param for genSelectStatement
   * @return ObservableList of Strings for use by javafx objects as options
   * @see Table#genSelectStatement(String[], String[], String[], String[])
   */
  public ObservableList<String> getStringList(
      String table, String[] sCols, String[] wCols, String[] like, String[] oCols) {
    ObservableList<String> stringList = FXCollections.observableArrayList();
    Table t = tableInstances.get(table);
    ResultSet rs = t.genSelectStatement(sCols, wCols, like, oCols);
    try {
      int count = rs.getMetaData().getColumnCount();
      if (count > 0) {
        while (rs.next()) {
          String item = rs.getString(1);
          for (int i = 1; i < count; i++) {
            item += ", " + rs.getString(i + 1);
          }
          stringList.add(item);
        }
      }
    } catch (SQLException e) {
    }

    return stringList;
  }
}
