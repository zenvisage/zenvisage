package edu.uiuc.zenvisage.zqlcomplete.executor;

import java.util.ArrayList;

/**
 * SQL query representation object
 */
public class SQLQuery {

	private ArrayList<String> selectClause;
	private String fromClause;
	private ArrayList<String> whereClause;
	private ArrayList<String> groupClause;
	private ArrayList<String> orderClause;

	public SQLQuery() {
		selectClause = new ArrayList<String>();
		fromClause = "";
		whereClause = new ArrayList<String>();
		groupClause = new ArrayList<String>();
		orderClause = new ArrayList<String>();
		
	}
	
	// supports only one element in each column, fix later
	public void constructFromZQL(ZQLRow zQLRow, ZQLTableResult zQLTableResult) {
		for (String x : zQLRow.getX().getAttributes()) {
			select(x.toLowerCase().replace("'",""));
			groupBy(x.replace("'", ""));
			orderBy(x.replace("'", ""));
		}
		
		for (String y : zQLRow.getY().getAttributes()) {
			select("AVG("+y.toLowerCase().replace("'", "")+") AS "+zQLRow.getY().getAttributes().get(0).toLowerCase().replace("'",""));
		}
		
		boolean isVariable = false;
		// check if input is a column or varible, infer column from variable
		if (zQLRow.getZ().getAttribute().equals("")) {
			zQLRow.getZ().setAttribute(zQLTableResult.getVariable(zQLRow.getZ().getVariable()).getName().replace("'",""));
			isVariable = true;
		}
		select(zQLRow.getZ().getAttribute().replace("'",""));
		
		from("realestate");
		
		
		if (zQLRow.getConstraint().size() > 0) {
			for (int i = 0; i < zQLRow.getConstraint().size(); i++)
				where(zQLRow.getConstraint().get(i).toString());
		}
		
		groupBy(zQLRow.getZ().getAttribute().replace("'", ""));
		orderBy(zQLRow.getZ().getAttribute().replace("'", ""));
		
		
		// Redundant convert to empty again if it is variable only since the executor only uses variable result if it is empty
		if (isVariable) {
			zQLRow.getZ().setAttribute("");
		}
	}
	
	// add element to select clause
	public void select(String value) {
		if (!value.equals("")) {
			this.selectClause.add(value);
		}
	}
	
	// add element to from clause
	public void from(String value) {
		if (!value.equals("")) {
			this.fromClause = value;
		}
	}
	
	// add element to where clause
	public void where(String value) {
		if (!value.equals("")) {
			this.whereClause.add(value);
		}
	}
	
	// add element to group by clause
	public void groupBy(String value) {
		if (!value.equals("")) {
			this.groupClause.add(value);
		}
	}
	
	// add element to order by clause
	public void orderBy(String value) {
		if (!value.equals("")) {
			this.orderClause.add(value);
		}
	}
	
	// Output the sql query as a string
	public String toString() {
		
		String query = "";
		
		//SELECT 
		query = query + "SELECT ";
		query = query + selectClause.get(0);
		for (int i = 1; i < selectClause.size(); i++) {
			query = query +", " + selectClause.get(i);
		}
		
		//FROM
		query = query + " FROM " + fromClause;
		
		//WHERE
		if (!whereClause.isEmpty()) {
			query = query + " WHERE ";
			query = query + whereClause.get(0);
			for (int i = 1; i < whereClause.size(); i++) {
				query = query +" OR " + whereClause.get(i);
			}
		}
		
		//GROUP BY
		if (!groupClause.isEmpty()) {
			query = query + " GROUP BY ";
			query = query + groupClause.get(0);
			for (int i = 1; i < groupClause.size(); i++) {
				query = query +", " + groupClause.get(i);
			}
		}
		
		//ORDER BY
		if (!orderClause.isEmpty()) {
			query = query + " ORDER BY ";
			query = query + orderClause.get(0);
			for (int i = 1; i < orderClause.size(); i++) {
				query = query +", " + orderClause.get(i);
			}
		}
		
		return query+";";
	}
	
}
