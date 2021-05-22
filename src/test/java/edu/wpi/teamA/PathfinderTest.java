/// *-------------------------*/
/// * DO NOT DELETE THIS TEST */
/// *-------------------------*/

package edu.wpi.teamA;

import edu.wpi.teamA.interfaces.PathGenerator;
import edu.wpi.teamA.modules.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PathfinderTest {

  PathGenerator<HospitalNode> generator;
  Set<HospitalNode> nodes;
  Map<String, Set<String>> connections;
  Graph<HospitalNode> graph;

  @BeforeEach
  public void init() {
    nodes = new HashSet<>();
    connections = new HashMap<>();

    String[] cols = {
      "id", "xcoord", "ycoord", "floor", "building", "nodetype", "longname", "shortname", "team"
    };
    String[][] values = {
      {"A", "0", "0", "1", "a*", "type", "Room A Star", "RA*", "A"},
      {"B", "1", "1", "1", "b*", "type", "Room B Star", "RB*", "A"},
      {"C", "1", "1", "1", "c*", "type", "Room C Star", "RC*", "A"},
      {"D", "1", "2", "1", "d*", "type", "Room D Star", "RD*", "A"},
      {"E", "2", "1", "1", "e*", "type", "Room E Star", "RE*", "A"},
      {"F", "2", "3", "1", "f*", "type", "Room F Star", "RF*", "A"},
      {"G", "0", "2", "1", "g*", "type", "Room G Star", "RG*", "A"},
      {"Z", "3", "3", "1", "z*", "type", "Room Z Star", "RZ*", "A"}
    };

    for (String[] v : values) {
      HashMap<String, String> temp = new HashMap<>();
      int i = 0;
      for (String col : cols) temp.put(col, v[i++]);
      nodes.add(new HospitalNode(temp));
    }

    connections.put("A", Stream.of("B", "C", "D").collect(Collectors.toSet()));
    connections.put("B", Stream.of("A", "C", "E").collect(Collectors.toSet()));
    connections.put("C", Stream.of("A", "B", "E").collect(Collectors.toSet()));
    connections.put("D", Stream.of("A", "E", "G").collect(Collectors.toSet()));
    connections.put("G", Stream.of("D", "F").collect(Collectors.toSet()));
    connections.put("F", Stream.of("G").collect(Collectors.toSet()));
    connections.put("Z", Stream.of("Z").collect(Collectors.toSet()));
    graph = new Graph<>(nodes, connections);
  }

  @Test
  public void testEasyPathAStar() {

    generator =
        new PathGeneratorAStar<>(graph, new ManhattanCostFunction(), new EuclideanCostFunction());

    List<HospitalNode> path = generator.findPath(graph.getNode("A"), graph.getNode("G"), 0);
    String output =
        path.stream().map(HospitalNode::toString).collect(Collectors.toList()).toString();

    Assertions.assertEquals("[Room A Star, Room D Star, Room G Star]", output);
  }

  @Test
  public void testHardPathAStar() {
    generator =
        new PathGeneratorAStar<>(graph, new ManhattanCostFunction(), new EuclideanCostFunction());
    List<HospitalNode> path = generator.findPath(graph.getNode("A"), graph.getNode("F"), 0);
    String output =
        path.stream().map(HospitalNode::toString).collect(Collectors.toList()).toString();

    Assertions.assertEquals("[Room A Star, Room D Star, Room G Star, Room F Star]", output);
  }

  @Test
  public void testSameNodeAStar() {
    generator =
        new PathGeneratorAStar<>(graph, new ManhattanCostFunction(), new EuclideanCostFunction());
    List<HospitalNode> path = generator.findPath(graph.getNode("A"), graph.getNode("A"), 0);
    String output =
        path.stream().map(HospitalNode::toString).collect(Collectors.toList()).toString();

    Assertions.assertEquals("[Room A Star]", output);
  }

  @Test
  public void testEasyPathBFS() {

    generator =
        new PathGeneratorBFS<>(graph, new ManhattanCostFunction(), new EuclideanCostFunction());

    List<HospitalNode> path = generator.findPath(graph.getNode("A"), graph.getNode("G"), 0);
    String output =
        path.stream().map(HospitalNode::toString).collect(Collectors.toList()).toString();

    Assertions.assertEquals("[Room A Star, Room D Star, Room G Star]", output);
  }

  @Test
  public void testHardPathBFS() {
    generator =
        new PathGeneratorBFS<>(graph, new ManhattanCostFunction(), new EuclideanCostFunction());
    List<HospitalNode> path = generator.findPath(graph.getNode("A"), graph.getNode("F"), 0);
    String output =
        path.stream().map(HospitalNode::toString).collect(Collectors.toList()).toString();

    Assertions.assertEquals("[Room A Star, Room D Star, Room G Star, Room F Star]", output);
  }

  @Test
  public void testSameNodeBFS() {
    generator =
        new PathGeneratorBFS<>(graph, new ManhattanCostFunction(), new EuclideanCostFunction());
    List<HospitalNode> path = generator.findPath(graph.getNode("A"), graph.getNode("A"), 0);
    String output =
        path.stream().map(HospitalNode::toString).collect(Collectors.toList()).toString();

    Assertions.assertEquals("[Room A Star]", output);
  }

  @Test
  public void testEasyPathDFS() {

    generator =
        new PathGeneratorDFS<>(graph, new ManhattanCostFunction(), new EuclideanCostFunction());

    List<HospitalNode> path = generator.findPath(graph.getNode("A"), graph.getNode("G"), 0);
    String output =
        path.stream().map(HospitalNode::toString).collect(Collectors.toList()).toString();

    Assertions.assertEquals("[Room A Star, Room D Star, Room G Star]", output);
  }

  @Test
  public void testHardPathDFS() {
    generator =
        new PathGeneratorDFS<>(graph, new ManhattanCostFunction(), new EuclideanCostFunction());
    List<HospitalNode> path = generator.findPath(graph.getNode("A"), graph.getNode("F"), 0);
    String output =
        path.stream().map(HospitalNode::toString).collect(Collectors.toList()).toString();

    Assertions.assertEquals("[Room A Star, Room D Star, Room G Star, Room F Star]", output);
  }

  @Test
  public void testSameNodeDFS() {
    generator =
        new PathGeneratorDFS<>(graph, new ManhattanCostFunction(), new EuclideanCostFunction());
    List<HospitalNode> path = generator.findPath(graph.getNode("A"), graph.getNode("A"), 0);
    String output =
        path.stream().map(HospitalNode::toString).collect(Collectors.toList()).toString();

    Assertions.assertEquals("[Room A Star]", output);
  }
}
