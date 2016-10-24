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
			System.out.println("testing basic query");
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
	
	@Test
	public void TestProcessQueryExecution() {
		String arg = "{\"zqlRows\":[{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f1\"},\"x\":{\"variable\":\"x1\",\"values\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"values\":[\"'soldprice'\"]},\"z\":{\"variable\":\"z1\",\"column\":\"'state'\",\"values\":[\"*\"]},\"constraints\":[{\"key\":\"state\",\"operator\":\"=\",\"value\":\"'CA'\"}]},{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f2\"},\"x\":{\"variable\":\"x1\",\"values\":[]},\"y\":{\"variable\":\"y1\",\"values\":[]},\"z\":{\"variable\":\"z1\",\"values\":[]},\"processe\":{\"variables\":[\"v1\"],\"method\":\"Dissimilar\",\"count\":\"7\",\"metric\":\"D\",\"arguments\":[\"f1\",\"f2\"]}},{\"name\":{\"output\":true,\"sketch\":false,\"name\":\"f3\"},\"x\":{\"variable\":\"x1\",\"values\":[]},\"y\":{\"variable\":\"y1\",\"values\":[]},\"z\":{\"variable\":\"v1\",\"values\":[]}}]}";
		
		try {
			ZvMain zvMain = new ZvMain();
			System.out.println("testing process query");
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
