package ie.gmit.sw.neuralnetwork;

import java.io.File;
import java.text.DecimalFormat;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationBipolarSteepenedSigmoid;
import org.encog.engine.network.activation.ActivationGaussian;
import org.encog.engine.network.activation.ActivationReLU;
import org.encog.engine.network.activation.ActivationSoftMax;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.mathutil.randomize.generate.GenerateRandom;
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
import ie.gmit.sw.util.Stopwatch;
import ie.gmit.sw.util.Utilities;

/* 
 * Class that creates the NN topology, trains, tests and saves it.
 * https://s3.amazonaws.com/heatonresearch-books/free/Encog3Java-User.pdf 
 */
public class NeuralNetwork {
	DecimalFormat trainFormat = new DecimalFormat("#.######");

	/* Constructs, trains and returns a BasicNetwork */
	public BasicNetwork GenTrainNeuralNetwork(int save, int inputNodes, int seconds) {
		int outputNodes = 235;
		int hiddenNodes = (int) Math.sqrt(inputNodes * outputNodes);

		System.out.println("Generating Neural Network ..");

		/* Neural Network Configuration */
		BasicNetwork network = new BasicNetwork();
		/* Input layer, amount of nodes are equal to vector size */
		network.addLayer(new BasicLayer(new ActivationReLU(), true, inputNodes));
		/* Single hidden layer, nodes equal to sqrt of (input * output) nodes */
		network.addLayer(new BasicLayer(new ActivationTANH(), true, hiddenNodes, 4135));
		/* Output layer, size equal to amount of languages to be classified (235) */
		network.addLayer(new BasicLayer(new ActivationSoftMax(), false, outputNodes));
		network.getStructure().finalizeStructure();
		network.reset();

		/* Some topology information .. */
		System.out.println("Neural Network Generated, Reporting Topology ..\n");
		System.out.println("[ReLU(" + inputNodes + ")]-->[HyperbolicTangent(" + hiddenNodes + ")]-->[ActivationSoftMax("
				+ outputNodes + ")]\n");

		/* Handle on the CSV file */
		DataSetCODEC dsc = new CSVDataCODEC(new File("./data.csv"), CSVFormat.DECIMAL_POINT, false, inputNodes,
				outputNodes, false);
		MemoryDataLoader mdl = new MemoryDataLoader(dsc);
		/* Configure the training set */
		MLDataSet mdlTrainingSet = mdl.external2Memory();
		/* "folds" the data into several equal (or nearly equal) datasets */
		FoldedDataSet folded = new FoldedDataSet(mdlTrainingSet);

		/* Neural Network Training config, ResilPropagation */
		ResilientPropagation train = new ResilientPropagation(network, folded, 0.02, 0.075);

		/* (5)k-fold cross validation */
		CrossValidationKFold kfold_train = new CrossValidationKFold(train, 5);

		/* To time training */
		Stopwatch trainingTimer = new Stopwatch();
		trainingTimer.start();

		System.out.println("Training will terminate at " + seconds + " seconds");

		/* Train */
		int iteration = 1;
		do {
			kfold_train.iteration();
			/* Some funky calculations to get accuracy as a % out of 100 */
			System.out.println("Iteration #" + iteration + " | Current Training Accuracy : "
					+ trainFormat.format(((1 - ((kfold_train.getError() * 100)))) *100) + "% | Target: 98" + "% | Time Elapsed "
					+ trainingTimer.elapsedSeconds() + " seconds");
			iteration++;
		} while (trainingTimer.elapsedSeconds() < seconds);

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
				System.out.println("New Best --> " + languages[i].toString());
				/* The i'th language gets saved as the highest */
				highest = i;
			}
		}
		System.out.println("Predicted language: " + languages[highest].toString());
	}

	//public static void main(String[] args) {
		//new NeuralNetwork().TestNN(Utilities.loadNeuralNetwork("NeuralNetwork.nn"), 310);
		//new NeuralNetwork().GenTrainNeuralNetwork(1, 310, 600);
	//}

	/* Test */
	public void TestNN(BasicNetwork network, int hiddenLayers) {
		/* 147 https://s3.amazonaws.com/heatonresearch-books/free/Encog3Java-User.pdf */
		/* Handle on the CSV file and testing data */
		DataSetCODEC dsc = new CSVDataCODEC(new File("./data.csv"), CSVFormat.DECIMAL_POINT, false, hiddenLayers, 235,
				false);
		MemoryDataLoader mdl = new MemoryDataLoader(dsc);
		MLDataSet testdata = mdl.external2Memory();

		/* Timing testing */
		Stopwatch timer = new Stopwatch();

		/* Used to calculate % of correct predictions */
		int totalValues = 0;
		int correct = 0;

		timer.start();
		System.out.println("Testing in progres ..");

		for (MLDataPair pair : testdata) {
			/* Keeps track of the best results from the prediction/actual set */
			int bestPredicted = 0;
			int bestActual = 0;

			/* Used to access prediction / actual data */
			MLData input = pair.getInput();
			MLData actualData = pair.getIdeal();
			MLData predictData = network.compute(input);

			/* Loop 235 times over predicted data, for each language .. */
			for (int i = 0; i < predictData.size(); i++) {
				/* If a new best was found in the prediction data, mark it as best .. */
				if ((predictData.getData(i) > predictData.getData(bestPredicted))) {
					bestPredicted = i;
				}
			}

			/* Loop 235 times over actual data, for each language .. */
			for (int j = 0; j < actualData.size(); j++) {
				/* 1 will indicate the correct language for the set */
				if (actualData.getData(j) == 1) {
					/* Get the index of the '1' out of the 235 columns */
					bestActual = j;
				}
			}

			/* If the predicted best and actual best are the same index .. */
			if (bestActual == bestPredicted) {
				correct++;
			}
			totalValues++;
		}

		timer.stop();

		/* Convert to double for display purposes */
		double percent = (double) correct / (double) totalValues;

		/* Some details about the testing */
		System.out.println("\n*Testing Finished in " + timer.toString());
		System.out.println("Total   - " + correct + "/" + totalValues);
		System.out.println("Correct - " + String.format("%.2f", percent * 100) + "%");
	}
}
