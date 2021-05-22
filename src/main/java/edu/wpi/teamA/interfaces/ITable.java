package edu.wpi.teamA.interfaces;

import edu.wpi.teamA.modules.Record;
import edu.wpi.teamA.modules.Table;
import java.sql.SQLException;
import java.util.List;

public interface ITable {

  boolean addRecord(Record record);

  void delRecord(Record record);

  List<Record> getRecords();

  void delAllRecords();

  void addForeignKey(String col, Table foreignName, String fcol) throws SQLException;

  void updateRecord(Record record, String col, String newVal);
}
