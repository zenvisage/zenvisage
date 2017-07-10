package edu.uiuc.zenvisage.service;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import edu.uiuc.zenvisage.model.ZvQuery;

/**
 * @author tarique
 *
 */
public class SmoothingUtil {

	static int robustness=1;

	
	// FIXME: Integrate this with frontend.
	public static double[] applySmoothing(String type,double[] xvals,double [] yvals, double windowcoeff){
//		System.out.println("Smoothing type="+type+", # vals="+yvals.length + ", windowcoeff="+windowcoeff);
		int window= (int) (yvals.length*windowcoeff);
		if(windowcoeff<0.01 || window<1){
			return yvals;
		}
			
		if(type.equals("movingaverage")) return movingAverage(yvals, window);
		if(type.equals("exponentialmovingaverage")) return exponentialMovingAverage(yvals, windowcoeff);
		
		if((type.equals("leossInterpolation") || type.equals("gaussian")) && xvals==null){
			xvals = new double[yvals.length];
			for(int i=0;i<yvals.length;i++)
			{
				xvals[i]=i;
			}
		}
		
		if (type.equals("leossInterpolation")) return leossInterpolation(xvals,yvals,windowcoeff,robustness);	
		if (type.equals("gaussian"))return gaussianConvolution(xvals,yvals,window);	
			//TODO: Handle this in a better way.
//		System.out.println("No smoothing applied");
		return yvals;
	}


	
	// FIXME: Integrate this with frontend.
	public static LinkedHashMap<String,LinkedHashMap<Float,Float>> applySmoothing(
			LinkedHashMap<String,LinkedHashMap<Float,Float>> data,
			ZvQuery zvQuery
			)
	{		
		String type=zvQuery.smoothingType;
		double coeff=zvQuery.smoothingcoefficient;
		applySmoothing(data, type,coeff);
		return data;
	}
	
	
	public static double[][]  applySmoothing(
			double[][] data,
			ZvQuery zvQuery
			)
	{			
		String type=zvQuery.smoothingType;
		double coeff=zvQuery.smoothingcoefficient;
		applySmoothing(data, type,coeff);
		return data;
	}
	
	
	// FIXME: Integrate this with frontend.
	public static double[]  applySmoothing(
			double[] data,
			ZvQuery zvQuery
			)
	{			
		String type=zvQuery.smoothingType;
		double coeff=zvQuery.smoothingcoefficient;
		applySmoothing(type,null,data,coeff);
		return data;
	}
	
	
	
	
	//Smoothing function wrapper on deprecated data format.
	public static LinkedHashMap<String,LinkedHashMap<Float,Float>> applySmoothing(
			LinkedHashMap<String,LinkedHashMap<Float,Float>> data,
			String type,
			double windowcoeff
			)
	{		
		Iterator<String> it = (Iterator<String>) data.keySet().iterator();
		while(it.hasNext()){
			String s=it.next();
		    int length=data.get(s).size();
		    double []xvals = new double[length];
			double []yvals = new double[length];
			LinkedHashMap<Float, Float> vals = data.get(s);
			int pos=0;
			for (Map.Entry<Float,Float> entry : vals.entrySet()) {
				xvals[pos]=(double) entry.getKey();
				yvals[pos]=(double)entry.getValue();
				pos++;
			}
			double[] ysmoothedvals=applySmoothing(type,xvals,yvals,windowcoeff);
//			System.out.println("Smoothing finished");
//			System.out.println("X length:" + xvals.length);
//			System.out.println("Y length:" +ysmoothedvals.length);
			for(int i=0;i<xvals.length;i++){
				vals.put((float)xvals[i], (float)ysmoothedvals[i]);
			}
		}
	   return data;
	}
	
	public static double[][]  applySmoothing(
			double[][] data,
			String type,
			double windowcoeff
			)
	{			
		for(int i=0;i<data.length;i++){
			data[i]=applySmoothing(type,null,data[i], windowcoeff);
		}
		return data;
	}
	
	
	
	// Take the average of window/2 elements on both sides. For elements near the boundaries, on one side, we look at fewer elements. 
	// TODO: Fix: Currently I assume that yvals size will always be more than window size.
	private static double[] movingAverage(double [] yvals, int window){
		System.out.println("Moving avergae smoothing function called: # values= "+yvals.length+", window size="+window);
	    double [] ySmoothedVals = new double[yvals.length];
	    double sum=yvals[0];
	    int pos = 0;
	    int sumsize=0;
	    int updatePos=-window/2;
	    while(pos< yvals.length){
	    	sum+=yvals[pos];
	    	sumsize++;
	    	if(sumsize>window)
	    	{
	    		sum=sum-yvals[pos-window];
	    		sumsize--;
	    	}
	    	if(updatePos>=0)
	    	{
	    		ySmoothedVals[updatePos]=sum/sumsize;	
	    	}
	    	pos++;
	    	updatePos++;
	    }
	    
	    for(int i=updatePos;i<yvals.length;i++){
	    	sum=sum-yvals[i];
	    	sumsize--;
	    	ySmoothedVals[i]=sum/sumsize;
	    }
	    
	    System.out.println("Moving average smoothing finished : "+yvals.length+", window size: "+window);
	    return ySmoothedVals;
	
   	}
	
	// It looks at only the previous values
	private static double[] exponentialMovingAverage(double [] yvals, double decayfactor){
		 double [] ySmoothedVals = new double[yvals.length];
		 ySmoothedVals[0]=yvals[0];
		 for(int i=1;i<yvals.length;i++){
		    ySmoothedVals[i]=decayfactor*yvals[i]+(1-decayfactor)*ySmoothedVals[i-1];
		 }
		return ySmoothedVals;
	}
	
	
	
	
	private static double[] leossInterpolation(double [] xvals,double [] yvals, double window,int robustness){
		System.out.println("Loess"+xvals.length+":"+yvals.length);
		
		LoessInterpolator loess = new LoessInterpolator(window, robustness);
		
		double [] ySmoothedVals = loess.smooth(xvals,yvals);
		return ySmoothedVals;
	}

	
	//currently assuming that Y values are equi-distant, so we look at previous and next windowsize/2 elements. However, in reality Y values might be far away. 
	private static double[] gaussianConvolution(double [] xvals,double [] yvals, int windowsize){
		double sum = 0;
		for (int i = 0; i < yvals.length; i++)
			sum+=yvals[i];
		double mean=sum/yvals.length;
		double std=0;
		int length=yvals.length;
		for (int i = 0; i < yvals.length; i++)
			{	std+=((yvals[i]-mean)*(yvals[i]-mean))/length;
			}
	 	std = Math.sqrt(std); 
	 				 			
		double[] gkernel=createGaussianfilter(windowsize,std);
		 double [] ySmoothedVals = new double[yvals.length];
		 ySmoothedVals[0]=yvals[0];
		 int halfwindowsize=(int) Math.floor(windowsize/2);
		 for(int i=0;i<yvals.length;i++){
			 ySmoothedVals[i]=yvals[i];
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
