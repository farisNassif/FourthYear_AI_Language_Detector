package ie.gmit.sw;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationBipolarSteepenedSigmoid;
import org.encog.engine.network.activation.ActivationReLU;
import org.encog.engine.network.activation.ActivationSoftMax;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.buffer.MemoryDataLoader;
import org.encog.ml.data.buffer.codec.CSVDataCODEC;
import org.encog.ml.data.buffer.codec.DataSetCODEC;
import org.encog.ml.data.folded.FoldedDataSet;
import org.encog.ml.train.strategy.Greedy;
import org.encog.ml.train.strategy.HybridStrategy;
import org.encog.ml.train.strategy.ResetStrategy;
import org.encog.ml.train.strategy.end.EndIterationsStrategy;
import org.encog.ml.train.strategy.end.EndMinutesStrategy;
import org.encog.ml.train.strategy.end.StoppingStrategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.cross.CrossValidationKFold;
import org.encog.neural.networks.training.propagation.manhattan.ManhattanPropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.csv.CSVFormat;
import org.encog.util.simple.EncogUtility;

import ie.gmit.sw.language.Language;
import ie.gmit.sw.process.TestInputProcess;
import ie.gmit.sw.util.Stopwatch;
import ie.gmit.sw.util.Utilities;

/* 
 * Class that creates the NN topology, trains, tests and saves it.
 * Helped with NN construction - https://s3.amazonaws.com/heatonresearch-books/free/Encog3Java-User.pdf 
 */
public class NeuralNetwork {
	public static int inputNodes;
	public static int save;
	DecimalFormat trainFormat = new DecimalFormat("#.######");

	public NeuralNetwork(int save, int inputNodes) {
		if (save != 1) {
			save = 0;
		}

		NeuralNetwork.inputNodes = inputNodes;
		NeuralNetwork.save = save;

		GenTrainNeuralNetwork();
	}

	public void GenTrainNeuralNetwork() {
		int outputNodes = 235;
		int hiddenNodes = (int) Math.sqrt(inputNodes * outputNodes);
		double minError = 0.0022;

		System.out.println("Generating Neural Network ..");

		/* Neural Network Configuration */
		BasicNetwork network = new BasicNetwork();
		/* Input layer, amount of nodes are equal to vector size */
		network.addLayer(new BasicLayer(new ActivationReLU(), true, inputNodes));
		/* Single hidden layer, nodes equal to sqrt of (input * output) nodes */
		network.addLayer(new BasicLayer(new ActivationTANH(), true, hiddenNodes, 125));
		/* Output layer, size equal to amount of languages to be classified (235) */
		network.addLayer(new BasicLayer(new ActivationSoftMax(), false, outputNodes));
		network.getStructure().finalizeStructure();
		network.reset();

		System.out.println("Neural Network Generated, Reporting Topology ..\n");
		System.out.println("[ReLU(" + inputNodes + ")]-->[BipolarSteepenedSigmoid(" + hiddenNodes
				+ ")]-->[ActivationSoftMax(" + outputNodes + ")]\n");

		/* Handle on the CSV file */
		DataSetCODEC dsc = new CSVDataCODEC(new File("./data.csv"), CSVFormat.DECIMAL_POINT, false, inputNodes,
				outputNodes, false);
		MemoryDataLoader mdl = new MemoryDataLoader(dsc);
		/* Configure the training set */
		MLDataSet mdlTrainingSet = mdl.external2Memory();
		/* "folds" the data into several equal (or nearly equal) datasets */
		FoldedDataSet folded = new FoldedDataSet(mdlTrainingSet);

		/* Neural Network Training config, manhattan prop with learning rate of 0.021 */
		ResilientPropagation train = new ResilientPropagation(network, folded);
		// ManhattanPropagation manh = new ManhattanPropagation(network, folded,
		// 0.00255);

		/* (5)k-fold cross validation */
		CrossValidationKFold kfold_train = new CrossValidationKFold(train, 5);

		/* Greedy strategy, if last iteration didn't improve training, discard it */

		Stopwatch trainingTimer = new Stopwatch();
		trainingTimer.start();

		System.out.println("Training will terminate at 600 seconds");

		/* Train */
		int iteration = 1;
		do {
			kfold_train.iteration();
			System.out.println(
					"Iteration #" + iteration + " | Current Error: " + trainFormat.format(kfold_train.getError() * 100)
							+ "% | Target Error: " + trainFormat.format(minError * 100) + "% | Time Elapsed "
							+ trainingTimer.elapsedSeconds() + " seconds");
			iteration++;
		} while (trainingTimer.elapsedSeconds() < 600);

		/* Declare the end of training */
		kfold_train.finishTraining();
		trainingTimer.stop();
		System.out.println("Training Done in " + trainingTimer.toString());

		/* Optional save */
		if (NeuralNetwork.save == 1) {
			System.out.println("Neural Network saved as 'NeuralNetwork.nn");
			Utilities.saveNeuralNetwork(network, "./NeuralNetwork.nn");
		}

		/* Stop Encog running */
		Encog.getInstance().shutdown();
	}

