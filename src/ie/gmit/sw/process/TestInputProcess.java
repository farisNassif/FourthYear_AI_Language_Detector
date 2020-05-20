package ie.gmit.sw.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/* Gets input, converts it to a vector and normalizes it for predictions */
public class TestInputProcess {
	/* Dirty static vars, will make everything nicer if theres time */
	private static int kmers;
	private static int vectorSize;
	private static String text;
	private static File inputFile;

	/* Input string constructor */
	public TestInputProcess(String text, int kmers, int vectorSize) {
		TestInputProcess.kmers = kmers;
		TestInputProcess.vectorSize = vectorSize;
		TestInputProcess.text = text;

		/* Delete file if exists */
		testData.delete();

		process();
	}

	/* Input file constructor */
	public TestInputProcess(File inputFile, int kmers, int vectorSize) {
		TestInputProcess.kmers = kmers;
		TestInputProcess.vectorSize = vectorSize;
		TestInputProcess.inputFile = inputFile;

		/* Delete file if exists */
		testData.delete();

		try {
			parse();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void parse() throws Throwable {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(TestInputProcess.inputFile)));
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			/* For each line of text in the document .. */
			process(line);
		}
		/* Processing is finished, close */
		bufferedReader.close();
	}

	private void process() {

	}
}
