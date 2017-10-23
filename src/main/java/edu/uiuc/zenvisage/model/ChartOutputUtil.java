package edu.uiuc.zenvisage.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.util.FastMath;

import edu.uiuc.zenvisage.data.remotedb.Points;
import edu.uiuc.zenvisage.data.remotedb.VisualComponent;
import edu.uiuc.zenvisage.data.remotedb.VisualComponentList;
import edu.uiuc.zenvisage.data.remotedb.WrapperType;
import edu.uiuc.zenvisage.model.BaselineQuery;
import edu.uiuc.zenvisage.model.ZvQuery;
import edu.uiuc.zenvisage.service.cluster.OutlierTrend;
import edu.uiuc.zenvisage.service.cluster.RepresentativeTrend;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.BiMap;

/**
 *
 */
public class ChartOutputUtil {
	public Result finalOutput;
	public ZvQuery args;
	public BiMap<Float, String> xMap;

	/**
	 * @param finalOutput
	 * @param args
	 */
	public ChartOutputUtil(Result finalOutput, ZvQuery args, BiMap<String, Float> xMap) {
		this.finalOutput = finalOutput;
		this.args = args;
		this.xMap = xMap.inverse();
	}
	
	
  public void chartOutput(LinkedHashMap<String,LinkedHashMap<Float,Float>> orig,ZvQuery args, Result finalOutput)
  {		 System.out.println("chartoutput executing!");
  		Iterator<String> it =orig.keySet().iterator();
  		
  		
		while(it.hasNext()){
		    String entry = it.next();
			Chart chartOutput = new Chart();
			/*Separate this call to rank and x axix and return separately*/
			//chartOutput.setxType((i+1)+" : "+mappings.get(orders.get(i)));
			chartOutput.setxType(args.xAxis);
			chartOutput.setyType(args.yAxis);
			chartOutput.setzType(args.groupBy);
			chartOutput.title = entry;
			chartOutput.setXRange(args.xRange);
			chartOutput.setConsiderRange(args.considerRange);

			// fill in chart data
			LinkedHashMap<Float,Float> points = orig.get(entry);
			if (points == null) continue;
			for(Float k : points.keySet()) {
				chartOutput.xData.add(Double.toString(k));
				chartOutput.yData.add(Double.toString(points.get(k)));
			}
			finalOutput.outputCharts.add(chartOutput);
			
        }
}
	/**
	 * @param result
	 * @param orig
	 * @param orders
	 * @param mappings
	 * @param args
	 * @param finalOutput
	 * @throws JsonProcessingException
	 */
	public void chartOutput(double[][] output,LinkedHashMap<String,LinkedHashMap<Float,Float>> orig,List<Integer> orders, List<Double> orderedDistances, ArrayList<String> mappings, List<BiMap<Float,String>> xMaps, ZvQuery args, Result finalOutput) throws JsonProcessingException{
		if (args.outlierCount==0)
			args.setOutlierCount(4);
		int outputLength = args.outlierCount;
		//if(output != null && !output.isEmpty()){
			outputLength = output.length;
		//}

//		Double range = 0.0;
//		if(orderedDistances != null && orderedDistances.size()!= 0){
////			range = orderedDistances.get(0) - orderedDistances.get(orderedDistances.size()-1);
//			range = orderedDistances.get(orderedDistances.size()-1);
//		}
			
	    int index = Math.min(outputLength-1, args.outlierCount);
	    if(index >= orderedDistances.size() || index <0){
	    	index = 0;
	    }
		double maxDist = 0;
		if(orderedDistances!=null && orderedDistances.size()!=0)
			maxDist = orderedDistances.get(index);
		//System.out.println("orderedDistances.size():"+Double.toString(orderedDistances.size()));
		double normDist;
//		System.out.println("args.isOutputNormalized():"+args.isOutputNormalized());
		for(int i = 0; i < Math.min(outputLength, args.outlierCount); i++) {
			//System.out.println("orderedDistances:"+Double.toString(orderedDistances.get(i)));
//			double normDist =normalize(orderedDistances, range, i);
			if (args.isOutputNormalized()) {
				normDist =normalize(orderedDistances,maxDist, i);
			}else {
				//System.out.println("not normalized dist");
				normDist =orderedDistances.get(i);
			}
			
			boolean displayThisViz = false;
			if (args.minDisplayThresh!=0.0){
				 if (normDist>=args.minDisplayThresh){
					 displayThisViz = true;
				 }
			}else{
				displayThisViz = true;
			}
			if (displayThisViz){
				// initialize a new chart
				Chart chartOutput = new Chart();
				/*Separate this call to rank and x axix and return separately*/
				//chartOutput.setxType((i+1)+" : "+mappings.get(orders.get(i)));
				chartOutput.setxType(args.xAxis);
				chartOutput.setyType(args.yAxis);
				chartOutput.setzType(args.groupBy);
				chartOutput.title = mappings.get(orders.get(i));
				chartOutput.setRank(i+1);
				chartOutput.setNormalizedDistance(normDist);
				// chartOutput.setyType(args.getSketchPoints()[j].aggrFunc+"("+args.getSketchPoints()[j].yAxis+")");
				chartOutput.setDistance(orderedDistances.get(i));
				chartOutput.setXRange(args.xRange);
				chartOutput.setConsiderRange(args.considerRange);

				// fill in chart data
				String key = mappings.get(orders.get(i));
				LinkedHashMap<Float,Float> points = orig.get(key);
				if (points == null) continue;
				for(Float k : points.keySet()) {
					chartOutput.xData.add(Double.toString(k));
					chartOutput.yData.add(Double.toString(points.get(k)));
				}
				finalOutput.outputCharts.add(chartOutput);
			}
		}

		return;
	}

	
	public void convertToRawViz(VisualComponentList v){	
		for(Chart chart:finalOutput.outputCharts){
			String ztype=chart.zType;
			chart.xData=new ArrayList<>();
			chart.yData=new ArrayList<>();
			VisualComponent vc =v.ZToVisualComponents.get(ztype);
			Points points =vc.getPoints();
			int i=0;
			for(WrapperType x:points.getXList()){
				chart.xData.add(Double.toString(x.getNumberValue()));
				chart.yData.add(Double.toString(points.getYList().get(i).getNumberValue()));
				i++;
			}
		}
	
	}
	
	
	
	
	
