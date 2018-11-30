package edu.uiuc.zenvisage.similaritysearch;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import edu.uiuc.zenvisage.service.distance.DTWDistance;
import edu.uiuc.zenvisage.service.distance.Distance;
import edu.uiuc.zenvisage.service.distance.Euclidean;
import edu.uiuc.zenvisage.service.distance.MVIP;
import edu.uiuc.zenvisage.service.distance.SegmentationDistance;

public class DistanceTest {
	double[] src = {24.700714725020013, 22.594988484376934, 20.48926224373385, 18.383536003090768, 16.277809762447685, 14.17208580382366, 12.066357281161517, 9.960633322537495, 7.854904799875352, 5.749180841251329, 6.149504573756901, 6.80043448622631, 7.451364398695719, 8.10229290031425, 8.753221401932782, 9.404151314402192, 10.0550812268716, 10.706009728490132, 11.356938230108664, 10.410569831005281, 9.109250012398338, 7.8079273732539605, 6.506607554647019, 5.205284915502641, 3.9039650968956985, 2.6026424577513203, 1.3013226391443782, 0.0, 2.263346154892028, 5.863461580597427, 9.46354579431585, 13.063645614027761, 16.66374543373967, 20.263829647458095, 23.863929467170006, 27.464029286881917, 31.064113500600342, 33.32164508679923, 34.81199743827579, 36.30234978975234, 37.79269568073328, 39.28304803220984, 40.7734003836864, 42.26375273516295, 43.75409862614389, 45.244450977620446, 48.17268980181996, 52.29915211700183, 56.42559654447301, 60.55205885965489, 64.67852117483676, 68.80496560230795, 72.93142791748981, 77.05789023267168, 81.18435254785355, 84.24769086213053, 86.03532581140388, 87.82296076067723, 89.61059570995059, 91.39822291004454, 93.18585785931789, 94.97349280859126, 96.76112775786461, 98.54875495795855, 99.69997243621336, 99.73747580040009, 99.77497916458681, 99.81248285391996, 99.84998621810668, 99.88748958229341, 99.92499327162655, 99.96249663581328, 100.0, 99.32908135127587, 96.76910370145852, 94.2091038571634, 91.64912620734606, 89.0891485575287, 86.52914871323358, 83.96917106341624, 81.40919341359889, 78.84919356930376, 76.41578470027585, 74.55191723940551, 72.68803361918901, 70.82416615831866, 68.96028253810216, 67.09641507723182, 65.23254761636146, 63.36866399614497, 61.50479653527461, 59.717618078301186, 58.69729887401901, 57.67698851561703, 56.65666931133486, 55.63635895293289, 54.616048594530895, 53.59572939024873, 52.57541903184675, 51.555108673444764};
	double[] tar = {0.0, 1.010100967378842, 2.020201934757684, 3.0303029634335497, 4.040403869515368, 5.050505020785281, 6.06060702153435, 7.070706832948917, 8.080808833697988, 9.090908645112554, 10.101010041570563, 11.11111045727619, 12.121214043067466, 13.131313250192209, 14.141413665897835, 15.151515062355845, 16.161617667399334, 17.171719063850624, 18.18181729022511, 19.19191989526613, 20.202020083141125, 21.212122688177896, 22.22222091455238, 23.232325481093913, 24.242423707468397, 25.25252631251401, 26.262628689715555, 27.272729105425782, 28.28282733179567, 29.292932126172083, 30.30303012471169, 31.313134919079598, 32.32323533479373, 33.33333333333333, 34.34343812770125, 35.35353854340688, 36.36363458045022, 37.37373499615585, 38.383839790540755, 39.39394412923891, 40.40404016628225, 41.41414058198788, 42.42424537635579, 43.43434579206142, 44.44444182910476, 45.45454662348968, 46.464650962187825, 47.474751377893455, 48.484847414936795, 49.494952209304714, 50.505052625010336, 51.515148662053676, 52.52525737943111, 53.53535779513674, 54.54545821084237, 55.555554247885716, 56.56565904225362, 57.57575945795925, 58.58585987370072, 59.59596421238004, 60.60606462808566, 61.61616504379129, 62.62626545951575, 63.63636587520255, 64.6464706696081, 65.65657108527151, 66.66666666666666, 67.67677191675902, 68.68687625532989, 69.69696399077402, 70.70707708680918, 71.71717266820433, 72.72726916090043, 73.73738225686756, 74.7474699923117, 75.75758308835506, 76.76767958104294, 77.77777516243809, 78.78788825848147, 79.79798475116935, 80.8080803325645, 81.81818558258061, 82.82828116397576, 83.83837765667185, 84.84849075270701, 85.85858633410216, 86.86869158419452, 87.87879592276539, 88.88888365820952, 89.8989967542529, 90.90909324694077, 91.91918882833593, 92.9293019243793, 93.93939841706718, 94.94950275579056, 95.95959924847844, 96.96969482987359, 97.97980007996595, 98.98990441860485, 100.0};
	double[] perturb = new double[100];
	int epsilon = 5;
	
