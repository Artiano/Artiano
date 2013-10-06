package artiano.ga;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Population {
	public Map<String,SuperChromosome> SAVE=new HashMap<String,SuperChromosome>();
	public List<SuperChromosome> list;
	public ISelectMethod selectMethod;
	public double expValue;
	public int MAXcount;
	private long mutationC=0;
	public long mutationMAX=0;
	public abstract void codeing();
	public abstract void decodeing();
	public abstract void init();
	public abstract double fitnessFunction(SuperChromosome o);
	public abstract boolean isTrueChromosome(SuperChromosome o);
	
	
	public void setPopulation(ISelectMethod selectMethod,double expValue,int MAXcount,long mutationMAX){
		this.selectMethod=selectMethod;
		this.expValue=expValue;
		this.MAXcount=MAXcount;
		
	}
	public void crossover() {
		for(int i=0;i<list.size()/2;i++){
			SuperChromosome o1=(SuperChromosome)list.get(i*2); 
			SuperChromosome o2=(SuperChromosome)list.get(i*2+1);
			int index=(int)Math.round(1+Math.random() * (o1.key.length()-2));
			String o11=o1.key.substring(0, index);
			String o12=o1.key.substring(index, o1.key.length());
			String o21=o2.key.substring(0, index);
			String o22=o2.key.substring(index, o1.key.length());
			System.out.println("|||| "+o11+" | "+o12);
			System.out.println("|||| "+o21+" | "+o22);
			o1.key=o11+o22;
			o2.key=o21+o12;
			System.out.println("|||| "+o1.key);
			System.out.println("|||| "+o2.key);
			o1=(SuperChromosome)this.mutation(o1);
			o2=(SuperChromosome)this.mutation(o2);
			if(this.isTrueChromosome(o1) && SAVE.get(o1.key)==null) SAVE.put(o1.key, o1);
			if(this.isTrueChromosome(o2) && SAVE.get(o2.key)==null) SAVE.put(o2.key, o2);
		}
		
	}
	public SuperChromosome mutation(SuperChromosome o) {
		mutationC+=o.key.length();
		if(mutationC>=mutationMAX){
			int index=(int)Math.round(Math.random() * (o.key.length()-1));
			char[] tmp=o.key.toCharArray();
			System.out.println("---to mutation---> "+o.key);
			if(tmp[index]=='0') {
				tmp[index]='1';
			}
			else{
				tmp[index]='0';
			}
			o.key=new String(tmp);
			System.out.println("---to mutationed---> index:["+index+"]   "+o.key);
		}
		return o;
	}
	public boolean toGA(){  //选择
		Set<String> keySet=this.SAVE.keySet();
		Iterator it=keySet.iterator();
		double count=0;
		SuperChromosome max=new SuperChromosome("-1",0,0);
		while(it.hasNext()){
			String key=(String)it.next();
			SuperChromosome o=this.SAVE.get(key);
			count+=o.f=this.fitnessFunction(o);
			if(o.f>max.f) max=o;
		}
		it=keySet.iterator();
		while(it.hasNext()){
			String key=(String)it.next();
			SuperChromosome o=this.SAVE.get(key);
			o.p=o.f/count;
			
			System.out.println(o.key+"    "+o.f+"   "+o.p);
		}
		this.list=new LinkedList<SuperChromosome>();
		this.selectMethod.select(list, SAVE);
		if(max.f>=this.expValue){
			System.out.println("                [----"+max.key+"  "+max.f+"  "+max.p+"-----]");
			return true;
		}
		return false;
		
	}
	
	public void runGA(){
		this.init();
		int con=0;
		while(!this.toGA() && con<this.MAXcount){
			System.out.println("========="+(con+1));
			this.crossover();
			con++;
		}
		
	}

}
