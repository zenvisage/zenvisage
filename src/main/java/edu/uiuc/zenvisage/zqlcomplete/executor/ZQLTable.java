package edu.uiuc.zenvisage.zqlcomplete.executor;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * @author tarique
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ZQLTable {
	private List<ZQLRow> zqlRows;
	private String db;
	
	public ZQLTable() {
		zqlRows=new ArrayList<>();
	}
	
	public List<ZQLRow> getZqlRows() {
		return zqlRows;
	}

	public void setZqlRows(List<ZQLRow> zqlRows) {
		this.zqlRows = zqlRows;
	}
	
	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}

}
