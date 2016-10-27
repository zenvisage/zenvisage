package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import java.sql.SQLException;
import java.util.List;

import edu.uiuc.zenvisage.data.remotedb.SQLQueryExecutor;
import edu.uiuc.zenvisage.zql.executor.Constraints;
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
	private SQLQueryExecutor sqlQueryExecutor;
	// private vc output
	//TODO: build separate result node
	// call QueryGraphResults
	// VisualComponentResultNode
		// should not have the visual componentquery
	// ProcessResultNode
		//should have top 5 visualcomponents from list (for now)
	
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
			AxisVariable axisVar = new AxisVariable("", x.getAttributes());
			lookuptable.put(x.getVariable(), axisVar);
		}
		if (!y.getVariable().equals("") && !y.getAttributes().isEmpty()) {
			AxisVariable axisVar = new AxisVariable("", y.getAttributes());
			lookuptable.put(y.getVariable(), axisVar);
		}
		// For z, use type variable = z.getColumn!
		if (!z.getVariable().equals("") && !z.getValues().isEmpty()) {
			AxisVariable axisVar = new AxisVariable(z.getAttribute(), z.getValues());
			lookuptable.put(z.getVariable(), axisVar);
		}
		// call SQL backend
		ZQLRow row = buildRowFromNode();
		try {
			sqlQueryExecutor.ZQLQueryEnhanced(row, "realestate");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		this.state = State.FINISHED;
		//update the look table with name variable, e.g, f1)
		String name = this.getVc().getName().getName();
		
		// CHECK THIS OUTPUT
		this.getLookUpTable().put(name, sqlQueryExecutor.getVisualComponentList());
		System.out.println("vcList for node "+ name);
		System.out.println(sqlQueryExecutor.getVisualComponentList());
		System.out.println("hi");
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
	public ZQLRow buildRowFromNode() {
		
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
			if(!values.isEmpty() && !values.get(0).equals("") && !values.get(0).equals("*")){
				String parentheSizedValues = generateParenthesizedList(values);
				edu.uiuc.zenvisage.zqlcomplete.executor.Constraints constraints = new edu.uiuc.zenvisage.zqlcomplete.executor.Constraints();
				constraints.setKey(z.getAttribute());
				constraints.setOperator(" IN");
				constraints.setValue(parentheSizedValues);		
				vc.getConstraints().add(constraints);
			}
			z.setValues(values);
		}
	
		
		// update the z column to make sure it strips extra '' out (so will be state, not 'state')
		String str = z.getAttribute();
		str = str.replace("'", "");
		z.setAttribute(str);

		System.out.println("z information:");
		System.out.println(z.getVariable());
		System.out.println(z.getValues());
		System.out.println(z.getAttribute());
		vc.getViz().setVariable("AVG");
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
		// TODO Auto-generated method stub
		String parentheSizedValues="(";
		for(String value: values){
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
