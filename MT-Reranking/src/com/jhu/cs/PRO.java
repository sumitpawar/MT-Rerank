/**
 * 
 */
package com.jhu.cs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.jhu.cs.NBests.Candidate;
import com.jhu.cs.NBests.Sample;
import com.jhu.cs.evaluation.BLEU;

/**
 * @author sumit
 *
 */
public class PRO {
	
	private final String trainsrc = "./data/train.src";
	private final String train100Best = "./data/train.100best";
	private final String trainref = "./data/train.ref";
	private final String dev100Best = "./data/dev+test.100best";
	private final String devsrc = "./data/dev+test.src";
	private double theta[] = new double[4];
	private List<NBests> nblist = new ArrayList<NBests>();
	
	private int tau = 10000;
	private double alpha = 0.1;
	private int xi = 100;
	private double eta = 0.1;
	private int epochs = 10;
	
	private void initializeTheta() {
		theta = new double[4];
		theta[0] = 1.0;
		theta[1] = 1.0;
		theta[2] = 1.0;
		theta[3] = 1.0; // length
	}

	/**
	 * @return the theta
	 */
	public double[] getTheta() {
		return theta;
	}

	/**
	 * @param theta the theta to set
	 */
	public void setTheta(double theta[]) {
		this.theta = theta;
	}
	
