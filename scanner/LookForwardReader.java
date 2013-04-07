/************************************************************
* @author: Angel Mirkovski m/n:0422200
* @author: Fabian Winkler  m/n:0422200
*
* 
*
************************************************************/
package scanner;

import java.io.IOException;
import java.io.Reader;

public class LookForwardReader
{
	private final Reader reader;
	private boolean end;
	private char currentCh;
	private char nextCh;
	private final Position pos;
	
	public LookForwardReader(Reader reader) throws IOException
	{
		this.reader = reader;
		this.pos = new Position(0, -1);
		this.readNext();
		this.end = false;
	}
	
	public char getAkChar()
	{
		return this.currentCh;
	}
	
	public boolean hasNext()
	{
		return !this.end;
	}
	
	public char lookAhead()
	{
		if (!this.hasNext())
			throw new IllegalStateException("Es gibt kein weiteres Zeichen!");
		return this.nextCh;
	}
	
	public char readNext()
	{
		this.currentCh = this.nextCh;
		Reader r = this.reader;
		char[] readCh = new char[1];
		
		int iCount;
		try
		{
			iCount = r.read(readCh);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		if (this.currentCh == '\n')
		{
			this.pos.raw++;
			this.pos.column = 0;
		}
		else{
			this.pos.column++;
		}
		
		if (iCount == -1)
			this.end = true;
		else
			this.nextCh = readCh[0];
		
		return this.currentCh;
	}
	public Position getPosition()
	{
		return this.pos;
	}
	
	public void closeLookForwardReader(){
		try {
			this.reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
