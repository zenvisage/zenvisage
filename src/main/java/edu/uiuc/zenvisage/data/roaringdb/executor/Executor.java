package edu.uiuc.zenvisage.data.roaringdb.executor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.roaringbitmap.IntIterator;
import org.roaringbitmap.RoaringBitmap;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.primitives.Doubles;

import edu.uiuc.zenvisage.data.Query;
import edu.uiuc.zenvisage.data.Query.CompositeFilter;
import edu.uiuc.zenvisage.data.Query.CompositeFilterOperator;
import edu.uiuc.zenvisage.data.Query.FilterPredicate;
import edu.uiuc.zenvisage.data.roaringdb.db.ColumnMetadata;
import edu.uiuc.zenvisage.data.roaringdb.db.Database;
import edu.uiuc.zenvisage.model.Point;
import edu.uiuc.zenvisage.model.ScatterPlotQuery;
import edu.uiuc.zenvisage.model.ScatterResult;
import edu.uiuc.zenvisage.data.Query.Filter;


public class Executor {
	private Database database;

	private LinearInterpolator interpolater = new LinearInterpolator();
	
	public Executor(Database database){
		this.database=database;
	}
	
	public Database getDatabase() {
		return database;
	}


	public void setDatabase(Database database) {
		this.database = database;
	}


	public LinearInterpolator getInterpolater() {
		return interpolater;
	}


	public void setInterpolater(LinearInterpolator interpolater) {
		this.interpolater = interpolater;
	}

	public ExecutorResult getData(Query query) throws InterruptedException{
	    //-----Look at the vde.database package for help. 
		//Steps
		
		// Not necessary-- Get the x,y,z values
		
		// First Apply filter.
		int n = (int) database.rowCount;
		RoaringBitmap bitSet= new RoaringBitmap();
		for(int i=0;i<n;i++)
			bitSet.add(i);
		if(query.getFilter()!=null){			
			bitSet = RoaringBitmap.and(bitSet, applyFilters(query));
		}
		// Group By  and Aggregation

		return applyGroupByAndAggregation(query,bitSet);
	}

	
	// Assuming no nesting for now, Assuming filters on indexed columns only
	public RoaringBitmap applyFilters(Query query){
	       // Apply Flat AND or OR.
		   // Return array of setbits.	
		Query.Filter filter= query.getFilter();
		RoaringBitmap result;					
		if(filter.isComposite()){
			CompositeFilter compositeFilter = (CompositeFilter) filter;
			List<Filter> subFilters=compositeFilter.getSubFilters();
			FilterPredicate filterPredicate1=(FilterPredicate) subFilters.get(0);
			result = database.getColumn(filterPredicate1);			
			for(int i=1;i<subFilters.size();i++){
				RoaringBitmap column = database.getColumn((FilterPredicate) subFilters.get(i));
				if(CompositeFilterOperator.AND==compositeFilter.getOperator()){
					result.and(column);
				}
				if(CompositeFilterOperator.OR==compositeFilter.getOperator()){
					result.or(column);
				}				
			}		
		}		
		else
			result = database.getColumn((FilterPredicate) filter);	
		//System.out.println("Ending Filters");
		return result;		
	}

