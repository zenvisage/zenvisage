/**
 * 
 */
package edu.uiuc.zenvisage.service;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import edu.uiuc.zenvisage.data.remotedb.SQLQueryExecutor;
import edu.uiuc.zenvisage.model.Point;
import edu.uiuc.zenvisage.model.Sketch;
import edu.uiuc.zenvisage.model.ZvQuery;
import edu.uiuc.zenvisage.data.remotedb.Attribute;

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
	public String toString(){
		return String.valueOf(minX)+ ","+String.valueOf(maxX)+","+String.valueOf(minY)+ ","+String.valueOf(maxY);
	}
}

private class RawData{
	HashMap<String,ArrayList<Float>> XYColumns;
	HashMap<String,ArrayList<String>> otherColumns;
	HashMap<String,Float> min;
	HashMap<String,Float> max;
	HashMap<String,Float> sum;
	HashMap<String,Float> variance;
	int size;
	HashMap<Axes,ArrayList<Integer>> projectedPoints;
	
}

private String query;

public FindTopKClasses(String query){
	this.query = query;
}

public String findtopKClasses() throws JsonParseException, JsonMappingException, IOException, SQLException{
	 //get the trend from /zv/postSimilarity
	ZvQuery args = new ObjectMapper().readValue(this.query,ZvQuery.class);
	//get the trend from /zv/postSimilarity
	Sketch[] sketch= args.getSketchPoints();
	String datasetName="real_estate";
	segmentTrendAndFindMargins(sketch,20);
	RawData rawData=projectPoints(datasetName, null, 0, 0, 0, 0);
	ArrayList<VisualClass> visualClasses=summarizeResults(rawData);
	return new ObjectMapper().writeValueAsString(visualClasses);
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
	 //Equal divide segments by x axis, 1. find segSize;
	 double segSize= (args.maxX - args.minX)/nOfSegments;
	 
	 List<Region> regions = new ArrayList<Region>();

	 float[] averagePoint = new float[2];
	 int i = 0; int j = 0;
	 //For each point see whether it falls into current segment. if
	 for(; i < points.size();){
		 Region r = new Region(Float.MAX_VALUE,Float.MIN_VALUE,Float.MAX_VALUE,Float.MIN_VALUE);
		 for(j = ; j < segSize && i+j < points.size(); j++){
			 float x = points.get(i+j).getX();
			 float y = points.get(i+j).getY();
			 averagePoint[0] += (x-averagePoint[0])/nPoints;
			 averagePoint[1] += (y-averagePoint[1])/nPoints;
			 if(x<r.minX) r.minX = x;
			 if(x>r.maxX) r.maxX = x;
			 if(y<r.minY) r.minY = y;
			 if(y>r.maxY) r.maxY = y;
			 regions.add(r);
			 System.out.println(r.toString()+"Average:"+averagePoint[0]+","+averagePoint[1]);
		 }
	 }
 }


RawData projectPoints(String datasetName,List<Region> regions,float pixelMinX,float pixelMaxX, float pixelMinY,float pixelMaxY) throws SQLException{
	SQLQueryExecutor sqlQueryExecutor = new SQLQueryExecutor();
	ArrayList<Attribute> attributes = sqlQueryExecutor.getAllAttribute(datasetName);
	ArrayList<String> xAttributes= new ArrayList();
	ArrayList<String> yAttributes= new ArrayList();
	ArrayList<String> otherAttributes= new ArrayList();
    findXandYAttributes(attributes,xAttributes,yAttributes,otherAttributes);					
	RawData rawData=fetchdata(datasetName,xAttributes,yAttributes,otherAttributes);
	
	HashMap<Axes,ArrayList<Integer>> projectedPoints= new HashMap<>();
	for(int k=0;k<rawData.size;k++)
	{
		for(int i=0;i<xAttributes.size();i++)
		for(int j=0;j<yAttributes.size();j++)
			{
			Float xvalue=rawData.XYColumns.get(xAttributes.get(i)).get(k);
			Float yvalue=rawData.XYColumns.get(yAttributes.get(i)).get(k);
			if(isWithinBounds(xvalue,yvalue,
					rawData.min.get(xAttributes.get(i)),
					rawData.max.get(xAttributes.get(i)),
					rawData.min.get(yAttributes.get(i)),					
					rawData.max.get(yAttributes.get(i)),regions, pixelMinX,pixelMaxX,pixelMinY,pixelMaxY)){
				Axes axes = new Axes(xAttributes.get(i),yAttributes.get(i));
				projectedPoints.get(axes).add(k);
			}
			
			}
	}
		
	rawData.projectedPoints=projectedPoints;
	return rawData;
}


