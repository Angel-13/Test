package Code;

import mapsTable.FieldIntMap;
import scanner.LookForwardScanner;
import symbolTable.Field;
import symbolTable.Method;
import symbolTable.Class;
import symbolTable.ParameterList;
import symbolTable.Type;
import tokens.Token;
import tokens.Tokens;
import compileTable.ByteWriter;
import compileTable.Operations;

public class MethodCallExpression implements Expression{

	private final Operations operations;
	
	private final Tokens tokens;
	
	private final ByteWriter code;
	
	private final FieldIntMap fieldsMap;
	
	private final Method method;
	
	private final LookForwardScanner lfc;
	
	private final Tokens tks;
	
	private boolean expecitngPop;
	
	private Method methodNameExpectiongPop;
	
	public MethodCallExpression(LookForwardScanner lfc, FieldIntMap fieldMap, Method m){
		this.operations = new Operations();
		this.lfc = lfc;
		this.code = new ByteWriter();
		this.fieldsMap = fieldMap;
		this.tokens = new Tokens();
		this.method = m;
		this.tks = new Tokens();
		this.expecitngPop = false;
		this.methodNameExpectiongPop = null;
	}

	public MethodCallExpression(LookForwardScanner lfc, Method m){
		this.operations = new Operations();
		this.lfc = lfc;
		this.code = new ByteWriter();
		this.fieldsMap = m.getFieldMap();
		this.tokens = new Tokens();
		this.method = m;
		this.tks = new Tokens();
		this.expecitngPop = false;
		this.methodNameExpectiongPop = null;
	}
	
	public void make(Method mRef, ParameterList p){
		Class c = this.method.getClazz();
		Token classR = this.lfc.readNextToken();
		if(this.method.isContainingFild(classR.getText())){
			Field f = this.method.getFieldByName(classR.getText());
			this.code.writeAll(this.getALoadCodeForRefereceClass(f));
			this.getCodeForParameters(p);
		}else{
			if(this.lfc.lookAhead().getToken() == this.tks.DOT){
				Field f = c.getFieldFromFieldRef(classR.getText());
				this.code.write1Byte(this.operations.ALOAD_0);
				this.code.write1Byte(this.operations.GETFIELD);
				int fieldMap = c.getFieldIntMap().get(f);
				this.code.write2Byte(fieldMap);
				this.getCodeForParameters(p);
			}else{
				this.code.write1Byte(this.operations.ALOAD_0);
				this.getCodeForParameters(p);
			}
		}
		/*if(this.lfc.lookAhead().getToken() == this.tks.DOT){
			System.out.println(classR.getText() + "   MethodCallExpression line 156  " + this.lfc.lookAhead());
			Field f = c.getFieldFromFieldRef(classR.getText());
			this.code.writeAll(this.getALoadCodeForRefereceClass(f));
			this.getCodeForParameters(mRef.getParameterList());
		}else{
			this.code.write1Byte(this.operations.ALOAD_0);
			this.getCodeForParameters(mRef.getParameterList());
		}*/
		if(mRef.isStatic()){
			//TODO
		}else{
			this.code.write1Byte(this.operations.INVOKEVIRTUAL);
			int methodMap = c.getMethodIntMap().get(mRef);
			this.code.write2Byte(methodMap);
		}
		Type returnType = mRef.getRetrunType();
		if(returnType == null){
			this.expecitngPop = true;
			this.methodNameExpectiongPop = mRef;
		}else if(!returnType.isVoid()){
			this.code.write1Byte(this.operations.POP);
		}
	}
	
