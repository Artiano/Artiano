package artiano.core.data;

public class DataSaveFactory {
	public static String SaveMethod="JsonData"; //以后通过配置文件来选择储存方式
	public static DataSave createDataSave(){
		Class dataSaveClass=null;
		try {
			dataSaveClass = Class.forName("artiano.core.data."+SaveMethod);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		DataSave dataSave=null;
		try {
			dataSave = (DataSave)dataSaveClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return dataSave;
	}

}
