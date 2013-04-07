/************************************************************
* @author: Angel Mirkovski m/n:0422200
* @author: Fabian Winkler  m/n:0821409
*
* 
*
************************************************************/
import java.io.*;

public class Test {
	public static void main(String[] args){
		
		File file = new File(".\\Test.java");
		/*String s = "a=b-2 !!123";*/
		Reader r ;//= new StringReader(s);
		LookForwardReader scr;
		LookForwardScanner lfs;
		try {
			r= new FileReader(file);
			scr=new LookForwardReader(r);
			Scanner sc = new Scanner(scr);
			lfs = new LookForwardScanner(sc);
			//lfs.readNextToken();
			//lfs.readNextToken();
			//lfs.readNextToken();
			/*while(lfs.lookAhead().getToken()!=4){
				
				lfs.readNextToken();
				
			}*/
			while(scr.hasNext()){
				System.out.println(sc.getNextToken());
				//System.out.println(lfs.getCurrentToken());
			}
			/*while(scr.hasNext()){
				
				System.out.println(sc.getNextToken());
				
			}
			
			 
					while(lfs.lookAhead().getText().compareTo("EOF")!=0){
						
						System.out.println(sc.getNextToken());
						
					}
					/*while(lfs.lookAhead().getText().compareTo("EOF")!=0){
						
						System.out.println(sc.getNextToken());
						
					}
			 */
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
