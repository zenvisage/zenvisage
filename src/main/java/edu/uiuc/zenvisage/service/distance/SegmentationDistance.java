package edu.uiuc.zenvisage.service.distance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.util.FastMath;

import net.sf.javaml.distance.fastdtw.dtw.DTW;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeries;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeriesPoint;


/*
 * @author Yuxuan
 */

public class SegmentationDistance implements Distance {
	static double MAX_ERROR = 1000;
	static double NUM_OF_SEGMENTS = 10;
	static int NUM_TO_RETURN = 20;
	
	public static class Segment {
		double startX;
		double startY;
		double endX;
		double endY;
		double costToMergeNext;
		
		double meanX;
		double meanY;
		double angle;
		double width;
		double height;
		double length;
		double intercept;
		double slope;
		
		Segment (int startX, int endX, double cost, double[][] inputTrend){
			this.startX = startX;
//			this.startY = startY;
			this.endX = endX;
//			this.endY = endY;
			this.costToMergeNext = cost;
			this.length = Math.sqrt(Math.pow(inputTrend[startX][0] - inputTrend[endX][0], 2) + Math.pow(inputTrend[startX][1] - inputTrend[endX][1], 2));
		}
		
		Segment (double startX, double startY, double endX, double endY, double angle, double slope, double intercept) {
			this.startX = startX;
			this.startY = startY;
			this.endX = endX;
			this.endY = endY;
			this.angle = angle;
			this.slope = slope;
			this.intercept = intercept;
		}
		
		double[] getMidpoint() {
			double[] rt = new double[2];
			rt[0] = 0.5 * (startX + endX);
			rt[1] = 0.5 * (startY + endY);
			return rt;
		}
		double getLength() {
			return Math.sqrt(Math.pow(startX - endX, 2) + Math.pow(startY - endY, 2));
		}
		double getWidth() {
			return endX - startX;
		}
	}
	
	public static int findCheapestMerge(List<Segment> segments) {
		double lowestCost = Double.POSITIVE_INFINITY;
		int position = 0;
		for (int i = 0; i < segments.size(); i++) {
//			System.out.println(i + "\t" + segments.get(i).costToMergeNext);
			if (segments.get(i).costToMergeNext < lowestCost) {
				lowestCost = segments.get(i).costToMergeNext;
				position = i;
			}
		}
		
		return position;
	}
	
	public static double getMergeCost(double[][] inputTrend, int start, int end) {
		SimpleRegression sr = new SimpleRegression();
		sr.addData(Arrays.copyOfRange(inputTrend, start, end+1));
		
		return sr.getMeanSquareError();
//		return sr.getSumSquaredErrors();
	}
	
	public static Segment updateRegression(double[][] inputTrend, Segment segment) {
		SimpleRegression sr = new SimpleRegression();
		sr.addData(Arrays.copyOfRange(inputTrend, (int) segment.startX, (int) segment.endX + 1));
		
		double intercept = sr.getIntercept();
		double slope = sr.getSlope();
		
		segment.angle = Math.toDegrees(Math.atan(slope));
		segment.width = segment.endX - segment.startX;
		segment.meanX = (segment.endX + segment.startX) / 2;
		segment.meanY = 0;
		for (int i = (int) segment.startX; i <= segment.endX; i++) {
			segment.meanY += inputTrend[i][1];
		}
		segment.meanY = segment.meanY/segment.width;
		segment.intercept = intercept;
		segment.slope = slope;
		segment.startY = segment.startX * slope + intercept;
		segment.endY = segment.endX * slope + intercept;
		segment.height = Math.abs(segment.endY - segment.startY);
		segment.length = Math.sqrt(Math.pow(segment.height, 2) + Math.pow(segment.width, 2));
		
		return segment;
	}
	