	private void getCodeForParameters(ParameterList p) {
		Class c = this.method.getClazz();
		for(int i = 0; i < p.getSize(); i++){
			Field f = p.getParameter(i);
			if(this.method.isContainingFild(f.getName())){
				f = this.method.getFieldByName(f.getName());
				if(f.getType().isClass()){
					this.code.writeAll(this.getALoadCodeForRefereceClass(f));
				}else{
					this.code.writeAll(this.getILoadCodeForRefereceClass(f));
				}
			}else{
				f = c.getFieldFromClassFieldsByName(f.getName());
				this.code.write1Byte(this.operations.ALOAD_0);
				this.code.write1Byte(this.operations.GETFIELD);
				int mapPos = c.getFieldIntMap().get(f);
				this.code.write2Byte(mapPos);
			}
			
		}
		this.lfc.readNextToken();
	}

	private ByteWriter getPositionOfReference(Token t) {
		ByteWriter b = new ByteWriter();
		Token refToken = this.lfc.readNextToken();
		Field fieldRef = this.method.getClazz().getFieldFromFieldRef(refToken.getText());
		Field classRef = this.method.getFieldByName(t.getText());
		if(fieldRef.getType().isArray()){
			this.lfc.readNextToken();
			Token identifierOrNumber = this.lfc.readNextToken();
			b.writeAll(this.getALoadCodeForRefereceClass(classRef));
			b.write1Byte(this.operations.GETFIELD);
			b.write2Byte(this.method.getClazz().getFieldIntMap().get(fieldRef));
			if(method.isContainingFildMethodAndClassAndLoops(identifierOrNumber.getText(), false)){
				Field f = method.findFieldInsideMethoAndClassAndScope(identifierOrNumber.getText());
				int mapPostition = method.getFieldMap().get(f);
				if((mapPostition>=0) && (mapPostition<=3)){
					b.write1Byte(this.operations.getILOADbyNumber(mapPostition));
				}else{
					b.write1Byte(this.operations.ILOAD);
					b.write1Byte(mapPostition);
				}
			}else{
				int number =  Integer.parseInt(identifierOrNumber.getText());
				b.writeAll(this.getCodeForPushNumber(number));
				
			}
			b.write1Byte(this.operations.IALOAD);
			
			this.lfc.readNextToken();
			
		}else{
			b.writeAll(this.getALoadCodeForRefereceClass(classRef));
			b.write1Byte(this.operations.GETFIELD);
			b.write2Byte(this.method.getClazz().getFieldIntMap().get(fieldRef));
		}
		
		return b;
	}
	
	private ByteWriter getCodeForPushNumber(int number){
		ByteWriter b = new ByteWriter();
		if((number >=0) && (number <= 5)){
			b.write1Byte(this.operations.getICONSTbyNumber(number));
		}else{
			b.write1Byte(this.operations.BIPUSH);
			b.write1Byte(number);
		}
		return b;
	}
	
	private ByteWriter getALoadCodeForRefereceClass(Field f){
		ByteWriter b = new ByteWriter();
		int position = this.fieldsMap.get(f);
		if((position >=0) && (position <= 3)){
			b.write1Byte(this.operations.getALOADbyNumber(position));
		}else{
			b.write1Byte(this.operations.ALOAD);
			b.write1Byte(position);
		}
		return b;
	}
	
	private ByteWriter getILoadCodeForRefereceClass(Field f){
		ByteWriter b = new ByteWriter();
		int mapPostition = method.getFieldMap().get(f);
		if((mapPostition>=0) && (mapPostition<=3)){
			b.write1Byte(this.operations.getILOADbyNumber(mapPostition));
		}else{
			b.write1Byte(this.operations.ILOAD);
			b.write1Byte(mapPostition);
		}
		return b;
	}
	
	public void writePopOperation(){
		this.code.write1Byte(this.operations.POP);
	}
	
	
	
	@Override
	public ByteWriter getCode() {
		// TODO Auto-generated method stub
		return this.code;
	}

	@Override
	public ByteWriter getExpressionCode() {
		// TODO Auto-generated method stub
		return this.code;
	}

	@Override
	public boolean expectingPopCode() {
		// TODO Auto-generated method stub
		return this.expecitngPop;
	}
	

	public Method getMethod(){
		return this.methodNameExpectiongPop;
	}

}
