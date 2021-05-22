package edu.wpi.teamA.interfaces;

import edu.wpi.teamA.modules.HospitalNode;
import java.util.ArrayList;

public interface PathGenerator<T extends Node> {
  ArrayList<HospitalNode> illegal = new ArrayList<>();

  ArrayList<HospitalNode> getIllegal();

  ArrayList<T> findPath(T start, T end, int elev);
}
