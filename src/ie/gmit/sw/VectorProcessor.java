package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

public class VectorProcessor {

	private double[] vector = new double[100];
	private DecimalFormat df = new DecimalFormat("###.###");

	private int n = 4;
	// private Language[] langs..

	public void go() throws Throwable {

		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(new File("./wili-2018-Small-11750-Edited.txt"))))) {
			String line = null;

			while ((br.readLine()) != null) {
				process(line);
			}

		} catch (Exception e) {

		}
	}

	public void process(String line) throws Exception {
		String[] record = line.split("@");

		if (record.length > 2) {
			return;
		}
		
		String text = record[0].toLowerCase();
		String lang = record[1];
		
		for (int i = 0; i < vector.length; i++) {
			
			// loop over text 
			// for each ngram
			// compute index as ngram.hashCode() % vector.length
			// vector[index] = current + 1;
			
			Utilities.normalize(vector, -1, 1);
			
			// write out the vector to a CSV file using df.format(number) for each vector index 
			
			// vector.length + num of labels in each row
			
		}
		
	}
}
