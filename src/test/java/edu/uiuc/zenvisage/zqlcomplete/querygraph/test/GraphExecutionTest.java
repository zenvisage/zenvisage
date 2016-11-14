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
		String arg = "{\"zqlRows\":[{\"name\":{\"output\":true,\"sketch\":false,\"name\":\"f1\"},\"x\":{\"attributes\":[\"'Year'\"]},\"y\":{\"attributes\":[\"'SoldPrice'\"]},\"z\":{\"attribute\":\"'State'\",\"values\":[\"*\"]}}]}";
		//String arg = "{\"zqlRows\":[{\"name\":{\"name\":\"f1\",\"output\":false,\"sketch\":false},\"x\":{\"variable\":\"\",\"attributes\":\"Year\",\"values\":[\"'Year'\"]},\"y\":{\"variable\":\"\",\"attribute\":\"SoldPrice\",\"values\":[\"'SoldPrice'\"]},\"z\":{\"variable\":\"\",\"attribute\":\"State\",\"values\":[\"*\"],\"expression\":\"\"},\"processe\":{\"variables\":[],\"method\":\"\",\"axis\":[],\"count\":\"\",\"metric\":\"\",\"arguments\":[],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"output\":false,\"constraint\":[]}]}";

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
	public void TestCrossProductProcessQueryExecution() {

		//String arg = "{\"zqlRows\":[{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f1\"},\"x\":{\"variable\":\"x1\",\"values\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"values\":[\"'soldprice'\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"'state'\",\"values\":[\"*\"]},\"constraints\":[{\"key\":\"state\",\"operator\":\"=\",\"value\":\"'CA'\"}]},{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f2\"},\"x\":{\"variable\":\"x1\",\"values\":[]},\"y\":{\"variable\":\"y1\",\"values\":[]},\"z\":{\"variable\":\"z1\",\"values\":[]},\"processe\":{\"variables\":[\"v1\"],\"method\":\"Dissimilar\",\"count\":\"7\",\"metric\":\"D\",\"arguments\":[\"f1\",\"f2\"]}},{\"name\":{\"output\":true,\"sketch\":false,\"name\":\"f3\"},\"x\":{\"variable\":\"x1\",\"values\":[]},\"y\":{\"variable\":\"y1\",\"values\":[]},\"z\":{\"variable\":\"v1\",\"values\":[]}}]}";
		// attribute can be null or empty string
		//String arg = "{\"zqlRows\":[{\"name\":{\"name\":\"f1\",\"output\":false,\"sketch\":false},\"x\":{\"variable\":\"x1\",\"attribute\":\"year\",\"values\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"attribute\":\"soldprice\",\"values\":[\"'soldprice'\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"state\",\"values\":[\"*\"],\"expression\":\"\"},\"processe\":{\"variables\":[],\"method\":\"\",\"axis\":[],\"count\":\"\",\"metric\":\"\",\"arguments\":[],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"constraint\":[{\"key\":\"state\",\"operator\":\"=\",\"value\":\"'CA'\"}],\"output\":false},{\"name\":{\"name\":\"f2\",\"output\":false,\"sketch\":false},\"x\":{\"variable\":\"x1\",\"attribute\":null,\"values\":[]},\"y\":{\"variable\":\"y1\",\"attribute\":null,\"values\":[]},\"z\":{\"variable\":\"z1\",\"attribute\":\"\",\"values\":[],\"expression\":\"\"},\"processe\":{\"variables\":[\"v1\"],\"method\":\"Dissimilar\",\"axis\":[],\"count\":\"7\",\"metric\":\"D\",\"arguments\":[\"f1\",\"f2\"],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"constraint\":[],\"output\":false},{\"name\":{\"name\":\"f3\",\"output\":true,\"sketch\":false},\"x\":{\"variable\":\"x1\",\"attribute\":null,\"values\":[]},\"y\":{\"variable\":\"y1\",\"attribute\":null,\"values\":[]},\"z\":{\"variable\":\"v1\",\"attribute\":\"\",\"values\":[],\"expression\":\"\"},\"processe\":{\"variables\":[],\"method\":\"\",\"axis\":[],\"count\":\"\",\"metric\":\"\",\"arguments\":[],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"constraint\":[],\"output\":true}]}";
		String arg = "{\"zqlRows\":[{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f1\"},\"x\":{\"variable\":\"x1\",\"attributes\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"attributes\":[\"'soldprice'\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"'state'\",\"values\":[\"*\"]},\"constraints\":[{\"key\":\"state\",\"operator\":\"=\",\"value\":\"'CA'\"}]},{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f2\"},\"x\":{\"variable\":\"x1\",\"attributes\":[]},\"y\":{\"variable\":\"y1\",\"attributes\":[]},\"z\":{\"variable\":\"z2\",\"attribute\":\"'state'\",\"values\":[\"*\"]},\"processe\":{\"variables\":[\"v1\",\"v2\"],\"method\":\"DEuclidean\",\"count\":\"7\",\"metric\":\"argmin\",\"arguments\":[\"f1\",\"f2\"],\"axisList1\":[\"z1\"],\"axisList2\":[\"z2\"]}},{\"name\":{\"output\":true,\"sketch\":false,\"name\":\"f3\"},\"x\":{\"variable\":\"x1\",\"attributes\":[]},\"y\":{\"variable\":\"y1\",\"attributes\":[]},\"z\":{\"variable\":\"v2\",\"values\":[]}}]}";
				
		try {
			ZvMain zvMain = new ZvMain();
			System.out.println("testing crossproduct process query");
			String outputGraphExecutor = zvMain.runQueryGraph(arg);
			System.out.println("Cross Product Output");
			System.out.println(outputGraphExecutor);
			System.out.println(outputGraphExecutor.length());
			
			System.out.println("old stuff");
			//String outputOldExecutor = zvMain.runZQLCompleteQuery(arg);
			System.out.println("Old Output");
			//System.out.println(outputOldExecutor);
			
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void TestPairwiseProcessQueryExecution() {

		//String arg = "{\"zqlRows\":[{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f1\"},\"x\":{\"variable\":\"x1\",\"values\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"values\":[\"'soldprice'\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"'state'\",\"values\":[\"*\"]},\"constraints\":[{\"key\":\"state\",\"operator\":\"=\",\"value\":\"'CA'\"}]},{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f2\"},\"x\":{\"variable\":\"x1\",\"values\":[]},\"y\":{\"variable\":\"y1\",\"values\":[]},\"z\":{\"variable\":\"z1\",\"values\":[]},\"processe\":{\"variables\":[\"v1\"],\"method\":\"Dissimilar\",\"count\":\"7\",\"metric\":\"D\",\"arguments\":[\"f1\",\"f2\"]}},{\"name\":{\"output\":true,\"sketch\":false,\"name\":\"f3\"},\"x\":{\"variable\":\"x1\",\"values\":[]},\"y\":{\"variable\":\"y1\",\"values\":[]},\"z\":{\"variable\":\"v1\",\"values\":[]}}]}";
		// attribute can be null or empty string
		//String arg = "{\"zqlRows\":[{\"name\":{\"name\":\"f1\",\"output\":false,\"sketch\":false},\"x\":{\"variable\":\"x1\",\"attribute\":\"year\",\"values\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"attribute\":\"soldprice\",\"values\":[\"'soldprice'\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"state\",\"values\":[\"*\"],\"expression\":\"\"},\"processe\":{\"variables\":[],\"method\":\"\",\"axis\":[],\"count\":\"\",\"metric\":\"\",\"arguments\":[],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"constraint\":[{\"key\":\"state\",\"operator\":\"=\",\"value\":\"'CA'\"}],\"output\":false},{\"name\":{\"name\":\"f2\",\"output\":false,\"sketch\":false},\"x\":{\"variable\":\"x1\",\"attribute\":null,\"values\":[]},\"y\":{\"variable\":\"y1\",\"attribute\":null,\"values\":[]},\"z\":{\"variable\":\"z1\",\"attribute\":\"\",\"values\":[],\"expression\":\"\"},\"processe\":{\"variables\":[\"v1\"],\"method\":\"Dissimilar\",\"axis\":[],\"count\":\"7\",\"metric\":\"D\",\"arguments\":[\"f1\",\"f2\"],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"constraint\":[],\"output\":false},{\"name\":{\"name\":\"f3\",\"output\":true,\"sketch\":false},\"x\":{\"variable\":\"x1\",\"attribute\":null,\"values\":[]},\"y\":{\"variable\":\"y1\",\"attribute\":null,\"values\":[]},\"z\":{\"variable\":\"v1\",\"attribute\":\"\",\"values\":[],\"expression\":\"\"},\"processe\":{\"variables\":[],\"method\":\"\",\"axis\":[],\"count\":\"\",\"metric\":\"\",\"arguments\":[],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"constraint\":[],\"output\":true}]}";
		String arg = "{\"zqlRows\":[{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f1\"},\"x\":{\"variable\":\"x1\",\"attributes\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"attributes\":[\"'soldprice'\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"'state'\",\"values\":[\"*\"]}},{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f2\"},\"x\":{\"variable\":\"x1\",\"attributes\":[]},\"y\":{\"variable\":\"y2\",\"attributes\":[\"listingprice\"]},\"z\":{\"variable\":\"z1\",\"values\":[]},\"processe\":{\"variables\":[\"v1\"],\"method\":\"DEuclidean\",\"count\":\"7\",\"metric\":\"argmin\",\"arguments\":[\"f1\",\"f2\"],\"axisList1\":[\"y1\",\"y2\"],\"axisList2\":[]}},{\"name\":{\"output\":true,\"sketch\":false,\"name\":\"f3\"},\"x\":{\"variable\":\"x1\",\"attributes\":[]},\"y\":{\"variable\":\"y3\",\"attributes\":[\"soldprice\",\"listingprice\"]},\"z\":{\"variable\":\"v1\",\"values\":[]}}]}";
				
		try {
			ZvMain zvMain = new ZvMain();
			System.out.println("testing pairwise process query");
			String outputGraphExecutor = zvMain.runQueryGraph(arg);
			System.out.println("Pairwise Output");
			System.out.println(outputGraphExecutor);
			System.out.println(outputGraphExecutor.length());

			
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void TestXYProcessQueryExecution() {

		//String arg = "{\"zqlRows\":[{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f1\"},\"x\":{\"variable\":\"x1\",\"values\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"values\":[\"'soldprice'\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"'state'\",\"values\":[\"*\"]},\"constraints\":[{\"key\":\"state\",\"operator\":\"=\",\"value\":\"'CA'\"}]},{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f2\"},\"x\":{\"variable\":\"x1\",\"values\":[]},\"y\":{\"variable\":\"y1\",\"values\":[]},\"z\":{\"variable\":\"z1\",\"values\":[]},\"processe\":{\"variables\":[\"v1\"],\"method\":\"Dissimilar\",\"count\":\"7\",\"metric\":\"D\",\"arguments\":[\"f1\",\"f2\"]}},{\"name\":{\"output\":true,\"sketch\":false,\"name\":\"f3\"},\"x\":{\"variable\":\"x1\",\"values\":[]},\"y\":{\"variable\":\"y1\",\"values\":[]},\"z\":{\"variable\":\"v1\",\"values\":[]}}]}";
		// attribute can be null or empty string
		//String arg = "{\"zqlRows\":[{\"name\":{\"name\":\"f1\",\"output\":false,\"sketch\":false},\"x\":{\"variable\":\"x1\",\"attribute\":\"year\",\"values\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"attribute\":\"soldprice\",\"values\":[\"'soldprice'\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"state\",\"values\":[\"*\"],\"expression\":\"\"},\"processe\":{\"variables\":[],\"method\":\"\",\"axis\":[],\"count\":\"\",\"metric\":\"\",\"arguments\":[],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"constraint\":[{\"key\":\"state\",\"operator\":\"=\",\"value\":\"'CA'\"}],\"output\":false},{\"name\":{\"name\":\"f2\",\"output\":false,\"sketch\":false},\"x\":{\"variable\":\"x1\",\"attribute\":null,\"values\":[]},\"y\":{\"variable\":\"y1\",\"attribute\":null,\"values\":[]},\"z\":{\"variable\":\"z1\",\"attribute\":\"\",\"values\":[],\"expression\":\"\"},\"processe\":{\"variables\":[\"v1\"],\"method\":\"Dissimilar\",\"axis\":[],\"count\":\"7\",\"metric\":\"D\",\"arguments\":[\"f1\",\"f2\"],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"constraint\":[],\"output\":false},{\"name\":{\"name\":\"f3\",\"output\":true,\"sketch\":false},\"x\":{\"variable\":\"x1\",\"attribute\":null,\"values\":[]},\"y\":{\"variable\":\"y1\",\"attribute\":null,\"values\":[]},\"z\":{\"variable\":\"v1\",\"attribute\":\"\",\"values\":[],\"expression\":\"\"},\"processe\":{\"variables\":[],\"method\":\"\",\"axis\":[],\"count\":\"\",\"metric\":\"\",\"arguments\":[],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"constraint\":[],\"output\":true}]}";
		String arg = "{\"zqlRows\":[{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f1\"},\"x\":{\"variable\":\"x1\",\"attributes\":[\"'year'\",\"month\"]},\"y\":{\"variable\":\"y1\",\"attributes\":[\"'soldprice'\",\"listingprice\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"'state'\",\"values\":[\"'CA'\"]}},{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f2\"},\"x\":{\"variable\":\"x1\",\"attributes\":[]},\"y\":{\"variable\":\"y1\",\"attributes\":[]},\"z\":{\"variable\":\"z2\",\"attribute\":\"'state'\",\"values\":[\"'NY'\"]},\"processe\":{\"variables\":[\"x2\",\"y2\"],\"method\":\"DEuclidean\",\"count\":\"1\",\"metric\":\"argmin\",\"arguments\":[\"f1\",\"f2\"],\"axisList1\":[\"x1\",\"y1\"],\"axisList2\":[]}},{\"name\":{\"output\":true,\"sketch\":false,\"name\":\"f3\"},\"x\":{\"variable\":\"x2\",\"attributes\":[]},\"y\":{\"variable\":\"y2\",\"attributes\":[]},\"z\":{\"variable\":\"v3\",\"attribute\":\"'state'\",\"values\":[\"'CA'\", \"'NY'\"]}}]}";
				
		try {
			ZvMain zvMain = new ZvMain();
			System.out.println("testing XY process query");
			String outputGraphExecutor = zvMain.runQueryGraph(arg);
			System.out.println("XY Output");
			System.out.println(outputGraphExecutor);
			System.out.println(outputGraphExecutor.length());

			
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// instead of z1<-'state'.'ca', we do z1<-'state'.*, and add constraint state='CA'
	@Test
	public void TestXYConstraintProcessQueryExecution() {

		//String arg = "{\"zqlRows\":[{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f1\"},\"x\":{\"variable\":\"x1\",\"values\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"values\":[\"'soldprice'\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"'state'\",\"values\":[\"*\"]},\"constraints\":[{\"key\":\"state\",\"operator\":\"=\",\"value\":\"'CA'\"}]},{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f2\"},\"x\":{\"variable\":\"x1\",\"values\":[]},\"y\":{\"variable\":\"y1\",\"values\":[]},\"z\":{\"variable\":\"z1\",\"values\":[]},\"processe\":{\"variables\":[\"v1\"],\"method\":\"Dissimilar\",\"count\":\"7\",\"metric\":\"D\",\"arguments\":[\"f1\",\"f2\"]}},{\"name\":{\"output\":true,\"sketch\":false,\"name\":\"f3\"},\"x\":{\"variable\":\"x1\",\"values\":[]},\"y\":{\"variable\":\"y1\",\"values\":[]},\"z\":{\"variable\":\"v1\",\"values\":[]}}]}";
		// attribute can be null or empty string
		//String arg = "{\"zqlRows\":[{\"name\":{\"name\":\"f1\",\"output\":false,\"sketch\":false},\"x\":{\"variable\":\"x1\",\"attribute\":\"year\",\"values\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"attribute\":\"soldprice\",\"values\":[\"'soldprice'\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"state\",\"values\":[\"*\"],\"expression\":\"\"},\"processe\":{\"variables\":[],\"method\":\"\",\"axis\":[],\"count\":\"\",\"metric\":\"\",\"arguments\":[],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"constraint\":[{\"key\":\"state\",\"operator\":\"=\",\"value\":\"'CA'\"}],\"output\":false},{\"name\":{\"name\":\"f2\",\"output\":false,\"sketch\":false},\"x\":{\"variable\":\"x1\",\"attribute\":null,\"values\":[]},\"y\":{\"variable\":\"y1\",\"attribute\":null,\"values\":[]},\"z\":{\"variable\":\"z1\",\"attribute\":\"\",\"values\":[],\"expression\":\"\"},\"processe\":{\"variables\":[\"v1\"],\"method\":\"Dissimilar\",\"axis\":[],\"count\":\"7\",\"metric\":\"D\",\"arguments\":[\"f1\",\"f2\"],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"constraint\":[],\"output\":false},{\"name\":{\"name\":\"f3\",\"output\":true,\"sketch\":false},\"x\":{\"variable\":\"x1\",\"attribute\":null,\"values\":[]},\"y\":{\"variable\":\"y1\",\"attribute\":null,\"values\":[]},\"z\":{\"variable\":\"v1\",\"attribute\":\"\",\"values\":[],\"expression\":\"\"},\"processe\":{\"variables\":[],\"method\":\"\",\"axis\":[],\"count\":\"\",\"metric\":\"\",\"arguments\":[],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"constraint\":[],\"output\":true}]}";
		String arg = "{\"zqlRows\":[{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f1\"},\"x\":{\"variable\":\"x1\",\"attributes\":[\"'year'\",\"month\"]},\"y\":{\"variable\":\"y1\",\"attributes\":[\"'soldprice'\",\"'listingprice'\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"'state'\",\"values\":[\"*\"]},\"constraints\":[{\"key\":\"state\",\"operator\":\"=\",\"value\":\"'CA'\"}]},{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f2\"},\"x\":{\"variable\":\"x1\",\"attributes\":[]},\"y\":{\"variable\":\"y1\",\"attributes\":[]},\"z\":{\"variable\":\"z2\",\"attribute\":\"'state'\",\"values\":[\"'NY'\"]},\"constraints\":[{\"key\":\"state\",\"operator\":\"=\",\"value\":\"'NY'\"}],\"processe\":{\"variables\":[\"x2\",\"y2\"],\"method\":\"DEuclidean\",\"count\":\"1\",\"metric\":\"argmin\",\"arguments\":[\"f1\",\"f2\"],\"axisList1\":[\"x1\",\"y1\"],\"axisList2\":[]}},{\"name\":{\"output\":true,\"sketch\":false,\"name\":\"f3\"},\"x\":{\"variable\":\"x2\",\"attributes\":[]},\"y\":{\"variable\":\"y2\",\"attributes\":[]},\"z\":{\"variable\":\"v3\",\"attribute\":\"'state'\",\"values\":[\"'CA'\", \"'NY'\"]},\"constraints\":[{\"key\":\"state\",\"operator\":\"=\",\"value\":\"'CA'\"},{\"key\":\"state\",\"operator\":\"=\",\"value\":\"'NY'\"}]}]}";
				
		try {
			ZvMain zvMain = new ZvMain();
			System.out.println("testing XY process query");
			String outputGraphExecutor = zvMain.runQueryGraph(arg);
			System.out.println("XY Output");
			System.out.println(outputGraphExecutor);
			System.out.println(outputGraphExecutor.length());

			
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