	public static List<Segment> getBottomUp(double[][] inputTrend) {
		assert inputTrend.length >= 2;
		
		List<Segment> sList = new ArrayList();
		
		for (int i = 0; i < inputTrend.length-2; i++) {
			Segment segment = new Segment(i, i+1, getMergeCost(inputTrend, i, i+2), inputTrend);
			sList.add(segment);
		}
		Segment lastS = new Segment(inputTrend.length-2, inputTrend.length-1, Double.POSITIVE_INFINITY, inputTrend);
		sList.add(lastS);
		
		while (true) {
			int position = findCheapestMerge(sList);
			
			if (sList.size() <= NUM_OF_SEGMENTS || sList.get(position).costToMergeNext > MAX_ERROR) {
//			if (sList.get(position).costToMergeNext > MAX_ERROR) {
//			if (sList.size() <= NUM_OF_SEGMENTS) {
				break;
			}
			
			Segment tempSegment = sList.get(position);
			tempSegment.endX = sList.get(position+1).endX;
			sList.set(position, tempSegment);
			
			sList.remove(position+1);
			
			if (position == sList.size()-1) {
				tempSegment = sList.get(position);
				tempSegment.costToMergeNext = Double.POSITIVE_INFINITY;
				sList.set(position, tempSegment);
			}
			else {
				tempSegment = sList.get(position);
				tempSegment.costToMergeNext = getMergeCost(inputTrend, (int) sList.get(position).startX, (int) sList.get(position+1).endX) * (sList.get(position).length + sList.get(position+1).length);
				sList.set(position, tempSegment); 
			}
			
			if (position == 0) {
				continue;
			}
			else {
				tempSegment = sList.get(position-1);
				tempSegment.costToMergeNext = getMergeCost(inputTrend, (int) sList.get(position-1).startX, (int) sList.get(position).endX) * (sList.get(position-1).length + sList.get(position).length);
				sList.set(position-1, tempSegment); 
			}			
		}
		
		for (int i = 0; i < sList.size(); i++) {
			sList.set(i, updateRegression(inputTrend, sList.get(i)));
//			System.out.println(sList.get(i).startX + "\t" + sList.get(i).startY + "\t"+  sList.get(i).endX + "\t" + sList.get(i).endY);
		}
		
		return sList;
	}
	
	
	public static double[] getSegmentSimilarity (Segment s1, Segment s2, int totalWidth, double totalLength1, double totalLength2) {
		double angleDifference = Math.abs(s1.angle - s2.angle)/180;

		Segment sa = s1.width < s2.width ? s1 : s2;
		Segment sb = s1.width < s2.width ? s2 : s1;		
		
		double ratio = Math.min(s1.length, s2.length);
			
		double translationDistance;
		double bx;
		double by;
		if (sa.startX < sb.startX) {
			bx = sb.startX + 1/2 * sa.width;
		}
		else if (sa.endX > sb.endX) {
			bx = sb.endX - 1/2 * sa.width;
		}
		else {
			bx = sa.meanX;
		}
		by = sb.slope * bx + sb.intercept;
		translationDistance = Math.sqrt(Math.pow(sa.meanX - bx, 2) + Math.pow(sa.meanY - by, 2));
		double areaOfRotation = Math.PI * ratio * ratio / 2 * angleDifference;
		double areaOfTranslation = translationDistance * ratio;
		
//		System.out.println(sa.meanX + "\t" + sa.meanY + "\t" + bx + "\t" + by + "\t" + (translationDistance) + "\t" + areaOfTranslation);
		
		double[] rt = new double[2];
//		rt[0] = (angleDifference + translationDistance);
		rt[0] = 100 * (0.5 - angleDifference) * (1 - translationDistance * 2.0 / (sa.length + sb.length)) * (ratio / (totalLength1 + totalLength2));
		rt[1] = ratio;
		
		return rt;
	}
	
