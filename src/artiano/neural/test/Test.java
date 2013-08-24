/**
 * Test.java
 */
package artiano.neural.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;


import artiano.neural.actfun.Sigmoid;
import artiano.neural.learning.StochasticBPLearning;
import artiano.neural.network.ActivationNetwork;
import artiano.neural.randomizer.GuassianRandomizer;
import artiano.neural.randomizer.Randomizer;



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
	static double[][] inputs;
	static double[][] outputs;
	
	static double premnmx( double num , double min , double max  )
	{
		if (num > max)
			num = max;
		if (num < min)
			num = min; 

		return 2 * (num - min) / (max - min) - 1;
	}

	static void read(String filename, int count) throws FileNotFoundException{
		File file=new File(filename);
        if(!file.exists()||file.isDirectory())
            throw new FileNotFoundException();
        FileInputStream fis=new FileInputStream(file);
        inputs = new double[count][4];
        outputs = new double[count][4];
        double[] max = new double[4];
        double[] min = new double[4];
        Scanner scanner = new Scanner(fis);
        Randomizer ram = new GuassianRandomizer(0, 0.3);
        for (int i = 0; i < count; i++){
        	for (int j = 0; j < 4; j++){
        		double x = scanner.nextDouble() + ram.next();
        		if (x > max[j])
    				max[j] = x;
    			if (x < min[j])
    				min[j] = x;
        		inputs[i][j] = x;
        	}
        	int idx = scanner.nextInt();
        	outputs[i][idx - 1] = 1;
        }
        scanner.close();
        
        for (int i = 0; i < count; ++i)
    	{
    		for (int j = 0; j < 4; ++j)
    		{
    			inputs[i][j] = premnmx(inputs[i][j], min[j], max[j]);
    		}
    	}
	}
	
	static void testActivationNetwork(){
		int[] h = {3};
		ActivationNetwork network = new ActivationNetwork(4, 3, h);
		network.randomize(new GuassianRandomizer(0, 0.5));
		network.setActivationFunction(new Sigmoid(2.0));
		StochasticBPLearning teacher = new StochasticBPLearning(network);
		double e = 0.01;
		try {
			read("f:\\trainData.txt", 75);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		while (network.squreError > e){
			teacher.runEpoch(inputs, outputs);
		}
		
		System.out.println("least squre error = " + network.squreError);
		System.out.println("epochs = " + network.epochs);
		
		try {
			read("f:\\testData.txt", 75);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int hit_num = 0;
		for (int i = 0; i < inputs.length; i++)
		{
			double[] xxx = network.compute(inputs[i]);
			double max_1 = 0., max_2 = 0.;
			int x_1 = 0, x_2 = 0;
			for (int j = 0; j < xxx.length; j++)
			{
				if (max_1 < xxx[j])
				{
					max_1 = xxx[j];
					x_1 = j;
				}
				if (max_2 < outputs[i][j])
				{
					max_2 = outputs[i][j];
					x_2 = j;
				}
			}
			if (x_1 == x_2)
				hit_num++;
		}
		System.out.println("accuracy = " + (double)hit_num / 75. + "%");
	}
	
	public static void main(String[] args){
		testActivationNetwork();
	}
}
