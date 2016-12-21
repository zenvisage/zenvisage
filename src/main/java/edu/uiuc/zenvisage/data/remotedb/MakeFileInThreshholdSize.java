package edu.uiuc.zenvisage.data.remotedb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MakeFileInThreshholdSize {
	private static final String READFILENAME = "data/flight-random2M.csv";
	private static final String WriteFILENAME ="data/flights.csv";
	private static final int N = 2000000;

	public static void main(String[] args) throws IOException {
		BufferedReader br = null;
		FileReader fr = null;
		fr = new FileReader(READFILENAME);
		br = new BufferedReader(fr);
		
		BufferedWriter bw = null;
		FileWriter fw = null;
		fw = new FileWriter(WriteFILENAME);
		bw = new BufferedWriter(fw);
			
		String sCurrentLine;
		bw.write("year,month,day,weekday,carrier,origin,destination,arrivaldelay,departuredelay,weatherdelay,distance\n");
		int n = 0;
		while ((sCurrentLine = br.readLine()) != null && n++ < N) {
			String[] sArr = sCurrentLine.split(",");
			//7,8,9,arrivaldelay,departuredelay,weatherdelay
			if (Integer.parseInt(sArr[7]) < 0 || Integer.parseInt(sArr[8]) < 0 || Integer.parseInt(sArr[9]) < 0 || Integer.parseInt(sArr[10]) < 0){
				continue;
			}			
			bw.write(sCurrentLine+"\n");
		}
		
		br.close();
		fr.close();
		bw.close();
		fw.close();
		System.out.println("Completed!");
	}
}
