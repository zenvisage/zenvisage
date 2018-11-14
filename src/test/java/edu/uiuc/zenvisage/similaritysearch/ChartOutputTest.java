package edu.uiuc.zenvisage.similaritysearch;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Test;

import edu.uiuc.zenvisage.api.CustomException;
import edu.uiuc.zenvisage.model.Chart;
import edu.uiuc.zenvisage.model.Result;
import edu.uiuc.zenvisage.service.ZvMain;

public class ChartOutputTest {
	String query = "{\"method\":\"SimilaritySearch\",\"databasename\":\"real_estate\",\"xAxis\":\"quarter\",\"yAxis\":\"soldpricepersqft\",\"groupBy\":\"city\",\"aggrFunc\":\"avg\",\"aggrVar\":\"soldpricepersqft\",\"outlierCount\":5,\"dataX\":[1,1.11,1.22,1.33,1.44],\"dataY\":[0,252,512,754.33929,1005.78572],\"yMax\":null,\"yMin\":null,\"error\":null,\"sketchPoints\":[{\"points\":[{\"xval\":1,\"yval\":0},{\"xval\":1.11,\"yval\":512},{\"xval\":1.22,\"yval\":254},{\"xval\":1.33,\"yval\":712},{\"xval\":1.44,\"yval\":866}],\"minX\":1,\"maxX\":1.44,\"minY\":0,\"maxY\":754.33929,\"yAxis\":\"soldpricepersqft\",\"xAxis\":\"month\",\"groupBy\":\"city\",\"aggrFunc\":\"avg\",\"aggrVar\":\"soldpricepersqft\"}],\"distanceNormalized\":\"linear\",\"outputNormalized\":true,\"clustering\":\"KMeans\",\"kmeansClusterSize\":3,\"distance_metric\":\"Euclidean\",\"predicateOperator\":\"\",\"predicateColumn\":\"\",\"predicateValue\":\"\",\"filter\":\"\",\"xRange\":[1,1.44],\"considerRange\":true,\"smoothingType\":\"none\",\"smoothingcoefficient\":0.5,\"download\":false,\"includeQuery\":false,\"yOnly\":false,\"downloadAll\":false,\"downloadThresh\":\"\",\"minDisplayThresh\":0}";
	@Test
	public void testNormDist() throws InterruptedException, IOException, SQLException, CustomException {
		ZvMain main = new ZvMain();
        Result result;
		result = main.runDragnDropInterfaceQuery(query,"SimilaritySearch");
		for(Chart chart : result.getOutputCharts()) {
			if(chart.getNormalizedDistance() > 1 || chart.getNormalizedDistance() < 0) {
				break;
			}
		}
	}
	
}
