package ie.gmit.sw.ui;

import java.util.Scanner;

import ie.gmit.sw.language.Language;

/* Simple class that just handles the program menu */
public class ConsoleMenu {
	Scanner scanner = new Scanner(System.in);

	/* Handles user input with a bog standard switch */
	public void MainMenu() {
		int option;

		System.out.println("Press 1 to Loop");
		System.out.println("Press 9 to Quit");
		option = scanner.nextInt();

		switch (option) {
		case 1:
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
			MainMenu();

			break;
		case 9:
			System.out.println("Exiting ..");
			break;
		default:
			System.out.println("Invalid input, try again.");
			MainMenu();
		}
		scanner.close();
	}
}
