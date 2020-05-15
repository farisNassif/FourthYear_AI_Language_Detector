package ie.gmit.sw.process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import ie.gmit.sw.language.Language;
import ie.gmit.sw.util.Utilities;

public class VectorProcessor {
	double[] vector = new double[100];
	private DecimalFormat df = new DecimalFormat("###.###");
	private String ngram;
	private int ngramSize = 2;
	private double[] langInd = new double[235];
	Language[] langs = Language.values();

	/* Temp runner */
	public static void main(String[] args) throws Throwable {
		new VectorProcessor().parse();
	}

	public void parse() throws Throwable {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(new FileInputStream("./wili-2018-Small-11750-Edited.txt")));
		String line = null;

		while ((line = bufferedReader.readLine()) != null) {
			/* Process each line of the wili text file */
			process(line);

		}
		bufferedReader.close();
	}

	/* Processes a line */
	public void process(String line) throws Exception {
		String[] record = line.split("@");

		if (record.length > 2) {
			return;
		}

		String text = record[0].replaceAll("[^a-zA-Z]", " ").toLowerCase();
		String lang = record[1];

		for (int i = 0; i <= vector.length - 1; i++) {
			vector[i] = 0;
			ngram = text.substring(i, i + ngramSize);
			// System.out.print(ngram.hashCode() % vector.length + " - ");
			vector[i] = ngram.hashCode() % vector.length;
			vector[i]++;
		}
		vector = Utilities.normalize(vector, 0, 1);
		// System.out.println();

		FileWriter fw = new FileWriter("./data.csv", true);
		BufferedWriter bw = new BufferedWriter(fw);
		for (int i = 0; i < vector.length - 1; i++) {
			fw.append(df.format(vector[i]) + ", ");
			// System.out.println(df.format(vector[i] + ", "));
		}

	}
}
