package ie.gmit.sw.ui;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import org.encog.neural.networks.BasicNetwork;

import ie.gmit.sw.neuralnetwork.NeuralNetwork;
import ie.gmit.sw.process.TestInputProcess;
import ie.gmit.sw.process.VectorProcessor;
import ie.gmit.sw.util.Utilities;

/* Simple class that just handles the programs menus */
public class ConsoleMenu {
	Scanner scanner = new Scanner(System.in, "ISO_8859_1");
	BasicNetwork NN;
	VectorProcessor vectorProcessor;

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
				if (NN == null || vectorProcessor == null) {
					System.out.println("Gotta have a Neural Network loaded/created + a vector created to classify :(");
				} else {
					ClassifyMenu();
				}
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

	/* Neural Network Related Items */
	public void NeuralNetworkMenu() {
		/* Params for NN */
		int inputNodes = 0;
		int save = 0;
		int seconds = 0;

		int option;
		boolean loop = true;
		System.out.println("\n------ Neural Network Menu ------");
		System.out.println("Press 1 : Create and Train a Neural Network on the fly (And optional save)");
		System.out.println("Press 2 : Load a Previously Saved Neural Network to use for classification");
		System.out.println("Press 3 : Test a Neural Network against the test dataset");
		System.out.println("Press 9 : Go back to the Main Menu");
		option = scanner.nextInt();

		while (loop == true) {
			switch (option) {
			case 1: // Neural Network Creation
				if (new File("./data.csv").exists()) {
					System.out.println(
							"How many input nodes should the NN have? (Should be equal to vector size, 310 advised)");
					inputNodes = scanner.nextInt();
					System.out.println("Save: Press 1 | Otherwise : Press 0");
					save = scanner.nextInt();
					System.out.println("How long should the NN train for (Seconds). An input of 600 --> 10 minutes");
					seconds = scanner.nextInt();

					NN = new NeuralNetwork().GenTrainNeuralNetwork(save, inputNodes, seconds);
				} else {
					System.out.println(
							"Need to create the vector first to generate the csv file (No .csv file was found)");
				}

				break;
			case 2: // Load Neural Network
				if (new File("./NeuralNetwork.nn").exists()) {
					NN = Utilities.loadNeuralNetwork("./NeuralNetwork.nn");
					System.out.println("Neural Network loaded!");
				} else {
					System.out.println("No Neural Network was found, create and save one first.");
				}
				break;
			case 3: // Classify something
				if (NN == null || vectorProcessor == null) {
					System.out.println("Gotta have a Neural Network loaded/created + a vector created to test :(");
				} else {
					System.out.println(
							"How big was the vector you created? (Should be same as hidden nodes in the NN you created, 310 advised)");
					inputNodes = scanner.nextInt();

					new NeuralNetwork().TestNN(NN, inputNodes);
				}
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
				System.out.println("Press 1 : Create and Train a Neural Network on the fly (And optional save)");
				System.out.println("Press 2 : Load a Previously Saved Neural Network to use for classification");
				System.out.println("Press 3 : Test a Neural Network against the test dataset");
				System.out.println("Press 9 : Go back to the Main Menu");
				option = scanner.nextInt();
			}
		}
	}

	/* Vector Related Items */
	public void VectorMenu() {
		/* Params for vector Processor */
		int vectorSize;
		int kmers;

		int option;
		boolean loop = true;
		System.out.println("\n----- Vector / Ngram Menu -----");
		System.out.println("Press 1 : Create a new Vector & specify sizes");
		System.out.println("Press 9 : Go back to the Main Menu");
		option = scanner.nextInt();

		while (loop == true) {
			switch (option) {
			case 1: // Vector Hashing
				System.out.println("Specify Max Vector Size (Found best resutls with 310)");
				vectorSize = scanner.nextInt();

				System.out.println("K-mer size (1 / 2 / 3 / 4, Anything else may crash everything! Also 2 is advised)");
				kmers = scanner.nextInt();

				/* Can pass this around when classifying */
				vectorProcessor = new VectorProcessor(kmers, vectorSize);
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
				System.out.println("Press 1 : Create a new Vector & specify sizes");
				System.out.println("Press 9 : Go back to the Main Menu");
				option = scanner.nextInt();
			}
		}
	}

	/* Classification Related Items */
	public void ClassifyMenu() {
		/* Params for vector Processor */
		int option;
		boolean loop = true;
		String input;
		String fileInput = null;

		System.out.println("\n----- Classificaition Menu -----");
		System.out.println("Press 1 : Classify via a file");
		System.out.println("Press 2 : Classify via an input string");
		System.out.println("Press 9 : Go back to the Main Menu");
		option = scanner.nextInt();

		while (loop == true) {
			switch (option) {
			case 1: // Classification of a File
				System.out.println("Enter the name of the File. Example -> ./filename.txt");
				input = scanner.next();

				/* Gets the whole text as a single string, makes it easier */
				try {
					fileInput = new String(Files.readAllBytes(Paths.get(input)), StandardCharsets.ISO_8859_1);
				} catch (IOException e) {
					e.printStackTrace();
				}

				new TestInputProcess(fileInput, vectorProcessor.getKmers(), vectorProcessor.getVectorSize());
				NeuralNetwork.Predict(TestInputProcess.getVector, NN);
				break;
			case 2: // Classification of a String
				System.out.println("Enter a String of text to classify");
				input = scanner.nextLine();
				input = scanner.nextLine(); // Needed a second nextLine() here because of a phantom read, beware

				new TestInputProcess(input, vectorProcessor.getKmers(), vectorProcessor.getVectorSize());
				NeuralNetwork.Predict(TestInputProcess.getVector, NN);
				break;
			case 9:
				loop = false;
				System.out.println("Going Back ...");
				break;
			default:
				System.out.println("Invalid input, try again.");
			}

			if (option != 9) {
				System.out.println("\n----- Classificaition Menu -----");
				System.out.println("Press 1 : Classify via a file");
				System.out.println("Press 2 : Classify via an input string");
				System.out.println("Press 9 : Go back to the Main Menu");
				option = scanner.nextInt();
			}
		}
	}

}