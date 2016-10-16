package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tarique
 *
 */
public class AxisVariableScores {
 private ArrayList<ArrayList<String>> axisvars;
 public ArrayList<ArrayList<String>> getAxisvars() {
	return axisvars;
}
public void setAxisvars(ArrayList<ArrayList<String>> axisvars) {
	this.axisvars = axisvars;
}
public double[] getScore() {
	return score;
}
public void setScore(double[] score) {
	this.score = score;
}
private double [] score;
 AxisVariableScores(ArrayList<ArrayList<String>> axisvars, double[] score) {
	 this.axisvars = axisvars;
	 this.score = score;
 }
 AxisVariableScores(ArrayList<ArrayList<String>> axisvars, List<Double> s) {
	 this.axisvars = axisvars;
	 this.score = new double[s.size()];
	 for (int i = 0; i < s.size(); i++) {
		 this.score[i] = s.get(i);
	 }
 }
}
