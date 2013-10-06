package artiano.ga.test;

import artiano.ga.RouletteMethod;
import artiano.ga.SuperChromosome;

public class GAtest extends artiano.ga.Population{
	public double[][] B={
			{77,92},
			{22,22},
			{29,87},
			{50,46},
			{99,90}
	};
	private double M=100;
	private int N=5;
	@Override
	public void codeing() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void decodeing() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init() {
		SAVE.put("10000", new SuperChromosome("10000",0,0));
		SAVE.put("01100", new SuperChromosome("01100",0,0));
		SAVE.put("00001", new SuperChromosome("00001",0,0));
		SAVE.put("01010", new SuperChromosome("01010",0,0));
		
	}

	@Override
	public double fitnessFunction(SuperChromosome o) {
		String key=o.key;
		double count=0;
		for(int i=0;i<key.length();i++){
			double tmp=0;
			if(key.charAt(i)=='1'){
				tmp+=B[i][1];
			}
			count+=tmp;
		}
		return count;
	}

	@Override
	public boolean isTrueChromosome(SuperChromosome o) {
		String key=o.key;
		double m=0;
		for(int i=0;i<N;i++){
			if(key.charAt(i)=='1'){
				m+=B[i][0];
			}
		}
		if(m<=M)return true;
		return false;
	}
	public static void main(String[] args){
		GAtest ts=new GAtest();
		ts.setPopulation(new RouletteMethod(), 133, 500);
		ts.runGA();
	}

}
