package ie.gmit.sw.ui;

import java.util.Scanner;

import ie.gmit.sw.language.Language;

/* Simple class that just handles the programs menus */
public class ConsoleMenu {
	Scanner scanner = new Scanner(System.in);

	public void MainMenu() {
		int option;
		boolean loop = true;
		System.out.println("---------- Main Menu ----------");
		System.out.println("Press 1 : Neural Network Options");
		System.out.println("Press 2 : Vector Hashing / Ngram Options");
		System.out.println("Press 3 : Classification Options");
		System.out.println("Press 9 : Quit");
		option = scanner.nextInt();

		while (loop == true) {
			switch (option) {
			case 1:
				NeuralNetworkMenu();
				break;
			case 2:
				VectorMenu();
				break;
			case 3:
				System.out.println("Classification Opts");
				break;
			case 9:
				loop = false;
				System.out.println("\n*Quitting Program ...");
				break;
			default:
				System.out.println("Invalid input, try again.");
			}

			if (option != 9) {
				System.out.println("\n---------- Main Menu ----------");
				System.out.println("Press 1 : Neural Network Options");
				System.out.println("Press 2 : Vector Hashing / Ngram Options");
				System.out.println("Press 3 : Classification Options");
				System.out.println("Press 9 : Quit");
				option = scanner.nextInt();
			}

		}
		scanner.close();
	}

	public void NeuralNetworkMenu() {
		int option;
		boolean loop = true;
		System.out.println("\n------ Neural Network Menu ------");
		System.out.println("Press 1 : Neural Network Option 1");
		System.out.println("Press 2 : Neural Network Option 2");
		System.out.println("Press 3 : Neural Network Option 3");
		System.out.println("Press 9 : Go back to the Main Menu");
		option = scanner.nextInt();

		while (loop == true) {
			switch (option) {
			case 1:
				System.out.println("Neural Network Option 1");
				break;
			case 2:
				System.out.println("Neural Network Option 2");
				break;
			case 3:
				System.out.println("Neural Network Option 3");
				break;
			case 9:
				loop = false;
				System.out.println("Going Back ...");
				break;
			default:
				System.out.println("Invalid input, try again.");
			}

			if (option != 9) {
				System.out.println("\n------ Neural Network Menu ------");
				System.out.println("Press 1 : Neural Network Option 1");
				System.out.println("Press 2 : Neural Network Option 2");
				System.out.println("Press 3 : Neural Network Option 3");
				System.out.println("Press 9 : Go back to the Main Menu");
				option = scanner.nextInt();
			}
		}
	}

	public void VectorMenu() {
		int option;
		boolean loop = true;
		System.out.println("\n----- Vector / Ngram Menu -----");
		System.out.println("Press 1 : Vector / Ngram Option 1");
		System.out.println("Press 2 : Vector / Ngram Option 2");
		System.out.println("Press 3 : Vector / Ngram Option 3");
		System.out.println("Press 9 : Go back to the Main Menu");
		option = scanner.nextInt();

		while (loop == true) {
			switch (option) {
			case 1:
				System.out.println("Vector / Ngram Option 1");
				break;
			case 2:
				System.out.println("Vector / Ngram Option 2");
				break;
			case 3:
				System.out.println("Vector / Ngram Option 3");
				break;
			case 9:
				loop = false;
				System.out.println("Going Back ...");
				break;
			default:
				System.out.println("Invalid input, try again.");
			}

			if (option != 9) {
				System.out.println("\n----- Vector / Ngram Menu -----");
				System.out.println("Press 1 : Vector / Ngram Option 1");
				System.out.println("Press 2 : Vector / Ngram Option 2");
				System.out.println("Press 3 : Vector / Ngram Option 3");
				System.out.println("Press 9 : Go back to the Main Menu");
				option = scanner.nextInt();
			}
		}
	}
}