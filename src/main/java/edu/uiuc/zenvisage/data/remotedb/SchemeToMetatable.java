package edu.uiuc.zenvisage.data.remotedb;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import edu.uiuc.zenvisage.model.AxisVariables;
import edu.uiuc.zenvisage.model.Variable;
import edu.uiuc.zenvisage.model.Variables;

public class SchemeToMetatable {
	
	public String createTableSQL;
	public List<String> columns;
	
	public SchemeToMetatable(){
		createTableSQL = null;
		columns = new ArrayList<String>();
	};
	
	public static void main(String[] args) throws IOException{
		String tablename = "real_estate";
		String filePath = "/Users/chaoran/Desktop/zenvisage/zenvisage/src/main/resources/data/real_estate.txt";
		System.out.println(new SchemeToMetatable().schemeFileToMetaSQL(filePath, tablename));
	}
	
	public String schemeFileToMetaSQL(String filePath, String tablename) throws IOException{
		StringBuffer sql = new StringBuffer("INSERT INTO zenvisage_metatable (tablename, attribute, type) VALUES ");
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String sCurrentLine;
		while ((sCurrentLine = br.readLine()) != null) {
			String split1[] = sCurrentLine.split(":");
			String split2[] = split1[1].split(",");
			sql.append("('" + tablename + "', '" + split1[0] + "', '" + split2[0] + "'), ");
		}
		br.close();
		sql.replace(sql.length()-2, sql.length(), ";");
		return sql.toString();
	}
	
	
	public String schemeFileToCreatTableSQL(String filePath, String tablename) throws IOException{
//		System.out.println(filePath);
//		System.out.println(tablename);
//		InputStream is = getClass().getResourceAsStream(filePath);
		tablename = tablename.toLowerCase();
		StringBuilder createTableSQLBuilder = new StringBuilder("Create table " + tablename + "(");
		createTableSQLBuilder.append("id SERIAL PRIMARY KEY, ");
		
	   	BufferedReader br = new BufferedReader(new FileReader(filePath));
		String sCurrentLine;
		while ((sCurrentLine = br.readLine()) != null) {
//			System.out.println(sCurrentLine);
			String split1[] = sCurrentLine.split(":");
			String split2[] = split1[1].split(",");
			createTableSQLBuilder.append(split1[0].toLowerCase().replaceAll("-", "")+ " " + typeToPostgresType(split2[0]) + ", ");
			this.columns.add(split1[0].toLowerCase().toLowerCase().replaceAll("-", ""));
		}
		
		//Adding dynamic_class column
		createTableSQLBuilder.append("dynamic_class"+ " " + "TEXT" + ", ");
		
		br.close();
		createTableSQLBuilder.replace(createTableSQLBuilder.length()-2,createTableSQLBuilder.length(), ");");
//		System.out.println(createTableSQL);
		return createTableSQLBuilder.toString();
	}
	
	public String schemeFileToMetaSQLStream(String filePath, String tablename) throws IOException{
//		System.out.println(filePath);
//		System.out.println(tablename);
//		InputStream is = getClass().getResourceAsStream(filePath);
		tablename = tablename.toLowerCase();
		StringBuilder createTableSQLBuilder = new StringBuilder("Create table " + tablename + "(");
		createTableSQLBuilder.append("id SERIAL PRIMARY KEY, ");
		
	   	BufferedReader br = new BufferedReader(new FileReader(filePath));
		StringBuffer sql = new StringBuffer("INSERT INTO zenvisage_metatable (tablename, attribute, type, selectedX, selectedY, selectedZ) VALUES ");
		String sCurrentLine;
		while ((sCurrentLine = br.readLine()) != null) {
//			System.out.println(sCurrentLine);
			String split1[] = sCurrentLine.split(":");
			String split2[] = split1[1].split(",");
			sql.append("('" + tablename + "', '" + split1[0].toLowerCase().replaceAll("-", "") 
					+ "', '" + split2[0] + "', "
			+ (split2[2].equals("T")?"True":"False") + ", " 
			+ (split2[3].equals("T")?"True":"False") + ", " 
			+ (split2[4].equals("T")?"True":"False")
			+ "), ");
			createTableSQLBuilder.append(split1[0].toLowerCase().replaceAll("-", "")+ " " + typeToPostgresType(split2[0]) + ", ");
			this.columns.add(split1[0].toLowerCase().toLowerCase().replaceAll("-", ""));
		}
		
		//Adding dynamic_class column
		createTableSQLBuilder.append("dynamic_class"+ " " + "TEXT" + ", ");
				
		br.close();
		sql.replace(sql.length()-2, sql.length(), ";");
		createTableSQLBuilder.replace(createTableSQLBuilder.length()-2,createTableSQLBuilder.length(), ");");
		this.createTableSQL = createTableSQLBuilder.toString();
//		System.out.println(createTableSQL);
		
		return sql.toString();
	}
	