	public static void main(String[] args) {
		// new NeuralNetwork(1, 125);
		// BasicNetwork v = Utilities.loadNeuralNetwork("./NeuralNetwork.nn");
		/* Handle on the CSV file */

		String text = "";
		try {
			text = new String(Files.readAllBytes(Paths.get("./predict.txt")), StandardCharsets.ISO_8859_1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(text);

		Predict(new TestInputProcess(text, 2, 200).getVector);
	}

	public static void Predict(double[] input) {
		Language[] languages = Language.values();
		BasicMLData data = new BasicMLData(input);
		data.setData(input);
		BasicNetwork network = Utilities.loadNeuralNetwork("./GoodNeuralNetwork.nn");
		MLData output = network.compute(data);

		double biggest = 0;
		int highest = 0;

		for (int i = 0; i < output.size(); i++) {
			if (output.getData(i) > biggest) {
				biggest = output.getData(i);
				System.out.println(languages[i].toString());
				highest = i;
			}
		}

		System.out.println("Predicted language: " + languages[highest].toString());
	}

	/* Test */
	public void TestNN(MLDataPair mdlTrainingSet[]) {
		int totalValues = 0;
		int correct = 0;
		int i = 0;
		int ideal = 0;
		int res = 0;

		/* Test the data */
		for (MLDataPair data : mdlTrainingSet) {
			/*
			 * https://s3.amazonaws.com/heatonresearch-books/free/Encog3Java-User.pdf -
			 * Page147
			 */
			MLData output = network.compute(data.getInput());
			double[] preferred = data.getIdeal().getData(); //

			for (i = 0; i < preferred.length; i++) {
				if (preferred[i] == 1) {
					ideal = i;
				}
			}

			for (i = 0; i < output.getData().length; i++) {
				if (output.getData(i) == 1) {
					res = i;
					if (i == ideal) {
						correct++;
					}
				}

			}
			totalValues++;
		}

		DecimalFormat decimalFormat = new DecimalFormat("##.##");
		decimalFormat.setRoundingMode(RoundingMode.CEILING);

		double percent = (double) correct / (double) totalValues;

		System.out.println("\nINFO: Testing complete.");
		System.out.println("Correct: " + correct + "/" + totalValues);
		System.out.println("Accuracy: " + decimalFormat.format(percent * 100) + "%");

		// System.out.println("\nINFO: Testing complete.");
		// System.out.println("Correct: 8664" + "/" + totalValues);
		// System.out.println("Accuracy: 73.8052645%"); // +
		// decimalFormat.format(percent * 100) + "%");

		double[] in = { 0, 0, 0, 0, 0, 0, 0.5, 0.5, 0.5, 1, 0, 0, 0, 0.5, 0, 0, 0.5, 0, 0.5, 0, 0, 0, 0, 0.5, 0.5, 0, 0,
				0, 0, 0, 0, 0, 0, 0.5, 0, 0, 0, 0, 0.5, 0.5, 0, 0, 0, 0, 0, 0.5, 0, 0, 0, 0, 0, 0, 0.5, 0.5, 0, 0, 0.5,
				0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0.5, 0.5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0.5, 0,
				0.5, 0, 0, 0, 0, 0, 0.5, 0, 0, 0, 0.5 };

		double[] out = new double[235];
	}

}
