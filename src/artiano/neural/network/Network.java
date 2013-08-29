/**
 * 
 */
package artiano.neural.network;



import artiano.core.structure.Matrix;
import artiano.neural.layer.Layer;
import artiano.neural.randomizer.Randomizer;

/**
 * <p>Description: base class of every neural network</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-14
 * @author (latest modification by Nano.Michael)
 * @function 
 * @since 1.0
 */
public abstract class Network {
	public final int version = 0x00000001;
	public Matrix outputs = null;
	public int inputCount = 0;
	public int outputCount = 0;
	public int layerCount = 0;
	public int epochs = 0;
	public Layer[] layers = null;
	
	/**
	 * constructor
	 * @param inputs dimension of input vector
	 * @param outputs dimension of output vector
	 * @param layers number of layers
	 */
	public Network(int inputs, int outputs, int layers){
		if (inputs <= 0 || outputs <= 0)
			throw new IllegalArgumentException("Parameters illegal.");
		inputCount = inputs;
		outputCount = outputs;
		layerCount = layers;
		this.layers = new Layer[layerCount];
	}
	
	/**
	 * randomize the network
	 * @param r specified randomizer
	 */
	public void randomize(Randomizer r){
		for (int i = 0; i < layers.length; i++)
			layers[i].randomize(r);
	}
	
	/**
	 * compute the output
	 * @param input input vector
	 * @return output vector
	 */
	public Matrix compute(Matrix input){
		Matrix v = input;
		for (int i = 0; i < layers.length; i++)
			v = layers[i].compute(v);
		outputs = v;
		return outputs;
	}
}
