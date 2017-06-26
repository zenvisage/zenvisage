package edu.uiuc.zenvisage.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import edu.uiuc.zenvisage.zqlcomplete.executor.VizColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.XColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.YColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLRow;


public class Query {
  
  private List<String> projections;	
  private Filter filter;
  private String compositeFilter="";
  private String groupBy;	
  private String aggregationFunc;
  private String aggregationVarible;
  
   public String getGroupBy() {
		return groupBy;
	}
	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}
	public String getAggregationVarible() {
		return aggregationVarible;
	}
	public void setAggregationVarible(String aggregationVarible) {
		this.aggregationVarible = aggregationVarible;
	}
	public List<String> getProjections() {
		return projections;
	}
	public String getAggregationFunc() {
		return aggregationFunc;
	}
	
	public ZQLRow getZQLRow(){
		//return new ZQLRow();
		String zAndX[] = groupBy.split(",");
		VizColumn vc = new VizColumn();
		vc.getMap().put(VizColumn.aggregation, aggregationFunc);
		ZQLRow zqlRow=null;
		if(!compositeFilter.equals(""))
		zqlRow = new ZQLRow(new XColumn(zAndX[1]), new YColumn(aggregationVarible), new ZColumn(zAndX[0]), compositeFilter, vc);
		else{
		zqlRow = new ZQLRow(new XColumn(zAndX[1]), new YColumn(aggregationVarible), new ZColumn(zAndX[0]), null, vc);}
		return zqlRow;
	}
  	
  public String getCompositeFilter() {
			return compositeFilter;
		}
		public void setCompositeFilter(String compositeFilter) {
			this.compositeFilter = compositeFilter;
		}

  public static class Filter{
	private boolean  isComposite;
	public boolean isComposite() {
		return isComposite;
	}
	public void setComposite(boolean isComposite) {
		this.isComposite = isComposite;
	}	 
  }
 
  public static final class FilterPredicate extends Filter{
	private String propertyName;
	private FilterOperator operator;
	private String value;
	public FilterPredicate(String propertyName, FilterOperator operator, String value){
		this.propertyName=propertyName;
		this.operator=operator;
		this.value=value;
		this.setComposite(false);
	}
	public FilterOperator getOperator(){
		return operator;
	}
	public String getPropertyName(){
		return propertyName;
	}
	public String getValue(){
		return value;
	}
	public String toString(){
		return "";
	}
  
  }
  
  public static final class CompositeFilter extends Filter{
	    private CompositeFilterOperator operator;
	    List<Filter> subFilters = new ArrayList<Filter>();
	    public CompositeFilter(CompositeFilterOperator operator, Collection<Filter> subFilters2) {
			this.operator=operator;
			this.subFilters=subFilters;
			this.setComposite(true);
		}
	 public	CompositeFilterOperator	getOperator(){ 
		 return operator;
	 } 
	 public	List<Filter>	getSubFilters(){
			return subFilters;
		}  
		
		public String	toString(){
			return "";
		} 
		
  }
  
	
  public  static enum FilterOperator{
		EQUAL("="), 
		GREATER_THAN(">"), 
		GREATER_THAN_OR_EQUAL(">="), 
		LESS_THAN("<"), 
		LESS_THAN_OR_EQUAL("<="), 
		NOT_EQUAL("!=");
		private final String value;
		FilterOperator(final String val) {
		 value = val;
	    }
		public static FilterPredicate of(String propertyName, Object value){ return null;}
		public  String	toString(){ return null;}
		
		public static FilterOperator fromString(String operator) {
			if (operator != null) {
				for (FilterOperator op : FilterOperator.values()) {
					if (operator.equalsIgnoreCase(op.value)) {
						return op;
					}
				}
			}
			return null;
		}

		
	}
  
 
  public static enum   CompositeFilterOperator{
		AND, 
		OR;		    	
		 public static CompositeFilter and(Filter... subFilters) {
		      return and(Arrays.asList(subFilters));
		    }

		    public static CompositeFilter and(Collection<Filter> subFilters) {
		      return new CompositeFilter(AND, subFilters);
		    }

		    public static CompositeFilter or(Filter... subFilters) {
		      return or(Arrays.asList(subFilters));
		    }

		    public static CompositeFilter or(Collection<Filter> subFilters) {
		      return new CompositeFilter(OR, subFilters);
		    }

		
	}
  

  	public Query(String string) {
	// TODO Auto-generated constructor stub
  	}
  	public Query addFilter(java.lang.String propertyName, Query.FilterOperator operator, java.lang.Object value){
		return null;
  	}
	public boolean	getDistinct(){
		return true;
	}	 
	public Filter	getFilter() {
		return filter;
	}
	public FilterPredicate	getFilterPredicates(){
		return null;
	}
	public String	getKind(){
		return "";
	}
	//public Collection<Projection>	getProjections(){}
	public Query	setDistinct(boolean distinct){
		return null;
	}
	public Query setFilter(Filter filter){
		this.filter=filter;
		return this;
	}
	public String toString(){
		return "";
	}
	public Query setGrouby(String groupBy){
		this.groupBy = groupBy;
		return this;
	}

	public Query    setHaving(){
		return null;
	}

	public Query setAggregationFunc(String aggregationFunc){
		this.aggregationFunc=aggregationFunc;
		return this;
	}

	public Query setAggregationVaribale(String aggregationVariable){
		this.aggregationVarible=aggregationVariable;
		return this;
	}


	public Query setProjections(List<String> projections){
		this.projections=projections;
		return this;
	}

	public static void main(String[] args) {

		Filter marksFilter =
			  new FilterPredicate("marks",
			                      FilterOperator.GREATER_THAN_OR_EQUAL,
			                      "90");

		Filter departmentFilter =
			  new FilterPredicate("department",
			                      FilterOperator.EQUAL,
			                      "CS");

	
	
			//Use CompositeFilter to combine multiple filters
		Filter filter =
			  CompositeFilterOperator.and(marksFilter, departmentFilter);

    	List<String> projections= new ArrayList<String>();
    	projections.add("id");
    
			// Use class Query to assemble a query
		Query q = new Query("query").setFilter(filter).setGrouby("name").setAggregationFunc("avg").setAggregationVaribale("marks");

	}

}