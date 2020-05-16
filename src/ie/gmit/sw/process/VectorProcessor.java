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
	private static DecimalFormat df2 = new DecimalFormat("#.##");
	double one = 1.00001;
	double zero = 0.00001;
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

		/* Loop depending on the size of the vector */
		for (int i = 0; i < vector.length; i++) {
			/* Generate (n) kmers */
			kmer = text.substring(i, i + kmerSize);
			/* Populate vector of (n) size with (n) kmers (hashcode of) */
			vector[i] = kmer.hashCode() % vector.length;
		}

		/* Normalize it */
		vector = Utilities.normalize(vector, 0, 1);

		/* File handlers */
		FileWriter fw = new FileWriter("./data.txt", true);
		BufferedWriter bw = new BufferedWriter(fw);

		/* Write the vector values to the file */
		for (int i = 0; i < vector.length; i++) {
			bw.write(df.format(vector[i]) + ", ");
		}

		/* 235 values following the vector values */
		for (int i = 0; i < langs.length; i++) {

			/* If the language being processed matches the value of the language enum */
			if (!lang.equalsIgnoreCase(String.valueOf(langs[i]))) {
				/* 0.0 will be appended 234 times to the vector values */
				bw.write("0.0");
			} else {
				/* Matched language defined by 1.0 */
				bw.write("1.0");
			}

			/* If it isn't the last value written in a line .. */
			if (i != langs.length - 1) {
				bw.write(", ");
			}
		}

		/* For each language being entered .. */
		bw.newLine();
		bw.close();
	}
}
