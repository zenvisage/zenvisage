package edu.uiuc.zenvisage.similaritysearch;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import edu.uiuc.zenvisage.data.Query;
import edu.uiuc.zenvisage.data.remotedb.SQLQueryExecutor;
import edu.uiuc.zenvisage.model.ZvQuery;

public class ZQLtoSQLTest {
	String query = "{\"xAxis\":\"month\",\"yAxis\":\"soldpricepersqft\",\"groupBy\":\"city\",\"aggrFunc\":\"avg\",\"aggrVar\":\"soldpricepersqft\"}";
	ZvQuery args;
	Query q;
	SQLQueryExecutor sqlQueryExecutor;
	
	@Before
	public void init() throws JsonParseException, JsonMappingException, IOException {
		args = new ObjectMapper().readValue(query,ZvQuery.class);
		q = new Query("query").setGrouby(args.groupBy+","+args.xAxis).setAggregationFunc(args.aggrFunc).setAggregationVaribale(args.aggrVar);
		sqlQueryExecutor = new SQLQueryExecutor();
	}
	
	@Test
	public void testZQLParser() throws SQLException {		
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    PrintStream ps = new PrintStream(baos);
	    PrintStream old = System.out;
	    System.setOut(ps);
	    
		sqlQueryExecutor.ZQLQueryEnhanced(q.getZQLRow(), "real_estate");
		
	    System.out.flush();
	    System.setOut(old);
	    String info = baos.toString();
	    int index = info.indexOf("Running ZQL Query :");
	    String res = info.substring(index + "Running ZQL Query :".length());
	    res = res.split("\n")[0];
	    String truth = "SELECT city,month,avg(soldpricepersqft) FROM real_estate GROUP BY city,month ORDER BY month";
	    assertTrue(res.equals(truth));
	}
	
}







