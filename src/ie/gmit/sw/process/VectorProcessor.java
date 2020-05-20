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
import ie.gmit.sw.util.Stopwatch;
import ie.gmit.sw.util.Utilities;

public class VectorProcessor {
	/* Dirty static vars, will make everything nicer if theres time */
	private static int kmers;
	private static int vectorSize;
	private static File data = new File("./data.csv");

	private DecimalFormat df = new DecimalFormat("###.###");
	Language[] langs = Language.values();

	public VectorProcessor(int kmers, int vectorSize) {
		VectorProcessor.kmers = kmers;
		VectorProcessor.vectorSize = vectorSize;

		Stopwatch timer = new Stopwatch();

		System.out.println("Checking to see if a csv file already exists .. ");

		/* Delete file if exists */
		data.delete();

		System.out.println("Processing Vector[" + vectorSize + "] with k-mers of size " + kmers);
		timer.start();

		try {
			parse();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		timer.stop();
		System.out.println(
				"Data file parsed, processed and hashed in " + timer.toString() + ". Saved to file " + data.getName());
	}

	private void parse() throws Throwable {
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

	/* Processes the language associated text */
	private void process(String line) throws Exception {
		/* New vector each iteration */
		double[] vector = new double[vectorSize];

		String[] record = line.split("@");

		/* If more than two @ symbols are found .. */
		if (record.length > 2) {
			return;
		}

		/* Anything before '@' is the language text, anything after is the language */
		String text = record[0].toLowerCase();
		String lang = record[1];

		/* Generate (n) kmers and loop (n) times */
		for (String kmer : genKmers(text, VectorProcessor.kmers)) {
			/* Increment the vector value at that index by 1 */
			vector[kmer.hashCode() % vector.length]++;
		}
		/* Normalize it */
		vector = Utilities.normalize(vector, 0, 1);

		/* File handlers */
		FileWriter fw = new FileWriter("./data.csv", true);
		BufferedWriter bw = new BufferedWriter(fw);

		/* Write the vector values to the file */
		for (int i = 0; i < vector.length; i++) {
			bw.write(df.format(vector[i]) + ",");
		}

		/* Loop 235 times, this will append the language as true / false (0/1) */
		for (int i = 0; i < langs.length; i++) {

			if (!langs[i].toString().equals(lang)) {
				/* If currently processed language doesn't match indexed lang .. */
				bw.write("0");
			} else {
				/* If indexed lang matches currently processed lang .. */
				bw.write("1");
			}

			/* If it's the last time looping, omit the comma */
			if (i != langs.length - 1) {
				bw.write(",");
			}
		}

		/* For each language being entered .. */
		bw.newLine();
		bw.close();
	}

	/* Standard, for each processed line, pass it in here and return kmers */
	private Set<String> genKmers(String text, int kmerSize) {
		Set<String> kmers = new HashSet<String>();

		for (int i = 0; i < text.length() - kmerSize; i++) {
			kmers.add(text.substring(i, i + kmerSize));
		}

		return kmers;
	}
}
