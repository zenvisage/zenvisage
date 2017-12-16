/**
 * 
 */
package edu.uiuc.zenvisage.zqlcomplete.querygraph.test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.uiuc.zenvisage.zql.QueryGraph;
import edu.uiuc.zenvisage.zql.ZQLTableToGraph;
import edu.uiuc.zenvisage.zqlcomplete.executor.Constraints;
import edu.uiuc.zenvisage.zqlcomplete.executor.Name;
import edu.uiuc.zenvisage.zqlcomplete.executor.Processe;
import edu.uiuc.zenvisage.zqlcomplete.executor.XColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.YColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLRow;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLTable;

/**
 * @author eideh
 *
 */
public class ZQLTableToGraphTest {

	@Test
	/**
	 * f1	x1<-{'year'}	y1<-{'soldprice'}	z1<-'state'.*					Check variable assigment in X, Y, Z columns  functionality.
	 * *f2	x1	y1	z1		state='CA'			Check variable recall in X, Y, Z columns and constraints column functionality.
	 */
	public void TestBasicQuery() {
		ZQLTable table = new ZQLTable();
		List<ZQLRow> rows = new ArrayList<ZQLRow>();
		
		XColumn x1 = new XColumn("x1");
		ArrayList<String> x1Values = new ArrayList<String>();
		x1Values.add("year");
		x1.setAttributes(x1Values);
		
		YColumn y1 = new YColumn("y1");
		ArrayList<String> y1Values = new ArrayList<String>();
		y1Values.add("soldprice");
		y1.setAttributes(y1Values);
		
		// Z column has slightly different syntax
		ZColumn z1 = new ZColumn("z");
		z1.setVariable("z1");
		ArrayList<String> z1Values = new ArrayList<String>();
		z1Values.add("CA");
		z1Values.add("MN");
		z1Values.add("FL");
		z1Values.add("GA");
		z1Values.add("OH");
		z1Values.add("TN");
		z1Values.add("AZ");
		z1.setValues(z1Values);
		
		ZQLRow row1 = new ZQLRow(x1, y1, z1, null, null);
		Name name1 = new Name();
		name1.setName("f1");
		row1.setName(name1);
		rows.add(row1);
		
		XColumn x2 = new XColumn("x1");
		YColumn y2 = new YColumn("y1");
		ZColumn z2 = new ZColumn("z");
		z2.setVariable("z1");
		
		Constraints constraint = new Constraints();
		constraint.setKey("state");
		constraint.setOperator("=");
		constraint.setValue("CA");
		List<Constraints> constraints = new ArrayList<Constraints>();
		constraints.add(constraint);
		
		ZQLRow row2 = new ZQLRow(x2, y2, z2, "", null);
		Name name2 = new Name();
		name2.setName("f2");
		row2.setName(name2);
		rows.add(row2);
		
		table.setZqlRows(rows);
		
		ZQLTableToGraph parser = new ZQLTableToGraph();
		QueryGraph graph;
		try {
			graph = parser.processZQLTable(table, null);
			//System.out.println(graph.toString());
			 graph.printString();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	@Test
	/**
	 * f1	x1<-{'year'}	y1<-{'soldprice'}	z1<-'state'.*		state='CA'							Check variable assigment in X, Y, Z columns  functionality.
	 * f2	x1	y1	z1			v1<-Similar[k=7]D(f1,f2)		Check Similar process column functionality.
	 * f3	x1	y1	v1					
	 */	
	public void testQueryWithProcess() {
		ZQLTable table = new ZQLTable();
		List<ZQLRow> rows = new ArrayList<ZQLRow>();
		
		XColumn x1 = new XColumn("x1");
		ArrayList<String> x1Values = new ArrayList<String>();
		x1Values.add("year");
		x1.setAttributes(x1Values);
		
		YColumn y1 = new YColumn("y1");
		ArrayList<String> y1Values = new ArrayList<String>();
		y1Values.add("soldprice");
		y1.setAttributes(y1Values);
		
		// Z column has slightly different syntax
		ZColumn z1 = new ZColumn("z");
		z1.setVariable("z1");
		ArrayList<String> z1Values = new ArrayList<String>();
		z1Values.add("CA");
		z1Values.add("MN");
		z1Values.add("FL");
		z1Values.add("GA");
		z1Values.add("OH");
		z1Values.add("TN");
		z1Values.add("AZ");
		z1.setValues(z1Values);
		
		Constraints constraint = new Constraints();
		constraint.setKey("state");
		constraint.setOperator("=");
		constraint.setValue("CA");
		List<Constraints> constraints = new ArrayList<Constraints>();
		constraints.add(constraint);
		
		ZQLRow row1 = new ZQLRow(x1, y1, z1, "", null);
		Name name1 = new Name();
		name1.setName("f1");
		row1.setName(name1);
		rows.add(row1);
		
		XColumn x2 = new XColumn("x1");
		YColumn y2 = new YColumn("y1");
		ZColumn z2 = new ZColumn("z");
		z2.setVariable("z1");
		
		// v1<-Similar[k=7]D(f1,f2)
		Processe process = new Processe();
		process.getVariables().add("v1");
		process.setMethod("similar");
		// axis?
		// count?
		// metric?
		// arguments?
		//process.getParameters().put("f1","1");
		//process.getParameters().put("f2","2");
		// process.setAxis("z1");
		process.setCount("7");
		process.setMetric("argmin");
		process.getArguments().add("f1");
		process.getArguments().add("f2");
		
		ZQLRow row2 = new ZQLRow(x2, y2, z2, null, null);
		row2.setProcesse(process);
		Name name2 = new Name();
		name2.setName("f2");
		row2.setName(name2);
		rows.add(row2);
		
		// f3	x1	y1	v1
		XColumn x3 = new XColumn("x1");
		YColumn y3 = new YColumn("y1");
		ZColumn z3 = new ZColumn("z");
		z2.setVariable("v1");		
		ZQLRow row3 = new ZQLRow(x3, y3, z3, null, null);
		Name name3 = new Name();
		name3.setName("f3");
		row3.setName(name3);
		rows.add(row3);
		
		table.setZqlRows(rows);
		ZQLTableToGraph parser = new ZQLTableToGraph();
		QueryGraph graph;
		try {
			graph = parser.processZQLTable(table, null);
			//System.out.println(graph.toString());
			 graph.printString();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
