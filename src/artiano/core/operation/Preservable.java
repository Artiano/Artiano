/**
 * Preservable.java
 */
package artiano.core.operation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * <p>Description: Abstract class of every preservable class. Every preservable class should extends this
 * class.</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-9-7
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public abstract class Preservable implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * save object to specified file
	 * @param filename Specified file name
	 * @throws IOException 
	 */
	public void save(String filename) throws IOException{
		FileOutputStream os = new FileOutputStream(new File(filename));
		save(os);
	}
	
	/**
	 * save object to output stream
	 * @param os Specified output stream
	 * @throws IOException
	 */
	public void save(OutputStream os) throws IOException{
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(this);
		os.flush();
		os.close();
		oos.flush();
		oos.close();
	}
	
	/**
	 * load object from specified file
	 * @param filename Specified file name
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static Preservable load(String filename) throws ClassNotFoundException, IOException{
		FileInputStream fis = new FileInputStream(new File(filename));
		return load(fis);
	}
	
	/**
	 * load object from specified input stream
	 * @param is Specified input stream
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static Preservable load(InputStream is) throws IOException, ClassNotFoundException{
		ObjectInputStream ois = new ObjectInputStream(is);
		Preservable obj = (Preservable) ois.readObject();
		is.close();
		ois.close();
		return obj;
	}
	
}
