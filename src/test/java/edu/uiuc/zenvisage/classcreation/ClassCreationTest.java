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
		String input = "{\"dataset\":\"real_estate\",\"classes\":[{\"name\":\"soldpricepersqft\",\"values\":[[0,100],[100,25144.643]]},{\"name\":\"listingpricepersqft\",\"values\":[[0,500],[500,1457.0552]]}]}";
	    
		try {
			ZvMain zvMain = new ZvMain();
			System.out.println("testing basic input parsing");
			String output = zvMain.runCreateClasses(input);
			System.out.println("Output");
			
			//assertFalse(outputGraphExecutor.equals(nullOutput));
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
	
