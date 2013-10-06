package artiano.ga;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RouletteMethod implements ISelectMethod{

	@Override
	public Object getKey(Map SAVE) {
		double nowP=Math.random();
		Set<String> keySet=SAVE.keySet();
		Iterator it=keySet.iterator();
		double m=0;
		while(it.hasNext()){
			String key=(String)it.next();
			SuperChromosome o=(SuperChromosome)SAVE.get(key);
			m+=o.p;
			if(nowP<=m) return key;
		}
		return "";		
	}


	@Override
	public void select(List list, Map SAVE) {
		for(int i=0;i<2;i++){
			String seleKey=(String)this.getKey(SAVE);
			list.add(new SuperChromosome((SuperChromosome)SAVE.get(seleKey)));
			System.out.println("--->"+seleKey);
		}
	}
	

}
