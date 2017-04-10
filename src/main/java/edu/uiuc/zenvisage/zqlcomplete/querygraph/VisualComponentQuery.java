package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import java.util.ArrayList;
import java.util.List;

import edu.uiuc.zenvisage.model.ScatterResult.Tuple;
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
	private List<Rectangle> rectangles;
	private int numOfResults;
	
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

	public List<Rectangle> getRectangles() {
		return rectangles;
	}

	public void setRectangles(List<Rectangle> rectangles) {
		this.rectangles = rectangles;
	}

	public int getNumOfResults() {
		return numOfResults;
	}

	public void setNumOfResults(int numOfResults) {
		this.numOfResults = numOfResults;
	}

	public static class Rectangle {
		public double x1;
	    public double x2;
	    public double y1;
	    public double y2;
		/**
		 * @param x1
		 * @param x2
		 * @param y1
		 * @param y2
		 */
		public Rectangle(double x1, double x2, double y1, double y2) {
			this.x1 = x1;
			this.x2 = x2;
			this.y1 = y1;
			this.y2 = y2;
		}
		
		public Rectangle() {
			
		}
		
		public boolean inArea(Tuple tuple) {
			return this.x1 <= tuple.x && tuple.x <= this.x2 && this.y1 <= tuple.y && tuple.y <= this.y2;
		}
	}
}
