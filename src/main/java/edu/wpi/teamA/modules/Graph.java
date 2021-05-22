package edu.wpi.teamA.modules;

import edu.wpi.teamA.interfaces.Node;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Graph<T extends Node> {
  private Set<T> nodes;
  private Map<String, Set<String>> connections;

  public Graph(Set<T> nodes, Map<String, Set<String>> connections) {
    this.nodes = nodes;
    this.connections = connections;
  }

  // TODO: Modify these setters to support how value editing works in app
  public void updateNodes(Set<T> nodes) {
    this.nodes = nodes;
  }

  public void updateConnections(Map<String, Set<String>> connections) {
    this.connections = connections;
  }

  public T getNode(String nodeID) {
    return nodes.stream()
        .filter(node -> node.getId().equals(nodeID))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Invalid Node ID"));
  }

  public Set<T> getNodes() {
    return this.nodes;
  }

  public Map<String, Set<String>> getAllConnections() {
    return this.connections;
  }

  public Set<T> getConnections(T node) {
    return connections.getOrDefault(node.getId(), Collections.emptySet()).stream()
        .map(this::getNode)
        .collect(Collectors.toSet());
    // Node -> nodeID -> Set of connections (String) from Map -> for each in Set, call getNode() ->
    // set of Nodes
  }
}
