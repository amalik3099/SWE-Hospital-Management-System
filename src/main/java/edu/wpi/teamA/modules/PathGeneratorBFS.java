package edu.wpi.teamA.modules;

import edu.wpi.teamA.interfaces.CostFunction;
import edu.wpi.teamA.interfaces.Node;
import edu.wpi.teamA.interfaces.PathGenerator;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PathGeneratorBFS<T extends Node> implements PathGenerator<T> {
  private final Graph<T> graph;
  private final CostFunction<T> nextNodeCostFunction;
  private final CostFunction<T> targetCostFunction;

  public PathGeneratorBFS(
      Graph<T> graph, CostFunction<T> nextNodeCostFunction, CostFunction<T> targetCostFunction) {
    this.graph = graph;
    this.nextNodeCostFunction = nextNodeCostFunction;
    this.targetCostFunction = targetCostFunction;
  }

  @Override
  public ArrayList<HospitalNode> getIllegal() {
    return illegal;
  }

  @Override
  public ArrayList<T> findPath(T from, T to, int elev) {
    log.debug("Attempting to find path from: " + from + " to: " + to);
    Map<T, PathingNode<T>> visitedNodes = new HashMap<>();
    Queue<PathingNode<T>> nextNodes = new ArrayDeque<>();

    PathingNode<T> start =
        new PathingNode<>(from, null, 0d, targetCostFunction.computeCost(from, to));
    nextNodes.add(start);
    visitedNodes.put(from, start);

    while (!nextNodes.isEmpty()) {
      log.debug(
          "Next nodes contains: "
              + nextNodes.stream().map(PathingNode::getCurrent).collect(Collectors.toSet()));
      PathingNode<T> nextNode = nextNodes.poll();

      if (nextNode.getCurrent().equals(to)) {
        log.debug("Destination Found!");

        ArrayList<T> path = new ArrayList<>();
        PathingNode<T> current = nextNode;
        do {
          path.add(0, current.getCurrent());
          current = visitedNodes.get(current.getPrevious());
        } while (current != null);

        log.debug("Path: " + path);
        return path;
      }
      graph
          .getConnections(nextNode.getCurrent())
          .forEach(
              connection -> {
                PathingNode<T> neighborNode = visitedNodes.getOrDefault(connection, null);
                if (neighborNode == null) {
                  if ((elev != 1 || !connection.getType().equals("STAI"))
                      && !illegal.contains(connection)) {
                    neighborNode = new PathingNode<>(connection);
                    neighborNode.setPrevious(nextNode.getCurrent());
                    visitedNodes.put(connection, neighborNode);
                    nextNodes.add(neighborNode);
                  }
                }
              });
    }

    throw new IllegalStateException("No Path Found from " + from + " to " + to);
  }
}