	/*Base on schema input from front-end, create database and insert variables into metadatatable
	 *First return is the metadatatable
	 *Second return is the setup of the new database
	 */
	public String[] schemeFileToMetaSQLStream2(AxisVariables axisVariables) throws IOException{

		String tablename = axisVariables.getDatasetName().toLowerCase();
		StringBuilder createTableSQLBuilder = new StringBuilder("Create table " + tablename + "(");
		createTableSQLBuilder.append("id SERIAL PRIMARY KEY, ");
		StringBuilder tableAttributes = new StringBuilder();
		tableAttributes.append(tablename + "(");
		
		StringBuffer sql = new StringBuffer("INSERT INTO zenvisage_metatable (tablename, attribute, type) VALUES ");
		
		String[][] xList = axisVariables.getXList();
		String[][] yList = axisVariables.getYList();
		String[][] zList = axisVariables.getZList();
		for(int i = 0; i < xList.length; i++) {
			String attribute = xList[i][0];
			String type = xList[i][1];
			sql.append("('" + tablename + "', '" + attribute.toLowerCase().replaceAll("-", "") + "', '" + type + "'), ");
			createTableSQLBuilder.append(attribute.toLowerCase().replaceAll("-", "")+ " " + typeToPostgresType(type) + ", ");
			tableAttributes.append(attribute.toLowerCase().toLowerCase().replaceAll("-", ""));
			tableAttributes.append(",");
		}
		
		for(int i = 0; i < yList.length; i++) {
			String attribute = yList[i][0];
			String type = yList[i][1];
			sql.append("('" + tablename + "', '" + attribute.toLowerCase().replaceAll("-", "") + "', '" + type + "'), ");
			createTableSQLBuilder.append(attribute.toLowerCase().replaceAll("-", "")+ " " + typeToPostgresType(type) + ", ");
			tableAttributes.append(attribute.toLowerCase().toLowerCase().replaceAll("-", ""));
			tableAttributes.append(",");
		}
		
		for(int i = 0; i < zList.length; i++) {
			String attribute = zList[i][0];
			String type = zList[i][1];
			sql.append("('" + tablename + "', '" + attribute.toLowerCase().replaceAll("-", "") + "', '" + type + "'), ");
			createTableSQLBuilder.append(attribute.toLowerCase().replaceAll("-", "")+ " " + typeToPostgresType(type) + ", ");
			tableAttributes.append(attribute.toLowerCase().toLowerCase().replaceAll("-", ""));
			tableAttributes.append(",");
		}
		
		tableAttributes.deleteCharAt(tableAttributes.length()-1);
		tableAttributes.append(")");
		
		//Adding dynamic_class column
		createTableSQLBuilder.append("dynamic_class"+ " " + "TEXT" + ", ");
				
		sql.replace(sql.length()-2, sql.length(), ";");
		createTableSQLBuilder.replace(createTableSQLBuilder.length()-2,createTableSQLBuilder.length(), ");");
		 
		System.out.println("this.createTableSQL:"+this.createTableSQL);

		return new String[]{sql.toString(),createTableSQLBuilder.toString(),tableAttributes.toString()};
	}
	
	/*Base on schema input from front-end, create database and insert variables into metadatatable
	 *First return is the metadatatable insert sql
	 *Second return is the new csv database create sql
	 */
	public String[] schemeFileToMetaSQLStream3(Variables variables) throws IOException{

		String tablename = variables.getDatasetName().toLowerCase();
		StringBuilder createTableSQLBuilder = new StringBuilder("Create table " + tablename + "(");
		createTableSQLBuilder.append("id SERIAL PRIMARY KEY, ");
		
		StringBuffer sql = new StringBuffer("INSERT INTO zenvisage_metatable (tablename, attribute, type, selectedX, selectedY, selectedZ) VALUES ");
		
		List<Variable> list = variables.getVariables();
		for(int i = 0; i < list.size(); i++) {
			Variable v = list.get(i);
			String attribute = v.getName();
			String type = v.getType();
			sql.append("('" + tablename + "', '" + attribute.toLowerCase().replaceAll("-", "") + "', '" 
			+ type + "', " + v.isSelectedX() + ", " + 
					v.isSelectedY() + ", " + v.isSelectedZ() + "), ");
			createTableSQLBuilder.append(attribute.toLowerCase().replaceAll("-", "")+ " " + typeToPostgresType(type) + ", ");
		}
		
		//Adding dynamic_class column
		createTableSQLBuilder.append("dynamic_class"+ " " + "TEXT" + ", ");
				
		sql.replace(sql.length()-2, sql.length(), ";");
		createTableSQLBuilder.replace(createTableSQLBuilder.length()-2,createTableSQLBuilder.length(), ");");
		 
		System.out.println("metatable insert sql:"+sql.toString());
		System.out.println("create csv table sql:"+createTableSQLBuilder.toString());

		return new String[]{sql.toString(),createTableSQLBuilder.toString()};
	}
	
	public String typeToPostgresType(String type){
		switch (type){
			case "float": return "REAL";
			case "int": return "INT";
			case "string": return "TEXT";
			case "timestamp": return "timestamp";
			case "date": return "timestamp";
		}
		return null;
	}
	
}
