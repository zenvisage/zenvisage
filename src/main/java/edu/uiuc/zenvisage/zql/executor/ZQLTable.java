package edu.uiuc.zenvisage.zql.executor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tarique
 *
 */
@Deprecated
public class ZQLTable {
	private List<ZQLRow> zqlRows;
	
	public ZQLTable() {
		zqlRows=new ArrayList<>();
	}
	
	public List<ZQLRow> getZqlRows() {
		return zqlRows;
	}

	public void setZqlRows(List<ZQLRow> zqlRows) {
		this.zqlRows = zqlRows;
	}

	
	
}
