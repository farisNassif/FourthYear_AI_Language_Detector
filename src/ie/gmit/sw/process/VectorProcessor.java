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
	private String kmer;
	private int kmerSize = 2;
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
		int count = 0;
		while ((line = bufferedReader.readLine()) != null && count != 5) {
			count++;
			/* For each line of text in the document .. */
			process(line);

		}
		bufferedReader.close();
	}

	/* Processes the line (language) */
	public void process(String line) throws Exception {
		String[] record = line.split("@");

		/* If more than two @ symbols are found .. */
		if (record.length > 2) {
			return;
		}

		/* Anything before '@' is the language text, anything after is the language */
		String text = record[0].toLowerCase();
		String lang = record[1];

		for (int i = 0; i <= vector.length - 1; i++) {
			/* Generate the kmer */

			kmer = text.substring(i, i + kmerSize);
			vector[i] = kmer.hashCode() % vector.length;
		}
		vector = Utilities.normalize(vector, 0, 1);

		for (int i = 0; i <= vector.length - 1; i++) {
			kmer = text.substring(i, i + kmerSize);
			System.out.println(lang + "[" + i + "] " + kmer + " - " + vector[i]);
		}

		FileWriter fw = new FileWriter("./data.txt", true);
		BufferedWriter bw = new BufferedWriter(fw);

		for (int i = 0; i < vector.length - kmerSize; i++) {
			bw.write(df.format(vector[i]) + ", ");
		}
		bw.write(lang);

		for (int i = 0; i < langs.length; i++) {
			if (lang.equalsIgnoreCase(String.valueOf(langs[i]))) {
				langInd[i] = 1;
			}
			// bw.write(langInd[i] + ", ");
			// langInd[i] = 0;

		}

		bw.newLine();
		bw.close();
	}
}
