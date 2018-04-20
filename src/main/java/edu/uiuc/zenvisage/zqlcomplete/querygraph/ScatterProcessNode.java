package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uiuc.zenvisage.data.remotedb.VisualComponent;
import edu.uiuc.zenvisage.data.remotedb.VisualComponentList;
import edu.uiuc.zenvisage.data.remotedb.WrapperType;
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
			long startTime = System.currentTimeMillis();
			if (!rectangles.isEmpty()) {
				removeNonRectanglePoints(vcNode.getVcList(), rectangles);
				output = scatterRepExecution();
				logger.info("Number of points in first scatterplot: " + output.getOutputCharts().get(0).count);
				/*
				try {
					Chart chart = output.getOutputCharts().get(0);
					String result = new ObjectMapper().writeValueAsString(chart);
					logger.info(result);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			}
			long endTime = System.currentTimeMillis();
			logger.info("Filtering points took " + (endTime - startTime) + "ms");
		}
		if(process.getMethod().equals("Rep")) {
			output = scatterRepExecution();
		} else if (process.getMethod().equals("Rank")) {
			ScatterVCNode vcNode = (ScatterVCNode) lookuptable.get(process.getArguments().get(0));
			List<Polygon> rectangles = vcNode.getVc().getSketch().getPolygons();
			List<String> values = scatterRankExecution(rectangles);
			double[] scores = new double[values.size()];
			for (int i = 0; i < values.size(); i++) {
				scores[i] = i;
			}
			
			// z1
		    String axisName = process.getAxisList1().get(0);
			AxisVariable axisVar = (AxisVariable) lookuptable.get(axisName);
			// newAxisVar is a subset of axisVar after processing
			AxisVariable newAxisVar = new AxisVariable(axisVar.getAttributeType(), axisVar.getAttribute(), values, scores);
			// v1, axisvar
			lookuptable.put(process.getVariables().get(0), newAxisVar);
		}

		this.state = State.FINISHED;
	}
	
	// currently data transform + task processing
	private Result scatterRepExecution() {
		// lookup table the VCNode we depend on
		ScatterVCNode vcNode = (ScatterVCNode) lookuptable.get(process.getArguments().get(0));
		Result output = new Result();
		computeScatterRep(vcNode.getVcList(), vcNode.getVc(), output);
		return output;
	}
	
	// TODO: broken: should return axis variables
	public static void computeScatterRep(VisualComponentList input, VisualComponentQuery q, Result finalOutput) {
		List<VisualComponent> vcList = input.getVisualComponentList();
		int len = Math.min(vcList.size(), q.getNumOfResults());
		if (q.getNumOfResults() == 0) {
			len = vcList.size();
		}
		for (int vc_index = 0; vc_index < len; vc_index++) {
			Chart chartOutput = new Chart();
			VisualComponent vc = vcList.get(vc_index);
			//System.out.println(data.name + Integer.toString(data.count / data.points.size()));
			chartOutput.setxType((vc_index+1)+" : "+vc.getxAttribute());
			chartOutput.setyType(q.getY().getAttributes().get(0));
			chartOutput.count = vc.getPoints().getXList().size();
			ArrayList<WrapperType> xList = vc.getPoints().getXList();
			ArrayList<WrapperType> yList = vc.getPoints().getYList();
			for (int i = 0; i < xList.size(); i++) {
				chartOutput.xData.add(xList.get(i).toString());
				chartOutput.yData.add(yList.get(i).toString());
			}
			finalOutput.outputCharts.add(chartOutput);
		}
	}
	
	private Result scatterSimilarity() {
		return null;
	}
	
	private List<String> scatterRankExecution(List<Polygon> polygons) {
		// lookup table the VCNode we depend on
		// task processor: (eg find the charts that match the scatter data in these rectangles)
		ScatterVCNode vcNode = (ScatterVCNode) lookuptable.get(process.getArguments().get(0));
		Result output = new Result();
		return computeScatterRank(vcNode.getVcList(), vcNode.getVc(), output, polygons);
	}
	
	public static List<String> computeScatterRank(VisualComponentList input, VisualComponentQuery q, Result finalOutput, List<Polygon> polygons) {
		List<VisualComponent> vcList = input.getVisualComponentList();
		List<Double> ratioList =new ArrayList<Double>();
		int len = Math.min(vcList.size(), q.getNumOfResults());
		if (q.getNumOfResults() == 0) {
			len = vcList.size();
		}
		for (int vc_index = 0; vc_index < len; vc_index++) {
			VisualComponent vc = vcList.get(vc_index);
			double ratio = computeBoundingBoxRatio(vc,polygons);
			ratioList.add(ratio);
		}
		
		 Map<Integer, Double> m = new HashMap<> ();
		 for (int i = 0; i < len; i++) {
		   m.put(i, ratioList.get(i));
		 }
		int [] ranks = m.entrySet().stream().sorted(Entry.comparingByValue()).mapToInt(Entry::getKey).toArray();
		
		for (int vc_index = 0; vc_index <  len; vc_index++) {
			Chart chartOutput = new Chart();
			VisualComponent vc = vcList.get(vc_index);
			chartOutput.setxType((vc_index+1)+" : "+vc.getxAttribute());
			chartOutput.setyType(vc.getyAttribute());
			chartOutput.setzType(vc.getZValue().toString());
//			System.out.println("Testing " + vc.getxAttribute());
//			System.out.println("Testing " + q.getY().getAttributes().get(1));
//			System.out.println("Testing " + vc.getyAttribute());
			chartOutput.count = vc.getPoints().getXList().size();
			chartOutput.setRank(ranks[vc_index]);
			System.out.println("VC_index: " + vc_index + " , Rank is: " + ranks[vc_index]);
			ArrayList<WrapperType> xList = vc.getPoints().getXList();
			ArrayList<WrapperType> yList = vc.getPoints().getYList();
//			System.out.println("PP Adding aggregate scatterplot with x attribute " + vc.getxAttribute() + " and y attribute " + vc.getyAttribute());
//			System.out.println("Num points is " + vc.getPoints().getXList().size());
			

			
			for (int i = 0; i < xList.size(); i++) {
				chartOutput.xData.add(xList.get(i).toString());
				chartOutput.yData.add(yList.get(i).toString());
			}
			finalOutput.outputCharts.add(chartOutput);
			//finalOutput.outputCharts.sort((o1, o2) -> ((Integer)o1.getRank()).compareTo((Integer)o2.getRank()));
		}
		finalOutput.outputCharts.sort((o1, o2) -> ((Integer)o1.getRank()).compareTo((Integer)o2.getRank()));
		List<String> res = new ArrayList<>();
		for (Chart c : finalOutput.outputCharts) {
			res.add(c.getzType());
		}
		int xa = 1;
		return res;
	}
	
	/**
	 * Given scatter plot charts, remove points from each that are not in the query rectangle
	 * @param allDataCharts (side effect: modified)
	 * @param rectangles
	 */
	private void removeNonRectanglePoints(VisualComponentList vcList, List<Polygon> polygons) {
		
		for (VisualComponent vc : vcList.getVisualComponentList()) {
			ArrayList<WrapperType> xList = vc.getPoints().getXList();
			ArrayList<WrapperType> yList = vc.getPoints().getYList();
			
			Iterator<WrapperType> yIt = yList.iterator();
			for(Iterator<WrapperType> xIt = xList.iterator(); xIt.hasNext();) {
				float x = xIt.next().getNumberValue();
				float y = yIt.next().getNumberValue();
				Point point = new Point(x,y);
				if(!inArea(point,polygons)) {
					xIt.remove();
					yIt.remove();
				}
			}
		}
	}
	
	private static double computeBoundingBoxRatio(VisualComponent vc, List<Polygon> polygons) {
		
		ArrayList<WrapperType> xList = vc.getPoints().getXList();
		ArrayList<WrapperType> yList = vc.getPoints().getYList();
		double numPointsInside = 0; 
		double numPointsOutside = 0;
		Iterator<WrapperType> yIt = yList.iterator();
		for(Iterator<WrapperType> xIt = xList.iterator(); xIt.hasNext();) {
			float x = xIt.next().getNumberValue();
			float y = yIt.next().getNumberValue();
			Point point = new Point(x,y);
			if(inArea(point,polygons)) {
				numPointsInside ++; 
			}
			else {
				numPointsOutside ++; 
			}
		}
		
		return (numPointsInside/numPointsOutside); 
		
	}
	
	private static boolean inArea(Point point, List<Polygon> polygons) {
		for (Polygon r : polygons) {
			if (r.inArea(point)) return true;
		}
		return false;
	}

}
