package ie.gmit.sw;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationReLU;
import org.encog.engine.network.activation.ActivationSoftMax;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.buffer.MemoryDataLoader;
import org.encog.ml.data.buffer.codec.CSVDataCODEC;
import org.encog.ml.data.buffer.codec.DataSetCODEC;
import org.encog.ml.data.folded.FoldedDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.cross.CrossValidationKFold;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.csv.CSVFormat;

import ie.gmit.sw.language.Language;
import ie.gmit.sw.util.Stopwatch;

/* 
 * Class that creates the NN topology, trains, tests and saves it.
 * Assisted with NN Development - https://s3.amazonaws.com/heatonresearch-books/free/Encog3Java-User.pdf 
 */
public class NeuralNetwork {

	public static void main(String[] args) {
		new NeuralNetwork();
	}

	public NeuralNetwork() {
		Language[] langs = Language.values();
		int inputs = 200;
		int outputs = 235;
		double minError = 0.0040; // While training, it would plateau at 0.004491100233148725

		/* Neural Network Configuration */
		BasicNetwork network = new BasicNetwork();
		/* Input layer, amount of nodes are equal to vector size */
		network.addLayer(new BasicLayer(new ActivationReLU(), true, inputs));
		/* Single hidden layer, nodes equal to sqrt of (input * output) nodes */
		network.addLayer(new BasicLayer(new ActivationReLU(), true, (int) Math.sqrt(inputs * outputs)));
		/* Output layer, size equal to amount of languages to be classified (235) */
		network.addLayer(new BasicLayer(new ActivationSoftMax(), false, outputs));
		network.getStructure().finalizeStructure();
		network.reset();

		/* Handle on the CSV file */
		DataSetCODEC dsc = new CSVDataCODEC(new File("./data.csv"), CSVFormat.DECIMAL_POINT, false, inputs, outputs,
				false);

		MemoryDataLoader mdl = new MemoryDataLoader(dsc);
		/* Configure the training set */
		MLDataSet mdlTrainingSet = mdl.external2Memory();
		/* "folds" the data into several equal (or nearly equal) datasets */
		FoldedDataSet folded = new FoldedDataSet(mdlTrainingSet);

		/* Neural Network Training configu */
		MLTrain train = new ResilientPropagation(network, folded);

		/* (5)k-fold cross validation */
		CrossValidationKFold cv = new CrossValidationKFold(train, 6);

		Stopwatch timer = new Stopwatch();

		/* Train */
		int epochs = 0;
		System.out.println("[INFO] Training...");
		timer.start();
		do {
			cv.iteration();
			epochs++;
			System.out.println("Epoch: " + epochs + " Error Rate: " + cv.getError() + " ...");

		} while (epochs < 2);

		/* Declare the end of training */
		cv.finishTraining();
		timer.stop();

		System.out.println(
				"Training Done in " + epochs + " epochs with error rate " + cv.getError() + " in " + timer.toString());

		int totalValues = 0;
		int correct = 0;
		double actual = 0;
		int ideal = 0;
		int res = 0;
		int i = 0;
		Language act;

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

		double[] in = { 0, 0, 0, 0, 0, 0, 0.5, 0.5, 0.5, 1, 0, 0, 0, 0.5, 0, 0, 0.5, 0, 0.5, 0, 0, 0, 0, 0.5, 0.5, 0, 0,
				0, 0, 0, 0, 0, 0, 0.5, 0, 0, 0, 0, 0.5, 0.5, 0, 0, 0, 0, 0, 0.5, 0, 0, 0, 0, 0, 0, 0.5, 0.5, 0, 0, 0.5,
				0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0.5, 0.5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0.5, 0,
				0.5, 0, 0, 0, 0, 0, 0.5, 0, 0, 0, 0.5 };

		double[] out = new double[235];

		/* Stop Encog running */
		Encog.getInstance().shutdown();
	}
}