	//works only for two levels--group by z and x, and indexed values
	public ExecutorResult applyGroupByAndAggregation(Query query, RoaringBitmap bitSet){
	  // APPLY Group bys;
		String yAxis = query.getAggregationVarible();
		String[] groupBy = query.getGroupBy().split(",");
		String zAxis = groupBy[0];
		String xAxis = groupBy[1];
		// API for finding missing x or y axis
		/*
		if (yAxis == null) {
			return applyGroupByAndAggregationWithMissingY(query, zAxis, xAxis, bitSet);
		}
		if (xAxis == null) {
			return applyGroupByAndAggregationWithMissingX(query, zAxis, yAxis, bitSet);
		}*/
		
		List<String> yValues = database.getUnIndexedColumn(yAxis);
		List<String> xValues2 = database.getUnIndexedColumn(xAxis);
		Map<String,RoaringBitmap> zValues = database.getIndexedColumn(zAxis);
//		Map<String,RoaringBitmap> xValues = database.getColumn(xAxis);
//		if (zValues == null || xValues == null) return null;
		
		List<String> zKeys = new ArrayList<String>(zValues.keySet());
//		List<String> xKeys = new ArrayList<String>(xValues.keySet());

		Collections.sort(zKeys);
//		Collections.sort(xKeys,new Comparator<String>() {
//			public int compare(String a, String b) {
//				try {
//					return Float.valueOf(a).compareTo(Float.valueOf(b));
//				}
//				catch(NumberFormatException e) {
//				  // sort alphabetically
//					return a.compareTo(b);
//				}
//					
//			}
//		});
		
		// add x axis key (string) value (float - index) mappings
		BiMap<String, Float> xMap = HashBiMap.create();
//		Float index = (float) 0;
//		for (String xKey : xKeys) {
//			xMap.put(xKey, index);
//			index++;
//		}
	
		LinkedHashMap<String,LinkedHashMap<Float,Float>> result = new LinkedHashMap<String,LinkedHashMap<Float,Float>>();
		for (String zKey : zKeys) {
			// check whether we need this zValue;
			if (RoaringBitmap.and(zValues.get(zKey), bitSet).getCardinality()==0) continue;
			LinkedHashMap<Float,Float> innerMap = new LinkedHashMap<Float,Float>();
			// do linear interpolation
			

			HashMap<Float,Float> innerMap2 = new HashMap<Float,Float>();
			HashMap<Float,Integer> countX = new HashMap<Float,Integer>();			
			for (int i : zValues.get(zKey)) {
				float currentX = Float.valueOf(xValues2.get(i));
				float currentY = Float.valueOf(yValues.get(i));
				
				if (innerMap2.containsKey(currentX)) {
					innerMap2.put(currentX, innerMap2.get(currentX) + currentY);
					countX.put(currentX, countX.get(currentX)+1);
				}
				else {
					innerMap2.put(currentX, currentY);
					countX.put(currentX, 1);
				}
			}
			
			TreeMap<Float, Float> innerMap3 = new TreeMap<Float, Float>();
			for (float x : innerMap2.keySet()) {
				float y = innerMap2.get(x)/countX.get(x);
				innerMap3.put(x, y);
			}
			
//			for (float x: innerMap3.keySet()) {
//				System.out.println(x + "\t" +  innerMap3.get(x));
//			}
			
			LinkedHashMap<Float, Float> innerMap4 = new LinkedHashMap<Float, Float>();
			for (float x : innerMap3.keySet()) {
				innerMap4.put(x, innerMap3.get(x));
			}
			
			
			
			
//			ArrayList<Float> xs = new ArrayList<Float>();
//			ArrayList<Float> ys = new ArrayList<Float>();
//			Map<Float,Float> yvs = new HashMap<Float,Float>();
//			float xmin = Float.valueOf(xMap.get(xKeys.get(0)));
//			float xmax = Float.valueOf(xMap.get(xKeys.get(xKeys.size()-1)));
//			for (String xKey : xKeys) {
//				// check whether this xKey has been filtered
//				if (RoaringBitmap.and(z//			ArrayList<Float> xs = new ArrayList<Float>();
//			ArrayList<Float> ys = new ArrayList<Float>();
//			Map<Float,Float> yvs = new HashMap<Float,Float>();
//			float xmin = Float.valueOf(xMap.get(xKeys.get(0)));
//			float xmax = Float.valueOf(xMap.get(xKeys.get(xKeys.size()-1)));
//			for (String xKey : xKeys) {
//				// check whether this xKey has been filtered
//				if (RoaringBitmap.and(zValues.get(zKey), bitSet).getCardinality()==0) continue;
//				RoaringBitmap bitset = RoaringBitmap.and(zValues.get(zKey), xValues.get(xKey));
//				float sum = 0;
//				int count = 0;
//				IntIterator it = bitset.getIntIterator();
//				while (it.hasNext()) {
//					sum += Float.valueOf(yValues.get(it.next()));
//					count++;
//				}
//				if (query.getAggregationFunc().equals("avg")) {
//					sum /= count;
//				}
//				
//				if (count == 0) {
//					sum = 0;
//				}
//				else {
//					xs.add(xMap.get(xKey));
//					ys.add(sum);
//					yvs.put(xMap.get(xKey), sum);
//				}
//				innerMap.put(Float.valueOf(xMap.get(xKey)), (float) sum);
//			}Values.get(zKey), bitSet).getCardinality()==0) continue;
//				RoaringBitmap bitset = RoaringBitmap.and(zValues.get(zKey), xValues.get(xKey));
//				float sum = 0;
//				int count = 0;
//				IntIterator it = bitset.getIntIterator();
//				while (it.hasNext()) {
//					sum += Float.valueOf(yValues.get(it.next()));
//					count++;
//				}
//				if (query.getAggregationFunc().equals("avg")) {
//					sum /= count;
//				}
//				
//				if (count == 0) {
//					sum = 0;
//				}
//				else {
//					xs.add(xMap.get(xKey));
//					ys.add(sum);
//					yvs.put(xMap.get(xKey), sum);
//				}
//				innerMap.put(Float.valueOf(xMap.get(xKey)), (float) sum);
//			}
//			// add more points for interpolation
//			xs.add(0,xmin-1);
//			ys.add(0,ys.get(0));
//			xs.add(xmax+1);
//			ys.add(ys.get(ys.size()-1));
			
			
			
//			// interpolate
//			double[] x = Doubles.toArray(xs);
//			double[] y = Doubles.toArray(ys);  
//			PolynomialSplineFunction psf = interpolater.interpolate(x, y);
	
			
			// BINNING
//			LinkedHashMap<Float,Float> binnedMap = new LinkedHashMap<Float,Float>();
		
//	 		int count=0;
//	        double yval=0.0;
//	        float min=Float.valueOf(xMap.get(xKeys.get(0)));
//	        float max=Float.valueOf(xMap.get(xKeys.get(xKeys.size()-1)));
//	        float previousValue=min;
//	    	int numBins = 100;
//			double binSize =(max-min)/numBins;     
			//System.out.println(binSize);
			/*
			for (String xKey : xKeys) {
				float keyValue = Float.valueOf(xMap.get(xKey));
			   if (yvs.containsKey(keyValue)) {
					yval += yvs.get(keyValue);
					innerMap.put(keyValue, yvs.get(keyValue));
				}
				else {
					float interpolate = (float) psf.value(keyValue);
					yval += interpolate;
					innerMap.put(keyValue, interpolate);
				}
				if((x.length > numBins)){
					int binno=(int) ((keyValue-min)/binSize);
					float binnedValue= (float)(binno*binSize+min) ;
					//System.out.println(binnedValue+":::"+previousValue);
			        if(binnedValue!=previousValue){
			        if(count>0){
			        	binnedMap.put(keyValue, (float) (yval/count));
			        	//System.out.println(binnedMap.toString()+"...........................");
					    count=0;
					    yval=0;
			        }
					previousValue=binnedValue;
				}else{
					count += 1;
				}
				}
				
			}*/
		//	System.out.println(binnedMap.toString()+"binned...........................");
		//	System.out.println(binSize+"binSize"+max+":"+min+":"+x.length+":"+numBins);
		//	System.out.println(innerMap.toString()+"innermap....................");
			//if(x.length > numBins) 			
			//result.put(zKey, binnedMap);
			//else
			result.put(zKey, innerMap4);
				
		}
		ExecutorResult executorResult = new ExecutorResult(result, xMap);
		return executorResult;
	}
	
