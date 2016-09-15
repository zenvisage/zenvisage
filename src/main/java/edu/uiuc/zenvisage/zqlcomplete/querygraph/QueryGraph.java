/**
 * 
 */
package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import java.util.List;

import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLRow;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLTable;

/**
 * @author eideh
 * Abstraction of the query graph representation of a query
 */
public class QueryGraph extends Graph {

	
	public QueryGraph () {
		
	}
	

	/**
	 * Processes a ZQLTable row by row and generates a graph
	 * @return List of entry points (for this table)
	 */
	public List<Node> processZQLTable(ZQLTable table) {
		for (ZQLRow row : table.getZqlRows()) {
			
		}
		return null;
		
	}
}
