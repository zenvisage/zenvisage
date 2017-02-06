package edu.uiuc.zenvisage.data.remotedb;

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

	public Column(ColumnMetadata columnMetadata,MetadataLoader metadataLoader){
		this.columnMetadata=columnMetadata;
		metadataLoader.getColumns().put(columnMetadata.name, this);
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

	//TODO: Change from string to value
	//TODO: Change from List to array for values;
	public void add(int row,String value){
	     if(columnMetadata.dataType.equals("int") || columnMetadata.dataType.equals("float") ){
			 Float num=Float.parseFloat(value);
			 if (num<columnMetadata.min)
				 columnMetadata.min=num;
			 if (num>columnMetadata.max)
				 columnMetadata.max=num;
		 }
	}
}
