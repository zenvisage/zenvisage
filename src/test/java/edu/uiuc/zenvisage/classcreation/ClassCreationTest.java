package edu.uiuc.zenvisage.classcreation;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import edu.uiuc.zenvisage.service.ZvMain;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLTable;

public class ClassCreationTest {
	
	@Test
	public void TestInputParsing() throws SQLException {
		long startTime = System.currentTimeMillis();
		
		String input = "{\"dataset\":\"real_estate\",\"classes\":["
				+ "{\"name\":\"soldpricepersqft\",\"values\":[[0,200],[201,2000]]},"
				+ "{\"name\":\"pctincreasing\",\"values\":[[0,90],[90,100]]},"
				+ "{\"name\":\"month\",\"values\":[[0,5],[5,10],[10,13]]}"
				+ "]}";
	    
		try {
			ZvMain zvMain = new ZvMain();
			System.out.println("testing basic input parsing");
			zvMain.runCreateClasses(input);
			long endTime  = System.currentTimeMillis();
			
			long totalTime = endTime - startTime;
			System.out.println("TotalTime:"+totalTime/1000);
			
			//assertFalse(outputGraphExecutor.equals(nullOutput));
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
	
