package edu.uiuc.zenvisage.zqlcomplete.querygraph.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uiuc.zenvisage.service.ZvMain;
import edu.uiuc.zenvisage.zql.ScatterVCNode;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLTable;

public class GraphExecutionTest {
	static final Logger logger = LoggerFactory.getLogger(GraphExecutionTest.class);
	String nullOutput = "{\"outputCharts\":[],\"method\":null,\"xUnit\":null,\"yUnit\":null,\"totalPage\":0}";

	@Test
	public void TestBasicQueryExecution() throws SQLException {
		//Q1 (similar)
		String arg = "{\"temp\":1, \"db\":\"real_estate\", \"zqlRows\":[{\"name\":{\"output\":true,\"sketch\":false,\"name\":\"f1\"},\"x\":{\"attributes\":[\"'Year'\"]},\"y\":{\"attributes\":[\"'SoldPrice'\"]},\"z\":{\"attribute\":\"'State'\",\"values\":[\"*\"]}}]}";		//String arg = "{\"zqlRows\":[{\"name\":{\"name\":\"f1\",\"output\":false,\"sketch\":false},\"x\":{\"variable\":\"\",\"attributes\":\"Year\",\"values\":[\"'Year'\"]},\"y\":{\"variable\":\"\",\"attribute\":\"SoldPrice\",\"values\":[\"'SoldPrice'\"]},\"z\":{\"variable\":\"\",\"attribute\":\"State\",\"values\":[\"*\"],\"expression\":\"\"},\"processe\":{\"variables\":[],\"method\":\"\",\"axis\":[],\"count\":\"\",\"metric\":\"\",\"arguments\":[],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"output\":false,\"constraint\":[]}]}";

		try {
			ZvMain zvMain = new ZvMain();
			System.out.println("testing basic query");
			String outputGraphExecutor = zvMain.runQueryGraph(arg);
			System.out.println("Output");
			System.out.println(outputGraphExecutor);
			
			assertFalse(outputGraphExecutor.equals(nullOutput));	
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// when the z field is empty, we should return one viz overall for the whole table with given x and y
	@Test
	public void TestBasicQueryAllZExecution() throws SQLException {
		//Q1 (similar)
		String arg = "{\"db\":\"real_estate\", \"zqlRows\":[{\"name\":{\"output\":true,\"sketch\":false,\"name\":\"f1\"},\"x\":{\"attributes\":[\"'Year'\"]},\"y\":{\"attributes\":[\"'SoldPrice'\"]}}]}";
		//String arg = "{\"zqlRows\":[{\"name\":{\"name\":\"f1\",\"output\":false,\"sketch\":false},\"x\":{\"variable\":\"\",\"attributes\":\"Year\",\"values\":[\"'Year'\"]},\"y\":{\"variable\":\"\",\"attribute\":\"SoldPrice\",\"values\":[\"'SoldPrice'\"]},\"z\":{\"variable\":\"\",\"attribute\":\"State\",\"values\":[\"*\"],\"expression\":\"\"},\"processe\":{\"variables\":[],\"method\":\"\",\"axis\":[],\"count\":\"\",\"metric\":\"\",\"arguments\":[],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"output\":false,\"constraint\":[]}]}";

		try {
			ZvMain zvMain = new ZvMain();
			System.out.println("testing basic query");
			String outputGraphExecutor = zvMain.runQueryGraph(arg);
			System.out.println("Output");
			System.out.println(outputGraphExecutor);
			
			assertFalse(outputGraphExecutor.equals(nullOutput));
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	@Test
	public void TestSketchQueryExecution() throws SQLException {
		String arg = "{\"db\":\"real_estate\",\"zqlRows\":[{\"name\":{\"output\":true,\"sketch\":true,\"name\":\"f1\"},\"sketchPoints\":{\"xAxis\":\"year\",\"yAxis\":\"soldprice\",\"groupBy\":\"city\",\"points\":[{\"x\":\"5\", \"y\":\"10000\"}, {\"x\":\"7\", \"y\":\"20000\"}, {\"x\":\"9\", \"y\":\"30000\"}]}}]}";
		
		try {
			ZvMain zvMain = new ZvMain();
			System.out.println("testing Sketech query");
			String outputGraphExecutor = zvMain.runQueryGraph(arg);
			System.out.println("Output");
			System.out.println(outputGraphExecutor);
			
			assertFalse(outputGraphExecutor.equals(nullOutput));
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void TestAdvancedSketchQueryExecution() throws SQLException {
		String arg = "{\"db\":\"real_estate\", \"zqlRows\":[{\"name\":{\"output\":false,\"sketch\":true,\"name\":\"f1\"},\"sketchPoints\":{\"xAxis\":\"year\",\"yAxis\":\"soldprice\",\"groupBy\":\"city\",\"points\":[{\"x\":\"5\", \"y\":\"10000\"}, {\"x\":\"7\", \"y\":\"20000\"}, {\"x\":\"9\", \"y\":\"30000\"}]}},{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f2\"},\"x\":{\"variable\":\"x1\",\"attributes\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"attributes\":[\"'soldprice'\"]},\"z\":{\"variable\":\"z2\",\"attribute\":\"'state'\",\"values\":[\"*\"]},\"processe\":{\"variables\":[\"v2\"],\"method\":\"DEuclidean\",\"count\":\"1\",\"metric\":\"argmin\",\"arguments\":[\"f1\",\"f2\"],\"axisList1\":[\"z2\"],\"axisList2\":[]}},{\"name\":{\"output\":true,\"sketch\":false,\"name\":\"f3\"},\"x\":{\"variable\":\"x1\",\"attributes\":[]},\"y\":{\"variable\":\"y1\",\"attributes\":[]},\"z\":{\"variable\":\"v2\",\"values\":[]}}]}";
		
		try {
			ZvMain zvMain = new ZvMain();
			System.out.println("testing Sketech query");
			String outputGraphExecutor = zvMain.runQueryGraph(arg);
			System.out.println("Output");
			System.out.println(outputGraphExecutor);
			
			assertFalse(outputGraphExecutor.equals(nullOutput));
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test 
	public void TestExpertSketchQueryExeuction() throws SQLException {
		String arg = "{\"zqlRows\":[{\"name\":{\"output\":false,\"sketch\":true,\"name\":\"f1\"},\"x\":{\"attributes\":[\"'year'\"],\"variable\":\"x1\"},\"y\":{\"attributes\":[\"'soldprice'\"],\"variable\":\"y1\"},\"z\":{\"attribute\":\"'state'\",\"values\":[\"*\"],\"variable\":\"z1\"},\"constraints\":\"state='NY'\",\"sketchPoints\":{\"points\":[{\"x\":1,\"y\":0},{\"x\":1.11,\"y\":331953.48615},{\"x\":1.22,\"y\":663906.9723},{\"x\":1.33,\"y\":995860.45845},{\"x\":1.44,\"y\":1327813.9446},{\"x\":1.55,\"y\":1659767.4307499998},{\"x\":1.6600000000000001,\"y\":1991720.9169},{\"x\":1.77,\"y\":2323674.40305},{\"x\":1.88,\"y\":2655627.8892},{\"x\":1.99,\"y\":2987581.37535},{\"x\":2.1,\"y\":3319534.8614999996},{\"x\":2.21,\"y\":3651488.34765},{\"x\":2.3200000000000003,\"y\":3983441.8338},{\"x\":2.4299999999999997,\"y\":4315395.31995},{\"x\":2.54,\"y\":4647348.8061},{\"x\":2.65,\"y\":4979302.29225},{\"x\":2.76,\"y\":5311255.7784},{\"x\":2.87,\"y\":5643209.2645499995},{\"x\":2.98,\"y\":5975162.7507},{\"x\":3.09,\"y\":6307116.236849999},{\"x\":3.2,\"y\":6639069.722999999},{\"x\":3.31,\"y\":6971023.20915},{\"x\":3.42,\"y\":7302976.6953},{\"x\":3.53,\"y\":7634930.18145},{\"x\":3.64,\"y\":7966883.6676},{\"x\":3.75,\"y\":8298837.15375},{\"x\":3.86,\"y\":8630790.6399},{\"x\":3.97,\"y\":8962744.12605},{\"x\":4.08,\"y\":9294697.6122},{\"x\":4.1899999999999995,\"y\":9626651.09835},{\"x\":4.3,\"y\":9958604.5845},{\"x\":4.41,\"y\":10290558.07065},{\"x\":4.52,\"y\":10622511.5568},{\"x\":4.63,\"y\":10954465.042949999},{\"x\":4.74,\"y\":11286418.529099999},{\"x\":4.85,\"y\":11618372.01525},{\"x\":4.96,\"y\":11950325.5014},{\"x\":5.07,\"y\":12282278.987549998},{\"x\":5.18,\"y\":12614232.473699998},{\"x\":5.29,\"y\":12946185.959849998},{\"x\":5.4,\"y\":13278139.445999999},{\"x\":5.51,\"y\":13610092.932149999},{\"x\":5.62,\"y\":13942046.4183},{\"x\":5.73,\"y\":14273999.90445},{\"x\":5.84,\"y\":14605953.3906},{\"x\":5.95,\"y\":14937906.87675},{\"x\":6.06,\"y\":15269860.3629},{\"x\":6.17,\"y\":15601813.84905},{\"x\":6.28,\"y\":15933767.3352},{\"x\":6.39,\"y\":16265720.82135},{\"x\":6.5,\"y\":16597674.3075},{\"x\":6.61,\"y\":16929627.79365},{\"x\":6.72,\"y\":17261581.2798},{\"x\":6.83,\"y\":17593534.76595},{\"x\":6.94,\"y\":17925488.2521},{\"x\":7.05,\"y\":18257441.73825},{\"x\":7.16,\"y\":18589395.2244},{\"x\":7.2700000000000005,\"y\":18921348.71055},{\"x\":7.38,\"y\":19253302.1967},{\"x\":7.49,\"y\":19585255.68285},{\"x\":7.6,\"y\":19917209.169},{\"x\":7.71,\"y\":20249162.65515},{\"x\":7.82,\"y\":20581116.1413},{\"x\":7.93,\"y\":20913069.62745},{\"x\":8.04,\"y\":21245023.1136},{\"x\":8.15,\"y\":21576976.599749997},{\"x\":8.26,\"y\":21908930.085899998},{\"x\":8.370000000000001,\"y\":22240883.572049998},{\"x\":8.48,\"y\":22572837.058199998},{\"x\":8.59,\"y\":22904790.54435},{\"x\":8.7,\"y\":23236744.0305},{\"x\":8.809999999999999,\"y\":23568697.51665},{\"x\":8.92,\"y\":23900651.0028},{\"x\":9.03,\"y\":24232604.48895},{\"x\":9.14,\"y\":24564557.975099996},{\"x\":9.25,\"y\":24896511.46125},{\"x\":9.36,\"y\":25228464.947399996},{\"x\":9.47,\"y\":25560418.43355},{\"x\":9.58,\"y\":25892371.919699997},{\"x\":9.69,\"y\":26224325.40585},{\"x\":9.8,\"y\":26556278.891999997},{\"x\":9.91,\"y\":26888232.37815},{\"x\":10.02,\"y\":27220185.864299998},{\"x\":10.13,\"y\":27552139.35045},{\"x\":10.24,\"y\":27884092.8366},{\"x\":10.35,\"y\":28216046.322750002},{\"x\":10.46,\"y\":28547999.8089},{\"x\":10.57,\"y\":28879953.295049995},{\"x\":10.68,\"y\":29211906.7812},{\"x\":10.790000000000001,\"y\":29543860.267349996},{\"x\":10.9,\"y\":29875813.7535},{\"x\":11.01,\"y\":30207767.239649996},{\"x\":11.12,\"y\":30539720.7258},{\"x\":11.23,\"y\":30871674.211949997},{\"x\":11.34,\"y\":31203627.6981},{\"x\":11.45,\"y\":31535581.184249997},{\"x\":11.56,\"y\":31867534.6704},{\"x\":11.67,\"y\":32199488.156549998},{\"x\":11.78,\"y\":32531441.6427},{\"x\":11.89,\"y\":32863395.128849998}],\"minX\":1,\"maxX\":12,\"minY\":15384.615,\"maxY\":33179964,\"yAxis\":\"soldprice\",\"xAxis\":\"year\",\"groupBy\":\"state\",\"aggrFunc\":\"avg\",\"aggrVar\":\"soldprice\"}},{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f2\"},\"x\":{\"variable\":\"x1\",\"attributes\":[]},\"y\":{\"variable\":\"y1\",\"attributes\":[]},\"z\":{\"variable\":\"z2\",\"attribute\":\"'state'\",\"values\":[\"*\"]},\"processe\":{\"variables\":[\"v1\",\"v2\"],\"method\":\"DEuclidean\",\"axisList1\":[\"z1\"],\"axisList2\":[\"z2\"],\"count\":\"3\",\"metric\":\"argmin\",\"arguments\":[\"f1\",\"f2\"]}},{\"name\":{\"output\":true,\"sketch\":false,\"name\":\"f3\"},\"x\":{\"variable\":\"x1\",\"attributes\":[]},\"y\":{\"variable\":\"y1\",\"attributes\":[]},\"z\":{\"variable\":\"v2\",\"values\":[]}}],\"db\":\"real_estate\"}";
		
		try {
			ZvMain zvMain = new ZvMain();
			System.out.println("testing Sketech query");
			String outputGraphExecutor = zvMain.runQueryGraph(arg);
			System.out.println("Output");
			System.out.println(outputGraphExecutor);
			
			assertFalse(outputGraphExecutor.equals(nullOutput));
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	@Test
	// does basic scatter fetch then filter
	public void TestScatterQueryExecution() throws SQLException {
		String arg = "{\"db\":\"real_estate\", \"zqlRows\":[{\"name\":{\"output\":true,\"sketch\":true,\"name\":\"f1\"},\"x\":{\"variable\":\"x1\",\"attributes\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"attributes\":[\"'soldprice'\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"'state'\",\"values\":[\"*\"]},\"sketchPoints\":{\"xAxis\":\"year\",\"yAxis\":\"soldprice\",\"groupBy\":\"state\",\"polygons\":[ {\"points\":[ {\"xval\":\"5\", \"yval\":\"200000\"}, {\"xval\":\"200\", \"yval\":\"410585\"} ]} ] }, \"viz\":{\"map\": {\"type\":\"scatter\"}} , \"processe\":{\"variables\":[\"v2\"],\"method\":\"Filter\",\"count\":\"1\",\"metric\":\"argmin\",\"arguments\":[\"f1\"],\"axisList1\":[],\"axisList2\":[]}}]}";
		
		try {
			ZvMain zvMain = new ZvMain();
			logger.info("testing Scatter query");
			String outputGraphExecutor = zvMain.runScatterQueryGraph(arg);
			logger.info("Output");
			//logger.info(outputGraphExecutor);

			assertFalse(outputGraphExecutor.equals(nullOutput));
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	//aggregates all Z into one scatter plot
	public void TestAggregateScatterQueryExecution() throws SQLException {
		String arg = "{\"db\":\"real_estate\", \"zqlRows\":[{\"name\":{\"output\":false,\"sketch\":true,\"name\":\"f1\"},\"x\":{\"variable\":\"x1\",\"attributes\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"attributes\":[\"'soldprice'\"]},\"z\":{\"aggregate\":true,\"variable\":\"z1\",\"attribute\":\"'state'\",\"values\":[\"*\"]},\"sketchPoints\":{\"xAxis\":\"year\",\"yAxis\":\"soldprice\",\"groupBy\":\"state\",\"polygons\":[ {\"points\":[ {\"xval\":\"5\", \"yval\":\"200000\"}, {\"xval\":\"200\", \"yval\":\"410585\"} ]} ] }, \"viz\":{\"map\": {\"type\":\"scatter\"}} , \"processe\":{\"variables\":[\"v2\"],\"method\":\"Filter\",\"count\":\"1\",\"metric\":\"argmin\",\"arguments\":[\"f1\"],\"axisList1\":[],\"axisList2\":[]}}]}";
		
		try {
			ZvMain zvMain = new ZvMain();
			logger.info("testing Scatter query");
			String outputGraphExecutor = zvMain.runScatterQueryGraph(arg);
			logger.info("Output");
			//logger.info(outputGraphExecutor);

			assertFalse(outputGraphExecutor.equals(nullOutput));
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	// currently unfinished
	@Test
	public void TestAdvancedScatterQueryExecution() throws SQLException {
		String arg = "{\"db\":\"real_estate\", \"zqlRows\":[{\"name\":{\"output\":false,\"sketch\":true,\"name\":\"f1\"},\"x\":{\"variable\":\"x1\",\"attributes\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"attributes\":[\"'soldprice'\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"'state'\",\"values\":[\"*\"]},\"sketchPoints\":{\"xAxis\":\"year\",\"yAxis\":\"soldprice\",\"groupBy\":\"state\",\"polygons\":[ {\"points\":[ {\"xval\":\"5\", \"yval\":\"200000\"}, {\"xval\":\"200\", \"yval\":\"410585\"} ]} ] }, \"viz\":{\"map\": {\"type\":\"scatter\"}}  },{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f2\"},\"x\":{\"variable\":\"x1\",\"attributes\":[]},\"y\":{\"variable\":\"y1\",\"attributes\":[]},\"z\":{\"variable\":\"z1\",\"values\":[]}, \"viz\":{\"map\": {\"type\":\"scatter\"}},\"processe\":{\"variables\":[\"v2\"],\"method\":\"Filter\",\"count\":\"1\",\"metric\":\"argmin\",\"arguments\":[\"f1\"],\"axisList1\":[],\"axisList2\":[]}}]}";
		
		try {
			ZvMain zvMain = new ZvMain();
			logger.info("testing Scatter query");
			String outputGraphExecutor = zvMain.runQueryGraph(arg);
			logger.info("Output");
			logger.info(outputGraphExecutor);
			
			assertFalse(outputGraphExecutor.equals(nullOutput));
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void TestIncreasingQueryExecution() throws SQLException {
		//Q4
		//String arg = "{\"db\":\"real_estate\", \"zqlRows\":[{\"name\":{\"output\":true,\"sketch\":false,\"name\":\"f1\"},\"x\":{\"attributes\":[\"'Year'\"]},\"y\":{\"attributes\":[\"'SoldPrice'\"]},\"z\":{\"attribute\":\"'State'\",\"values\":[\"*\"]}}]}";
		//String arg = "{\"zqlRows\":[{\"name\":{\"name\":\"f1\",\"output\":false,\"sketch\":false},\"x\":{\"variable\":\"\",\"attributes\":\"Year\",\"values\":[\"'Year'\"]},\"y\":{\"variable\":\"\",\"attribute\":\"SoldPrice\",\"values\":[\"'SoldPrice'\"]},\"z\":{\"variable\":\"\",\"attribute\":\"State\",\"values\":[\"*\"],\"expression\":\"\"},\"processe\":{\"variables\":[],\"method\":\"\",\"axis\":[],\"count\":\"\",\"metric\":\"\",\"arguments\":[],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"output\":false,\"constraint\":[]}]}";
		String arg = "{\"zqlRows\":[{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f1\"},\"x\":{\"variable\":\"x1\",\"attributes\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"attributes\":[\"'soldprice'\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"'state'\",\"values\":[\"*\"]},\"processe\":{\"variables\":[\"v1\"],\"method\":\"T\",\"axisList1\":[\"z1\"],\"axisList2\":[],\"count\":\"40\",\"metric\":\"argmax\",\"arguments\":[\"f1\"]}},{\"name\":{\"output\":true,\"sketch\":false,\"name\":\"f2\"},\"x\":{\"variable\":\"x1\",\"attributes\":[]},\"y\":{\"variable\":\"y1\",\"attributes\":[]},\"z\":{\"variable\":\"v1\",\"values\":[]}}],\"db\":\"real_estate\"}";
		try {
			ZvMain zvMain = new ZvMain();
			System.out.println("testing Increasing query");
			String outputGraphExecutor = zvMain.runQueryGraph(arg);
			System.out.println("Output");
			System.out.println(outputGraphExecutor);
			
			assertFalse(outputGraphExecutor.equals(nullOutput));
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	// Test deprecated, will fail because expects {z1}x{z2} syntax
	@Test
	public void TestCrossProductProcessQueryExecution() throws SQLException {
		//Q2
		//String arg = "{\"zqlRows\":[{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f1\"},\"x\":{\"variable\":\"x1\",\"values\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"values\":[\"'soldprice'\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"'state'\",\"values\":[\"*\"]},\"constraints\":[{\"key\":\"state\",\"operator\":\"=\",\"value\":\"'CA'\"}]},{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f2\"},\"x\":{\"variable\":\"x1\",\"values\":[]},\"y\":{\"variable\":\"y1\",\"values\":[]},\"z\":{\"variable\":\"z1\",\"values\":[]},\"processe\":{\"variables\":[\"v1\"],\"method\":\"Dissimilar\",\"count\":\"7\",\"metric\":\"D\",\"arguments\":[\"f1\",\"f2\"]}},{\"name\":{\"output\":true,\"sketch\":false,\"name\":\"f3\"},\"x\":{\"variable\":\"x1\",\"values\":[]},\"y\":{\"variable\":\"y1\",\"values\":[]},\"z\":{\"variable\":\"v1\",\"values\":[]}}]}";
		// attribute can be null or empty string
		//String arg = "{\"zqlRows\":[{\"name\":{\"name\":\"f1\",\"output\":false,\"sketch\":false},\"x\":{\"variable\":\"x1\",\"attribute\":\"year\",\"values\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"attribute\":\"soldprice\",\"values\":[\"'soldprice'\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"state\",\"values\":[\"*\"],\"expression\":\"\"},\"processe\":{\"variables\":[],\"method\":\"\",\"axis\":[],\"count\":\"\",\"metric\":\"\",\"arguments\":[],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"constraint\":[{\"key\":\"state\",\"operator\":\"=\",\"value\":\"'CA'\"}],\"output\":false},{\"name\":{\"name\":\"f2\",\"output\":false,\"sketch\":false},\"x\":{\"variable\":\"x1\",\"attribute\":null,\"values\":[]},\"y\":{\"variable\":\"y1\",\"attribute\":null,\"values\":[]},\"z\":{\"variable\":\"z1\",\"attribute\":\"\",\"values\":[],\"expression\":\"\"},\"processe\":{\"variables\":[\"v1\"],\"method\":\"Dissimilar\",\"axis\":[],\"count\":\"7\",\"metric\":\"D\",\"arguments\":[\"f1\",\"f2\"],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"constraint\":[],\"output\":false},{\"name\":{\"name\":\"f3\",\"output\":true,\"sketch\":false},\"x\":{\"variable\":\"x1\",\"attribute\":null,\"values\":[]},\"y\":{\"variable\":\"y1\",\"attribute\":null,\"values\":[]},\"z\":{\"variable\":\"v1\",\"attribute\":\"\",\"values\":[],\"expression\":\"\"},\"processe\":{\"variables\":[],\"method\":\"\",\"axis\":[],\"count\":\"\",\"metric\":\"\",\"arguments\":[],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"constraint\":[],\"output\":true}]}";
		String arg = "{\"db\":\"real_estate\", \"zqlRows\":[{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f1\"},\"x\":{\"variable\":\"x1\",\"attributes\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"attributes\":[\"'soldprice'\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"'state'\",\"values\":[\"*\"]},\"constraints\":\"state='CA'\"},{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f2\"},\"x\":{\"variable\":\"x1\",\"attributes\":[]},\"y\":{\"variable\":\"y1\",\"attributes\":[]},\"z\":{\"variable\":\"z2\",\"attribute\":\"'state'\",\"values\":[\"*\"]},\"processe\":{\"variables\":[\"v1\",\"v2\"],\"method\":\"DEuclidean\",\"count\":\"1\",\"metric\":\"argmin\",\"arguments\":[\"f1\",\"f2\"],\"axisList1\":[\"z1\"],\"axisList2\":[\"z2\"]}},{\"name\":{\"output\":true,\"sketch\":false,\"name\":\"f3\"},\"x\":{\"variable\":\"x1\",\"attributes\":[]},\"y\":{\"variable\":\"y2\",\"attributes\":[\"soldprice\", \"soldpricepersqft\"]},\"z\":{\"variable\":\"v2\",\"values\":[]}}]}";
				
		try {
			ZvMain zvMain = new ZvMain();
			System.out.println("testing crossproduct process query");
			String outputGraphExecutor = zvMain.runQueryGraph(arg);
			System.out.println("Cross Product Output");
			System.out.println(outputGraphExecutor);
			System.out.println(outputGraphExecutor.length());
			
			assertFalse(outputGraphExecutor.equals(nullOutput));
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Testing if we can understand {z1}x{z2} by just writing {z1,z2}
	// Finds the states that are most similar to the soldprice over year trend for CA
	@Test
	public void TextNewCrossProductExecution() throws SQLException{
		String arg = "{\"db\":\"real_estate\", \"zqlRows\":[{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f1\"},\"x\":{\"variable\":\"x1\",\"attributes\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"attributes\":[\"'soldprice'\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"'state'\",\"values\":[\"*\"]},\"constraints\":\"state='CA'\"},{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f2\"},\"x\":{\"variable\":\"x1\",\"attributes\":[]},\"y\":{\"variable\":\"y1\",\"attributes\":[]},\"z\":{\"variable\":\"z2\",\"attribute\":\"'state'\",\"values\":[\"*\"]},\"processe\":{\"variables\":[\"v1\",\"v2\"],\"method\":\"DEuclidean\",\"count\":\"1\",\"metric\":\"argmin\",\"arguments\":[\"f1\",\"f2\"],\"axisList1\":[\"z1\",\"z2\"],\"axisList2\":[]}},{\"name\":{\"output\":true,\"sketch\":false,\"name\":\"f3\"},\"x\":{\"variable\":\"x1\",\"attributes\":[]},\"y\":{\"variable\":\"y2\",\"attributes\":[\"soldprice\", \"soldpricepersqft\"]},\"z\":{\"variable\":\"v2\",\"values\":[]}}]}";
		
		try {
			ZvMain zvMain = new ZvMain();
			System.out.println("testing crossproduct process query");
			String outputGraphExecutor = zvMain.runQueryGraph(arg);
			System.out.println("Cross Product Output");
			System.out.println(outputGraphExecutor);
			System.out.println(outputGraphExecutor.length());
			
			assertFalse(outputGraphExecutor.equals(nullOutput));
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	@Test
	// cross product query, but testing v2<-argmax_{z2} instead of v1,v2<-argmax_{z1,z2}
	public void TestOnevsManyQueryExecution() throws SQLException {
		//Q2
		//String arg = "{\"zqlRows\":[{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f1\"},\"x\":{\"variable\":\"x1\",\"values\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"values\":[\"'soldprice'\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"'state'\",\"values\":[\"*\"]},\"constraints\":[{\"key\":\"state\",\"operator\":\"=\",\"value\":\"'CA'\"}]},{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f2\"},\"x\":{\"variable\":\"x1\",\"values\":[]},\"y\":{\"variable\":\"y1\",\"values\":[]},\"z\":{\"variable\":\"z1\",\"values\":[]},\"processe\":{\"variables\":[\"v1\"],\"method\":\"Dissimilar\",\"count\":\"7\",\"metric\":\"D\",\"arguments\":[\"f1\",\"f2\"]}},{\"name\":{\"output\":true,\"sketch\":false,\"name\":\"f3\"},\"x\":{\"variable\":\"x1\",\"values\":[]},\"y\":{\"variable\":\"y1\",\"values\":[]},\"z\":{\"variable\":\"v1\",\"values\":[]}}]}";
		// attribute can be null or empty string
		//String arg = "{\"zqlRows\":[{\"name\":{\"name\":\"f1\",\"output\":false,\"sketch\":false},\"x\":{\"variable\":\"x1\",\"attribute\":\"year\",\"values\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"attribute\":\"soldprice\",\"values\":[\"'soldprice'\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"state\",\"values\":[\"*\"],\"expression\":\"\"},\"processe\":{\"variables\":[],\"method\":\"\",\"axis\":[],\"count\":\"\",\"metric\":\"\",\"arguments\":[],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"constraint\":[{\"key\":\"state\",\"operator\":\"=\",\"value\":\"'CA'\"}],\"output\":false},{\"name\":{\"name\":\"f2\",\"output\":false,\"sketch\":false},\"x\":{\"variable\":\"x1\",\"attribute\":null,\"values\":[]},\"y\":{\"variable\":\"y1\",\"attribute\":null,\"values\":[]},\"z\":{\"variable\":\"z1\",\"attribute\":\"\",\"values\":[],\"expression\":\"\"},\"processe\":{\"variables\":[\"v1\"],\"method\":\"Dissimilar\",\"axis\":[],\"count\":\"7\",\"metric\":\"D\",\"arguments\":[\"f1\",\"f2\"],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"constraint\":[],\"output\":false},{\"name\":{\"name\":\"f3\",\"output\":true,\"sketch\":false},\"x\":{\"variable\":\"x1\",\"attribute\":null,\"values\":[]},\"y\":{\"variable\":\"y1\",\"attribute\":null,\"values\":[]},\"z\":{\"variable\":\"v1\",\"attribute\":\"\",\"values\":[],\"expression\":\"\"},\"processe\":{\"variables\":[],\"method\":\"\",\"axis\":[],\"count\":\"\",\"metric\":\"\",\"arguments\":[],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"constraint\":[],\"output\":true}]}";
		String arg = "{\"db\":\"real_estate\", \"zqlRows\":[{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f1\"},\"x\":{\"variable\":\"x1\",\"attributes\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"attributes\":[\"'soldprice'\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"'state'\",\"values\":[\"*\"]},\"constraints\":\"state='CA'\"},{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f2\"},\"x\":{\"variable\":\"x1\",\"attributes\":[]},\"y\":{\"variable\":\"y1\",\"attributes\":[]},\"z\":{\"variable\":\"z2\",\"attribute\":\"'state'\",\"values\":[\"*\"]},\"processe\":{\"variables\":[\"v2\"],\"method\":\"DEuclidean\",\"count\":\"3\",\"metric\":\"argmax\",\"arguments\":[\"f1\",\"f2\"],\"axisList1\":[\"z2\"],\"axisList2\":[]}},{\"name\":{\"output\":true,\"sketch\":false,\"name\":\"f3\"},\"x\":{\"variable\":\"x1\",\"attributes\":[]},\"y\":{\"variable\":\"y1\",\"attributes\":[]},\"z\":{\"variable\":\"v2\",\"values\":[]}}]}";
				
		try {
			ZvMain zvMain = new ZvMain();
			System.out.println("testing crossproduct process query");
			String outputGraphExecutor = zvMain.runQueryGraph(arg);
			System.out.println("Cross Product Output");
			System.out.println(outputGraphExecutor);
			System.out.println(outputGraphExecutor.length());
			
			assertFalse(outputGraphExecutor.equals(nullOutput));
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	// Find the states where the soldprice over year trend is the most similar to its listingprice over year trend
	public void TestPairwiseProcessQueryExecution() throws SQLException {
		//Q6
		//String arg = "{\"zqlRows\":[{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f1\"},\"x\":{\"variable\":\"x1\",\"values\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"values\":[\"'soldprice'\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"'state'\",\"values\":[\"*\"]},\"constraints\":[{\"key\":\"state\",\"operator\":\"=\",\"value\":\"'CA'\"}]},{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f2\"},\"x\":{\"variable\":\"x1\",\"values\":[]},\"y\":{\"variable\":\"y1\",\"values\":[]},\"z\":{\"variable\":\"z1\",\"values\":[]},\"processe\":{\"variables\":[\"v1\"],\"method\":\"Dissimilar\",\"count\":\"7\",\"metric\":\"D\",\"arguments\":[\"f1\",\"f2\"]}},{\"name\":{\"output\":true,\"sketch\":false,\"name\":\"f3\"},\"x\":{\"variable\":\"x1\",\"values\":[]},\"y\":{\"variable\":\"y1\",\"values\":[]},\"z\":{\"variable\":\"v1\",\"values\":[]}}]}";
		// attribute can be null or empty string
		//String arg = "{\"zqlRows\":[{\"name\":{\"name\":\"f1\",\"output\":false,\"sketch\":false},\"x\":{\"variable\":\"x1\",\"attribute\":\"year\",\"values\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"attribute\":\"soldprice\",\"values\":[\"'soldprice'\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"state\",\"values\":[\"*\"],\"expression\":\"\"},\"processe\":{\"variables\":[],\"method\":\"\",\"axis\":[],\"count\":\"\",\"metric\":\"\",\"arguments\":[],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"constraint\":[{\"key\":\"state\",\"operator\":\"=\",\"value\":\"'CA'\"}],\"output\":false},{\"name\":{\"name\":\"f2\",\"output\":false,\"sketch\":false},\"x\":{\"variable\":\"x1\",\"attribute\":null,\"values\":[]},\"y\":{\"variable\":\"y1\",\"attribute\":null,\"values\":[]},\"z\":{\"variable\":\"z1\",\"attribute\":\"\",\"values\":[],\"expression\":\"\"},\"processe\":{\"variables\":[\"v1\"],\"method\":\"Dissimilar\",\"axis\":[],\"count\":\"7\",\"metric\":\"D\",\"arguments\":[\"f1\",\"f2\"],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"constraint\":[],\"output\":false},{\"name\":{\"name\":\"f3\",\"output\":true,\"sketch\":false},\"x\":{\"variable\":\"x1\",\"attribute\":null,\"values\":[]},\"y\":{\"variable\":\"y1\",\"attribute\":null,\"values\":[]},\"z\":{\"variable\":\"v1\",\"attribute\":\"\",\"values\":[],\"expression\":\"\"},\"processe\":{\"variables\":[],\"method\":\"\",\"axis\":[],\"count\":\"\",\"metric\":\"\",\"arguments\":[],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"constraint\":[],\"output\":true}]}";
		String arg = "{\"db\":\"real_estate\", \"zqlRows\":[{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f1\"},\"x\":{\"variable\":\"x1\",\"attributes\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"attributes\":[\"'soldprice'\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"'state'\",\"values\":[\"*\"]}},{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f2\"},\"x\":{\"variable\":\"x1\",\"attributes\":[]},\"y\":{\"variable\":\"y2\",\"attributes\":[\"listingprice\"]},\"z\":{\"variable\":\"z1\",\"values\":[]},\"processe\":{\"variables\":[\"v1\"],\"method\":\"DEuclidean\",\"count\":\"7\",\"metric\":\"argmin\",\"arguments\":[\"f1\",\"f2\"],\"axisList1\":[\"z1\"],\"axisList2\":[]}},{\"name\":{\"output\":true,\"sketch\":false,\"name\":\"f3\"},\"x\":{\"variable\":\"x1\",\"attributes\":[]},\"y\":{\"variable\":\"y3\",\"attributes\":[\"soldprice\",\"listingprice\"]},\"z\":{\"variable\":\"v1\",\"values\":[]}}]}";
				
		try {
			ZvMain zvMain = new ZvMain();
			System.out.println("testing pairwise process query");
			String outputGraphExecutor = zvMain.runQueryGraph(arg);
			System.out.println("Pairwise Output");
			System.out.println(outputGraphExecutor);
			System.out.println(outputGraphExecutor.length());
			
			assertFalse(outputGraphExecutor.equals(nullOutput));
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void TestXYProcessQueryExecution() throws SQLException {
		//Q3
		//String arg = "{\"zqlRows\":[{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f1\"},\"x\":{\"variable\":\"x1\",\"values\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"values\":[\"'soldprice'\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"'state'\",\"values\":[\"*\"]},\"constraints\":[{\"key\":\"state\",\"operator\":\"=\",\"value\":\"'CA'\"}]},{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f2\"},\"x\":{\"variable\":\"x1\",\"values\":[]},\"y\":{\"variable\":\"y1\",\"values\":[]},\"z\":{\"variable\":\"z1\",\"values\":[]},\"processe\":{\"variables\":[\"v1\"],\"method\":\"Dissimilar\",\"count\":\"7\",\"metric\":\"D\",\"arguments\":[\"f1\",\"f2\"]}},{\"name\":{\"output\":true,\"sketch\":false,\"name\":\"f3\"},\"x\":{\"variable\":\"x1\",\"values\":[]},\"y\":{\"variable\":\"y1\",\"values\":[]},\"z\":{\"variable\":\"v1\",\"values\":[]}}]}";
		// attribute can be null or empty string
		//String arg = "{\"zqlRows\":[{\"name\":{\"name\":\"f1\",\"output\":false,\"sketch\":false},\"x\":{\"variable\":\"x1\",\"attribute\":\"year\",\"values\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"attribute\":\"soldprice\",\"values\":[\"'soldprice'\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"state\",\"values\":[\"*\"],\"expression\":\"\"},\"processe\":{\"variables\":[],\"method\":\"\",\"axis\":[],\"count\":\"\",\"metric\":\"\",\"arguments\":[],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"constraint\":[{\"key\":\"state\",\"operator\":\"=\",\"value\":\"'CA'\"}],\"output\":false},{\"name\":{\"name\":\"f2\",\"output\":false,\"sketch\":false},\"x\":{\"variable\":\"x1\",\"attribute\":null,\"values\":[]},\"y\":{\"variable\":\"y1\",\"attribute\":null,\"values\":[]},\"z\":{\"variable\":\"z1\",\"attribute\":\"\",\"values\":[],\"expression\":\"\"},\"processe\":{\"variables\":[\"v1\"],\"method\":\"Dissimilar\",\"axis\":[],\"count\":\"7\",\"metric\":\"D\",\"arguments\":[\"f1\",\"f2\"],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"constraint\":[],\"output\":false},{\"name\":{\"name\":\"f3\",\"output\":true,\"sketch\":false},\"x\":{\"variable\":\"x1\",\"attribute\":null,\"values\":[]},\"y\":{\"variable\":\"y1\",\"attribute\":null,\"values\":[]},\"z\":{\"variable\":\"v1\",\"attribute\":\"\",\"values\":[],\"expression\":\"\"},\"processe\":{\"variables\":[],\"method\":\"\",\"axis\":[],\"count\":\"\",\"metric\":\"\",\"arguments\":[],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"constraint\":[],\"output\":true}]}";
		String arg = "{\"db\":\"real_estate\", \"zqlRows\":[{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f1\"},\"x\":{\"variable\":\"x1\",\"attributes\":[\"'year'\",\"month\"]},\"y\":{\"variable\":\"y1\",\"attributes\":[\"'soldprice'\",\"listingprice\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"'state'\",\"values\":[\"'CA'\"]}},{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f2\"},\"x\":{\"variable\":\"x1\",\"attributes\":[]},\"y\":{\"variable\":\"y1\",\"attributes\":[]},\"z\":{\"variable\":\"z2\",\"attribute\":\"'state'\",\"values\":[\"'NY'\"]},\"processe\":{\"variables\":[\"x2\",\"y2\"],\"method\":\"DEuclidean\",\"count\":\"1\",\"metric\":\"argmin\",\"arguments\":[\"f1\",\"f2\"],\"axisList1\":[\"x1\",\"y1\"],\"axisList2\":[]}},{\"name\":{\"output\":true,\"sketch\":false,\"name\":\"f3\"},\"x\":{\"variable\":\"x2\",\"attributes\":[]},\"y\":{\"variable\":\"y2\",\"attributes\":[]},\"z\":{\"variable\":\"v3\",\"attribute\":\"'state'\",\"values\":[\"'CA'\", \"'NY'\"]}}]}";
				
		try {
			ZvMain zvMain = new ZvMain();
			System.out.println("testing XY process query");
			String outputGraphExecutor = zvMain.runQueryGraph(arg);
			System.out.println("XY Output");
			System.out.println(outputGraphExecutor);
			System.out.println(outputGraphExecutor.length());

			assertFalse(outputGraphExecutor.equals(nullOutput));			
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// instead of z1<-'state'.'ca', we do z1<-'state'.*, and add constraint state='CA'
	@Test
	public void TestXYConstraintProcessQueryExecution() throws SQLException {

		//String arg = "{\"zqlRows\":[{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f1\"},\"x\":{\"variable\":\"x1\",\"values\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"values\":[\"'soldprice'\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"'state'\",\"values\":[\"*\"]},\"constraints\":[{\"key\":\"state\",\"operator\":\"=\",\"value\":\"'CA'\"}]},{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f2\"},\"x\":{\"variable\":\"x1\",\"values\":[]},\"y\":{\"variable\":\"y1\",\"values\":[]},\"z\":{\"variable\":\"z1\",\"values\":[]},\"processe\":{\"variables\":[\"v1\"],\"method\":\"Dissimilar\",\"count\":\"7\",\"metric\":\"D\",\"arguments\":[\"f1\",\"f2\"]}},{\"name\":{\"output\":true,\"sketch\":false,\"name\":\"f3\"},\"x\":{\"variable\":\"x1\",\"values\":[]},\"y\":{\"variable\":\"y1\",\"values\":[]},\"z\":{\"variable\":\"v1\",\"values\":[]}}]}";
		// attribute can be null or empty string
		//String arg = "{\"zqlRows\":[{\"name\":{\"name\":\"f1\",\"output\":false,\"sketch\":false},\"x\":{\"variable\":\"x1\",\"attribute\":\"year\",\"values\":[\"'year'\"]},\"y\":{\"variable\":\"y1\",\"attribute\":\"soldprice\",\"values\":[\"'soldprice'\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"state\",\"values\":[\"*\"],\"expression\":\"\"},\"processe\":{\"variables\":[],\"method\":\"\",\"axis\":[],\"count\":\"\",\"metric\":\"\",\"arguments\":[],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"constraint\":[{\"key\":\"state\",\"operator\":\"=\",\"value\":\"'CA'\"}],\"output\":false},{\"name\":{\"name\":\"f2\",\"output\":false,\"sketch\":false},\"x\":{\"variable\":\"x1\",\"attribute\":null,\"values\":[]},\"y\":{\"variable\":\"y1\",\"attribute\":null,\"values\":[]},\"z\":{\"variable\":\"z1\",\"attribute\":\"\",\"values\":[],\"expression\":\"\"},\"processe\":{\"variables\":[\"v1\"],\"method\":\"Dissimilar\",\"axis\":[],\"count\":\"7\",\"metric\":\"D\",\"arguments\":[\"f1\",\"f2\"],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"constraint\":[],\"output\":false},{\"name\":{\"name\":\"f3\",\"output\":true,\"sketch\":false},\"x\":{\"variable\":\"x1\",\"attribute\":null,\"values\":[]},\"y\":{\"variable\":\"y1\",\"attribute\":null,\"values\":[]},\"z\":{\"variable\":\"v1\",\"attribute\":\"\",\"values\":[],\"expression\":\"\"},\"processe\":{\"variables\":[],\"method\":\"\",\"axis\":[],\"count\":\"\",\"metric\":\"\",\"arguments\":[],\"parameters\":{}},\"viz\":{\"variable\":\"\",\"type\":[],\"parameters\":[]},\"sketchPoints\":null,\"constraint\":[],\"output\":true}]}";
		String arg = "{\"db\":\"real_estate\", \"zqlRows\":[{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f1\"},\"x\":{\"variable\":\"x1\",\"attributes\":[\"'year'\",\"month\"]},\"y\":{\"variable\":\"y1\",\"attributes\":[\"'soldprice'\",\"'listingprice'\"]},\"z\":{\"variable\":\"z1\",\"attribute\":\"'state'\",\"values\":[\"*\"]},\"constraints\":\"state='CA'\"},{\"name\":{\"output\":false,\"sketch\":false,\"name\":\"f2\"},\"x\":{\"variable\":\"x1\",\"attributes\":[]},\"y\":{\"variable\":\"y1\",\"attributes\":[]},\"z\":{\"variable\":\"z2\",\"attribute\":\"'state'\",\"values\":[\"'NY'\"]},\"constraints\":\"state='NY'\",\"processe\":{\"variables\":[\"x2\",\"y2\"],\"method\":\"DEuclidean\",\"count\":\"1\",\"metric\":\"argmin\",\"arguments\":[\"f1\",\"f2\"],\"axisList1\":[\"x1\",\"y1\"],\"axisList2\":[]}},{\"name\":{\"output\":true,\"sketch\":false,\"name\":\"f3\"},\"x\":{\"variable\":\"x2\",\"attributes\":[]},\"y\":{\"variable\":\"y2\",\"attributes\":[]},\"z\":{\"variable\":\"v3\",\"attribute\":\"'state'\",\"values\":[\"'CA'\", \"'NY'\"]},\"constraints\":\"state IN ('CA', 'NY')\"}]}";
				
		try {
			ZvMain zvMain = new ZvMain();
			System.out.println("testing XY process query");
			String outputGraphExecutor = zvMain.runQueryGraph(arg);
			System.out.println("XY Output");
			System.out.println(outputGraphExecutor);
			System.out.println(outputGraphExecutor.length());

			assertFalse(outputGraphExecutor.equals(nullOutput));			
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
