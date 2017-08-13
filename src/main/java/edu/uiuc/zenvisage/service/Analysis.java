
package edu.uiuc.zenvisage.service;

import java.io.IOException;
import java.util.LinkedHashMap;

import edu.uiuc.zenvisage.service.utility.*;

import edu.uiuc.zenvisage.data.roaringdb.db.Database;
import edu.uiuc.zenvisage.data.roaringdb.executor.Executor;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.uiuc.zenvisage.service.distance.*;
import edu.uiuc.zenvisage.model.*;
import edu.uiuc.zenvisage.model.ZvQuery;

/**
 * Super class of all data analysis subclasses.
 */
public abstract class Analysis {
	/** 
	 * Variables of analysis class
	 */
	public ChartOutputUtil chartOutput;
	public Distance distance;
	public Normalization normalization;
	public ZvQuery args;
	public String downloadData;

	/**
	 * @param executor
	 * @param chartOutput
	 * @param distance
	 * @param normalization
	 */
	public Analysis(ChartOutputUtil chartOutput, Distance distance,
			Normalization normalization, ZvQuery args) {
		this.chartOutput = chartOutput;
		this.distance = distance;
		this.normalization = normalization;
		this.args = args;
		this.downloadData="";
	}

	/**
	 * General method for getting analysis data.
	 * @param output TODO
	 * @param normalizedgroups TODO
	 * @throws JsonProcessingException 
	 */
	public abstract void compute(LinkedHashMap<String, LinkedHashMap<Float, Float>> output, double[][] normalizedgroups, ZvQuery args) throws JsonProcessingException, java.io.IOException;

	/**
	 * @return the chartOutput
	 */
	public ChartOutputUtil getChartOutput() {
		return chartOutput;
	}

	/**
	 * @param chartOutput the chartOutput to set
	 */
	public void setChartOutput(ChartOutputUtil chartOutput) {
		this.chartOutput = chartOutput;
	}

	/**
	 * @return the distance
	 */
	public Distance getDistance() {
		return distance;
	}

	/**
	 * @param distance the distance to set
	 */
	public void setDistance(Distance distance) {
		this.distance = distance;
	}

	/**
	 * @return the normalization
	 */
	public Normalization getNormalization() {
		return normalization;
	}

	/**
	 * @param normalization the normalization to set
	 */
	public void setNormalization(Normalization normalization) {
		this.normalization = normalization;
	}

	public void download(LinkedHashMap<String, LinkedHashMap<Float, Float>> output, double[][] normalizedgroups,
			ZvQuery args) throws JsonProcessingException, IOException {
		// TODO Auto-generated method stub
	}
}