	/*z= (xi-min(x)) /(max(x)-min(x))*/
//	public double normalize(List<Double> orderedDistances, double range, int i){
//		if (range == 0)
//			return 1.0;
//		else
//			return (range - orderedDistances.get(i)) / range;
//	}
//	public double normalize(List<Double> orderedDistances, int i){
////		double sum=0;
//		double max= Double.NEGATIVE_INFINITY;
//		double[] distArr=new double[orderedDistances.size()];
//		for (int j = 0; j<orderedDistances.size(); j++){
//			double val = orderedDistances.get(j);
////			sum+=val;
//			distArr[j]=val;
////			if (val>max){
////				max=val;
////			}
////			System.out.println("val:"+Double.toString(val));
////			System.out.println("imax:"+Double.toString(max));
//		}
//		double mean = StatUtils.mean(distArr);
//		double std = FastMath.sqrt(StatUtils.variance(distArr));
//
//		System.out.println("min:"+orderedDistances.get(0));
//		System.out.println("max:"+orderedDistances.get(orderedDistances.size()-1));
//		System.out.println("mean:"+Double.toString(mean));
//		System.out.println("std:"+Double.toString(std));
//		double fakeMin = mean-3*std;
//		double fakeMax = mean+3*std;
//		System.out.println("fakeMin:"+Double.toString(fakeMin));
//		System.out.println("fakeMax:"+Double.toString(fakeMax));
//		System.out.println("orderedDistances.size():"+orderedDistances.size());
//		double[] cleanedDistArr=new double[orderedDistances.size()];
//		for (int j = 0; j<orderedDistances.size(); j++){
//			double val = orderedDistances.get(j);
//			if (val<=fakeMax){
//				cleanedDistArr[j]=val;
//			}
////			System.out.println("val:"+Double.toString(val));
////			System.out.println("imax:"+Double.toString(max));
//		}
//		System.out.println("cleanedDistArr.length:"+cleanedDistArr.length);
//		mean = StatUtils.mean(cleanedDistArr);
//		std = FastMath.sqrt(StatUtils.variance(cleanedDistArr));
//		System.out.println("mean:"+Double.toString(mean));
//		System.out.println("std:"+Double.toString(std));
//		
//		System.out.println("result:"+Double.toString((fakeMax-orderedDistances.get(i))/(fakeMax)));
////		return  (orderedDistances.get(i)-fakeMin)/(max-fakeMin);
//		if (orderedDistances.get(i)<= orderedDistances.get(0)){
//			return 1.0;
//		}
//		return (fakeMax-orderedDistances.get(i))/(fakeMax);
//		
////		if (range == 0)
////			return 1.0;
////		else
////			return (range - orderedDistances.get(i)) / range;
//	}
	public double normalize(List<Double> orderedDistances, double maxDist, int i){
//		double max = 0.0;
////		orderedDistances.size()*0.1
//		if(orderedDistances != null){
//			if (orderedDistances.size()>5){
//				//		range = orderedDistances.get(0) - orderedDistances.get(orderedDistances.size()-1);
//				max = orderedDistances.get(orderedDistances.size()-5);
//
//			}else if (orderedDistances.size()!=0){
//				max = orderedDistances.get(orderedDistances.size()-1);
//			}
//		}
//		if (range == 0)
//			return 1.0;
//		else
//		System.out.println("max:"+Double.toString(max));
		
		return (maxDist- orderedDistances.get(i)) /maxDist;
	}
	