Boolean isWithinBounds(Float xvalue,Float yvalue,Float xmin,Float xmax, Float ymin,Float ymax,List<Region> regions,float pixelMinX,float pixelMaxX, float pixelMinY,float pixelMaxY){
	xvalue=((xvalue-xmin)/(xmax-xmin))*(pixelMaxX-pixelMinX);
	yvalue=((yvalue-ymin)/(ymax-ymin))*(pixelMaxY-pixelMinY);
	for(Region region: regions) {
		if(region.minX<=xvalue)
			if(region.maxX>xvalue){
				if(yvalue>region.minY && yvalue<region.maxY)
				{
					return true;
				}
				return false;
			}			
	}
	
	return false;
	
}
/**
 * @param datasetName
 * @param xAttributes
 * @param yAttributes
 * @param otherAttributes
 * @throws SQLException 
 */
private RawData fetchdata(String datasetName, ArrayList<String> xAttributes, ArrayList<String> yAttributes,
		ArrayList<String> otherAttributes) throws SQLException {
	// TODO Auto-generated method stub
	String projections="";
	HashMap<String,ArrayList<Float>> XYColumns= new HashMap<String,ArrayList<Float>>();
	HashMap<String,ArrayList<String>> otherColumns= new HashMap<String,ArrayList<String>>();
	for(int i=0;i<xAttributes.size();i++){
		projections=projections+","+xAttributes.get(i);
		XYColumns.put(xAttributes.get(i), new ArrayList<Float>());
	}
	for(int i=0;i<yAttributes.size();i++){
		projections=projections+","+yAttributes.get(i);
		XYColumns.put(yAttributes.get(i), new ArrayList<Float>());
	}
	for(int i=0;i<otherAttributes.size();i++){
		projections=projections+","+otherAttributes.get(i);
		otherColumns.put(otherAttributes.get(i), new ArrayList<String>());
	}
	
	//TODO: Need to handle it in a better way -- throw exception
	if(projections.length()<1)
	{
		return null;
	}
	projections=projections.substring(1);
	String SQLquery="SELECT "+projections+" from " +datasetName +" ;";
	SQLQueryExecutor sqlQueryExecutor = new SQLQueryExecutor();			
	ResultSet rs = sqlQueryExecutor.query(SQLquery);
	HashMap<String,Float> min = new HashMap<>();
	HashMap<String,Float> max = new HashMap<>();
	HashMap<String,Float> sum = new HashMap<>();
	HashMap<String,Float> variance = new HashMap<>();
	
	int rowCount=0;
	while(rs.next()){
		for(int i=0;i<xAttributes.size();i++){
			Float val=rs.getFloat(xAttributes.get(i));
			XYColumns.get(rs.getFloat(xAttributes.get(i))).add(rowCount,val);
			sum.put(xAttributes.get(i),sum.get(xAttributes.get(i))+val);
			if(val<min.get(xAttributes.get(i)))
				min.put(xAttributes.get(i),val);
			if(val>max.get(xAttributes.get(i)))
				max.put(xAttributes.get(i),val);			
		}
				
		for(int i=0;i<yAttributes.size();i++){
			Float val=rs.getFloat(xAttributes.get(i));
			sum.put(yAttributes.get(i),sum.get(yAttributes.get(i))+val);
			XYColumns.get(rs.getFloat(yAttributes.get(i))).add(rowCount,rs.getFloat(yAttributes.get(i)));
			if(val<min.get(yAttributes.get(i)))
				min.put(yAttributes.get(i),val);
			if(val>max.get(yAttributes.get(i)))
				max.put(yAttributes.get(i),val);		
		}
		for(int i=0;i<otherAttributes.size();i++){
		   otherColumns.get(rs.getString(otherAttributes.get(i))).add(rowCount,rs.getString(otherAttributes.get(i)));
		}
		rowCount++;
	}
	
	
	
	//normalize Y values

	for(int i=0;i<yAttributes.size();i++){
		ArrayList<Float> yColumn= XYColumns.get(yAttributes.get(i));
		
		float mean=sum.get(yAttributes.get(i))/yColumn.size();
		float var=0;
		for(int j=0;j<yColumn.size();j++){
			var+=(float) Math.pow(yColumn.get(j)-mean,2);
		}
		var=var/yColumn.size();
		float std=(float) Math.sqrt(var);
		
		for(int j=0;j<yColumn.size();j++){
			yColumn.set(i, (yColumn.get(i)-mean)/std);
		}
		variance.put(yAttributes.get(i), var);
	}
	
	RawData  rawData= new RawData();
	rawData.XYColumns=XYColumns;
	rawData.otherColumns=otherColumns;
	rawData.min=min;
	rawData.max=max;
	rawData.sum=sum;
	rawData.variance=variance;
	rawData.size=rowCount;
	return rawData;
}

