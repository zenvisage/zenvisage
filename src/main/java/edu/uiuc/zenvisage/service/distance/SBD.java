package edu.uiuc.zenvisage.service.distance;

import org.apache.commons.math3.transform.*;
import org.apache.commons.math3.complex.Complex;

/*
 * @author Changfeng
 * This is the metric in k-Shape ("k-Shape: Efficient and Accurate Clustering of Time Series")
 * In order to be consistent with the source code of k-Shape, (almost) all the names of variables and methods are same as k-Shape's source code in Matlab.
 * You can find k-Shape's source code here: http://www.cs.columbia.edu/%7Ejopa/kshape.html
 */

public class SBD implements Distance {
	
	//next power of 2 which is larger than a
	public static int nextpow2(final int a)
    {
		/*
        int b = 1;
        int power = 0;
        while (b < a)
        {
            b = b << 1;
            power++;
        }
        return power;
        */
		return (32 - Integer.numberOfLeadingZeros(a - 1));
    }
	
	//fft with padding zeros. fftlength is the length after padding zeros
	public static Complex[] fft(double[] x, int fftlength) {
		assert x.length <= fftlength;
		assert (fftlength & (fftlength - 1)) == 0; // fftlength must be a power of 2
		
		double[] xPaddedZeros = new double[fftlength];
		for (int i = 0; i < fftlength; ++i) {
			if (i < x.length)
				xPaddedZeros[i] = x[i];
			else
				xPaddedZeros[i] = 0;
		}
		FastFourierTransformer fftTrans = new FastFourierTransformer(DftNormalization.STANDARD);
		Complex[] fftResult = fftTrans.transform(xPaddedZeros, TransformType.FORWARD);
		
		return fftResult;
	}
	
	public static double[] ifft(Complex[] x) {
		assert (x.length & (x.length - 1)) == 0; // x.length is a power of 2
		
		FastFourierTransformer ifftTrans = new FastFourierTransformer(DftNormalization.STANDARD);
		Complex[] ifftResult = ifftTrans.transform(x, TransformType.INVERSE);
		double[] result = new double[ifftResult.length];
		for (int i = 0; i < ifftResult.length; ++i) {
			result[i] = ifftResult[i].getReal();
		}
		
		return result;
	}

	//calculate the conjugate values of a complex array
	public static Complex[] conj(Complex[] x) {
		Complex[] result = new Complex[x.length];
		for(int i = 0; i < x.length; ++i) {
			result[i] = x[i].conjugate();
		}
		return result;
	}
	
	//equal to ".*" in Matlab
	public static Complex[] arrayMultiply(Complex[] x, Complex[] y) {
		assert x.length == y.length;
		
		Complex[] result = new Complex[x.length];
		for (int i = 0; i < x.length; ++i) {
			result[i] = x[i].multiply(y[i]);
		}
		return result;
	}
	
	public static double norm(double[] x) {
		double sum = 0;
		for (int i = 0; i < x.length; ++i) {
			sum += (x[i] * x[i]);
		}
		return Math.sqrt(sum);
	}
	
	//Normalized cross-correlation with the coefficient normalization
	public static double[] NCCc(double[] x, double[] y) {
		int len = x.length;
		
		//fast method to calculate nextpow2 value
		int fftlength = 1;
        while (fftlength < (2 * len - 1))
        {
            fftlength = fftlength << 1;
        }
		//int fftlength = (int)(Math.pow(2, nextpow2(2 * len - 1)));
		
		double[] r = ifft(arrayMultiply(fft(x, fftlength), conj(fft(y,fftlength))));
		
		double[] new_r = new double[2 * len - 1];
		for (int i = 0; i < (len - 1); ++i) {
			new_r[i] = r[r.length -len + 1 + i];
		}
		for (int i = 0; i < len; ++i) {
			new_r[len - 1 + i] = r[i];
		}
		
		//Normalization
		double[] cc_sequence = new double[new_r.length];
		for (int i = 0; i < cc_sequence.length; ++i) {
			cc_sequence[i] = new_r[i] / (norm(x) * norm(y));
		}
		
		return cc_sequence;
	}
	
	@Override
	public double calculateDistance(double[] src, double[] tar) {
		// TODO Auto-generated method stub
		assert src.length == tar.length;
		
		double[] X1 = NCCc(src, tar); // X1 is the NCCc sequence
		
		//Get the maximum value in X1
		double m = X1[0];
		for (int i = 1; i < X1.length; ++i) {
			if (X1[i] > m)
				m = X1[i];
		}
		
		return (1-m);
	}
}
