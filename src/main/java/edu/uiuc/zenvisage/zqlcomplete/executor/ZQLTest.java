/**
 * 
 */
package org.vde.zql;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import visual.Result;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import api.Args;
import api.VDE;

/**
 * @author tarique
 *
 */
public class ZQLTest {
	
	
	//similarity search
	public static ZQLTable createZQLTable(){
		ZQLTable zQLTable = new ZQLTable();
		
		//create Row 1
		// Year, SoldPrice, city, City="Chicago"
		ZQLRow zqlRow = new ZQLRow();
		zqlRow.setVariables("1");
		zqlRow.getX().add("Year");
		zqlRow.getY().add("SoldPrice");
		zqlRow.getZ().add("City");
		Constraints constraint = new Constraints();
		constraint.setKey("City");
		constraint.setOperator("=");
		constraint.setValue("Chicago");
		zqlRow.getConstraint().add(constraint);
		zQLTable.getZqlRows().add(zqlRow);
				
		//Add Row 2
		// Year, SoldPrice, city, City="Chicago"
		ZQLRow zqlRow1 = new ZQLRow();
		zqlRow1.setVariables("2");
		zqlRow1.getX().add("Year");
		zqlRow1.getY().add("SoldPrice");
		zqlRow1.getZ().add("City");
	    Processe processe = new Processe();
	    processe.setVariables("2p");
	    processe.setFunction("similar");
	    processe.getParameters().put("viz1","1");
	    processe.getParameters().put("viz2","2");
	    zqlRow1.setProcesse(processe);
	    zQLTable.getZqlRows().add(zqlRow1);
	    
	    //Add Row 3
		ZQLRow zqlRow2 = new ZQLRow();
		zqlRow2.setVariables("3");
		zqlRow2.getX().add("Year");
		zqlRow2.getY().add("SoldPrice");
		zqlRow2.getZ().add("_2p");
	    zqlRow2.setOutput(true);
	    zQLTable.getZqlRows().add(zqlRow2);
	    
	    return zQLTable;
	}
	
	//dissimilarity search
	public static ZQLTable createZQLTable1(){
		ZQLTable zQLTable = new ZQLTable();
		
		//create Row 1
		// Year, SoldPrice, city, City="Chicago"
		ZQLRow zqlRow = new ZQLRow();
		zqlRow.setVariables("1");
		zqlRow.getX().add("Year");
		zqlRow.getY().add("SoldPrice");
		zqlRow.getZ().add("City");
		Constraints constraint = new Constraints();
		constraint.setKey("City");
		constraint.setOperator("=");
		constraint.setValue("Chicago");
		zqlRow.getConstraint().add(constraint);
		zQLTable.getZqlRows().add(zqlRow);
				
		//Add Row 2
		// Year, SoldPrice, city, City="Chicago"
		ZQLRow zqlRow1 = new ZQLRow();
		zqlRow1.setVariables("2");
		zqlRow1.getX().add("Year");
		zqlRow1.getY().add("SoldPrice");
		zqlRow1.getZ().add("City");
	    Processe processe = new Processe();
	    processe.setVariables("2p");
	    processe.setFunction("dissimilar");
	    processe.getParameters().put("viz1","1");
	    processe.getParameters().put("viz2","2");
	    zqlRow1.setProcesse(processe);
	    zQLTable.getZqlRows().add(zqlRow1);
	    
	    //Add Row 3
		ZQLRow zqlRow2 = new ZQLRow();
		zqlRow2.setVariables("3");
		zqlRow2.getX().add("Year");
		zqlRow2.getY().add("SoldPrice");
		zqlRow2.getZ().add("_2p");
	    zqlRow2.setOutput(true);
	    zQLTable.getZqlRows().add(zqlRow2);
	    
	    return zQLTable;
	}
	
