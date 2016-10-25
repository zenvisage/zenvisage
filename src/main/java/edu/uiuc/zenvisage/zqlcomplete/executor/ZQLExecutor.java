package edu.uiuc.zenvisage.zqlcomplete.executor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.uiuc.zenvisage.data.Query;
import edu.uiuc.zenvisage.data.roaringdb.db.Database;
import edu.uiuc.zenvisage.data.roaringdb.executor.Executor;
import edu.uiuc.zenvisage.data.roaringdb.executor.ExecutorResult;
import edu.uiuc.zenvisage.service.distance.Distance;
import edu.uiuc.zenvisage.service.distance.Euclidean;
import edu.uiuc.zenvisage.model.*;
import edu.uiuc.zenvisage.service.utility.Normalization;

import edu.uiuc.zenvisage.service.utility.Zscore;

/**
 * @author tarique
 *
 */
public class ZQLExecutor {
	public static Executor executor;

	public static Result execute(ZQLTable zQLTable)
			throws InterruptedException, JsonProcessingException, SQLException {

		// Table to store each individual row result
		ZQLTableResult zQLTableResult = new ZQLTableResult();

		int rowCount = 0;

		// Execute ZQLTable row by row
		for (ZQLRow zQLRow : zQLTable.getZqlRows()) {
			rowCount++;
			ZQLRowResult zqlRowResult = new ZQLRowResult();

			// hash row result with key:name and value:result
			zQLTableResult.getZqlRowResults().put(zQLRow.getName().getName(),
					zqlRowResult);

			// write the resulting data points to result using data in postgres
			// of it
			getSQLVisualisations(zQLRow, zqlRowResult, zQLTableResult);

			// for each row, we execute the process after getting the
			// visualizations and put it in to a variable by executeProcess()
			if (!zQLRow.getProcesse().getMethod().equals(""))
				executeProcess(zQLRow, zqlRowResult, zQLTableResult);

			/*
			// If the last row contains process
			if (zQLRow.getProcesse() != null
					&& rowCount == zQLTable.getZqlRows().size()) {
				zQLRow.getZ().getValues().clear();
				zQLRow.getZ().getValues().add("_" + zQLRow.getName() + "p");
				Name temp = new Name();
				temp.setName("x");
				zQLRow.setName(temp);
				ZQLRowResult zqlRowResultT = new ZQLRowResult();
				zQLTableResult.getZqlRowResults().put(
						zQLRow.getName().getName(), zqlRowResultT);
				getSQLVisualisations(zQLRow, zqlRowResultT, zQLTableResult);
				return executeOutput(zqlRowResultT, zQLTableResult);

			}
			 */
			// return the visualization if it is the last row or this row is a output, skip rest and output if is output
			if (rowCount == zQLTable.getZqlRows().size() || zQLRow.isOutput())
				return executeOutput(zqlRowResult, zQLTableResult);
		}

		return null;
	}

	public static void getVisualisations(ZQLRow zqlRow,
			ZQLRowResult zqlRowResult, ZQLTableResult zQLTableResult)
					throws InterruptedException {
		// I assume X has always one value which will be true for the user
		// study. Need to fix this later.

		// get the first z axis
		String z = zqlRow.getZ().getValues().get(0);

		// retrieve sketch points from query passed in by front-end
		if ("sketch".equals(z)) {
			System.out.println(z + "inside");
			zqlRowResult.setSketchPoints(zqlRow.getSketchPoints());
			zqlRowResult.setSketch(true);
			return;
		}

		// list of visualizations in float:float form
		List<ZQLRowVizResult> zqlRowVizResults = new ArrayList<ZQLRowVizResult>();
		zqlRowResult.setZqlRowVizResults(zqlRowVizResults);

		// list of z values
		List<String> zvalues = new ArrayList<>();

		if (z.startsWith("_")) {
			// get the zvalues from process result
			zvalues.addAll(getZValueFromProcessesAbove(zQLTableResult,
					z.substring(1, z.length() - 1)));
			z = getZTypeFromProcessesAbove(zQLTableResult,
					z.substring(1, z.length() - 1));
		}

		for (String x : zqlRow.getX().getValues())
			for (String y : zqlRow.getY().getValues()) {
				ZQLRowVizResult zQLRowVizResult = new ZQLRowVizResult();
				Query q = new Query("query").setGrouby(z + "," + x)
						.setAggregationFunc("avg").setAggregationVaribale(y);
				setFilter(q, zqlRow.getConstraint());

				// get data points with a in memory query
				LinkedHashMap<String, LinkedHashMap<Float, Float>> output = executor
						.getData(q).output;

				if (zvalues.size() > 0) {
					LinkedHashMap<String, LinkedHashMap<Float, Float>> newoutput = new LinkedHashMap<String, LinkedHashMap<Float, Float>>();
					for (String zvalue : zvalues) {
						LinkedHashMap<Float, Float> results = output
								.get(zvalue);
						newoutput.put(zvalue, results);
					}
					output = newoutput;
				}

				// set visualization data points to row visualization
				zQLRowVizResult.setVizData(output);
				zQLRowVizResult.setX(x);
				zQLRowVizResult.setY(y);
				zQLRowVizResult.setZ(z);
				zqlRowVizResults.add(zQLRowVizResult);
			}
		return;
	}

