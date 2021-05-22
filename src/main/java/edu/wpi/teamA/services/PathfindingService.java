package edu.wpi.teamA.services;

import com.google.inject.Inject;
import edu.wpi.teamA.interfaces.PathGenerator;
import edu.wpi.teamA.modules.*;
import java.util.*;
import java.util.stream.Collectors;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PathfindingService {

  private static final int OFFSET_X = 333;
  private static final int OFFSET_Y = 842;

  @Inject DatabaseService db;
  private final Graph<HospitalNode> hospital;
  private PathGenerator<HospitalNode> pathGenerator;
  private BestPathAlgo<HospitalNode> bestPath;

  public PathfindingService() {
    Set<HospitalNode> nodes = new HashSet<>();
    Map<String, Set<String>> connections = new HashMap<>();

    hospital = new Graph<>(nodes, connections);

    // default is A star
    bestPath =
        new PathGeneratorAStar<>(
            hospital, new ManhattanCostFunction(), new EuclideanCostFunction());
    pathGenerator = bestPath;
  }

  public void setAlgorithm(String algorithm) {
    switch (algorithm) {
      default:
        bestPath =
            new PathGeneratorAStar<>(
                hospital, new ManhattanCostFunction(), new EuclideanCostFunction());
        pathGenerator = bestPath;
        break;
      case "DFS":
        pathGenerator =
            new PathGeneratorDFS<>(
                hospital, new ManhattanCostFunction(), new EuclideanCostFunction());
        break;
      case "BFS":
        pathGenerator =
            new PathGeneratorBFS<>(
                hospital, new ManhattanCostFunction(), new EuclideanCostFunction());
        break;
      case "DJSTR":
        bestPath =
            new PathGeneratorDijkstra<>(
                hospital, new ManhattanCostFunction(), new EuclideanCostFunction());
        pathGenerator = bestPath;
    }
  }

  public ArrayList<HospitalNode> findPath(String startId, String endId, int elevator) {
    log.debug("Getting path with ID's: " + startId + ", " + endId);
    log.debug("Hospital Nodes:");
    hospital.getNodes().forEach(n -> log.debug(n.toString()));
    log.debug("Hospital Edges:");
    hospital
        .getAllConnections()
        .forEach((k, v) -> log.debug("Node " + k + " | Connections: " + v.toString()));
    return findPath(hospital.getNode(startId), hospital.getNode(endId), elevator);
  }

  public ArrayList<HospitalNode> findPath(HospitalNode start, HospitalNode end, int elevator) {
    return pathGenerator.findPath(start, end, elevator);
  }

  public void addImpossibleNode(HospitalNode node) {
    log.debug(node.getId());
    pathGenerator.getIllegal().add(node);
  }

  public void removeImpossibleNode(HospitalNode node) {
    pathGenerator.getIllegal().remove(node);
  }

  public void clearImpossibleNodes() {
    pathGenerator.getIllegal().clear();
  }

  public void updateGraph() {
    updateNodes();
    updateEdges();
  }

  public void updateNodes() {
    hospital.updateNodes(
        new HashSet<>(
            db.getItemsNodes().stream().map(HospitalNode::new).collect(Collectors.toList())));
  }

  public void updateEdges() {
    ObservableList<Record> wackEdges = db.getItemsEdges();
    log.debug("Edge list size: " + wackEdges.size());
    Map<String, Set<String>> connections = new HashMap<>();

    wackEdges.forEach(
        x -> {
          Set<String> set =
              connections.getOrDefault(x.getFieldAsString("startnode"), new HashSet<String>());
          set.add(x.getFieldAsString("endnode"));
          connections.put(x.getFieldAsString("startnode"), set);
          set = connections.getOrDefault(x.getFieldAsString("endnode"), new HashSet<String>());
          set.add(x.getFieldAsString("startnode"));
          connections.put(x.getFieldAsString("endnode"), set);
        });

    updateEdges(connections);
  }

  public void updateEdges(Map<String, Set<String>> connections) {
    hospital.updateConnections(connections);
  }

  public Graph<HospitalNode> getGraph() {
    return this.hospital;
  }
}