	public void chartOutput(List<RepresentativeTrend> representativeTrends,LinkedHashMap<String,LinkedHashMap<Float,Float>> orig, ZvQuery args, Result finalOutput) throws JsonProcessingException{

		for(int i = 0; i < representativeTrends.size(); i++) {
			// initialize a new chart
			Chart chartOutput = new Chart();
			RepresentativeTrend repTrend = representativeTrends.get(i);
			/*Separate this call to rank and x axix and return separately*/
			//chartOutput.setxType((i+1)+" : "+repTrend.getKey());
			//chartOutput.setxType(repTrend.getKey());

			// chartOutput.setxType(repTrend.getKey());
			// chartOutput.setRank(i+1);
			// chartOutput.setyType(args.aggrFunc+"("+args.yAxis+")");

			chartOutput.setxType(args.xAxis);
			chartOutput.setyType(args.yAxis);
			chartOutput.setzType(args.groupBy);
			chartOutput.setRank(i+1);
			chartOutput.setTitle(repTrend.getKey());
			
			// fill in chart data
			LinkedHashMap<Float,Float> points = orig.get(repTrend.getKey());
			int c = 0;
			double[] p = repTrend.getP();
			for(Float k : points.keySet()) {
				chartOutput.xData.add(Float.toString(k));
				chartOutput.yData.add(Float.toString(points.get(k)));
				c++;
			}
			chartOutput.setCount(repTrend.getSimilarTrends());
			finalOutput.outputCharts.add(chartOutput);
		}

		return;
	}

	public void chartOutput(List<OutlierTrend> outlierTrends,LinkedHashMap<String,LinkedHashMap<Float,Float>> orig, ZvQuery args, Result finalOutput, int flag) throws JsonProcessingException{

		for(int i = 0; i < outlierTrends.size(); i++) {
			// initialize a new chart
			Chart chartOutput = new Chart();
			OutlierTrend outTrend = outlierTrends.get(i);
			/*Separate this call to rank and x axix and return separately*/
			//chartOutput.setxType((i+1)+" : "+repTrend.getKey());
			//chartOutput.setxType(repTrend.getKey());

			// chartOutput.setxType(repTrend.getKey());
			// chartOutput.setRank(i+1);
			// chartOutput.setyType(args.aggrFunc+"("+args.yAxis+")");
			chartOutput.setxType(args.xAxis);
			chartOutput.setyType(args.yAxis);
			chartOutput.setzType(args.groupBy);
			chartOutput.setNormalizedDistance(outTrend.getNormalizedDistance());
			chartOutput.title = outTrend.getKey();
			chartOutput.setRank(i+1);

			// fill in chart data
			LinkedHashMap<Float,Float> points = orig.get(outTrend.getKey());
			int c = 0;
			double[] p = outTrend.getP();
			for(Float k : points.keySet()) {
				chartOutput.xData.add(Float.toString(k));
				chartOutput.yData.add(Float.toString(points.get(k)));
				c++;
			}
			chartOutput.count = (int) outTrend.getWeightedDistance();
			finalOutput.outputCharts.add(chartOutput);
		}

		return;
	}
	
