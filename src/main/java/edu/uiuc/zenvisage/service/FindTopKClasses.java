/**
 * 
 */
package edu.uiuc.zenvisage.service;

import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import edu.uiuc.zenvisage.model.Sketch;
import edu.uiuc.zenvisage.model.ZvQuery;

/**
 * @author tarique
 *
 */
public class FindTopKClasses {
	
	
void findtopKClasses(String query) throws JsonParseException, JsonMappingException, IOException{
	ZvQuery args = new ObjectMapper().readValue(query,ZvQuery.class);
	Sketch[] sketch= args.getSketchPoints();
	String datasetName="real_estate";
	segmentTrend();
	findMargins();
	ProjectedPoints projectedPoints=projectPoints(datasetName);
	summarizeResults();
	
	//findMargins
	//projectPoints
	//findMostCommonXandY
}


void segmentTrend(){
	
}
	
void findMargins(){
	
}

ProjectedPoints projectPoints(String datasetName){
	 ProjectedPoints projectedPoints=new ProjectedPoints();
	//write a sql query to fetch the data points.
	//iterate through the rows, project them with all possible X and Y, and see if they fall in the margin. If so, add them to the projected points.

	 return projectedPoints;
}


/**
* 
*/
private ArrayList<VisualClass> summarizeResults() {
	return null;
	

}

	
}
