package edu.uiuc.zenvisage.zqlcomplete.querygraph.test;

import java.io.IOException;
import java.sql.SQLException;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import edu.uiuc.zenvisage.service.ZvMain;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLTable;

public class GraphExecutionTest {

	@Test
	public void TestBasicQueryExecution() {
		//String arg = "{\"zqlRows\":[{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f1\"},\"x\":{\"values\":[\"'Year'\"]},\"y\":{\"values\":[\"'SoldPrice'\"]},\"z\":{\"attribute\":\"'State'\",\"values\":[\"*\"]}}]}";
		String arg = "{\"zqlRows\":[{\"name\":{\"name\":\"f1\",\"output\":false,\"sketch\":false},\"x\":{\"variable\":\"\",\"attribute\":\"Year\",\"values\":[\"'Year'\"]},\"y\":{\"variable\":\"\",\"attribute\":\"SoldPrice\",\"values\":[\"'SoldPrice'\"]},\"z\":{\"variable\":\"\",\"attribute\":\"State\",\"values\":[\"*\"],\"expression\":\"\"},\"processe\":{\"variables\":[],\"method\":\"\",\"axis\":[],\"count\":\"\",\"metric\":\"\",\"arguments\":[],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"output\":false,\"constraint\":[]}]}";

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

		//String arg = "{\"zqlRows\":[{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f1\"},\"x\":{\"variable\":\"x1\",\"values\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"values\":[\"'soldprice'\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"'state'\",\"values\":[\"*\"]},\"constraints\":[{\"key\":\"state\",\"operator\":\"=\",\"value\":\"'CA'\"}]},{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f2\"},\"x\":{\"variable\":\"x1\",\"values\":[]},\"y\":{\"variable\":\"y1\",\"values\":[]},\"z\":{\"variable\":\"z1\",\"values\":[]},\"processe\":{\"variables\":[\"v1\"],\"method\":\"Dissimilar\",\"count\":\"7\",\"metric\":\"D\",\"arguments\":[\"f1\",\"f2\"]}},{\"name\":{\"output\":true,\"sketch\":false,\"name\":\"f3\"},\"x\":{\"variable\":\"x1\",\"values\":[]},\"y\":{\"variable\":\"y1\",\"values\":[]},\"z\":{\"variable\":\"v1\",\"values\":[]}}]}";
		// attribute can be null or empty string
		String arg = "{\"zqlRows\":[{\"name\":{\"name\":\"f1\",\"output\":false,\"sketch\":false},\"x\":{\"variable\":\"x1\",\"attribute\":\"year\",\"values\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"attribute\":\"soldprice\",\"values\":[\"'soldprice'\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"state\",\"values\":[\"*\"],\"expression\":\"\"},\"processe\":{\"variables\":[],\"method\":\"\",\"axis\":[],\"count\":\"\",\"metric\":\"\",\"arguments\":[],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"constraint\":[{\"key\":\"state\",\"operator\":\"=\",\"value\":\"'CA'\"}],\"output\":false},{\"name\":{\"name\":\"f2\",\"output\":false,\"sketch\":false},\"x\":{\"variable\":\"x1\",\"attribute\":null,\"values\":[]},\"y\":{\"variable\":\"y1\",\"attribute\":null,\"values\":[]},\"z\":{\"variable\":\"z1\",\"attribute\":\"\",\"values\":[],\"expression\":\"\"},\"processe\":{\"variables\":[\"v1\"],\"method\":\"Dissimilar\",\"axis\":[],\"count\":\"7\",\"metric\":\"D\",\"arguments\":[\"f1\",\"f2\"],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"constraint\":[],\"output\":false},{\"name\":{\"name\":\"f3\",\"output\":true,\"sketch\":false},\"x\":{\"variable\":\"x1\",\"attribute\":null,\"values\":[]},\"y\":{\"variable\":\"y1\",\"attribute\":null,\"values\":[]},\"z\":{\"variable\":\"v1\",\"attribute\":\"\",\"values\":[],\"expression\":\"\"},\"processe\":{\"variables\":[],\"method\":\"\",\"axis\":[],\"count\":\"\",\"metric\":\"\",\"arguments\":[],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"constraint\":[],\"output\":true}]}";

				
		try {
			ZvMain zvMain = new ZvMain();
			System.out.println("testing process query");
			String outputGraphExecutor = zvMain.runQueryGraph(arg);
			System.out.println("Output");
			System.out.println(outputGraphExecutor);
			
			System.out.println("old stuff");
			//String outputOldExecutor = zvMain.runZQLCompleteQuery(arg);
			System.out.println("Old Output");
			//System.out.println(outputOldExecutor);
			
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
