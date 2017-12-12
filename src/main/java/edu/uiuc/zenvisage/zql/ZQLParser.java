package edu.uiuc.zenvisage.zql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZQLParser {

	public static void main(String[] args) {
		
		String pattern = "(\\w)(\\s+)([\\.,])";
		System.out.println("hi .".replaceAll(pattern, "$1$3"));
		
		
		//(space before word)(word)(space after word)(comma)* : last word is (space)(word)
		pattern = "(?<=\\[)((\\s)*(\\w+)(\\s*),)*(\\s)*(\\w+)(?=\\])";
		Matcher match = Pattern.compile(pattern).matcher("[CA, NY,LA]");
	    while (match.find()) {
	        for (int i = 0; i <= match.groupCount(); i++) {
	            System.out.println(i + " " + match.group(i));
	            System.out.println(match.start(i) + " " + match.end(i));
	            // match start: regexMatcher.start(i)
	            // match end: regexMatcher.end(i)
	        }
	        System.out.println("---");
	    }
	    parseList("[CA]");
	    parseList("[CA, NY,LA]");
	    //parseList("CA");
	}
	
	private static List<String> parseList(String input) {
		List<String> res = null;
		String pattern = "(?<=\\[)((\\s)*(\\w+)(\\s*),)*(\\s)*(\\w+)(?=\\])";
		Matcher match = Pattern.compile(pattern).matcher(input);
		if (match.find()) {
			String list = match.group(); // of format CA, NY, LA
			res = new ArrayList<String>(Arrays.asList(list.split("\\s*,\\s*")));
		}

		return res;
	}
}
