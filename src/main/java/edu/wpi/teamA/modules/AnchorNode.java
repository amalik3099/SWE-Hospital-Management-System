package edu.wpi.teamA.modules;

import com.jfoenix.controls.JFXListView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.util.ArrayList;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

public class AnchorNode extends Circle {

  private final DoubleProperty x, y;
  private final double initialX, initialY;
  private final HospitalNode node;
  private boolean dragged;
  private final SimpleBooleanProperty edited;
  private final JFXListView<Label> listView;
  private final ObservableList<Edit<HospitalNode>> edits;
  private final Paint fillColor;
  private final ArrayList<AnchorNode> selectedNodes;
  private final SimpleBooleanProperty selected;
  private final AnchorNode self = this;

  public AnchorNode(DoubleProperty x, DoubleProperty y, HospitalNode node) {
    super(x.get(), y.get(), 5);
    this.node = node;
    this.x = x;
    this.y = y;
    this.initialX = x.get();
    this.initialY = y.get();
    edited = new SimpleBooleanProperty(false);
    listView = null;
    edits = null;
    fillColor = null;
    selectedNodes = null;
    selected = new SimpleBooleanProperty(false);

    setMouseTransparent(true);
    setVisible(false);
  }

  public AnchorNode(
      Color color,
      DoubleProperty x,
      DoubleProperty y,
      HospitalNode node,
      JFXListView<Label> listView,
      ObservableList<Edit<HospitalNode>> edits,
      ArrayList<AnchorNode> selectedNodes) {
    super(x.get(), y.get(), 5);
    this.fillColor = color.deriveColor(1, 1, 1, 0.5);
    setFill(this.fillColor);
    setStroke(color);
    setStrokeWidth(2);
    setStrokeType(StrokeType.OUTSIDE);

    this.listView = listView;

    this.node = node;
    this.edits = edits;
    this.x = x;
    this.y = y;
    this.initialX = x.get();
    this.initialY = y.get();
    this.dragged = false;
    this.edited = new SimpleBooleanProperty(false);
    this.selectedNodes = selectedNodes;

    x.bind(centerXProperty());
    y.bind(centerYProperty());
    y.bind(centerYProperty());
    selected = new SimpleBooleanProperty(false);
    selected.addListener(
        (ob, o, n) ->
            setEffect(n ? new DropShadow(BlurType.TWO_PASS_BOX, Color.GOLD, 2, 10, 0, 0) : null));
    enableDrag();
  }

  public HospitalNode getNode() {
    return this.node;
  }

  public double getX() {
    return x.get();
  }

  public DoubleProperty getXProperty() {
    return x;
  }

  public double getY() {
    return y.get();
  }

  public DoubleProperty getYProperty() {
    return y;
  }

  public void setSelected(boolean val) {
    selected.set(val);
  }

  public boolean getSelected() {
    return selected.get();
  }

  public boolean beingDragged() {
    return this.dragged;
  }

  public SimpleBooleanProperty isEdited() {
    return this.edited;
  }

  // make a node movable by dragging it around with the mouse.
  private void enableDrag() {
    final AnchorNode.Delta dragDelta = new AnchorNode.Delta();
    setOnMousePressed(
        new EventHandler<MouseEvent>() {
          @Override
          public void handle(MouseEvent mouseEvent) {
            // record a delta distance for the drag and drop operation.
            dragDelta.x = getCenterX() - mouseEvent.getX();
            dragDelta.y = getCenterY() - mouseEvent.getY();
          }
        });
    setOnMouseReleased(
        new EventHandler<MouseEvent>() {
          @Override
          public void handle(MouseEvent mouseEvent) {
            if (dragged) {
              move();
            }
            dragged = false;
          }
        });

    setOnMouseDragged(
        new EventHandler<MouseEvent>() {
          @Override
          public void handle(MouseEvent mouseEvent) {
            double newX = mouseEvent.getX() + dragDelta.x;
            double newY = mouseEvent.getY() + dragDelta.y;
            boolean moveX = newX > 0 && newX < getParent().getBoundsInParent().getMaxX();
            boolean moveY = newY > 0 && newY < getParent().getBoundsInParent().getMaxY();

            if (selectedNodes.contains(self)) {
              double deltaX = moveX ? newX - self.getX() : 0;
              double deltaY = moveY ? newY - self.getY() : 0;
              selectedNodes.forEach(
                  n -> {
                    n.move(n.getX() + deltaX, n.getY() + deltaY);
                  });
            }
            if (moveX) {
              setCenterX(newX);
              dragged = true;
            }
            if (moveY) {
              setCenterY(newY);
              dragged = true;
            }
          }
        });
  }

  public void move(double newX, double newY) {
    setCenterX(newX);
    setCenterY(newY);
    move();
  }

  private void move() {
    if (edited.get()) {
      edits.removeIf(
          e -> !e.isEdge() && ((AnchorNode) e.getObject()).getNode().getId() == node.getId());
      listView
          .getItems()
          .removeIf(
              x ->
                  x.getText().equals(node.toString())
                      && ((MaterialDesignIconView) x.getGraphic())
                          .getGlyphName()
                          .equals("CURSOR_MOVE"));
    }
    edited.set(true);
    Edit<HospitalNode> edit =
        new Edit(
            node,
            node.modifiedClone(
                (int) (x.get() / (initialX / node.getXCoord())),
                (int) (y.get() / (initialY / node.getYCoord()))),
            AnchorNode.this);
    edits.add(0, edit);
    setFill(Color.BLACK);
    Label label =
        new Label(node.toString(), new MaterialDesignIconView(MaterialDesignIcon.CURSOR_MOVE));
    Tooltip tooltip = new Tooltip();
    tooltip.setText(
        String.format(
            "Moved from: (%s, %s) -> (%s, %s)", initialX, initialY, (int) getX(), (int) getY()));
    // tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_BOTTOM_RIGHT);
    label.setTooltip(tooltip);
    label.setId(node.getId());
    label.setUserData(edit);
    listView.getItems().add(0, label);
  }

  public void undoMove() {
    edits.removeIf(
        x -> {
          System.out.println("checking");
          if (!x.isEdge()) {
            return x.getBefore() != null
                && x.getAfter() != null
                && ((AnchorNode) x.getObject()).getNode().getId().equals(node.getId());
          }
          return false;
        });

    setCenterX(initialX);
    setCenterY(initialY);
    edited.set(false);
    setFill(fillColor);
    listView
        .getItems()
        .removeIf(
            x ->
                x.getId().equals(node.getId())
                    && ((MaterialDesignIconView) x.getGraphic())
                        .getGlyphName()
                        .equals("CURSOR_MOVE"));
  }

  // records relative x and y co-ordinates.
  private class Delta {
    double x, y;
  }
}
