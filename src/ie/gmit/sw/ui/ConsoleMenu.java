package ie.gmit.sw.ui;

import java.util.Scanner;

import ie.gmit.sw.language.Language;

/* Simple class that just handles the program menu */
public class ConsoleMenu {
	Scanner scanner = new Scanner(System.in);

	/* Handles user input with a bog standard switch */
	public void MainMenu() {
		int option;
		boolean loop = true;
		System.out.println("Press 1 : Neural Network Options");
		System.out.println("Press 2 : Vector Hashing / Ngram Options");
		System.out.println("Press 3 : Classification Options");
		System.out.println("Press 9 : Quit");
		option = scanner.nextInt();

		while (loop == true) {
			switch (option) {
			case 1:
				System.out.println("Neural Network Opts");
				break;
			case 2:
				System.out.println("Vector Hashing Opts");
				break;
			case 3:
				System.out.println("Classification Opts");
				break;
			case 9:
				loop = false;
				System.out.println("Quitting Program ...");
				break;
			default:
				System.out.println("Invalid input, try again.");
			}

			if (option != 9) {
				System.out.println("Press 1 : Neural Network Options");
				System.out.println("Press 2 : Vector Hashing / Ngram Options");
				System.out.println("Press 3 : Classification Options");
				System.out.println("Press 9 : Quit");
				option = scanner.nextInt();
			}

		}
		scanner.close();
	}
}

/*
 * Each of the languages in the enum Language can be represented as a number
 * between 0 and 234. You can map the output of the neural network and the
 * training data label to / from the language using the following. Eg. index 0
 * maps to Achinese, i.e. langs[0].
 */
//Language[] langs = Language.values(); // Only call this once...
//for (int i = 0; i < langs.length; i++) {
//	System.out.println(i + "-->" + langs[i]);
//}