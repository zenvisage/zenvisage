/**
 * 
 */
package org.vde.zql;

import java.util.ArrayList;
import java.util.List;

import api.SketchPoints;

/**
 * @author tarique
 *
 */
public class ZQLRowResult {
	private SketchPoints sketchPoints;
	private boolean isSketch=false; 
	private List<ZQLRowVizResult> zqlRowVizResults;
	private ZQLRowProcessResult zqlProcessResult;
	
	public ZQLRowResult(){
		zqlRowVizResults=new ArrayList<ZQLRowVizResult>();
	}
	
	public SketchPoints getSketchPoints() {
		return sketchPoints;
	}


	public void setSketchPoints(SketchPoints sketchPoints) {
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
