package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.roaringbitmap.IntIterator;
import org.roaringbitmap.RoaringBitmap;

import edu.uiuc.zenvisage.data.remotedb.Points;
import edu.uiuc.zenvisage.data.remotedb.SQLQueryExecutor;
import edu.uiuc.zenvisage.data.remotedb.VisualComponentList;
import edu.uiuc.zenvisage.data.remotedb.WrapperType;
import edu.uiuc.zenvisage.data.remotedb.VisualComponent;
import edu.uiuc.zenvisage.model.Result;
import edu.uiuc.zenvisage.model.ScatterPlotQuery;
import edu.uiuc.zenvisage.model.ScatterResult;
import edu.uiuc.zenvisage.model.Sketch;
import edu.uiuc.zenvisage.model.ScatterResult.Tuple;
import edu.uiuc.zenvisage.service.ScatterRank;
import edu.uiuc.zenvisage.service.ScatterRep;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLRow;
import edu.uiuc.zenvisage.zqlcomplete.querygraph.VisualComponentQuery.Rectangle;

public class ScatterVCNode extends VisualComponentNode {

	private Map<String, ScatterResult> data = new HashMap<String, ScatterResult>();
	
	public Map<String, ScatterResult> getData() {
		return data;
	}

	public void setData(Map<String, ScatterResult> data) {
		this.data = data;
	}

	public ScatterVCNode(VisualComponentQuery vc, LookUpTable table, SQLQueryExecutor sqlQueryExecutor, Sketch sketch) {
		super(vc, table, sqlQueryExecutor, sketch);
		// TODO Auto-generated constructor stub

	}

	@Override
	public void execute() {
		// execute scatter
		
		// data fetcher
		data = getScatterData();
		
		// data transformer
		List<Rectangle> rectangles = this.getVc().getRectangles();
		if (!rectangles.isEmpty()) {
			removeNonRectanglePoints(data, rectangles);
		}
		
		// After we have executed this scatter vc node, we have fetched the data and performed data transformation.
		// This allows us to easily move on to the task processor, while keeping the VCNode -> ProcessNode flow, with having to add a specific data transform node
	}
	
	private Map<String, ScatterResult> getScatterData() {
		Map<String, ScatterResult> result = new HashMap<String, ScatterResult>();
		
		// old method: grab info from ScatterPlotQuery
		//String yAxis = query.yAxis; // eg year, yValues = [2001, 2002, ...]
		//String xAxis = query.xAxis; // eg soldPrice, xValues = [10000, 12330, ...]
		//String zAxis = query.zAxis; // eg State, zValues = ['CA', 'MN', ...]
		
		// new method: grab info from the VisualComponentQuery
		// call SQL backend (for scatter plot, no aggregation method)
		ZQLRow row = buildRowFromNode("");
		try {
			// run zqlquery on this ZQLRow on the database table db
			if(this.db == null || this.db.equals("")) {
				// default case
				sqlQueryExecutor.ZQLQueryEnhanced(row, "real_estate");
			}
			sqlQueryExecutor.ZQLQueryEnhanced(row, this.db);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		VisualComponentList vcList = sqlQueryExecutor.getVisualComponentList();
		for (VisualComponent vc : vcList.getVisualComponentList()) {
			String zValue = vc.getZValue().getStrValue();
			Points points = vc.getPoints();
			List<WrapperType> xValues = points.getXList();
			List<WrapperType> yValues = points.getYList();
			List<Tuple> tuples = new ArrayList<Tuple>();
			for(int i = 0; i < points.getXList().size(); i++) {
				Tuple tuple = new Tuple((xValues.get(i).getNumberValue()), yValues.get(i).getNumberValue());
				tuples.add(tuple);
			}
			ScatterResult currResult = new ScatterResult(tuples,0,zValue);
			result.put(zValue, currResult);
			
		}
		
		return result;
	}
	
	/**
	 * Given scatter plot charts, remove points from each that are not in the query rectangle
	 * @param allDataCharts (side effect: modified)
	 * @param rectangles
	 */
	private void removeNonRectanglePoints(Map<String, ScatterResult> allDataCharts, List<Rectangle> rectangles) {
		for (ScatterResult chart : allDataCharts.values()) {
			for(Iterator<Tuple> it = chart.points.iterator(); it.hasNext();) {
				Tuple point = it.next();
				if(!inArea(point,rectangles)) {
					it.remove();					
				}
			}
		}
	}
	
	private static boolean inArea(Tuple tuple, List<Rectangle> rectangles) {
		for (Rectangle r : rectangles) {
			if (r.inArea(tuple)) return true;
		}
		return false;
	}
	
	private void simpleBinning(Map<String, ScatterResult> allDataCharts, int bins) {
		int rows = 100; // height
		int cols = 200; // width
		int cells = rows*cols;
		// grid interval 
		double S = Math.sqrt(cells/bins);
		
		Tuple grid_center = new Tuple(S/2, S/2);
		while (grid_center.y < rows && grid_center.x < cols) {
			
			// add grid to array, maybe count points here
			
			// move right, next grid in this row
			grid_center.x = grid_center.x + S;
			if (grid_center.x + S > cols) {
				// move down, start of next grid row
				grid_center.y = grid_center.y + S;
				grid_center.x = S/2;
			}
		}
		// 2D array for each bin.
		// look through all tuples, add them to each bin.
		
		// instead of having X points in a grid, just display the grid center, and the count (or size)
		// Our ScatterResult object currently supports points as just tuples of (x,y)
		// to support binning, we probably need (x,y, count)? 
		
		// how does front end use the binning? 
		
	}
}
