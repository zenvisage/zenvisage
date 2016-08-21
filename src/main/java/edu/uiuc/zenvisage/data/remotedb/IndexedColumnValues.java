package edu.uiuc.zenvisage.data.remotedb;

import java.util.HashMap;
import java.util.Map;

import org.roaringbitmap.RoaringBitmap;

public class IndexedColumnValues {
	Map<String,RoaringBitmap> columnValues= new HashMap<String, RoaringBitmap>();

	public Map<String, RoaringBitmap> getColumnValues() {
		return columnValues;
	}

	public void add(int row,String value) {

		RoaringBitmap rbm= columnValues.get(value);
		if(rbm==null){
			rbm=new RoaringBitmap();
			columnValues.put(value, rbm);
		}
		rbm.add(row);		
	}

}
