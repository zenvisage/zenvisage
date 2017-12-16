package edu.uiuc.zenvisage.zql.test;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.uiuc.zenvisage.zql.ZQLParser;

public class ZQLParserTest {

	@Test
	public void testAxis() {
		ZQLParser.parseScript("ax z1 = [state.*]");
		ZQLParser.parseScript("ax z1 = [year, month]");
		ZQLParser.parseScript("ax z1 = year");
		ZQLParser.parseScript("ax z1 = temp, month");
		// These two * ones not working for now
		ZQLParser.parseScript("az y1 = [*]");
		ZQLParser.parseScript("az y1 = *");
		
	}
	@Test
	public void testAll() {
		ZQLParser.parseScript("ax x1 = [year]\n"
				+ "ax y1 = [soldprice]\n"
				+ "ax z1 = [state.*]\n"
				+ "vc f1 = {x1, y1, z1}\n"
				+ "ax y2 = [listingprice]\n"
				+ "vc f2 = {x1, y1, z2}\n"
				+ "process(argmin={z1},k=1,DEuclidean(f1,f2))");
		
	}
}
