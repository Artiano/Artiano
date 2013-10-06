package artiano.ga;

import java.util.List;
import java.util.Map;

public interface IChromosome {
	public void  codeing();
	public void decodeing();
	public void init();
	public void crossover(Map SAVE,List list);
	public SuperChromosome mutation(SuperChromosome chromosome);
	public boolean isTrueChromosome(SuperChromosome chromosome);
	 
}
