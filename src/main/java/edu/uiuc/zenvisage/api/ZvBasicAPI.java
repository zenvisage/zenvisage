package edu.uiuc.zenvisage.api;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.core.JsonParseException;

import edu.uiuc.zenvisage.service.ZvMain;
//import edu.uiuc.zenvisage.service.utility.UploadHandleServlet;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLExecutor;

@Controller
public class ZvBasicAPI {

	@Autowired
	private ZvMain zvMain;
	
   
	
    public ZvBasicAPI(){
    	
	}
    
	@RequestMapping(value = "/fileUpload", method = RequestMethod.POST)
	@ResponseBody
	public void fileUpload(HttpServletRequest request, HttpServletResponse response) throws ClassNotFoundException, InterruptedException, IOException, ServletException {
		zvMain.fileUpload(request, response);
	}
		
		
    /* Will be obsolete after separated calls*/
	@RequestMapping(value = "/getdata", method = RequestMethod.GET)
	@ResponseBody
	public String getData(@RequestParam(value="query") String arg) throws InterruptedException, IOException {
		//System.out.println(arg);
		return zvMain.runDragnDropInterfaceQuery(arg);
	}
	
	/* New Separated API calls
	 * Representative
	 * 	-Distance(Euc/DTW)
	 * Outlier
	 * 	-Distance(Euc/DTW)
	 * Similarity
	 *	-Distance(Euc/DTW)
	 *  -Similarity/Dis-similarity (true/false)
	 */
	@RequestMapping(value = "/getRepresentative", method = RequestMethod.GET)
	@ResponseBody
	public String getRepresentative(@RequestParam(value="query") String arg) throws InterruptedException, IOException {
		return zvMain.runDragnDropInterfaceQuerySeparated(arg, "RepresentativeTrends");
	}
	
	@RequestMapping(value = "/getOutlier", method = RequestMethod.GET)
	@ResponseBody
	public String getOutlier(@RequestParam(value="query") String arg) throws InterruptedException, IOException {
		return zvMain.runDragnDropInterfaceQuerySeparated(arg, "Outlier");
	}
	
	@RequestMapping(value = "/getSimilarity", method = RequestMethod.GET)
	@ResponseBody
	public String getSimilarity(@RequestParam(value="query") String arg) throws InterruptedException, IOException {
		System.out.println(arg);
		return zvMain.runDragnDropInterfaceQuerySeparated(arg, "SimilaritySearch");
	}
	
	@RequestMapping(value = "/getDissimilarity", method = RequestMethod.GET)
	@ResponseBody
	public String getDissimilarity(@RequestParam(value="query") String arg) throws InterruptedException, IOException {
		System.out.println(arg);
		return zvMain.runDragnDropInterfaceQuerySeparated(arg, "DissimilaritySearch");
	}

	@RequestMapping(value = "/getformdata", method = RequestMethod.GET)
	@ResponseBody
	public String getformdata( @RequestParam(value="query") String arg) throws JsonGenerationException, JsonMappingException, IOException {
		System.out.println(arg);
		return zvMain.getInterfaceFomData(arg);
	}

	@RequestMapping(value = "/getBaselineData", method = RequestMethod.GET)
	@ResponseBody
	public String getBaselineData(@RequestParam(value="query")String arg) throws JsonParseException, JsonMappingException, IOException, InterruptedException {
		return zvMain.getBaselineData(arg);
	}

	@RequestMapping(value = "/getscatterplot", method = RequestMethod.GET)
	@ResponseBody
	public String getscatterplot(@RequestParam(value="query") String arg) throws JsonParseException, JsonMappingException, IOException {
		return zvMain.getScatterPlot(arg);
	}

	@RequestMapping(value = "/executeZQL", method = RequestMethod.GET)
	@ResponseBody
	public String executeZQL(@RequestParam(value="query") String arg) throws IOException, InterruptedException {
		return zvMain.runZQLQuery(arg);
	}

	@RequestMapping(value = "/executeZQLComplete", method = RequestMethod.GET)
	@ResponseBody
	public String executeZQLComplete(@RequestParam(value="query")  String arg) throws IOException, InterruptedException, SQLException {
		return zvMain.runZQLCompleteQuery(arg);
	}


	@RequestMapping(value = "/test", method = RequestMethod.GET)
	@ResponseBody
	public String test(@RequestParam(value="query") String arg) {
		return "Test successful:" + arg;
	}


}
