package edu.uiuc.zenvisage.model;

public class Node {

	public String type;
	public String name;
	public String xval;
	public String yval;
	public String zval;
	public String constraint;
	public String process;
	
	public Node() {
		
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getXval() {
		return xval;
	}

	public void setXval(String xval) {
		this.xval = xval;
	}

	public String getYval() {
		return yval;
	}

	public void setYval(String yval) {
		this.yval = yval;
	}

	public String getZval() {
		return zval;
	}

	public void setZval(String zval) {
		this.zval = zval;
	}

	public String getConstraint() {
		return constraint;
	}

	public void setConstraint(String constraint) {
		this.constraint = constraint;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

}
