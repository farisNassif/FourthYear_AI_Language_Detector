package ie.gmit.sw.neuralnetwork;

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
 * Helped a lot - https://s3.amazonaws.com/heatonresearch-books/free/Encog3Java-User.pdf 
 */
public class NeuralNetwork {
	DecimalFormat trainFormat = new DecimalFormat("#.######");

	/* Constructs, trains and returns a BasicNetwork */
	public BasicNetwork GenTrainNeuralNetwork(int save, int inputNodes) {
		int outputNodes = 235;
		int hiddenNodes = (int) Math.sqrt(inputNodes * outputNodes);
		double minError = 0.002;

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

		/* Some topology information .. */
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

		/* Neural Network Training config, ResilPropagation */
		ResilientPropagation train = new ResilientPropagation(network, folded);

		/* (5)k-fold cross validation */
		CrossValidationKFold kfold_train = new CrossValidationKFold(train, 5);

		/* To time training */
		Stopwatch trainingTimer = new Stopwatch();
		trainingTimer.start();

		System.out.println("Training will terminate at 600 seconds");

		/* Train */
		int iteration = 1;
		do {
			kfold_train.iteration();
			/* Some information about individual epochs */
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
		if (save == 1) {
			System.out.println("Neural Network saved as 'NeuralNetwork.nn");
			Utilities.saveNeuralNetwork(network, "./NeuralNetwork.nn");
		}

		/* Stop Encog running */
		Encog.getInstance().shutdown();
		return network;
	}

	public static void main(String[] args) {

	}

	/* Post input processing, takes the vector and NN to generate a prediction */
	public static void Predict(double[] input, BasicNetwork network) {
		Language[] languages = Language.values();
		BasicMLData data = new BasicMLData(input);
		data.setData(input);

		/* Generate a prediction based on input .. */
		MLData output = network.compute(data);

		/* Used to get current best result */
		double biggest = 0;
		int highest = 0;

		/* Loop 235 times for each 'output' or language */
		for (int i = 0; i < output.size(); i++) {
			/* If the i'th language looks better than any others seen before .. */
			if (output.getData(i) > biggest) {
				biggest = output.getData(i);
				System.out.println("New Best --> " + anguages[i].toString());
				/* The i'th language gets saved as the highest */
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
