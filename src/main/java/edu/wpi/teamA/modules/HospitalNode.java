package edu.wpi.teamA.modules;

import edu.wpi.teamA.interfaces.Node;
import java.util.HashMap;

public class HospitalNode extends Record implements Node {

  public HospitalNode() {
    super();
  }

  public HospitalNode(HashMap<String, String> data) {
    super(data);
  }

  public HospitalNode(Record record) {
    super(record.data);
  }

  @Override
  public String getId() {
    return data.get("id");
  }

  @Override
  public Integer getXCoord() {
    return Integer.parseInt(data.get("xcoord"));
  }

  @Override
  public Integer getYCoord() {
    return Integer.parseInt(data.get("ycoord"));
  }

  @Override
  public String getType() {
    return data.get("nodetype");
  }

  @Override
  public String toString() {
    return data.get("longname");
  }

  public HospitalNode modifiedClone(int newX, int newY) {
    HashMap<String, String> newData = (HashMap<String, String>) data.clone();
    newData.replace("xcoord", String.valueOf(newX));
    newData.replace("ycoord", String.valueOf(newY));
    return new HospitalNode(newData);
  }
}
