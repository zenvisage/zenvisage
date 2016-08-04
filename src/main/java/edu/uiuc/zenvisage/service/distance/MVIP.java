package edu.uiuc.zenvisage.service.distance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * @author Changfeng
 */

public class MVIP implements Distance {
	
	//Visually important points' info
	public static class VIPinfo implements Comparable<VIPinfo> {
		public Integer index;//X-index in time series
		public double dist;//vertical distance to the line connecting two adjacent VIPs
		public int importance;//selecting order of VIPs
		
		public VIPinfo (int INDEX, double DIST, int IMPORTANCE) {
			index = INDEX;
			dist = DIST;
			importance = IMPORTANCE;
		}
		
		public int compareTo(VIPinfo arg0) {
	        return this.index.compareTo(arg0.index);
	    }
	}
	
	//possible VIPs in waitinglist during getVIPs
	public static class possibleVIP implements Comparable<possibleVIP> {
		public int index;
		public Double dist;
		public int leftVIPIndex;
		public int rightVIPIndex;
		
		public possibleVIP(int INDEX, double DIST, int LEFTVIPINDEX, int RIGHTVIPINDEX) {
			index = INDEX;
			dist = DIST;
			leftVIPIndex = LEFTVIPINDEX;
			rightVIPIndex = RIGHTVIPINDEX;
		}
		
		public int compareTo(possibleVIP arg0) {
	        return this.dist.compareTo(arg0.dist);
	    }
	}
	
	public static double[] preprocessing(double[] array) {
		double[] result;
		
		//Zcore - already done by service.utility.Zscore
		
		//Smoothing. Smoothing window: [1/4, 1/2, 1/4].
		if (array.length > 5) {
			result = new double[array.length-2];
			for (int i = 0; i < result.length; ++i){
				result[i] = array[i] / 4 + array[i+1] / 2 + array[i+2] / 4;
			}
		}
		else {
			result = array;
		}
		
		return result;
	}

	//Normalized vertical distance to the line connecting two adjacent VIPs
	public static double[] NormVDist(double[] array) {
		final double step = (array[array.length-1] - array[0]) / (array.length - 1);
		double current = array[0];
		double[] Dist = new double[array.length];
		
		for (int i = 0; i < array.length; ++i) {
			Dist[i] = Math.abs((array[i] - current));
			current += step;
		}
		return Dist;
	}
	
	public static List<VIPinfo> getVIPs(double[] ts) {
		
		final double threshold = 0.05;
		List<VIPinfo> VIPlist = new ArrayList<VIPinfo>();
		VIPinfo newVIP;
		double[] Dist;
		int possVIPindex;
		double possVIPdist;
		List<possibleVIP> waitinglist = new ArrayList<possibleVIP>();
		possibleVIP possVIP;
		int dewlIndex;//the index in waitinglist which will be deleted from waitinglist
		double tmpdist;
		
		newVIP = new VIPinfo(0, 0, 0);//head point of time series
		VIPlist.add(newVIP);
		if (ts.length > 1) {
			newVIP = new VIPinfo(ts.length-1, 0, 1);//tail point of time series
			VIPlist.add(newVIP);
		}
		
		if (ts.length > 2) {
			Dist = NormVDist(ts);
			possVIPindex = 1;
			possVIPdist = Dist[1];
			for (int i = 2; i < (ts.length-1); ++i) {
				if (Dist[i] > possVIPdist) {
					possVIPdist = Dist[i];
					possVIPindex = i;
				}
			}
			if (possVIPdist > threshold) {
				possVIP = new possibleVIP(possVIPindex, possVIPdist, 0, ts.length-1);
				waitinglist.add(possVIP);
				dewlIndex = 0;
			}
			else
				dewlIndex = -1;
			
			while(dewlIndex >= 0) {
				possVIP = waitinglist.get(dewlIndex);
				newVIP = new VIPinfo(possVIP.index, possVIP.dist, VIPlist.size());
				VIPlist.add(newVIP);
				waitinglist.remove(dewlIndex);
				
				int startIndex = possVIP.leftVIPIndex;
				int endIndex = possVIP.rightVIPIndex;
				int middleIndex = possVIP.index;
				
				//find possible VIP from left subsequence divided by newVIP
				if (middleIndex > (startIndex+1)) {
					Dist = NormVDist(Arrays.copyOfRange(ts,startIndex,middleIndex+1));
					possVIPindex = startIndex+1;
					possVIPdist = Dist[1];
					for (int i = startIndex+2; i < middleIndex; ++i) {
						if (Dist[i-startIndex] > possVIPdist) {
							possVIPdist = Dist[i-startIndex];
							possVIPindex = i;
						}
					}
					if (possVIPdist > threshold) {
						possVIP = new possibleVIP(possVIPindex, possVIPdist, startIndex, middleIndex);
						waitinglist.add(possVIP);
					}
				}
				
				//find possible VIP from right subsequence divided by newVIP
				if (endIndex > (middleIndex+1)) {
					Dist = NormVDist(Arrays.copyOfRange(ts,middleIndex,endIndex+1));
					possVIPindex = middleIndex+1;
					possVIPdist = Dist[1];
					for (int i = middleIndex+2; i < endIndex; ++i) {
						if (Dist[i-middleIndex] > possVIPdist) {
							possVIPdist = Dist[i-middleIndex];
							possVIPindex = i;
						}
					}
					if (possVIPdist > threshold) {
						possVIP = new possibleVIP(possVIPindex, possVIPdist, middleIndex, endIndex);
						waitinglist.add(possVIP);
					}
				}
				
				if (waitinglist.size() == 0)
					dewlIndex = -1;
				else {
					dewlIndex = 0;
					tmpdist = waitinglist.get(dewlIndex).dist;
					for (int i = 1; i < waitinglist.size(); ++i) {
						if (waitinglist.get(i).dist > tmpdist) {
							tmpdist = waitinglist.get(i).dist;
							dewlIndex = i;
						}
					}
				}	
			}
		}
		VIPlist.sort(null);;
		return VIPlist;
	}
	
