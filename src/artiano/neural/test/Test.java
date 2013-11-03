/**
 * Test.java
 */
package artiano.neural.test;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.math.plot.Plot3DPanel;



import artiano.core.operation.CSVLoader;
import artiano.core.operation.MatrixOpt;
import artiano.core.structure.Matrix;
import artiano.core.structure.NominalAttribute;
import artiano.core.structure.Range;
import artiano.core.structure.StringAttribute;
import artiano.core.structure.Table;
import artiano.neural.actfun.Sigmoid;
import artiano.neural.initializer.NguyenWidrow;
import artiano.neural.initializer.WeightsInitializer;
import artiano.neural.learning.LevenbergMarquardtLearning;
import artiano.neural.learning.SOMLearning;
import artiano.neural.learning.StochasticBPLearning;
import artiano.neural.network.ActivationNetwork;
import artiano.neural.network.DistanceNetwork;
import artiano.randomizer.GuassianRandomizer;
import artiano.randomizer.Randomizer;



/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0
 * @date 2013-8-14
 * @author (latest modification by Nano.Michael)
 * @function 
 * @since 1.0
 */
public class Test {
	
	static Table table = null;
	
	static Matrix inputs = null;
	static Matrix outputs = null;
	
	static double[][] x,y,z;

	static void read(String filename, int count) throws IOException{
		CSVLoader loader = new CSVLoader(filename);
		table = loader.read(count);
		loader.close();
	}
	
	static void normalize(){
		StringAttribute att = (StringAttribute) table.removeAttribute(4);
		NominalAttribute att2 = att.toNominal();
		table.addAttributes(att2.toBinary());
	}
	
	static void testActivationNetwork(){
		int[] h = {6};
		ActivationNetwork network = new ActivationNetwork(4, 3, h);
		//network.randomize(new GuassianRandomizer(0, 0.5));
		WeightsInitializer initializer = new NguyenWidrow();
		initializer.initialize(network);
		network.setActivationFunction(new Sigmoid(2.0));
		StochasticBPLearning teacher = new StochasticBPLearning(network);
		//LevenbergMarquardtLearning teacher = new LevenbergMarquardtLearning(network, 1);
		double e = 0.01;
		try {
			read("f:\\iris.csv", -1);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		normalize();
		//resample
		Table[] tables = table.resample(0.8);
		Matrix train = tables[0].toMatrix();
		Matrix test = tables[1].toMatrix();
		System.out.println("train matirx:");
		train.print();
		System.out.println("test matrix:");
		test.print();
		//split inputs & outputs
		inputs = train.at(Range.all(), new Range(0, 4)).normalizeRows();
		outputs = train.at(Range.all(), new Range(4, 7));
		System.out.println("after normalize:");
		inputs.print();
		
		while (network.squreError > e && network.epochs < 1000){
			double err = teacher.runEpoch(inputs, outputs);
			System.out.println("Error: " + err);
		}
		
		System.out.println("least squre error = " + network.squreError);
		System.out.println("epochs = " + network.epochs);
		
		//save
		try {
			network.save("F:\\Artiano\\Activation-Network.net");
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		inputs = test.at(Range.all(), new Range(0, 4)).normalizeRows();
		outputs = test.at(Range.all(), new Range(4, 7));
		//load
		try {
			ActivationNetwork network2 = (ActivationNetwork) ActivationNetwork.load("F:\\Artiano\\Activation-Network.net");
			int hit_num = 0;
			for (int i = 0; i < inputs.rows(); i++)
			{
				Matrix xxx = network2.compute(inputs.row(i));
				double max_1 = 0., max_2 = 0.;
				int x_1 = 0, x_2 = 0;
				for (int j = 0; j < xxx.columns(); j++)
				{
					if (max_1 < xxx.at(j))
					{
						max_1 = xxx.at(j);
						x_1 = j;
					}
					if (max_2 < outputs.row(i).at(j))
					{
						max_2 = outputs.row(i).at(j);
						x_2 = j;
					}
				}
				if (x_1 == x_2)
					hit_num++;
			}
			System.out.println("accuracy = " + (double)hit_num / 30. * 100 + "%");
		} catch (ClassNotFoundException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static void testSOM(){
		DistanceNetwork network = new DistanceNetwork(4, 25);
		network.randomize(new GuassianRandomizer(0, 0.5));
		SOMLearning teacher = new SOMLearning(network);
		try {
			read("f:\\trainData.txt", 75);
		} catch (IOException e) {
			e.printStackTrace();
		}
		double err = 0;
		for (int i = 0; i < 5000; i++){
			err = teacher.runEpoch(inputs);
		}
		try {
			read("f:\\testData.txt", 75);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Error: " + err);
		for (int i = 0; i < 75; i++){
			network.compute(inputs.row(i));
			int winner = network.winner();
			System.out.println("winner: " + winner);
		}
	}
	
	/*public static void testPlot(){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		x = new double[3][25];
		y = new double[3][25];
		z = new double[3][25];
		for (int i=0; i<3; i++){
			x[i] = inputs.col(i).toArray();
			
			for (int j=0; j<25; j++){
				x[i][j] = inputs[i*25+j].at(0);
				y[i][j] = inputs[i*25+j].at(1);
				z[i][j] = inputs[i*25+j].at(2);
			}
		}
		Plot3DPanel panel = new Plot3DPanel("SOUTH");
		panel.addScatterPlot("1", Color.CYAN, x[0], y[0], z[0]);
		panel.addScatterPlot("2", Color.RED, x[1], y[1], z[1]);
		panel.addScatterPlot("3", Color.GREEN, x[2], y[2], z[2]);
		JFrame frame = new JFrame("a plot panel");
        frame.setSize(600, 600);
        frame.setContentPane(panel);
        frame.setVisible(true);
	}*/
	
	public static void main(String[] args){
		testActivationNetwork();
		//testPlot();
		//testSOM();
	}
}
