package artiano.core.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import artiano.core.structure.Matrix;
/***
 * 
 * @author BreezeDust
 *
 */
public class JsonData implements DataSave{
	public String[] fieldNames;

	@Override
	public boolean save(Matrix mx, String fileName, String[] fieldNames) {
		if(mx.data().length<=0) return false;
		String jsonStr="{fields:'";
		for(int con=0;con<fieldNames.length;con++){
			jsonStr+=fieldNames[con];
			if(con<fieldNames.length-1) jsonStr+=" ";
		}
		jsonStr+="',";
		jsonStr+="Matrix:[";
		for(int i=0;i<mx.rows();i++){
			String rowStr="";
			for(int j=0;j<mx.columns();j++){
				rowStr+=fieldNames[j]+":"+mx.at(i, j);
				if(j<mx.columns()-1) rowStr+=",";
			}
			rowStr="{"+rowStr+"}";
			if(i<mx.rows()-1) rowStr+=",";
			jsonStr+=rowStr;
		}
		jsonStr+="]}";
		
		
		File file=new File(fileName);
		try {
			PrintWriter cout=new PrintWriter(file);
			cout.println(jsonStr);
			cout.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		System.out.print("**********"+fileName+" save successful!!"+"**********");
		this.fieldNames=fieldNames;
		return true;
	}

	@Override
	public Matrix load(String fileName) {
		String jsonStr="";
		try {
			Scanner cin=new Scanner(new File(fileName));
			while(cin.hasNext()){
				jsonStr+=cin.nextLine();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		JSONObject jsonObject=new JSONObject(jsonStr);
		String[] fieldNames=jsonObject.getString("fields").split(" ");
		JSONArray jsonArray = jsonObject.getJSONArray("Matrix");
		Matrix mx=new Matrix(jsonArray.length(),fieldNames.length);
		for(int i=0;i<jsonArray.length();i++){
			JSONObject row=jsonArray.getJSONObject(i);
			for(int j=0;j<fieldNames.length;j++){
				mx.set(i,j,row.getDouble(fieldNames[j]));
			}
		}
		this.fieldNames=fieldNames;
		return mx;
	}

	

}
