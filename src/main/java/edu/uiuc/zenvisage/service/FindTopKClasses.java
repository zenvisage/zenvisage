/**
 * 
 */
package edu.uiuc.zenvisage.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import edu.uiuc.zenvisage.data.Query;
import edu.uiuc.zenvisage.data.remotedb.SQLQueryExecutor;
import edu.uiuc.zenvisage.model.Point;
import edu.uiuc.zenvisage.model.Sketch;
import edu.uiuc.zenvisage.model.ZvQuery;

/**
 * @author tarique
 *
 */


public class FindTopKClasses {

private class Region{
	float minX;
	float maxX;
	float minY;
	float maxY;
	public Region(float minX, float maxX, float minY, float maxY){
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
	}
}

private String query;

public FindTopKClasses(String query){
	this.query = query;
}

public String findtopKClasses() throws JsonParseException, JsonMappingException, IOException{
	 //get the trend from /zv/postSimilarity
	ZvQuery args = new ObjectMapper().readValue(this.query,ZvQuery.class);
	//get the trend from /zv/postSimilarity
	Sketch[] sketch= args.getSketchPoints();
	String datasetName="real_estate";
	segmentTrendAndFindMargins(sketch,20);
	ProjectedPoints projectedPoints=projectPoints(datasetName);
	ArrayList<VisualClass> visualClasses=summarizeResults();
		
//	return new ObjectMapper().writeValueAsString(visualClasses);
	return "placeholder";
	//findMargins
	//projectPoints
	//findMostCommonXandY
}


/**
 * 
 * @param sketch
 * @param nOfSegments
 * @throws JsonParseException
 * @throws JsonMappingException
 * @throws IOException
 * 	 1.get the average point of each region
 *	 2.get the square region of each segment
 */
 void segmentTrendAndFindMargins(Sketch[] sketch, int nOfSegments) throws JsonParseException, JsonMappingException, IOException{

	 ZvQuery args = new ObjectMapper().readValue(this.query,ZvQuery.class);
	 List<Point> points = sketch[0].getPoints();
	 int nPoints = points.size();
	 int segSize = nPoints/nOfSegments;
	 List<Region> regions = new ArrayList<Region>();

	 int[] averagePoint = new int[2];
	 int i = 0; int j = 0;
	 for(; i < points.size();i+=(j+1)){
		 Region r = new Region(Float.MAX_VALUE,Float.MIN_VALUE,Float.MAX_VALUE,Float.MIN_VALUE);
		 for(j = 0; j < segSize && i+j < points.size(); j++){
			 float x = points.get(i+j).getX();
			 float y = points.get(i+j).getY();
			 averagePoint[0] += (x-averagePoint[0])/nPoints;
			 averagePoint[1] += (y-averagePoint[1])/nPoints;
			 if(x<r.minX) r.minX = x;
			 if(x>r.maxX) r.maxX = x;
			 if(y<r.minY) r.minY = y;
			 if(y>r.maxY) r.maxY = y;
			 regions.add(r);
		 }
	 }
 }


ProjectedPoints projectPoints(String datasetName){
	 ProjectedPoints projectedPoints=new ProjectedPoints();
	//write a sql query to fetch the data points.
	//iterate through the rows, project them with all possible X and Y, and see if they fall in the margin. If so, add them to the projected points.
	 SQLQueryExecutor sqlQueryExecutor = new SQLQueryExecutor();
	 
	 return projectedPoints;
}


/**
* 
*/
private ArrayList<VisualClass> summarizeResults() {
	return null;
	

}

	
}
