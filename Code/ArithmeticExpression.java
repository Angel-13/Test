package Code;

import mapsTable.FieldIntMap;
import compileTable.ByteWriter;
import compileTable.Operations;
import scanner.LookForwardScanner;
import symbolTable.Class;
import symbolTable.Field;
import symbolTable.Method;
import tokens.Token;
import tokens.Tokens;

public class ArithmeticExpression implements Expression{

	private final Class clazz;
	
	private final Method method;
	
	private final ByteWriter code;
	
	private final Operations operations;
	
	private final Tokens tokens;
	
	private final FieldIntMap fieldsMap;
	
	private final LookForwardScanner lfc;
	
	private final Field field;
	
	private final Field classReference;
	
	private Token position;
	
	public ArithmeticExpression(Class clazz,FieldIntMap fieldMap, Method m, LookForwardScanner lfc, Field field, Token position, Field classReference, String expression){
		this.clazz = clazz;
		this.method = m;
		this.code = new ByteWriter();
		this.operations = new Operations();
		this.tokens = new Tokens();
		this.fieldsMap = fieldMap;
		this.lfc = lfc;
		this.field = field;
		this.position = position;
		this.classReference = classReference;
	}
	
	public ArithmeticExpression(LookForwardScanner lfc, FieldIntMap fieldMap, Method m, Field classReference, String expression){
		this.clazz = m.getClazz();
		this.method = m;
		this.code = new ByteWriter();
		this.operations = new Operations();
		this.tokens = new Tokens();
		this.fieldsMap = fieldMap;
		this.lfc = lfc;
		this.field = null;
		this.position = new Token(-1);
		this.classReference = classReference;
	}
	
	public ArithmeticExpression(Method m){
		this.clazz = m.getClazz();
		this.method = m;
		this.code = new ByteWriter();
		this.operations = new Operations();
		this.tokens = new Tokens();
		this.fieldsMap = this.method.getFieldMap();
		this.lfc = null;
		this.field = null;
		this.position = new Token(-1);
		this.classReference = null;
	}
	
	public void make(boolean isRef){
		if(isRef){
			this.makeForRef();
		}else{
			this.make();
		}
	}
	
	private void make() {
		Token leftSide = this.classReference.getToken();
		this.lfc.readNextToken();
		
		
		if(this.method.isContainingFild(leftSide.getText())){
			this.code.writeAll(this.getCode());
			int position = this.fieldsMap.get(this.method.getFieldByName(leftSide.getText()));
			if((position>=0) && (position<=3)){
				this.code.write1Byte(this.operations.getISTOREbyNumber(position));
			}else{
				this.code.write1Byte(this.operations.ISTORE);
				this.code.write1Byte(position);
			}
		}else{
			
			Field f = this.method.getClazz().getFieldFromFieldRef(leftSide.getText());
			this.code.write1Byte(this.operations.getALOADbyNumber(0));
			this.code.writeAll(this.getCode());
			this.code.write1Byte(this.operations.PUTFIELD);
			this.code.write2Byte(this.method.getClazz().getFieldIntMap().get(f));
			
			
		}
		
	}

	private void makeForRef(){
		this.lfc.readNextToken();
		if(this.method.isContainingFild(this.classReference.getName())){
			this.code.writeAll(this.getALoadCodeForRefereceClass(this.classReference));
		}else{
			this.code.write1Byte(this.operations.ALOAD_0);
			this.code.write1Byte(this.operations.GETFIELD);
			this.code.write2Byte(this.clazz.getFieldIntMap().get(this.classReference));
		}
		if(this.field.getType().isArray()){
			this.code.write1Byte(this.operations.GETFIELD);
			this.code.write2Byte(this.clazz.getFieldIntMap().get(this.field));
			//int number = Integer.parseInt(this.position.getText());
			this.code.writeAll(this.getCodeForIdentifeerOrNumber(this.position,false));
		}
		this.code.writeAll(this.getCode());
		if(this.field.getType().isArray()){
			this.code.write1Byte(this.operations.IASTORE);
		}else{
			this.code.write1Byte(this.operations.PUTFIELD);
			this.code.write2Byte(this.clazz.getFieldIntMap().get(this.field));
		}
	}
	
