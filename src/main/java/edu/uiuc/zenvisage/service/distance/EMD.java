package edu.uiuc.zenvisage.service.distance;

import scpsolver.constraints.Constraint;
import scpsolver.problems.LPSolution;
import scpsolver.problems.LPWizard;
import scpsolver.problems.LPWizardConstraint;

public class EMD {

	/**
	 * Assumes that the weight for each point is 1.
	 * src[i][0] and src[i][1] stand for x,y for src[i].
	 * Use Euclidean for distance.
	 */
	public double calculateDistance(double[][] src, double[][] tar) {
		int m = src.length;
		int n = tar.length;
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
			LPWizardConstraint c = lpw.addConstraint("c_i_" + i,1,">=");
			for(int j = 0; j < n; j++) {
				c.plus("f_" + i + "_" + j);
			}
		}
		for(int j = 0; j < n; j++) {
			LPWizardConstraint c = lpw.addConstraint("c_j_" + j,1,">=");
			for(int i = 0; i < m; i++) {
				c.plus("f_" + i + "_" + j);
			}
		}
		LPWizardConstraint c_sum = lpw.addConstraint("c_sum",Math.min(m, n),"==");
		for(int i = 0; i < m; i++) {
			for(int j = 0; j < n; j++) {			
				c_sum.plus("f_" + i + "_" + j); 
			}
		}
		
		System.out.println("before LP");
		double solution = lpw.solve().getObjectiveValue();			
		System.out.println("after LP: " + (solution / Math.min(m, n)));
				
		return solution / Math.min(m, n);
	}

}
