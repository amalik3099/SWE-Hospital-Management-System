package edu.wpi.teamA.modules;

import java.awt.*;
import javafx.animation.FadeTransition;
import javafx.scene.CacheHint;
import javafx.util.Duration;

public class Fading {

  public void fadeIn(javafx.scene.Node n, double toOpacity) {
    FadeTransition fadeIn = new FadeTransition();
    fadeIn.setDuration(Duration.millis(1000));
    fadeIn.setFromValue(n.getOpacity());
    fadeIn.setToValue(toOpacity);
    fadeIn.setNode(n);

    fadeIn.play();
    fadeIn.setOnFinished(e -> n.setCacheHint(CacheHint.QUALITY));
  }

  public void fadeOut(javafx.scene.Node n, double toOpacity) {
    FadeTransition fadeOut = new FadeTransition();
    fadeOut.setDuration(Duration.millis(1000));
    fadeOut.setFromValue(n.getOpacity());
    fadeOut.setToValue(toOpacity);
    fadeOut.setNode(n);

    fadeOut.play();
    fadeOut.setOnFinished(e -> n.setCacheHint(CacheHint.SPEED));
  }
}
