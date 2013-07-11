package compileTable;

import java.io.File;
import java.io.FileReader;

import scanner.LookForwardReader;
import scanner.LookForwardScanner;
import scanner.Scanner;
import symbolTable.Class;
import symbolTable.Field;
import symbolTable.Method;
import symbolTable.ParameterList;
import symbolTable.Type;
import tokens.Token;
import tokens.Tokens;

public class JavaReader {
	
	private LookForwardScanner lfc;
	
	private final Tokens tks;
	
	private boolean hasMethodName;
	
	private boolean hasParameters;
	
	private Field f; 
	
	private Method m;
	
	public JavaReader(String filepath) throws Exception{
		this.lfc = new LookForwardScanner(new Scanner(new LookForwardReader(new FileReader(new File(filepath)))));
		this.hasMethodName = false;
		this.hasParameters = false;
		this.tks = new Tokens();
		this.f = null;
		this.m = null;
	}
	
	/*public JavaReader(ByteWriter b) throws Exception{
		this.lfc = null;
		this.hasMethodName = false;
		this.hasParameters = false;
		this.tks = new Tokens();
		this.f = null;
		this.m = null;
		//this.makePostition();
	}*/
	

	public boolean findFieldFromJavaFile(Token fieldName, Class clazz){
		boolean b = false;
		boolean finished = false;
		Token token = this.lfc.lookAhead();
		while (!finished && !b)
		{
			if (token.getToken() == this.tks.FIELD_ANNOTATION){
				this.lfc.lookAhead();
				b = this.parseField(fieldName, clazz);
			}else if (token.getToken() == this.tks.EOF){
				finished = true;
			}
			this.lfc.readNextToken();
			token = this.lfc.lookAhead();
		}
		this.lfc.closeReader();
		return b; 
	}
	
	private boolean parseField(Token fieldName, Class clazz) {
		boolean b = false;
		boolean isPrivate = false;
		boolean isStatic = false;
		boolean isArray = false;
		this.lfc.readNextToken();
		//System.out.println(this.lfc.lookAhead().getText()  +  "   TUKAA");
		if(this.isNextToken(new Token(this.tks.PUBLIC)) || this.isNextToken(new Token(this.tks.PRIVATE))){
			Token t = this.lfc.readNextToken();
			if(t.getToken() == this.tks.PRIVATE){
				isPrivate = true;
			}
			if(this.isNextToken(new Token(this.tks.STATIC))){
				t = this.lfc.readNextToken();
				isStatic = true;
			}
			if(this.isNextToken(new Token(this.tks.INT)) || this.isNextToken(new Token(this.tks.IDENTIFIER))){
				
				Token type = this.lfc.readNextToken();
				boolean err = false;
				if(this.isNextToken(new Token(this.tks.SQUARE_BRACKET_OPEN))){
					this.lfc.readNextToken();
					isArray = true;
					if(this.isNextToken(new Token(this.tks.SQUARE_BRACKET_CLOSE))){
						this.lfc.readNextToken();
					}else{
						err = true;
					}
				}
				if(this.isNextToken(new Token(this.tks.IDENTIFIER)) && !err){
					Token name = this.lfc.readNextToken();
					if(name.getText().equals(fieldName.getText())){
						b = true;
						if(isArray){
							this.f = new Field(new Type(new Type(type)), name.getText(), clazz, isStatic, isPrivate, false, fieldName);
						}else{
							this.f = new Field(new Type(type), name.getText(), clazz, isStatic, isPrivate, false, fieldName);
						}
						
					}
				}
			}
		}
		return b;
	}
	
	public boolean findMethodFromJavaFile(Token fieldName, Class clazz, ParameterList p){
		boolean b = false;
		boolean finished = false;
		Token token = this.lfc.lookAhead();
		while (!finished && !b)
		{
			if (token.getToken() == this.tks.METHOD_ANNOTATION){
				this.lfc.readNextToken();
				b = this.parseMethod(fieldName, clazz, p);
			}else if (token.getToken() == this.tks.EOF){
				finished = true;
			}else{
				this.lfc.readNextToken();
			}
			token = this.lfc.lookAhead();
		}
		this.lfc.closeReader();
		return b; 
	}
	
