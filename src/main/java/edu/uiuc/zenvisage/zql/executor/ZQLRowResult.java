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
public class ZQLRowResult {
	private Sketch sketchPoints;
	private boolean isSketch=false; 
	private List<ZQLRowVizResult> zqlRowVizResults;
	private ZQLRowProcessResult zqlProcessResult;
	public ZQLRowResult(){
		zqlRowVizResults=new ArrayList<ZQLRowVizResult>();
	}
	
	public Sketch getSketchPoints() {
		return sketchPoints;
	}


	public void setSketchPoints(Sketch sketchPoints) {
		this.sketchPoints = sketchPoints;
	}


	public boolean isSketch() {
		return isSketch;
	}


	public void setSketch(boolean isSketch) {
		this.isSketch = isSketch;
	}
	
	public List<ZQLRowVizResult> getZqlRowVizResults() {
		return zqlRowVizResults;
	}

	public void setZqlRowVizResults(List<ZQLRowVizResult> zqlRowVizResults) {
		this.zqlRowVizResults = zqlRowVizResults;
	}
	public ZQLRowProcessResult getZqlProcessResult() {
		return zqlProcessResult;
	}
	public void setZqlProcessResult(ZQLRowProcessResult zqlProcessResult) {
		this.zqlProcessResult = zqlProcessResult;
	}
	
	
	
}
