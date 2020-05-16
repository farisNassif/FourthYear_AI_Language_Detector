package ie.gmit.sw.process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

import ie.gmit.sw.language.Language;
import ie.gmit.sw.util.Utilities;

public class VectorProcessor {

	private DecimalFormat df = new DecimalFormat("###.###");
	private String kmer;
	private int kmerSize = 2;
	Language[] langs = Language.values();
	static File data = new File("./data.txt");

	/* Temp runner */
	public static void main(String[] args) throws Throwable {
		if (data.delete()) {
			System.out.println("Deleted the file: " + data.getName());
		} else {
			System.out.println("No file existed to delete");
		}
		new VectorProcessor().parse();
	}

	public void parse() throws Throwable {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(new FileInputStream("./wili-2018-Small-11750-Edited.txt")));
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			/* For each line of text in the document .. */
			process(line);
		}
		/* Processing is finished, close */
		bufferedReader.close();
	}

	/* Processes the line (language) */
	public void process(String line) throws Exception {
		/* New vector each iteration */
		double[] vector = new double[100];

		String[] record = line.split("@");

		/* If more than two @ symbols are found .. */
		if (record.length > 2) {
			return;
		}

		/* Anything before '@' is the language text, anything after is the language */
		String text = record[0].toLowerCase();
		String lang = record[1];

		/* Generate (n) kmers and loop (n) times */
		for (String kmer : genKmers(text)) {
			/* Vector index will be frequency of that hashcode, increment if found again */
			vector[kmer.hashCode() % vector.length]++;
		}

		/* Normalize it */
		vector = Utilities.normalize(vector, 0, 1);

		/* File handlers */
		FileWriter fw = new FileWriter("./data.txt", true);
		BufferedWriter bw = new BufferedWriter(fw);

		/* Write the vector values to the file */
		for (int i = 0; i < vector.length; i++) {
			bw.write(df.format(vector[i]) + ",");
		}

		/* Append the language */
		bw.write(lang);

		/* For each language being entered .. */
		bw.newLine();
		bw.close();
	}

	public Set<String> genKmers(String text) {
		int kmerSize = 3;
		Set<String> kmers = new HashSet<String>();

		for (int i = 0; i < text.length() - kmerSize; i++) {
			kmers.add(text.substring(i, i + kmerSize));
		}

		return kmers;
	}

}
