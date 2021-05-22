package edu.wpi.teamA.modules;

import edu.wpi.teamA.interfaces.Node;
import lombok.Data;

@Data
public class PathingNode<T extends Node> implements Comparable<PathingNode> {

  private final T current;
  private T previous;
  private double pathCost; // Cost from start Node to current PathingNode
  private double heuristicCost; // Cost from PathingNode to final Node

  public PathingNode(T current) {
    this(current, null, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
  }

  public PathingNode(T current, T previous, Double routeCost, double estimatedScore) {
    this.current = current;
    this.previous = previous;
    this.pathCost = routeCost;
    this.heuristicCost = estimatedScore;
  }

  public double getTotalCost() {
    return this.pathCost + this.heuristicCost;
  }

  @Override
  public int compareTo(PathingNode node) {
    return Double.compare(this.getTotalCost(), node.getTotalCost());
  }

  protected boolean canEqual(final Object other) {
    return other instanceof PathingNode;
  }
}
