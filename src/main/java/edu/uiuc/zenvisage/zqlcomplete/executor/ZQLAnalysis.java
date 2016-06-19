/**
 * 
 */
package edu.uiuc.zenvisage.zqlcomplete.executor;

import edu.uiuc.zenvisage.service.utility.Normalization;
import com.fasterxml.jackson.core.JsonProcessingException;

import edu.uiuc.zenvisage.data.roaringdb.db.Database;
import edu.uiuc.zenvisage.data.roaringdb.executor.Executor;
import edu.uiuc.zenvisage.service.distance.Distance;
import edu.uiuc.zenvisage.model.*;

/**
 * Super class of all data analysis subclasses.
 */
public abstract class ZQLAnalysis {
	/** 
	 * Variables of analysis class
	 */
	public Executor executor;
	public Database inMemoryDatabase;
	public ChartOutputUtil chartOutput;
	public Distance distance;
	public Normalization normalization;

	/**
	 * @param executor
	 * @param inMemoryDatabase
	 * @param chartOutput
	 * @param distance
	 * @param normalization
	 */
	public ZQLAnalysis(Database inMemoryDatabase, Distance distance,
			Normalization normalization) {
		this.inMemoryDatabase = inMemoryDatabase;
		this.distance = distance;
		this.normalization = normalization;
	}

	/**
	 * General method for getting analysis data.
	 * @param output TODO
	 * @param normalizedgroups TODO
	 * @throws JsonProcessingException 
	 */
	public abstract ZQLRowProcessResult generateAnalysis(ZQLRow zqlRow,ZQLTableResult zqlTableResult) throws JsonProcessingException;

	/**
	 * @return the executor
	 */
	public Executor getExecutor() {
		return executor;
	}

	/**
	 * @param executor the executor to set
	 */
	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	/**
	 * @return the inMemoryDatabase
	 */
	public Database getInMemoryDatabase() {
		return inMemoryDatabase;
	}

	/**
	 * @param inMemoryDatabase the inMemoryDatabase to set
	 */
	public void setInMemoryDatabase(Database inMemoryDatabase) {
		this.inMemoryDatabase = inMemoryDatabase;
	}

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
}
