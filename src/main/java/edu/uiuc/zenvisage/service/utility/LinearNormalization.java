/**
 * 
 */
package edu.uiuc.zenvisage.service.utility;

/**
 *
 */
public class LinearNormalization implements Normalization {

	@Override
	public void normalize(double[] input) {
		// TODO Auto-generated method stub
		double max = input[0];
		double min = input[0];
		for (int i = 0; i < input.length; i++) {
			if (input[i] > max) {
				max = input[i];
			}
			if (input[i] < min) {
				min = input[i];
			}
		}
//		System.out.println("max:"+ Double.toString(max));
//		System.out.println("min:"+ Double.toString(min));
		if (max == min || (max-min<1)) {
			return;
		}
		for (int i = 0; i < input.length; i++) {
			input[i] = (input[i] - min) / (max - min) * 100;
		}
	}

}
