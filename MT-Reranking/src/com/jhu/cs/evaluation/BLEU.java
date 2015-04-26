/**
 * 
 */
package com.jhu.cs.evaluation;

/**
 * @author sumit
 *
 */
public class BLEU {

	private double getGramMatch(int gram, String h, String[] ref) {
		double match = 0.0;
		int len = ref.length - (gram - 1);
		for(int j = 0; j < len; j++) {
			String rfr = "";
			for(int i = 0; i < gram ; i++) {
				rfr += ref[i+j];
			}
			if(h.contains(rfr)) {
				match++;
			}
		}
		match = match / ((double) len);
		return match;
	}
	
	public double computeBLEU(String ref, String cand) {
		String refarr[] = ref.split("\\s+");
		double reflen = ref.split("\\s+").length;
		double candlen = cand.split("\\s+").length;
		// Compute BP
		double bp = (candlen < reflen)?Math.exp(1 - (reflen/candlen)):1.0;
		
		double t = 0.0;
		for(int j = 1; j < reflen - 1; j++) {
			double val = getGramMatch(j, cand, refarr);
			t += (j+50) * ((val != 0.0)?Math.log( val) / (reflen-j):0.0);
		}
		return (double)(bp * Math.exp(t));
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
