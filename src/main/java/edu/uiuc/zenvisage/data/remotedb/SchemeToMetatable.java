package edu.uiuc.zenvisage.data.remotedb;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class SchemeToMetatable {
	public static void main(String[] args) throws IOException{
		String tablename = "real_estate";
		String filePath = "/Users/chaoran/Desktop/zenvisage/zenvisage/src/main/resources/data/real_estate.txt";
		System.out.println(schemeFileToMetaSQL(filePath, tablename));
	}
	
	public static String schemeFileToMetaSQL(String filePath, String tablename) throws IOException{
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
}
