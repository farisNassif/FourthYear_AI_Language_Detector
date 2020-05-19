package ie.gmit.sw;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationBipolarSteepenedSigmoid;
import org.encog.engine.network.activation.ActivationReLU;
import org.encog.engine.network.activation.ActivationSoftMax;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.buffer.MemoryDataLoader;
import org.encog.ml.data.buffer.codec.CSVDataCODEC;
import org.encog.ml.data.buffer.codec.DataSetCODEC;
import org.encog.ml.data.folded.FoldedDataSet;
import org.encog.ml.train.strategy.Greedy;
import org.encog.ml.train.strategy.ResetStrategy;
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
import ie.gmit.sw.util.Stopwatch;
import ie.gmit.sw.util.Utilities;

/* 
 * Class that creates the NN topology, trains, tests and saves it.
 * Assisted with NN Development - https://s3.amazonaws.com/heatonresearch-books/free/Encog3Java-User.pdf 
 */
public class NeuralNetwork {
	public static int inputNodes;
	public static int save;

	public static void main(String[] args) {
		new NeuralNetwork(0, 100);
	}

	public NeuralNetwork(int save, int inputNodes) {
		if (save != 1) {
			save = 0;
		}

		NeuralNetwork.inputNodes = inputNodes;
		NeuralNetwork.save = save;

		GenNeuralNetwork();
	}

	public void GenNeuralNetwork() {
		Language[] langs = Language.values();
		int outputNodes = 235;
		double minError = 0.0002;

		System.out.println("Generating Neural Network ..");

		/* Neural Network Configuration */
		BasicNetwork network = new BasicNetwork();
		/* Input layer, amount of nodes are equal to vector size */
		network.addLayer(new BasicLayer(new ActivationReLU(), true, inputNodes));
		/* Single hidden layer, nodes equal to sqrt of (input * output) nodes */
		network.addLayer(new BasicLayer(new ActivationBipolarSteepenedSigmoid(), true,
				(int) Math.sqrt(inputNodes * outputNodes)));
		/* Output layer, size equal to amount of languages to be classified (235) */
		network.addLayer(new BasicLayer(new ActivationSoftMax(), false, outputNodes));
		network.getStructure().finalizeStructure();
		network.reset();

		System.out.println("Neural Network Generated, Reporting Topology ..");
		System.out.println("[ReLU(" + inputNodes + ")]-->[BipolarSteepenedSigmoid("
				+ (int) Math.sqrt(inputNodes * outputNodes) + ")]-->[ActivationSoftMax(" + outputNodes + ")]\n");
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
		/* (5)k-fold cross validation */
		new CrossValidationKFold(train, 5);

		/* Greedy strategy, if last iteration didn't improve training, discard it */

		Stopwatch timer = new Stopwatch();

		timer.start();

		System.out.println("Training will terminate at 600, X iterations or when no improvement is observed");
		/* Train */
		EncogUtility.trainToError(train, minError);

		/* Declare the end of training */
		timer.stop();
		System.out.println("Training Done in " + timer.toString());

		if (NeuralNetwork.save == 1) {
			System.out.println("Neural Network saved as 'NeuralNetwork.nn");
			Utilities.saveNeuralNetwork(network, "./NeuralNetwork.nn");
		}

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

		/* Stop Encog running */
		Encog.getInstance().shutdown();
	}
}
