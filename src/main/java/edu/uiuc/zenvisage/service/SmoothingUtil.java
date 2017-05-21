package edu.uiuc.zenvisage.service;

import java.util.Iterator;
import java.util.LinkedHashMap;
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import edu.uiuc.zenvisage.model.ZvQuery;

/**
 * @author tarique
 *
 */
public class SmoothingUtil {

	static int robustness=2;

	
	// FIXME: Integrate this with frontend.
	public static double[] applySmoothing(String type,double[] xvals,double [] yvals, int window, int robustness){
		if(type=="movingaverage") return movingAverage(yvals, window);
		if(type=="exponentialmovingaverage") return exponentialMovingAverage(yvals, window);
		if(type=="leossInterpolation") return leossInterpolation(xvals,yvals,window,robustness);	
		if(type=="gaussian") return gaussianConvolution(xvals,yvals,window,robustness);	
		//TODO: Handle this in a better way.
		return null;
	}


	
	// FIXME: Integrate this with frontend.
	public static LinkedHashMap<String,LinkedHashMap<Float,Float>> applySmoothing(
			LinkedHashMap<String,LinkedHashMap<Float,Float>> data,
			ZvQuery zvQuery
			)
	{		
		
		
		return data;
	}
	
	
	// FIXME: Integrate this with frontend.
	//Smoothing function wrapper on deprecated data format.
	public static double[][]  applySmoothing(
			double[][] data,
			ZvQuery zvQuery
			)
	{			
		// Extract type,window,robustness from the zvQuery object and call the below functions.
		return data;
	}
	
	
	// FIXME: Integrate this with frontend.
	public static double[]  applySmoothing(
			double[] data,
			ZvQuery zvQuery
			)
	{			
		// Extract type,window,robustness from the zvQuery object and call the below functions.
		return data;
	}
	
	
	
	
	//Smoothing function wrapper on deprecated data format.
	public static LinkedHashMap<String,LinkedHashMap<Float,Float>> applySmoothing(
			LinkedHashMap<String,LinkedHashMap<Float,Float>> data,
			String type,
			int window,
			int robustness
			)
	{		
		Iterator<String> it = (Iterator<String>) data.keySet();
		while(it.hasNext()){
			String s=it.next();
		    int length=data.get(s).size();
		    double []xvals = new double[length];
			double []yvals = new double[length];
			LinkedHashMap<Float, Float> vals = data.get(s);
			Iterator<Float> itval = (Iterator<Float>) vals.keySet();
			int pos=0;
			while(itval.hasNext()){
				xvals[pos]=(double)itval.next();
				yvals[pos]=(double)vals.get(xvals[pos]);
				pos++;	
			}
			double[] ysmoothedvals=applySmoothing(type,xvals,yvals,window,robustness);
			itval = (Iterator<Float>) vals.keySet();
			for(int i=0;i<xvals.length;i++){
				vals.put((float)xvals[i], (float)ysmoothedvals[i]);
			}
		}
	   return data;
	}
	
	
	//Smoothing function wrapper on deprecated data format.
	public static double[][]  applySmoothing(
			double[][] data,
			String type,
			int window,
			int robustness
			)
	{			
		for(int i=0;i<data.length;i++){
			data[i]=applySmoothing(type,null,data[i], window, robustness);
		}
		return data;
	}
	
	
	
	// Take the average of window/2 elements on both sides. For elements near the boundaries, on one side, we look at fewer elements. 
	// TODO: Fix: Currently I assume that yvals size will always be more than window size.
	private static double[] movingAverage(double [] yvals, int window){
	    double [] ySmoothedVals = new double[yvals.length];
	    double sum=yvals[0];
	    int pos = 0;
	    int sumsize=0;
	    while(pos< yvals.length){
	    	sum+=yvals[pos];
	    	sumsize++;
	    	if(sumsize>window)
	    	{
	    		sum=sum-yvals[pos-window];
	    		sumsize--;
	    	}
	    	if(pos>=window/2)
	    	{
	    		ySmoothedVals[(pos-sumsize)]=sum/sumsize;	
	    	}	
	    }
	    
	    for(int i=(yvals.length-window);i<yvals.length;i++){
	    	sum=sum-yvals[i];
	    	sumsize--;
	    	ySmoothedVals[i]=sum/sumsize;
	    }
	    
	    return ySmoothedVals;
	
   	}
	
	// It looks at only the previous values
	private static double[] exponentialMovingAverage(double [] yvals, int decayfactor){
		 double [] ySmoothedVals = new double[yvals.length];
		 ySmoothedVals[0]=yvals[0];
		 for(int i=1;i<yvals.length;i++){
		    ySmoothedVals[i]=yvals[i]+decayfactor*ySmoothedVals[i-1];
		 }
		return ySmoothedVals;
	}
	
	
	
	
	private static double[] leossInterpolation(double [] xvals,double [] yvals, int window,int robustness){
		LoessInterpolator loess = new LoessInterpolator(window, robustness);
		double [] ySmoothedVals = loess.smooth(xvals,yvals);
		return ySmoothedVals;
	}

	
	//currently assuming that Y values are equi-distant, so we look at previous and next windowsize/2 elements. However, in reality Y values might far away. 
	private static double[] gaussianConvolution(double [] xvals,double [] yvals, int windowsize, double std){
		double[] gkernel=createGaussianfilter(windowsize,std);
		 double [] ySmoothedVals = new double[yvals.length];
		 ySmoothedVals[0]=yvals[0];
		 int halfwindowsize=(int) Math.floor(windowsize/2);
		 for(int i=0;i<yvals.length;i++){
			 ySmoothedVals[i]=0.0;
			 int j=i-halfwindowsize;
			 int pos=0;
			 while(j<i+halfwindowsize){
				 if(j>0 && j<yvals.length){
				 ySmoothedVals[i]=ySmoothedVals[i]+gkernel[pos]*yvals[j]; 
				 }
				 pos++;
				 j++;
			 }
		 }
		return ySmoothedVals;	 
		
	}
	
	
	// windowsize must be odd
	private static double[] createGaussianfilter(int windowsize, double std){
		double sigma=2.0*std*std;
		double sqrt_2pi= Math.sqrt(2*Math.PI);
		// making windowsize odd if it isn't
		if(windowsize%2==0)
			windowsize=windowsize+1;
		
		double[] gkernel= new double[windowsize];
		int centerPos=(int) Math.floor(windowsize/2);
		double sum=0.0;
		for(int i=0;i<windowsize;i++){
			int x=Math.abs(i-centerPos);
			gkernel[i]=Math.exp(-(x*x)/sigma)/(sqrt_2pi*std);
			sum+=gkernel[i];
		}
		//normalizing
		for(int i=0;i<windowsize;i++){
			gkernel[i]=gkernel[i]/sum;
		}
		
		return gkernel;
	
	}
	
	
}
