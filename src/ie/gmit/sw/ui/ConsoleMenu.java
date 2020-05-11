package ie.gmit.sw.ui;

import java.util.Scanner;

import ie.gmit.sw.Language;

/* Simple class that just handles the program menu */
public class ConsoleMenu {
	Scanner scanner = new Scanner(System.in);
	
	public void DisplayMenu() {
		/*
		 * Each of the languages in the enum Language can be represented as a number
		 * between 0 and 234. You can map the output of the neural network and the
		 * training data label to / from the language using the following. Eg. index 0
		 * maps to Achinese, i.e. langs[0].
		 */
		Language[] langs = Language.values(); // Only call this once...
		for (int i = 0; i < langs.length; i++) {
			System.out.println(i + "-->" + langs[i]);
		}
	}

	public void MainMenu() {
		int option;
	
		do {
			System.out.println("Press 1 to Loop");
			System.out.println("Press 9 to Quit");
			option = scanner.nextInt();

		} while (option != 9);
		scanner.close();
	}
}