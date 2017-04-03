package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.roaringbitmap.IntIterator;
import org.roaringbitmap.RoaringBitmap;

import edu.uiuc.zenvisage.data.remotedb.SQLQueryExecutor;
import edu.uiuc.zenvisage.model.Result;
import edu.uiuc.zenvisage.model.ScatterPlotQuery;
import edu.uiuc.zenvisage.model.ScatterResult;
import edu.uiuc.zenvisage.model.Sketch;
import edu.uiuc.zenvisage.model.ScatterResult.Tuple;
import edu.uiuc.zenvisage.service.ScatterRank;
import edu.uiuc.zenvisage.service.ScatterRep;

public class ScatterVCNode extends VisualComponentNode {

	private ScatterPlotQuery query;
	private Map<String, ScatterResult> data = new HashMap<String, ScatterResult>();
	
	public ScatterPlotQuery getQuery() {
		return query;
	}

	public void setQuery(ScatterPlotQuery query) {
		this.query = query;
	}

	public Map<String, ScatterResult> getData() {
		return data;
	}

	public void setData(Map<String, ScatterResult> data) {
		this.data = data;
	}

	public ScatterVCNode(ScatterPlotQuery query, VisualComponentQuery vc, LookUpTable table, SQLQueryExecutor sqlQueryExecutor, Sketch sketch) {
		super(vc, table, sqlQueryExecutor, sketch);
		// TODO Auto-generated constructor stub
		this.query = query;
	}

	@Override
	public void execute() {
		// execute scatter
		
		// data fetcher
		data = getScatterData(query);
		// data transformer??

		// task processor: (eg find the charts that match the scatter data in these rectangles)
		
	}
	
	private Map<String, ScatterResult> getScatterData(ScatterPlotQuery query) {
		Map<String, ScatterResult> result = new HashMap<String, ScatterResult>();
		String yAxis = query.yAxis;
		String xAxis = query.xAxis;
		String zAxis = query.zAxis;
		// TODO: convert to postgres format
		List<String> yValues = null; //database.getUnIndexedColumn(yAxis);
		List<String> xValues = null; //database.getUnIndexedColumn(xAxis);
		Map<String,RoaringBitmap> zValues = null; // database.getIndexedColumn(zAxis);
		if (zValues == null) return null;
		List<String> zKeys = new ArrayList<String>(zValues.keySet());
		for (String zKey : zKeys) {
			List<Tuple> points = new ArrayList<Tuple>();
			RoaringBitmap bitset = zValues.get(zKey);
			IntIterator it = bitset.getIntIterator();
			while (it.hasNext()) {
				int row = it.next();
				Tuple tuple = new Tuple(Double.parseDouble(xValues.get(row)),Double.parseDouble(yValues.get(row)));
				points.add(tuple);
			}
			ScatterResult currResult = new ScatterResult(points,0,zKey);
			result.put(zKey, currResult);
		}
		return result;
	}
}