/**
 * @param attributes
 * @param xAttributes
 * @param yAttributes
 */
private void findXandYAttributes(ArrayList<Attribute> attributes, ArrayList<String> xAttributes,
		ArrayList<String> yAttributes,ArrayList<String> otherAttributes) {
	// TODO Auto-generated method stub
	
	for(Attribute attribute:attributes){
		if(attribute.axis=="X")
			xAttributes.add(attribute.name);
		
		if(attribute.axis=="Y")
			yAttributes.add(attribute.name);
		
		if(attribute.axis!="X" || attribute.axis!="Y")
			otherAttributes.add(attribute.name);
		
	}
	
}

/**
* 
*/
private ArrayList<VisualClass> summarizeResults(RawData rawData) {
	
	HashMap<Axes,ArrayList<Integer>> projectedPoints= rawData.projectedPoints;
	HashMap<Integer,Axes> axesBySize = new HashMap<>();
	 List<Axes> mapKeys = new ArrayList<Axes>();
	 List<Integer> values = new ArrayList<Integer>();
	 ArrayList<VisualClass> visualClasses=new ArrayList<>();
	for(Axes key:projectedPoints.keySet())
	{	values.add(projectedPoints.get(key).size());
		axesBySize.put(projectedPoints.get(key).size(), key);
	}
	
	 LinkedHashMap<Axes,ArrayList<Integer>> sortedMap =
		        new LinkedHashMap<>();
	Collections.sort(values);
	for(int i=0;i<values.size();i++){
		sortedMap.put(mapKeys.get(values.get(i)),projectedPoints.get(mapKeys.get(values.get(i))));
	}
	int classCount=0;
	for(Axes key:sortedMap.keySet()){
		VisualClass vClass= new VisualClass();
		ArrayList<Integer> indices = sortedMap.get(key);
		vClass.X=key.getX();
		vClass.Y=key.getY();
		Integer rowCount=0;
		HashMap<String,Float> min= new HashMap<>();
		HashMap<String,Float> max= new HashMap<>();
		HashMap<String,Set<String>> in= new HashMap<>();
		for(int i:indices){
			for(String x:rawData.XYColumns.keySet()){
				if(x==vClass.X){
					vClass.xPoints.add(rowCount, rawData.XYColumns.get(x).get(i));						
				}
				else
					if(x==vClass.Y){
					vClass.yPoints.add(rowCount, rawData.XYColumns.get(x).get(i));				
					}
				else 
					{
					if(rawData.XYColumns.get(x).get(i)< min.get(x))
						min.put(x, rawData.XYColumns.get(x).get(i));
					if(rawData.XYColumns.get(x).get(i)> max.get(x))
						max.put(x, rawData.XYColumns.get(x).get(i));
					}
			
			}
			
			for(String x:rawData.otherColumns.keySet()){
					in.get(x).add(rawData.otherColumns.get(x).get(i));
					
					}
			
			}
		
		for(String k:min.keySet()){
			Float mn=min.get(k);
			Float mx=max.get(k);
			String value="";
			if(mn==mx)
			{
				value=k+"="+"mn";
			}
			else{
				value=min+"<=k<="+max;
			}
			
			vClass.constraints.put(k, value);
		
		}
	
		for(String k:in.keySet()){
			Set<String> st = in.get(k);
			String ct=k+ " IN (";
			String c="";
			for(String s:st){
				c=c+","+s;
			}
			ct=ct+c.substring(1)+")";
			vClass.constraints.put(k, ct);
		
		}
	
		visualClasses.add(vClass);
	
	}

	return visualClasses;
}

	
}
