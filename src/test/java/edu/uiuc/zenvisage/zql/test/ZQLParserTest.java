package edu.uiuc.zenvisage.zql.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import edu.uiuc.zenvisage.service.ZvMain;
import edu.uiuc.zenvisage.zql.ZQLParser;

public class ZQLParserTest {

	@Test
	public void testAxis() {
		ZQLParser.parseScript("az y1 = *");

		ZQLParser.parseScript("ax z1 = [state.*]");
		ZQLParser.parseScript("ax z1 = [year, month]");
		ZQLParser.parseScript("ax z1 = year");
		ZQLParser.parseScript("ax z1 = temp, month");
		// These two * ones not working for now
		ZQLParser.parseScript("az y1 = [*]");
		ZQLParser.parseScript("az y1 = *");
		
	}
	
	@Test
	public void testParseScript() {
		ZQLParser.parseScript("db = real_estate\n"
				+ "ax x1 = [year]\n"
				+ "ax y1 = [soldprice]\n"
				+ "ax z1 = [state.*]\n"
				+ "vc f1 = {x1, y1, z1}\n"
				+ "ax y2 = [listingprice]\n"
				+ "vc f2 = {x1, y2, z2}\n"
				+ "ax v1 = process(argmin={z1},k=1,DEuclidean(f1,f2))\n"
				+ "ax y2 = [soldprice, listingprice]\n" // can actually reuse y2!
				+ "vc f3 = {x1, y2, v1}\n"
				+ "display(f3)");
	}
	
	@Test
	public void testAll() {
		ZvMain zvMain = new ZvMain();
		String script = "db = real_estate\n"
				+ "ax x1 = [year]\n"
				+ "ax y1 = [soldprice]\n"
				+ "ax z1 = [state.*]\n"
				+ "vc f1 = {x1, y1, z1}\n"
				+ "ax y2 = [listingprice]\n"
				+ "vc f2 = {x1, y2, z1}\n"
				+ "ax v1 = process(argmin={z1},k=1,DEuclidean(f1,f2))\n"
				+ "ax y2 = [soldprice, listingprice]\n" // can actually reuse y2!
				+ "vc f3 = {x1, y2, v1}\n"
				+ "display(f3)";
		try {
			zvMain.runZQLScript(script);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
