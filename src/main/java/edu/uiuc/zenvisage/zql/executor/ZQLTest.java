/**
 * 
 */
package edu.uiuc.zenvisage.zql.executor;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonProcessingException;

import edu.uiuc.zenvisage.data.roaringdb.db.Database;
import edu.uiuc.zenvisage.data.roaringdb.executor.Executor;
import edu.uiuc.zenvisage.service.distance.Distance;
import edu.uiuc.zenvisage.model.*;
import edu.uiuc.zenvisage.service.utility.Normalization;


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
		zqlRow.setName("1");
		zqlRow.getX().add("Year");
		zqlRow.getY().add("SoldPrice");
		zqlRow.getZ().add("City");
		Constraints constraint = new Constraints();
		constraint.setKey("City");
		constraint.setOperator("=");
		constraint.setValue("Chicago");
		zqlRow.getConstraints().add(constraint);
		zQLTable.getZqlRows().add(zqlRow);
				
		//Add Row 2
		// Year, SoldPrice, city, City="Chicago"
		ZQLRow zqlRow1 = new ZQLRow();
		zqlRow1.setName("2");
		zqlRow1.getX().add("Year");
		zqlRow1.getY().add("SoldPrice");
		zqlRow1.getZ().add("City");
	    Processe processe = new Processe();
	    processe.setName("2p");
	    processe.setFunction("similar");
	    processe.getParameters().put("viz1","1");
	    processe.getParameters().put("viz2","2");
	    zqlRow1.setProcesse(processe);
	    zQLTable.getZqlRows().add(zqlRow1);
	    
	    //Add Row 3
		ZQLRow zqlRow2 = new ZQLRow();
		zqlRow2.setName("3");
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
		zqlRow.setName("1");
		zqlRow.getX().add("Year");
		zqlRow.getY().add("SoldPrice");
		zqlRow.getZ().add("City");
		Constraints constraint = new Constraints();
		constraint.setKey("City");
		constraint.setOperator("=");
		constraint.setValue("Chicago");
		zqlRow.getConstraints().add(constraint);
		zQLTable.getZqlRows().add(zqlRow);
				
		//Add Row 2
		// Year, SoldPrice, city, City="Chicago"
		ZQLRow zqlRow1 = new ZQLRow();
		zqlRow1.setName("2");
		zqlRow1.getX().add("Year");
		zqlRow1.getY().add("SoldPrice");
		zqlRow1.getZ().add("City");
	    Processe processe = new Processe();
	    processe.setName("2p");
	    processe.setFunction("dissimilar");
	    processe.getParameters().put("viz1","1");
	    processe.getParameters().put("viz2","2");
	    zqlRow1.setProcesse(processe);
	    zQLTable.getZqlRows().add(zqlRow1);
	    
	    //Add Row 3
		ZQLRow zqlRow2 = new ZQLRow();
		zqlRow2.setName("3");
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
		zqlRow1.setName("1");
		zqlRow1.getX().add("Year");
		zqlRow1.getY().add("SoldPrice");
		zqlRow1.getZ().add("City");
	    Processe processe = new Processe();
	    processe.setName("1p");
	    processe.setFunction("DecTrends");
	    processe.getParameters().put("viz1","1");
	    zqlRow1.setProcesse(processe);
	    zQLTable.getZqlRows().add(zqlRow1);
	    
	    //Add Row 3
		ZQLRow zqlRow2 = new ZQLRow();
		zqlRow2.setName("2");
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
	
	
/*		
	public static void main(String[] args) throws IOException, InterruptedException {
		VDE vde = new VDE();
		vde.loadOutlierData();
		
	//	ZQLTable zQLTable = createZQLTable();
//		ZQLTable zQLTable = createZQLTable2();
//		String json=new ObjectMapper().writeValueAsString(zQLTable);
//		System.out.println("input:"+json);
//		String output=vde.runZQLQuery(json);
//		String output=vde.runZQLQuery(json);
		
		String output=vde.runZQLQuery(createZQLTable4());
		System.out.println(output);
			
		
	}
	*/

}
