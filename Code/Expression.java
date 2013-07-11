package Code;

import compileTable.ByteWriter;
import symbolTable.Method;

public interface Expression {
	
	public ByteWriter getCode();
	public ByteWriter getExpressionCode();
	public boolean expectingPopCode();
	public Method getMethod();
	public void writePopOperation();

}