	//increasingTrend
	public static ZQLTable createZQLTable2(){
		ZQLTable zQLTable = new ZQLTable();

		//Add Row 1
		// Year, SoldPrice, city, City="Chicago"
		ZQLRow zqlRow1 = new ZQLRow();
		zqlRow1.setVariables("1");
		zqlRow1.getX().add("Year");
		zqlRow1.getY().add("SoldPrice");
		zqlRow1.getZ().add("City");
	    Processe processe = new Processe();
	    processe.setVariables("1p");
	    processe.setFunction("DecTrends");
	    processe.getParameters().put("viz1","1");
	    zqlRow1.setProcesse(processe);
	    zQLTable.getZqlRows().add(zqlRow1);
	    
	    //Add Row 3
		ZQLRow zqlRow2 = new ZQLRow();
		zqlRow2.setVariables("2");
		zqlRow2.getX().add("Year");
		zqlRow2.getY().add("SoldPrice");
		zqlRow2.getZ().add("_1p");
	    zqlRow2.setOutput(true);
	    zQLTable.getZqlRows().add(zqlRow2);
	    
	    return zQLTable;
	}
		
	public static String createZQLTable3(){
		String json="{\"zqlRows\":[{\"name\":0,\"x\":[\"Year\"],\"y\":[\"SoldPrice\"],\"z\":[\"City\"],\"constraints\":[],\"processe\":null,\"output\":false,\"sketchPoints\":null},{\"name\":1,\"x\":[\"Year\"],\"y\":[\"ListingPrice\"],\"z\":[\"City\"],\"constraints\":[],\"processe\":{\"name\":\"1p\",\"function\":\"similar\",\"parameters\":{\"viz2\":\"1\",\"viz1\":\"0\"}},\"output\":false,\"sketchPoints\":null},{\"name\":2,\"x\":[\"Year\"],\"y\":[\"SoldPrice\",\"ListingPrice\"],\"z\":[\"_1p\"],\"constraints\":[],\"processe\":null,\"output\":false,\"sketchPoints\":null}]}";
        return json; 	
	}
	