	public static double getSimilarityRate(Segment s1, Segment s2, double totalLength1, double totalLength2, double totalWidth) {
		double angleDifference = Math.abs(s1.angle - s2.angle)/180;		
		double distance = 0;

//		distance = Math.sqrt(Math.pow(s1.getMidpoint()[0] - s2.getMidpoint()[0], 2) + Math.pow(s1.getMidpoint()[1] - s2.getMidpoint()[1], 2));
		distance = Math.sqrt(2 * Math.pow(s1.getMidpoint()[1] - s2.getMidpoint()[1], 2));
		
		distance = 0;
		int k = 5;
		double width1 = s1.getWidth();
		double width2 = s2.getWidth();
		for (int i = 0; i <= k; i++) {
			double tempX1 = s1.startX + i * 1.0 / k * width1;
			double tempY1 = tempX1 * s1.slope + s1.intercept;
			double tempX2 = s2.startX + i * 1.0 / k * width2;
			double tempY2 = tempX2 * s2.slope + s2.intercept;
			
			distance += Math.pow(tempY1 - tempY2, 2);
		}
		distance /= k+1;
		distance = Math.sqrt(distance);
		
		double distanceDifference = distance / 100;
//		System.out.println(x1 + "\t" + x2 + "\t" + angleDifference + "\t" + translationDifference + "\t" + Math.abs(s1.length - s2.length)/(s1.length + s2.length));
		
//		return (1 - angleDifferenceTransform(angleDifference)) * (1 - distanceDifferenceTransform(distanceDifference));
		return (1 - 0.1 * angleDifferenceTransform(angleDifference) - 0.9 * distanceDifferenceTransform(distanceDifference));
	}
	
	
	public static double getMatchingSimilarity(List<Segment> l1, List<Segment> l2, int type, double totalLength1, double totalLength2, double totalWidth) {
		if (type == 0) {
			Segment s1 = l1.get(0);
			Segment s2 = l2.get(0); 
			double similarityRate = getSimilarityRate(s1, s2, totalLength1, totalLength2, totalWidth);
			double weight = (s1.length + s2.length) / (totalLength1 + totalLength2) * 100;
//			double weight = (s1.width + s2.width) / (2 * totalWidth) * 100;
			double widthSimilarity = 1 - widthDifferenceTransform(Math.abs(s1.width - s2.width)/(s1.width + s2.width));
			
//			System.out.print(similarityRate + "\t" + widthSimilarity + "\t" + weight + "\t" + similarityRate * weight * widthSimilarity);
//			System.out.println();
			
			return similarityRate * weight * 1;
		}
		
		Segment s;
		List<Segment> l;
		
		if (type == 1) {
			s = l2.get(0);
			l = l1;
		}
		else if (type == 2) {
			s = l1.get(0);
			l = l2;
		}
		else {
			return -1;
		}
	
		double listLength = 0;		
		double listWidth = 0;
		double averageSimilarity = 0;
		for (int i = 0; i < l.size(); i++) {
			listLength += l.get(i).length;
			listWidth += l.get(i).width;
		}		
		double currentStart = s.startX;
		for (int i = 0; i < l.size(); i++) {			
			double[][] temp2 = new double[2][2];
			double startX = currentStart;
			double startY = startX * s.slope + s.intercept;
			double endX = startX + l.get(i).length / listLength * s.width;
			double endY = endX * s.slope + s.intercept;
			
			Segment tempS = new Segment(startX, startY, endX, endY, s.angle, s.slope, s.intercept);
			
			averageSimilarity += getSimilarityRate(tempS, l.get(i), totalLength1, totalLength2, totalWidth) * l.get(i).length;
			//			System.out.println("hehehehe\t" + tempX1 + "\t" + tempY1 + "\t" + getSimilarityRateV2(tempX1, tempY1, s.angle, l.get(i).meanX, l.get(i).meanY, l.get(i).angle, totalLength1, totalLength2, totalWidth) );
			currentStart += l.get(i).length / listLength * s.width;
		}
		averageSimilarity = averageSimilarity / listLength;		
//		System.out.println(s.startX + "\t" + l.get(0).startX + ": " + l.size() + "\t" + averageSimilarity + "\t" + averageSimilarity * (listLength + s.length) / (totalLength1 + totalLength2) * 100 * (1 - Math.abs(s.length - listLength)/(totalLength1 + totalLength2)));

		double weight = (listLength + s.length) / (totalLength1 + totalLength2) * 100;
//		double weight = (listWidth + s.width) / (2 * totalWidth) * 100;
		double widthSimilarity = 1 - widthDifferenceTransform(Math.abs(s.width - listWidth)/(s.width + listWidth));
		
//		System.out.print(averageSimilarity + "\t" + widthSimilarity + "\t" + weight + "\t" + averageSimilarity * weight * widthSimilarity);
//		System.out.println();
		
		return averageSimilarity * weight * 1;
	}
	
	public static double getMaxAtIJ(double[][][] similarities, int i, int j, int baseLevel) {
		double rt = similarities[i][j][baseLevel - j];
		int pos = baseLevel - j;
		for (int k = baseLevel - j + 1; k <= baseLevel + i-1; k++) {
			if (similarities[i][j][k] > rt) {
				rt = similarities[i][j][k];
				pos = k;
			}
		}
		
		return rt;
	}
	
	public static double getMaxFrom3(double a, double b, double c) {
		if (a >= b) 
			if (a >= c) 
				return a;
			else
				return c;
		else
			if (b >= c)
				return b;
			else
				return c;
	}
	
