package edu.uiuc.zenvisage.zql;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.roaringbitmap.IntIterator;
import org.roaringbitmap.RoaringBitmap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uiuc.zenvisage.data.remotedb.Points;
import edu.uiuc.zenvisage.data.remotedb.SQLQueryExecutor;
import edu.uiuc.zenvisage.data.remotedb.VisualComponentList;
import edu.uiuc.zenvisage.data.remotedb.WrapperType;
import edu.uiuc.zenvisage.data.remotedb.VisualComponent;
import edu.uiuc.zenvisage.model.Chart;
import edu.uiuc.zenvisage.model.Point;
import edu.uiuc.zenvisage.model.Result;
import edu.uiuc.zenvisage.model.ScatterPlotQuery;
import edu.uiuc.zenvisage.model.ScatterResult;
import edu.uiuc.zenvisage.model.Sketch;
import edu.uiuc.zenvisage.service.ScatterRank;
import edu.uiuc.zenvisage.service.ScatterRep;
import edu.uiuc.zenvisage.zqlcomplete.executor.VizColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.XColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.YColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLRow;

public class ScatterVCNode extends VisualComponentNode {
	static final Logger logger = LoggerFactory.getLogger(ScatterVCNode.class);

	private VisualComponentList vcList;
	
	public VisualComponentList getVcList() {
		return vcList;
	}

	public void setVcList(VisualComponentList vcList) {
		this.vcList = vcList;
	}

	public ScatterVCNode(VisualComponentQuery vc, LookUpTable table, SQLQueryExecutor sqlQueryExecutor) {
		super(vc, table, sqlQueryExecutor);
		// TODO Auto-generated constructor stub

	}

	@Override
	public void execute() {
		
		// update lookup table with axisvariables
		XColumn x = this.getVc().getX();
		YColumn y = this.getVc().getY();
		ZColumn z = this.getVc().getZ();

		// e.g., x1 <- 'year'
		if (!x.getVariable().equals("") && !x.getAttributes().isEmpty()) {
			AxisVariable axisVar = new AxisVariable("X", "", x.getAttributes());
			lookuptable.put(x.getVariable(), axisVar);
		}
		if (!y.getVariable().equals("") && !y.getAttributes().isEmpty()) {
			AxisVariable axisVar = new AxisVariable("Y", "", y.getAttributes());
			lookuptable.put(y.getVariable(), axisVar);
		}
		// For z, use type variable = z.getColumn!
		if (!z.getVariable().equals("") && !z.getValues().isEmpty()) {
			AxisVariable axisVar = new AxisVariable("Z", z.getAttribute(), z.getValues());
			lookuptable.put(z.getVariable(), axisVar);
		}
		
		// execute scatter
		
		// data fetcher
		long startTime = System.currentTimeMillis();
		this.vcList = getScatterData();
		long endTime = System.currentTimeMillis();
		logger.info("Fetching Scatter Data took " + (endTime - startTime) + "ms");
		Result output = new Result();

		startTime = System.currentTimeMillis();
		ScatterProcessNode.computeScatterRep(this.vcList, this.getVc(), output);
		endTime = System.currentTimeMillis();
		logger.info("Computing Scatter Rep took " + (endTime - startTime) + "ms");
		logger.info("scatter data: first chart size: " + output.getOutputCharts().get(0).count);
		/*
		try {
			Chart chart = output.getOutputCharts().get(0);
			String result = new ObjectMapper().writeValueAsString(chart);
			logger.info(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */
		// data transformer
		
		// After we have executed this scatter vc node, we have fetched the data and performed data transformation.
		// This allows us to easily move on to the task processor, while keeping the VCNode -> ProcessNode flow, with having to add a specific data transform node
		this.getLookUpTable().put(this.getVc().getName().getName(), this);
		this.state = State.FINISHED;
	}
	
	private VisualComponentList getScatterData() {
		
		// old method: grab info from ScatterPlotQuery
		//String yAxis = query.yAxis; // eg year, yValues = [2001, 2002, ...]
		//String xAxis = query.xAxis; // eg soldPrice, xValues = [10000, 12330, ...]
		//String zAxis = query.zAxis; // eg State, zValues = ['CA', 'MN', ...]
		
		// new method: grab info from the VisualComponentQuery
		// call SQL backend (for scatter plot, no aggregation method)
		ZQLRow row = buildRowFromNode();
		this.getVc().getViz().getMap().put(VizColumn.aggregation, "");
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
		if (this.getVc().getZ().isAggregate()) {
			return createAggregatedScatterResults(vcList);
		}
		
		return vcList;
	}
	
/**
 * Aggregates a list of one or moreVisualComponent into a list of one VisualComponent
 * @param vcList
 */
private VisualComponentList createAggregatedScatterResults(VisualComponentList vcList) {
	if (vcList.getVisualComponentList().size() <= 1) {
		return vcList;
	}
	
	ArrayList<WrapperType> aggregateXList = new ArrayList<WrapperType>();
	ArrayList<WrapperType> aggregateYList = new ArrayList<WrapperType>();
	for (VisualComponent vc : vcList.getVisualComponentList()) {
		//String zValue = vc.getZValue().getStrValue();
		Points points = vc.getPoints();
		List<WrapperType> xValues = points.getXList();
		List<WrapperType> yValues = points.getYList();
		aggregateXList.addAll(xValues);
		aggregateYList.addAll(yValues);
	}
	Points aggregatePoints = new Points(aggregateXList, aggregateYList);
	String xAttribute = vcList.getVisualComponentList().get(0).getxAttribute();
	String yAttribute = vcList.getVisualComponentList().get(0).getyAttribute();
	VisualComponent aggregateVisualComponent = new VisualComponent(new WrapperType("agg"), aggregatePoints, xAttribute, yAttribute);

	VisualComponentList out = new VisualComponentList();
	out.addVisualComponent(aggregateVisualComponent);
	
	return out;
}
	//TODO: implement
	private void simpleBinning(Map<String, ScatterResult> allDataCharts, int bins) {
		int rows = 100; // height
		int cols = 200; // width
		int cells = rows*cols;
		// grid interval 
		float S = (float) Math.sqrt(cells/bins);
		
		float x = S/2;
		float y = S/2;
		
		// create all grid centers
		while (y < rows && x < cols) {
			
			// add grid to array, maybe count points here
			
			// move right, next grid in this row
			x = x + S;
			if (x + S > cols) {
				// move down, start of next grid row
				y = y + S;
				x = S/2;
			}
			
			Point grid_center = new Point(x,y);
		}
		// 2D array for each bin.
		// look through all tuples, add them to each bin.
		
		// instead of having X points in a grid, just display the grid center, and the count (or size)
		// Our ScatterResult object currently supports points as just tuples of (x,y)
		// to support binning, we probably need (x,y, count)? 
		
		// how does front end use the binning? 
		
	}
}