	private void readFiles() {
		try {
			BufferedReader br1 = new BufferedReader(new FileReader(trainsrc));
			BufferedReader br2 = new BufferedReader(new FileReader(train100Best));
			BLEU bleu = new BLEU();
			String line = "";
			while ((line = br1.readLine()) != null){
				String[] lines = line.split("\\s+\\|+\\s+");
				NBests nb = new NBests();
				nb.setId(Integer.parseInt(lines[0]));
				nb.setSource(lines[1]);
				double l = lines[1].trim().split("\\s+").length;
				List<NBests.Candidate> candidates = new ArrayList<NBests.Candidate>();
				for(int i = 0; i < 100; i++) {
					Candidate cand = nb.getCandidate();
					String line2 = br2.readLine();
					String[] lines2 = line2.split("\\s+\\|+\\s+");
					int id = Integer.parseInt(lines2[0]);
					// Check for Ids
					if(id != nb.getId()) {
						System.err.println("Error occurred: ids do not match up.");
						break;
					}
					cand.setCandidate(lines2[1]);
					String features[] = lines2[2].trim().split("\\s+");
					double h[] = new double[4];
					for(int j = 0; j < features.length; j++) {
						String fea = features[j];
						double val = Double.parseDouble(fea.split("=")[1]);
						h[j] = val;
					}
					double rl = lines2[1].trim().split("\\s+").length;
					h[3] = (l - rl)/l;
					cand.setH(h);
					//compute BLEU score
					double bs = bleu.computeBLEU(nb.getSource(), cand.getCandidate());
					cand.setBLEU(bs);
					candidates.add(cand);
				}
//				System.out.println("adding : " + candidates.size());
				nb.setCandidates(candidates);
				nblist.add(nb);
			}
			br1.close();
			br2.close();
		} catch (FileNotFoundException e) {
			System.err.println("FileNotFoundException: ");
			System.err.println(System.getProperty("user.dir"));
			e.printStackTrace();
		}catch (IOException e) {
			System.err.println("IOException: ");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getSample(NBests nb) {
		Random random = new Random();
		List<Sample> samples = new ArrayList<Sample>();
		for(int i = 0;i < tau; i++) {
			// Randomly choose 2 items from nbests list
			int c1 = random.nextInt(100);
			int c2 = random.nextInt(100);			
			Candidate cand1 = nb.getCandidates().get(c1);
			Candidate cand2 = nb.getCandidates().get(c2);
			if(Math.abs(cand1.getBLEU() - cand2.getBLEU()) > alpha) {
				Sample sample = nb.getSample();
				if(cand1.getBLEU() > cand2.getBLEU()) {
					sample.add(cand1, cand2);
				}else {
					sample.add(cand2, cand1);
				}
				samples.add(sample);
			}
		}
		
		Comparator<Sample> BLEU_DIFF_ASC = new Comparator<NBests.Sample>() {
			@Override
			public int compare(Sample s1, Sample s2) {
				Double val1 = s1.getC1().getBLEU() - s1.getC2().getBLEU();
				Double val2 = s2.getC1().getBLEU() - s2.getC2().getBLEU();
				return val1.compareTo(val2);
			}
		};
		Collections.sort(samples, BLEU_DIFF_ASC);
		int sample_size = Math.min(xi, samples.size());
//		System.out.println("sample_size : " + sample_size );
		List<Sample> topxi = samples.subList(0, sample_size);
		nb.setSamples(topxi);
		//return samples;
	}
	
	private void learnTheta() {
		initializeTheta();
		for(int i = 0; i < epochs; i++) {
			int mistakes = 0;
			for(NBests nb : nblist) {
				/*List<Sample> samples = */
				getSample(nb);
//				System.out.println("sample size : " + nb.getSamples().size());
				for(Sample sample : nb.getSamples()) {
					double h1[] = sample.getC1().getH();
					double h2[] = sample.getC2().getH();
					double val1 = (theta[0] * h1[0]) + (theta[1] * h1[1]) + (theta[2] * h1[2]) + (theta[3] * h1[3]);
					double val2 = (theta[0] * h2[0]) + (theta[1] * h2[1]) + (theta[2] * h2[2]) + (theta[3] * h2[3]);
					// Update theta
					if(val1 <= val2) {
						mistakes++;
						theta[0] += eta * (h1[0] - h2[0]);
						theta[1] += eta * (h1[1] - h2[1]);
						theta[2] += eta * (h1[2] - h2[2]);
						theta[3] += eta * (h1[3] - h2[3]);
					}
				}
			}
//			System.out.println("Total mistakes at round " + i + ": " + mistakes);
		}
	}

	private List<String> evaluateAccuracyTrain(){
		List<String> results = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(trainref));
			double accuracy = 0.0;
			for(NBests nb : nblist) {
				String bestc = "";
				double bestv = Double.NEGATIVE_INFINITY;
				for(Candidate c : nb.getCandidates()) {
					double h[] = c.getH();
					double val = (theta[0] * h[0]) + (theta[1] * h[1]) + (theta[2] * h[2]);
					if(val > bestv) {
						bestv = val;
						bestc = c.getCandidate();
					}
				}
				String ref = br.readLine();
				if(ref.equals(bestc)) {
					accuracy++;
				}
				results.add(bestc);
//				System.out.println(bestc);
			}
//			System.out.println("Accuracy on training data: " + ((double)accuracy/(double)nblist.size()));
			br.close();
		} catch (FileNotFoundException e) {
			System.err.println("FileNotFoundException: ");
			System.err.println(System.getProperty("user.dir"));
			e.printStackTrace();
		}catch (IOException e) {
			System.err.println("IOException: ");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}
	
	private List<String> evaluateAccuracyTest(){
		List<String> results = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(dev100Best));
			BufferedReader br2 = new BufferedReader(new FileReader(devsrc));
			int count = 0;
			String line = "";
			String best = "";	
			double bestScore = Double.NEGATIVE_INFINITY;
			String ref = br2.readLine();
			double l = ref.trim().split("\\s+").length;
			while((line = br.readLine()) != null) {
				count++;
				String[] lines = line.split("\\s+\\|+\\s+");
				String c = lines[1];
				String features[] = lines[2].trim().split("\\s+");
				double h[] = new double[4];
				for(int j = 0; j < features.length; j++) {
					String fea = features[j];
					double val = Double.parseDouble(fea.split("=")[1]);
					h[j] = val;
				}
				double rl = c.trim().split("\\s+").length;
				h[3] = (l - rl)/l;
				double val = (h[0] * theta[0]) + (h[1] * theta[1]) + (h[2] * theta[2]) + (h[3] * theta[3]);
				if(val > bestScore) {
					best = c;
					bestScore = val;
				}
				if(count%100 == 0) {
					System.out.println(best);
					results.add(best);
					best = "";	
					bestScore = Double.NEGATIVE_INFINITY;
					ref = br2.readLine();
					if(null != ref) {
						l = ref.trim().split("\\s+").length;
					}
				}
			}
			br.close();
			br2.close();
		}catch (FileNotFoundException e) {
			System.err.println("FileNotFoundException: ");
			System.err.println(System.getProperty("user.dir"));
			e.printStackTrace();
		}catch (IOException e) {
			System.err.println("IOException: ");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}
	
	private void rerank() {
		readFiles();
//		System.out.println("nblist.size(): " + nblist.size());
		learnTheta();
//		System.out.println("theta: [" + theta[0] + ", " + theta[1] + ", " + theta[2] + " ]");
		evaluateAccuracyTrain();
//		theta[0] = 1.0;theta[1] = 0.5;theta[2] = 0.5;
		evaluateAccuracyTest();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new PRO().rerank();
	}

}
