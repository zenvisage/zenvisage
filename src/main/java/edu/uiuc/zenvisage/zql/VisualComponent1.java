package edu.uiuc.zenvisage.zql;

import java.util.ArrayList;
import java.util.List;

import edu.uiuc.zenvisage.zqlcomplete.executor.Constraints;
import edu.uiuc.zenvisage.zqlcomplete.executor.Name;
import edu.uiuc.zenvisage.zqlcomplete.executor.VizColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.XColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.YColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZColumn;


// Deprecated. TODO: delete
public class VisualComponent1 {
	private Name name;
	private XColumn x;
	private YColumn y;
	private ZColumn z;
	private List<Constraints> constraints;
	private VizColumn viz;
	// is sketchPoints associated with visualComponent?
	// or vc output?
	
	public VisualComponent1() {
		name = new Name();
		x = new XColumn();
		y= new YColumn();
		z= new ZColumn();
		constraints= new ArrayList<Constraints>();
		viz = new VizColumn();		
	}

	public VisualComponent1(Name name, XColumn x, YColumn y, ZColumn z, List<Constraints> constraints, VizColumn viz) {
		super();
		this.name = name;
		this.x = x;
		this.y = y;
		this.z = z;
		this.constraints = constraints;
		this.viz = viz;
	}

	public VisualComponent1(VisualComponent1 source) {
		name = source.getName();
		x = source.getX();
		y = source.getY();
		z = source.getZ();
		constraints = source.getConstraints();
		viz = source.getViz();
	}
	
	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}

	public XColumn getX() {
		return x;
	}

	public void setX(XColumn x) {
		this.x = x;
	}

	public YColumn getY() {
		return y;
	}

	public void setY(YColumn y) {
		this.y = y;
	}

	public ZColumn getZ() {
		return z;
	}

	public void setZ(ZColumn z) {
		this.z = z;
	}

	public List<Constraints> getConstraints() {
		return constraints;
	}

	public void setConstraints(List<Constraints> constraints) {
		this.constraints = constraints;
	}

	public VizColumn getViz() {
		return viz;
	}

	public void setViz(VizColumn viz) {
		this.viz = viz;
	}	
}
