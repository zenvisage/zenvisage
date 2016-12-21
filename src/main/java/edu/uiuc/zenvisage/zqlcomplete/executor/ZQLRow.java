/**
 * 
 */
package edu.uiuc.zenvisage.zqlcomplete.executor;

import java.util.ArrayList;
import java.util.List;

import edu.uiuc.zenvisage.model.Sketch;

/**
 * @author tarique
 *
 */
public class ZQLRow {
	private Name name;
	private XColumn x;
	private YColumn y;
	private ZColumn z;
	private List<Constraints> constraints;
	private Processe processe;
	private VizColumn viz;
	public Sketch sketchPoints;
	
	// Needed for jackson mapping from json to object
	public ZQLRow() {
		name = new Name();
		x = new XColumn();
		y= new YColumn();
		z= new ZColumn();
		constraints= new ArrayList<Constraints>();
		processe = new Processe();
		viz = new VizColumn();
	}

	public ZQLRow(XColumn x, YColumn y, ZColumn z, List<Constraints> constraints, VizColumn viz) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.constraints= constraints;
		this.viz = viz;
	}
	
	public ZQLRow(ZQLRow source) {
		name = source.getName();
		x = source.x;	
		y = source.y;
		z = source.z;
		constraints = source.constraints;
		processe = source.processe;
		viz = source.viz;
		sketchPoints = source.sketchPoints;
		
	}
	
	/**
	 * Need implementing using parser parsing expressions
	 * @return
	 */
	public void resolveVariables(PSQLDatabase zenvisageDB, ZQLTableResult globalTable) {
		
		// parse expressions
		
		// if the variable already exists in the table, reference it
		
		// if expression require sql query, construct sql query and put db query result into variable result
		
		// if expression is constant set, put set value into variable result
			
	}

	/**
	 * Use these to get the values retrieved in the current row, need to add support for multiple variable assignment
	 * @return
	 */
	
	public Name getName() {
		return name;
	}
	public void setName(Name source) {
		this.name = source;
	}
	public XColumn getX() {
		return x;
	}
	public void setX(XColumn source) {
		this.x = source;
	}
	
	public YColumn getY() {
		return y;
	}
	public void setY(YColumn source) {
		this.y = source;
	}
	public ZColumn getZ() {
		return z;
	}
	public void setZ(ZColumn source) {
		this.z = source;
	}
	public List<Constraints> getConstraint() {
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
	public Boolean isOutput() {
		return name.getOutput();
	}
	public void setOutput(Boolean isOutput) {
		this.name.setOutput(isOutput);
	}
	
	public Sketch getSketchPoints() {
		return sketchPoints;
	}

	public void setSketchPoints(Sketch sketchPoints) {
		this.sketchPoints = sketchPoints;
	}
	public VizColumn getViz() {
		return viz;
	}
	
	public void setViz(VizColumn source) {
		viz = source;
	}

}
