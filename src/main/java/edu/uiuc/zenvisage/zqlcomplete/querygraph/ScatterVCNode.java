package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
		// data transformer??

		// task processor: (eg find the charts that match the scatter data in these rectangles)
		
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
}
