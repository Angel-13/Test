package Code;

import mapsTable.FieldIntMap;
import scanner.LookForwardScanner;
import symbolTable.Class;
import symbolTable.Field;
import symbolTable.Method;
import tokens.Token;
import tokens.Tokens;
import compileTable.ByteWriter;
import compileTable.Operations;

public class NewArrayExpression implements Expression{
	
	private final Operations operations;

	private final ByteWriter code;
	
	private final Method method;
	
	private final Field fieldRef;
	
	private final Class clazz;
	
	private final String lange;
	
	private final Field classRef;
	
	public NewArrayExpression(Method method, Field f, Class clazz, String lange, Field classRef){
		this.operations = new Operations();
		this.code = new ByteWriter();
		this.method = method;
		this.clazz = clazz;
		this.fieldRef = f;
		this.lange = lange;
		this.classRef = classRef;
	}
	@Override
	public ByteWriter getCode() {
		if(this.method.isContainingFild(this.classRef.getName())){
			int position = this.method.getFieldMap().get(this.classRef);
			if((position >=0) && (position <=3)){
				this.code.write1Byte(this.operations.getALOADbyNumber(position));
			}else{
				this.code.write1Byte(this.operations.ALOAD);
				this.code.write1Byte(position);
			}
		}else{
			int position = this.clazz.getFieldIntMap().get(this.classRef);
			this.code.write1Byte(this.operations.ALOAD_0);
			this.code.write1Byte(this.operations.GETFIELD);
			this.code.write2Byte(position);
		}
		if(method.isContainingFildMethodAndClassAndLoops(lange, false)){

			
			Field f = method.findFieldInsideMethoAndClassAndScope(lange);
			this.fieldRef.setSize(f.getValue());
			int mapPostition = method.getFieldMap().get(f);
			if((mapPostition>=0) && (mapPostition<=3)){
				this.code.write1Byte(this.operations.getILOADbyNumber(mapPostition));
			}else{
				this.code.write1Byte(this.operations.ILOAD);
				this.code.write1Byte(mapPostition);
			}
			
		}else{
			int number =  Integer.parseInt(this.lange);
			this.fieldRef.setSize(number);
			if((number >=0) && (number <=5)){
				this.code.write1Byte(this.operations.getICONSTbyNumber(number));
				
			}else{
				this.code.write1Byte(this.operations.BIPUSH);
				this.code.write1Byte(number);
			}
		}
		this.code.write1Byte(this.operations.NEWARRAY);
		//TODO To implement for other types
		if(this.fieldRef.getType().getBaseType().isInteger()){
			this.code.write1Byte(0x0a);
		}
		this.code.write1Byte(this.operations.PUTFIELD);
		this.code.write2Byte(this.method.getClazz().getFieldIntMap().get(this.fieldRef));
		return this.code;
	}
	
	public ByteWriter getCodeForLocalArrayFieldInitialization() {
		if(method.isContainingFildMethodAndClassAndLoops(lange, false)){
			Field f = method.findFieldInsideMethoAndClassAndScope(lange);
			this.fieldRef.setSize(f.getValue());
			int mapPostition = method.getFieldMap().get(f);
			if((mapPostition>=0) && (mapPostition<=3)){
				this.code.write1Byte(this.operations.getILOADbyNumber(mapPostition));
			}else{
				this.code.write1Byte(this.operations.ILOAD);
				this.code.write1Byte(mapPostition);
			}
			
		}else{
			int number =  Integer.parseInt(this.lange);
			this.fieldRef.setSize(number);
			if((number >=0) && (number <=5)){
				this.code.write1Byte(this.operations.getICONSTbyNumber(number));
				
			}else{
				this.code.write1Byte(this.operations.BIPUSH);
				this.code.write1Byte(number);
			}
			this.code.write1Byte(this.operations.NEWARRAY);
			if(this.fieldRef.getType().getBaseType().isInteger()){
				this.code.write1Byte(0x0a);
			}
			//TODO To implement for other types
			int position = this.method.getFieldMap().get(this.fieldRef);
			//System.out.println(position + "  " + this.fieldRef.getName());
			if((position >=0) && (position <=3)){
				this.code.write1Byte(this.operations.getASTROEbyNumber(position));
			}else{
				this.code.write1Byte(this.operations.ASTORE);
				this.code.write1Byte(position);
			}
		}
		return this.code;
	}

	@Override
	public ByteWriter getExpressionCode() {
		return this.code;
	}
	@Override
	public boolean expectingPopCode() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public Method getMethod() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void writePopOperation() {
		// TODO Auto-generated method stub
		
	}

}
