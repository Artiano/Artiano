package artiano.ml.classifier;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import artiano.core.structure.Domain;
import artiano.core.structure.Matrix;

/***
 * 
 * @author BreezeDust
 * 
 */
public class NaiveBayesDiscreteClassifier {
	public String[] domainStr;
	public Map<Integer, Matrix> labelMap = new LinkedHashMap<Integer, Matrix>();
	public List<Integer> labeList = new LinkedList<Integer>();
	public List<Domain[]> domainList = new LinkedList<Domain[]>();
	public Matrix[] trainingResults;
	public double[] plabel;
	
	public void group(Matrix trainData,int labelColIndex){
		Matrix labeMx=trainData.getSingerCol(labelColIndex);
		/***
		 * 整理训练矩阵
		 */
		for(int i=0;i<trainData.rows();i++){
			Matrix rowMx=new Matrix(1,trainData.columns()-1);
			/***
			 * 取得处类标的矩阵
			 */
			for(int j=0,con=0;j<trainData.columns();j++){
				if(j!=labelColIndex){
					rowMx.set(0,con++,trainData.at(i,j));
				}
			}
			int labelValue=(int)labeMx.at(i,0);
			//压入HASHMAP
			if(labelMap.get(labelValue)==null){
				labelMap.put(labelValue,rowMx);
				labeList.add(labelValue);
			}
			else{
				Matrix oldMx=labelMap.get(labelValue);
				oldMx.mergeAfterRow(rowMx);
			}
		}		
	}
	public Matrix trainWork(Matrix trainData,Domain[] domains,int con){
		/***
		 * 计算每个label的概率
		 */
		plabel=new double[labeList.size()];
		int rows=trainData.rows();
		Matrix result=null;
		for(int i=0;i<labeList.size();i++){
			Matrix rowMx=new Matrix(1,domains.length+1);
			Matrix tmpMx=labelMap.get(labeList.get(i));
//			tmpMx.print();
			if(plabel[i]==0)plabel[i]=(double)tmpMx.rows()/(double)rows;
			rowMx.set(0,0,plabel[i]);
			
			for(int j=0;j<domains.length;j++){
				int tmpCon=0;
				for(int x=0;x<tmpMx.rows();x++){
					if(domains[j].isIn(tmpMx.at(x,con))==0) tmpCon++;
				}
				double pAB=(double)tmpCon/(double)rows;
				double pA_Y=pAB/plabel[i];
				rowMx.set(0,j+1,pA_Y);
			}
//			rowMx.print();
			if(i==0) result=rowMx;
			if(i>0) result.mergeAfterRow(rowMx);
		}
		return result;
		
	}
	public void groupDomain(String[] domianStr){
		for(int i=0;i<domianStr.length;i++){
			Domain[] tmp=Domain.getArray(domianStr[i]);
			domainList.add(tmp);
		}
		this.domainStr=domianStr;
	}
	public void trainWorkBoot(Matrix trainData){
		trainingResults=new Matrix[domainList.size()];
		for(int i=0;i<domainList.size();i++){
			Matrix tmp=this.trainWork(trainData, domainList.get(i), i);
			System.out.println("["+i+"]"+"---------"+domainStr[i]);
			tmp.print();
			trainingResults[i]=tmp;
		}
		
	}
	public boolean train(Matrix trainData,String[] domainStr,int labelColIndex){
		this.groupDomain(domainStr);
		this.group(trainData, labelColIndex);
		this.trainWorkBoot(trainData);
		return false;
	}
}
