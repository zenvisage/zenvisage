package edu.uiuc.zenvisage.zql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.uiuc.zenvisage.data.remotedb.Points;
import edu.uiuc.zenvisage.data.remotedb.SQLQueryExecutor;
import edu.uiuc.zenvisage.data.remotedb.VisualComponent;
import edu.uiuc.zenvisage.data.remotedb.VisualComponentList;
import edu.uiuc.zenvisage.data.remotedb.WrapperType;
import edu.uiuc.zenvisage.model.Point;
import edu.uiuc.zenvisage.model.Sketch;
import edu.uiuc.zenvisage.zql.executor.Constraints;
import edu.uiuc.zenvisage.zqlcomplete.executor.Name;
import edu.uiuc.zenvisage.zqlcomplete.executor.VizColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.XColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.YColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLRow;

/**
 * @author Edward Xue
 * The visual component node to be executed
 */
public class VisualComponentNode extends QueryNode{

	private VisualComponentQuery vc;
	protected SQLQueryExecutor sqlQueryExecutor;
	protected String db;
	
	// private vc output
	//TODO: build separate result node
	// call QueryGraphResults
	// VisualComponentResultNode
		// should not have the visual componentquery
	// ProcessResultNode
		//should have top 5 visualcomponents from list (for now)

	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}

	public VisualComponentNode(VisualComponentQuery vc) {
		this.vc = vc;
	}

	public VisualComponentNode(VisualComponentQuery vc, LookUpTable table, SQLQueryExecutor sqlQueryExecutor) {
		super(table);
		this.vc = vc;
		this.sqlQueryExecutor = sqlQueryExecutor;
	}

	@Override
	public void execute() {
		if (isBlocked()) {
			this.state = State.BLOCKED;
			return;
		}
		this.state = State.RUNNING;
		LookUpTable lookuptable = this.getLookUpTable();
		
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
		
		Name name_obj = this.getVc().getName();
		// if we are dealing with a row that wants to get sketch data
		if (name_obj.getSketch()) {
			// Thus, the Sketch object should be populated too
			VisualComponentList vcList = new VisualComponentList();
			ArrayList<VisualComponent> list = new ArrayList<VisualComponent>();
			vcList.setVisualComponentList(list);
			Sketch sketch = this.getVc().getSketch();
			if(sketch == null || sketch.getPoints().isEmpty()) {
				this.state = State.FINISHED;
				return;
			}
			ArrayList<WrapperType> xList = new ArrayList<WrapperType>();
			ArrayList<WrapperType> yList = new ArrayList<WrapperType>();
			for (Point point : sketch.getPoints()) {
				xList.add(new WrapperType(point.getXval()));
				yList.add(new WrapperType(point.getYval()));
			}
			
			VisualComponent vc = new VisualComponent(new WrapperType("sketch", "string"), new Points(xList, yList));
			vc.setxAttribute(sketch.getxAxis());
			vc.setyAttribute(sketch.getyAxis());
			vc.setzAttribute(sketch.getGroupBy());
			vcList.addVisualComponent(vc);
			
			this.getLookUpTable().put(name_obj.getName(), vcList);
			System.out.println("vcList for sketch "+ name_obj.getName());
			this.state = State.FINISHED;
			return;
		}
		
		//update the look table with name variable, e.g, f1)
		String name = this.getVc().getName().getName();

		//Fills in missing info into vcList for output purposes
		AxisVariable axisVar = (AxisVariable) lookuptable.get(z.getVariable());
		VisualComponentList vcList = getVisualCollection();
		// If our Z values are from the output of a process, we need to sort the list using the scores
		if (axisVar != null && axisVar.getScores() != null && axisVar.getScores().length > 0) {
			double[] scores = axisVar.getScores();
			List<String> values = axisVar.getValues();
			sortVisualComponentList(vcList, scores, values, axisVar.getAttribute());
		} else {
			for (int i = 0; i < vcList.getVisualComponentList().size(); i++) {
				VisualComponent vc = vcList.getVisualComponentList().get(i);;
				vc.setzAttribute(z.getAttribute());
			}			
		}
		this.getLookUpTable().put(name, vcList);
		//System.out.println("vcList for node "+ name);
		//System.out.println(sqlQueryExecutor.getVisualComponentList());
		this.state = State.FINISHED;
	}
	
	/**
	 * Grabs the Visual Collection from data source. Currently implemented with Postgres backing it. 
	 * In progress to make more modular, so can connect with any backend data tool.
	 * @return
	 */
	private VisualComponentList getVisualCollection() {
		// call SQL backend
		ZQLRow row = buildRowFromNode();
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
		
		// Replace zvalues [*] with actual values like [CA,NY,...]
		ZColumn z = row.getZ();
		AxisVariable zAxisVariable = (AxisVariable) lookuptable.get(z.getVariable());
		if (z.getValues().get(0).equals("*")) {
			List<String> zValues = new ArrayList<>();
			for (VisualComponent vc : sqlQueryExecutor.getVisualComponentList().visualComponentList) {
				zValues.add(vc.getZValue().toString());
			}
			zAxisVariable.setValues(zValues);
		}
		return sqlQueryExecutor.getVisualComponentList();
	}
	
	/**
	 * SQLExecutor will return vcList, with the visualComponents in alphabetical order.
	 * We want to return the list of VisualComponents in the same order as the scores.
	 * First, I create a hashmap of (value, VC) eg (NY, VisualComponent)
	 * Then, I used the sorted scores and values arrays to grab the correct VC to create a new vcList of correct order
	 * This would be O(n), and would not require resorting
	 * @param vcList
	 * @param scores sorted list of scores eg [0.0, 0.1, 0.2] 
	 * @param values sorted list of values eg [CA, MA, NY]	 */
	public void sortVisualComponentList(VisualComponentList vcList, double[] scores, List<String> values, String zAttribute) {
		/*
		 * We might have to display multiple axis, using the same set of scores and values
		 * Eg scores = [0.0, 0.1, 0.2]
		 * values = [CA, MA, NY]
		 * For A = {year, soldprice} and B = {year, soldpricepersqft}
		 * -> This means we would have 6 visual components in vcList, 3 for A and then 3 for B.
		 */
		HashMap<String, VisualComponent> mapping = new HashMap<String, VisualComponent>();
		ArrayList<VisualComponent> inputList = vcList.getVisualComponentList();
		ArrayList<VisualComponent> outputList = new ArrayList<VisualComponent>();
		
		// lists is an arraylist of arraylists
		// outer arraylist corresponds to list over the different X,Y pairs 
		// inner array list is list over the different VisualComponents for that X,Y pair (aka specific X,Y,Z data)
		// eg [ [VC-year,soldprice,CA; VC-year,soldprice,NY ] , [VC-year,listingprice,CA; VC-year,listingprice,NY] ]
		ArrayList<ArrayList<VisualComponent>> lists = new ArrayList<ArrayList<VisualComponent>>();
		
		int index = 0;
		while(index < inputList.size()) {
			// say outputList.size() = 10, scores.length = 5
			// first iteration would be [0,5)
			// second iteration would be [5, 10)
			lists.add(new ArrayList<VisualComponent>());
			for(int i = index; i < scores.length + index; i++) {
				VisualComponent vc = inputList.get(i);
				mapping.put(vc.getZValue().getStrValue(), vc);		// NOTE: if we have multiple of same zvalue (within the same {x,y} pair), this keeps latest one
			}
			for(int i = 0; i < scores.length; i++) {
				VisualComponent vc = mapping.get(values.get(i));
				vc.setScore(scores[i]);
				vc.setzAttribute(zAttribute);
				//outputList.add(vc);
				lists.get(index/scores.length).add(vc);
			}
			index += scores.length;
			//mapping.clear();										// I don't believe this is necessary, should be overwritten
		}
		for(int i = 0; i < scores.length; i++) {
			for(int xy_pair = 0; xy_pair < lists.size(); xy_pair++) {	
				outputList.add(lists.get(xy_pair).get(i));
			}
		}
		vcList.setVisualComponentList(outputList);
	}

	public VisualComponentQuery getVc() {
		return vc;
	}

	public void setVc(VisualComponentQuery vc) {
		this.vc = vc;
	}

	@Override
	/**
	 * Four cases to deal with:
	 * Column has variable and values. Can use as is.
	 * Column has variable, but no values. Need to fill in values from lookup
	 * Column hs no variable name, but values. Can use as is
	 * Column has no variable name, and no value. Send as is (columns may be optional)
	 */
	protected ZQLRow buildRowFromNode() {

		XColumn x = vc.getX();
		// x1 (variable, no values)
		if(!x.getVariable().equals("") && x.getAttributes().isEmpty()) {
			// The lookup table for x should have value = AxisVariable
			List<String> attributes = ((AxisVariable) lookuptable.get(x.getVariable())).getValues();
			x.setAttributes(attributes);
		}
		// Stripping out '' from first value
		String var = x.getAttributes().get(0);
		var = var.replace("'", "");
		x.getAttributes().set(0, var);

		// Some debuf info
		System.out.println("x information:");
		System.out.println(x.getVariable());
		System.out.println(x.getAttributes());
		System.out.println(x.getAttributes().get(0));

		YColumn y = vc.getY();
		// y1 (variable, no values)
		if(!y.getVariable().equals("") && y.getAttributes().isEmpty()) {
			List<String> attributes = ((AxisVariable) lookuptable.get(y.getVariable())).getValues();
			y.setAttributes(attributes);
		}
		// Stripping out '' from first value
		var = y.getAttributes().get(0);
		var = var.replace("'", "");
		y.getAttributes().set(0, var);

		ZColumn z = vc.getZ();
		System.out.println("Checking: " + z.getVariable());
		// z1 (variable, no values)
		AxisVariable zAxisVariable = (AxisVariable) lookuptable.get(z.getVariable());

		// if z is missing column information, grab from axisVariable type! (Special case!)
		if(!z.getVariable().equals("") && z.getAttribute().isEmpty()) {
			z.setAttribute(zAxisVariable.getAttribute());
			List<String> values = zAxisVariable.getValues();
			z.setValues(values);
		}

		// update the z column to make sure it strips extra '' out (so will be state, not 'state')
		String str = z.getAttribute();
		str = str.replace("'", "");
		z.setAttribute(str);
		
		// So either z naturally has values eg from query with z=state.{'CA','NY'}
		// Or z got values from the lookuptable eg z=v1
		List<String> values = z.getValues();
		if(!values.isEmpty() && !values.get(0).equals("") && !values.get(0).equals("*")){
			String parentheSizedValues = generateParenthesizedList(values);
		
			if(vc.getConstraints()!=null && vc.getConstraints()!="" )
			{
				vc.setConstraints(vc.getConstraints()+" AND ("+ z.getAttribute() +" IN "+parentheSizedValues + ")");
			}
			else
				vc.setConstraints(z.getAttribute() +" IN "+parentheSizedValues);
		}

		System.out.println("z information:");
		System.out.println(z.getVariable());
		//System.out.println(z.getValues());
		System.out.println(z.getAttribute());
		
		if(!vc.getViz().getMap().containsKey(VizColumn.aggregation)) {
			vc.getViz().getMap().put(VizColumn.aggregation, "AVG");
		}
		ZQLRow result = new ZQLRow(x, y, z, vc.getConstraints(), vc.getViz());
		// null processe and sketchPoints (for now)
		return result;
	}

	/**
	 * @param values
	 * @return
	 */
	//TODO: FIX it doesn't work for non-strings
	private String generateParenthesizedList(List<String> values) {
		String parentheSizedValues="(";
		for(String value: values){
			value = value.replaceAll("'", "").replaceAll("\"", "");
			parentheSizedValues+= " \'"+value+"\',";
		}
		parentheSizedValues=parentheSizedValues.substring(0,parentheSizedValues.length()-1);
		parentheSizedValues+=")";
		return parentheSizedValues;

	}

	public void updateAxisVaribles(){
		//TODO
	}

}
