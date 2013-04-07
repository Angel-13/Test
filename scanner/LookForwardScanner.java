/************************************************************
* @author: Angel Mirkovski m/n:0422200
* @author: Fabian Winkler  m/n:0422200
*
* 
*
************************************************************/
package scanner;

import tokens.*;

import java.io.IOException;

public class LookForwardScanner
{
	private final Scanner scanner;
	private Token currentToken;
	private Token nextToken;

	public LookForwardScanner(Scanner scanner) throws IOException
	{
		this.scanner = scanner;
		this.readNextToken();
	}

	public Token getCurrentToken()
	{
		return this.currentToken;
	}

	public Token lookAhead()
	{
		return this.nextToken;
	}

	public Token readNextToken()
	{
		this.currentToken = this.nextToken;
		Scanner scanner = this.scanner;
		this.nextToken = scanner.getNextToken();
		return this.currentToken; 
	}
	
	public Scanner getScanner(){
		return this.scanner;
	}
	
	public void closeReader(){
		this.scanner.getReader().closeLookForwardReader();
	}
}