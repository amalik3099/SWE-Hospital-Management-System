package edu.wpi.teamA.modules;

import edu.wpi.teamA.interfaces.CostFunction;
import edu.wpi.teamA.interfaces.Node;
import edu.wpi.teamA.interfaces.PathGenerator;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BestPathAlgo<T extends Node> implements PathGenerator<T> {
  private final Graph<T> graph;
  private final CostFunction<T> nextNodeCostFunction;
  private final CostFunction<T> targetCostFunction;
  Map<T, PathingNode<T>> visitedNodes;
  Queue<PathingNode> nextNodes = new PriorityQueue<>();

  abstract void searchPath(PathingNode<T> nextNode, T to, int elev);

  abstract ArrayList<T> createPath(PathingNode<T> nextNode);

  public BestPathAlgo(
      Graph<T> graph, CostFunction<T> nextNodeCostFunction, CostFunction<T> targetCostFunction) {
    this.graph = graph;
    this.nextNodeCostFunction = nextNodeCostFunction;
    this.targetCostFunction = targetCostFunction;
    this.visitedNodes = new HashMap<>();
    this.nextNodes = new PriorityQueue<>();
  }

  public Graph<T> getGraph() {
    return graph;
  }

  public CostFunction<T> getNextNodeCostFunction() {
    return nextNodeCostFunction;
  }

  public CostFunction<T> getTargetCostFunction() {
    return targetCostFunction;
  }

  @Override
  public ArrayList<T> findPath(T from, T to, int elev) {
    log.debug("Attempting to find path from: " + from + " to: " + to);
    this.visitedNodes = new HashMap<>();
    this.nextNodes = new PriorityQueue<>();
    PathingNode<T> start =
        new PathingNode<>(from, null, 0d, getTargetCostFunction().computeCost(from, to));
    this.nextNodes.add(start);
    this.visitedNodes.put(from, start);

    while (!this.nextNodes.isEmpty()) {
      log.debug(
          "Next nodes contains: "
              + this.nextNodes.stream().map(PathingNode::getCurrent).collect(Collectors.toSet()));
      PathingNode<T> nextNode = this.nextNodes.poll();

      if (nextNode.getCurrent().equals(to)) {
        return createPath(nextNode);
      }

      searchPath(nextNode, to, elev);
    }
    throw new IllegalStateException("No Path Found from " + from + " to " + to);
  }

  public Map<T, PathingNode<T>> getVisitedNodes() {
    return visitedNodes;
  }

  public void setVisitedNodes(Map<T, PathingNode<T>> visitedNodes) {
    this.visitedNodes = visitedNodes;
  }

  public Queue<PathingNode> getNextNodes() {
    return nextNodes;
  }

  public void setNextNodes(Queue<PathingNode> nextNodes) {
    this.nextNodes = nextNodes;
  }
}