	public static String createZQLTable4(){
		String json="{\"zqlRows\":[{\"name\":0,\"x\":[\"Year\"],\"y\":[\"SoldPrice\"],\"z\":[\"sketch\"],\"constraints\":[],\"processe\":null,\"output\":false,\"sketchPoints\":{\"points\":[{\"x\":17,\"y\":-286},{\"x\":17,\"y\":-285},{\"x\":18,\"y\":-285},{\"x\":19,\"y\":-283},{\"x\":20,\"y\":-279},{\"x\":22,\"y\":-277},{\"x\":26,\"y\":-273},{\"x\":30,\"y\":-267},{\"x\":33,\"y\":-260},{\"x\":41,\"y\":-253},{\"x\":58,\"y\":-236},{\"x\":80,\"y\":-216},{\"x\":108,\"y\":-196},{\"x\":137,\"y\":-174},{\"x\":173,\"y\":-152},{\"x\":213,\"y\":-128},{\"x\":256,\"y\":-104},{\"x\":300,\"y\":-78},{\"x\":349,\"y\":-54},{\"x\":400,\"y\":-27},{\"x\":454,\"y\":-4},{\"x\":508,\"y\":17},{\"x\":568,\"y\":40},{\"x\":618,\"y\":55},{\"x\":673,\"y\":71},{\"x\":716,\"y\":80},{\"x\":763,\"y\":90},{\"x\":798,\"y\":98},{\"x\":830,\"y\":102},{\"x\":853,\"y\":102},{\"x\":873,\"y\":104},{\"x\":890,\"y\":104},{\"x\":902,\"y\":104},{\"x\":911,\"y\":104},{\"x\":913,\"y\":104},{\"x\":916,\"y\":105},{\"x\":917,\"y\":105},{\"x\":918,\"y\":105},{\"x\":919,\"y\":105},{\"x\":919,\"y\":106},{\"x\":921,\"y\":106},{\"x\":923,\"y\":107},{\"x\":927,\"y\":108},{\"x\":930,\"y\":108},{\"x\":934,\"y\":110},{\"x\":939,\"y\":111},{\"x\":943,\"y\":112},{\"x\":949,\"y\":112},{\"x\":953,\"y\":113},{\"x\":959,\"y\":114},{\"x\":963,\"y\":114},{\"x\":968,\"y\":115},{\"x\":972,\"y\":115},{\"x\":974,\"y\":116},{\"x\":978,\"y\":116},{\"x\":980,\"y\":117},{\"x\":983,\"y\":117},{\"x\":987,\"y\":118},{\"x\":991,\"y\":118},{\"x\":994,\"y\":118},{\"x\":999,\"y\":120},{\"x\":1003,\"y\":121},{\"x\":1008,\"y\":121},{\"x\":1012,\"y\":122},{\"x\":1017,\"y\":123},{\"x\":1021,\"y\":124},{\"x\":1023,\"y\":124},{\"x\":1027,\"y\":125},{\"x\":1030,\"y\":126},{\"x\":1032,\"y\":127},{\"x\":1034,\"y\":127},{\"x\":1037,\"y\":128},{\"x\":1039,\"y\":130},{\"x\":1041,\"y\":130},{\"x\":1042,\"y\":131},{\"x\":1044,\"y\":132},{\"x\":1046,\"y\":133},{\"x\":1047,\"y\":134},{\"x\":1049,\"y\":135},{\"x\":1051,\"y\":137},{\"x\":1053,\"y\":140},{\"x\":1057,\"y\":142},{\"x\":1060,\"y\":144},{\"x\":1063,\"y\":147},{\"x\":1067,\"y\":151},{\"x\":807,\"y\":83},{\"x\":807,\"y\":84},{\"x\":807,\"y\":85},{\"x\":807,\"y\":86}],\"minX\":0,\"maxX\":620,\"minY\":0,\"maxY\":310,\"yAxis\":\"SoldPrice\",\"xAxis\":\"Year\",\"groupBy\":null,\"aggrFunc\":\"avg\",\"aggrVar\":\"SoldPrice\"}},{\"name\":1,\"x\":[\"Year\"],\"y\":[\"SoldPrice\"],\"z\":[\"State\"],\"constraints\":[],\"processe\":{\"name\":\"1p\",\"function\":\"similar\",\"parameters\":{\"viz2\":\"1\",\"viz1\":\"0\"}},\"output\":false},{\"name\":2,\"x\":[\"Year\"],\"y\":[\"SoldPrice\"],\"z\":[\"_1p\"],\"constraints\":[],\"processe\":null,\"output\":false}]}";
	  return json; 	
	}

	public static String createZQLTable5(){
		String json="{\"zqlRows\":[{\"name\":0,\"x\":[\"Month\"],\"y\":[\"SoldPrice\"],\"z\":[\"State\"],\"constraints\":[{\"key\":\"State\",\"operator\":\"=\",\"value\":\"CA\"}],\"output\":false}]}";

		return json; 	
	}