	public void makeForRetrun(){
		this.code.writeAll(this.getALoadCodeForRefereceClass(this.classReference));
		if(this.field.getType().isArray()){
			this.code.write1Byte(this.operations.GETFIELD);
			this.code.write2Byte(this.clazz.getFieldIntMap().get(this.field));
			//int number = Integer.parseInt(this.position.getText());
			this.code.writeAll(this.getCodeForIdentifeerOrNumber(this.position,false));
			this.code.write1Byte(this.operations.IALOAD);
		}else{
			this.code.write1Byte(this.operations.GETFIELD);
			this.code.write2Byte(this.clazz.getFieldIntMap().get(this.field));
		}
		this.code.write1Byte(this.operations.IRETURN);
	}
	
	public void makeCodeForSimpleRetrun(Field fl, Field f2){
		if(fl == null){
			this.code.write1Byte(this.operations.RETURN);
		}else if(fl.getType().isArray()){
			this.code.writeAll(this.getALoadCodeForRefereceClass(fl));
			this.code.writeAll(this.getCodeForIdentifeerOrNumber(f2.getToken(), false));
			this.code.write1Byte(this.operations.IALOAD);
			this.code.write1Byte(this.operations.IRETURN);
		}else{
			this.code.writeAll(this.getCodeForIdentifeerOrNumber(fl.getToken(), false));
			this.code.write1Byte(this.operations.IRETURN);
		}
	}
	
