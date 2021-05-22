package edu.wpi.teamA.modules;

import edu.wpi.teamA.interfaces.CostFunction;
import edu.wpi.teamA.interfaces.Node;
import edu.wpi.teamA.interfaces.PathGenerator;
import java.util.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PathGeneratorAStar<T extends Node> extends BestPathAlgo<T>
    implements PathGenerator<T> {

  public PathGeneratorAStar(
      Graph<T> graph, CostFunction<T> nextNodeCostFunction, CostFunction<T> targetCostFunction) {
    super(graph, nextNodeCostFunction, targetCostFunction);
  }

  @Override
  void searchPath(PathingNode<T> nextNode, T to, int elev) {
    getGraph()
        .getConnections(nextNode.getCurrent())
        .forEach(
            connection -> {
              double newCost =
                  nextNode.getPathCost()
                      + getNextNodeCostFunction().computeCost(nextNode.getCurrent(), connection);
              if (elev == 1) {
                newCost =
                    nextNode.getCurrent().getType().equals("STAI")
                        ? Double.POSITIVE_INFINITY
                        : newCost;
              }

              if (illegal.contains(nextNode.getCurrent())) {
                log.debug("Illegal contains: " + nextNode.getCurrent().toString());
                newCost = Double.POSITIVE_INFINITY;
              }

              PathingNode<T> neighborNode =
                  getVisitedNodes().getOrDefault(connection, new PathingNode<>(connection));
              getVisitedNodes().put(connection, neighborNode);

              if (neighborNode.getPathCost() > newCost) {
                neighborNode.setPrevious(nextNode.getCurrent());
                neighborNode.setPathCost(newCost);
                neighborNode.setHeuristicCost(
                    newCost + getTargetCostFunction().computeCost(connection, to));
                getNextNodes().add(neighborNode);
                log.debug(
                    "Found Lower Cost Node: "
                        + neighborNode
                        + " Cost: "
                        + neighborNode.getPathCost()
                        + neighborNode.getHeuristicCost());
              }
            });
  }

  @Override
  ArrayList<T> createPath(PathingNode<T> nextNode) {
    log.debug("Destination Found!");

    ArrayList<T> path = new ArrayList<>();
    PathingNode<T> current = nextNode;
    do {
      path.add(0, current.getCurrent());
      current = this.visitedNodes.get(current.getPrevious());
    } while (current != null);

    log.debug("Path: " + path);
    return path;
  }

  @Override
  public ArrayList<HospitalNode> getIllegal() {
    return illegal;
  }
}
