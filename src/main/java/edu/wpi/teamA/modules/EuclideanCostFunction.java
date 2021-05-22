package edu.wpi.teamA.modules;

import edu.wpi.teamA.interfaces.CostFunction;

public class EuclideanCostFunction implements CostFunction<HospitalNode> {

  @Override
  public double computeCost(HospitalNode from, HospitalNode to) {
    return Math.sqrt(
        Math.pow(to.getXCoord() - from.getXCoord(), 2)
            + Math.pow(to.getYCoord() - from.getYCoord(), 2));
  }
}