	public static void showMatching(double[][][] matchingSimilarities, int baseLevel, List<Segment> t1, List<Segment> t2, double totalLength1, double totalLength2, double totalWidth) {
		int i = matchingSimilarities.length-1;
		int j = matchingSimilarities[i].length - 1;
		
		while (i != 0 && j != 0) {
			double rt = matchingSimilarities[i][j][baseLevel - j + 1];
			int pos = baseLevel - j + 1;
			for (int k = baseLevel - j + 1; k <= baseLevel + i - 1; k++) {
				if (matchingSimilarities[i][j][k] > rt) {
					rt = matchingSimilarities[i][j][k];
					pos = k;
				}
			}
			pos = pos - baseLevel;
			
			if (pos > 0) {
				System.out.println(i-pos + "-" + i + ": " + j + "\t");
				getMatchingSimilarity(t1.subList(i-pos,  i+1), t2.subList(j, j+1), 1, totalLength1, totalLength2, totalWidth);
				i = i - pos - 1;
				j = j -1;
			}
			else if (pos == 0) {
				System.out.println(i + ": " + j + "\t");
				getMatchingSimilarity(t1.subList(i,  i+1), t2.subList(j, j+1), 0, totalLength1, totalLength2, totalWidth);
				i--;
				j--;
			}
			else {
				System.out.println(i + ": " + (j+pos) + "-" + j + "\t");
				getMatchingSimilarity(t1.subList(i,  i+1), t2.subList(j + pos, j+1), 2, totalLength1, totalLength2, totalWidth);
				i--;
				j = j + pos - 1;
			}
		}
		
		if (i == 0 && j == 0) {
			System.out.println(i + ": " + j + "\t" );
			getMatchingSimilarity(t1.subList(i,  i+1), t2.subList(j, j+1), 0, totalLength1, totalLength2, totalWidth);
		}
		else if (i != 0){
			System.out.println(0 + "-" + i + ": " + j + "\t");
			 getMatchingSimilarity(t1.subList(0,  i+1), t2.subList(j, j+1), 1, totalLength1, totalLength2, totalWidth);
		}
		else {
			System.out.println(i + ": " + 0 + "-" + j + "\t");
			getMatchingSimilarity(t1.subList(i,  i+1), t2.subList(0, j+1), 2, totalLength1, totalLength2, totalWidth);
		}
		
		System.out.println();
	}
	
	public static double getDTWSimilarityV2(List<Segment> t1, List<Segment> t2, int totalWidth) {
		int baseLevel = t2.size()-1;
		double[][][] matchingSimilarities = new double[t1.size()][t2.size()][t1.size() + t2.size() - 1];
		double[][][] accumulatedSimilarity = new double[t1.size()][t2.size()][t1.size() + t2.size() - 1];
		double[][] accumulatedMax = new double[t1.size()][t2.size()];
		
		double totalLength1 = 0;
		for (int i = 0; i < t1.size(); i++) {
			totalLength1 += t1.get(i).length;
		}
		double totalLength2 = 0;
		for (int i = 0; i < t2.size(); i++) {
			totalLength2 += t2.get(i).length;
		}
		
		for (int i = 0; i < t1.size(); i++) {
			for (int j = 0; j < t2.size(); j++) {
				matchingSimilarities[i][j][baseLevel] = getMatchingSimilarity(t1.subList(i, i+1), t2.subList(j, j+1), 0, totalLength1, totalLength2, totalWidth);
						
				for (int k = 1; k <= i; k++ ) {
					matchingSimilarities[i][j][baseLevel + k] = getMatchingSimilarity(t1.subList(i-k, i+1), t2.subList(j, j+1), 1, totalLength1, totalLength2, totalWidth);					
				}
				
				for (int k = 1; k <= j; k++ ) {
					matchingSimilarities[i][j][baseLevel - k] = getMatchingSimilarity(t1.subList(i, i+1), t2.subList(j-k, j+1), 2, totalLength1, totalLength2, totalWidth);					
				}
			}
		}
		
		accumulatedSimilarity[0][0][baseLevel] = matchingSimilarities[0][0][baseLevel];
		accumulatedMax[0][0] = accumulatedSimilarity[0][0][baseLevel];
		
		for (int i = 1; i < t1.size(); i++) {
			for (int k = 0; k <= i; k++) {
				accumulatedSimilarity[i][0][baseLevel + k] = matchingSimilarities[i][0][baseLevel + i];
			}
			accumulatedMax[i][0] = matchingSimilarities[i][0][baseLevel + i];
		}
		
		for (int j = 1; j < t2.size(); j++) {
			for (int k = 0; k <= j; k++) {
				accumulatedSimilarity[0][j][baseLevel - j] = matchingSimilarities[0][j][baseLevel - j];
			}
			accumulatedMax[0][j] = matchingSimilarities[0][j][baseLevel - j];
		}
		
		for (int i = 1; i < t1.size(); i++) {
			for (int j = 1; j < t2.size(); j++) {
				accumulatedSimilarity[i][j][baseLevel] = accumulatedMax[i-1][j-1] + matchingSimilarities[i][j][baseLevel];
						
				for (int k = 1; k <= i-1; k++ ) {
					accumulatedSimilarity[i][j][baseLevel + k] = accumulatedMax[i-k-1][j-1] + matchingSimilarities[i][j][baseLevel + k];				
				}
//				accumulatedSimilarity[i][j][baseLevel + i] = accumulatedMax[0][j-1] + matchingSimilarities[i][j][baseLevel + i];
				
				for (int k = 1; k <= j-1; k++ ) {
					accumulatedSimilarity[i][j][baseLevel - k] = accumulatedMax[i-1][j-k-1] + matchingSimilarities[i][j][baseLevel - k];					
				}
//				accumulatedSimilarity[i][j][baseLevel - j] = accumulatedMax[i-1][0] + matchingSimilarities[i][j][baseLevel + i];
				
				accumulatedMax[i][j] = getMaxAtIJ(accumulatedSimilarity, i, j, baseLevel);
			}
		}
		
//		for (int i = 0; i < t1.size(); i++) {
//			for (int j = 0; j < t2.size(); j++) {
//				System.out.print(accumulatedMax[i][j] + "\t");
//			}
//			System.out.println();
//		}
		
//		showMatching(accumulatedSimilarity, baseLevel, t1, t2, totalLength1, totalLength2, totalWidth);
		
		return accumulatedMax[t1.size()-1][t2.size()-1];
	}
	
