/************************************************************
* @author: Angel Mirkovski m/n:0422200
* @author: Fabian Winkler  m/n:0422200
*
* 
*
************************************************************/
package scanner;

import java.text.MessageFormat;

public class Position
{
	
	public int raw;
	
	public int column;

	
	public Position(int raw, int column)
	{
		this.raw = raw;
		this.column = column;
		return;
	}

	
	@SuppressWarnings("static-access")
	public String toString()
	{
		MessageFormat format = new MessageFormat("");
		Object[] arguments = new Integer[2];
		arguments[0] = new Integer(this.raw + 1);
		arguments[1] = new Integer(this.column + 1);
		return format.format("Raw {0}, Column {1}", arguments);
	}
}