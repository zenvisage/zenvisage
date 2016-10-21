package edu.uiuc.zenvisage.zqlcomplete.querygraph.test;

import java.io.IOException;

import org.junit.Test;

import edu.uiuc.zenvisage.service.ZvMain;

public class GraphExecutionTest {

	@Test
	public void TestBasicQueryExecution() {
		String arg = "{\"zqlRows\":[{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f1\"},\"x\":{\"values\":[\"'year'\"]},\"y\":{\"values\":[\"'soldprice'\"]},\"z\":{\"column\":\"'state'\",\"values\":[\"*\"]}}]}";

		try {
			ZvMain zvMain = new ZvMain();
			System.out.println("testing");
			String outputGraphExecutor = zvMain.runQueryGraph(arg);
			System.out.println("Output");
			System.out.println(outputGraphExecutor);
			
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
