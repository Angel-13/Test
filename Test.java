import milestone2.Parser;

/************************************************************
* @author: Angel Mirkovski m/n:0422200
* @author: Fabian Winkler  m/n:0821409
*
* 
*
************************************************************/


public class Test {
	public static void main(String[] args){
		
		if(args.length == 1){
				try {
				
				Parser p = new Parser(args[0]);//"./Test.java");
				}catch (Exception e) {
				e.printStackTrace();}
		}else{
				System.out.println("Usage: java class_name filename");
		}
		
	}
}
