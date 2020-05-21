package ie.gmit.sw.process;

import java.util.HashSet;
import java.util.Set;

import ie.gmit.sw.util.Utilities;

/* Gets user input, converts it to a vector and normalizes it for predictions */
public class TestInputProcess {
	/* Dirty static vars, will make everything nicer if theres time */
	public static double[] getVector;
	private static int kmers = 2;
	private static int vectorSize = 200;
	private static String text;

	/* Input string constructor, changed to work for files now */
	public TestInputProcess(String text, int kmers, int vectorSize) {
		TestInputProcess.kmers = kmers;
		TestInputProcess.vectorSize = vectorSize;
		TestInputProcess.text = text;

		process(TestInputProcess.text);
	}

	/* Process into a fixed size vector */
	private static void process(String text) {
		/* New vector each iteration */
		double[] vector = new double[vectorSize];

		/* Generate (n) kmers and loop (n) times */
		for (String kmer : genKmers(text.toLowerCase(), TestInputProcess.kmers)) {
			/* Increment the vector value at that index by 1 */
			vector[kmer.hashCode() % vector.length]++;
		}

		/* Normalize it */
		vector = Utilities.normalize(vector, 0, 1);

		TestInputProcess.getVector = vector;
	}

	/* Standard, for each processed line, pass it in here and return kmers */
	private static Set<String> genKmers(String text, int kmerSize) {
		Set<String> kmers = new HashSet<String>();

		for (int i = 0; i < text.length() - kmerSize; i++) {
			kmers.add(text.substring(i, i + kmerSize));
		}
		return kmers;
	}
}
