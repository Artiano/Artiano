package artiano.core.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import artiano.core.operation.Preservable;

public class Test extends Preservable{
	public int a=10;
	public static void main(String[] args) throws IOException, ClassNotFoundException{
		Test cs=new Test();
		cs.a=-101;
		cs.save("a.txt");
		Test b=(Test)Test.load("a.txt");
		System.out.println(b.a);
	}

}
