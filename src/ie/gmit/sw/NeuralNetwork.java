package ie.gmit.sw;

import java.io.File;

import org.encog.engine.network.activation.ActivationReLU;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationSoftMax;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.buffer.MemoryDataLoader;
import org.encog.ml.data.buffer.codec.CSVDataCODEC;
import org.encog.ml.data.buffer.codec.DataSetCODEC;
import org.encog.ml.train.strategy.RequiredImprovementStrategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.csv.CSVFormat;

public class NeuralNetwork {
	public static void main(String[] args) {
		new NeuralNetwork();
	}

	public NeuralNetwork() {
		int inputs = 100;
		int outputs = 235;
		double minError = 0.1;

		// Configure the neural network topology.
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, inputs));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 330));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, outputs));
		network.getStructure().finalizeStructure();
		network.reset();

		DataSetCODEC dsc = new CSVDataCODEC(new File("./data.txt"), CSVFormat.DECIMAL_POINT, false, inputs, outputs, false);
		MemoryDataLoader mdl = new MemoryDataLoader(dsc);
		MLDataSet trainingSet = mdl.external2Memory();

		/*
		 * First-order optimization algorithm, supervised learning in feedforward ANN's
		 */
		ResilientPropagation train = new ResilientPropagation(network, trainingSet);

		/* Train */
		int epoch = 1;
		System.out.println("[INFO] Training...");
		do {
			train.iteration();
			epoch++;
		} while (train.getError() > minError);
		train.finishTraining();
		System.out.println("Training Done in " + epoch + " epochs with error " + train.getError());
	}
}