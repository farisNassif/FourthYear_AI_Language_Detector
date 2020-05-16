package ie.gmit.sw.process;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* Contains functionality to parse text into kmers */
public class Parser {

	/* Returns the wili data line my line as an array */
	public String[] ParseFile() throws Throwable {
		/* Need to change this, definition of quick and dirty */
		String[] lines = new String[11750];
		String line = null;
		/* Will hold all lines of text within a file */

		/* Get a handle on the wili text file */
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(new FileInputStream("./wili-2018-Small-11750-Edited.txt")));

		int i = 0;
		/* Get a handle on each line of the file */
		while ((line = bufferedReader.readLine()) != null) {
			lines[i] = line;
			i++;
		}

		/* Processing is finished, close */
		bufferedReader.close();

		/* Return array of lines */
		return lines;
	}

	public Set<String> ToNgrams() {
		Set<String> ignoreWords = new HashSet<String>();
		
		
		return ignoreWords;
	}
	public static void main(String[] args) throws Throwable {
		String[] f = new Parser().ParseFile();

		for (int i = 0; i < f.length; i++) {
			System.out.println(f[i]);
		}
	}
}