	public static String createZQLTable5Solution(){
		String output="{\"outputCharts\":[{\"xType\":\"1 : CA\",\"yType\":\"avg(SoldPrice)\",\"zType\":null,\"title\":null,\"xData\":[\"3.0\",\"6.0\",\"10.0\",\"14.0\",\"17.0\",\"21.0\",\"25.0\",\"28.0\",\"32.0\",\"36.0\",\"40.0\",\"43.0\",\"47.0\",\"51.0\",\"54.0\",\"58.0\",\"62.0\",\"65.0\",\"69.0\",\"73.0\",\"77.0\",\"80.0\",\"84.0\",\"88.0\",\"91.0\",\"95.0\",\"99.0\",\"102.0\",\"106.0\",\"110.0\",\"114.0\",\"117.0\",\"121.0\",\"125.0\",\"128.0\",\"132.0\",\"136.0\"],\"yData\":[\"593117.6\",\"1301589.8\",\"1822540.8\",\"1888528.1\",\"1508764.2\",\"2141953.2\",\"2179359.0\",\"1645660.0\",\"2236552.8\",\"2191130.8\",\"2164179.0\",\"1652188.1\",\"2123088.2\",\"2007873.5\",\"1449039.5\",\"1837615.8\",\"1669025.5\",\"1181192.1\",\"1583608.9\",\"1550639.5\",\"1543247.2\",\"1183085.9\",\"1536109.6\",\"1483098.0\",\"1141526.5\",\"1484360.5\",\"1424821.5\",\"1123449.8\",\"1547527.6\",\"1584814.5\",\"1735316.4\",\"1379377.8\",\"1869513.5\",\"1943356.2\",\"1511447.5\",\"2003257.0\",\"2028383.9\"],\"count\":0}],\"method\":null,\"xUnit\":null,\"yUnit\":null,\"totalPage\":0}";
		return output; 	
	}

	
	
	public static void main(String[] args) throws IOException, InterruptedException, SQLException {

		
		/**
		 * 
		 * Name		X			Y				Z				Process
		 * f1		'month'		'soldprice'		v1<-'state'.*	v2<–argany_v1[t>0]T(f1)
		 * *f2		'month'		'soldprice'		v2
		 * 
		 */
		
		/*
		ZQLTable table1 = new ZQLTable();
		
		List<ZQLRow> zqlRows = new ArrayList<ZQLRow>();
		
		ZQLRow row1 = new ZQLRow();
		row1.setName("f1");
		row1.setX(new ArrayList<String>(Arrays.asList("month")));
		row1.setY(new ArrayList<String>(Arrays.asList("soldprice")));
		row1.setZ(new ArrayList<String>(Arrays.asList("state")));
		row1.addZVariable(new ZQLVariable("v1", "'state'.*"));
		row1.addZVariable(new ZQLVariable("v2", "argany_v1[t>0]T(f1)"));
		zqlRows.add(row1);
		
		ZQLRow row2 = new ZQLRow();
		row2.setName("*f2");
		row2.setX(new ArrayList<String>(Arrays.asList("month")));
		row2.setY(new ArrayList<String>(Arrays.asList("soldprice")));
		row2.setZ(new ArrayList<String>(Arrays.asList("state")));
		row2.addZVariable(new ZQLVariable("v2", ""));
		zqlRows.add(row2);
		
		table1.setZqlRows(zqlRows);
		*/
		
		
		ZQLTable testObject = new ZQLTable();
		
		List<ZQLRow> zqlRows = new ArrayList<ZQLRow>();
		zqlRows.add(new ZQLRow());
		testObject.setZqlRows(zqlRows);
		
		String testString = "{\"zqlRows\":[{\"name\":\"test\"}]}";
		String jsonInString = new ObjectMapper().writeValueAsString(testObject);
		ZQLTable resultObject = new ObjectMapper().readValue(testString, ZQLTable.class);
		System.out.println(jsonInString);
		System.out.println(resultObject.toString());
		
/*
		VDE vde = new VDE();
		vde.loadOutlierData();
		
	//	ZQLTable zQLTable = createZQLTable();
//		ZQLTable zQLTable = createZQLTable2();
//		String json=new ObjectMapper().writeValueAsString(zQLTable);
//		System.out.println("input:"+json);
//		String output=vde.runZQLQuery(json);
//		String output=vde.runZQLQuery(json);
		
		String output=vde.runZQLQuery(createZQLTable5());
		if (output.equals(createZQLTable5Solution())) {
			System.out.println("Answer matched:");
		}
		else {
			System.out.println("Answer does not match");
		}
		System.out.println(createZQLTable5Solution());
		System.out.println(output);
*/
		
	}
	

}
