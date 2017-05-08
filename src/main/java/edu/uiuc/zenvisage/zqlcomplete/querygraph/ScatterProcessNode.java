package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uiuc.zenvisage.model.Chart;
import edu.uiuc.zenvisage.model.Point;
import edu.uiuc.zenvisage.model.Result;
import edu.uiuc.zenvisage.model.ScatterPlotQuery;
import edu.uiuc.zenvisage.model.ScatterResult;
import edu.uiuc.zenvisage.service.ScatterRep;
import edu.uiuc.zenvisage.zqlcomplete.executor.Processe;
import edu.uiuc.zenvisage.zqlcomplete.querygraph.QueryNode.State;

public class ScatterProcessNode extends ProcessNode {
	static final Logger logger = LoggerFactory.getLogger(ScatterProcessNode.class);

	public ScatterProcessNode(Processe process_, LookUpTable table) {
		super(process_, table);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void execute() {
		if (isBlocked()) {
			this.state = State.BLOCKED;
			return;
		}
		
		this.state = State.RUNNING;
		//
		Result output;
		if(process.getMethod().equals("Filter")) {
			logger.info("TScatterFilter");
			ScatterVCNode vcNode = (ScatterVCNode) lookuptable.get(process.getArguments().get(0));
			List<Polygon> rectangles = vcNode.getVc().getSketch().getPolygons();
			if (!rectangles.isEmpty()) {
				removeNonRectanglePoints(vcNode.getData(), rectangles);
				Result result = scatterRepExecution();
				logger.info(result.outputCharts.toString());
			}
		}
		if(process.getMethod().equals("Rep")) {
			output = scatterRepExecution();
		} else if (process.getMethod().equals("Rank")) {
			output = scatterRankExecution();
		}
		this.state = State.FINISHED;
	}
	
	// currently data transform + task processing
	private Result scatterRepExecution() {
		// lookup table the VCNode we depend on
		ScatterVCNode vcNode = (ScatterVCNode) lookuptable.get(process.getArguments().get(0));
		Result output = new Result();
		computeScatterRep(vcNode.getData(), vcNode.getVc(), output);
		return output;
	}
	
	public static void computeScatterRep(Map<String, ScatterResult> input, VisualComponentQuery q, Result finalOutput) {
		List<ScatterResult> datas = new ArrayList<ScatterResult>(input.values());
		int len = Math.min(datas.size(), q.getNumOfResults());
		for (int i = 0; i < len; i++) {
			Chart chartOutput = new Chart();
			ScatterResult data = datas.get(i);
			System.out.println(data.name + Integer.toString(data.count / data.points.size()));
			chartOutput.setxType((i+1)+" : "+data.name);
			chartOutput.setyType(q.getY().getAttributes().get(0));
			for (Point point : data.points) {
				chartOutput.xData.add(Float.toString(point.getX()));
				chartOutput.yData.add(Float.toString(point.getY()));
			}
			finalOutput.outputCharts.add(chartOutput);
		}
	}
	
	private Result scatterSimilarity() {
		return null;
	}
	
	private Result scatterRankExecution() {
		// lookup table the VCNode we depend on
		// task processor: (eg find the charts that match the scatter data in these rectangles)
		return null;
	}
	
	/**
	 * Given scatter plot charts, remove points from each that are not in the query rectangle
	 * @param allDataCharts (side effect: modified)
	 * @param rectangles
	 */
	private void removeNonRectanglePoints(Map<String, ScatterResult> allDataCharts, List<Polygon> polygons) {
		for (ScatterResult chart : allDataCharts.values()) {
			for(Iterator<Point> it = chart.points.iterator(); it.hasNext();) {
				Point point = it.next();
				if(!inArea(point,polygons)) {
					it.remove();					
				}
			}
		}
	}
	
	private static boolean inArea(Point point, List<Polygon> polygons) {
		for (Polygon r : polygons) {
			if (r.inArea(point)) return true;
		}
		return false;
	}

}