	public Map<String, ScatterResult> getScatterData(ScatterPlotQuery query) {
		Map<String, ScatterResult> result = new HashMap<String, ScatterResult>();
		String yAxis = query.yAxis;
		String xAxis = query.xAxis;
		String zAxis = query.zAxis;
		List<String> yValues = database.getUnIndexedColumn(yAxis);
		List<String> xValues = database.getUnIndexedColumn(xAxis);
		Map<String,RoaringBitmap> zValues = database.getIndexedColumn(zAxis);
		if (zValues == null) return null;
		List<String> zKeys = new ArrayList<String>(zValues.keySet());
		for (String zKey : zKeys) {
			List<Point> points = new ArrayList<Point>();
			RoaringBitmap bitset = zValues.get(zKey);
			IntIterator it = bitset.getIntIterator();
			while (it.hasNext()) {
				int row = it.next();
				Point tuple = new Point(Float.parseFloat(xValues.get(row)),Float.parseFloat(yValues.get(row)));
				points.add(tuple);
			}
			ScatterResult currResult = new ScatterResult(points,0,zKey);
			result.put(zKey, currResult);
		}
		return result;
	}

	
	// Missing X axis need to change all upstream code to account for the situation where array lengths are different
	private LinkedHashMap<String,LinkedHashMap<Float,Float>> applyGroupByAndAggregationWithMissingX(Query query, String zAxis, String yAxis, RoaringBitmap bitSet) {
		List<String> yValues = database.getUnIndexedColumn(yAxis);
		return null;
	}

	// Finding the missing Y axis without binning and interpolation and assume y axis normalization
	private LinkedHashMap<String,LinkedHashMap<Float,Float>> applyGroupByAndAggregationWithMissingY(Query query, String zAxis, String xAxis, RoaringBitmap bitSet) {
		// getting the xAxis
		Map<String,RoaringBitmap> xValues = database.getColumn(xAxis);
		List<String> xKeys = new ArrayList<String>(xValues.keySet());
		Collections.sort(xKeys,new Comparator<String>() {
			public int compare(String a, String b) {
				return Float.valueOf(a).compareTo(Float.valueOf(b));
			}
		});
		// getting all of the possible yAxis
		Map<String,ColumnMetadata> yAxisColumns = database.databaseMetaData.getyAxisColumns();
		List<String> yKeys = new ArrayList<String>(yAxisColumns.keySet());
		Collections.sort(yKeys);
		// container for the result
		LinkedHashMap<String,LinkedHashMap<Float,Float>> result = new LinkedHashMap<String,LinkedHashMap<Float,Float>>();
		for (String yKey : yKeys) {
			List<String> yValues = database.getUnIndexedColumn(yKey);
			LinkedHashMap<Float,Float> innerMap = new LinkedHashMap<Float,Float>();
			for (String xKey : xKeys) {
				RoaringBitmap bitset = xValues.get(xKey);
				float sum = 0;
				int count = 0;
				IntIterator it = bitset.getIntIterator();
				while (it.hasNext()) {
					sum += Float.valueOf(yValues.get(it.next()));
					count++;
				}
				if (query.getAggregationFunc().equals("avg")) {
					sum /= count;
				}
				
				if (count == 0) {
					sum = 0;
				}
				innerMap.put(Float.valueOf(xKey), (float) sum);
			}
			result.put(yKey, innerMap);
		}
		return result;
	}




}
	
	

