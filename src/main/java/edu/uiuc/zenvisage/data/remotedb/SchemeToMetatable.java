package edu.uiuc.zenvisage.data.remotedb;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SchemeToMetatable {
	
	public StringBuilder createTableSQL;
	public List<String> columns;
	
	public SchemeToMetatable(){
		createTableSQL =null;
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
	
	
	public String schemeFileToMetaSQLStream(String filePath, String tablename) throws IOException{
		System.out.println(filePath);
		System.out.println(tablename);
//		InputStream is = getClass().getResourceAsStream(filePath);
		
		this.createTableSQL = new StringBuilder("Create table " + tablename + "(");
		
	   	BufferedReader br = new BufferedReader(new FileReader(filePath));
		StringBuffer sql = new StringBuffer("INSERT INTO zenvisage_metatable (tablename, attribute, type) VALUES ");
		String sCurrentLine;
		while ((sCurrentLine = br.readLine()) != null) {
			System.out.println(sCurrentLine);
			String split1[] = sCurrentLine.split(":");
			String split2[] = split1[1].split(",");
			sql.append("('" + tablename + "', '" + split1[0] + "', '" + split2[0] + "'), ");
			this.createTableSQL.append(split1[0] + " " + typeToPostgresType(split2[0]) + ", ");
			this.columns.add(split1[0]);
		}
		br.close();
		sql.replace(sql.length()-2, sql.length(), ";");
		this.createTableSQL.replace(this.createTableSQL.length()-2,this.createTableSQL.length(), ");");
		
		System.out.println(this.createTableSQL);
		return sql.toString();
	}
	
	public String typeToPostgresType(String type){
		switch (type){
			case "float": return "REAL";
			case "int": return "INT";
			case "string": return "TEXT";
		}
		return null;
	}
	
}
