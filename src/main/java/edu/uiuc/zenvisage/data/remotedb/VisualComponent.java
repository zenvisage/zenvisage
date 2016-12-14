package edu.uiuc.zenvisage.data.remotedb;

public class VisualComponent {		
	
	private WrapperType zValue; // get the string, that is chart ztype
	private String xAttribute;
	private String yAttribute;	
	private String zAttribute;
	private Points points;
	private double score = 0.0;

	/*Empty Constructor*/
	public VisualComponent(WrapperType zValue, Points points){
		this.zValue = zValue;
		this.points = points;
	};
	
	public VisualComponent(WrapperType zValue, Points points, String xAttribute, String yAttribute){
		this.zValue = zValue;
		this.points = points;
		this.xAttribute = xAttribute;
		this.yAttribute = yAttribute;
	};
	
	public VisualComponent(WrapperType zValue, Points points, String xAttribute, String yAttribute, double score){
		this.zValue = zValue;
		this.points = points;
		this.xAttribute = xAttribute;
		this.yAttribute = yAttribute;
		this.score = score;
	};	
	/**
	 * @return the xAttribute
	 */
	public String getxAttribute() {
		return xAttribute;
	}

	/**
	 * @param xAttribute the xAttribute to set
	 */
	public void setxAttribute(String xAttribute) {
		this.xAttribute = xAttribute;
	}

	/**
	 * @return the yAttribute
	 */
	public String getyAttribute() {
		return yAttribute;
	}

	/**
	 * @param yAttribute the yAttribute to set
	 */
	public void setyAttribute(String yAttribute) {
		this.yAttribute = yAttribute;
	}
	
	public String getzAttribute() {
		return zAttribute;
	}

	public void setzAttribute(String zAttribute) {
		this.zAttribute = zAttribute;
	}

	public WrapperType getZValue() {		
		return zValue;		
	}
	
	public void setZValues(WrapperType zValues) {		
		this.zValue = zValues;		
	}
	
	public Points getPoints() {		
		return points;		
	}
	
	public void setPoints(Points points) {		
		this.points = points;
	}
	
	public String toString(){
		
		StringBuilder ret = new StringBuilder();
	    ret.append(zValue.toString()+"\n");  
	    
	    for(WrapperType x: points.getXList())
	    	ret.append(x.toString() + " ");
	    ret.append("\n");
	    
	    for(WrapperType y: points.getYList())
	    	ret.append(y.toString() + " ");
	    ret.append("\n");
	    
		return ret.toString();
		
	}
	
	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}
		
}