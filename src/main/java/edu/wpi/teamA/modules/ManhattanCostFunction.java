package edu.wpi.teamA.modules;

import edu.wpi.teamA.interfaces.CostFunction;

public class ManhattanCostFunction implements CostFunction<HospitalNode> {

  @Override
  public double computeCost(HospitalNode from, HospitalNode to) {
    return Math.abs(to.getXCoord() - from.getXCoord())
        + Math.abs(to.getYCoord() - from.getYCoord());
  }
}
