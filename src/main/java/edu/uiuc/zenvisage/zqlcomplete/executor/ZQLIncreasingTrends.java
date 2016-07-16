/**
 * 
 */
package edu.uiuc.zenvisage.zqlcomplete.executor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.map.MultiValueMap;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.uiuc.zenvisage.data.roaringdb.db.Database;
import edu.uiuc.zenvisage.service.distance.Distance;
import edu.uiuc.zenvisage.service.utility.Normalization;


/**
 *
 */
public class ZQLIncreasingTrends extends ZQLAnalysis {
	/* Whether rank trends in descending order */
	public boolean descending = true;
	public ZQLPiecewiseAggregation paa;
	public int k=20;

	public ZQLIncreasingTrends( Database inMemoryDatabase, Distance distance, Normalization normalization, ZQLPiecewiseAggregation paa) {
		super(inMemoryDatabase, distance, normalization);
		// TODO Auto-generated constructor stub
		this.paa = paa;
	}

	public ZQLRowProcessResult generateAnalysis(ZQLRow zqlRow, ZQLTableResult zqlTableResult) throws JsonProcessingException {
		Processe processe = zqlRow.getProcesse();
		HashMap<String, String> parameters = processe.getParameters();
		String v1=zqlRow.getName().getName();
		if(parameters.containsKey("viz1") && parameters.get("viz1")!=null)
			v1=parameters.get("viz1");
		ZQLRowVizResult viz=zqlTableResult.getZqlRowResults().get(v1).getZqlRowVizResults().get(0);
		ZQLRowVizResult dataViz=viz;
		LinkedHashMap<String, LinkedHashMap<Float, Float>> output = dataViz.getVizData();
		
		// TODO Auto-generated method stub
		ArrayList<String> mappings = new ArrayList<String>();
		for(String key : output.keySet()) {
			 mappings.add(key);
		}
		Set<Float> ignore = new HashSet<Float>();
		paa.setPAAwidth(output,dataViz.getX());
		double[][] normalizedgroup = paa.applyPAAonData(output,ignore,dataViz.getX());
		List<Integer> orders=computeOrders(normalizedgroup,mappings);;
		
		ZQLRowProcessResult zQLRowProcessResult= new ZQLRowProcessResult();
		zQLRowProcessResult.setzType(dataViz.getZ());
	    for(int i=0;i<orders.size();i++){
	    	System.out.println(mappings.get(orders.get(i)));
	      	zQLRowProcessResult.getzValues().add(mappings.get(orders.get(i)));
	    }
		
		return zQLRowProcessResult;
	}
	
	/**
	 * @param normalizedgroups
	 * @param queryTrend
	 * @param mappings
	 * @return
	 */
	
	public double getslope(double[] y) {
        
        int N = y.length;
        double[] x= new double[N];
        // first pass
        double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;
        for (int i = 0; i < N; i++){
        	x[i]=i;
            sumx  += x[i];
        }
        for (int i = 0; i < N; i++)
            sumx2 += x[i]*x[i];
        for (int i = 0; i < N; i++)
            sumy  += y[i];
        double xbar = sumx / N;
        double ybar = sumy / N;

        // second pass: compute summary statistics
        double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
        for (int i = 0; i < N; i++) {
            xxbar += (x[i] - xbar) * (x[i] - xbar);
            yybar += (y[i] - ybar) * (y[i] - ybar);
            xybar += (x[i] - xbar) * (y[i] - ybar);
        }
        double slope  = xybar / xxbar;
        return slope;  
        
    }

	
		
	
	public List<Integer> computeOrders(double[][] normalizedgroups, ArrayList<String> mappings) {
		List<Integer> orders = new ArrayList<Integer>();
		MultiValueMap indexOrder =new MultiValueMap();
    	List<Double> slopes = new ArrayList<Double>(); 
    	for(int i = 0;i < normalizedgroups.length;i++) {
	    		double slope = getslope(normalizedgroups[i]);
	    		slopes.add(slope);	
	    		indexOrder.put(slope,i);
	//    		System.out.println(mappings.get(i)+"\t"+slope);
	    	}   
    
    	Collections.sort(slopes);
    	if (descending)
    		Collections.reverse(slopes);
    	for(Double d : slopes){
    		System.out.println(d);
       		if(descending)
    		{	if(d<0)
    				break;
    		}
    		else{
    			if(d>0)
    				break;
    		}
			ArrayList values = (ArrayList)indexOrder.get(d);
		    Integer val = (Integer) values.get(0);
			orders.add((val));
			indexOrder.remove(d,val);			 
		 }
    	return orders;		
	}

	/**
	 * @return the descending
	 */
	public boolean isDescending() {
		return descending;
	}

	/**
	 * @param descending the descending to set
	 */
	public void setDescending(boolean descending) {
		this.descending = descending;
	}

}