package edu.wpi.teamA.modules;

import edu.wpi.teamA.interfaces.Node;

public class Edit<T extends Node> {

  private final Node before;
  private final Node after;
  private final javafx.scene.Node object;
  private final boolean isEdge, isDelete;

  public Edit(T before, T after, javafx.scene.Node object) {
    this(before, after, object, false, false);
  }

  public Edit(T before, T after, javafx.scene.Node object, boolean isEdge, boolean isDelete) {
    this.isEdge = isEdge;
    this.before = before;
    this.after = after;
    this.object = object;
    this.isDelete = isDelete;
  }

  public boolean isDelete() {
    return this.isDelete;
  }

  public boolean isEdge() {
    return this.isEdge;
  }

  public Node getAfter() {
    return after;
  }

  public Node getBefore() {
    return before;
  }

  public javafx.scene.Node getObject() {
    return object;
  }
}
