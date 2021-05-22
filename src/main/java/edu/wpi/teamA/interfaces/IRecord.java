package edu.wpi.teamA.interfaces;

public interface IRecord {
  void addProperty(String key, String value);

  String getFieldAsString(String key);
}
