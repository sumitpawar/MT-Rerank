/**
 * 
 */
package com.jhu.cs.evaluation;

/**
 * @author sumit
 *
 */
public class METEOR {

	
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
		//match = match / ((double) len);
		return match;
	}
	
	private double evaluateSimple(String ref, String cand) {
		double alpha = 0.65;
		
		String refarr[] = ref.split("\\s+");
		double len = refarr.length;
		double candlen = cand.split("\\s+").length;
		double m1 = getGramMatch(1, cand, refarr);
		double p1 =  m1 / len;
		double r1 = m1 / candlen;
		double meteor =  p1 * r1 / (( (1 - alpha) * p1) + (r1 * alpha));
		return meteor;
	}
	
	private double evaluateSimple2(String ref, String cand) {
		double alpha = 0.65;
		String refarr[] = ref.split("\\s+");
		double len = refarr.length;
		double candlen = cand.split("\\s+").length;
		
		double meteor = 0.0;
		double p = 0.0;
		double r = 0.0;
		for(int j = 1; j < len - 1; j++) {
			double m1 = getGramMatch(j, cand, refarr);
			p +=  (j+50) * m1 / (len + j + 6);
			r +=  (j+50) * m1 / (candlen + j + 6);
			//meteor1 += p1 * r1 / (( (1 - alpha) * p1) + (r1 * alpha));
		}
		meteor += p * r / (( (1 - alpha) * p) + (r * alpha));
		return meteor;
	}
	
	private String getMatchAndChunk(int gram, String h, String[] ref) {
		double match = 0.0;
		double chunk = 1.0;
		String arr[] = new String[ref.length];
		int len = ref.length - (gram - 1);
		String[] har = h.split("\\s+");
		int c = 0;
		for(int j = 0; j < len; j++) {
			String rfr = "";
			for(int i = 0; i < gram ; i++) {
				rfr += " " + ref[i+j];
				rfr = rfr.trim();
			}
			if(h.contains(rfr)) {
				match++;
				for(int k = 0; k < har.length; k ++) {
					if(rfr.equalsIgnoreCase(har[k])) {
						arr[c] = k + "-" + j;
						k++;
						break;
					}
				}
			}
		}

		for(int i = 1; i < c; i ++) {
			String s1 = arr[i-1];
			String s2 = arr[i];
			String[] sp = s1.split("\\-");
			int c1 = Integer.parseInt(sp[0]);
			int r1 = Integer.parseInt(sp[1]);
			sp = s2.split("\\-");
			int c2 = Integer.parseInt(sp[0]);
			int r2 = Integer.parseInt(sp[1]);
			if((!(r1 == (r2 + 1) && c1 == (c2 + 1))
				|| (r1 == (r2 - 1) && c2 == (c2 - 1)) 
				|| (r1 == (r2 + 1 ) && c2 == (c2 - 1))
				|| (r1 == (r2 - 1) && c1 == (c2 + 1))) ) {
				chunk++;
			}
		}
		//match = match / ((double) len);
		return (match +"-"+ chunk);
	}
	
	private double evaluatePenalty(String ref, String cand) {
		double alpha = 0.65;
		double beta = 3.0;
		double gamma = 0.9;
		
		String refarr[] = ref.split("\\s+");
		double len = refarr.length;
		double candlen = cand.split("\\s+").length;
		String mnc[] = getMatchAndChunk(1, cand, refarr).split("\\-");
		double m = Double.parseDouble(mnc[0]);
		double c = Double.parseDouble(mnc[1]);
		double p =  m / len;
		double r = m / candlen;
		double pen = Math.pow((gamma * (c/m)), beta);
		double meteor =  (1 - pen) * ( p * r / (( (1 - alpha) * p) + (r * alpha)) );
		return meteor;
	}

	private double evaluatePenalty2(String ref, String cand) {
		double alpha = 0.78;
		double beta = 4.0;
		double gamma = 0.9;
		String refarr[] = ref.split("\\s+");
		double len = refarr.length;
		double candlen = cand.split("\\s+").length;
		
		double meteor = 0.0;
		double p = 0.0;
		double r = 0.0;
		double pen = 0.0;
		for(int j = 1; j < len - 1; j++) {
			String mnc[] = getMatchAndChunk(1, cand, refarr).split("\\-");
			double m1 = Double.parseDouble(mnc[0]);
			double c1 = Double.parseDouble(mnc[1]);
			p +=   m1 / (len);
			r +=   m1 / (candlen);
			pen += Math.pow((gamma * (c1/m1)), beta);
		}
		meteor +=  (1 - pen) * ( p * r / (( (1 - alpha) * p) + (r * alpha)) );
		
		return meteor;
	}

	public double computeMETEOR(final int choice, String ref, String cand) {
		double meteor = 0.0;
		switch(choice) {
			case 1: meteor = evaluateSimple(ref, cand);
				break;
			case 2: meteor = evaluateSimple2(ref, cand);
				break;
			case 3: meteor = evaluatePenalty(ref, cand);
				break;
			case 4: meteor = evaluatePenalty2(ref, cand);
				break;
			default: meteor = evaluateSimple(ref, cand);
		}
		return meteor;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
