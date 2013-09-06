/**
 * 
 */
package artiano.neural.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import artiano.core.structure.Matrix;
import artiano.neural.layer.Layer;
import artiano.randomizer.Randomizer;

/**
 * <p>Description: base class of every neural network</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-14
 * @author (latest modification by Nano.Michael)
 * @function 
 * @since 1.0
 */
public abstract class Network implements Serializable {
	
	private static final long serialVersionUID = 1L;
	transient public Matrix outputs = null;
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
	 * save network to specified file
	 * @param filename - specified file name
	 * @throws IOException
	 */
	public void save(String filename) throws IOException{
		FileOutputStream fos = new FileOutputStream(new File(filename));
		save(fos);
	}
	
	/**
	 * save network to specified output stream
	 * @param os - output stream
	 * @throws IOException
	 */
	public void save(OutputStream os) throws IOException{
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(this);
		os.flush();
		os.close();
		oos.flush();
		oos.close();
	}
	
	/**
	 * load network from specified file
	 * @param filename - specified file name
	 * @return - a object of network
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static Network load(String filename) throws ClassNotFoundException, IOException{
		FileInputStream fis = new FileInputStream(new File(filename));
		return load(fis);
	}
	
	/**
	 * load network from specified input stream
	 * @param is - input stream
	 * @return - a object of network
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Network load(InputStream is) throws IOException, ClassNotFoundException{
		ObjectInputStream ois = new ObjectInputStream(is);
		Network network = (Network) ois.readObject();
		is.close();
		ois.close();
		return network;
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
