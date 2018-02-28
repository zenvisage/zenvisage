/**
 * 
 */
package edu.uiuc.zenvisage.zql.executor;

import java.util.ArrayList;
import java.util.List;

import edu.uiuc.zenvisage.model.Sketch;


/**
 * @author tarique
 *
 */
@Deprecated
public class ZQLRow {
	private String name;
	private List<String> x;
	private List<String> y;
	private List<String> z;
	private List<Constraints> constraints;
	private Processe processe;
	private boolean isOutput;
	public Sketch sketchPoints;
	
	public ZQLRow() {
		x=new ArrayList<String>();
		y=new ArrayList<String>();
		z=new ArrayList<String>();
		constraints=new ArrayList<Constraints>();
		isOutput=false;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getX() {
		return x;
	}
	public void setX(List<String> x) {
		this.x = x;
	}
	public List<String> getY() {
		return y;
	}
	public void setY(List<String> y) {
		this.y = y;
	}
	public List<String> getZ() {
		return z;
	}
	public void setZ(List<String> z) {
		this.z = z;
	}
	public List<Constraints> getConstraints() {
		return constraints;
	}
	public void setConstraints(List<Constraints> constraints) {
		this.constraints = constraints;
	}
	public Processe getProcesse() {
		return processe;
	}
	public void setProcesse(Processe processe) {
		this.processe = processe;
	}
	public boolean isOutput() {
		return isOutput;
	}
	public void setOutput(boolean isOutput) {
		this.isOutput = isOutput;
	}
	
	public Sketch getSketchPoints() {
		return sketchPoints;
	}

	public void setSketchPoints(Sketch sketchPoints) {
		this.sketchPoints = sketchPoints;
	}
	

}
