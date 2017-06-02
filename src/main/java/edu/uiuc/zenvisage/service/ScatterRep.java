package edu.uiuc.zenvisage.service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import edu.uiuc.zenvisage.model.ScatterPlotQuery;
import edu.uiuc.zenvisage.model.*;

/**
 * @author xiaofo
 *
 */
@Deprecated
public class ScatterRep {

	public static void compute(Map<String, ScatterResult> output, ScatterPlotQuery q, Result finalOutput) {
		List<ScatterResult> datas = new ArrayList<ScatterResult>(output.values());
		generateCharts(datas, q.numOfResults, q.yAxis, finalOutput);
	}
	
	public static void generateCharts(List<ScatterResult> datas, int numOfResult, String yAxis, Result finalOutput) {
		int len = Math.min(datas.size(), numOfResult);
		for (int i = 0; i < len; i++) {
			Chart chartOutput = new Chart();
			ScatterResult data = datas.get(i);
			System.out.println(data.name + Integer.toString(data.count / data.points.size()));
			chartOutput.setxType((i+1)+" : "+data.name);
			chartOutput.setyType(yAxis);
			for (Point point : data.points) {
				chartOutput.xData.add(Float.toString(point.getXval()));
				chartOutput.yData.add(Float.toString(point.getYval()));
			}
			finalOutput.outputCharts.add(chartOutput);
		}
	}
}
