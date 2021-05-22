package edu.wpi.teamA.modules;

import java.sql.*;

public class IncrementTable extends Table {

  public IncrementTable(Connection connection, String tableName, String[] columnNames) {
    super(connection, tableName, columnNames);
  }

  /**
   * Initialize the table with tableName, some columns. Uses sql create table command. Includes a
   * counter for the first column that is assumed to be ID
   *
   * @param tableName String for table name
   * @param columns Array of strings for columns to enter. Each will be assumed varchar(255)
   * @throws SQLException On table already exists
   */
  @Override
  void initTable(String tableName, String[] columns) throws SQLException {
    String query =
        "CREATE TABLE "
            + tableName
            + "("
            + columns[0]
            + " INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), ";

    for (int i = 1; i < columns.length; i++) {
      query += columns[i];
      query += " varchar(255), ";
    }

    query += "PRIMARY KEY(ID))";
    stmt.execute(query);
  }

  /**
   * Add a new record into the table
   *
   * @param record Instance of record to insert. ID value is ignored
   * @return true if successfully added, false otherwise
   */
  @Override
  public boolean addRecord(Record record) {
    String query = "INSERT INTO " + tableName + " VALUES(default, ";

    for (int n = 1; n < columnNames.length - 1; n++)
      query += "'" + record.getFieldAsString(columnNames[n]) + "', ";

    query += "'" + record.getFieldAsString(columnNames[columnNames.length - 1]) + "')";

    boolean flag = execute(query);

    try {
      ResultSet rs = stmt.getGeneratedKeys();
      rs.next();
      String s = rs.getString(1);
      record.addProperty("id", s);
      items.add(record);
    } catch (SQLException e) {
    }

    return flag;
  }

  /**
   * Method to easily insert a record for testing
   *
   * @param attributes must match the length of columnNames
   */
  @Override
  public void insertValues(String[] attributes) {
    assert attributes.length == columnNames.length - 1;

    String query = "INSERT INTO " + tableName + " VALUES(";
    query += "DEFAULT, ";
    for (int n = 0; n < attributes.length - 1; n++) {
      query += "'" + attributes[n] + "', ";
    }
    query += "'" + attributes[attributes.length - 1] + "')";

    execute(query);
  }

  /** Run update table based on changes to the items list */
  @Override
  public void updateTable() {
    delAllRecords();
    addRecords(items);
    items = getRecords();
  }

  @Override
  public void updateRecord(Record record, String col, String newVal) {
    String query =
        "UPDATE "
            + tableName
            + " SET "
            + col
            + " = '"
            + newVal
            + "' WHERE ID = "
            + record.getFieldAsString("id");
    execute(query);
    record.addProperty(col, newVal);
  }

  @Override
  public void delRecord(Record record) {
    String query = "DELETE FROM " + tableName + " WHERE ID = " + record.getFieldAsString("id");
    execute(query);
    items.removeIf(r -> r.getFieldAsString("id").equals(record.getFieldAsString("id")));
  }

  /**
   * Wrapper for stmt.execute with associated try catch
   *
   * @param query SQL Formatted query
   */
  private boolean execute(String query) {
    try {
      stmt.execute(query, Statement.RETURN_GENERATED_KEYS);
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return false;
  }
}
