/**
 * Test.java
 */
package artiano.neural.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;


import artiano.core.operation.MatrixOpt;
import artiano.core.structure.Matrix;
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
	
	static Matrix[] inputs = null;
	static Matrix[] outputs = null;

	static void read(String filename, int count) throws FileNotFoundException{
		File file=new File(filename);
        if(!file.exists()||file.isDirectory())
            throw new FileNotFoundException();
        FileInputStream fis=new FileInputStream(file);
        inputs = new Matrix[count];
        outputs = new Matrix[count];
       
        Scanner scanner = new Scanner(fis);
        Randomizer ram = new GuassianRandomizer(0, 0.3);
        for (int i = 0; i < count; i++){
        	inputs[i] = new Matrix(1, 4);
        	outputs[i] = new Matrix(1, 3);
        	for (int j = 0; j < 4; j++){
        		double x = scanner.nextDouble() + ram.next();
        		inputs[i].set(0, j, x);
        	}
        	int idx = scanner.nextInt();
        	outputs[i].set(0, idx - 1, 1.);
        }
        scanner.close();
        MatrixOpt.normalizeByMinMax(inputs, false);
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
			read("f:\\trainData.txt", 75);
			inputs[0].print();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		while (network.squreError > e && network.epochs < 1000){
			double err = teacher.runEpoch(inputs, outputs);
			System.out.println("Error: " + err);
		}
		
		System.out.println("least squre error = " + network.squreError);
		System.out.println("epochs = " + network.epochs);
		
		try {
			read("f:\\testData.txt", 75);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		int hit_num = 0;
		for (int i = 0; i < inputs.length; i++)
		{
			Matrix xxx = network.compute(inputs[i]);
			double max_1 = 0., max_2 = 0.;
			int x_1 = 0, x_2 = 0;
			for (int j = 0; j < xxx.columns(); j++)
			{
				if (max_1 < xxx.at(j))
				{
					max_1 = xxx.at(j);
					x_1 = j;
				}
				if (max_2 < outputs[i].at(j))
				{
					max_2 = outputs[i].at(j);
					x_2 = j;
				}
			}
			if (x_1 == x_2)
				hit_num++;
		}
		System.out.println("accuracy = " + (double)hit_num / 75. * 100 + "%");
	}
	
	public static void testSOM(){
		DistanceNetwork network = new DistanceNetwork(4, 25);
		network.randomize(new GuassianRandomizer(0, 0.5));
		SOMLearning teacher = new SOMLearning(network);
		try {
			read("f:\\trainData.txt", 75);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		double err = 0;
		for (int i = 0; i < 5000; i++){
			err = teacher.runEpoch(inputs);
		}
		try {
			read("f:\\testData.txt", 75);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("Error: " + err);
		for (int i = 0; i < 75; i++){
			network.compute(inputs[i]);
			int winner = network.winner();
			System.out.println("winner: " + winner);
		}
	}
	
	public static void main(String[] args){
		testActivationNetwork();
		//testSOM();
	}
}