	@Override
	public ByteWriter getCode() {
		ByteWriter b = new ByteWriter();
		int pos = 0;
		int value = 0;
		while(this.lfc.lookAhead().getToken() != this.tokens.SEMICOLON){
			/******ADDE******************/
			
			if(this.lfc.lookAhead().getToken() == this.tokens.ADD){
				this.lfc.readNextToken();
				if(pos !=0){
					if(this.lfc.lookAhead().getToken() == this.tokens.ROUND_BRACKET_OPEN){
						this.lfc.readNextToken();
						b.writeAll(this.getCode());
					}else{
						b.writeAll(this.getCodeForIdentifeerOrNumber(this.lfc.readNextToken(), false));
					}
					while((this.lfc.lookAhead().getToken() == this.tokens.DIV) ||(this.lfc.lookAhead().getToken() == this.tokens.MULT)){
						Token op = this.lfc.readNextToken();
						if(this.lfc.lookAhead().getToken() == this.tokens.ROUND_BRACKET_OPEN){
							this.lfc.readNextToken();
							b.writeAll(this.getCode());
							b.write1Byte(this.getOperationFromOperatorToken(op));
						}else{
							b.writeAll(this.getCodeForIdentifeerOrNumber(this.lfc.readNextToken(), false));
							b.write1Byte(this.getOperationFromOperatorToken(op));
						}
						
					}
					
					b.write1Byte(this.operations.IADD);
				}
				/******ISUB******************/
			}else if(this.lfc.lookAhead().getToken() == this.tokens.SUB){
				this.lfc.readNextToken();
				if(pos !=0){
					if(this.lfc.lookAhead().getToken() == this.tokens.ROUND_BRACKET_OPEN){
						this.lfc.readNextToken();
						b.writeAll(this.getCode());
					}else{
						b.writeAll(this.getCodeForIdentifeerOrNumber(this.lfc.readNextToken(), false));
					}
					while((this.lfc.lookAhead().getToken() == this.tokens.DIV) ||(this.lfc.lookAhead().getToken() == this.tokens.MULT)){
						Token op = this.lfc.readNextToken();
						if(this.lfc.lookAhead().getToken() == this.tokens.ROUND_BRACKET_OPEN){
							this.lfc.readNextToken();
							b.writeAll(this.getCode());
							b.write1Byte(this.getOperationFromOperatorToken(op));
						}else{
							b.writeAll(this.getCodeForIdentifeerOrNumber(this.lfc.readNextToken(), false));
							b.write1Byte(this.getOperationFromOperatorToken(op));
						}
					}
					
					b.write1Byte(this.operations.ISUB);
				}else{
					if(this.lfc.lookAhead().getToken() == this.tokens.ROUND_BRACKET_OPEN){
						
						this.lfc.readNextToken();
						b.writeAll(this.getCode());
						b.write1Byte(this.operations.INEG);
					}else{
						b.writeAll(this.getCodeForIdentifeerOrNumber(this.lfc.readNextToken(), true));
					}
					while((this.lfc.lookAhead().getToken() == this.tokens.DIV) ||(this.lfc.lookAhead().getToken() == this.tokens.MULT)){
						Token op = this.lfc.readNextToken();
						if(this.lfc.lookAhead().getToken() == this.tokens.ROUND_BRACKET_OPEN){
							this.lfc.readNextToken();
							b.writeAll(this.getCode());
							b.write1Byte(this.getOperationFromOperatorToken(op));
						}else{
							b.writeAll(this.getCodeForIdentifeerOrNumber(this.lfc.readNextToken(), false));
							b.write1Byte(this.getOperationFromOperatorToken(op));
						}
					}
				}
			}else if((this.lfc.lookAhead().getToken() == this.tokens.DIV) || (this.lfc.lookAhead().getToken() == this.tokens.MULT)){
				while((this.lfc.lookAhead().getToken() == this.tokens.DIV) ||(this.lfc.lookAhead().getToken() == this.tokens.MULT)){
					Token op = this.lfc.readNextToken();
					if(this.lfc.lookAhead().getToken() == this.tokens.ROUND_BRACKET_OPEN){
						this.lfc.readNextToken();
						b.writeAll(this.getCode());
						b.write1Byte(this.getOperationFromOperatorToken(op));
					}else{
						b.writeAll(this.getCodeForIdentifeerOrNumber(this.lfc.readNextToken(), false));
						b.write1Byte(this.getOperationFromOperatorToken(op));
					}
				}
			}else if(this.lfc.lookAhead().getToken() == this.tokens.ROUND_BRACKET_OPEN){
				this.lfc.readNextToken();
				b.writeAll(this.getCode());
			}else if ((this.lfc.lookAhead().getToken() == this.tokens.IDENTIFIER) || (this.lfc.lookAhead().getToken() == this.tokens.NUMBER)){
				b.writeAll(this.getCodeForIdentifeerOrNumber(this.lfc.readNextToken(), false));
			}
			if(this.lfc.lookAhead().getToken() == this.tokens.ROUND_BRACKET_CLOSE){
				this.lfc.readNextToken();
				return b;
			}
			pos++;
			
		}
		
		return b;
	}

	@Override
	public ByteWriter getExpressionCode() {
		return this.code;
	}
	
	public ByteWriter getCodeForIdentifeerOrNumber(Token t, boolean negative){
		ByteWriter b = new ByteWriter();

		
		if(t.getToken() == this.tokens.IDENTIFIER){
			if(this.lfc.lookAhead().getToken() == this.tokens.DOT){
				this.lfc.readNextToken();
				b.writeAll(this.getPositionOfReference(t));
			}else{
				int position = 0;
				if(this.method.isContainingFild(t.getText())){
					position= this.fieldsMap.get(this.method.getFieldByName(t.getText()));
					if((position>=0) && (position<=3)){
						b.write1Byte(this.operations.getILOADbyNumber(position));
						if(negative){
							b.write1Byte(this.operations.INEG);
						}
					}else{
						b.write1Byte(this.operations.ILOAD);
						b.write1Byte(position);
					}
				}else{
					b.write1Byte(this.operations.ALOAD_0);
					b.write1Byte(this.operations.GETFIELD);
					Class c = this.method.getClazz();
					Field f = c.getFieldFromFieldRef(t.getText());
					position = c.getFieldIntMap().get(f);
					b.write2Byte(position);
				}
				
				
			}
		}else{
			int number;
			if(negative){
				number = Integer.parseInt("-" + t.getText());
			}else{
				number = Integer.parseInt(t.getText());
			}
			if(this.classReference != null){
				if(!this.classReference.getType().isArray() && !this.classReference.getType().isClass()){
					this.classReference.setValue(number);
				}
			}
			//TODO Make it functional for other expressions
			if(this.lfc != null){
				if(this.isNextTokenOperator() && (this.lfc.lookAhead().getToken() == this.tokens.NUMBER)){
					Token op = this.lfc.readNextToken();
					int number1 = Integer.parseInt(this.lfc.readNextToken().getText());
					number = this.operate(op, number, number1);
				}
			}
			
			
			if((number >=0) && (number <=5)){
				b.write1Byte(this.operations.getICONSTbyNumber(number));
				
			}else{
				if(number > 127){
					b.write1Byte(this.operations.SIPUSH);
					b.write2Byte(number);
				}else{
					b.write1Byte(this.operations.BIPUSH);
					b.write1Byte(number);
				}
			}
		}
		return b;
	}
	
