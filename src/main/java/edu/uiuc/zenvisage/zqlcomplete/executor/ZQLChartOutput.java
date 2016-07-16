package edu.uiuc.zenvisage.zqlcomplete.executor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import com.fasterxml.jackson.core.JsonProcessingException;

import edu.uiuc.zenvisage.model.Chart;
import edu.uiuc.zenvisage.model.Result;

/**
 *
 */
public class ZQLChartOutput {
		
/*	*//**
	public void chartOutput(List<double[][]> output,List<LinkedHashMap<String,LinkedHashMap<Float,Float>>> orig,List<Integer> orders,ArrayList<String> mappings,  Result finalOutput) throws JsonProcessingException{
		if (args.outlierCount==0)
			args.setOutlierCount(4);
		int outputLength = args.outlierCount;
		if(output != null && !output.isEmpty()){
			outputLength = output.get(0).length;
		}
		for(int i = 0; i < Math.min(outputLength, args.outlierCount); i++) {
			// initialize a new chart
			int j = 0;
			for (double[][] result : output) {
				Chart chartOutput = new Chart();
				chartOutput.setxType((i+1)+" : "+mappings.get(orders.get(i)));
				chartOutput.setyType(args.getSketchPoints()[j].aggrFunc+"("+args.getSketchPoints()[j].yAxis+")");
				// fill in chart data
				String key = mappings.get(orders.get(i));
				LinkedHashMap<Float,Float> points = orig.get(j).get(key);
				if (points == null) continue;
				int c = 0;
				for(Float k : points.keySet()) {
					chartOutput.xData.add(Float.toString(k));
					chartOutput.yData.add(Double.toString(result[orders.get(i)][c]));
					c++;
				}
				finalOutput.outputCharts.add(chartOutput);
				j++;
			}
		}

		return;	
	}
	*/
	
	
	
	public static Result chartOutput(ZQLRowResult zqlRowResult) throws JsonProcessingException{
		Result finalOutput=new Result();
		int outputLength = 50;
		List<ZQLRowVizResult> orig = zqlRowResult.getZqlRowVizResults() ;
/*		Normalization outputNormalization = new Original();
		 // reformat database data
		 DataReformation dataReformatter = new DataReformation(outputNormalization);
		 double[][] output  = dataReformatter.reformatData(orig);*/
		
		List<Iterator<Entry<String, LinkedHashMap<Float, Float>>>> iteratorList= new ArrayList<>();
		List<String> xs= new ArrayList<>();
		List<String> ys= new ArrayList<>();
		List<String> zs= new ArrayList<>();
		
		
		for (ZQLRowVizResult output  : orig) {
			xs.add(output.getX());
			ys.add(output.getY());
			zs.add(output.getZ());
			Set<Entry<String, LinkedHashMap<Float, Float>>> vizentryset = output.getVizData().entrySet();
			Iterator<Entry<String, LinkedHashMap<Float, Float>>> it = vizentryset.iterator();
			iteratorList.add(it);
		}
				
			
		for(int i = 0; i < Math.min(orig.get(0).getVizData().size(), outputLength); i++) {
				// initialize a new chart
			int j = 0;
			
		
			for(Iterator<Entry<String, LinkedHashMap<Float, Float>>> it:iteratorList){
				Entry<String, LinkedHashMap<Float, Float>> entry = it.next();
				String zvalue=entry.getKey();
				Set<Entry<Float, Float>> innerkeyset = entry.getValue().entrySet();
				if(innerkeyset.size()<0)
					continue;
				Iterator<Entry<Float, Float>> innerit = innerkeyset.iterator();
				Chart chartOutput = new Chart();
				chartOutput.setxType((i+1)+" : "+zvalue);
				chartOutput.setyType("avg"+"("+ys.get(j)+")");
				while(innerit.hasNext()){
					Entry<Float, Float> innerentry = innerit.next();
					Float xvalue=innerentry.getKey();		
					Float yvalue=innerentry.getValue();		
					chartOutput.xData.add(Float.toString(xvalue));
					chartOutput.yData.add(Float.toString(yvalue));
				}

				j++;
				finalOutput.outputCharts.add(chartOutput);
				
			}
		}
		return finalOutput;
	
 }
	
}
