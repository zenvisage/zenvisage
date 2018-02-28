package edu.uiuc.zenvisage.zql;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import edu.uiuc.zenvisage.data.remotedb.SQLQueryExecutor;
import edu.uiuc.zenvisage.data.roaringdb.db.Column;
import edu.uiuc.zenvisage.data.roaringdb.db.ColumnMetadata;
import edu.uiuc.zenvisage.data.roaringdb.db.DatabaseMetaData;
import edu.uiuc.zenvisage.zqlcomplete.executor.Name;
import edu.uiuc.zenvisage.zqlcomplete.executor.Processe;
import edu.uiuc.zenvisage.zqlcomplete.executor.VizColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.XColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.YColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLRow;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLTable;

/**
 * @author Edward Xue
 * Takes in a ZQLTable, and parses it into a query graph
 */
public class ZQLTableToGraph {
	
	// A query graph needs O(1) access to any node
	// Here the map is of form key = input var, value = result Node that output this
	Map<String, Node> nodeMap = new HashMap<String, Node>();
	
	
	/**
	 * Processes a ZQLTable row by row and generates a plan graph
	 * @return QueryGraph for this table
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public QueryGraph processZQLTable(ZQLTable table, LookUpTable lookUpTable) throws SQLException, IOException {
		
		List<Node> queryEntryNodes = new ArrayList<Node>();
		QueryGraph graph = new QueryGraph();
		if (lookUpTable == null) {
			lookUpTable = new LookUpTable();
		}
		String db = table.getDb();
		
		// get the possible axis attributes (like year, month for x)
		String locations[] = new SQLQueryExecutor().getMetaFileLocation(db);
		DatabaseMetaData dbMetaData = readSchema(locations[0]);
		List<String> xAttributes = new ArrayList<String>();
		for (String xAttribute : dbMetaData.xAxisColumns.keySet()) {
			xAttributes.add(xAttribute);
		}
		List<String> yAttributes = new ArrayList<String>();
		for (String yAttribute : dbMetaData.yAxisColumns.keySet()) {
			yAttributes.add(yAttribute);
		}
		System.out.println("metadata");
		System.out.println(xAttributes);
		System.out.println(yAttributes);
		
		Queue<Node> nodeQueue = new ArrayDeque<Node>();

		// first pass, generate nodes
		// add to hashmap the variables
		for (ZQLRow row : table.getZqlRows()) {
			XColumn x = row.getX();
			YColumn y = row.getY();
			ZColumn z = row.getZ(); // eg z1 or v1
			Name name = row.getName();
			
			// Create the nodes
			VisualComponentQuery vc = new VisualComponentQuery(row.getName(), x, y, z, row.getConstraint(), row.getViz(), row.getSketchPoints());
			SQLQueryExecutor sqlQueryExecutor= new SQLQueryExecutor();
			VisualComponentNode vcNode = new VisualComponentNode(vc, lookUpTable, sqlQueryExecutor);
			Processe process = row.getProcesse();
			ProcessNode processNode = new ProcessNode(process, lookUpTable);
			
			if(vc.getViz().getMap().containsKey(VizColumn.type) && vc.getViz().getMap().get(VizColumn.type).equals(VizColumn.scatter)) {
				vcNode = new ScatterVCNode(vc, lookUpTable, sqlQueryExecutor);
				processNode = new ScatterProcessNode(process, lookUpTable);
			}
			vcNode.setDb(db);

			
			// Add to queue
			nodeQueue.add(vcNode);
			if(process != null && process.getVariables().size() > 0) {
				nodeQueue.add(processNode);
			}
			
			// Add to nodeMap
			// assume each var will be written to only in one row, and laters rows only read that var
			// this assumption is needed because we are creating nodes first, then edges and we don't need to depend on row order
			nodeMap.put(name.getName(), vcNode); 
			if (x.getVariable() != null && !x.getVariable().equals("") && x.getAttributes() != null && !x.getAttributes().isEmpty()) {
				if(x.getAttributes().get(0).equals("*")) {
					// replaces x1<-* with x1<-'year','month',...
					x.setAttributes(xAttributes);
				}
				nodeMap.put(x.getVariable(), vcNode);
			}
			if (y.getVariable() != null && !y.getVariable().equals("") && y.getAttributes() != null && !y.getAttributes().isEmpty()) {
				if(y.getAttributes().get(0).equals("*")) {
					y.setAttributes(yAttributes);
				}
				nodeMap.put(y.getVariable(), vcNode);
			}
			if (z.getVariable() != null && !z.getVariable().equals("") && z.getValues() != null && !z.getValues().isEmpty()) {
				nodeMap.put(z.getVariable(), vcNode);
			}
			if (process != null) {
				for (String variable : process.getVariables()) {
					nodeMap.put(variable, processNode); // if they reuse a process variable, assume future rows refer to the latest reused variable name node
				}
			}
		}
		// second pass, add dependencies
		while(!nodeQueue.isEmpty()) {
			QueryNode currNode = (QueryNode) nodeQueue.remove();
			if (currNode instanceof VisualComponentNode) {
				VisualComponentNode vcNode = (VisualComponentNode) currNode;
				XColumn x = vcNode.getVc().getX();
				YColumn y = vcNode.getVc().getY();
				ZColumn z = vcNode.getVc().getZ();
				
				// A vcnode is an entry node if it has defined its X,Y,Z (so has no parents) eg X1<-'month', Y1<-'soldprice', Z1<-'state'.* 
				// New entry node! Add as an entry node for this query, and entry node for the entire graph
				// Since we are assuming variables are write once, read forever
				// A vcnode is also an entry node if it has no variables, but defines all its X,Y,Z (so still no parents) Eg X: 'month', Y: 'soldprice', Z: 'state'.* 
				// so we don't need this check: x.getVariable() != null && !x.getVariable().equals("")
				
				if (x.getAttributes() != null && !x.getAttributes().isEmpty()) {
					if (y.getAttributes() != null && !y.getAttributes().isEmpty()) {
						// if ZAttribute and ZValues are both null emptyZ case
						//boolean emptyZ = (z.getValues() == null || z.getValues().isEmpty()) && z.getAttribute().equals("");
						if ( (z.getValues() != null && !z.getValues().isEmpty())  ) {
							queryEntryNodes.add(vcNode);
							graph.entryNodes.add(vcNode);
							continue;
						}	
					}					
				}
				
				// A vcNode is also an entry node if it is a sketch
				if (vcNode.getVc().getName().getSketch()) {
					queryEntryNodes.add(vcNode);
					graph.entryNodes.add(vcNode);
					continue;					
				}
				
				// So if any x,y,or z variable is referenced from table, link that as a parent
				// we can have multiple parents (say Z from a processNode, X,Y from a vcNode)
				Node prevParent = null;
				if (nodeMap.containsKey(x.getVariable())) {
					Node parent = nodeMap.get(x.getVariable());
					if (parent != vcNode) {
						parent.addChild(vcNode);
						vcNode.addParent(parent);
						prevParent = parent;
					}
				} 
				if (nodeMap.containsKey(y.getVariable())) {
					Node parent = nodeMap.get(y.getVariable());
					if (prevParent != parent && parent != vcNode) { // don't add a parent twice
						parent.addChild(vcNode);
						vcNode.addParent(parent);
						prevParent = parent;
					}
				} 
				if (nodeMap.containsKey(z.getVariable())) { 
					Node parent = nodeMap.get(z.getVariable());
					// / make sure this parent is not ourself! We may not be an entry node, but we may define Z
					if (prevParent != parent && parent != vcNode) {
						parent.addChild(vcNode);
						vcNode.addParent(parent);
					}
				}
				
			}
			
			if (currNode instanceof ProcessNode) {
				ProcessNode processNode = (ProcessNode) currNode;
				Processe process = processNode.getProcess();
				boolean hasParent = false;
				if (process != null) {
					for (String argument : process.getArguments()) {
						Node parent = nodeMap.get(argument);
						if (parent != null) {
							hasParent = true;
							parent.addChild(processNode);
							processNode.addParent(parent);					
						}
					}
				}
				if (!hasParent && process != null) {
					queryEntryNodes.add(processNode);
					graph.entryNodes.add(processNode);
					// process nodes depend on some parameters, so is this case reachable?
				}
			}
		}
		return graph;
		
	}
	
	private DatabaseMetaData readSchema(String schemafilename) throws IOException {
		DatabaseMetaData databaseMetaData = new DatabaseMetaData();
	   	BufferedReader bufferedReader = new BufferedReader(new FileReader(schemafilename));
		 String line;
		 while ((line = bufferedReader.readLine()) != null){
				 ColumnMetadata columnMetadata= new ColumnMetadata();
				 String[] sections=line.split(":");
				 columnMetadata.name=sections[0].toLowerCase().replaceAll("-", "");
				 String[] terms=sections[1].split(",");
				 columnMetadata.isIndexed=true;
				 columnMetadata.dataType=terms[0];
				 columnMetadata.columnType=terms[8];
				 if("indexed".equals(terms[1])){
					 columnMetadata.isIndexed=true;
				 }
				 else{
					 columnMetadata.isIndexed=false;
				 }

			     if(terms[2].equals("T")){
			    	 databaseMetaData.xAxisColumns.put(columnMetadata.name,columnMetadata);
			     }
			     if(terms[3].equals("T")){
			    	 databaseMetaData.yAxisColumns.put(columnMetadata.name,columnMetadata);
			     }

			     if(terms[4].equals("T")){
			    	 databaseMetaData.zAxisColumns.put(columnMetadata.name,columnMetadata);
			     }

			     if(terms[5].equals("T")){
			    	 databaseMetaData.predicateColumns.put(columnMetadata.name,columnMetadata);
			     }
			     if (terms[6].equals("T")) {
			    	 columnMetadata.unit = terms[7];
			     }
			 }
			bufferedReader.close();
		return databaseMetaData;
	}
}
