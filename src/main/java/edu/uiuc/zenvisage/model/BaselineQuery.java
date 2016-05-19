/**
 * 
 */
package edu.uiuc.zenvisage.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaofo
 *
 */
public class BaselineQuery {
	public String zAxis;
	public String xAxis;
	public List<String> yAxis = new ArrayList<String>();
	public int pageNum;
	public String predicateColumn;
	public String predicateOperator;
	public String predicateValue;
	public String aggrFunc;
	public List<String> xOperator = new ArrayList<String>();
	public List<Float> xValue = new ArrayList<Float>();
	public List<String> y1Operator = new ArrayList<String>();
	public List<Float> y1Value = new ArrayList<Float>();
	public List<String> y2Operator = new ArrayList<String>();
	public List<Float> y2Value = new ArrayList<Float>();


	public BaselineQuery() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		BaselineQuery bq = (BaselineQuery) obj;
		return this.zAxis.equals(bq.zAxis) &&
				this.xAxis.equals(bq.xAxis) &&
				this.yAxis.equals(bq.yAxis) &&
				this.predicateValue.equals(bq.predicateValue) &&
				this.predicateColumn.equals(bq.predicateColumn) &&
				this.predicateOperator.equals(bq.predicateOperator) &&
				this.aggrFunc.equals(bq.aggrFunc) &&
				this.xOperator.equals(bq.xOperator) &&
				this.xValue.equals(bq.xValue) &&
				this.y1Operator.equals(bq.y1Operator) &&
				this.y1Value.equals(bq.y1Value) &&
				this.y2Operator.equals(bq.y2Operator) &&
				this.y2Value.equals(bq.y2Value);
	}

}
