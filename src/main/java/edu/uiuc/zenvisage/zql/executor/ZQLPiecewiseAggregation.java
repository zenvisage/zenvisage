/**
 * 
 */
package edu.uiuc.zenvisage.zql.executor;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.uiuc.zenvisage.data.roaringdb.db.ColumnMetadata;
import edu.uiuc.zenvisage.data.roaringdb.db.Database;
import edu.uiuc.zenvisage.data.roaringdb.executor.Executor;
import edu.uiuc.zenvisage.model.Point;
import edu.uiuc.zenvisage.model.Sketch;
import edu.uiuc.zenvisage.service.distance.Distance;
import edu.uiuc.zenvisage.model.*;
import edu.uiuc.zenvisage.service.utility.Normalization;



/**
 * @author xiaofo
 *
 */
public class ZQLPiecewiseAggregation {
	public Normalization normalization;
	public Database inMemoryDatabase;
	
	/**
	 * @param normalization
	 * @param args
	 * @param inMemoryDatabase
	 */
	public ZQLPiecewiseAggregation(Normalization normalization,
			Database inMemoryDatabase) {
		this.normalization = normalization;
		this.inMemoryDatabase = inMemoryDatabase;
	}

	public double[] applyPAA(Set<Integer> ignore) {
		return null;
	}
	
	public void setPAAwidth(LinkedHashMap<String, LinkedHashMap<Float, Float>> output, String x) {
		if (output == null) return;
		ColumnMetadata columnMetadata = inMemoryDatabase.getColumnMetaData(x);
		columnMetadata.pAAWidth = (float) 1/(output.values().iterator().next().size() - 2);
		columnMetadata.numberOfSegments = output.values().iterator().next().size();
	}
	
	public double[][] applyPAAonData(LinkedHashMap<String,LinkedHashMap<Float,Float>> data, Set<Float> ignore, String x){
		double[][] normalizedgroups = new double[data.size()][];
		int count = 0;
		ColumnMetadata columnMetadata = inMemoryDatabase.getColumnMetaData(x);
		float pAAWidth = columnMetadata.pAAWidth;
		int numberofsegments = (int) (1/pAAWidth);
		float min = columnMetadata.min;
		float max = columnMetadata.max;	
		float range = max-min;
	
	  
		for (String key : data.keySet()) {
			Map<Float,Float> values = data.get(key);
			double [] normalizedValues = new double[numberofsegments+1];
			int[] numberofpoints = new int[numberofsegments+1];
			for (int i = 0; i < numberofsegments; i++) {
				normalizedValues[i] = 0.0;
				numberofpoints[i] = 0;
				ignore.add((float)i);
			}
		  
			for (Float key1 : values.keySet()) {
				if (range == 0)
					continue;
				int segment = (int) (((key1-min))/(range*pAAWidth));
				normalizedValues[segment] = normalizedValues[segment] + values.get(key1);
				numberofpoints[segment] = numberofpoints[segment]+1;
				ignore.remove(key1);
			}
		  
			for (int i = 0; i <= numberofsegments; i++) {
				if (numberofpoints[i] > 0)
					normalizedValues[i] = normalizedValues[i] / numberofpoints[i];
			}
		  
			for (int i = 0; i < numberofsegments; i++) {
				if (numberofpoints[i] == 0) {
					if (i > 0 && i < numberofsegments) {
						normalizedValues[i] = (normalizedValues[i-1] + normalizedValues[i+1])/2;
					}
					else if(i > 0) {
						normalizedValues[i] = normalizedValues[i-1];
					}
					else{
						normalizedValues[i] = normalizedValues[i+1];
					}
				}
				normalization.normalize(normalizedValues);
			}
			  
			  
			normalizedgroups[count] = normalizedValues;
			count++;		  
		}
		return normalizedgroups;  
	}
	
	
	
	
	
	public double[] applyPAAonQuery(Set<Float> ignore, Sketch sketchPoint){
		ColumnMetadata xcolumnMetadata = inMemoryDatabase.getColumnMetaData(sketchPoint.getxAxis());
		float pAAWidth = xcolumnMetadata.pAAWidth;
		int numberofsegments = (int) (1/pAAWidth);
		float min = sketchPoint.getMinX();
		float max = sketchPoint.getMaxX();		
		float rangeX = max-min;
		double [] normalizedValues = new double[numberofsegments+1];
		int[] numberofpoints = new int[numberofsegments+1];
		for (int i = 0;i < numberofsegments;i++) {
			normalizedValues[i] = 0.0;
			numberofpoints[i] = 0;
		}
	  
		ColumnMetadata ycolumnMetadata = inMemoryDatabase.getColumnMetaData(sketchPoint.getyAxis());
		float minY = ycolumnMetadata.min;
		float maxY = ycolumnMetadata.max;
		float rangeY = maxY-minY;
	  
		float minYQ = sketchPoint.getMinY();
		float maxYQ = sketchPoint.getMaxY();		
		float rangeYQ = maxYQ-minYQ;
		for (Point p : sketchPoint.getPoints()) {		  
			int segment = (int) ((p.getXval()-min)/(rangeX*pAAWidth));
			//System.out.println(segment);
			float yvalue = minY+((p.getYval()-minYQ)*rangeY/(rangeYQ));
			normalizedValues[segment] = normalizedValues[segment]+yvalue;
			numberofpoints[segment] = numberofpoints[segment]+1;
		}
		  
		for (int i = 0; i <= numberofsegments; i++) {
			if(numberofpoints[i] > 0)
				normalizedValues[i] = normalizedValues[i]/numberofpoints[i];
		}
		  
		for (int i = 0; i < numberofsegments; i++) {
			if (numberofpoints[i] == 0) {
				if (i > 0 && i < numberofsegments) {
					normalizedValues[i] = (normalizedValues[i-1] + normalizedValues[i+1])/2;
				}
				else if (i > 0) {
					normalizedValues[i] = normalizedValues[i-1];
				}
				else {
					normalizedValues[i] = normalizedValues[i+1];
				}
			}
			
			if (ignore.contains(i))
				normalizedValues[i] = 0;
				
		}
		normalization.normalize(normalizedValues); 
		
		//System.out.println(Arrays.toString(normalizedValues));
		return normalizedValues;
	}
	
	
	
}