	public static double getDTWSimilarity(List<Segment> t1, List<Segment> t2, int totalWidth) {
		double[][][] similarities = new double[t1.size()][t2.size()][2];
		
		double totalLength1 = 0;
		for (int i = 0; i < t1.size(); i++) {
			totalLength1 += t1.get(i).length;
		}
		double totalLength2 = 0;
		for (int i = 0; i < t2.size(); i++) {
			totalLength2 += t2.get(i).length;
		}
		
		for (int i = 0; i < t1.size(); i++) {
			for (int j = 0; j < t2.size(); j++) {
				double[] values = getSegmentSimilarity(t1.get(i), t2.get(j), totalWidth, totalLength1, totalLength2);
				similarities[i][j][0] = values[0];
				similarities[i][j][1] = values[1];				
			}
		}
		
		double[][] accumulatedSimilarity = new double[t1.size()][t2.size()];
		accumulatedSimilarity[0][0] = similarities[0][0][0];
		
		for (int i = 1; i < t1.size(); i++) {
			accumulatedSimilarity[i][0] = similarities[i][0][0] + accumulatedSimilarity[i-1][0];
		}
		for (int i = 1; i < t2.size(); i++) {
			accumulatedSimilarity[0][i] = similarities[0][i][0] + accumulatedSimilarity[0][i-1];
		}
		
		double accumulatedRatio = similarities[0][0][1];
		for (int i = 1; i < t1.size(); i++) {
			for (int j = 1; j < t2.size(); j++) {
				double d1 = accumulatedSimilarity[i-1][j-1];
				double d2 = accumulatedSimilarity[i][j-1];
				double d3 = accumulatedSimilarity[i-1][j];
				
				if (d1 >= d2 && d1 >= d3) {
					accumulatedSimilarity[i][j] = d1 + similarities[i][j][0];
					accumulatedRatio += similarities[i][j][1];
				}
				else if (d2 >= d3) {
					accumulatedSimilarity[i][j] = d2 + similarities[i][j][0];
					accumulatedRatio += similarities[i][j][1];					
				}
				else {
					accumulatedSimilarity[i][j] = d3 + similarities[i][j][0];
					accumulatedRatio += similarities[i][j][1];					
				}			
			}
		}
		
		showDTWMapping(accumulatedSimilarity, similarities);

		
		return accumulatedSimilarity[t1.size()-1][t2.size()-1];
	}
	
