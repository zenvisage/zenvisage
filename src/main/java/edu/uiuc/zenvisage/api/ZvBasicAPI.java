package edu.uiuc.zenvisage.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.uiuc.zenvisage.model.DynamicClass;
import edu.uiuc.zenvisage.model.ZvQuery;
import edu.uiuc.zenvisage.model.AxisVariables;
import edu.uiuc.zenvisage.service.ZvMain;

@Controller
public class ZvBasicAPI {

	@Autowired
	private ZvMain zvMain;
	public String logFilename="";
	public String querieslogFilename="";
    public ZvBasicAPI(){

	}
    
	@RequestMapping(value = "/fileUpload", method = RequestMethod.POST)
	@ResponseBody
	public void fileUpload(HttpServletRequest request, HttpServletResponse response) {
		zvMain = new ZvMain();
		try {
		logQueries("fileUpload",request,"");
		zvMain.fileUpload(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	@RequestMapping(value = "/createClasses", method = RequestMethod.POST)
	@ResponseBody
	public String createClasses(HttpServletRequest request, HttpServletResponse response) {
		String type="Create Classes";
		StringBuilder stringBuilder = new StringBuilder();
	    zvMain = new ZvMain();
	    try {
		    Scanner scanner = new Scanner(request.getInputStream());
		    while (scanner.hasNextLine()) {
		    	stringBuilder.append(scanner.nextLine());
		    }
		    String body = stringBuilder.toString();
		    zvMain.runCreateClasses(body);
		    scanner.close();
		    return new ObjectMapper().writeValueAsString(body);
	    } catch (Exception e) {
			e.printStackTrace();
			try {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	    }
	    return null;
	}
	
	/*
	 * /zv/getClassInfo
	 * {“dataset”: “real_estate”}
	 */
	@RequestMapping(value = "/getClassInfo", method = RequestMethod.POST)
	@ResponseBody
	public String getClassInfo(HttpServletRequest request, HttpServletResponse response) throws ClassNotFoundException, InterruptedException, IOException, ServletException, SQLException {
		StringBuilder stringBuilder = new StringBuilder();
	    zvMain = new ZvMain();
		try {
		    Scanner scanner = new Scanner(request.getInputStream());
		    while (scanner.hasNextLine()) {
		    	stringBuilder.append(scanner.nextLine());
		    }
		    String body = stringBuilder.toString();
		    logQueries("getClassInfo",request,body);
		    scanner.close();
		    return zvMain.runRetrieveClasses(body);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}
	
	@RequestMapping(value = "/gettablelist", method = RequestMethod.GET)
	@ResponseBody
	public ArrayList<String> gettablelist(HttpServletResponse response) throws JsonGenerationException, JsonMappingException, IOException, InterruptedException, SQLException {
		zvMain = new ZvMain();
//		System.out.println(arg);
		try {
			return zvMain.getTablelist();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}

//    /* Will be obsolete after separated calls*/
//	@RequestMapping(value = "/getdata", method = RequestMethod.GET)
//	@ResponseBody
//	public String getData(@RequestParam(value="query") String arg) throws InterruptedException, IOException {
//		//System.out.println(arg);
//		return zvMain.runDragnDropInterfaceQuery(arg);
//	}

	/* New Separated API calls
	 * Representative
	 * 	-Distance(Euc/DTW)
	 * Outlier
	 * 	-Distance(Euc/DTW)
	 * Similarity
	 *	-Distance(Euc/DTW)
	 *  -Similarity/Dis-similarity (true/false)
	 */
	@RequestMapping(value = "/postRepresentative", method = RequestMethod.POST)
	@ResponseBody
	public String postRepresentative(HttpServletRequest request, HttpServletResponse response) {
	//	logQueries("postRepresentative",request);
		StringBuilder stringBuilder = new StringBuilder();
		zvMain = new ZvMain();
		try {
			Scanner scanner = new Scanner(request.getInputStream());
			while (scanner.hasNextLine()) {
				stringBuilder.append(scanner.nextLine());
			}
			String body = stringBuilder.toString();
			String bodyforlogging=removeSketchPoints(body);
			logQueries("postRepresentative",request,bodyforlogging);
			System.out.println("Representative:"+body);
			scanner.close();
			return zvMain.runDragnDropInterfaceQuerySeparated(body, "RepresentativeTrends");
		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}
	
//	@RequestMapping(value = "/downloadRepresentative", method = RequestMethod.POST)
//	@ResponseBody
//	public String downloadRepresentative(HttpServletRequest request, HttpServletResponse response) throws InterruptedException, IOException, SQLException {
//		StringBuilder stringBuilder = new StringBuilder();
//	    Scanner scanner = new Scanner(request.getInputStream());
//	    while (scanner.hasNextLine()) {
//	        stringBuilder.append(scanner.nextLine());
//	    }
//
//	    String body = stringBuilder.toString();
////	    System.out.println("Representative:"+body);
//		zvMain.runDragnDropInterfaceQuerySeparated(body, "RepresentativeTrends");
//	}

	@RequestMapping(value = "/postOutlier", method = RequestMethod.POST)
	@ResponseBody
	public String postOutlier(HttpServletRequest request, HttpServletResponse response) {
		String type="postOutlier";
	//	logQueries(type,request);
		StringBuilder stringBuilder = new StringBuilder();
	    zvMain = new ZvMain();
		try {
		    Scanner scanner = new Scanner(request.getInputStream());
		    while (scanner.hasNextLine()) {
		        stringBuilder.append(scanner.nextLine());
		    }
		    String body = stringBuilder.toString();
		    String bodyforlogging=removeSketchPoints(body);
		    logQueries(type,request,bodyforlogging);
		    scanner.close();
			return zvMain.runDragnDropInterfaceQuerySeparated(body, "Outlier");
		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}
	@RequestMapping(value = "/downloadSimilarity", method = RequestMethod.POST)
	@ResponseBody
	public String downloadSimilarity(HttpServletRequest request, HttpServletResponse response) {
		String type="downloadSimilarity";
		//		logQueries(type,request);
		StringBuilder stringBuilder = new StringBuilder();
		zvMain = new ZvMain();
		try {
			Scanner scanner = new Scanner(request.getInputStream());
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				stringBuilder.append(line);
				System.out.println(line);
			}
			//String resp = "{\"xval\":\""+zvMain.saveDragnDropInterfaceQuerySeparated(body, "SimilaritySearch")+"\"}";
			//System.out.println("resp: "+resp);
			//JSONObject jsonObj = new JSONObject("{\"phonetype\":\"N95\",\"cat\":\"WP\"}");
			String body = stringBuilder.toString();
			String bodyforlogging=removeSketchPoints(body);
			logQueries(type,request,bodyforlogging);
			String resp = zvMain.saveDragnDropInterfaceQuerySeparated(body, "SimilaritySearch");
			scanner.close();
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}			
		}
		return null;
	}
	
	@RequestMapping(value = "/downloadOutlier", method = RequestMethod.POST)
	@ResponseBody
	public String downloadOutlier(HttpServletRequest request, HttpServletResponse response) {
		String type="downloadOutlier";
		System.out.println("downloadOutlier");
		StringBuilder stringBuilder = new StringBuilder();
	    zvMain = new ZvMain();
		try {
		    Scanner scanner = new Scanner(request.getInputStream());
		    while (scanner.hasNextLine()) {
		    		String line = scanner.nextLine();
		        stringBuilder.append(line);
		        System.out.println(line);
		    }
		    String body = stringBuilder.toString();
		    String bodyforlogging=removeSketchPoints(body);
		    logQueries(type,request,bodyforlogging);
		    scanner.close();
		    return zvMain.saveDragnDropInterfaceQuerySeparated(body, "Outlier");
		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}
	
	@RequestMapping(value = "/postSimilarity_error", method = RequestMethod.POST)
	@ResponseBody
	public String postSimilarity_error(HttpServletRequest request, HttpServletResponse response) {
		String type="postSimilarity_error";
		StringBuilder stringBuilder = new StringBuilder();
		try { 
		    Scanner scanner = new Scanner(request.getInputStream());
		    while (scanner.hasNextLine()) {
		        stringBuilder.append(scanner.nextLine());
		    }
		    String body = stringBuilder.toString();
		    String bodyforlogging=removeSketchPoints(body);
		    logQueries(type,request,bodyforlogging);
		    scanner.close();
		    return zvMain.runDragnDropInterfaceQuerySeparated_error(body, "SimilaritySearch");
		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}			
		}
		return null;
	}
	
	@RequestMapping(value = "/postSimilarity", method = RequestMethod.POST)
	@ResponseBody
	public String postSimilarity(HttpServletRequest request, HttpServletResponse response) {
	    Scanner scanner;
		String type="postSimilarity";
		StringBuilder stringBuilder = new StringBuilder();
	    String bodyforlogging;
		zvMain = new ZvMain();
		try {
			scanner = new Scanner(request.getInputStream());
		    while (scanner.hasNextLine()) {
		        stringBuilder.append(scanner.nextLine());
		    }
			String body = stringBuilder.toString();
			bodyforlogging = removeSketchPoints(body);
		    logQueries(type,request,bodyforlogging);
		    scanner.close();
		    return zvMain.runDragnDropInterfaceQuerySeparated(body, "SimilaritySearch");
		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}
	

	@RequestMapping(value = "/logger", method = RequestMethod.POST)
	@ResponseBody
	public void logger(HttpServletRequest request, HttpServletResponse response) {
		System.out.print("logFilename:");
		System.out.println(logFilename);
		zvMain = new ZvMain();
		try {
			if (logFilename.equals("")){
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH");
				logFilename = "../"+sdf.format(timestamp)+".log";
			}
			File file = new File(logFilename);

			BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
			String log = request.getParameter("timestamp")+","+request.getRemoteAddr()+','+request.getParameter("message")+'\n';
			System.out.println(log);
			writer.write(log);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	 
	
	public void logQueries(String type,HttpServletRequest request,String message) {
		zvMain = new ZvMain();
		System.out.print("QuerieslogFilename:");
		System.out.println(querieslogFilename);
		if (querieslogFilename.equals("")){
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH");
			querieslogFilename = "../"+"Queries-"+sdf.format(timestamp)+".log";
		}		
		File file = new File(querieslogFilename);
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
			String log="";
			if(request!=null) {

				if(request.getParameter("timestamp")==null) {
					Timestamp timestamp = new Timestamp(System.currentTimeMillis());
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	
					log  = sdf.format(timestamp)+","+request.getRemoteAddr()+","+type+','+message+'\n';
				} else {
					log  = request.getParameter("timestamp")+","+request.getRemoteAddr()+','+type+','+message+'\n';
				}
			} else {
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	
				log  = sdf.format(timestamp)+",null,"+type+','+message+'\n';
			}
			System.out.println(log);
			writer.write(log);
			writer.close();
		} catch(Exception e){
			System.out.println("Error while logging: "+e.toString());
		}
	}
	 
	
	
	@RequestMapping(value = "/getDissimilarity", method = RequestMethod.GET)
	@ResponseBody
	public String getDissimilarity(@RequestParam(value="query") String arg, HttpServletResponse response) {
//		System.out.println(arg);
		zvMain = new ZvMain();
		try {
		return zvMain.runDragnDropInterfaceQuerySeparated(arg, "DissimilaritySearch");
		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}

	@RequestMapping(value = "/getformdata", method = RequestMethod.GET)
	@ResponseBody
	public String getformdata(@RequestParam(value="query") String arg, HttpServletResponse response) {
//		System.out.println(arg);
		zvMain = new ZvMain();
		try {
		return zvMain.getInterfaceFomData(arg);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}

	public String removeSketchPoints(String body) throws Exception {
		String bodyforlogging = "";
		try {
			ZvQuery args = new ObjectMapper().readValue(body, ZvQuery.class);
			args.dataX=null;
			args.dataY=null;
			args.sketchPoints=null;
			bodyforlogging=new ObjectMapper().writeValueAsString(args);
		} catch (JsonParseException e) {
			throw new Exception("Json Parsing Exception");
		} catch (JsonMappingException e) {
			throw new Exception("Json Mapping Exception");
		} catch (JsonGenerationException e) {
			throw new Exception("Json Generation Exception");
		}catch (IOException e) {
			throw new Exception("IOException");
		}
	    return bodyforlogging;
	}
	
//	@RequestMapping(value = "/getBaselineData", method = RequestMethod.GET)
//	@ResponseBody
//	public String getBaselineData(@RequestParam(value="query")String arg) throws JsonParseException, JsonMappingException, IOException, InterruptedException {
//		return zvMain.getBaselineData(arg);
//	}
//
//	@RequestMapping(value = "/getscatterplot", method = RequestMethod.GET)
//	@ResponseBody
//	public String getscatterplot(@RequestParam(value="query") String arg) throws JsonParseException, JsonMappingException, IOException {
//		return zvMain.getScatterPlot(arg);
//	}

	@RequestMapping(value = "/executeZQL", method = RequestMethod.GET)
	@ResponseBody
	public String executeZQL(@RequestParam(value="query") String arg) throws IOException, InterruptedException {
		//return zvMain.runZQLQuery(arg);
		return "";
	}

	@RequestMapping(value = "/executeZQLComplete", method = RequestMethod.GET)
	@ResponseBody
	public String executeZQLComplete(@RequestParam(value="query")  String arg, HttpServletResponse response) {
		// for testing my query graph executor with zql.html
		// String outputExecutor = zvMain.runZQLCompleteQuery(arg);
		zvMain = new ZvMain();
		try {
		String outputGraphExecutor = zvMain.runQueryGraph(arg);
		logQueries("ZQL",null,arg);
		return outputGraphExecutor;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}
	
	@RequestMapping(value = "/selectXYZ", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<AxisVariables> executeSelectXYZ(@RequestBody AxisVariables axisVariables) throws SQLException, IOException {
		zvMain = new ZvMain();
		if( axisVariables != null) {
	      zvMain.insertZenvisageMetatable(axisVariables);
          System.out.println(axisVariables.toString());
        } else {
          System.out.println("axisVariables are null");
        }
		return null;
	}

	@RequestMapping(value = "/executeScatter", method = RequestMethod.GET)
	@ResponseBody
	public String executeScatter(@RequestParam(value="query")  String arg, HttpServletResponse response) {
		zvMain = new ZvMain();
		try {
			String outputGraphExecutor = zvMain.runScatterQueryGraph(arg);
			logQueries("ZQL-Scatter",null,arg);
			return outputGraphExecutor;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	@ResponseBody
	public String test(@RequestParam(value="query") String arg) {
		 logQueries("test",null,arg);
			// TODO change to graph executor
		return "Test successful:" + arg;
	}
	
//	@RequestMapping(value = "/verifyPassword", method = RequestMethod.GET)
//	@ResponseBody
//	public String verifyPassword(@RequestParam(value="query") String arg) throws IOException {
//		System.out.println("arg:");
//		System.out.println(arg);
//		// Creates a FileReader Object
//		System.out.println("verifyPassword");
//	    FileReader fr = new FileReader("../secret.txt"); 
//	    char [] a = new char[50];
//	    fr.read(a);   // reads the content to the array
//	    for(char c : a)
//	       System.out.print(c);   // prints the characters one by one
//	    fr.close();
//		return "Test successful:" + arg;
//	}
	

}
