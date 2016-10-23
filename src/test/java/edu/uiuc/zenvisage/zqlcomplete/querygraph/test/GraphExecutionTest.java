package edu.uiuc.zenvisage.zqlcomplete.querygraph.test;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Test;

import edu.uiuc.zenvisage.service.ZvMain;

public class GraphExecutionTest {

	@Test
	public void TestBasicQueryExecution() {
		String arg = "{\"zqlRows\":[{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f1\"},\"x\":{\"values\":[\"'Year'\"]},\"y\":{\"values\":[\"'SoldPrice'\"]},\"z\":{\"column\":\"'State'\",\"values\":[\"*\"]}}]}";

		try {
			ZvMain zvMain = new ZvMain();
			System.out.println("testing");
			String outputGraphExecutor = zvMain.runQueryGraph(arg);
			System.out.println("Output");
			System.out.println(outputGraphExecutor);
			
			
			String outputOldExecutor = zvMain.runZQLCompleteQuery(arg);
			System.out.println("Old Output");
			System.out.println(outputOldExecutor);
			
		} catch (IOException | InterruptedException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
