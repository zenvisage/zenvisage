/**
 * 
 */
package edu.uiuc.zenvisage.zqlcomplete.querygraph.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

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
		ArrayList<String> values = new ArrayList<String>();
		values.add("year");
		x1.setValues(values);
		
		YColumn y1 = new YColumn("y1");
		values.clear();
		values.add("soldprice");
		y1.setValues(values);
		
		// Z column has slightly different syntax
		ZColumn z1 = new ZColumn("z");
		z1.setVariable("z1");
		values.add("CA");
		values.clear();
		//values.add
		
	}
}