	public static void getSQLVisualisations(ZQLRow zqlRow,
			ZQLRowResult zqlRowResult, ZQLTableResult zQLTableResult)
					throws InterruptedException, SQLException {
		// I assume X has always one value which will be true for the user
		// study. Need to fix this later.

		// set list of visualizations in float:float form to zqlRowResult
		List<ZQLRowVizResult> zqlRowVizResults = new ArrayList<ZQLRowVizResult>();
		zqlRowResult.setZqlRowVizResults(zqlRowVizResults);

		/*
		 * // list of z values given by the process List<String> zProcessValues=
		 * new ArrayList<>(); if(zColumn.startsWith("_")){ // get the zvalues
		 * from process result System.out.printf("z value before: %s", zColumn);
		 * zColumn = zColumn.substring(1,zColumn.length()-1);
		 * zProcessValues.addAll
		 * (getZValueFromProcessesAbove(zQLTableResult,zColumn));
		 * zColumn=getZTypeFromProcessesAbove(zQLTableResult,zColumn); }
		 */

		// System.out.printf("z value after: %s", zColumn);
		// convert back to normal z value, only works for one z value in ZQL
		// column
		// zqlRow.getZ().getValues().set(0, zColumn);

		// connect to db
		PSQLDatabase zenvisageDB = new PSQLDatabase();

		// get the z axis
		String zColumn = zqlRow.getZ().getAttribute().replace("'","");
		String zVariable = zqlRow.getZ().getVariable();
		List<String> zValues = zqlRow.getZ().getValues();

		
		// check if we need to assign value to variable in z column
		if (zValues.size() > 0 && !zVariable.equals("")) {
			ZQLVariable zTemp = new ZQLVariable();
			zTemp.setName(zColumn); 
			zTemp.setValues(zValues);
			zQLTableResult.setVariable(zVariable, zTemp);
		}
		
		// check if z column is a variable if yes get the values
		if (zColumn.equals("") && !zVariable.equals("") ) {
			System.out.println(zqlRow.getName().getName()+" "+zqlRow.getZ().getVariable());
			zValues = zQLTableResult.getVariable(zqlRow.getZ().getVariable()).getValues();
			zColumn = zQLTableResult.getVariable(zqlRow.getZ().getVariable()).getName();
			zqlRow.getZ().setAttribute(zColumn);
		}
		
		// empty the list to specify all z values
		if (!zValues.isEmpty() && zValues.get(0).equals("*")) {
			zValues.clear();
		}
		
		String xVariable = zqlRow.getX().getVariable();
		String yVariable = zqlRow.getY().getVariable();
		
		List<String> xValues = zqlRow.getX().getValues();
		List<String> yValues = zqlRow.getY().getValues();
		
		if (!xVariable.equals("")) {
			if (xValues.isEmpty()) {
				xValues = zQLTableResult.getVariable(xVariable).getValues();
			}
			else {
				ZQLVariable variableValues = new ZQLVariable();
				variableValues.setValues(xValues);
				zQLTableResult.setVariable(xVariable, variableValues);
			}
		}
	
		if (!yVariable.equals("")) {
			if (yValues.isEmpty()) {
				yValues = zQLTableResult.getVariable(yVariable).getValues();
			}
			else {
				ZQLVariable variableValues = new ZQLVariable();
				variableValues.setValues(yValues);
				zQLTableResult.setVariable(yVariable, variableValues);
			}
		}
		
		// For each x axis and y axis value
		for (String x : xValues) {
			for (String y : yValues) {
				// construct an sql query from zql row
				ZQLRow tempRow = new ZQLRow(zqlRow);
				tempRow.getX().setValues(new ArrayList<String>(Arrays.asList(x)));
				tempRow.getY().setValues(new ArrayList<String>(Arrays.asList(y)));
				SQLQuery query = new SQLQuery();
				query.constructFromZQL(tempRow, zQLTableResult);

				System.out.println(query.toString());

				ResultSet queryResult = zenvisageDB.query(query.toString());
				
				// Initialize a new row result to hold visualization data points
				ZQLRowVizResult zQLRowVizResult = new ZQLRowVizResult();
				LinkedHashMap<String, LinkedHashMap<Float, Float>> output = new LinkedHashMap<String, LinkedHashMap<Float, Float>>();
				
				x = x.replace("'","");
				y = y.replace("'","");

				// format output into interface format
				while (queryResult.next()) {
					String zAxis = queryResult.getString(zColumn
							.toLowerCase().replace("'",""));
					
					// if specific zValues are specific requested
					if (!zValues.isEmpty()) {
						if (!zValues.contains(zAxis)) {
							continue;
						}
					}

					Float yAxis = queryResult.getFloat(y.toLowerCase());
					Float xAxis = queryResult.getFloat(x.toLowerCase());

					if (!output.containsKey(zAxis)) {
						output.put(zAxis, new LinkedHashMap<Float, Float>());
					}
					output.get(zAxis).put(xAxis, yAxis);
				}

				// set visualization x axis y axis z axis and data points to row
				// visualization
				zQLRowVizResult.setVizData(output);
				zQLRowVizResult.setX(x);
				zQLRowVizResult.setY(y);
				zQLRowVizResult.setZ(zColumn);
				zqlRowVizResults.add(zQLRowVizResult);
			}
		}
	}

