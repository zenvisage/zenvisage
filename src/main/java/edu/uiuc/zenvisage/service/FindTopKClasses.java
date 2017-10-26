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
import edu.uiuc.zenvisage.data.remotedb.Statistics;
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
	double minX;
	double maxX;
	double minY;
	double maxY;
	double averageY;
	int nPoints;
	public Region(double minX, double maxX, double minY, double maxY){
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
	}
	public String toString(){
		return "minX:"+String.valueOf(minX)+ ","+
			   "maxX:"+String.valueOf(maxX)+","+
			   "minY:"+String.valueOf(minY)+ ","+
			   "maxY:"+String.valueOf(maxY)+","+
			   "averageY:"+String.valueOf(averageY)+
			   "nPoints:"+String.valueOf(nPoints);
	}
}

private class RawData{
	HashMap<String,ArrayList<Double>> XYColumns;
	HashMap<String,ArrayList<String>> otherColumns;
	HashMap<String,Double> min;
	HashMap<String,Double> max;
	HashMap<String,Double> sum;
	HashMap<String,Double> variance;
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
	segmentTrendAndFindMargins(sketch,20, 0.0);
	
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
 public List<Region> segmentTrendAndFindMargins(Sketch[] sketch, int nOfSegments, double margin) throws JsonParseException, JsonMappingException, IOException{
	 ZvQuery args = new ObjectMapper().readValue(this.query,ZvQuery.class);
	 List<Point> points = sketch[0].getPoints();
	 int nPoints = points.size();
	 //Equal divide segments by x axis, 1. find segSize;
	 double segSize=0;
	 double minX = Double.MAX_VALUE;
	 double maxX = Double.MIN_VALUE;
	 /**
	  * 1. z-normalize y values
	  */
	 double[] dataY = new double[nPoints];
	 double[] dataX = new double[nPoints];
	 for(int i = 0;i<nPoints;i++){
		 dataX[i] = points.get(i).getXval();
		 dataY[i] = points.get(i).getYval();
		 if(minX > dataX[i]) minX = dataX[i];
		 if(maxX < dataX[i]) maxX = dataX[i];
	 }
	 segSize = (maxX-minX)/nOfSegments;
	 Statistics yStatistics = new Statistics(dataY);
	 for(int i = 0;i<nPoints;i++){
		 dataY[i]= yStatistics.getZScore(dataY[i]);
	 }
	 
	 List<Region> regions = new ArrayList<Region>();
	 int i = 0;
	 /**
	  * 2.Segmentation
	  * For each point see whether it falls into current segment.
	  * if it is, normalize y value, change average and region for this segment.
	  * if not, proceed to next segment.*/
	 //initialize, Region(double minX, double maxX, double minY, double maxY)
	 Region r = new Region(minX, minX + segSize,Double.MAX_VALUE,Double.MIN_VALUE);
	 for(; i < nPoints;i++){
		 double x = dataX[i];
		 double y = dataY[i];
		 //x out of range, hop to next region
		 if(x > r.maxX){
			 r.minY = r.minY - margin;
			 r.maxY = r.maxY + margin;
			 System.out.println(r.toString());
			 regions.add(r);
			 r = new Region(r.maxX,r.maxX+segSize,Double.MAX_VALUE,Double.MIN_VALUE);
		 }
		 //update minY, maxY, nPoint, averageY for new point added in a region
		 if(y<r.minY) r.minY = y;
		 if(y>r.maxY) r.maxY = y;
		 r.nPoints++;
		 //calculation of average, prevent overflow by upgrade step by step
		 r.averageY += (y-r.averageY)*1.0/(i+1); 
		 regions.add(r);
	 }
	 //process last region
	 r.minY = r.minY - margin;
	 r.maxY = r.maxY + margin;
	 System.out.println(r.toString());
	 regions.add(r);
	 return regions;
 }


RawData projectPoints(String datasetName,List<Region> regions,double pixelMinX,double pixelMaxX, double pixelMinY,double pixelMaxY) throws SQLException{
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
			double xvalue=rawData.XYColumns.get(xAttributes.get(i)).get(k);
			double yvalue=rawData.XYColumns.get(yAttributes.get(i)).get(k);
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


Boolean isWithinBounds(double xvalue,double yvalue,double xmin,double xmax, double ymin,double ymax,List<Region> regions,double pixelMinX,double pixelMaxX, double pixelMinY,double pixelMaxY){
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
	HashMap<String,ArrayList<Double>> XYColumns= new HashMap<String,ArrayList<Double>>();
	HashMap<String,ArrayList<String>> otherColumns= new HashMap<String,ArrayList<String>>();
	for(int i=0;i<xAttributes.size();i++){
		projections=projections+","+xAttributes.get(i);
		XYColumns.put(xAttributes.get(i), new ArrayList<Double>());
	}
	for(int i=0;i<yAttributes.size();i++){
		projections=projections+","+yAttributes.get(i);
		XYColumns.put(yAttributes.get(i), new ArrayList<Double>());
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
	HashMap<String,Double> min = new HashMap<>();
	HashMap<String,Double> max = new HashMap<>();
	HashMap<String,Double> sum = new HashMap<>();
	HashMap<String,Double> variance = new HashMap<>();
	
	int rowCount=0;
	while(rs.next()){
		for(int i=0;i<xAttributes.size();i++){
			double val=rs.getDouble(xAttributes.get(i));
			XYColumns.get(rs.getDouble(xAttributes.get(i))).add(rowCount,val);
			sum.put(xAttributes.get(i),sum.get(xAttributes.get(i))+val);
			if(val<min.get(xAttributes.get(i)))
				min.put(xAttributes.get(i),val);
			if(val>max.get(xAttributes.get(i)))
				max.put(xAttributes.get(i),val);			
		}
				
		for(int i=0;i<yAttributes.size();i++){
			double val=rs.getDouble(xAttributes.get(i));
			sum.put(yAttributes.get(i),sum.get(yAttributes.get(i))+val);
			XYColumns.get(rs.getDouble(yAttributes.get(i))).add(rowCount,rs.getDouble(yAttributes.get(i)));
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
	rs.close();
	sqlQueryExecutor.st.close();
	
	
	//normalize Y values

	for(int i=0;i<yAttributes.size();i++){
		ArrayList<Double> yColumn= XYColumns.get(yAttributes.get(i));
		
		double mean=sum.get(yAttributes.get(i))/yColumn.size();
		double var=0;
		for(int j=0;j<yColumn.size();j++){
			var+=(double) Math.pow(yColumn.get(j)-mean,2);
		}
		var=var/yColumn.size();
		double std=(double) Math.sqrt(var);
		
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
		HashMap<String,Double> min= new HashMap<>();
		HashMap<String,Double> max= new HashMap<>();
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
			double mn=min.get(k);
			double mx=max.get(k);
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