	//calculate the feature vectors (indicators) of VIPs
	public static double[][] getIndicators(double[] ts, List<VIPinfo> VIPlist) {
		final int dimension = 8;
		double[][] indicatorArray = new double[VIPlist.size()][dimension];
		double Xrange = ts.length - 1;
		int index;
		int VIPindex;
		int[] nearbyShapeInterval = {-2, -1, 1, 2};
		int[] nearbyPatternInterval = {-1, 1};
		
		for (int i = 0; i < VIPlist.size(); ++i) {
			//X
			if (Xrange > 0)
				indicatorArray[i][0] = VIPlist.get(i).index / Xrange;
			
			//Y
			indicatorArray[i][1] = ts[VIPlist.get(i).index];
			
			//nearby shape
			for (int j = 0; j < nearbyShapeInterval.length; ++j) {
				index = VIPlist.get(i).index + nearbyShapeInterval[j];
				if (index >= 0 && index < ts.length)
					indicatorArray[i][2+j] = (ts[index] - ts[VIPlist.get(i).index]) * Xrange;
				else
					indicatorArray[i][2+j] = 0;
			}
			
			//nearby pattern
			for (int j = 0; j < nearbyPatternInterval.length; ++j) {
				VIPindex = i + nearbyPatternInterval[j];
				if (VIPindex >= 0 && VIPindex < VIPlist.size()) {
					indicatorArray[i][2+nearbyShapeInterval.length+j] = 
							(ts[VIPlist.get(i).index] - ts[VIPindex]) / 
							((VIPlist.get(i).index - VIPindex) / Xrange);
				}
				else {
					indicatorArray[i][2+nearbyShapeInterval.length+j] = 0;
				}
			}
		}
		
		return indicatorArray;
	}
	
	//Euclidean Distance
	public static double eucDist(double[] ts1, double[] ts2) {
		assert ts1.length == ts2.length;
		
		double sum = 0;
		double diff = 0;
		for(int i = 0; i < ts1.length; ++i) {
			diff = ts1[i] - ts2[i];
			sum += diff * diff;
		}
		return Math.sqrt(sum);
	}
	
	//DTW distance
	public static double DTWDist(double[][] IndicatorsI, double[][]IndicatorsJ) {
		double[][] costMatrix = new double[IndicatorsI.length][IndicatorsJ.length];
		
		costMatrix[0][0] = eucDist(IndicatorsI[0], IndicatorsJ[0]);
		for (int j = 1; j < IndicatorsJ.length; ++j) {
			costMatrix[0][j] = costMatrix[0][j-1] + eucDist(IndicatorsI[0], IndicatorsJ[j]);
		}
		for (int i = 1; i < IndicatorsI.length; ++i) {
			costMatrix[i][0] = costMatrix[i-1][0] + eucDist(IndicatorsI[i],IndicatorsJ[0]);
			for (int j = 1; j < IndicatorsJ.length; ++j) {
				costMatrix[i][j] = Math.min(costMatrix[i-1][j-1], Math.min(costMatrix[i][j-1], costMatrix[i-1][j])) + eucDist(IndicatorsI[i],IndicatorsJ[j]);
			}
		}
		
		return costMatrix[IndicatorsI.length-1][IndicatorsJ.length-1];
	}
	
	@Override
	public double calculateDistance(double[] src, double[] tar) {
		// TODO Auto-generated method stub
		assert src.length == tar.length;
		
		List<VIPinfo> srcVIPlist;
		List<VIPinfo> tarVIPlist;
		double[][] srcIndicators;
		double[][] tarIndicators;
		double distance;
		
		src = preprocessing(src);
		tar = preprocessing(tar);
		
		srcVIPlist = getVIPs(src);
		tarVIPlist = getVIPs(tar);
		
		srcIndicators = getIndicators(src, srcVIPlist);
		tarIndicators = getIndicators(tar, tarVIPlist);
		
		distance = DTWDist(srcIndicators, tarIndicators) / Math.max(srcIndicators.length, tarIndicators.length);
		return distance;
	}
}
