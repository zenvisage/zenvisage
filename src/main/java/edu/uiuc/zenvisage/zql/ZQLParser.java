package edu.uiuc.zenvisage.zql;

public class ZQLParser {

	public static void main(String[] args) {
		String pattern = "(\\w)(\\s+)([\\.,])";
		System.out.println("hi .".replaceAll(pattern, "$1$3"));
	}
}
