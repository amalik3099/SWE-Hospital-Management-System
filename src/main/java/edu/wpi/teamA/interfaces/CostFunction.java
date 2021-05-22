package edu.wpi.teamA.interfaces;

public interface CostFunction<T extends Node> {
  double computeCost(T from, T to);
}
