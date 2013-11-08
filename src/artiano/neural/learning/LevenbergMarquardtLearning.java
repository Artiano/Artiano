/**
 * LevenbergMarquardtLearning.java
 */
package artiano.neural.learning;

import artiano.core.structure.Matrix;
import artiano.math.algebra.CholeskyDecomposition;
import artiano.neural.actfun.ActivationFunction;
import artiano.neural.layer.ActivationLayer;
import artiano.neural.layer.Layer;
import artiano.neural.network.ActivationNetwork;
import artiano.neural.neuron.ActivationNeuron;
import artiano.neural.neuron.Neuron;

/**
 * <p>Description: Implementation of Levenberg-Marquart learning algorithm</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-30
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class LevenbergMarquardtLearning implements SupervisedNeuralLearning {

	protected ActivationNetwork network = null;
	protected Matrix jacobian = null;
	protected Matrix hessian = null;
	protected Matrix error = null;
	protected int numParameters = 0;
	protected double regularizationFactor = 0.01;
	
	protected int blockSize = 1;
	protected int finalBlockSize = 0;
	protected double sumSquaredError = 0.;
	
	protected boolean isAllocate = false;
	
	/**
	 * constructor
	 * @param network - activation-network
	 */
	public LevenbergMarquardtLearning(ActivationNetwork network){
		this(network, 1);
	}
	
	/**
	 * constructor
	 * @param network - activation network
	 * @param blockSize - block size to handle
	 */
	public LevenbergMarquardtLearning(ActivationNetwork network, int blockSize){
		this.network = network;
		this.blockSize = blockSize >= 1? blockSize: 1;
		numberWeights();
		allocateMemory();
	}
	
	/**
	 * allocate memory for the parameters
	 */
	protected void allocateMemory(){
		jacobian = new Matrix(blockSize*network.outputCount, numParameters);
		error = new Matrix(blockSize*network.outputCount, 1);
	}
	
	/**
	 * absolute row index of jacobian matrix according to block index and output index
	 * @param blockIdx - block index
	 * @param outputIdx - output index
	 * @return - the absolute row
	 */
	public int absRow(int blockIdx, int outputIdx){
		return blockIdx*network.outputCount + outputIdx;
	}
	
	/**
	 * absolute column of jacobian matrix, it is calculated according to the layer index, neuron index and 
	 * weight index on the network
	 * @param layerIdx - layer index
	 * @param neuronIdx - neuron index
	 * @param weightIdx - weight index
	 * @return
	 */
	public int absCol(int layerIdx, int neuronIdx, int weightIdx){
		int y;
		int previosLayerWeightsCount = numPreviousWig[layerIdx];
		y = previosLayerWeightsCount + neuronIdx * (network.layers[layerIdx].inputsCount+1) + weightIdx;
		return y;
	}
	
	//for calculate the column index on the network
	protected int[] numPreviousWig = null;
	/**
	 * calculate the number of weights on the network
	 */
	public void numberWeights(){
		numPreviousWig = new int[network.layerCount+1];
		numPreviousWig[0] = 0;
		for (int i = 0; i < network.layerCount; i++){
			Layer layer = network.layers[i];
			for (int j = 0; j < layer.neuronsCount; j++){ 
				Neuron neuron = layer.neurons[j];
				//add bias
				numParameters += neuron.weights.length + 1;
			}
			numPreviousWig[i+1] = numParameters;
		}
	}
	
	/**
	 * compute the jacobian matrix
	 * @param inputs - inputs of a block
	 * @param targetOutputs - outputs of a block
	 * @return - sum of squared errors
	 */
	protected double computeJacobian(Matrix[] inputs, Matrix[] targetOutputs){
		jacobian.clear();
		double squareError = 0.;
		//bi: block index
		for (int bi = 0; bi < inputs.length; bi++){
			Matrix input = inputs[bi];
			Matrix targetOutput = targetOutputs[bi];
			network.compute(input);
			//oi: output index
			for (int oi = 0; oi < network.outputCount; oi++){
				squareError += computeRowErrorTerm(targetOutput.at(oi), bi, oi);
				computeRowDerivatives(input, bi, oi);
			}
		}
		return squareError / 2.;
	}
	
	/**
	 * compute the a row of partial derivatives of jacobian matrix
	 * @param input - input matrix
	 * @param blockIdx - block index
	 * @param outputIdx - output index
	 */
	protected void computeRowDerivatives(Matrix input, int blockIdx, int outputIdx){
		//row index of jacobian matrix
		final int jacobianRow = absRow(blockIdx, outputIdx);
		//column index of jacobian matrix
		int jacobianCol = 0;
		Matrix v = input;
		//for each hidden layers
		int li;
		ActivationLayer layer;
		for (li = 0; li < network.layerCount - 1; li++){
			layer = (ActivationLayer) network.layers[li];
			for (int ni = 0; ni < layer.neuronsCount; ni++){
				ActivationNeuron neuron = (ActivationNeuron) layer.neurons[ni];
				int wi;
				for (wi = 0; wi < neuron.weights.length; wi++){
					jacobianCol = absCol(li, ni, wi);
					double derivative = v.at(wi)*neuron.e;
					jacobian.set(jacobianRow, jacobianCol, derivative);
				}
				//also set derivative to bias
				jacobianCol = absCol(li, ni, wi);
				jacobian.set(jacobianRow, jacobianCol, neuron.e);
			}
			v = layer.outputs;
		}
		//output neuron
		ActivationNeuron outputNeuron = (ActivationNeuron)network.layers[li].neurons[outputIdx];
		int wi;
		for (wi = 0; wi < outputNeuron.weights.length; wi++){
			jacobianCol = absCol(li, outputIdx, wi);
			double derivative = v.at(wi)*outputNeuron.e;
			jacobian.set(jacobianRow, jacobianCol, derivative);
		}
		jacobianCol = absCol(li, outputIdx, wi);
		jacobian.set(jacobianRow, jacobianCol, outputNeuron.e);
	}
	
	/**
	 * compute a row of error term of the network
	 * @param targetOutput - target output
	 * @param blockIdx - block index
	 * @param outputIdx - output index
	 * @return - squared error
	 */
	protected double computeRowErrorTerm(double targetOutput, int blockIdx, int outputIdx){
		//step 1. calculate the error term of output layer first, and store it to member e of the neurons
		ActivationLayer outputLayer = (ActivationLayer) network.layers[network.layerCount -1];
		ActivationNeuron outputNeuron = (ActivationNeuron) outputLayer.neurons[outputIdx];
		ActivationFunction fun = outputLayer.actfun;
		//calculate the error of output neuron and store it to error matrix
		double e = targetOutput - outputNeuron.output;
		double se = e*e;
		error.set(absRow(blockIdx, outputIdx), e);
		//calculate the error term of the specified output neuron and store it
		outputNeuron.e = -fun.derivativeByY(outputNeuron.output);
		
		//step 2. calculate the error term of the hidden layers
		//direct downstream of current neuron
		Neuron[] nextNeurons = {outputNeuron};
		ActivationLayer layer;
		for (int li = network.layerCount-2; li >= 0; li--){
			//current layer
			layer = (ActivationLayer) network.layers[li];
			fun = layer.actfun;
			//current neuron
			ActivationNeuron neuron;
			for (int ni = 0; ni < layer.neuronsCount; ni++){
				//sum error of direct downstream neurons
				double sum = 0.;
				for (int k = 0; k < nextNeurons.length; k++)
					sum += nextNeurons[k].e*nextNeurons[k].weights[ni];
				//calculate error term of current neuron
				neuron = (ActivationNeuron) layer.neurons[ni];
				//!notice: i'm wrong here
				neuron.e = fun.derivativeByY(neuron.output)*sum;
			}
			//next direct downstream neurons
			nextNeurons = layer.neurons;
		}
		return se;
	}
	
	/**
	 * compute the inversion of hessian matrix
	 * @return - success or not
	 */
	protected boolean computeHessian(){
		hessian = jacobian.t().multiply(jacobian);
		for (int i = 0; i < hessian.rows(); i++)
			hessian.add(i, i, regularizationFactor);
		CholeskyDecomposition decomposition = new CholeskyDecomposition(hessian);
		if (!decomposition.isDefinite())
			return false;
		Matrix t = decomposition.inverseOfL(false);
		hessian = t.t().multiply(t);
		return true;
	}
	
	/**
	 * update the network
	 */
	protected void update(){
		Matrix t = jacobian.t().multiply(error);
		Matrix updater = hessian.multiply(t);
		//Matrix updater = jacobian.t().multiply(error);
		ActivationLayer layer;
		//update
		for (int li = 0; li < network.layerCount; li++){
			layer = (ActivationLayer) network.layers[li];
			ActivationNeuron neuron;
			for (int ni = 0; ni < layer.neuronsCount; ni++){
				neuron = (ActivationNeuron) layer.neurons[ni];
				int wi;
				for (wi = 0; wi < neuron.weights.length; wi++)
					neuron.weights[wi] -= updater.at(absCol(li, ni, wi));
				neuron.bias -= updater.at(absCol(li, ni, wi));
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see artiano.neural.learning.SupervisedNeuralLearning#runEpoch(artiano.core.structure.Matrix[], artiano.core.structure.Matrix[])
	 */
	@Override
	public double runEpoch(Matrix inputs, Matrix targetOutputs) {
		finalBlockSize = inputs.rows() % blockSize;
		int blocks = inputs.rows() / blockSize;
		blocks = finalBlockSize == 0? blocks: blocks+1;
		int blockIdx = 0;
		sumSquaredError = 0.;
		for (int i = 0; i < blocks; i++){
			//reallocate memory if needed
			if (i == blocks-1){
				if (finalBlockSize != 0){
					jacobian = new Matrix(finalBlockSize*network.outputCount, numParameters);
					error = new Matrix(finalBlockSize*network.outputCount, 1);
					blockSize = finalBlockSize;
				}
			}
			//allocate blocks
			Matrix[] inputBlock = new Matrix[blockSize];
			Matrix[] outputBlock = new Matrix[blockSize];
			for (int j = 0; j < blockSize; j++, blockIdx++){
				inputBlock[j] = inputs.row(blockIdx);
				outputBlock[j] = targetOutputs.row(blockIdx);
			}
			sumSquaredError += computeJacobian(inputBlock, outputBlock);
			if (computeHessian()){
				update();
			}
		}
		network.epochs++;
		network.squreError = sumSquaredError;
		return sumSquaredError;
	}

	/* (non-Javadoc)
	 * @see artiano.neural.learning.SupervisedNeuralLearning#run(artiano.core.structure.Matrix, artiano.core.structure.Matrix)
	 */
	@Override
	public double run(Matrix input, Matrix targetOutput) {
		throw new UnsupportedOperationException("LenvenbergMarquartLearning, running in batch mode only.");
	}
	
}
