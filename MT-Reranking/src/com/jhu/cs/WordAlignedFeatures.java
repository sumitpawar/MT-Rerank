/**
 * 
 */
package com.jhu.cs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author sumit
 *
 */
public class WordAlignedFeatures {

	private final String aligned = "./data/train.ru-en.align";
	private Map<String, Object[]> alignMap = new HashMap<String, Object[]>();
	/*
	 * Map contents: 
	 * Key: 	Russian-English String
	 * Value: [thetaId, distance, occurrence, thetaVal]
	 */
	
	public void loadData() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(aligned));
			int thetacount = 3;
			String line = "";
			while((line = br.readLine()) != null) {
				String[] lines = line.trim().split("\\s+\\|+\\s+");
				String[] rus = lines[0].trim().split("\\s+");
				String[] eng = lines[1].trim().split("\\s+");
				String[] align = lines[2].trim().split("\\s+");
				for(String a : align) {
					String[] as = a.trim().split("\\-");
					int r = Integer.parseInt(as[0]);
					int e = Integer.parseInt(as[1]);
					String rt = rus[r];
					String et = eng[e];
					String key = rt  + "#" +et;
					if(!alignMap.containsKey(key)) {
						thetacount++;
						Object[] obj = new Object[4];
						obj[0] = thetacount;
						obj[1] = r - e;
						obj[2] = 1;
						obj[3] = 0.0;
						alignMap.put(key, obj);
					}else {
						Object[] obj = alignMap.get(key);
						int diff = r - e + Integer.parseInt(obj[1].toString());
						obj[1] = diff;
						obj[2] = ((Integer.parseInt(obj[2].toString())) + 1);
					}
				}
			}
			br.close();
			
			for(Entry<String, Object[]> entry : alignMap.entrySet()) {
				String key = entry.getKey();
				Object[] obj = entry.getValue();
				obj[3] = Double.parseDouble(obj[1].toString()) / Double.parseDouble(obj[2].toString());
				alignMap.put(key, obj);
			}
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
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new WordAlignedFeatures().loadData();
	}

	/**
	 * @return the alignMap
	 */
	public Map<String, Object[]> getAlignMap() {
		return alignMap;
	}

	/**
	 * @param alignMap the alignMap to set
	 */
	public void setAlignMap(Map<String, Object[]> alignMap) {
		this.alignMap = alignMap;
	}

}
