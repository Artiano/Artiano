package artiano.ga;

public class SuperChromosome {
	public String key;
	public double f;
	public double p;
	public SuperChromosome(String key,double f,double p){
		this.key=key;
		this.f=f;
		this.p=p;
	}
	public SuperChromosome(SuperChromosome o){
		this.key=o.key;
		this.f=o.f;
		this.p=o.p;
	}

}