	public static void showDTWMapping(double[][] sm, double[][][] dm) {
		for (int i = 0; i < dm.length; i++) {
			for (int j = 0; j < dm[i].length; j++) {
				System.out.print(dm[i][j][0] + "\t");				
			}
			System.out.println();
		}
		System.out.println();
		
		int i = sm.length-1;
		int j = sm[i].length - 1;
		while (true) {
			if (i == 0) {
				while (j != 0) {
					System.out.println(i + "\t" + j + "\t" + dm[i][j][0]);
					j--;
				}
				break;
			}
			if (j == 0) {
				while (i != 0) {
					System.out.println(i + "\t" + j + "\t" + dm[i][j][0]);
					i--;
				}
				break;
			}
			double as1 = sm[i-1][j-1];
			double as2 = sm[i][j-1];
			double as3 = sm[i-1][j];
			
			if (as1 > as2) {
				if (as1 > as3) {
					System.out.println(i + "\t" + j + "\t" + dm[i][j][0]);
					i--;
					j--;
				}
				else {
					System.out.println(i + "\t" + j + "\t" + dm[i][j][0]);
					i--;
				}
			}
			else {
				if (as2 > as3) {
					System.out.println(i + "\t" + j + "\t" + dm[i][j][0]);
					j--;
				}
				else {
					System.out.println(i + "\t" + j + "\t" + dm[i][j][0]);
					i--;
				}
			}			
		}
		System.out.println(i + "\t" + j + "\t" + dm[i][j][0]  + "\n");
	}
	
	public static double widthDifferenceTransform(double input) {
		if (input <= 0.2) {
			input = 0.5 * input;
		}
		else if (input <= 0.5) {
			input = -0.3 + 2 * input; 
		}
		else if (input <= 1) {
			input = 0.4 + 0.6 * input;
		}
		return input;
	}
	
	public static double distanceDifferenceTransform(double input) {
		if (input <= 0.2) {
			input = input;
		}
		else if (input <= 0.5) {
			input = -0.2 + 2 * input; 
		}
		else if (input <= 1) {
			input = 0.6 + 0.4 * input;
		}
		else {
			input = 1;
		}
		return input;
	}
	
	public static double angleDifferenceTransform(double input) {
		if (input <= 1.0 / 6) {
			input *= 1.2;
		}
		else if (input <= 1.0 / 2) {
			input = input*1.8 - 0.1;
		}
		else {
			input = input*0.4 + 0.6;
		}
		return input;
	}
	
	public static double[][] linearNormalize(double[][] input) {
		double max = input[0][1];
		double min = input[0][1];
		for (int i = 0; i < input.length; i++) {
			if (input[i][1] > max) {
				max = input[i][1];
			}
			if (input[i][1] < min) {
				min = input[i][1];
			}
		}
		
		if (max == min) {
			return input;
		}
		
		for (int i = 0; i < input.length; i++) {
			input[i][1] = (input[i][1] - min) / (max - min) * 100;
		}
		
		return input;
	}
	
	public static double[][] zScoreNormalize(double[][] inputTemp) {
		double[] input = new double[inputTemp.length];
		
		for (int i = 0; i < inputTemp.length; i++) {
			input[i] = inputTemp[i][1];
		}
		
		double mean = StatUtils.mean(input);
		double std = FastMath.sqrt(StatUtils.variance(input));
		for(int i = 0; i < input.length; i++) {
			if (std == 0)
				inputTemp[i][1] = 0;
			else 
				inputTemp[i][1] = (input[i] - mean) / std;
		}
		
		return inputTemp;
	}
	
	public static double calculateSimilarity(double[][] srcR, double[][] tarR) {
		List<Segment> t1 = getBottomUp(srcR);
		List<Segment> t2 = getBottomUp(tarR);
		double s = getDTWSimilarityV2(t1, t2, srcR.length);
		return s;
//		return s * (1 - Math.abs((t1.size() - t2.size() * 1.0) / (t1.size() + t2.size())));
		// *  Math.pow(1 - Math.abs(  (t1.size() - t2.size())*1.0 / ((t1.size() + t2.size())) ), 2);
	}
	
	@Override
	public double calculateDistance(double[] src, double[] tar) {
		
		// TODO Auto-generated method stub
		if (src.length > 138)
			this.MAX_ERROR = 100.0 * src.length/138;
		
		double[][] srcR = new double[src.length][2];
		for (int i = 0; i < src.length; i++) {
			srcR[i][0] = i;
			srcR[i][1] = src[i];
		}		
		
		srcR = linearNormalize(srcR);		
		
		double[][] tarR = new double[tar.length][2];
		for (int i = 0; i < tar.length; i++) {
			tarR[i][0] = i;
			tarR[i][1] = tar[i];
		}
		tarR = linearNormalize(tarR);
		
		double s = calculateSimilarity(srcR, tarR);
//		System.out.println(s);
		
		return 100 - s;
	}

}