	private ByteWriter getPositionOfReference(Token t) {
		ByteWriter b = new ByteWriter();
		Token refToken = this.lfc.readNextToken();
		Field fieldRef = this.clazz.getFieldFromFieldRef(refToken.getText());
		Field classRef = null;
		if(this.method.isContainingFild(t.getText())){
			classRef = this.method.getFieldByName(t.getText());
		}else{
			classRef = this.clazz.getFieldFromFieldRef(t.getText());
		}
		if(fieldRef.getType().isArray()){
			this.lfc.readNextToken();
			Token identifierOrNumber = this.lfc.readNextToken();
			b.writeAll(this.getALoadCodeForRefereceClass(classRef));
			b.write1Byte(this.operations.GETFIELD);
			b.write2Byte(this.clazz.getFieldIntMap().get(fieldRef));
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
			b.write2Byte(this.clazz.getFieldIntMap().get(fieldRef));
		}
		
		return b;
	}

	private int getOperationFromOperatorToken(Token token) {
		if(token.getToken() == this.tokens.DIV){
			return this.operations.IDIV;
		}else if(token.getToken() == this.tokens.ADD){
			return this.operations.IADD;
		}else if(token.getToken() == this.tokens.SUB){
			return this.operations.ISUB;
		}else if(token.getToken() == this.tokens.DIV){
			return this.operations.IDIV;
		}else if(token.getToken() == this.tokens.MULT){
			return this.operations.IMUL;
		}else if(token.getToken() == this.tokens.NEGATE){
			return this.operations.INEG;
		}
		return -2;
	}
	

	private ByteWriter getALoadCodeForRefereceClass(Field f){
		ByteWriter b = new ByteWriter();
		if(this.method.isContainingFild(f.getName())){
			int position = this.fieldsMap.get(f);
			if((position >=0) && (position <= 3)){
				b.write1Byte(this.operations.getALOADbyNumber(position));
			}else{
				b.write1Byte(this.operations.ALOAD);
				b.write1Byte(position);
			}
		}else{
			b.write1Byte(this.operations.ALOAD_0);
			b.write1Byte(this.operations.GETFIELD);
			b.write2Byte(this.clazz.getFieldIntMap().get(f));
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
	
/*******************************************************************************************
 *  isNextTokenOperator() 
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	private boolean isNextTokenOperator(){
		Token t1 = this.lfc.lookAhead();
		if((t1.getToken() == this.tokens.ADD) || (t1.getToken() == this.tokens.SUB) || (t1.getToken() == this.tokens.DIV) || (t1.getToken() == this.tokens.MULT)){
			return true;
		}
		return false;
	}
	
/*******************************************************************************************
 *  int operate(Token operator, int s, int s1)
 *  	- 
 *******************************************************************************************/
	private int operate(Token operator, int s, int s1){
		if(operator.getToken() == this.tokens.ADD){
			return s + s1;
		}else if(operator.getToken() == this.tokens.DIV){
			return s/s1;
		}else if(operator.getToken() == this.tokens.MULT){
			return s*s1;
		}else{
			return s-s1;
		}
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
