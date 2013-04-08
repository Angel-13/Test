/************************************************************
* @author: Angel Mirkovski m/n:0422200
* @author: Fabian Winkler  m/n:0821409
*
* 
*
************************************************************/
import java.io.*;
import scanner.*;

public class Test {
	public static void main(String[] args){
		
		File file = new File("Test.java");
		Reader r ;
		LookForwardReader scr;
		LookForwardScanner lfs;
		try {
			r= new FileReader(file);
			scr=new LookForwardReader(r);
			Scanner sc = new Scanner(scr);
			lfs = new LookForwardScanner(sc);
			while(scr.hasNext()){
				System.out.println(sc.getNextToken());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