	private boolean parseMethod(Token MethodName, Class clazz, ParameterList p) {
		boolean b = false;
		boolean isPrivate = false;
		boolean isStatic = false;
		boolean isArray = false;
		if(this.isNextToken(new Token(this.tks.PUBLIC)) || this.isNextToken(new Token(this.tks.PRIVATE))){
			Token t = this.lfc.readNextToken();
			if(t.getToken() == this.tks.PRIVATE){
				isPrivate = true;
			}
			if(this.isNextToken(new Token(this.tks.STATIC))){
				t = this.lfc.readNextToken();
				isStatic = true;
			}
			if(this.isNextToken(new Token(this.tks.INT)) || this.isNextToken(new Token(this.tks.IDENTIFIER)) || this.isNextToken(new Token(this.tks.VOID))){
				Token type = this.lfc.readNextToken();
				boolean err = false;
				if(this.isNextToken(new Token(this.tks.SQUARE_BRACKET_OPEN))){
					this.lfc.readNextToken();
					isArray = true;
					if(this.isNextToken(new Token(this.tks.SQUARE_BRACKET_CLOSE))){
						this.lfc.readNextToken();
					}else{
						err = true;
					}
				}
				if(this.isNextToken(new Token(this.tks.IDENTIFIER)) && !err){
					Token name = this.lfc.readNextToken();
					if(this.isNextToken(new Token(this.tks.ROUND_BRACKET_OPEN))){
						this.lfc.readNextToken();
						ParameterList p1 = new ParameterList();
						/*System.out.println("PUBLIC: " + !isPrivate + " STATIC: " + isStatic + " TYPE: " + type.getText() + " NAME:" + name.getText());*/
						err = this.parseParameters(p1);
						if(err){
							/*for(int k=0; k<p1.getSize(); k++){
								System.out.print("Type: " + p1.getParameter(k).getType().toString() + "  ,  ");
							}
							System.out.println();*/
							
							if(name.getText().equals(MethodName.getText())){
								if(this.areParmeterListSame(p, p1)){
									b = true;
									if(isArray){
										this.m = new Method(null, name.getText(), new Type(new Type(type)), p1, clazz, isStatic, isPrivate, name);
									}else{
										this.m = new Method(null, name.getText(), new Type(type), p1, clazz, isStatic, isPrivate, name);
									}
								}
								
							}
						}else{
							b = false;
						}
					
					}
				}
			}
		}
		return b;
	}

	
	private boolean parseParameters(ParameterList p1) {
		boolean b = true;
		while(!this.isNextToken(new Token(this.tks.ROUND_BRACKET_CLOSE, ")")) && b){
			if(this.isNextToken()){
				Token type = this.lfc.readNextToken();
				if(this.isNextToken(new Token(this.tks.IDENTIFIER))){
					Token name = this.lfc.readNextToken();
					Token comma = new Token(this.tks.COMMA, ",");
					if(this.isNextToken(comma)){
						this.lfc.readNextToken();
						if(this.isNextToken(new Token(this.tks.ROUND_BRACKET_CLOSE, ")"))){
							b = false;
						}else{
							Field f = new Field(new Type(type), name.getText(), null, name);
							if(p1.containsByName(f.getName())){
								b = false;
							}else{
								p1.addParameter(f);
							}
						}
					}else if(this.isNextToken(new Token(this.tks.ROUND_BRACKET_CLOSE, ")"))){
						Field f = new Field(new Type(type), name.getText(), null, name);
						if(p1.containsByName(f.getName())){
							b = false;
						}else{
							p1.addParameter(f);
						}
					}else{
						b = false;
					}
				}else if(this.isNextToken(new Token(this.tks.SQUARE_BRACKET_OPEN))){
					this.lfc.readNextToken();
					Field f = this.parseArray(null, false, false, type);
					if(f == null){
						b = false;
					}
					Token comma = new Token(this.tks.COMMA, ",");
					if(this.isNextToken(comma)){
						this.expected(comma);
						if(this.isNextToken(new Token(this.tks.ROUND_BRACKET_CLOSE, ")"))){
							b = false;
						}else{
							if(p1.containsByName(f.getName())){
								b = false;
							}else{
								p1.addParameter(f);
							}
						}
					}else if(this.isNextToken(new Token(this.tks.ROUND_BRACKET_CLOSE, ")"))){
						if(p1.containsByName(f.getName())){
							b = false;
						}else{
							p1.addParameter(f);
						}
					}else{
						b = false;
					}
				}else{
					b = false;
				}
			}else{
				b = false;
			}
		}
		
		if(b){
			Token token = this.expected(new Token(this.tks.ROUND_BRACKET_CLOSE, ")"));
			if(token.getToken() == -1){
				b = false;
			}
		}
		return b;
	}

	private boolean isNextToken(Token t){
		Token t1 = this.lfc.lookAhead();
		if(t1.getToken() == t.getToken()){
			return true;
		}
		return false;
	}
	
	public Field getField(){
		return this.f;
	}
	
	private boolean isNextToken(){
		if(((this.lfc.lookAhead().getToken() == this.tks.INT) || (this.lfc.lookAhead().getToken() == this.tks.IDENTIFIER)
				|| (this.lfc.lookAhead().getToken() == this.tks.CHAR))){
			return true;
		}
		return false;
	}
	
	private Field parseArray(Token modifier, boolean isStatic, boolean isFinal, Token type) {
		Token t = this.expected(new Token(this.tks.SQUARE_BRACKET_CLOSE, "]"));
		if(t.getToken() != -1){
			t = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"));
			if(t.getToken() != -1){
				Field f = new Field(new Type(new Type(type)), t.getText(), null, isStatic, false, isFinal, t);
				return f;
			}
		}
		return null;
	}
	
	private Token expected(Token token) 
	{
		if(this.lfc.lookAhead().getToken() != token.getToken()){
			return new Token(-1 , "Unknown Token");
		}
		return this.lfc.readNextToken();
	}
	
	/*private boolean areMethodsSame(Method m1, Method m2){
		boolean isEqual = false;
		if((m1.getName().equals(m2.getName())) 
				&& (m1.getParameterList().getSize() == m2.getParameterList().getSize())){
			isEqual = this.areParmeterListSame(m1.getParameterList(), m2.getParameterList());
			if(isEqual){
				return isEqual;
			}
		}
		isEqual = false;
	
		return isEqual;
	}*/
	
	private boolean areParmeterListSame(ParameterList p1, ParameterList p2){
		boolean isEqual = true;
		for(int j=0; j < p1.getSize(); j++){
			isEqual = isEqual && this.areSameTypes(p1.getParameter(j).getType(), p2.getParameter(j).getType());
		}
		return isEqual;
	}
	
	private boolean areSameTypes(Type t1, Type t2) {
		if(t1.getType() == t2.getType()){
			if(t1.isArray()){
				if(t1.getBaseType().getType() == t2.getBaseType().getType()){
					return true;
				}else{
					return false;
				}
			}else{
				return true;
			}
		}else{
			return false;
		}
	}

	public Method getMethod() {
		return this.m;
	}
}
