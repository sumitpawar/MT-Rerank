/**
 * 
 */
package com.jhu.cs;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sumit
 *
 */
public class NBests {

	private String source;
	
	private int id;
	
	private List<NBests.Candidate> candidates = new ArrayList<NBests.Candidate>();
	
	private List<NBests.Sample> samples = new ArrayList<NBests.Sample>();
	
	public class Candidate {
		private String candidate;
		
		private double BLEU;

		private double h[] = new double[3];
		
		/**
		 * @return the candidate
		 */
		public String getCandidate() {
			return candidate;
		}

		/**
		 * @param candidate the candidate to set
		 */
		public void setCandidate(String candidate) {
			this.candidate = candidate;
		}

		/**
		 * @return the bLEU
		 */
		public double getBLEU() {
			return BLEU;
		}

		/**
		 * @param bLEU the bLEU to set
		 */
		public void setBLEU(double bLEU) {
			BLEU = bLEU;
		}

		/**
		 * @return the h
		 */
		public double[] getH() {
			return h;
		}

		/**
		 * @param h the h to set
		 */
		public void setH(double h[]) {
			this.h = h;
		}
	}
	
	public class Sample {
		private Candidate c1;
		
		private Candidate c2;

		public void add(Candidate c1, Candidate c2) {
			this.c1 = c1;
			this.c2 = c2;
		}
		/**
		 * @return the c1
		 */
		public Candidate getC1() {
			return c1;
		}

		/**
		 * @param c1 the c1 to set
		 */
		public void setC1(Candidate c1) {
			this.c1 = c1;
		}

		/**
		 * @return the c2
		 */
		public Candidate getC2() {
			return c2;
		}

		/**
		 * @param c2 the c2 to set
		 */
		public void setC2(Candidate c2) {
			this.c2 = c2;
		}
	}
	
	/**
	 * @return the reference
	 */
	public String getSource() {
		return source;
	}
	/**
	 * @param reference the reference to set
	 */
	public void setSource(String source) {
		this.source = source;
	}
	/**
	 * @return the candidates
	 */
	public List<NBests.Candidate> getCandidates() {
		return candidates;
	}
	/**
	 * @param candidates the candidates to set
	 */
	public void setCandidates(List<NBests.Candidate> candidates) {
		this.candidates = candidates;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	public Candidate getCandidate() {
		return new Candidate();
	}

	public Sample getSample() {
		return new Sample();
	}

	/**
	 * @return the samples
	 */
	public List<NBests.Sample> getSamples() {
		return samples;
	}
	/**
	 * @param samples the samples to set
	 */
	public void setSamples(List<NBests.Sample> samples) {
		this.samples = samples;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
