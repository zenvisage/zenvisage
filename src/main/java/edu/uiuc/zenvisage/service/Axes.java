/**
 * 
 */
package edu.uiuc.zenvisage.service;

/**
 * @author tarique
 *
 */
public class Axes {
private String X;
private String Y;

public Axes(String X,String Y){
	this.X=X;
	this.Y=Y;
}
/**
 * @return the x
 */
public String getX() {
	return X;
}
/**
 * @param x the x to set
 */
public void setX(String x) {
	X = x;
}
/**
 * @return the y
 */
public String getY() {
	return Y;
}
/**
 * @param y the y to set
 */
public void setY(String y) {
	Y = y;
}

}