	// all baseline one time stuff...
	public void baselineOutput(List<LinkedHashMap<String,LinkedHashMap<Float,Float>>> output, BaselineQuery bq, Result finalOutput) {
		for (String zAxis : output.get(0).keySet()) {
			boolean filtered = false;
			Chart[] outputArray = new Chart[output.size()];
			for (int index = 0; index < output.size(); index++) {
				LinkedHashMap<Float,Float> trend = output.get(index).get(zAxis);
				// can add more logic here -- filter some trends
				outputArray[index] = new Chart();
				// outputArray[index].setxType(zAxis);
				// outputArray[index].setyType(bq.aggrFunc+"("+bq.yAxis.get(index)+")");
				outputArray[index].setxType(bq.xAxis);
				outputArray[index].setyType(bq.yAxis.get(index));
				outputArray[index].setzType(zAxis);
				List<String> yOperators;
				List<Float> yValues;
				if (index == 0) {
					yOperators = bq.y1Operator;
					yValues = bq.y1Value;
				}
				else {
					yOperators = bq.y2Operator;
					yValues = bq.y2Value;
				}
				for (Float k : trend.keySet()) {
					for (int i = 0; i < bq.xOperator.size(); i++) {
						Operator xOperator = Operator.getValue((bq.xOperator.get(i)));
						if (xOperator.filter(bq.xValue.get(i), k)) {
							Operator yOperator = Operator.getValue(yOperators.get(i));
							//System.out.println(trend.get(k)+" > "+yValues.get(i));
							//System.out.println(yOperator.filter(yValues.get(i), trend.get(k)));
							if (!yOperator.filter(yValues.get(i), trend.get(k))) {
								filtered = true;
								break;
							}
						}
					}
					if (filtered) break;
					outputArray[index].xData.add(Float.toString(k));
					outputArray[index].yData.add(Float.toString(trend.get(k)));
				}
				if (filtered) break;
			}

			if (!filtered) {
				for (int i = 0; i < output.size(); i++)
				finalOutput.outputCharts.add(outputArray[i]);
			}
		}
	}

	public enum Operator
	{
		EQUAL("=") {
			@Override public boolean filter(float constraint, float value) {
				return (int) constraint == (int) value;
			}
		},
		GREATER_THAN(">") {
			@Override public boolean filter(float constraint, float value) {
				return value > constraint;
			}
		},
		GREATER_THAN_OR_EQUAL(">=") {
			@Override public boolean filter(float constraint, float value) {
				return value >= constraint;
			}
		},
		LESS_THAN("<") {
			@Override public boolean filter(float constraint, float value) {
				return value < constraint;
			}
		},
		LESS_THAN_OR_EQUAL("<=") {
			@Override public boolean filter(float constraint, float value) {
				return value <= constraint;
			}
		},
		NOT_EQUAL("!=") {
			@Override public boolean filter(float constraint, float value) {
				return (int) constraint != (int) value;
			}
		};
	    // You'd include other operators too...

	    private final String text;

	    private Operator(String text) {
	        this.text = text;
	    }

	    // Yes, enums *can* have abstract methods. This code compiles...
	    public abstract boolean filter(float constraint, float value);

	    @Override public String toString() {
	        return text;
	    }

	    public static Operator getValue(String text) {
	    	for (Operator op : Operator.values()) {
	    		if (op.text.equals(text)) {
	    			return op;
	    		}
	    	}
	    	return null;
	    }
	}

}
