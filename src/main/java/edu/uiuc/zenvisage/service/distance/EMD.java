package edu.uiuc.zenvisage.service.distance;

import java.util.ArrayList;

import scpsolver.constraints.Constraint;
import scpsolver.problems.LPSolution;
import scpsolver.problems.LPWizard;
import scpsolver.problems.LPWizardConstraint;

public class EMD {
	/**
	 * src[i][j] stands for the weight at src[i][j].
	 * Remove the 0 intensity grid first.
	 */
	public double calculateDistance(float[][] src, float[][] tar) {
		ArrayList<double[]> newSrc = new ArrayList();
		ArrayList<double[]> newTar = new ArrayList();
		for(int i = 0; i < src.length; i++) {
			for(int j = 0; j < src[0].length; j++) {
				if(src[i][j] > 0) newSrc.add(new double[]{i, j, src[i][j]});
				if(tar[i][j] > 0) newTar.add(new double[]{i, j, tar[i][j]});
			}
		}
		double[][] newSrc2 = new double[newSrc.size()][3];
		double[][] newTar2 = new double[newTar.size()][3];
		newSrc2 = newSrc.toArray(newSrc2);
		newTar2 = newTar.toArray(newTar2);
		
		return calculateDistanceHelper(newSrc2, newTar2);
	}
	

	/**
	 * src[i][0], src[i][1], src[i][3] stand for x,y,weight for src[i].
	 * Use L1 distance.
	 */
	public double calculateDistanceHelper(double[][] src, double[][] tar) {
		int m = src.length;
		int n = tar.length;
		//if(m == n) System.out.println(1);
		double[][] D = new double[m][n];
		double[][] F = new double[m][n];
		for(int i = 0; i < m; i++) {
			for(int j = 0; j < n; j++) {
				D[i][j] = Math.sqrt(Math.pow(src[i][0] - tar[j][0], 2) + Math.pow(src[i][1] - tar[j][1], 2));
			}
		}
					
		LPWizard lpw = new LPWizard(); 		
		for(int i = 0; i < m; i++) {
			for(int j = 0; j < n; j++) {
				//System.out.println("f_" + i + "_" + j);
				lpw.plus("f_" + i + "_" + j, D[i][j]);
			}
		}
		for(int i = 0; i < m; i++) {
			for(int j = 0; j < n; j++) {
				//System.out.println(i + " " + j);
				lpw.addConstraint("c_" + i + "_" + j,0,"<=").plus("f_" + i + "_" + j); 
			}
		}
		for(int i = 0; i < m; i++) {
			LPWizardConstraint c = lpw.addConstraint("c_i_" + i,src[i][2],">=");
			for(int j = 0; j < n; j++) {
				c.plus("f_" + i + "_" + j);
			}
		}
		for(int j = 0; j < n; j++) {
			LPWizardConstraint c = lpw.addConstraint("c_j_" + j,tar[j][2],">=");
			for(int i = 0; i < m; i++) {
				c.plus("f_" + i + "_" + j);
			}
		}
		LPWizardConstraint c_sum = lpw.addConstraint("c_sum",1,"==");
		for(int i = 0; i < m; i++) {
			for(int j = 0; j < n; j++) {			
				c_sum.plus("f_" + i + "_" + j); 
			}
		}
		
		System.out.println("before LP");
		double solution = lpw.solve().getObjectiveValue();			
		System.out.println("after LP: " + solution);
				
		return solution;
	}

}