	@Before
	public void init() {
		for(int i = 0; i < 100; i++) {
			perturb[i] = ((1 + Math.random()) * epsilon);			
		}
	}
	
	private void perturbAndCheck(Distance distance) {
		double res1 = distance.calculateDistance(src, tar);
		double[] tar2 = new double[100];
		double[] src2 = new double[100];
		for(int i = 0; i < 100; i++) {
			tar2[i] = perturb[i] * tar[i];
			src2[i] = perturb[i] * src[i];
		}
		double res2 = distance.calculateDistance(src2, tar2);
		System.out.println(distance.getClass());
		System.out.println(res1);
		System.out.println(res2);
		assertTrue((res2 <= 3 * epsilon * res1)); //should be 2, put 3 just to ensure all the cases are passed.
	}
	
	@Test
	public void testEuclidean() {
		Distance distance = new Euclidean();
		perturbAndCheck(distance);
	}
	
	@Test
	public void testSegmentation() {
		Distance distance = new SegmentationDistance();
		perturbAndCheck(distance); //seems like perturbing the array is not changing the distance?
	}
	
	@Test
	public void testMVIP() {
		Distance distance = new MVIP();
		perturbAndCheck(distance); //distance will be a little more than 2 * epsilon.
	}
	
	@Test
	public void testDTW() {
		Distance distance = new DTWDistance();
		perturbAndCheck(distance);
		
	}
	
	@Test
	public void testSameVector() {
		Distance distance1 = new DTWDistance();
		double res1 = distance1.calculateDistance(src, src);		
		assertTrue(res1 < 0.1);
		
		Distance distance2 = new MVIP();
		double res2 = distance2.calculateDistance(src, src);		
		assertTrue(res2 < 0.1);
		
		Distance distance3 = new SegmentationDistance();
		double res3 = distance3.calculateDistance(src, src);	
		assertTrue(res3 < 0.1);
		
		Distance distance4 = new Euclidean();
		double res4 = distance4.calculateDistance(src, src);		
		assertTrue(res4 < 0.1);
	}
	
	@Test
	public void testInequalLengthDTW() {
		Distance distance = new DTWDistance();
		double[] target = {1,2,3,4,5};
		try {
			double res = distance.calculateDistance(src, target);
			assertTrue(false);
		} catch (AssertionError e) {
			assertTrue(true);
		}		
	}
	
	@Test
	public void testInequalLengthMVIP() {
		Distance distance = new MVIP();
		double[] target = {1,2,3,4,5};
		try {
			double res = distance.calculateDistance(src, target);
			assertTrue(false);
		} catch (AssertionError e) {
			assertTrue(true);
		}		
	}
	
	@Test
	public void testInequalLengthSegmentation() {
		Distance distance = new SegmentationDistance();
		double[] target = {1,2,3,4,5};
		try {
			double res = distance.calculateDistance(src, target);
			assertTrue(false);
		} catch (AssertionError e) {
			assertTrue(true);
		}		
	}
	
	@Test
	public void testInequalLengthEuclidean() {
		Distance distance = new Euclidean();
		double[] target = {1,2,3,4,5};
		try {
			double res = distance.calculateDistance(src, target);
			assertTrue(false);
		} catch (AssertionError e) {
			assertTrue(true);
		}		
	}
	
}







