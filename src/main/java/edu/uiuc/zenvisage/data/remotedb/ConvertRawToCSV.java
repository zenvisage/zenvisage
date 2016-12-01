package edu.uiuc.zenvisage.data.remotedb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConvertRawToCSV {
	private static final String READFILENAME = "flight-100.csv";
	private static final String WriteFILENAME = "flight-100-processed.csv";

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
		
		while ((sCurrentLine = br.readLine()) != null) {
			bw.write(sCurrentLine+"^M");
		}
		br.close();
		fr.close();
		bw.close();
		fw.close();
	}
}
