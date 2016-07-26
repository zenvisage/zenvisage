package edu.uiuc.zenvisage.data.roaringdb.db;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.roaringbitmap.RoaringBitmap;
import edu.uiuc.zenvisage.data.Query.FilterOperator;
import edu.uiuc.zenvisage.data.Query.FilterPredicate;

public class Column {
	public ColumnMetadata columnMetadata= new ColumnMetadata();
	private IndexedColumnValues indexedColumnValues;
	private UnIndexedColumnValues unIndexedColumnValues;

	public Column(ColumnMetadata columnMetadata,Database database){
		this.columnMetadata=columnMetadata;
		database.getColumns().put(columnMetadata.name, this);
		if(columnMetadata.isIndexed==true){
			indexedColumnValues=new IndexedColumnValues();
			database.getIndexedColumns().put(columnMetadata.name, this);
		}
		unIndexedColumnValues=new UnIndexedColumnValues();
	}

	public String getName() {
		return columnMetadata.name;
	}

	public ColumnMetadata getColumnMetadata() {
		return columnMetadata;
	}

	public void setColumnMetadata(ColumnMetadata columnMetadata) {
		this.columnMetadata = columnMetadata;
	}

	public IndexedColumnValues getIndexedColumnValues() {
		return indexedColumnValues;
	}

	public UnIndexedColumnValues getunIndexedColumnValues() {
		return unIndexedColumnValues;
	}

	//TODO: Change from string to value
	//TODO: Change from List to array for values;
	public void add(int row,String value){
		//System.out.println(row);
		//System.out.println(value);
		if(columnMetadata.isIndexed){
			addIndexedValue(row,value);
		}
	    addUnIndexedValue(row,value);

	     if(columnMetadata.dataType.equals("int") || columnMetadata.dataType.equals("float") ){
			 Float num=Float.parseFloat(value);
			 if (num<columnMetadata.min)
				 columnMetadata.min=num;
			 if (num>columnMetadata.max)
				 columnMetadata.max=num;
		 }



	}

	public void addUnIndexedValue(int row, String value) {
		unIndexedColumnValues.getColumnValues().add(value);

	}
	public void addIndexedValue(int row, String value) {
		indexedColumnValues.add(row,value);
	}


	public 	Map<String,RoaringBitmap>  getIndexedValues(){
		Map<String,RoaringBitmap> valuesCopy = new HashMap<String, RoaringBitmap>();
		for(String key:indexedColumnValues.columnValues.keySet()){
			valuesCopy.put(key,(RoaringBitmap)indexedColumnValues.columnValues.get(key).clone());
		}
		return valuesCopy;
	}

	public  List<String> getUnIndexedValues(){
		 List<String> columnValuesCopy= new ArrayList<String>();
		 columnValuesCopy.addAll(unIndexedColumnValues.getColumnValues());
		 return columnValuesCopy;

	}

	public RoaringBitmap getIndexedValues(FilterPredicate filterPredicate){
		String value=String.valueOf(filterPredicate.getValue());
		RoaringBitmap values = new RoaringBitmap();
		for(String key: indexedColumnValues.columnValues.keySet()) {
			if(filterPredicate.getOperator()==FilterOperator.EQUAL && key.equals(value)) {
				values=(RoaringBitmap)indexedColumnValues.columnValues.get(key).clone();
			}
		}
		return values;
	}


	public RoaringBitmap getUnIndexedValues(FilterPredicate filterPredicate){
		//TODO
		String value = String.valueOf(filterPredicate.getValue());
		RoaringBitmap values = new RoaringBitmap();
		List<String> columnValues = this.unIndexedColumnValues.getColumnValues();
		for (int i = 0; i < columnValues.size(); i++) {
			if (filterPredicate.getOperator() == FilterOperator.EQUAL && value.equals(columnValues.get(i))) {
				values.add(i);
			}
		}
		return values;
	}





}
