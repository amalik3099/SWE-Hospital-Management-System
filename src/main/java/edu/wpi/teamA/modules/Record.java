package edu.wpi.teamA.modules;

import edu.wpi.teamA.interfaces.IRecord;
import java.util.HashMap;
import java.util.Set;

public class Record implements IRecord {
  HashMap<String, String> data;

  public Record() {
    data = new HashMap<>();
  }

  public Record(HashMap<String, String> data) {
    this.data = data;
  }

  public void addProperty(String key, String value) {
    data.put(key, value);
  }

  public String getFieldAsString(String key) {
    return data.get(key);
  }

  public Set<String> getKeys() {
    return data.keySet();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Record)) return false;
    Record record = (Record) o;
    return getFieldAsString("id").equals(record.getFieldAsString("id"));
  }
}
