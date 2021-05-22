package edu.wpi.teamA.modules;

import edu.wpi.teamA.interfaces.ITable;
import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Simple Table class for Relations that have String:String relationships. Multivalued Relationships
 * are not supported
 */
public class Table implements ITable {
  String tableName;
  String[] columnNames;
  HashSet<Table> referencedBy;
  ObservableList<Record> items;
  Statement stmt;

  public Table(Connection connection, String tableName, String[] columnNames) {
    this.tableName = tableName;
    this.columnNames = columnNames;
    items = FXCollections.observableArrayList();
    referencedBy = new HashSet<>();
    try {
      stmt = connection.createStatement();
      initTable(tableName, columnNames);
    } catch (SQLException e) {
      if (stmt == null) System.out.println("Statement could not be created");
      else {
        System.out.println("Table already exists");
        items = getRecords();
      }
    }
  }

  /**
   * Initialize the table with tableName, some columns. Uses sql create table command
   *
   * @param tableName String for table name
   * @param columns Array of strings for columns to enter. Each will be assumed varchar(255)
   * @throws SQLException On table already exists
   */
  void initTable(String tableName, String[] columns) throws SQLException {
    String query = "CREATE TABLE " + tableName + "(";
    for (String s : columns) {
      query += s;
      query += " varchar(255), ";
    }
    query += "PRIMARY KEY(ID))";
    stmt.execute(query);
  }

  /**
   * Get all the records form the table
   *
   * @return Observable list of records
   */
  @Override
  public ObservableList<Record> getRecords() {
    items.removeAll(items);

    try {
      ResultSet results = stmt.executeQuery("SELECT * FROM " + tableName);
      while (results.next()) {
        HashMap<String, String> attributes = new HashMap<>();
        for (String columnName : columnNames)
          attributes.put(columnName, results.getString(columnName));

        items.add(new Record(attributes));
      }
    } catch (SQLException e) {
      e.printStackTrace();
      System.out.println("OOF!");
    }

    return items;
  }

  /** Delete all records from the table */
  @Override
  public void delAllRecords() {
    if (execute("DELETE FROM " + tableName)) referencedBy.forEach(Table::getRecords);
    items.removeAll(items);
  }

  /**
   * Delete a single record from the table
   *
   * @param record Instance of the record (only uses the id)
   */
  @Override
  public void delRecord(Record record) {
    String query =
        "DELETE FROM " + tableName + " WHERE ID = '" + record.getFieldAsString("id") + "'";
    if (execute(query)) referencedBy.forEach(Table::getRecords);
    items.removeIf(r -> r.getFieldAsString("id").equals(record.getFieldAsString("id")));
  }

  /**
   * Add a new record into the table
   *
   * @param record Instance of record to insert
   * @return true if successfully added, false otherwise
   */
  @Override
  public boolean addRecord(Record record) {
    String query = "INSERT INTO " + tableName + " VALUES(";

    for (int n = 0; n < columnNames.length - 1; n++) {
      query += "'" + record.getFieldAsString(columnNames[n]) + "', ";
    }
    query += "'" + record.getFieldAsString(columnNames[columnNames.length - 1]) + "')";
    if (execute(query)) {
      items.add(record);
      return true;
    }
    return false;
  }

  /**
   * Try to add all the records in the given list, if any cannot be added, it is removed from the
   * list
   *
   * @param ls List of records to add
   */
  public void addRecords(List<Record> ls) {
    ls.removeIf(r -> !addRecord(r));
  }

  /** Run update table based on changes to the items list */
  public void updateTable() {
    delAllRecords();
    addRecords(items);
  }

  /**
   * Method to easily insert a record for testing
   *
   * @param attributes must match the length of columnNames
   */
  protected void insertValues(String[] attributes) {
    assert attributes.length == columnNames.length;
    HashMap<String, String> temp = new HashMap<>();
    int i = 0;
    for (String c : columnNames) temp.put(c, attributes[i++]);

    addRecord(new Record(temp));
  }

  /**
   * Adds a foreign key constraint to this table
   *
   * @param col Which column from this table is referring to another table
   * @param foreignName Table instance of the foreign table
   * @param fcol The column in the foreign table that is being referenced
   */
  @Override
  public void addForeignKey(String col, Table foreignName, String fcol) {
    String query =
        String.format(
            "ALTER TABLE %s ADD CONSTRAINT FK_%s_%s FOREIGN KEY (%s) REFERENCES %s(%s) ON DELETE CASCADE ON UPDATE NO ACTION",
            tableName, foreignName.tableName, col, col, foreignName.tableName, fcol);

    execute(query);
    foreignName.referencedBy.add(this);
  }

  /**
   * Update a record in the table and then updates the record
   *
   * @param record Instance to update
   * @param col Column name
   * @param newVal New value to enter
   */
  @Override
  public void updateRecord(Record record, String col, String newVal) {
    String query =
        "UPDATE "
            + tableName
            + " SET "
            + col
            + " = '"
            + newVal
            + "' WHERE ID = '"
            + record.getFieldAsString("id")
            + "'";
    if (execute(query)) record.addProperty(col, newVal);
  }

  public String getTableName() {
    return this.tableName;
  }

  public ObservableList<Record> getItems() {
    return items;
  }

  /**
   * Generates an SQL SELECT statement using SELECT, FROM, WHERE, LIKE, and ORDER BY Do not give
   * columns of type INT or columns which contain INTs as String for the where or like components,
   * or you may select extra records. All params except table can be null or empty arrays.
   *
   * @param sCols String Array of Columns in database to be selected. If null or empty all columns
   *     are selected.
   * @param wCols String Array of Columns to be filtered using WHERE.
   * @param like String Array of Values to look for in columns specified by wCols.
   * @param oCols String Array of Columns used to sort the list.
   * @return SQL SELECT statement as String.
   */
  public ResultSet genSelectStatement(
      String[] sCols, String[] wCols, String[] like, String[] oCols) {
    String query = "SELECT ";
    int i = 0;

    // Add columns we want to select, or all(*) if none specified
    if (sCols != null && sCols.length > 0) {
      query += sCols[i];

      for (i = 1; i < sCols.length; i++) {
        query += ", " + sCols[i];
      }

      i = 0;
    } else {
      query += "*";
    }

    // Add table to select from
    query += " FROM " + tableName;

    // Add conditions to filter list if there are any given
    if (wCols != null && wCols.length > 0 && like != null && like.length > 0) {
      query += " WHERE " + wCols[i] + " LIKE  '%" + like[i] + "%'";

      for (i = 1; i < wCols.length && i < like.length; i++) {
        query += " AND " + wCols[i] + " LIKE  '%" + like[i] + "%'";
      }

      i = 0;
    }

    // Add columns to sort by, if any
    if (oCols != null && oCols.length > 0) {
      query += " ORDER BY " + oCols[i];

      for (i = 1; i < oCols.length; i++) {
        query += ", " + oCols[i];
      }
    }

    return executeQuery(query);
  }

  /**
   * Wrapper for stmt.execute with associated try catch
   *
   * @param query SQL Formatted query
   */
  private boolean execute(String query) {
    try {
      stmt.execute(query);
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return false;
  }

  private ResultSet executeQuery(String query) {
    try {
      ResultSet rs = stmt.executeQuery(query);
      return rs;
    } catch (SQLException e) {
      // e.printStackTrace();
    }

    return null;
  }
}