	private static List<String> getZValueFromProcessesAbove(
			ZQLTableResult zQLTableResult, String rowID) {
		return zQLTableResult.getZqlRowResults().get(rowID)
				.getZqlProcessResult().getzValues();

	}

	private static String getZTypeFromProcessesAbove(
			ZQLTableResult zQLTableResult, String rowID) {
		return zQLTableResult.getZqlRowResults().get(rowID)
				.getZqlProcessResult().getzType();

	}

	public static void executeProcess(ZQLRow zQLRow, ZQLRowResult zqlRowResult,
			ZQLTableResult zQLTableResult) throws JsonProcessingException {
		Processe processe = zQLRow.getProcesse();
		String function = processe.getMethod();
		ZQLAnalysis analysis = null;
		Distance distance = new Euclidean();
		Normalization normalization = new Zscore();
		ZQLPiecewiseAggregation paa;
		Database inMemoryDatabase = executor.getDatabase();
		// reformat database data

		// generate the corresponding analysis method
		if (function.equals("Similar")) {
			paa = new ZQLPiecewiseAggregation(normalization, inMemoryDatabase);
			analysis = new ZQLSimilarity(inMemoryDatabase, distance,
					normalization, paa);
			((ZQLSimilarity) analysis).setDescending(false);
		} else if (function.equals("Dissimilar")) {
			paa = new ZQLPiecewiseAggregation(normalization, inMemoryDatabase);
			analysis = new ZQLSimilarity(inMemoryDatabase, distance,
					normalization, paa);
			((ZQLSimilarity) analysis).setDescending(true);
		} else if (function.equals("IncTrends")) {
			paa = new ZQLPiecewiseAggregation(normalization, inMemoryDatabase);
			analysis = new ZQLIncreasingTrends(inMemoryDatabase, distance,
					normalization, paa);
			((ZQLIncreasingTrends) analysis).setDescending(true);
		} else if (function.equals("DecTrends")) {
			paa = new ZQLPiecewiseAggregation(normalization, inMemoryDatabase);
			analysis = new ZQLIncreasingTrends(inMemoryDatabase, distance,
					normalization, paa);
			((ZQLIncreasingTrends) analysis).setDescending(false);
		} else {
			System.out.println("Wrong Processing Function: " + function);
		}
		zqlRowResult.setZqlProcessResult(analysis.generateAnalysis(zQLRow,
				zQLTableResult));

		/**
		 * Store the process result values into corresponding variable in ZQLRow
		 */
		ZQLVariable processResult = new ZQLVariable();
		processResult.setName(zQLRow.getZ().getAttribute()); //bug here, need to find column name by visualization given
		processResult.setValues(zqlRowResult.getZqlProcessResult().getzValues());
		zQLTableResult.setVariable(zQLRow.getProcesse().getVariables().get(0), processResult);

	}

	public static Result executeOutput(ZQLRowResult zQLRowResult,
			ZQLTableResult zQLTableResult) throws JsonProcessingException {
		return ZQLChartOutput.chartOutput(zQLRowResult);

	}

	private static void setFilter(Query q, List<Constraints> constraints) {
		if (constraints.size() > 0) {
			Constraints constraint = constraints.get(0);
			Query.Filter filter = new Query.FilterPredicate(
					constraint.getKey(),
					Query.FilterOperator.fromString(constraint.getOperator()),
					constraint.getValue());
			q.setFilter(filter);
		}
	}
}
