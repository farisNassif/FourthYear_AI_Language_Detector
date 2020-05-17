package ie.gmit.sw;

import java.io.File;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationBipolarSteepenedSigmoid;
import org.encog.engine.network.activation.ActivationReLU;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
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

public class NeuralNetwork {

	public static void main(String[] args) {
		new NeuralNetwork();
	}

	public NeuralNetwork() {
		int inputs = 100;
		int outputs = 235;
		double minError = 0.02;

		/* Neural Network Configuration */
		BasicNetwork network = new BasicNetwork();
		/* Input layer, amount of nodes are equal to vector size */
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, inputs));
		/* Single hidden layer, nodes equal to sqrt of (input * output) nodes */
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, (int) Math.sqrt(inputs * outputs)));
		/* Output layer, size equal to amount of languages to be classified (235) */
		network.addLayer(new BasicLayer(new ActivationReLU(), true, outputs));
		network.getStructure().finalizeStructure();
		network.reset();

		/* Handle on the CSV file */
		DataSetCODEC dsc = new CSVDataCODEC(new File("./data.txt"), CSVFormat.DECIMAL_POINT, false, inputs, outputs,
				false);

		MemoryDataLoader mdl = new MemoryDataLoader(dsc);
		/* Configure the training set */
		MLDataSet mdlTrainingSet = mdl.external2Memory();
		/* "folds" the data into several equal (or nearly equal) datasets */
		FoldedDataSet folded = new FoldedDataSet(mdlTrainingSet);

		/* Neural Network Training configu */
		MLTrain train = new ResilientPropagation(network, folded);
		
		/* 5-fold cross validation */
		CrossValidationKFold cv = new CrossValidationKFold(train, 5);

		/* Train */
		int epoch = 1;
		System.out.println("[INFO] Training...");
		do {
			cv.iteration();
			epoch++;
		} while (cv.getError() > minError);
		/* Declare the end of training */
		cv.finishTraining();

		System.out.println("Training Done in " + epoch + " epochs with error " + cv.getError());

		/* Stop Encog running */
		Encog.getInstance().shutdown();
	}
}