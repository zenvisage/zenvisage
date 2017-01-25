/**
 * 
 */
package edu.uiuc.zenvisage.service.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author tarique
 *
 */
public class MergeDateFields {
	static String inputfilename="src/main/resources/data/flights.csv";
	static String outputfilename="src/main/resources/data/flights_dt.csv";
	static String newfilename="";
	static int dayindex=2;
	static int monthindex=1;
	static int yearindex=0;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new FileReader(inputfilename));
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputfilename));
		try {
		    String line = br.readLine();
		    String[] words = line.split(",");
		    StringBuilder newline = new StringBuilder();
	    	for(int i=0;i<words.length;i++){
	    		if(i!=dayindex && i!=monthindex && i!=yearindex){
	    			newline.append(words[i]+",");
	    		}		    		
	    	}
	    	newline.append("date"+System.lineSeparator());
	    	writer.write(newline.toString());
	    	
	    	
	    	line = br.readLine();
		    while (line != null) {
		    	String day="";
		    	String month="";
		    	String year="";
		    	newline = new StringBuilder();
		        words = line.split(",");
		    	for(int i=0;i<words.length;i++){
		    		if(i!=dayindex && i!=monthindex && i!=yearindex){
		    			newline.append(words[i]+",");
		    		}		    		
		    	}
		    	day=words[dayindex];
	    		month=words[monthindex];
	    		year=words[yearindex];
	    		String date=year+"-"+month+"-"+day;
	    		newline.append(date+System.lineSeparator());
		        writer.write(newline.toString());
		    	line = br.readLine();
		    }
		   
		} finally {
		    br.close();
		    writer.close();
		}
	}

}
