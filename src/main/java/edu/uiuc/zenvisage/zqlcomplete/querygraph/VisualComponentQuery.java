package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import java.util.ArrayList;
import java.util.List;

import edu.uiuc.zenvisage.zqlcomplete.executor.Constraints;
import edu.uiuc.zenvisage.zqlcomplete.executor.Name;
import edu.uiuc.zenvisage.zqlcomplete.executor.VizColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.XColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.YColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZColumn;

public class VisualComponentQuery {
	private Name name;
	private XColumn x;
	private YColumn y;
	private ZColumn z;
	private String constraints;
	private VizColumn viz;
	// is sketchPoints associated with visualComponent?
	// or vc output?
	
	public VisualComponentQuery() {
		name = new Name();
		x = new XColumn();
		y= new YColumn();
		z= new ZColumn();
		viz = new VizColumn();		
	}

	public VisualComponentQuery(Name name, XColumn x, YColumn y, ZColumn z, String constraints, VizColumn viz) {
		super();
		this.name = name;
		this.x = x;
		this.y = y;
		this.z = z;
		this.constraints = constraints;
		this.viz = viz;
	}

	public VisualComponentQuery(VisualComponentQuery source) {
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

	public String getConstraints() {
		return constraints;
	}

	public void setConstraints(String constraints) {
		this.constraints = constraints;
	}
	
	public VizColumn getViz() {
		return viz;
	}

	public void setViz(VizColumn viz) {
		this.viz = viz;
	}	
}
