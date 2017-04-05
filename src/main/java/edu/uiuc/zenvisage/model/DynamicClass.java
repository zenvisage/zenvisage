package edu.uiuc.zenvisage.model;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicClass {
	
	public String dataset;
	public ClassElement[] classes;

	public DynamicClass() {
	}
	
	public ClassElement[] getClassElement(){
		return classes;
	}
	
	public void setClassElement(ClassElement[] classes){
		this.classes = classes;
	}
	
	public Map<String, ClassElement> getDCHashMap(){
		Map<String, ClassElement> map = new HashMap<>();
		for(ClassElement ce: classes){
			map.put(ce.name, ce);
		}
		return map;
	}
	
	/**
	 * Enumerate combinations of criteria of each classes, set all rows satisfy that combination a dynamic_class string
	 * bp [0-10] [20-30] [40-50]
	 * fp [0-10] [20-30]
	 * ad [0-20] [30-40]
	 * if we have bp [0-10] fp [20-30] ad not satisfied,
	 * we mark dynamic_class string as  0.1.-1, 
	 * which means choose first criteria of bp, 
	 * second criteria of fp and none of ad. 
	 * Moreover . is the separator.
	 * @throws SQLException 
	 * http://stackoverflow.com/questions/6446250/sql-statement-with-multiple-sets-and-wheres
	 * http://stackoverflow.com/questions/27800119/postgresql-case-end-with-multiple-conditions
	 * http://dba.stackexchange.com/questions/39815/use-case-to-select-columns-in-update-query
	 */

	public String getSQL(){
		StringBuilder ret = new StringBuilder("Update " + this.dataset + "\nSET dynamic_class = CASE \n");
		List<String> updateList = new ArrayList<String>();
		List<String> sqlList = new ArrayList<String>();
		GeneratePermutations(classes, updateList, sqlList, 0, "", "");
		int i = 0;
		for(; i < updateList.size()-1; i++){
			ret.append(" When "+ sqlList.get(i) + " THEN '" + updateList.get(i) +  "'\n");
		}
		ret.append("ELSE '" + updateList.get(i) + "'\n");
		ret.append("END;");
		return ret.toString();
	}

	public void GeneratePermutations(ClassElement[] classes, List<String> updateList, List<String> sqlList, int depth, String current, String currentSQL)
	{
	    if(depth == classes.length)
	    {
	       updateList.add(current.substring(0, current.length()-1));
	       sqlList.add(currentSQL);
	       return;
	     }

	    for(int i = classes[depth].values.length-1; i >= -1 ; i--)
	    {
	    	if( i == -1)
	    		GeneratePermutations(classes, updateList, sqlList, depth + 1, current + i + ".", currentSQL);
	    	else {
	    		String addon = classes[depth].name + " >= " + classes[depth].values[i][0]
	    				+ " AND " + classes[depth].name + " < " + classes[depth].values[i][1];
	    		if(currentSQL.equals("")){
	    			GeneratePermutations(classes, updateList, sqlList, depth + 1, current + i + ".", currentSQL + addon);
	    		}else{
	    			GeneratePermutations(classes, updateList, sqlList, depth + 1, current + i + ".", currentSQL + " AND " + addon);
	    		}
	    		
	    	}
	    }
	}
}
