package milestone2;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import compileTable.ByteReader;
import compileTable.JavaReader;

import Code.ArithmeticExpression;
import Code.CoditionExpression;
import Code.MethodCallExpression;
import Code.NewArrayExpression;
import Code.NewExpression;
import Code.PrintExpression;

import scanner.LookForwardReader;
import scanner.LookForwardScanner;
import scanner.Scanner;
import symbolTable.Field;
import symbolTable.Method;
import symbolTable.Class;
import symbolTable.ParameterList;
import symbolTable.TokenArrayList;
import symbolTable.Type;
import tokens.Token;
import tokens.Tokens;

public class ExpressionParser {

	private final Parser p;
		
	private final ErrorsClass errors;
	
	private final Tokens tks;
	
	private final ParameterList pList;
	
	private final BodyParser bodyParser;
	
	private String str;
	
	private final LookForwardScanner lfc;
	
	private int roundBracketOperCounter;
	
	private TokenArrayList tList;
	
	public ExpressionParser(Parser p, BodyParser bodyParser){
		this.p = p;
		this.errors = new ErrorsClass();
		this.tks = new Tokens();
		this.pList = new ParameterList();
		this.bodyParser = bodyParser;
		this.lfc  = this.p.getLfc();
		this.roundBracketOperCounter = 0;
		this.tList = new TokenArrayList();
		this.str = "";
	}
	
	public ExpressionParser(Parser p){
		this.p = p;
		this.errors = new ErrorsClass();
		this.tks = new Tokens();
		this.pList = new ParameterList();
		this.bodyParser = null;
		this.str = "";
		this.roundBracketOperCounter = 0;
		this.tList = new TokenArrayList();
		this.lfc  = this.p.getLfc();
		
	}
		
/*******************************************************************************************
 *  parseExpression(Method m, Field f, boolean isFieldAlreadyDeclared) 
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	public boolean parseExpression(Method m, boolean scopeCall){
		boolean b = true;
		Class c = m.getClazz();
		Token type = this.lfc.readNextToken();
		this.tList.add(type);
		Token t;
		if(type.getToken() == this.tks.RETURN){
			if(m.getRetrunType().isVoid()){
				this.errors.printVoidRetrunTypeError(type, m);
				this.expected(new Token(this.tks.SEMICOLON, "\";\""));
				this.lfc.readNextToken();
				b = false;
			}else{
				b = this.parseReturnSyntax(m, scopeCall);
				if(b){
					boolean e = this.parseReturnExpressionVariableType(m, scopeCall);
					if(e){
						e = this.checkTypeCompatability();
						if(e){
							Field f = this.pList.getParameter(1);
							this.makeString(true);
							this.addSimpleArtithmeticExrepssion(m, f, null);
						}
					}
				}
			}
		}else if(type.getText().equals("System")){
			t = this.expected(new Token(this.tks.DOT, "\".\""));
			if(t.getToken() != -1){
				this.tList.add(t);
				b = this.parseSystemOutPrintln(m);
			}else{
				this.expected(new Token(this.tks.SEMICOLON, "\";\""));
				this.lfc.readNextToken();
				b = false;
			}
		}else if(this.isNextToken(new Token(this.tks.DOT))){
			Token dot = this.expected(new Token(this.tks.DOT, "."));
			this.tList.add(dot);
			b = this.parseMethodOrFieldCallSyntax(this.tList, false);
			if(b){
				if(this.isNextToken(new Token(this.tks.ASSIGNMENT))){
					boolean methodCall = false;
					if(this.tList.get(this.tList.size()-1).getToken() == this.tks.ROUND_BRACKET_CLOSE){
						methodCall = true;
					}
					t = this.expected(new Token(this.tks.ASSIGNMENT, "\"=\""));
					this.tList.add(t);
					if(this.isNextToken(new Token(this.tks.NEW))){
						b = this.parseNewObjectSyntax();
						if(b){
							if(methodCall){
								int pos1 = this.tList.getPositionOfToken(this.tks.ROUND_BRACKET_OPEN);
								this.errors.printExpectedVariableNotValue(this.tList, pos1);
								this.p.setError(true);
							}else{
								boolean err = this.parseNewExpressionVariableType(m, scopeCall);
								if(err){
									err = this.checkTypeCompatability();
									if(err){
										if(!this.p.getError()){
											Field field = this.pList.getParameter(0);
											//System.out.println(field.getName() + "  EP 143" );
											this.makeString(false);
											/*if(!c.isAllreadyContainingClassReference(field.getClazz().getName())){
												c.addStaticCassReference(field.getClazz());
											}*/
											//DA SE PROVERI  OD TUKA
											/*if(!c.isAllreadyContainingFieldReference(field.getName())){
												c.addFieldReference(field);
											}else{
												field = c.getFieldFromFieldRef(field.getName());
											}
											if(!m.getAlreadyDefinedFields().containsByName(field.getName())){
												m.addToAlreadyDefinedFields(field);
											}*/
											// DO TUKA 
											//System.out.println("Expression Parser line 157 TODO");
											
											if(field.getType().isArray()){
												
												//Field classRef = this.getExistingFieldOrNull(type, m, scopeCall);
												//Field classRef = c.getFieldFromFieldRef(type.getText());
												Field classRef = c.getFieldFromClassFieldsByName(type.getText());
												System.out.println(classRef.getName());
												System.out.println(c.getFieldIntMap().containsKey(classRef) + "     + ++++++++");
												/*System.out.println(this.tList.printTokens());
												System.out.println(classRef.getName() + "    " +  field.getName());
												System.out.println(c.getFieldIntMap().containsKey(classRef));
												System.out.println(m.getFieldMap().containsKey(field));*/
												this.addNewFieldArrayWriter(m, field, c, classRef);
											}else{
												//System.out.println(field.getName()  +  "   Expression parser line 162");
												this.addNewFieldReferenceWriter(m, field, type);
											}
										}
									}
								}
							}
								
						}
						//}
					}else{
						/*int p1 = this.tList.getPositionOfToken(this.tks.SQUARE_BRACKET_OPEN+1);
						//Token to = this.tList.get(p1);
						Token to = this.tList.get(this.tList.size()-3);
						Token to1 = this.tList.get(this.tList.size()-2);
						
						if((to.getToken() != this.tks.IDENTIFIER) && (to.getToken() != this.tks.NUMBER) && (to1.getToken() == this.tks.SQUARE_BRACKET_CLOSE)){
							TokenArrayList tl = new TokenArrayList();
							tl.add(new Token(this.tks.IDENTIFIER, "\"Identifier\""));
							tl.add(new Token(this.tks.NUMBER, "\"Number\""));
							this.errors.printExpectedMoreTokensError(to, tl);
							this.p.setError(true);
							b = false;
						}else{*/
						b= this.parseArithmeticExpressionSyntax(this.tList);
						if(b){
							if(methodCall){
								int pos1 = this.tList.getPositionOfToken(this.tks.ROUND_BRACKET_OPEN);
								this.errors.printExpectedVariableNotValue(this.tList, pos1);
								this.p.setError(true);
							}else{
								boolean e = this.parseArithemticExpressionVariableType(m, scopeCall);
								if(e){
									e = this.checkTypeCompatability();
									if(e){
										Field classFil = null;
										if(m.isContainingFild(type.getText())){
											classFil = m.getFieldByName(type.getText());
										}else{
											classFil = c.getFieldFromFieldRef(type.getText());
											if(!c.isAllreadyContainingFieldReference(classFil.getName())){
												c.addFieldReference(classFil);
											}
										}
										Field fieldRef = c.getFieldFromFieldRef(this.tList.get(2).getText());
										this.makeString(false);
										Token pos = null;
										if(fieldRef.getType().isArray()){
											pos = this.tList.get(4);
										}else{
											pos = new Token(-1);
										}
										
										this.addArithmeticFieldRefWriter(m, fieldRef, pos, classFil);
										
									}
								}
							}
						}
					}
					//}
				}else if(this.isNextToken(new Token(this.tks.SEMICOLON))){
					t = this.expected(new Token(this.tks.SEMICOLON, ";"));
					this.tList.add(t);
					if(this.tList.get(this.tList.size()-2).getToken() != this.tks.ROUND_BRACKET_CLOSE){
						this.errors.printNotStatmementError(this.tList.get(2));
						this.p.setError(true);
					}else{
						t = this.tList.get(2);
						ParameterList p = new ParameterList();
						TokenArrayList tl = new TokenArrayList();
						int i = this.tList.getPositionOfToken(this.tks.ROUND_BRACKET_OPEN) + 1;
						Token t1 = this.tList.get(i-1);
						Token t2 = this.tList.get(i);
						while(t2.getToken() != this.tks.ROUND_BRACKET_CLOSE){
							tl.add(t2);
							i++;
							t2 = this.tList.get(i);
						}
						tl.add(t2);
						boolean err = this.parseParameters(tl, m, p);
						if(err){
							err = this.methodFieldRefExistance(type, t, t1, m, scopeCall, p, null, true, false);
							if(err){
								if(!m.isContainingFild(type.getText())){
									Field f = c.getFieldFromClassFieldsByName(type.getText());
									if(!c.isAllreadyContainingFieldReference(f.getName())){
										c.addFieldReference(f);
									}
								}
								Method mRef = c.getMethoddFromClassMethodReferenceByName(t.getText(), p);
								
								//System.out.println(mRef.getRetrunType());
								this.makeString(true);
								this.addMethodCallWriter(m, scopeCall, mRef, p);
							}
						}
						//System.out.println(this.tList.printTokens()  +  "    ExpressionParser parseEpression() line at 227");
					}
				}else{
					this.expected(new Token(this.tks.ASSIGNMENT, "\"=\""), new Token(this.tks.SEMICOLON, "\";\""));
					this.lfc.readNextToken();
					b = false;
				}
			}
		}else if(this.isNextToken(new Token(this.tks.SQUARE_BRACKET_OPEN))){
			t = this.expected(new Token(this.tks.SQUARE_BRACKET_OPEN, "["));
			this.tList.add(t);
			b = this.parseArraySyntax(this.tList);
			if(b){
				t = this.expected(new Token(this.tks.ASSIGNMENT, "="));
				if(t.getToken() != -1){
					this.tList.add(t);
					if(this.isNextToken(new Token(this.tks.NEW))){
						int pos1 = this.tList.getPositionOfToken(this.tks.SQUARE_BRACKET_OPEN)+1;
						Token to = this.tList.get(pos1);
						if(to.getToken() != this.tks.SQUARE_BRACKET_CLOSE){
							this.errors.printError(to, new Token(this.tks.SQUARE_BRACKET_CLOSE,"\"]\""));
							this.p.setError(true);
							b = false;
						}
						else{
							b = this.parseNewObjectSyntax();
							if(b){
								boolean help = this.parseNewExpressionVariableType(m, scopeCall);
								if(help){
									help = this.checkTypeCompatability();
									if(help){
										Field field = this.pList.getParameter(0);
										if(field.getType().isClass()){
											Class classRef = this.pList.getParameter(0).getType().getClazz();
											if(!c.isAllreadyContainingClassReference(classRef.getName()) && !c.getName().equals(m.getClazz().getName())){
												c.addClassReference(classRef);
											}
											if(!c.isAllreadyContainingFieldReference(field.getName())){
												c.addFieldReference(field);
											}else{
												field = c.getFieldFromFieldRef(field.getName());
											}
											if(!m.getAlreadyDefinedFields().containsByName(field.getName())){
												m.addToAlreadyDefinedFields(field);
											}
											
										}
										
										if(!this.p.getError()){
											this.makeString(false);
											this.addNewFieldArrayWriter(m, field, m.getClazz(), null);
										}
									}
								}
							}
						}
					}else{
						
						int pos1 = this.tList.getPositionOfToken(this.tks.SQUARE_BRACKET_OPEN)+1;
						Token to = this.tList.get(pos1);
						if((to.getToken() != this.tks.IDENTIFIER) && (to.getToken() != this.tks.NUMBER)){
							TokenArrayList tl = new TokenArrayList();
							tl.add(new Token(this.tks.IDENTIFIER, "\"Identifier\""));
							tl.add(new Token(this.tks.NUMBER, "\"Number\""));
							this.errors.printExpectedMoreTokensError(to, tl);
							this.p.setError(true);
							b = false;
						}else{
							b = this.parseArithmeticExpressionSyntax(this.tList);
							if(b){
								//this.tList.printTokens();
								boolean e = this.parseArithemticExpressionVariableType(m, scopeCall);
								if(e){
									e = this.checkTypeCompatability();
								}
							}
						}
					}
				}else{
					this.lfc.readNextToken();
					this.expected(new Token(this.tks.SEMICOLON, ";"));
					b = false;
				}
			}
		}else if(this.isNextToken(new Token(this.tks.IDENTIFIER))){
			Token name = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"));
			this.tList.add(name);
			if(this.isNextToken(new Token(this.tks.SEMICOLON))){
				t = this.expected(new Token(this.tks.SEMICOLON, ";"));
				this.tList.add(t);
				boolean err = this.checkAndPrintFieldAlreadyExists(name, m, scopeCall, true);
				if(!err){
					this.checkTypeExistanceOrAddToLocalsWithoutDeclaration(type, name, m);
				}
			}else if(this.isNextToken(new Token(this.tks.ASSIGNMENT))){
				t = this.expected(new Token(this.tks.ASSIGNMENT, "="));
				this.tList.add(t);
				if(this.isNextToken(new Token(this.tks.NEW))){
					b = this.parseNewObjectSyntax();
					if(b){
						boolean err = this.checkAndPrintFieldAlreadyExists(name, m, scopeCall, true);
						if(!err){
							err = this.checkTypeExistanceOrAddToLocals(type, name, m);
							if(err){
								err = this.parseNewExpressionVariableType(m, scopeCall);
								if(err){
									err = this.checkTypeCompatability();
									if(err){
										Field field = m.getFieldByName(name.getText());
										Class classRef = field.getType().getClazz();
										if(!c.isAllreadyContainingClassReference(classRef.getName())){
											c.addClassReference(classRef);
										}
										if(!m.getAlreadyDefinedFields().containsByName(field.getName())){
											m.addToAlreadyDefinedFields(field);
										}
										
										/*if(c.isAllreadyContainingFieldReference(name.getText())){
											field = c.getFieldFromFieldRef(name.getText()); 
											Class classRef = field.getType().getClazz();
											if(!c.isAllreadyContainingClassReference(classRef.getName())){
												c.addCassReference(classRef);
											}
										}else if(m.isContainingFild(name.getText())){
											field = m.getFieldByName(name.getText());
											Class classRef = field.getType().getClazz();
											if(!c.isAllreadyContainingClassReference(classRef.getName())){
												c.addCassReference(classRef);
											}
										}else{
											field = this.getExistingFieldOrNull(name, m, scopeCall);
											Class classRef = field.getType().getClazz();
											if(!c.isAllreadyContainingClassReference(classRef.getName())){
												c.addCassReference(classRef);
											}
											c.addFieldReference(field);
										}*/
										
										if(!this.p.getError()){
											this.makeString(false);
											this.addNewFieldWriter(m, field);
										}
									}
								}
							}
						}
					}
				}else{
					b = this.parseArithmeticExpressionSyntax(this.tList);
					if(b){
						boolean err = this.checkAndPrintFieldAlreadyExists(name, m, scopeCall, true);
						if(!err){
							err = this.checkTypeExistanceOrAddToLocals(type, name, m);
							if(err){
								err = this.parseArithemticExpressionVariableType(m, scopeCall);
								if(err){
									err = this.checkTypeCompatability();
									if(err && !this.p.getError()){
										Field field = m.getFieldByName(name.getText());
										//Field field = this.pList.getParameter(0);
										this.makeString(false);
										this.addArithmeticFieldWriter(m, field);
									}
								}
							}
						}
					}
				}
			}else{
				this.expected(new Token(this.tks.SEMICOLON, ";"), new Token(this.tks.ASSIGNMENT, "="));
				this.lfc.readNextToken();
				this.expected(new Token(this.tks.SEMICOLON, ";"));
				b = false;
			}
		}else if(this.isNextToken(new Token(this.tks.ASSIGNMENT))){
			t = this.expected(new Token(this.tks.ASSIGNMENT, "="));
			this.tList.add(t);
			if(this.isNextToken(new Token(this.tks.NEW))){
				b = this.parseNewObjectSyntax();
				if(b){
					boolean help = this.parseNewExpressionVariableType(m, scopeCall);
					if(help){
						help = this.checkTypeCompatability();
						if(help){
							
							help = this.parseMethodFieldCallFromThisClass(type, null, m, scopeCall, null, null, false, true);
							if(help){
								Field field = c.getFieldFromFieldRef(type.getText());
								
								if(!this.p.getError()){
									this.makeString(false);
									this.addNewFieldWriter(m, field);
								}
							}
						}
					}
				}
			}else{
				b = this.parseArithmeticExpressionSyntax(this.tList);
				if(b){
					boolean help = this.parseArithemticExpressionVariableType(m, scopeCall);
					if(help){
						boolean e = this.checkTypeCompatability();
						if(e && !this.p.getError()){
							//Field field = this.pList.getParameter(0);
							Field fi = null;
							this.makeString(false);
							
							if(!m.isContainingFild(type.getText())){
								//fi = c.getFieldFromClassFieldsByName(type.getText());
								fi = c.getFieldFromFieldRef(fi.getName());
								/*if(!c.isAllreadyContainingFieldReference(fi.getName())){
									c.addFieldReference(fi);
								}else{
									fi = c.getFieldFromFieldRef(fi.getName());
								}*/
							}else{
								fi = m.getFieldByName(type.getText());
							}
							if(!m.getAlreadyDefinedFields().containsByName(fi.getName())){
								m.addToAlreadyDefinedFields(fi);
								m.addFieldToStackFrameFieldCounter(fi);
							}
							this.addArithmeticFieldWriter(m, fi);
						}
					}
				}
			}
		}else if(this.isNextToken(new Token(this.tks.ROUND_BRACKET_OPEN))){
			b = this.parseMethodOrFieldCallSyntax(this.tList, false);
			if(b){
				t = this.expected(new Token(this.tks.SEMICOLON, "\";\""));
				if(t.getToken() != -1){
					this.tList.add(t);
					t = this.tList.get(1);
					ParameterList pr = new ParameterList();
					TokenArrayList tl = new TokenArrayList();
					int i = 2;
					Token t2 = this.tList.get(i);
					while(t2.getToken() != this.tks.ROUND_BRACKET_CLOSE){
						tl.add(t2);
						i++;
						t2 = this.tList.get(i);
					}
					tl.add(t2);
					boolean tr = this.parseParameters(tl, m, pr);
					if(tr){
						tr = parseMethodFieldCallFromThisClass(type, t, m, scopeCall, pr, null, true, false);
						if(tr){
							Method mRef = c.getMethoddFromClassMethodReferenceByName(type.getText(), pr);
							this.makeString(true);
							//ystem.out.println(this.tList.printTokens());
							this.addMethodCallWriter(m, scopeCall, mRef, pr);
						}
					}
					
				}else{
					b = false;
				}
			}
		}else{
			this.expected(new Token(this.tks.SEMICOLON, ";"), new Token(this.tks.ASSIGNMENT, "="));
			this.lfc.readNextToken();
			this.expected(new Token(this.tks.SEMICOLON, ";"));
			b = false;
		}
		
/*************************************************************************************************************************/
		/*if(this.isNextToken(new Token(this.tks.ASSIGNMENT))){
			Token assigementToken = lfc.readNextToken();
			Field f;
			if(m.isContainingFildMethodAndClass(type.getText(),false)){
				f = m.findFieldInsideMethoAndClass(type.getText());
				if(this.isNextToken(new Token(this.tks.NEW))){
					this.lfc.readNextToken();
					//TODO str to make to be it for parameters  -->  expression.parseTokenNew(s, str);
					b = expression.parseTokenNew(f, m);
					if(!b){
						break;
					}else{
						if(!this.clazz.isAllreadyContainingClassReference(f.getType().toString())
								&& !this.error){
							
							this.clazz.addCassReference(f.getClazz());
						}
						if(!this.error){
							expression.addNewFieldWriter(m, f);
						}
					}
				}else{
					b = expression.parseExpression(m, f, true, false);
					if(b){
						if(!this.clazz.isAllreadyContainingClassReference(f.getType().toString()) && !this.isIdentifierToken(f.getToken())){
							this.clazz.addCassReference(f.getClazz());
						}
						if(!this.error){
							expression.addArithmeticFieldWriter(m, f);
						}
					}
				}
				if(!this.error){
					if(m.getLocalVariables().containsByName(f.getName())){
						if(!m.getAlreadyDefinedFields().containsByName(f.getName())){
							m.addToAlreadyDefinedFields(f);
							m.addFieldToStackFrameFieldCounter(f);
						}
					}
				}
			}else{
				f = new Field(null, type.getText(), null, type);
				b = expression.parseExpression(m, f, true, false);
				if(b){
					if(!this.clazz.isAllreadyContainingClassReference(f.getType().toString()) && !this.isIdentifierToken(f.getToken())
							&& !this.error){
						this.clazz.addCassReference(f.getClazz());
					}
					if(!this.error){
						expression.addArithmeticFieldWriter(m, f);
					}
				}
				if(!this.error){ 
					if(m.getLocalVariables().containsByName(f.getName())){
						if(!m.getAlreadyDefinedFields().containsByName(f.getName())){
							m.addToAlreadyDefinedFields(f);
							m.addFieldToStackFrameFieldCounter(f);
						}
					}
				}
			}
		}else if(this.isNextToken(new Token(this.tks.DOT))){
			this.lfc.readNextToken();
			ReferenceCallParser refCall = new ReferenceCallParser(this.lfc, this.clazz, this.error, this);
			b = refCall.parseMethodOrFieldCall(type, m, null);
			this.error = refCall.getError();
			if(!b){
				break;
			}
		}else if(this.isNextToken(new Token(this.tks.IDENTIFIER))){
			Token name = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"));
			Token equal = new Token(this.tks.ASSIGNMENT, "=");
			if(this.isNextToken(equal)){
				this.lfc.readNextToken();
				String str = this.getFilePath(type);
				Field s;
				if(m.isContainingFildMethodAndClass(name.getText(),false)){
					s = m.findFieldInsideMethoAndClass(name.getText());
				}else{
					Class cl = this.clazz.getUsedClassByName(type.getText());
					if(cl == null){
						s = new Field(new Type(type), name.getText(), new Class(type.getText(), null, str), name);
					}else{
						s = new Field(new Type(type), name.getText(), cl, name);
					}
				}
				ExpressionParser expression = new ExpressionParser(this);
				if(this.isNextToken(new Token(this.tks.NEW))){
					this.lfc.readNextToken();
					//TODO str to make to be it for parameters  -->  expression.parseTokenNew(s, str);
					b = expression.parseTokenNew(s, m);
					if(!b){
						break;
					}else{
						//m.addLocalVariable(s);
						if(m.isContainingFildMethodAndClass(s.getName(), true)){
							this.errors.printContainsFieldError(s.getToken());
							this.error = true;
						}else{
							m.addLocalVariable(s);
						}
						if(!this.clazz.isAllreadyContainingClassReference(s.getType().toString())){
							this.clazz.addCassReference(s.getClazz());
						}
						this.checkIfFileOrClassExists(type);
						if(!this.error){
							
							expression.addNewFieldWriter(m, s);
						}
					}
				}else{
					b = expression.parseExpression(m, s, true, false);
					if(b){
						
						if(!this.clazz.isAllreadyContainingClassReference(s.getType().toString()) && !this.isIdentifierToken(s.getToken())
								&& !this.error){
							this.clazz.addCassReference(s.getClazz());
						}
						if(m.isContainingFildMethodAndClass(s.getName(), true)){
							this.errors.printContainsFieldError(s.getToken());
							this.error = true;
						}else{
							m.addLocalVariable(s);
						}
						this.checkIfFileOrClassExists(type);
						if(!this.error){
							expression.addArithmeticFieldWriter(m, s);
						}
					}else{
						break;
					}
				}
				if(!this.error){
					if(m.getLocalVariables().containsByName(s.getName())){
						if(!m.getAlreadyDefinedFields().containsByName(s.getName())){
							m.addToAlreadyDefinedFields(s);
							m.addFieldToStackFrameFieldCounter(s);
						}
					}
				}
			}else if(this.isNextToken(new Token(this.tks.SEMICOLON,";"))){
				b = m.isContainingFildMethodAndClass(name.getText(), true);
				if(b){
					Field f = m.findFieldInsideMethoAndClass(name.getText());
					this.errors.printContainsFieldError(name, f.getToken());
					b = false;
				}else{
					String str = this.getFilePath(type);
					this.checkIfFileOrClassExists(type);
					Field s;
					Class cl = this.clazz.getUsedClassByName(type.getText());
					if(cl == null){
						s = new Field(new Type(type), name.getText(), new Class(type.getText(), null, str), name);
					}else{
						s = new Field(new Type(type), name.getText(), cl, name);
					}
					if(m.isContainingFildMethodAndClass(s.getName(), true)){
						this.errors.printContainsFieldError(s.getToken());
						this.error = true;
					}else{
						m.addLocalVariable(s);
					}
					this.expected(new Token(this.tks.SEMICOLON, ";"));
					b = true;
				}
			}else{
				Token err = this.lfc.readNextToken();
				this.errors.printError(err, new Token(this.tks.ASSIGNMENT, "="), new Token(this.tks.SEMICOLON, ";"));
				this.expected(new Token(this.tks.SEMICOLON, ";"));
				b = false;
			}
		}else{
			this.expected(new Token(this.tks.IDENTIFIER, "Identifier"));
			this.lfc.readNextToken();
			this.expected(new Token(this.tks.SEMICOLON, ";"));
			b=false;
			break;
		}
/**********************************************************************************************************************************************/
		/*this.str = "= ";
		while(!this.isNextToken(new Token(this.tks.SEMICOLON, ";"))){
			Token t = this.expected(new Token(this.tks.NUMBER, "Number"), new Token(this.tks.IDENTIFIER, "Identifier"));
			if(this.iskCorrectToken(t)){
				this.str = this.str +  " " + t.getText();
				if(!this.isNextToken(new Token(this.tks.SEMICOLON, ";"))){
					if(!this.isNextTokenOperator()){
						Token err = this.p.getLfc().readNextToken();
						this.errors.printExpectsOperatorError(err);
						this.expected(new Token(this.tks.SEMICOLON, ";"));
						b = false;
						break;
					}else{
						this.str = str + " " +this.p.getLfc().readNextToken().getText();
						if(this.isNextToken(new Token(this.tks.NUMBER)) || this.isNextToken(new Token(this.tks.IDENTIFIER))){
							if(scopeCall){
								this.pList.addParameter(this.makeSkopeField(m, t));
							}else{
								this.pList.addParameter(this.makeField(m, t));
							}
						}else{
							Token nextToken = this.p.getLfc().readNextToken();
							this.errors.printError(nextToken, new Token(this.tks.NUMBER, "Number"), new Token(this.tks.IDENTIFIER, "Identifier"));
							this.expected(new Token(this.tks.SEMICOLON, ";"));
							b = false;
							break;
						}
					}
				}else{
					if(scopeCall){
						this.pList.addParameter(this.makeSkopeField(m, t));
					}else{
						this.pList.addParameter(this.makeField(m, t));
					}
				}
			}else{
				this.p.getLfc().readNextToken();
				this.expected(new Token(this.tks.SEMICOLON, ";"));
				b = false;
				break;
			}
		}

		
		if(b){
			Token t = this.expected(new Token(this.tks.SEMICOLON, ";"));
			if(this.iskCorrectToken(t)){
				str = str + t.getText();
				b = this.checkParametersCompatability();
			}else{
				b = false;
			}
		}
		if(!isFieldAlreadyDeclared && b){
			if(m.isContainingFild(f.getName())){
				this.errors.printContainsFieldError(f.getToken());
				b = false;
			}
		}
		/*if(b){
			
			if(this.ifparser == null){
				this.addByteWriterToMethodBody(str,m);
			}else{
				this.addByteWriterToIfScope(str, m);
			}
		}*/
/*************************************************************AB HIER FUNKTIONIERT********************************************************************/
	/*public boolean parseExpression(Method m, Field f, boolean isFieldAlreadyDeclared, boolean scopeCall){
		boolean b = true;
		this.pList.addParameter(f);
		if(this.isNextTokenSubAddOperator()){
			this.p.getLfc().readNextToken();
		}

		this.str = "= ";
		while(!this.isNextToken(new Token(this.tks.SEMICOLON, ";"))){
			Token t = this.expected(new Token(this.tks.NUMBER, "Number"), new Token(this.tks.IDENTIFIER, "Identifier"));
			if(this.iskCorrectToken(t)){
				this.str = this.str +  " " + t.getText();
				if(!this.isNextToken(new Token(this.tks.SEMICOLON, ";"))){
					if(!this.isNextTokenOperator()){
						Token err = this.p.getLfc().readNextToken();
						this.errors.printExpectsOperatorError(err);
						this.expected(new Token(this.tks.SEMICOLON, ";"));
						b = false;
						break;
					}else{
						this.str = str + " " +this.p.getLfc().readNextToken().getText();
						if(this.isNextToken(new Token(this.tks.NUMBER)) || this.isNextToken(new Token(this.tks.IDENTIFIER))){
							if(scopeCall){
								this.pList.addParameter(this.makeSkopeField(m, t));
							}else{
								this.pList.addParameter(this.makeField(m, t));
							}
						}else{
							Token nextToken = this.p.getLfc().readNextToken();
							this.errors.printError(nextToken, new Token(this.tks.NUMBER, "Number"), new Token(this.tks.IDENTIFIER, "Identifier"));
							this.expected(new Token(this.tks.SEMICOLON, ";"));
							b = false;
							break;
						}
					}
				}else{
					if(scopeCall){
						this.pList.addParameter(this.makeSkopeField(m, t));
					}else{
						this.pList.addParameter(this.makeField(m, t));
					}
				}
			}else{
				this.p.getLfc().readNextToken();
				this.expected(new Token(this.tks.SEMICOLON, ";"));
				b = false;
				break;
			}
		}

		
		if(b){
			Token t = this.expected(new Token(this.tks.SEMICOLON, ";"));
			if(this.iskCorrectToken(t)){
				str = str + t.getText();
				b = this.checkParametersCompatability();
			}else{
				b = false;
			}
		}
		if(!isFieldAlreadyDeclared && b){
			if(m.isContainingFild(f.getName())){
				this.errors.printContainsFieldError(f.getToken());
				b = false;
			}
		}
		/*if(b){
			
			if(this.ifparser == null){
				this.addByteWriterToMethodBody(str,m);
			}else{
				this.addByteWriterToIfScope(str, m);
			}
		}*/
/*************************************************************BIS HIER FUNKTIONIERT********************************************************************/
		return b;
	}



/*******************************************************************************************
 *  boolean parseReturnExpressionVariableType(Method m, boolean scopeCall)
 *  	- 
 *******************************************************************************************/
	private boolean parseReturnExpressionVariableType(Method m, boolean scopeCall) {
		boolean b = true; 
		Field f = new Field(m.getRetrunType(), m.getName());
		this.pList.addParameter(f);
		int i = 1;
		Token t = this.tList.get(i);
		i++;
		if(t.isNumberToken()){
			f = new Field(new Type(new Token(this.tks.INT, "Integer")), t);
			this.pList.addParameter(f);
		}else{
			Token t1 = this.tList.get(i);
			i++;
			if(t1.getToken() != this.tks.SEMICOLON){
				//TODO
			}else{
				b = this.checkIfFieldExsists(t, m, scopeCall, false);
				if(b){
					f = this.getExistingFieldOrNull(t, m, scopeCall);
					this.pList.addParameter(f);
					
				}else{
					this.errors.printFieldDoesNotExists(t);
					this.p.setError(true);
					b = false;
				}
			}
		}
		return b;
	}

/*******************************************************************************************
 *  boolean parseArithemticExpression(Method m, Token type, Token object, boolean scopeCall)
 *  	- 
 *******************************************************************************************/
	private boolean parseArithemticExpressionVariableType(Method m, boolean scopeCall) {
		boolean b = true;
		Token t, t1;
		int i = 0;
		t = this.tList.get(i);
		i++;
		t1 = this.tList.get(i);
		i++;
		if(t1.isIdentifierToken()){
			b = this.checkIfFieldExsists(t1, m, scopeCall, false);
			if(b){
				Field f = this.getExistingFieldOrNull(t1, m, scopeCall);
				this.pList.addParameter(f);
				i++;
			}else{
				this.errors.printFieldDoesNotExists(t1);
				this.p.setError(true);
			}
		}else if(t1.isDotToken()){
			t1 = this.tList.get(i);
			i++;
			Token t2 = this.tList.get(i);
			i++;
			if(t2.isSquareBracketOpenToken()){
				t2 = this.tList.get(i);
				i = i + 2;
				b = this.methodFieldRefExistance(t, t1, t2, m, scopeCall, null, null, true, false);
			}else if(t2.isAssignmentToken()){
				b = this.methodFieldRefExistance(t, t1, null, m, scopeCall, null, null, true, false);
			}
		}else if(t1.isAssignmentToken()){
			b = this.checkIfFieldExsists(t, m, scopeCall, false);
			if(b){
				Field f = this.getExistingFieldOrNull(t, m, scopeCall);
				this.pList.addParameter(f);
				/*if(!m.getAlreadyDefinedFields().containsByName(f.getName())){
					m.addToAlreadyDefinedFields(f);
					m.addFieldToStackFrameFieldCounter(f);
				}*/
			}else{
				this.errors.printFieldDoesNotExists(t);
				this.p.setError(true);
			}
		}else if(t1.isSquareBracketOpenToken()){
			t1 = this.tList.get(i);
			i = i + 2;
			b = this.parseArrayExistanceOrPrintsError(t, t1, m, scopeCall);
		}
		
		while(i < this.tList.size()){
			t = this.tList.get(i);
			if(t.isNumberToken()){
				Field f = new Field(new Type(new Token(this.tks.INT, "Integer")), t.getText(), null, t);
				this.pList.addParameter(f);
				i++;
			}else if(t.isIdentifierToken()){
				t1 = this.tList.get(i+1);
				if(t1.isDotToken()){
					i = i + 2;
					t1 = this.tList.get(i);
					i++;
					Token t2 = this.tList.get(i);
					i++;
					Type expected = this.pList.getParameter(0).getType();
					if(t2.isSquareBracketOpenToken()){
						t2 = this.tList.get(i);
						i = i + 2;
						b = this.methodFieldRefExistance(t, t1, t2, m, scopeCall, null, expected, false, false);
					}else if(t2.isRoundBracketOpenToken()){
						Token t3 = this.tList.get(i);
						ParameterList p = new ParameterList();
						TokenArrayList tl = new TokenArrayList();
						while(t3.getToken() != this.tks.ROUND_BRACKET_CLOSE){
							tl.add(t3);
							i++;
							t3 = this.tList.get(i);
						}
						tl.add(t3);
						i++;
						b = this.parseParameters(tl, m, p);
						if(b){
							b = this.methodFieldRefExistance(t, t1, t2, m, scopeCall, p, expected, false, false);
						}
					}else{
						b = this.methodFieldRefExistance(t, t1, null, m, scopeCall, null, expected, false, false);
					}
				}else if(t1.isSquareBracketOpenToken()){
					t1 = this.tList.get(i);
					i = i + 2;
					b = this.parseArrayExistanceOrPrintsError(t, t1, m, scopeCall);
				}else{
					b = this.checkIfFieldExsists(t, m, scopeCall, false);
					if(b){
						Field f = null; 
						if(m.isContainingFild(t.getText())){ 
							f = this.getExistingFieldOrNull(t, m, scopeCall);
							if(!m.getAlreadyDefinedFields().containsByName(t.getText())){
								this.errors.printFieldNotDeclaredError(t);
								this.p.setError(true);
								b = false;
							}
						}else{
							Class c = m.getClazz();
							if(!c.isAllreadyContainingFieldReference(t.getText())){
								f = c.getFieldFromClassFieldsByName(t.getText());
								c.addFieldReference(f);
							}else{
								f = c.getFieldFromFieldRef(t.getText());
							}
						}
						
						this.pList.addParameter(f);
						i++;
					}else{
						this.errors.printFieldDoesNotExists(t);
						this.p.setError(true);
					}
				}
			}else{
				i++;
			}
		}
		return b;
	}
	
/*******************************************************************************************
 *  boolean parseNewExpressionVariableType()
 *  	- 
 *******************************************************************************************/
	private boolean parseNewExpressionVariableType(Method m, boolean scopeCall) {
		boolean b = true;
		int i = 0;	
		Token t = this.tList.get(i);
		i++;
		Token t1 = this.tList.get(i);
		i++;
		Field f;
		if(t1.isAssignmentToken()){
			b = this.checkIfFieldExsists(t, m, scopeCall, false);
			if(b){
				f = this.getExistingFieldOrNull(t, m, scopeCall);
				this.pList.addParameter(f);
			}else{
				this.errors.printFieldDoesNotExists(t);
				b = false;
			}
		}else if(t1.isDotToken()){
			t1 = this.tList.get(i);
			i++;
			Token t2 = this.tList.get(i);
			i++;
			if(t2.isSquareBracketOpenToken()){
				t2 = this.tList.get(i);
				i = i + 2;
				
				b = this.methodFieldRefExistance(t, t1, t2, m, scopeCall, null, null, true, true);
			}else if(t2.isAssignmentToken()){
				b = this.methodFieldRefExistance(t, t1, null, m, scopeCall, null, null, true, true);
			}
		}else if(t1.isSquareBracketOpenToken()){
			i ++;
			t1 = this.tList.get(i);
			b = this.checkAndPrintFieldAlreadyExists(t, m, scopeCall, true);
			if(!b){
				b = this.checkArrayExistanceOrAddToLocals(t, t1, m);
				if(b){
					Field fl = this.getExistingFieldOrNull(t1, m, scopeCall);
					this.pList.addParameter(fl);
					i++;
				}
			}else{
				b = false;
			}
		}else{
			b = this.checkIfFieldExsists(t1, m, scopeCall, false);
			if(b){
				f = this.getExistingFieldOrNull(t1, m, scopeCall);
				this.pList.addParameter(f);
			}else{
				this.errors.printFieldDoesNotExists(t1);
				b = false;
			}
			i++;
		}
		t1 = this.tList.get(i);
		while(i < this.tList.size() && b){
			if(t1.isIdentifierToken()){
				Class c = m.getClazz();
				Class cl = c.getUsedOrImportedClassByName(t1.getText());
				if(cl == null){
					if(!this.checkFile(c.getFilePath()  + t1.getText() + ".java")){
						this.errors.printFileDoesNotExists(t1, t1.getText());
						b = false;
						this.p.setError(true);
					}else{
						String dirPath = c.getFilePath();
						String packageName = dirPath + "/" + t1.getText();
						cl = new Class(t1.getText(), c.getSuperClass(), packageName, dirPath);
						f = new Field(new Type(cl), t1);
						this.pList.addParameter(f);
					}
				}else{
					f = new Field(new Type(cl), t1);
					this.pList.addParameter(f);
				}
				break;
			}else if(t1.isIntToken()){
				f = new Field(new Type(new Type(new Token(this.tks.INT, "\"Integer\""))), t1);
				this.pList.addParameter(f);
				break;
			}
			t1 = this.tList.get(i);
			i++;
		}
		return b;
	}



/*******************************************************************************************
 *  boolean parseArrayExistanceOrPrintsError(Token t, Token t1, Method m,boolean scopeCall)
 *  	- 
 *******************************************************************************************/
	private boolean parseArrayExistanceOrPrintsError(Token t, Token t1, Method m, boolean scopeCall) {
		boolean b = true;
		Field f = null;
		if(this.checkIfFieldExsists(t, m, scopeCall, false)){
			f = this.getExistingFieldOrNull(t, m, scopeCall);
			if(f.getType().isArray()){
				if(t1.isIdentifierToken()){
					if(this.checkIfFieldExsists(t1, m, scopeCall, false)){
						Field parameter = this.getExistingFieldOrNull(t1, m, scopeCall);
						if(!parameter.getType().isInteger()){
							this.errors.printNotCompatibleType(parameter, t1, new Token(this.tks.INT, "Integer"));
							this.p.setError(true);
							b = false;
						}
					}else{
						this.errors.printFieldDoesNotExists(t1);
						this.p.setError(true);
						b = false;
					}
				}
			}else{
				this.errors.printExpectedArrayFieldRef(f);
				this.p.setError(true);
				b = false;
			}
			if(b){
				this.pList.addParameter(new Field(f.getType().getBaseType(), f.getName(), null, f.getToken()));
			}
		}else{
			this.errors.printFieldDoesNotExists(t);
			this.p.setError(true);
			b = false;
		}
		return b;
	}

/*******************************************************************************************
 *  boolean methodFieldRefExistance(Token t, Token t1, Object object, Method m, boolean scopeCall)
 *  	- 
 *******************************************************************************************/
	private boolean methodFieldRefExistance(Token classR, Token fieldR, Token p2, Method m, boolean scopeCall, ParameterList pl, Type expectedType, boolean leftSide, boolean newCall) {
		boolean b = true;
		if(this.checkIfFieldExsists(classR, m, scopeCall, false)){
			Field classReference = this.getExistingFieldOrNull(classR, m, scopeCall);
			if(!classReference.getType().isClass()){
				this.errors.printVariableIsNotAnObject(classReference, classR);
				this.p.setError(true);
				b = false;
			}else{
				if(!m.isContainingFild(classR.getText())){
					Class c = m.getClazz();
					if(!c.isAllreadyContainingFieldReference(classReference.getName())){
						c.addFieldReference(classReference);
						//this.addClassFromFieldRefToStaticCLassRef(c, classReference, false);
						this.addClassFromFieldRefToStaticCLassRef(c, classReference, true);
					}else{
						classReference = c.getFieldFromFieldRef(classReference.getName());
					}
				}
				
				if(classReference.getType().getClazz().getName().equals(m.getClazz().getName())){
					b = this.parseMethodFieldCallFromThisClass(fieldR, p2, m, scopeCall, pl, expectedType, leftSide, newCall);
				}else if((p2 == null) || !p2.isRoundBracketOpenToken()){
					
					//b = this.checkFieldRef(classReference, fieldR, p2, m, scopeCall);
					b = this.checkFieldRef(classReference, fieldR, m, scopeCall, newCall);
					if(b){
						Field fieldRef = this.pList.getParameter(this.pList.getSize() - 1);
						/*
						System.out.println("TUKAA");*/
						if(p2 != null){
							
							this.pList.remove(this.pList.getSize()-1);
							if(p2.isIdentifierToken()){
								if(this.checkIfFieldExsists(p2, m, scopeCall, false)){
									Field par = this.getExistingFieldOrNull(p2, m, scopeCall);
									if(!par.getType().isInteger()){
										this.errors.printNotCompatibleType(par, p2, new Token(this.tks.INT, "Integer"));
										this.p.setError(true);
										b = false;
									}
								}else{
									this.errors.printFieldDoesNotExists(p2);
									this.p.setError(true);
									b = false;
								}
							}
							if(!fieldRef.getType().isArray() && b){
								this.errors.printExpectedArrayFieldRef(fieldRef);
								this.p.setError(true);
								b = false;
							}
							if(b){
								Field fil = new Field(fieldRef.getType().getBaseType(), fieldRef.getName(), fieldRef.getClazz(), fieldRef.getToken());
								this.pList.addParameter(fil);
							}
							
						}
					
						if(!fieldRef.isStatic() && b){
							if(m.isContainingFild(classR.getText()) && !leftSide){
								if(!m.getAlreadyDefinedFields().containsByName(classR.getText())){
									this.errors.printFieldNotDeclaredError(classR);
									this.p.setError(true);
									b = false;
								}
							}
						}
						
					}
				}else{
					if(m.isContainingFild(classR.getText()) && !leftSide){
						if(!m.getAlreadyDefinedFields().containsByName(classR.getText()) ){
							this.errors.printFieldNotDeclaredError(classR);
							this.p.setError(true);
							b = false;
						}
					}
					if(b){
						b = this.checkMethodRef(classReference, fieldR, p2, m, scopeCall, pl);
					}
					
				}
			}
			/*if(!m.getAlreadyDefinedFields().containsByName(classR.getText())){
				this.errors.printf
				this.p.setError(true);
				b = false;
			}*/
		}else{
			this.errors.printFieldDoesNotExists(classR);
			this.p.setError(true);
			b = false;
		}
		return b;
	}
/*******************************************************************************************
 *  addClassFromFieldRefToStaticCLassRef(Class c, Field classReference)
 *  	- 
 *******************************************************************************************/
	private void addClassFromFieldRefToStaticCLassRef(Class c, Field classReference, boolean checkType) {
		Class cl = classReference.getClazz();
		if(!c.isAllreadyContainingClassReference(cl.getName()) && !checkType){
			if(!cl.getName().equals(c.getName())){
				c.addStaticCassReference(cl);
			}
		}
		cl = classReference.getType().getClazz();
		if(!c.isAllreadyContainingClassReference(cl.getName()) && checkType){
			if(!cl.getName().equals(c.getName())){
				c.addStaticCassReference(cl);
			}
		}
		
	}

/*******************************************************************************************
 *  void parseMethodFieldCallFromThisClass(Token fieldR, Token p2, Method m, boolean scopeCall, ParameterList pl)
 *  	- 
 *******************************************************************************************/
	private boolean parseMethodFieldCallFromThisClass(Token fieldR, Token p2, Method m, boolean scopeCall, ParameterList pl, Type expectedRetrunType, boolean leftSide, boolean newCall) {
		boolean b = true;
		Class c = m.getClazz();
		if((p2 == null) || !p2.isRoundBracketOpenToken()){
			if(c.isAllreadyContainingField(fieldR.getText())){
				Field fieldRef = c.getFieldFromClassFieldsByName(fieldR.getText());
				if(!c.isAllreadyContainingFieldReference(fieldRef.getName())){
					c.addFieldReference(fieldRef);
				}else{
					fieldRef = c.getFieldFromFieldRef(fieldRef.getName());
				}
				if(p2 != null){
					if(p2.isIdentifierToken()){
						if(this.checkIfFieldExsists(p2, m, scopeCall, false)){
							Field par = this.getExistingFieldOrNull(p2, m, scopeCall);
							if(!par.getType().isInteger()){
								this.errors.printNotCompatibleType(par, p2, new Token(this.tks.INT, "\"Integer\""));
								this.p.setError(true);
								b = false;
							}
						}else{
							this.errors.printFieldDoesNotExists(p2);
							this.p.setError(true);
							b = false;
						}
					}
					if(!fieldRef.getType().isArray() && b){
						this.errors.printExpectedArrayFieldRef(fieldRef);
						this.p.setError(true);
						b = false;
					}
					if(b){
						Field fil = new Field(fieldRef.getType().getBaseType(), fieldRef.getName(), fieldRef.getClazz(), fieldRef.getToken());
						this.pList.addParameter(fil);
					}
				}else{
					//Field fil = new Field(fieldRef.getType(), fieldRef.getName(), fieldRef.getClazz(), fieldR);
					this.pList.addParameter(fieldRef);
				}
				Class cl = fieldRef.getType().getClazz();
				if(c.isAllreadyContainingClassReference(cl.getName())){
					if(newCall){
						if(!c.isAllreadyContainingClassReferenceBotNotStatic(cl.getName())){
							cl = c.getFromStaticClassReferenceByName(cl.getName());
							c.getStaticClassReferences().remove(cl);
							c.addClassReference(cl);
						}
					}
				}else if(c.isAllreadyContainingClass(cl.getName())){
					cl = c.getUsedOrImportedClassByName(cl.getName());
					if(newCall){
						c.addClassReference(cl);
					}else {
						this.addClassFromFieldRefToStaticCLassRef(c, fieldRef, true);
						//c.addStaticCassReference(cl);
					}
				}else if(cl.getName().equals(c.getName())){
					if(newCall){
						c.addClassReference(cl);
					}
				}/*else{
					cl = new Class(cl.getName(), c.getSuperClass(), c.getPackageName(), c.getFilePath());
					fieldRef = new Field(new Type(cl), fieldRef.getName(), fieldRef.getClazz(), fieldRef.isStatic(), fieldRef.isPrivate(), false, fieldRef.getToken());
					if(newCall){
						c.addClassReference(cl);
					}else{
						c.addStaticCassReference(cl);
					}
				}*/
			}else{
				this.errors.printFieldDoesNotExists(fieldR);
				this.p.setError(true);
				b = false;
			}
		}else{
			//dsadsa
			Method newMethod = new Method(this.p, fieldR.getText(), expectedRetrunType, pl, c, false, false, fieldR);
			if(c.isAllreadyContainingMethod(newMethod)){
				Method oldMethod = c.getMethoddFromClassMethodsByName(fieldR.getText(), pl);
				if(newMethod.getRetrunType() == null || this.areSameTypes(newMethod.getRetrunType(), oldMethod.getRetrunType())){
					if(!c.isAllreadyContainingMethodRef(oldMethod)){
						c.addMethodReference(oldMethod);
					}
				}else{
					this.errors.printNotCompatibleRetunTypes(fieldR, oldMethod, newMethod.getRetrunType());
					this.p.setError(true);
					b = false;
				}
			}else if(!c.isAllreadyContainingMethodRef(newMethod)){
				c.addMethodReference(newMethod);
				c.addToMethodsToBeCheckedIfExists(newMethod);
			}
		}
		return b;
	}

/*******************************************************************************************
 *  boolean checkFieldRef(Field classReference, Token fieldR, Token p2, Method m, boolean scopeCall)
 *  	- 
 *******************************************************************************************/
	private boolean checkFieldRef(Field classReference, Token fieldR, Method m, boolean scopeCall, boolean newCall) {
		boolean b = true;
		Class c = m.getClazz();
		Field fieldReference = null;
		if(c.isAllreadyContainingFieldReference(fieldR.getText())){
			fieldReference = c.getFieldFromFieldRef(fieldR.getText());
			this.pList.addParameter(fieldReference);
		}else{
			String filePathClass = classReference.getClazz().getFilePath() + classReference.getType().getClazz().getName() + ".class";
			String filePathJava = classReference.getClazz().getFilePath() + classReference.getType().getClazz().getName() + ".java";
			//System.out.println(filePathClass);
			//System.out.println(filePathJava);
			if(this.checkFile(filePathJava)){
				JavaReader JReader;
				try {
					JReader = new JavaReader(filePathJava);
					
					//System.out.println(classReference.getType().getClazz().getName() + "  csadas");
					if(JReader.findFieldFromJavaFile(fieldR, classReference.getType().getClazz())){
						fieldReference = JReader.getField();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else if(this.checkFile(filePathClass)){
				ByteReader breader;
				try {
					breader = new ByteReader(filePathClass);
					fieldReference = breader.findField(fieldR, classReference.getClazz());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				this.errors.printFileDoesNotExists(classReference.getToken(), filePathJava);
				this.p.setError(true);
				fieldReference = null;
				b = false;
			}
			if(fieldReference == null){
				this.errors.printFieldDoesNotExists(fieldR);
				this.p.setError(true);
				b = false;
			}else{
				//System.out.println(fieldReference.getName() + "    TUKAAA   "  + fieldReference.getType().isClass());
				if(fieldReference.getType().isClass()){
					Class cl = fieldReference.getType().getClazz();
					if(c.isAllreadyContainingClassReference(cl.getName())){
						if(newCall){
							if(c.isAllreadyContainingClassReferenceBotNotStatic(cl.getName())){
								cl = c.getFromClassReferenceAndStaticClassByName(cl.getName());
								fieldReference = new Field(new Type(cl), fieldReference.getName(), fieldReference.getClazz(), fieldReference.isStatic(), fieldReference.isPrivate(), false, fieldReference.getToken());	
							}else{
								cl = c.getFromStaticClassReferenceByName(cl.getName());
								c.getStaticClassReferences().remove(cl);
								fieldReference = new Field(new Type(cl), fieldReference.getName(), fieldReference.getClazz(), fieldReference.isStatic(), fieldReference.isPrivate(), false, fieldReference.getToken());	
								c.addClassReference(cl);
							}
						}else{
							cl = c.getFromClassReferenceAndStaticClassByName(cl.getName());
							fieldReference = new Field(new Type(cl), fieldReference.getName(), fieldReference.getClazz(), fieldReference.isStatic(), fieldReference.isPrivate(), false, fieldReference.getToken());					
						}
					}else if(c.isAllreadyContainingClass(cl.getName())){
						cl = c.getUsedOrImportedClassByName(cl.getName());
						fieldReference = new Field(new Type(cl), fieldReference.getName(), fieldReference.getClazz(), fieldReference.isStatic(), fieldReference.isPrivate(), false, fieldReference.getToken());
						if(newCall){
							c.addClassReference(cl);
						}else {
							this.addClassFromFieldRefToStaticCLassRef(c, fieldReference, true);
							//c.addStaticCassReference(cl);
						}
					}else if(cl.getName().equals(c.getName())){
						fieldReference = new Field(new Type(c), fieldReference.getName(), fieldReference.getClazz(), fieldReference.isStatic(), fieldReference.isPrivate(), false, fieldReference.getToken());
						if(newCall){
							c.addClassReference(cl);
						}
					}else{
						cl = new Class(cl.getName(), c.getSuperClass(), c.getPackageName(), c.getFilePath());
						fieldReference = new Field(new Type(cl), fieldReference.getName(), fieldReference.getClazz(), fieldReference.isStatic(), fieldReference.isPrivate(), false, fieldReference.getToken());
						if(newCall){
							c.addClassReference(cl);
						}else{
							c.addStaticCassReference(cl);
						}
					}
				}else{
					//System.out.println(c.getFieldIntMap().containsKey(classReference));
				}
				c.addFieldReference(fieldReference);
				this.pList.addParameter(fieldReference);
			}
		}
		
		return b;
	}
/*******************************************************************************************
 *  boolean checkMethodRef(Field classReference, Token fieldR, Token p2, Method m, boolean scopeCall
 *  	- 
 *******************************************************************************************/
	private boolean checkMethodRef(Field classReference, Token methodRef, Token p2, Method m, boolean scopeCall, ParameterList pl) {
		boolean b = true;
		Class c = m.getClazz();
		Method methodReference = null;
		Method newMethod = new Method(this.p, methodRef.getText(), null, pl, classReference.getClazz(), false, false, methodRef);
		if(c.isAllreadyContainingMethodRef(newMethod)){
			methodReference = c.getMethoddFromClassMethodReferenceByName(methodRef.getText(), pl);
			Field f = new Field(methodReference.getRetrunType(), methodRef);
			this.pList.addParameter(f);
		}else{
			String filePathClass = classReference.getClazz().getFilePath() + classReference.getType().getClazz().getName() + ".class";
			String filePathJava = classReference.getClazz().getFilePath() + classReference.getType().getClazz().getName() + ".java";
			//System.out.println(filePathClass);
			//System.out.println(filePathJava);
			if(this.checkFile(filePathJava)){
				JavaReader JReader;
				try {
					JReader = new JavaReader(filePathJava);
					if(JReader.findMethodFromJavaFile(methodRef, classReference.getType().getClazz(), pl)){
						methodReference = JReader.getMethod();
						
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else if(this.checkFile(filePathClass)){
				ByteReader breader;
				try {
					//TODO
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				this.errors.printFileDoesNotExists(classReference.getToken(), filePathJava);
				this.p.setError(true);
				methodReference = null;
				b = false;
			}
			if(methodReference == null){
				this.errors.printMethodDoesNotExistsError(newMethod, methodRef);
				//this.errors.printFieldDoesNotExists(methodRef);
				this.p.setError(true);
				b = false;
			}/*else if(!methodReference.isStatic()){
				c.addMethodReference(methodReference);
				Field f = new Field(methodReference.getRetrunType(), methodRef);
				this.pList.addParameter(f);
				//}
			}*/else{
				/*Class cl = null;
				if()*/
				c.addMethodReference(methodReference);
				Field f = new Field(methodReference.getRetrunType(), methodRef);
				this.pList.addParameter(f);
				/*else{
					this.errors.printExpectedNotVoidReturnType(methodRef);
					this.p.setError(true);
					b = false;
				}*/
			}
			/*Class cl = fieldReference.getType().getClazz();
			if(c.isAllreadyContainingClassReference(cl.getName())){
				cl = c.getClassReferenceByName(cl.getName());
				fieldReference = new Field(new Type(cl), fieldReference.getName(), fieldReference.getClazz(), fieldReference.isStatic(), fieldReference.isPrivate(), false, fieldReference.getToken());
			}else if(c.isAllreadyContainingClass(cl.getName())){
				cl = c.getUsedOrImportedClassByName(cl.getName());
				fieldReference = new Field(new Type(cl), fieldReference.getName(), fieldReference.getClazz(), fieldReference.isStatic(), fieldReference.isPrivate(), false, fieldReference.getToken());
				//c.addCassReference(cl);
			}else if(cl.getName().equals(c.getName())){
				fieldReference = new Field(new Type(c), fieldReference.getName(), fieldReference.getClazz(), fieldReference.isStatic(), fieldReference.isPrivate(), false, fieldReference.getToken());
				//c.addCassReference(c);
			}else{
				cl = new Class(cl.getName(), c.getSuperClass(), c.getPackageName(), c.getFilePath());
				fieldReference = new Field(new Type(cl), fieldReference.getName(), fieldReference.getClazz(), fieldReference.isStatic(), fieldReference.isPrivate(), false, fieldReference.getToken());
				//c.addCassReference(cl);
			}*/
		}
		
		return b;
	}

/*******************************************************************************************
 *  addArithmeticFieldWriter(Method m)
 *  	- 
 *******************************************************************************************/
	public void addArithmeticFieldWriter(Method m, Field classRef) {
		if(this.bodyParser == null){
			this.addByteWriterToMethodBody(str,m, classRef);
		}else{
			this.addByteWriterToIfScope(str, m, classRef);
		}
		
	}
/*******************************************************************************************
 *  addArithmeticFieldWriter(Method m)
 *  	- 
 *******************************************************************************************/
	private void addArithmeticFieldRefWriter(Method m, Field fieldRef, Token pos, Field classFil) {
		if(this.bodyParser == null){
			this.addArithmeticFieldRefWriter(m, fieldRef, m.getClazz(), pos, classFil);
		}else{
			//this.addByteWriterToIfScope(str, m, classRef);
		}
		
	}
/*******************************************************************************************
 *  addByteWriterToIfScope(String str, Method m) 
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	private void addByteWriterToIfScope(String str, Method m, Field classReference) {//Method m, Token name, Field fieldFromClassRef, Class clazz, int position, Field classRef
		LookForwardScanner lfc;
		try {
			lfc = new LookForwardScanner(new Scanner(new LookForwardReader(new StringReader(str))));
			ArithmeticExpression ax = new ArithmeticExpression(lfc, m.getFieldMap(), m, classReference, str) ;
			//System.out.println(ax.getExpressionCode().size());
			//ax.getExpressionCode().printByteArray();
			ax.make(false);
			//System.out.println(ax.getExpressionCode().size() + "   size");
			this.bodyParser.addExpresssion(ax);
			//this.ifparser.setByteWriter(ax.getExpressionCode());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

/*******************************************************************************************
 *  addByteWriterToMethodBody(String str, Method m) 
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	private void addByteWriterToMethodBody(String str, Method m, Field classReference) {
		LookForwardScanner lfc;
		try {
			lfc = new LookForwardScanner(new Scanner(new LookForwardReader(new StringReader(str))));
			ArithmeticExpression ax = new ArithmeticExpression(lfc, m.getFieldMap(), m, classReference, str);
			//System.out.println(ax.getExpressionCode().size());
			//ax.getExpressionCode().printByteArray();
			ax.make(false);
			m.addExpresssion(ax);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

/*******************************************************************************************
 *  parseLogicalExpression(Method m) 
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	public boolean parseLogicalExpression(Method m) {
		boolean b = true;
		Token name1 = null;
		while(!this.isNextToken(new Token(this.tks.ROUND_BRACKET_CLOSE)) && b){
			if(this.isNextToken(new Token(this.tks.ROUND_BRACKET_OPEN))){
				this.expected(new Token(this.tks.ROUND_BRACKET_OPEN, "("));
				b = b && this.parseLogicalExpression(m);
			}else if(this.isNextToken()){
				name1 = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"), new Token(this.tks.NUMBER, "Number"));
				if(this.iskCorrectToken(name1)){
					this.tList.add(name1);
					if(this.isNextToken(new Token(this.tks.DOT))){
						this.expected(new Token(this.tks.DOT, "."));
						if(this.isNextToken(new Token(this.tks.IDENTIFIER))){
							Token refField1 = this.p.getLfc().readNextToken();
							this.checkReferenceFieldExistenceAndParseArrays(m, name1, refField1);
							if(this.isNextTokenOperator()){
								Token name3 = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"));
								if(name3.getToken() != -1){
									this.checkTypeCompatability();
								}else{
									this.p.getLfc().readNextToken();
									this.expected(new Token(this.tks.SEMICOLON, ";"));
									b = false;
								}
							}
						}else{
							this.expected(new Token(this.tks.IDENTIFIER, "Identifier"));
							this.p.getLfc().readNextToken();
							this.expected(new Token(this.tks.SEMICOLON, ";"));
							b = false;
						}
					}else if(this.isNextExpectedComparableOperator()){
						Token operator = this.expectedComparableOperator();
						this.tList.add(operator);
						Token name2 = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"), new Token(this.tks.NUMBER, "Number"));
						if(this.iskCorrectToken(name2)){
							this.tList.add(name2);
							Field f1 = this.makeSkopeField(m, name1);
							Field f2 = this.makeSkopeField(m, name2);
							this.pList.addParameter(f1);
							this.pList.addParameter(f2);
							this.checkParametersCompatability();
						}else{
							this.p.getLfc().readNextToken();
							this.expected(new Token(this.tks.SEMICOLON, ";"));
							b = false;
						}
					}else if(this.isNextToken(new Token(this.tks.SQUARE_BRACKET_OPEN))){
						if(name1.getToken() == this.tks.NUMBER){
							this.errors.printError(name1, new Token(this.tks.IDENTIFIER, "Identifier"));
							this.p.setError(false);
							this.p.getLfc().readNextToken();
							this.expected(new Token(this.tks.SEMICOLON, ";"));
							b = false;
						}else{
							b = this.checkReferenceFieldExistenceAndParseArrays(m, name1, null);
							if(b){
								b = this.parseOperator(name1, null, m);
							}
						}
						//System.out.println(this.str + "   TUUKA");                           /*****************************************************************************************/
					}else if(this.isNextToken(new Token(this.tks.ROUND_BRACKET_OPEN))){
						//this.expected(new Token(this.tks.ROUND_BRACKET_CLOSE, ")"));
						b = this.parseMethodCall(m.getClazz(), m, name1);
						if(b){
							
							Token operator = this.expectedComparableOperator();
							if(operator.getToken() != -1){
								Token name2 = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"), new Token(this.tks.NUMBER, "Number"));
								if(name2.getToken() != -1){
									if(name2.getToken() == this.tks.IDENTIFIER){
										
										//TODO;
									}else{
										//System.out.println(this.str);   /*****************************************************************************************/
									}
									//this,str = this.str + name2.getText();
									//Token name2 = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"), new Token(this.tks.NUMBER, "Number"));
								}else{
									this.p.getLfc().readNextToken();
									this.expected(new Token(this.tks.SEMICOLON, ";"));
									b = false;
								}
							}else{
								this.p.getLfc().readNextToken();
								this.expected(new Token(this.tks.SEMICOLON, ";"));
								b = false;
							}
						}
					}else{
						Token t = this.p.getLfc().readNextToken();
						this.errors.printComparableOperatorError(t);
						this.p.getLfc().readNextToken();
						this.expected(new Token(this.tks.SEMICOLON, ";"));
						b = false;
					}
				}else{
					this.p.getLfc().readNextToken();
					this.expected(new Token(this.tks.SEMICOLON, ";"));
					b = false;
				}
			}else if(this.isNextExpectedComparableOperator()){
				
			}else if(this.isNextToken(new Token(this.tks.AND)) || this.isNextToken(new Token(this.tks.OR))){
				return b;
			}else{
				//TODO implement char and boolean
				this.expected(new Token(this.tks.IDENTIFIER, "Identfier"), new Token(this.tks.NUMBER, "Number"));
				this.p.getLfc().readNextToken();
				this.expected(new Token(this.tks.SEMICOLON, ";"));
				b=false;
			}
			
		}
		if(b){
			Token to = this.expected(new Token(this.tks.ROUND_BRACKET_CLOSE));
			if(to.getToken() == -1){
				this.p.getLfc().readNextToken();
				this.expected(new Token(this.tks.SEMICOLON,";"));
				b = false;
			}
		}
		return b;
	}
/**********************************************************************************************************************
 *  boolean parseMethodCall(Class clazz, Token name1)
 *  	- 
 *********************************************************************************************************************/
	public boolean parseMethodCall(Class clazz, Method m, Token name1) {
		boolean b = true;
		this.expected(new Token(this.tks.ROUND_BRACKET_OPEN, "("));
		//this.str = this.str + name1.getText() + "(";
		ParameterList mp = new ParameterList();
		b = this.parseParameters(m, mp);
		if(b){
			Method newMethod = new Method(this.p, name1.getText(), new Type(new Token(this.tks.INT, "Integer")), mp, clazz, false, false);
			if(clazz.isAllreadyContainingMethod(newMethod)){
				Method oldMethod = clazz.getMethoddFromClassMethodsByName(name1.getText(), mp);
				if(this.areSameTypes(newMethod.getRetrunType(), oldMethod.getRetrunType())){
					if(!clazz.isAllreadyContainingMethodRef(oldMethod)){
						clazz.addMethodReference(oldMethod);
					}
				}else{
					this.errors.printNotCompatibleRetunTypes(name1, oldMethod, newMethod.getRetrunType());
					this.p.setError(true);
				}
			}else if(!clazz.isAllreadyContainingMethodRef(newMethod)){
				clazz.addMethodReference(newMethod);
				clazz.addToMethodsToBeCheckedIfExists(newMethod);
			}
		}
		/*System.out.println(clazz.getCounter());
		System.out.println(clazz.isNumberMappedToMethod(1));
		System.out.println(clazz.isNumberMappedToMethod(2));

		System.out.println(clazz.getMethodMappedToValue(1).getName());
		System.out.println(clazz.getMethodMappedToValue(1).printParameters());*/
		return b;
	}
	
/**********************************************************************************************************************
 * boolean parseParameters(ParameterList mp)
 *  	- 
 *********************************************************************************************************************/
	private boolean parseParameters(TokenArrayList tl, Method m, ParameterList mp) {
		boolean b = true;
		int i = 0;
		Token t = tl.get(i);
		Class c = m.getClazz();
		while((t.getToken() != this.tks.ROUND_BRACKET_CLOSE)){
			b = true;
			if(t.isNumberToken()){
				mp.addParameter(new Field(new Type(new Token(this.tks.INT)), t.getText(), m.getClazz(), t));
				b = this.checkNextTokenComma();
			}else if(t.isIdentifierToken()){
				Field f = null;
				if(m.isContainingFild(t.getText())){
					f = m.findFieldInsideMethoAndClassAndScope(t.getText());
					if(!m.getAlreadyDefinedFields().contains(f)){
						this.errors.printFieldNotDeclaredError(t);
						this.p.setError(true);
					}
				}else if(c.isAllreadyContainingField(t.getText())){
					f = c.getFieldFromClassFieldsByName(t.getText());
					if(!c.isAllreadyContainingFieldReference(f.getName())){
						c.addFieldReference(f);
					}
				}else{
					this.p.setError(true);
					this.errors.printFieldDoesNotExists(t);
					b = false;
				}
				Token t1 = this.tList.get(i+1);
				if(t1.isSquareBracketOpenToken() && b){
					i++;
					t1 = tl.get(i+1);
					i++;
					if(t1.isIdentifierToken()){
						if(f.getType().isArray()){
							if(m.isContainingFildMethodAndClassAndLoops(t1.getText(), false)){
								Field f1 = m.findFieldInsideMethoAndClassAndScope(t1.getText());
								if(m.isContainingFild(f1.getName())){
									if(!m.getAlreadyDefinedFields().contains(f1)){
										this.errors.printFieldNotDeclaredError(t1);
										this.p.setError(true);
									}
								}
								if(f1.getType().isInteger()){
									i++;
									mp.addParameter(new Field(new Type(f.getType().getBaseType().getType()), "", m.getClazz(), null));
								}else{
									this.p.setError(true);
									this.errors.printNotCompatibleType(f1, new Token(this.tks.INT, "Integer"));
									b = false;
								}
							}else{
								this.p.setError(true);
								this.errors.printFieldDoesNotExists(t1);
								b = false;
							}
						}else{
							this.p.setError(true);
							this.errors.printExpectedArrayFieldRef(f);
							b = false;
						}
					}else{
						if(f.getType().isArray()){
							i++;
							mp.addParameter(new Field(new Type(f.getType().getBaseType().getType()), "", m.getClazz(), null));
						}else{
							this.p.setError(true);
							this.errors.printExpectedArrayFieldRef(f);
							b = false;
						}
					}
				}else if(this.isNextTokenSubAddOperator(t1) && b){
					//TODO
					i++;
					mp.addParameter(new Field(new Type(t.getToken()), t));
					t1 = tl.get(i+1);
					while((t1.getToken() != this.tks.COMMA) && (t1.getToken() != this.tks.ROUND_BRACKET_CLOSE)){
						i++;
						t1 = tl.get(i+1);
					}
				}else if(b){
						mp.addParameter(new Field(f.getType(), f.getName(), m.getClazz(), t));
				}
			}
			i++;
			t = tl.get(i);
			if(t.getToken() == this.tks.COMMA){
				i++;
				t = tl.get(i);
			}
		}
		/*boolean b = true;
		  while(!this.isNextToken(new Token(this.tks.ROUND_BRACKET_CLOSE))  && b){
			if(this.isNextToken()){
				Token t = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"), new Token(this.tks.NUMBER, "Number"));
				if(t.getToken() == this.tks.IDENTIFIER){
					if(this.isNextToken(new Token(this.tks.SQUARE_BRACKET_OPEN))){
						this.p.getLfc().readNextToken();
						this.str = this.str + t.getText() + "[";
						
						Token to1 = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"), new Token(this.tks.NUMBER, "Number"));
						if(to1.getToken() != -1){
							if(to1.getToken() == this.tks.IDENTIFIER){
								Field f = null;
								if(m.isContainingFildMethodAndClassAndLoops(t.getText(), false)){
									f = m.findFieldInsideMethoAndClassAndScope(t.getText());
									if(f.getType().isArray()){
										if(m.isContainingFildMethodAndClassAndLoops(to1.getText(), false)){
											Field f1 = m.findFieldInsideMethoAndClassAndScope(to1.getText());
											if(f1.getType().isInteger()){
												this.str = this.str + to1.getText();
												
												Token to2 = this.expected(new Token(this.tks.SQUARE_BRACKET_CLOSE,"]"));
												if(to2.getToken() != -1){
													this.str = this.str + "]";
													mp.addParameter(new Field(new Type(f.getType().getBaseType().getType()), "", m.getClazz(), null));
													b = this.checkNextTokenComma();
												}else{
													this.p.getLfc().readNextToken();
													this.expected(new Token(this.tks.SEMICOLON, ";"));
													b = false;
												}
											}else{
												this.p.setError(true);
												this.errors.printNotCompatibleType(f1, new Token(this.tks.INT, "Integer"));
											}
										}else{
											this.p.setError(true);
											this.errors.printFieldDoesNotExists(to1);
										}
									}else{
										this.p.setError(true);
										this.errors.printExpectedArrayFieldRef(f);
									}
								}else{
									this.p.setError(true);
									this.errors.printFieldDoesNotExists(t);
								}
							}else{
								this.str = this.str + to1.getText();
								if(m.isContainingFildMethodAndClassAndLoops(t.getText(), false)){
									Field f = m.findFieldInsideMethoAndClassAndScope(t.getText());
									if(f.getType().isArray()){
										Token to2 = this.expected(new Token(this.tks.SQUARE_BRACKET_CLOSE,"]"));
										if(to2.getToken() != -1){
											this.str = this.str + "]";
											mp.addParameter(new Field(new Type(f.getType().getBaseType().getType()), "", m.getClazz(), null));
											b = this.checkNextTokenComma();
										}else{
											this.p.getLfc().readNextToken();
											this.expected(new Token(this.tks.SEMICOLON, ";"));
											b = false;
										}
									}else{
										this.p.setError(true);
										this.errors.printExpectedArrayFieldRef(f);
									}
								}else{
									this.p.setError(true);
									this.errors.printFieldDoesNotExists(t);
								}
							}
						}else{
							this.p.getLfc().readNextToken();
							this.expected(new Token(this.tks.SEMICOLON, ";"));
							b = false;
						}
					}else if(this.isNextTokenSubAddOperator()){
						//TODO CHECKING ERRORS FOR ARITHMETIC OPERATION
						this.str = this.str + t.getText();
						
						mp.addParameter(new Field(new Type(t.getToken()), t));
						while(this.p.getLfc().lookAhead().getToken() != this.tks.ROUND_BRACKET_CLOSE){
							this.str = this.str + this.p.getLfc().readNextToken().getText();
						}
					}else{
						if(m.isContainingFildMethodAndClassAndLoops(t.getText(), false)){
							this.str = this.str + t.getText();
							Field f = m.findFieldInsideMethoAndClassAndScope(t.getText());
							mp.addParameter(new Field(f.getType(), "", m.getClazz(), null));
							b = this.checkNextTokenComma();
						}else{
							this.p.setError(true);
							this.errors.printFieldDoesNotExists(t);
						}
					}
				}else{
					this.str = this.str + t.getText();
					mp.addParameter(new Field(new Type(new Token(this.tks.INT)), "", m.getClazz(), null));
					b = this.checkNextTokenComma();
				}
			}else if(this.isNextToken(new Token(this.tks.STRING_LITERAL))){
				Token tk = this.expected(new Token(this.tks.STRING_LITERAL, "String"));
				this.str = this.str + "\""+tk.getText() + "\"";
				Field f = new Field(new Type(new Token(this.tks.STRING_LITERAL)), tk.getText(), new Class("java/lang/String", m.getClazz().getSuperClass(), "java/lang/String","java/lang/"), tk);
				mp.addParameter(f);
				Class mainClass = m.getClazz();
				mainClass.addStrinReference(tk.getText());
				//System.out.println(mp.getParameter(0).getType().getDescriptor());
				//System.out.println(mainClass.getMethodReferences().size() + " TUKA SUMMMMM  EXPRESSION-PARSER LINE 1370");
				
				//mainClass.printMethodReferences();
				//mainClass.getMethodReferences().get(3).printParameters();
				//System.out.println(mainClass.isNumberMappedToString(5));
				/*if(mainClass.isNumberMappedToString(6)){
					
					System.out.println(mainClass.getStringdMappedToValue(6));
				}
			}else{
				this.p.getLfc().readNextToken();
				this.expected(new Token(this.tks.SEMICOLON, ";"));
				b = false;
			}
		}
		if(b){
			Token to = this.expected(new Token(this.tks.ROUND_BRACKET_CLOSE,")"));
			if(to.getToken() == -1){
				this.p.getLfc().readNextToken();
				this.expected(new Token(this.tks.SEMICOLON, ";"));
				b = false;
			}else{
				this.str = this.str + ")";
			}
		}*/
		return b;
	}

/**********************************************************************************************************************
 *  boolean parseOperator(Token name1, Method m)
 *  	- 
 *********************************************************************************************************************/
	private boolean parseOperator(Token name1, Token fieldRef1, Method m) {
		boolean b = true;
		Token operator =  this.expectedComparableOperator();
		if(operator.getToken() != -1){
			//this.str= this.str + operator.getText();
			Token name2;
			if(this.isNextToken(new Token(this.tks.NUMBER))){
				name2 = this.expected(new Token(this.tks.NUMBER, "Number"));
				this.checkTypeCompatability();
				//this.str = this.str + name2.getText();
			}else if(this.isNextToken(new Token(this.tks.IDENTIFIER))){
				name2 = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"));
				if(this.isNextToken(new Token(this.tks.DOT))){
					this.expected(new Token(this.tks.DOT, "."));
					if(this.isNextToken(new Token(this.tks.IDENTIFIER))){
						Token refField2 = this.p.getLfc().readNextToken();
						//this.str = this.str  + name2.getText() + "." + refField2.getText();
						b = this.checkReferenceFieldExistenceAndParseArrays(m, name2, refField2);
						if(!this.p.getError()){
							this.checkTypeCompatability();
						}
					}else{
						this.expected(new Token(this.tks.IDENTIFIER, "Identifier"));
						this.p.getLfc().readNextToken();
						this.expected(new Token(this.tks.SEMICOLON, ";"));
						b = false;
					}
				}else if(this.isNextToken(new Token(this.tks.SQUARE_BRACKET_OPEN))){
					b = this.checkReferenceFieldExistenceAndParseArrays(m, name2, null);
					if(b){
						this.checkTypeCompatability();
					}
				}else{
					//this.str = this.str + operator.getText() + name2.getText();
					this.checkTypeCompatability();
				}
			}else{
				this.expected(new Token(this.tks.IDENTIFIER, "Identifier"), new Token(this.tks.NUMBER, "Number"));
				this.p.getLfc().readNextToken();
				this.expected(new Token(this.tks.SEMICOLON, ";"));
				b = false;
			}
		}else{
			this.p.getLfc().readNextToken();
			this.expected(new Token(this.tks.SEMICOLON, ";"));
			b = false;
		}
		return b;
	}

/**********************************************************************************************************************
 *  void checkTypeCompatability(Token name1, Token refField1, Token name2, Token refField2)
 *  	- 
 *********************************************************************************************************************/
	private boolean checkTypeCompatability() {
		boolean b = true;
		Type expectedType = this.pList.getParameter(0).getType();
		if(expectedType.isVoid()){
			this.errors.printExpectedNoVoidReturnType(this.pList.getParameter(0).getToken());
			this.p.setError(true);
			b = false;
		}else if(expectedType.isArray() || expectedType.isClass()){
			Field f = this.pList.getParameter(1);
			if(this.pList.getSize() > 2){
				/*for(int i = 0; i < this.pList.getSize(); i++){
					System.out.println(this.pList.getParameter(i).getName());
				}*/
				System.out.println("EXPRESSION PARSER METHOD checkTypeCompatability() ERROR printBadOperandTypesForBinaryOperators or incomatible types  at 1641");
				//this.errors.printBadOperandTypesForBinaryOperators();
				this.p.setError(true);
				b = false;
			}else if(expectedType.isArray()){
				if(f.getType().isArray()){
					if(expectedType.getBaseType().getType() != f.getType().getBaseType().getType()){
						this.errors.printNotCompatibleTypes(f, this.pList.getParameter(0));
						this.p.setError(true);
						b = false;
					}
				}else{
					this.errors.printNotCompatibleTypes(this.pList.getParameter(0), f);
					this.p.setError(true);
					b = false;
				}
			}else if(expectedType.isClass()){
				if(expectedType.getType() != f.getType().getType()){
					this.errors.printNotCompatibleTypes(this.pList.getParameter(0), f);
					this.p.setError(true);
					b = false;
				}else if(!expectedType.getDescriptor().equals(f.getType().getDescriptor())){
					this.errors.printNotCompatibleTypes(this.pList.getParameter(0), f);
					this.p.setError(true);
					b = false;
				}
			}
		}else if(expectedType.isInteger()){
			int i = 1;
			while(i<this.pList.getSize()){
				Type t = this.pList.getParameter(i).getType();
				if(expectedType.getType() != t.getType()){
					this.errors.printNotCompatibleTypes(this.pList.getParameter(0), this.pList.getParameter(i));
					this.p.setError(true);
					b = false;
				}
				i++;
			}
		}else{
			System.out.println("EXPRESSION PARSER ERROR UNKNOWN TYPE(NOT YET IMPLEMENTED AT LINE 1442)");
			this.p.setError(true);
			b = false;
		}
		
		return b;
	}

/**********************************************************************************************************************
 *  boolean checkReferenceFieldExistenceAndParseArrays(Method m, Token name1, Token refField1)
 *  	- 
 *********************************************************************************************************************/
	private boolean checkReferenceFieldExistenceAndParseArrays(Method m, Token name1, Token refField1) {
		boolean b = true;
		if(m.isContainingFildMethodAndClassAndLoops(name1.getText(), false)){
			Field classRef = m.findFieldInsideMethoAndClassAndScope(name1.getText());
			if(refField1 == null){
				
				if(classRef.getType().isArray()){
					b = this.parseArray(m);
					
				}else{
					System.out.println("Errot at: Class ExpressionParser, method: checkReferenceFieldExistenceAndParseArrays(Method m, Token name1, Token refField1)    -> ERROR1");
					this.p.setError(false);
					this.p.getLfc().readNextToken();
					this.expected(new Token(this.tks.SEMICOLON, ";"));
					b = false;
				}
			}else if(!classRef.getType().isClass()){
				System.out.println("Errot at: Class ExpressionParser, method: checkReferenceFieldExistenceAndParseArrays(Method m, Token name1, Token refField1)");
			}else{
				if(classRef.getClazz().isAllreadyContainingField(refField1.getText())){
					Field fieldRef = classRef.getClazz().getFieldFromClassFieldsByName(refField1.getText());
					if(fieldRef.getType().isArray()){
						b = this.parseArray(m);
					}else{
						System.out.println("Errot at: Class ExpressionParser, method: checkReferenceFieldExistenceAndParseArrays(Method m, Token name1, Token refField1)    -> ERROR1");
						this.p.setError(false);
						this.p.getLfc().readNextToken();
						this.expected(new Token(this.tks.SEMICOLON, ";"));
						b = false;
					}
				}else{
					this.p.setError(true);
					this.errors.printReferenceDoesNotExist(refField1);
				}
			}
		}else{
			this.p.setError(true);
			this.errors.printFieldDoesNotExists(name1);
		}
		return b;
	}
/*******************************************************************************************
 *  void parseArray(Method m, Field classRef)
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	private boolean parseArray(Method m) {
		boolean b = true;
		Token t = this.expected(new Token(this.tks.SQUARE_BRACKET_OPEN, "["));
		if(t.getToken() != -1){
			Token nextT = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"), new Token(this.tks.NUMBER, "Number"));
			if(nextT.getToken() != -1){
				if(nextT.getToken() == this.tks.IDENTIFIER){
					if(m.isContainingFildMethodAndClassAndLoops(nextT.getText(), false)){
						Field counter = m.findFieldInsideMethoAndClassAndScope(nextT.getText());
						if(counter.getType().isInteger()){
							//this.str = this.str + t.getText() + nextT.getText();
						}else{
							this.p.setError(true);
							System.out.println("Errot at: Class ExpressionParser, method: checkReferenceFieldExistenceAndParseArrays(Method m, Token name1, Token refField1)");
						}
					}else{
						this.p.setError(true);
						this.errors.printFieldDoesNotExists(nextT);
					}
				}else{
					//this.str = this.str + t.getText() + nextT.getText();
				}
				Token t1 = this.expected(new Token(this.tks.SQUARE_BRACKET_CLOSE, "]"));
				if(t1.getToken() != -1){
					//this.str = this.str + t1.getText();
				}else{
					this.p.getLfc().readNextToken();
					this.expected(new Token(this.tks.SEMICOLON, ";"));
					b = false;
				}
			}else{
				this.p.getLfc().readNextToken();
				this.expected(new Token(this.tks.SEMICOLON, ";"));
				b = false;
			}
		}else{
			this.p.getLfc().readNextToken();
			this.expected(new Token(this.tks.SEMICOLON, ";"));
			b = false;
		}
		return b;
	}

/*******************************************************************************************
 *  addLogicalFieldWriter(Method m)
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	public void addLogicalFieldWriter(Method m) {
		this.addByteWriterToLogicalIfScope(m);
		
	}
/*******************************************************************************************
 *  void addMethodCallWriter(Method m)
 *  	- 
 *******************************************************************************************/
	public void addMethodCallWriter(Method m, boolean scopeCall, Method mRef, ParameterList pli) {
		if(scopeCall){
			//TODO
		}else{
			this.addMethoCallByteWriterToMethodBody(m, mRef, pli);
		}
	}
/*******************************************************************************************
 *  void addMethoCallByteWriterToMethodBody(Method m)
 *  	- 
 *******************************************************************************************/
	private void addMethoCallByteWriterToMethodBody(Method m, Method mRef, ParameterList pli) {
		LookForwardScanner lfc;
		try {
			lfc = new LookForwardScanner(new Scanner(new LookForwardReader(new StringReader(this.str))));
			//System.out.println(this.str);
			MethodCallExpression mex = new MethodCallExpression(lfc, m);
			mex.make(mRef, pli);
			m.addExpresssion(mex);
			//m.addExpresssion(ax);
			//this.ifparser.setByteWriter(ax.getExpressionCode());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

/*******************************************************************************************
 *  addByteWriterToLogicalIfScope(Method m) 
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	private void addByteWriterToLogicalIfScope(Method m) {
		LookForwardScanner lfc;
		try {
			this.makeString(true);
			lfc = new LookForwardScanner(new Scanner(new LookForwardReader(new StringReader(this.str))));
			CoditionExpression ax = new CoditionExpression(lfc, m.getFieldMap(), m);
			//ax.getExpressionCode().printByteArray();
			this.bodyParser.addExpresssion(ax);
			//m.addExpresssion(ax);
			//this.ifparser.setByteWriter(ax.getExpressionCode());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

/***************************************************************************************
 *  - checkParametersCompatability()
 *  	- return true, if the next token and the field before equal are the same type
 *  	- otherwise false
 **************************************************************************************/	
	private boolean checkParametersCompatability() {
		boolean b = true;
		/*for(int i = 0; i < this.pList.getSize(); i++){
			if(this.pList.getParameter(i).getType() == null){
				this.errors.identifierDoesNotExistsError(this.pList.getParameter(i).getToken());
				b = false;
			}
		}*/
		if(this.pList.getParameter(0).getType() == null){
			this.errors.identifierDoesNotExistsError(this.pList.getParameter(0).getToken());
			b = false;
		}
		if(!b){
			return b;
		}
		for(int i = 1; i < this.pList.getSize(); i++){
			
			if(this.pList.getParameter(i).getType() == null){
				this.errors.identifierDoesNotExistsError(this.pList.getParameter(i).getToken());
				b = false;
			}else{
				
				if(this.pList.getParameter(0).getType().isClass()){
					
					if(!this.pList.getParameter(0).getType().getDescriptor().equals(this.pList.getParameter(i).getType().getDescriptor())){
						this.errors.printNotCompatibleTypes(this.pList.getParameter(0), this.pList.getParameter(i));
						b = false;
					}
				}else if(this.pList.getParameter(0).getType().isArray()){
					if(this.pList.getParameter(0).getType().getBaseType().getType() != this.pList.getParameter(i).getType().getType()){
						this.errors.printNotCompatibleTypes(this.pList.getParameter(0), this.pList.getParameter(i));
						b = false;
					}
				}
				else{
					if(this.pList.getParameter(0).getType().getType() != this.pList.getParameter(i).getType().getType()){
						this.errors.printNotCompatibleTypes(this.pList.getParameter(0), this.pList.getParameter(i));
						b = false;
					}
				}
			}
		}
		return b;
	}
/**************** TO be checked what is used and what not ****************************/	


/***************************************************************************************
 *  - iskCorrectToken(Token t) 
 *  	- return true if the token t is not an unknown token
 *  	- otherwise false
 **************************************************************************************/
	private boolean iskCorrectToken(Token t){
		if(t.getToken() != -1){
			return true;
		}
		return false;
	}
/***************************************************************************************
 *  - isNextToken() - checks if the next token is char, int, indetifier or string 
 *  - return true if it is otherwise false
 **************************************************************************************/
	private boolean isNextToken(){
		if((this.p.getLfc().lookAhead().getToken() == this.tks.INT) || (this.p.getLfc().lookAhead().getToken() == this.tks.NUMBER) 
				|| (this.p.getLfc().lookAhead().getToken() == this.tks.IDENTIFIER)){
			return true;
		}
		return false;
	}
	
/***************************************************************************************
 *  - isNextToken(Token t) - checks if the next token is the same as the parameter t 
 *  - return true if it is otherwise false
 **************************************************************************************/
	private boolean isNextToken(Token t){
		Token t1 = this.p.getLfc().lookAhead();
		if(t1.getToken() == t.getToken()){
			return true;
		}
		return false;
	}
	
/***************************************************************************************
 *  - isNextToken(Token t) - checks if the next token is the same as the parameter t 
 *  - return true if it is otherwise false
 **************************************************************************************/
	private boolean isNextToken(int i, Token t){
		Token t1 = this.tList.get(i+1);
		if(t1.getToken() == t.getToken()){
			return true;
		}
		return false;
	}
/***************************************************************************************
 *  expected(Token token) 
 *  	-
 **************************************************************************************/
	private Token expected(Token token) 
	{
		if(this.p.getLfc().lookAhead().getToken() != token.getToken()){
			this.errors.printError(this.p.getLfc().lookAhead(), token);
			this.p.setError(true);
			return new Token(-1 , "Unknown Token");
		}
		return this.p.getLfc().readNextToken();
	}
	
/*******************************************************************************************
 *  expected((Token token, Token token2)) 
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	private Token expected(Token token1, Token token2) {
		Token t = this.p.getLfc().lookAhead();
		if((t.getToken() != token1.getToken()) && (t.getToken() != token2.getToken())){
			this.errors.printError(t, token1, token2);
			this.p.setError(true);
			return new Token(-1 , "Unknown Token");
		}
		t = this.p.getLfc().readNextToken();
		return t;
		
	}
/***************************************************************************************
 *  expected(Token token) 
 *  	-
 **************************************************************************************/
	private Token expectedComparableOperator() 
	{
		Token t = this.p.getLfc().lookAhead();
		if((t.getToken() != this.tks.LESS) && (t.getToken() != this.tks.LESS_EQUAL) && (t.getToken() != this.tks.GREATER) && (t.getToken() != this.tks.GREATER_EQUAL) 
				&& (t.getToken() != this.tks.EQUAL) &&  (t.getToken() != this.tks.UNEQUAL)){
			this.errors.printComparableOperatorError(t);
			return new Token(-1 , "Unknown Token");
		}
		return this.p.getLfc().readNextToken();
	}
/***************************************************************************************
 *  boolean isNextExpectedComparableOperator() 
 *  	-
 **************************************************************************************/
	private boolean isNextExpectedComparableOperator() 
	{
		Token t = this.p.getLfc().lookAhead();
		if((t.getToken() != this.tks.LESS) && (t.getToken() != this.tks.LESS_EQUAL) && (t.getToken() != this.tks.GREATER) && (t.getToken() != this.tks.GREATER_EQUAL) 
				&& (t.getToken() != this.tks.EQUAL) &&  (t.getToken() != this.tks.UNEQUAL)){
			return false;
		}
		return true;
	}

/*******************************************************************************************
 *  isNextTokenOperator() 
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	private boolean isNextTokenOperator(){
		Token t1 = this.p.getLfc().lookAhead();
		if((t1.getToken() == this.tks.ADD) || (t1.getToken() == this.tks.SUB) || (t1.getToken() == this.tks.DIV) || (t1.getToken() == this.tks.MULT)){
			return true;
		}
		return false;
	}
/*******************************************************************************************
 *  isIdentifierToken(Token t) 
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	private boolean isIdentifierToken(Token t){
		if(t.getToken() == this.tks.IDENTIFIER){
			return true;
		}
		return false;
	}
/*******************************************************************************************
 *  makeField(Token t)
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	private Field makeField(Method m, Token t){
		Field f;
		if(this.isIdentifierToken(t)){
			if(m.isContainingFildMethodAndClass(t.getText(),false)){
				f = m.findFieldInsideMethoAndClass(t.getText());
			}else{
				f = new Field(null, t.getText(), null, t);
			}
		}else{
			f = new Field(new Type(new Token(this.tks.INT, "int")), t.getText(), null, t);
		}
		return f;
	}
	
/*******************************************************************************************
 *  makeField(Token t)
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	private Field makeSkopeField(Method m, Token t){
		Field f;
		if(this.isIdentifierToken(t)){
			if(m.isContainingFildMethodAndClassAndLoops(t.getText(), false)){
				f = m.findFieldInsideMethoAndClassAndScope(t.getText());
			}else{
				f = new Field(null, t.getText(), null, t);
				//this.errors.printFieldDoesNotExists(t);
				//this.p.setError(true);
			}
		}else{
			f = new Field(new Type(new Token(this.tks.INT, "int")), t.getText(), null, t);
		}
		return f;
	}
	
/*******************************************************************************************
 *  boolean parseTokenNew(Field f)
 *  		- 
 *******************************************************************************************/
	public boolean parseTokenNew(Field f, Method m) {
		boolean b = true;
		Token type = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"), new Token(this.tks.INT, "int"));
		if(type.getToken() != -1){
			if(this.isNextToken(new Token(this.tks.SQUARE_BRACKET_OPEN))){
				this.p.getLfc().readNextToken();
				
				Token numberOrIdentfier = this.expected(new Token(this.tks.NUMBER, "Number"), new Token(this.tks.IDENTIFIER, "Identfier"));
				if(numberOrIdentfier.getToken() != -1){
					Token to = this.expected(new Token(this.tks.SQUARE_BRACKET_CLOSE,"]"));
					if(to.getToken() != -1){
						//this.str = numberOrIdentfier.getText();
						
						to = this.expected(new Token(this.tks.SEMICOLON, ";"));
						if(to.getToken() == -1){
							
							this.p.getLfc().readNextToken();
							b=false;
						}
						if(this.isIdentifierToken(numberOrIdentfier)){
							//TODO to implement for checking inside loop 
							if(m.isContainingFildMethodAndClassAndLoops(numberOrIdentfier.getText(), false)){
								Field field = m.findFieldInsideMethoAndClassAndScope(numberOrIdentfier.getText());
								
								if(!field.getType().isInteger()){
									Field f1 = new Field(new Type(numberOrIdentfier.getToken()), numberOrIdentfier.getText(), null, numberOrIdentfier);
									Field f2 = new Field(new Type(this.tks.INT), numberOrIdentfier.getText(), null, numberOrIdentfier);
									this.errors.printNotCompatibleTypes(f1, f2);
									this.p.setError(true);
								}
							}
						}

						
						//this.str = numberOrIdentfier.getText();
						if(b){
							if(!this.isSameType(f.getType(), new Type(new Type(type)))){
								this.errors.printNotCompatibleClasses(f,type);
								this.p.setError(true);
							}
						}
					}else{
						this.p.getLfc().readNextToken();
						this.expected(new Token(this.tks.SEMICOLON, ";"));
						b=false;
					}
				}else{
					this.p.getLfc().readNextToken();
					this.expected(new Token(this.tks.SEMICOLON, ";"));
					b=false;
				}
			}else if(this.isNextToken(new Token(this.tks.ROUND_BRACKET_OPEN))){
				this.expected(new Token(this.tks.ROUND_BRACKET_OPEN,"("));
				//TODO to make it work(it works for no parameters)
				b = this.parseMethodCallParametersOrNewObjectCreation(f);
				if(b){
					if(!this.isSameType(f.getType(), new Type(type))){
						this.errors.printNotCompatibleClasses(f,type);
						this.p.setError(true);
					}
				}
			}else{
				this.p.getLfc().readNextToken();
				this.expected(new Token(this.tks.SEMICOLON, ";"));
				b = false;
			}
		}else{
			this.p.getLfc().readNextToken();
			this.expected(new Token(this.tks.SEMICOLON, ";"));
			b = false;
		}

		
		return b;
	}
/*******************************************************************************************
*  boolean checkTypes(Type type, Type type2)
*  		- 
*******************************************************************************************/
	private boolean isSameType(Type type, Type type2) {
		return type.toString().equals(type2.toString());
	}
/*******************************************************************************************
 *  boolean parseMethodCallParametersOrNewObjectCreation(Field f)
 *  		- 
 *******************************************************************************************/
	private boolean parseMethodCallParametersOrNewObjectCreation(Field f) {
		boolean b = true;
		Token t = this.expected(new Token(this.tks.ROUND_BRACKET_CLOSE,")"));
		if(t.getToken()== -1){
			this.p.getLfc().readNextToken();
			this.expected(new Token(this.tks.SEMICOLON, ";"));
			b = false;
		}
		if(b){
			t = this.expected(new Token(this.tks.SEMICOLON, ";"));
			if(t.getToken() == -1){
				this.p.getLfc().readNextToken();
				b=false;
			}
		}
		return b;
	}

/*******************************************************************************************
 *  void addNewFieldWriter(Method m)
 *  	-  
 *******************************************************************************************/
	public void addNewFieldWriter(Method m, Field f) {
		if(this.bodyParser == null){
			this.addNewByteWriterToMethodBody(f,m);
		}else{
			//this.addByteWriterToIfScope(f., m);
		}
		
	}
/*******************************************************************************************
 *  void addNewFieldReferenceWriter(Method m, Field field, Token type)
 *  	-  
 *******************************************************************************************/
	private void addNewFieldReferenceWriter(Method m, Field field, Token type) {
		if(this.bodyParser == null){
			this.addNewFieldRefByteWriterToMethodBody(m, field, type);
		}else{
			//this.addByteWriterToIfScope(f., m);
		}
		
	}

/*******************************************************************************************
 *  private void addNewByteWriterToMethodBody(String str2, Method m)
 *  	-  
 *******************************************************************************************/
	private void addNewByteWriterToMethodBody(Field f, Method m) {
		LookForwardScanner lfc;
		try {
			lfc = new LookForwardScanner(new Scanner(new LookForwardReader(new StringReader(str))));
			NewExpression newex = new NewExpression(lfc, m.getFieldMap(), m, f);
			//System.out.println(f + "   ExpressionParser addNewByteWriterToMethodBody line 2639");
			if(!(m.isContainingFildMethodAndClass(f.getName(), true))){
				newex.getCodeForFieldFromParsedClass();
			}else{
				newex.getCode();
			}
			//System.out.println(ax.getExpressionCode().size());
			//newex.getExpressionCode().printByteArray();
			//newex.getExpressionCode().printByteArray();
			m.addExpresssion(newex);
			//m.pr
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}

	private void addNewFieldRefByteWriterToMethodBody(Method m, Field fieldRef, Token classField) {
		LookForwardScanner lfc;
		try {
			lfc = new LookForwardScanner(new Scanner(new LookForwardReader(new StringReader(str))));
			NewExpression newex = new NewExpression(lfc, m.getFieldMap(), m, fieldRef);
			newex.makeCodeForFieldRef(classField);
			//System.out.println(ax.getExpressionCode().size());
			//newex.getExpressionCode().printByteArray();
			//newex.getExpressionCode().printByteArray();
			m.addExpresssion(newex);
			//m.pr
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
/*******************************************************************************************
 *  boolean parseFieldRefExpression()
 *  	-  
 *******************************************************************************************/
	public boolean parseFieldRefExpression(Token fieldRef, Method m, Class classRef) {
		boolean b = true;
		this.pList.addParameter(this.makeField(classRef, fieldRef));
		/*if(this.isNextTokenSubAddOperator()){
			this.p.getLfc().readNextToken();
		}*/
		
		this.str = "= ";
		
		//TODO PARSE ERRORS FOR ARITHMETIC EXRPESSIONS WITH REF
		while(!this.isNextToken(new Token(this.tks.SEMICOLON, ";"))){
			//this.str = this.str + this.p.getLfc().readNextToken().getText();
			//System.out.println("TUKA");
			
			/*Token t;
			if(this.isNextToken(new Token(this.tks.IDENTIFIER)) || this.isNextToken(new Token(this.tks.NUMBER))){
				t = this.expected(new Token(this.tks.NUMBER, "Number"), new Token(this.tks.IDENTIFIER, "Identifier"));
				this.str = this.str +  " " + t.getText();
				if(!this.isNextToken(new Token(this.tks.SEMICOLON, ";"))){
					if(this.isNextTokenOperator()){
						this.str = str + " " +this.p.getLfc().readNextToken().getText();
					}else if(this.isn){
						
					}
					
				}else{
					this.pList.addParameter(this.makeField(m, t));
				}
			}
			
			//Token t = this.expected(new Token(this.tks.NUMBER, "Number"), new Token(this.tks.IDENTIFIER, "Identifier"));
			/*if(this.iskCorrectToken(t)){
				this.str = this.str +  " " + t.getText();
				if(!this.isNextToken(new Token(this.tks.SEMICOLON, ";"))){
					if(!this.isNextTokenOperator()){
						Token err = this.p.getLfc().readNextToken();
						this.errors.printExpectsOperatorError(err);
						this.expected(new Token(this.tks.SEMICOLON, ";"));
						b = false;
						break;
					}else{
						this.str = str + " " +this.p.getLfc().readNextToken().getText();
						if(this.isNextToken(new Token(this.tks.NUMBER)) || this.isNextToken(new Token(this.tks.IDENTIFIER))){
							this.pList.addParameter(this.makeField(m, t));
						}else{
							Token nextToken = this.p.getLfc().readNextToken();
							this.errors.printError(nextToken, new Token(this.tks.NUMBER, "Number"), new Token(this.tks.IDENTIFIER, "Identifier"));
							this.expected(new Token(this.tks.SEMICOLON, ";"));
							b = false;
							break;
						}
					}
				}else{
					this.pList.addParameter(this.makeField(m, t));
				}
			}else{
				this.p.getLfc().readNextToken();
				this.expected(new Token(this.tks.SEMICOLON, ";"));
				b = false;
				break;
			}*/
		}
		if(b){
			Token t = this.expected(new Token(this.tks.SEMICOLON, ";"));
			if(this.iskCorrectToken(t)){
				str = str + t.getText();
				//this.p.setError(!this.checkParametersCompatability());
			}else{
				b = false;
			}
		}
		return b;
	}
/*******************************************************************************************
 *  Field makeField(Class classRef, Token fieldRef)
 *  	-  
 *******************************************************************************************/
	private Field makeField(Class classRef, Token fieldRef) {
		Field f;
		if(this.isIdentifierToken(fieldRef)){
			if(classRef.isAllreadyContainingField(fieldRef.getText())){
				f = classRef.getFieldFromClassFieldsByName(fieldRef.getText());
			}else{
				f = new Field(null, fieldRef.getText(), null, fieldRef);
			}
		}else{
			f = new Field(new Type(new Token(this.tks.INT, "int")), fieldRef.getText(), null, fieldRef);
		}
		return f;
	}
/*******************************************************************************************
 *  void addArithmeticFieldRefWriter(Method m, Token name, Field fieldFromClassRef, Class clazz)
 *  	-  
 *******************************************************************************************/
	public void addArithmeticFieldRefWriter(Method m, Field fieldFromClassRef, Class clazz, Token position, Field classRef) {
		LookForwardScanner lfc;
		try {
			lfc = new LookForwardScanner(new Scanner(new LookForwardReader(new StringReader(this.str))));
			ArithmeticExpression ar = new ArithmeticExpression(clazz, m.getFieldMap(), m,lfc, fieldFromClassRef, position, classRef, this.str);
			ar.make(true);
			//ar.getExpressionCode().printByteArray();
			m.addExpresssion(ar);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
/*******************************************************************************************
*  void addArithmeticFieldRefWriterInScope(Method m, Token name, Field fieldFromClassRef, Class clazz)
*  	-  
*******************************************************************************************/
	public void addArithmeticFieldRefWriterInScope(Method m, Field fieldFromClassRef, Class clazz, Token position, Field classRef) {
		LookForwardScanner lfc;
		try {
			lfc = new LookForwardScanner(new Scanner(new LookForwardReader(new StringReader(this.str))));
			ArithmeticExpression ar = new ArithmeticExpression(clazz, m.getFieldMap(), m,lfc, fieldFromClassRef, position, classRef, this.str);
			ar.make(true);
			//ar.getExpressionCode().printByteArray();
			this.bodyParser.addExpresssion(ar);
		} catch (IOException e) {
			e.printStackTrace();							
		}
		
	}
/*******************************************************************************************
 *  void addNewFieldRefWriter(Method m, Field fieldFromClassRef, Class clazz)
 *  	-  
 *******************************************************************************************/
	public void addNewFieldArrayWriter(Method m, Field fieldArray, Class clazz, Field classRef) {
		String s = "";
		for(int i = 0; i <  this.str.length(); i++){
			if(this.str.charAt(i) == '['){
				i++;
				while(this.str.charAt(i) != ']'){
					if(this.str.charAt(i) != ' '){
						s = s + this.str.charAt(i);
					}
					i++;
				}
				break;
			}
			
		}

		
		NewArrayExpression narray = new NewArrayExpression(m, fieldArray, clazz, s, classRef);
		//System.out.println(this.str);
		if(classRef == null){
			
			narray.getCodeForLocalArrayFieldInitialization();
		}else{
			
			narray.getCode();
		}
	
		//narray.getExpressionCode().printByteArray();
		m.addExpresssion(narray);
	}
/*******************************************************************************************
 *  void addNewFieldRefWriter(Method m, Field fieldFromClassRef, Class clazz)
 *  	-  
 *******************************************************************************************/
	/*public void parseArrayExpression() {
		
	}*/
/*******************************************************************************************
 *  boolean parseReturnRef(Field f)
 *  	-  
 *******************************************************************************************/
	public boolean parseReturnRef(Field f, Class clazz, Method m) {
		boolean b = true;
		Token fieldRef = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"));
		if(fieldRef.getToken() != -1){
			//Token t = this.expected(new Token(this.tks.SEMICOLON, ";"));
			Field fieldReference = clazz.getFieldFromFieldRef(fieldRef.getText());
			if(this.isNextToken(new Token(this.tks.SQUARE_BRACKET_OPEN))){
				this.expected(new Token(this.tks.SQUARE_BRACKET_OPEN, "["));
				Token arrPos = this.expected(new Token(this.tks.NUMBER,"Number"));
				if(arrPos.getToken() != -1){
					int arrPossition = Integer.parseInt(arrPos.getText());
					Token t = this.expected(new Token(this.tks.SQUARE_BRACKET_CLOSE, "]"));
					if(t.getToken() != -1){
						if(this.isNextToken(new Token(this.tks.SEMICOLON))){
							if(!m.isContainingFildMethodAndClass(f.getName(), false)){
								this.errors.identifierDoesNotExistsError(f.getToken());
								this.p.setError(true);
							}else if(!clazz.isAllreadyContainingFieldReference(fieldRef.getText())){
								//TODO Implement to search it with byte reader
								this.errors.printReferenceDoesNotExist(fieldRef);
								this.p.setError(true);
							}else if(!fieldReference.getType().isArray()){
								this.errors.printExpectedArrayFieldRef(fieldReference);
								b = false;
								this.p.setError(true);
							}else if(m.getRetrunType().getType() != fieldReference.getType().getBaseType().getType()){
								this.errors.printNotCompatibleTypes(fieldReference, m);
								this.p.setError(true);
							}else if(arrPossition >= fieldReference.getSize()){
								//TODO
								System.out.println("ERROR SIZE OVERFLOW!");
								this.p.setError(true);
							}else{
								if(!this.p.getError()){
									ArithmeticExpression ar = new ArithmeticExpression(clazz, m.getFieldMap(), m, null, fieldReference, arrPos, f, "");
									ar.makeForRetrun();
									m.addExpresssion(ar);
								}
							}
						}else{
							this.expected(new Token(this.tks.SEMICOLON, ";"));
							this.p.getLfc().readNextToken();
							b = false;
						}
					}else{
						this.p.getLfc().readNextToken();
						this.expected(new Token(this.tks.SEMICOLON, ";"));
						b = false;
					}
				}else{
					this.p.getLfc().readNextToken();
					this.expected(new Token(this.tks.SEMICOLON, ";"));
					b = false;
				}
			}else if(this.isNextToken(new Token(this.tks.SEMICOLON))){
				//TODO
				if(m.getRetrunType().getType() != fieldReference.getType().getType()){
					this.errors.printNotCompatibleTypes(fieldReference, m);
					this.p.setError(true);
				}else{
					if(!this.p.getError()){
						ArithmeticExpression ar = new ArithmeticExpression(clazz, m.getFieldMap(), m, null, fieldReference, new Token(-1), f, "");
						ar.makeForRetrun();
						m.addExpresssion(ar);
					}
				}
			}else{
				this.expected(new Token(this.tks.SEMICOLON));
				this.p.getLfc().readNextToken();
				b = false;
			}
		}else{
			this.p.getLfc().readNextToken();
			this.expected(new Token(this.tks.SEMICOLON, ";"));
			b = false;
		}
		return b;
		
	}
	
	public void addString(String s){
		this.str = s;
	}
	
	private void makeString(boolean isForReturn){
		if(isForReturn){
			int i = 0;
			while(i < this.tList.size()){
				this.str = this.str + this.tList.get(i).getText() + " ";
				i++;
			}
		}else{
			int i = 0;
			this.str = "";
			Token t = this.tList.get(i);
			while(i < this.tList.size()){
				if(t.isAssignmentToken() || t.getToken() == this.tks.SEMICOLON){
					break;
				}
				i++;
				t = this.tList.get(i);
			}
			this.str = this.str + t.getText() + " ";
			i++;
			while(i < this.tList.size()){
				this.str = this.str + this.tList.get(i).getText() + " ";
				i++;
			}
		}
		
	}

/**********************************************************************************************************************
 *  boolean checkNextTokenComma()
 *  	- 
 *********************************************************************************************************************/
	private boolean checkNextTokenComma() {
		boolean b = true;
		if(this.isNextToken(new Token(this.tks.COMMA))){
			this.p.getLfc().readNextToken();
			this.str = this.str + ",";
			if(this.isNextToken(new Token(this.tks.ROUND_BRACKET_CLOSE))){
				this.expected(new Token(this.tks.IDENTIFIER, "Identifier"), new Token(this.tks.NUMBER, "Number"));
				this.p.getLfc().readNextToken();
				this.expected(new Token(this.tks.SEMICOLON, ";"));
				b = false;
			}
		}
		return b;
	}
/**********************************************************************************************************************
 *  boolean areSameTypes(Method newMethod, Method oldMethod)
 *  	- 
 *********************************************************************************************************************/
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
/*********************************************************************************************************************
 *  boolean parseSystemOutPrintln()
 *  	- 
 *********************************************************************************************************************/
	public boolean parseSystemOutPrintln(Method m) {
		boolean b = true;
		Class mainClass = m.getClazz();
		Class clazz;
		this.tList.remove(0);
		this.tList.remove(0);
		if(mainClass.isAlreadyContainingStaticClassRef("java/lang/System")){
			clazz = mainClass.getFromStaticClassReferenceByName("java/lang/System");
		}else if(mainClass.isAllreadyUsedClass("java/lang/System")){
			clazz = m.getClazz().getUsedOrImportedClassByName("java/lang/System");
			mainClass.addStaticCassReference(clazz);
		}else{
			clazz = new Class("java/lang/System", mainClass.getSuperClass(), "java/lang/System", "java/lang/");
			mainClass.addUsedClasses(clazz);
			mainClass.addStaticCassReference(clazz);
		}
		Token t = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"));
		if(t.getToken() != -1){
			if(t.getText().equals("out")){
				//this.tList.add(t);
				Field f;
				if(mainClass.isAllreadyContainingFieldReference(t.getText())){
					f = mainClass.getFieldFromFieldRef(t.getText());
				}else{
					Class printClass;
					if(mainClass.isAlreadyContainingStaticClassRef("java/io/PrintStream")){
						printClass = mainClass.getFromStaticClassReferenceByName("java/io/PrintStream");
					}else if(mainClass.isAllreadyUsedClass("java/io/PrintStream")){
						printClass = m.getClazz().getUsedOrImportedClassByName("java/io/PrintStream");
						mainClass.addStaticCassReference(printClass);
					}else{
						printClass = new Class("java/io/PrintStream", mainClass.getSuperClass(), "java/io/PrintStream", "java/io/");
						mainClass.addUsedClasses(printClass);
						mainClass.addStaticCassReference(printClass);
					}
					
					f = new Field(new Type(printClass), t.getText(), clazz, true, false, false, t);
					mainClass.addFieldReference(f);
				}
				Token t1 = this.expected(new Token(this.tks.DOT, "."));
				if(t1.getToken() != -1){
					Token methodName = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"));
					if(methodName.getToken() != -1){
						if(!methodName.getText().equals("println") || !methodName.getText().equals("print")){
							Token tk = this.expected(new Token(this.tks.ROUND_BRACKET_OPEN,"("));
							ParameterList pr = new ParameterList();
							if(tk.getToken() != -1){
								boolean er = this.parseParametersSyntax(this.tList, false);
								if(er){
									t = this.expected(new Token(this.tks.SEMICOLON, "\";\""));
									if(t.getToken() != -1){
										//this.tList.add(t);
										er = this.parseParameters(this.tList, m, pr);
										if(er){
											Method me = new Method(this.p, methodName.getText(), new Type(new Token(this.tks.VOID , "void")), pr, f.getType().getClazz(), false, false, methodName);
											//printMethod = new Method(this.p, methodName.getText(), new Type(new Token(this.tks.VOID)), pr, f.getType().getClazz(), false, false);
											if(mainClass.isAllreadyContainingMethodRef(me)){
												me =  mainClass.getMethoddFromClassMethodReferenceByName(methodName.getText(), pr);
											}else{
												mainClass.addMethodReference(me);
											}
											this.makeString(true);
											this.addPrintExpressionCode(mainClass, m);
										}
									}else{
										b = false;
									}
								}
							}else{
								this.p.getLfc().readNextToken();
								this.expected(new Token(this.tks.SEMICOLON, ";"));
								b = false;
							}
						}else{
							System.out.println("ERROR AT CLASS EXPRESSION-PARSER, AT LINE: 1372");
							this.p.setError(true);
						}
					}else{
						this.p.getLfc().readNextToken();
						this.expected(new Token(this.tks.SEMICOLON, ";"));
						b = false;
					}
				}else{
					this.p.getLfc().readNextToken();
					this.expected(new Token(this.tks.SEMICOLON, ";"));
					b = false;
				}
			}else{
				System.out.println("ERROR AT CLASS EXPRESSION-PARSER, AT LINE: 561");
				this.p.setError(true);
			}
		}else{
			this.p.getLfc().readNextToken();
			this.expected(new Token(this.tks.SEMICOLON, "\";\""));
			b = false;
		}
		return b;
	}
/*********************************************************************************************************************
 *  addPrintExpressionCode(Field fi, Method printMethod, Class clazz)
 *  	- 
 *********************************************************************************************************************/
	private void addPrintExpressionCode(Class clazz, Method m) {
		LookForwardScanner lfc;
		try {
			lfc = new LookForwardScanner(new Scanner(new LookForwardReader(new StringReader(this.str))));
			PrintExpression printEx = new PrintExpression(clazz, m, lfc);
			printEx.makePrintCode();
			if(this.bodyParser != null){
				//printEx.getExpressionCode().printByteArray();
				//System.out.println(this.str);
				this.bodyParser.addExpresssion(printEx);
			}else{
				m.addExpresssion(printEx);
			}
		} catch (IOException e) {
			e.printStackTrace();							
		}
		
	}
/*********************************************************************************************************************
 *  void addSimpleArtithmeticExrepssion(Method m, Field f1, Field f2)
 *  	- 
 *********************************************************************************************************************/
	public void addSimpleArtithmeticExrepssion(Method m, Field f1, Field f2) {
		LookForwardScanner lfc;
		try {
			lfc = new LookForwardScanner(new Scanner(new LookForwardReader(new StringReader(str))));
			ArithmeticExpression ax = new ArithmeticExpression(lfc, m.getFieldMap(), m, null, null);
			//System.out.println(ax.getExpressionCode().size());
			//newex.getExpressionCode().printByteArray();
			ax.makeCodeForSimpleRetrun(f1, f2);
			//ax.getExpressionCode().printByteArray();
			if(this.bodyParser != null){
				this.bodyParser.addExpresssion(ax);
				//ax.getExpressionCode().printByteArray();
			}else{
				m.addExpresssion(ax);
			}
			//m.pr
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
/*********************************************************************************************************************
 *  void addMethodCallExpression()
 *  	- 
 *********************************************************************************************************************/
	public void addMethodCallExpression(Method m) {
		LookForwardScanner lfc;
		try {
			lfc = new LookForwardScanner(new Scanner(new LookForwardReader(new StringReader(this.str))));
			Token name = lfc.readNextToken();
			lfc.readNextToken();
			CoditionExpression c = new CoditionExpression(lfc, m);
			c.getExpressionCode().writeAll(c.getCodeForMethodCall(name));
			c.getExpressionCode().write1Byte(0xac);
			if(this.bodyParser != null){
				//c.getExpressionCode().printByteArray();
				this.bodyParser.addExpresssion(c);
			}else{
				//TODO
			}
		} catch (IOException e) {
			e.printStackTrace();							
		}
	}
	
/*******************************************************************************************
 *  boolean parseMethodOrFieldCallSyntax(TokenArrayList tokenList)
 *  	- 
 *******************************************************************************************/
	private boolean parseMethodOrFieldCallSyntax(TokenArrayList tokenList, boolean expectedSquareBracketClose) {
		boolean b = true;
		if(this.isNextToken(new Token(this.tks.ROUND_BRACKET_OPEN))){
			Token t = this.expected(new Token(this.tks.ROUND_BRACKET_OPEN, "("));
			tokenList.add(t);
			b = this.parseParametersSyntax(tokenList, expectedSquareBracketClose);
		}else{
			Token identifier = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"));
			if(identifier.getToken() != -1){
				tokenList.add(identifier);
				if(this.isNextToken(new Token(this.tks.SQUARE_BRACKET_OPEN))){
					Token t = this.expected(new Token(this.tks.SQUARE_BRACKET_OPEN, "["));
					tokenList.add(t);
					b = this.parseArraySyntax(tokenList, expectedSquareBracketClose);
				}else if(this.isNextToken(new Token(this.tks.ROUND_BRACKET_OPEN))){
					Token t = this.expected(new Token(this.tks.ROUND_BRACKET_OPEN, "("));
					tokenList.add(t);
					b = this.parseParametersSyntax(tokenList, expectedSquareBracketClose);
				}
			}else{
				b = false;
				this.lfc.readNextToken();
				this.expected(new Token(this.tks.SEMICOLON, ";"));
			}
		}
		return b;
	}
	
/*******************************************************************************************
 *  boolean parseParametersSyntax(TokenArrayList tokenList)
 *  	- 
 *******************************************************************************************/
	private boolean parseParametersSyntax(TokenArrayList tokenList, boolean expectedSquareBracketClose) {
		boolean b = true;
		while(!this.isNextToken(new Token(this.tks.ROUND_BRACKET_CLOSE)) && b){
			
			Token t = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"), new Token(this.tks.NUMBER, "Number"));
			//System.out.println(t.getText());
			if(t.getToken() != -1){
				//TODO to implement -> recognize method or field calls as parameters
				tokenList.add(t);
				if(this.isNextToken(new Token(this.tks.SQUARE_BRACKET_OPEN))){
					t = this.expected(new Token(this.tks.SQUARE_BRACKET_OPEN, "["));
					tokenList.add(t);
					b = this.parseArraySyntax(tokenList, expectedSquareBracketClose);
					if(b){
						if(!this.isNextToken(new Token(this.tks.ROUND_BRACKET_CLOSE))){
							t = this.expected(new Token(this.tks.COMMA, ","));
							if(t.getToken() != -1){
								tokenList.add(t);
							}else{
								this.lfc.readNextToken();
								this.expected(new Token(this.tks.SEMICOLON, ";"));
								b = false;
							}
						}
					}
				}else if(this.isNextToken(new Token(this.tks.COMMA))){
					t = this.expected(new Token(this.tks.COMMA, ","));
					if(!this.isNextToken(new Token(this.tks.NUMBER)) && !this.isNextToken(new Token(this.tks.IDENTIFIER))){
						this.expected(new Token(this.tks.IDENTIFIER, "Identifier"), new Token(this.tks.NUMBER, "Number"));
						this.lfc.readNextToken();
						this.expected(new Token(this.tks.SEMICOLON, "\";\""));
						this.p.setError(true);
						b = false;
					}else{
						tokenList.add(t);
					}
				}else if(!this.isNextToken(new Token(this.tks.ROUND_BRACKET_CLOSE))){
					this.expected(this.expected(new Token(this.tks.SQUARE_BRACKET_OPEN, "[")), this.expected(new Token(this.tks.COMMA, ",")));
					this.lfc.readNextToken();
					this.expected(new Token(this.tks.SEMICOLON, ";"));
					b = false;
				}
			}else{
				this.lfc.readNextToken();
				this.expected(new Token(this.tks.SEMICOLON, ";"));
				b = false;
			}
		}
		if(b){
			Token t = this.expected(new Token(this.tks.ROUND_BRACKET_CLOSE, ")"));
			if(t.getToken()!=-1){
				tokenList.add(t);
			}else{
				b = false;
			}
		}
		return b;
	}

/*******************************************************************************************
 *  boolean parseArraySyntax(TokenArrayList tokenList)
 *  	- 
 *******************************************************************************************/
	private boolean parseArraySyntax(TokenArrayList tokenList, boolean expectedSquareBracketClose) {
		boolean b = true;
		if(this.isNextToken(new Token(this.tks.SQUARE_BRACKET_CLOSE)) && expectedSquareBracketClose){
			Token t1 = this.expected(new Token(this.tks.SQUARE_BRACKET_CLOSE, "]"));
			if(t1.getToken() != -1){
				tokenList.add(t1);
				t1 = this.expected(new Token(this.tks.IDENTIFIER, "\"Identifier\""));
				if(t1.getToken() != -1){
					tokenList.add(t1);
				}else{
					this.lfc.readNextToken();
					this.expected(new Token(this.tks.SEMICOLON, ";"));
					b = false;
				}
			}else{
				this.lfc.readNextToken();
				this.expected(new Token(this.tks.SEMICOLON, ";"));
				b = false;
			}
		}else if(expectedSquareBracketClose){
			this.expected(new Token(this.tks.SQUARE_BRACKET_CLOSE, "]"));
			this.lfc.readNextToken();
			this.expected(new Token(this.tks.SEMICOLON, ";"));
			b = false;
		}else{
			Token t = this.expected(new Token(this.tks.IDENTIFIER, "\"Identifier\""), new Token(this.tks.NUMBER, "\"Number\""));
			if(t.getToken() != -1){
				tokenList.add(t);
				t = this.expected(new Token(this.tks.SQUARE_BRACKET_CLOSE, "]"));
				if(t.getToken() != -1){
					tokenList.add(t);
					//TODO to implement -> a[5].r
				}else{
					this.lfc.readNextToken();
					this.expected(new Token(this.tks.SEMICOLON, ";"));
					b = false;
				}
			}else{
				this.lfc.readNextToken();
				this.expected(new Token(this.tks.SEMICOLON, ";"));
				b = false;
			}
			
		}
		
		return b;
	}

/*******************************************************************************************
 *  boolean parseArraySyntax(TokenArrayList tokenList)
 *  	- 
 *******************************************************************************************/
	private boolean parseArraySyntax(TokenArrayList tokenList) {
		boolean b = true;
		if(this.isNextToken(new Token(this.tks.SQUARE_BRACKET_CLOSE))){
			Token t1 = this.expected(new Token(this.tks.SQUARE_BRACKET_CLOSE, "]"));
			if(t1.getToken() != -1){
				tokenList.add(t1);
				t1 = this.expected(new Token(this.tks.IDENTIFIER, "\"Identifier\""));
				if(t1.getToken() != -1){
					tokenList.add(t1);
				}else{
					this.lfc.readNextToken();
					this.expected(new Token(this.tks.SEMICOLON, ";"));
					b = false;
				}
			}else{
				this.lfc.readNextToken();
				this.expected(new Token(this.tks.SEMICOLON, ";"));
				b = false;
			}
		}else{
			Token t = this.expected(new Token(this.tks.IDENTIFIER, "\"Identifier\""), new Token(this.tks.NUMBER, "\"Number\""));
			if(t.getToken() != -1){
				tokenList.add(t);
				t = this.expected(new Token(this.tks.SQUARE_BRACKET_CLOSE, "]"));
				if(t.getToken() != -1){
					tokenList.add(t);
					//TODO to implement -> a[5].r
				}else{
					this.lfc.readNextToken();
					this.expected(new Token(this.tks.SEMICOLON, ";"));
					b = false;
				}
			}else{
				this.lfc.readNextToken();
				this.expected(new Token(this.tks.SEMICOLON, ";"));
				b = false;
			}
			
		}
		
		return b;
	}

/*******************************************************************************************
 *  boolean parseArithmeticExpressionSyntax(TokenArrayList tokenList)
 *  	- 
 *******************************************************************************************/
	private boolean parseArithmeticExpressionSyntax(TokenArrayList tokenList) {
		boolean b = true;
		Token t;
		if(this.isNextToken(new Token(this.tks.SEMICOLON))){
			Token error = this.lfc.readNextToken();
			TokenArrayList tk = new TokenArrayList();
			tk.add(new Token(this.tks.NUMBER, "Number"));
			tk.add(new Token(this.tks.IDENTIFIER, "Identifier"));
			tk.add(new Token(this.tks.ROUND_BRACKET_OPEN, "("));
			tk.add(new Token(this.tks.ROUND_BRACKET_CLOSE, ")"));
			this.errors.printExpectedMoreTokensError(error, tk);
			this.p.setError(true);
			this.expected(new Token(this.tks.SEMICOLON, ";"));
			b = false;
		
		}else if(this.isNextTokenSubAddOperator()){
			t = this.lfc.readNextToken();
			//this.str = this.str + t.getText();
			tokenList.add(t);
			if(this.isNextToken(new Token(this.tks.ROUND_BRACKET_OPEN))){
				this.roundBracketOperCounter++;
				t = this.expected(new Token(this.tks.ROUND_BRACKET_OPEN, "("));
				//this.str = this.str + t.getText();
				tokenList.add(t);
				b = this.parseArithmeticExpressionSyntaxInsideRoundBracket(tokenList);
				if(b){
					b = this.parseArithmeticExpressionSyntaxAfterRoundBracketClose(tokenList);
				}
			}else if(!this.isNextToken(new Token(this.tks.IDENTIFIER)) && !this.isNextToken(new Token(this.tks.NUMBER))){
				this.expected(new Token(this.tks.IDENTIFIER, "Identifier") , new Token(this.tks.NUMBER, "Number"));
				this.lfc.readNextToken();
				this.expected(new Token(this.tks.SEMICOLON, ";"));
				b = false;
			}
		}
		while(!this.isNextToken(new Token(this.tks.SEMICOLON)) && b){
			if(this.isNextToken(new Token(this.tks.IDENTIFIER))){
				t = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"));
				//this.str = this.str + t.getText();
				tokenList.add(t);
				if(this.isNextToken(new Token(this.tks.DOT))){
					t = this.expected(new Token(this.tks.DOT, "."));
					//this.str = this.str + t.getText();
					tokenList.add(t);
					b = this.parseMethodOrFieldCallSyntax(tokenList, false);
				}else if(this.isNextToken(new Token(this.tks.SQUARE_BRACKET_OPEN))){
					t = this.expected(new Token(this.tks.SQUARE_BRACKET_OPEN, "["));
					////this.str = //this.str + t.getText();
					tokenList.add(t);
					b = this.parseArraySyntax(tokenList, false);
				}else if(this.isNextToken(new Token(this.tks.ROUND_BRACKET_OPEN))){
					t = this.expected(new Token(this.tks.ROUND_BRACKET_OPEN, "("));
					//this.str = this.str + t.getText();
					tokenList.add(t);
					b = this.parseParametersSyntax(tokenList, false);
				}
				if(b){
					if(this.isNextTokenOperator()){
						t = this.lfc.readNextToken();
						//this.str = this.str + t.getText();
						tokenList.add(t);
						b = this.parseArithmeticExpressionAfterOperatorToken();
					}else if(!this.isNextToken(new Token(this.tks.SEMICOLON)) && !this.isNextToken(new Token(this.tks.ROUND_BRACKET_CLOSE))){
						//this.expected(new Token.)
						
						this.expected(new Token(this.tks.SEMICOLON, ";"), new Token(this.tks.ROUND_BRACKET_CLOSE, ")"));
						this.lfc.readNextToken();
						b = false;
					}
				}
			}else if(this.isNextToken(new Token(this.tks.NUMBER))){
				t = this.expected(new Token(this.tks.NUMBER));
				//this.str = this.str + t.getText();
				tokenList.add(t);
				if(this.isNextTokenOperator()){
					t = this.lfc.readNextToken();
					//this.str = this.str + t.getText();
					tokenList.add(t);
					b = this.parseArithmeticExpressionAfterOperatorToken();
				}else if(!this.isNextToken(new Token(this.tks.SEMICOLON))){
					//this.expected(new Token.)
					this.expected(new Token(this.tks.SEMICOLON, ";"));
					this.lfc.readNextToken();
					b = false;
				}
			}else if(this.isNextToken(new Token(this.tks.ROUND_BRACKET_OPEN))){
				t = this.expected(new Token(this.tks.ROUND_BRACKET_OPEN, "("));
				//this.str = this.str + t.getText();
				this.roundBracketOperCounter++;
				tokenList.add(t);
				b = this.parseArithmeticExpressionSyntaxInsideRoundBracket(tokenList);
				if(b){
					b = this.parseArithmeticExpressionSyntaxAfterRoundBracketClose(tokenList);
				}
			}else if(this.isNextToken(new Token(this.tks.ROUND_BRACKET_CLOSE))){
				if(this.roundBracketOperCounter < 1){
					t = this.lfc.readNextToken();
					this.errors.printToMuchRoundBracketCloseError(t);
					this.p.setError(true);
					b = false;
				}else{
					this.roundBracketOperCounter--;
					return b;
				}
			}else{
				Token error = this.lfc.readNextToken();
				TokenArrayList tk = new TokenArrayList();
				tk.add(new Token(this.tks.NUMBER, "Number"));
				tk.add(new Token(this.tks.IDENTIFIER, "Identifier"));
				tk.add(new Token(this.tks.ROUND_BRACKET_OPEN, "("));
				tk.add(new Token(this.tks.ROUND_BRACKET_CLOSE, ")"));
				this.errors.printExpectedMoreTokensError(error, tk);
				this.p.setError(true);
				this.expected(new Token(this.tks.SEMICOLON, ";"));
				b = false;
			}
		}
		if(b){
			if(this.roundBracketOperCounter > 0){
				t = this.expected(new Token(this.tks.ROUND_BRACKET_CLOSE, "\")\""));
				this.lfc.readNextToken();
				this.expected(new Token(this.tks.SEMICOLON, "\";\""));
				b = false;
			}else{
				t = this.expected(new Token(this.tks.SEMICOLON, "\";\""));
				if(t.getToken() == -1){
					this.lfc.readNextToken();
					b = false;
				}else{
					tokenList.add(t);
				}
			}
		}
		return b;
	}
/*******************************************************************************************
 *  boolean parseReturnSyntax(Method m, boolean scopeCall)
 *  	- 
 *******************************************************************************************/
	private boolean parseReturnSyntax(Method m, boolean scopeCall) {
		boolean b = true;
		Token t = this.expected(new Token(this.tks.IDENTIFIER, "\"Identifier\""), new Token(this.tks.NUMBER, "\"Number\""));
		if(t.getToken() != -1){
			if(t.isIdentifierToken()){
				this.tList.add(t);
				if(this.isNextToken(new Token(this.tks.SEMICOLON))){
					t = this.expected(new Token(this.tks.SEMICOLON, "\";\""));
					this.tList.add(t);
				}else{
					this.expected(new Token(this.tks.SEMICOLON, "\";\""));
					this.lfc.readNextToken();
					b = false;
				}
			}
			else{
				t = this.expected(new Token(this.tks.SEMICOLON, "\";\""));
				if(t.getToken() != -1){
					this.tList.add(t);
				}else{
					this.lfc.readNextToken();
					b = false;
				}
			}
		}else{
			this.lfc.readNextToken();
			this.expected(new Token(this.tks.SEMICOLON, "\";\""));
			b = false;
		}
		
		return b;
	}
	
/*******************************************************************************************
 *  private boolean parseNewObjectSyntax()
 *  	- 
 *******************************************************************************************/
	private boolean parseNewObjectSyntax() {
		boolean b = true;
		Token t = this.expected(new Token(this.tks.NEW, "\"New\""));
		this.tList.add(t);
		if(this.isNextToken(new Token(this.tks.IDENTIFIER))){
			t = this.expected(new Token(this.tks.IDENTIFIER, "\"Identifier\""));
			this.tList.add(t);
			t = this.expected(new Token(this.tks.ROUND_BRACKET_OPEN, "\"(\""));
			if(t.getToken() != -1){
				this.tList.add(t);
				//TODO PARAMETER LIST FOR NEW OBJECT(CONSTRUCTORS)
				t = this.expected(new Token(this.tks.ROUND_BRACKET_CLOSE, "\")\""));
				if(t.getToken() != -1){
					this.tList.add(t);
				}else{
					this.lfc.readNextToken();
					this.expected(new Token(this.tks.SEMICOLON, "\";\""));
					b = false;
				}
			}else{
				this.lfc.readNextToken();
				this.expected(new Token(this.tks.SEMICOLON, "\";\""));
				b = false;
			}
		}else if(this.isNextToken(new Token(this.tks.INT))){
			t = this.expected(new Token(this.tks.INT, "\"Integer\""));
			this.tList.add(t);
			t = this.expected(new Token(this.tks.SQUARE_BRACKET_OPEN, "\"[\""));
			if(t.getToken() != -1){
				this.tList.add(t);
				b = this.parseArraySyntax(tList, false);
			}else{
				this.lfc.readNextToken();
				this.expected(new Token(this.tks.SEMICOLON, "\";\""));
				b = false;
			}
		}else{
			this.expected(new Token(this.tks.INT, "\"Integer\""), new Token(this.tks.IDENTIFIER, "\"Identifier\""));
			t = this.lfc.readNextToken();
			this.expected(new Token(this.tks.SEMICOLON, "\";\""));
			b = false;
		}

		if(b){
			t = this.expected(new Token(this.tks.SEMICOLON, "\";\""));
			if(t.getToken() != -1){
				this.tList.add(t);
			}else{
				b=false;
			}
		}
		return b;
	}
	
/*******************************************************************************************
 *  boolean parseArithmeticExpressionSyntaxInsideRoundBracket(TokenArrayList tokenList)
 *  	- 
 *******************************************************************************************/
	private boolean parseArithmeticExpressionSyntaxInsideRoundBracket(TokenArrayList tokenList) {
		boolean b = true;
		if(this.isNextToken(new Token(this.tks.ROUND_BRACKET_CLOSE))){
			Token t = this.expected(new Token(this.tks.ROUND_BRACKET_CLOSE, ")"));
			this.errors.printIlegalStartOfType(t);
			this.p.setError(true);
			b = false;
		}else{
			b = this.parseArithmeticExpressionSyntax(tokenList);
			if(b){
				Token t = this.expected(new Token(this.tks.ROUND_BRACKET_CLOSE, ")"));
				if(t.getToken() != -1){
					tokenList.add(t);
					//this.str = this.str + t.getText();
				}else{
					this.lfc.readNextToken();
					this.expected(new Token(this.tks.SEMICOLON, ";"));
					b = false;
				}
			}
		}
		
		
		return b;
	}
	
/*******************************************************************************************
 *  boolean parseArithmeticExpressionSyntaxAfterRoundBracketOpen(TokenArrayList tokenList)
 *  	- 
 *******************************************************************************************/
	private boolean parseArithmeticExpressionAfterOperatorToken() {
		boolean b = true;
		if(!this.isNextToken(new Token(this.tks.IDENTIFIER)) && !this.isNextToken(new Token(this.tks.NUMBER)) && !this.isNextToken(new Token(this.tks.ROUND_BRACKET_OPEN))){
			Token error = this.lfc.readNextToken();
			TokenArrayList tk = new TokenArrayList();
			tk.add(new Token(this.tks.NUMBER, "Number"));
			tk.add(new Token(this.tks.IDENTIFIER, "Identifier"));
			tk.add(new Token(this.tks.ROUND_BRACKET_OPEN, "("));
			this.errors.printExpectedMoreTokensError(error, tk);
			this.p.setError(true);
			this.lfc.readNextToken();
			b = false;
		}
		return b;
	}
	
/*******************************************************************************************
 *  boolean parseArithmeticExpressionSyntaxBeforeRoundBracketClose(TokenArrayList tokenList)
 *  	- 
 *******************************************************************************************/
	private boolean parseArithmeticExpressionSyntaxAfterRoundBracketClose(TokenArrayList tokenList) {
		boolean b = true;
		Token t;
		if(this.isNextTokenOperator()){
			t = this.lfc.readNextToken();
			tokenList.add(t);
			//this.str = this.str + t.getText();
			if(!this.isNextToken(new Token(this.tks.IDENTIFIER)) && !this.isNextToken(new Token(this.tks.NUMBER)) && !this.isNextToken(new Token(this.tks.ROUND_BRACKET_OPEN))){
				Token error = this.lfc.readNextToken();
				TokenArrayList tk = new TokenArrayList();
				tk.add(new Token(this.tks.NUMBER, "Number"));
				tk.add(new Token(this.tks.IDENTIFIER, "Identifier"));
				tk.add(new Token(this.tks.ROUND_BRACKET_OPEN, "("));
				this.errors.printExpectedMoreTokensError(error, tk);
				this.p.setError(true);
				this.lfc.readNextToken();
				this.expected(new Token(this.tks.SEMICOLON, ";"));
				b = false;
			}
		}else if(!this.isNextToken(new Token(this.tks.SEMICOLON)) && !this.isNextToken(new Token(this.tks.ROUND_BRACKET_CLOSE))){
			this.expected(new Token(this.tks.SEMICOLON, ";"), new Token(this.tks.ROUND_BRACKET_CLOSE, ")"));
			this.lfc.readNextToken();
			b = false;
		}
		return b;
	}
		
/*******************************************************************************************
 *  isNextTokenSubAddOperator(Token t) 
 *  	- 
 *******************************************************************************************/
	private boolean isNextTokenSubAddOperator(Token t){
		//Token t1 = this.p.getLfc().lookAhead();
		if((t.getToken() == this.tks.ADD) || (t.getToken() == this.tks.SUB)){
			return true;
		}
		return false;
	}
/*******************************************************************************************
 *  isNextTokenSubAddOperator() 
 *  	- 
 *******************************************************************************************/
	private boolean isNextTokenSubAddOperator(){
		Token t1 = this.p.getLfc().lookAhead();
		if((t1.getToken() == this.tks.ADD) || (t1.getToken() == this.tks.SUB)){
			return true;
		}
		return false;
	}
/*******************************************************************************************
 *  boolean checkAndPrintFieldAlreadyExists(Token name, Method m, boolean scopeCall, boolean forError)
 *  	- 
 *******************************************************************************************/
	private boolean checkAndPrintFieldAlreadyExists(Token name, Method m, boolean scopeCall, boolean forError) {
		boolean err = this.checkIfFieldExsists(name, m, scopeCall, true);
		if(err){
			Field field = null;
			if(scopeCall){
				field = m.findFieldInsideMethoAndClassAndScope(name.getText());
			}else{
				field = m.findFieldInsideMethoAndClass(name.getText());
			}
			this.errors.printContainsFieldError(name, field.getToken());
			this.p.setError(true);
		}
		return err;
	}
	
/*******************************************************************************************
 *  boolean checkFieldExsistsError(Token name, Method m, boolean scopeCall)
 *  	- 
 *******************************************************************************************/
	private boolean checkIfFieldExsists(Token name, Method m, boolean scopeCall, boolean forError) {
		boolean exists = false;
		if(scopeCall){
			exists = m.isContainingFildMethodAndClassAndLoops(name.getText(), forError);
		}else{
			exists = m.isContainingFildMethodAndClass(name.getText(), forError);
		}
		return exists;
	}
/*******************************************************************************************
 *  void checkTypeExistanceOrAddToLocals(Token type, Token name, Method m)
 *  	- 
 *******************************************************************************************/
	private boolean checkTypeExistanceOrAddToLocals(Token type, Token name, Method m) {
		boolean b = true;
		Class clazz = m.getClazz();
		Field s = null;
		if(type.isIntToken()){
			s = new Field(new Type(type), name.getText(), clazz, name);
			m.addLocalVariable(s);
			if(!m.getAlreadyDefinedFields().containsByName(s.getName())){
				m.addToAlreadyDefinedFields(s);
				m.addFieldToStackFrameFieldCounter(s);
			}
		}else if(type.isIdentifierToken()){
			if(type.getText().equals(clazz.getName())){
				s = new Field(new Type(clazz), name.getText(), clazz, name);
				m.addLocalVariable(s);
				if(!m.getAlreadyDefinedFields().containsByName(s.getName())){
					m.addToAlreadyDefinedFields(s);
					m.addFieldToStackFrameFieldCounter(s);
				}
			}else{
				boolean err = false;
				Class cl = clazz.getUsedOrImportedClassByName(type.getText());
				if(cl != null){
					s = new Field(new Type(cl), name.getText(), clazz, name);
					m.addLocalVariable(s);
					if(!m.getAlreadyDefinedFields().containsByName(s.getName())){
						m.addToAlreadyDefinedFields(s);
						m.addFieldToStackFrameFieldCounter(s);
					}
				}else{
					err =  this.ifFileOrClassDoesntExists(type, clazz);
					if(!err){
						cl = clazz.getUsedOrImportedClassByName(type.getText());
						s = new Field(new Type(cl), name.getText(), clazz, name);
						m.addLocalVariable(s);
						if(!m.getAlreadyDefinedFields().containsByName(s.getName())){
							m.addToAlreadyDefinedFields(s);
							m.addFieldToStackFrameFieldCounter(s);
						}
					}else{
						b = false;
					}
				}
			}
		}else{
			this.expected(new Token(this.tks.IDENTIFIER, "\"Identifier\""), new Token(this.tks.INT, "\"Integer\""));
			this.lfc.readNextToken();
			this.expected(new Token(this.tks.SEMICOLON, "\";\""));
			this.p.setError(true);
			b = false;
		}
		return b;
	}
	
/*****************************************************************************************************************
 * void checkIfFileOrClassExists(Token type)
 * 		-  
 ****************************************************************************************************************/
	private boolean ifFileOrClassDoesntExists(Token type, Class clazz) {
		//TODO Implement for java/lang/ or find similar solution
		boolean b = false;
		if(type.getText().equals("String")){
			//TODO read the parent class from class file
			if(!clazz.isAllreadyContainingClass("java/lang/String")){
				clazz.addUsedClasses(new Class("java/lang/String", clazz.getSuperClass(), "java/lang/String", "java/lang/"));//type.getText(), null, "java/lang/String", "java/lang/String"));
			}
		}else{
			if(!this.checkFile(clazz.getFilePath()  + type.getText() + ".java")){
				this.errors.printFileDoesNotExists(type, type.getText());
				b = true;
				this.p.setError(true);
			}else{
				String dirPath = clazz.getFilePath();
				String packageName = dirPath + "/" + type.getText();
				clazz.addUsedClasses(new Class(type.getText(), clazz.getSuperClass(), packageName, dirPath));
			}
		}
		return b;
		
	}
/*******************************************************************************************
 * boolean checkFile(String importName) 
 *  		-
 *******************************************************************************************/
	private boolean checkFile(String importName) {
		String str = importName;
		boolean checkFile = new File(str).isFile();
		return checkFile;
	}
/*******************************************************************************************
 *  boolean checkFieldDoesntExsistsError(Token name, Method m, boolean scopeCall)
 *  	- 
 *******************************************************************************************/
	private Field getExistingFieldOrNull(Token name, Method m, boolean scopeCall) {
		//boolean err = false;
		Field field = null;
		if(scopeCall){
			//err =	m.isContainingFildMethodAndClassAndLoops(name.getText(), false);
			field = m.findFieldInsideMethoAndClassAndScope(name.getText());
		}else{
			//err = m.isContainingFildMethodAndClass(name.getText(), false);
			field = m.findFieldInsideMethoAndClass(name.getText());
		}
		/*if(!err){
			this.errors.printFieldDoesNotExists(name);
			this.p.setError(true);
		}*/
		return field;
	}
/*******************************************************************************************
 *  void checkTypeExistanceOrAddToLocalsWithoutDeclaration(Token type, Token name, Method m)
 *  	- 
 *******************************************************************************************/
	private boolean checkTypeExistanceOrAddToLocalsWithoutDeclaration(Token type, Token name, Method m) {
		boolean b = true;
		Class clazz = m.getClazz();
		Field s = null;
		if(type.isIntToken()){
			s = new Field(new Type(type), name.getText(), clazz, name);
			m.addLocalVariable(s);
			/*if(!m.getAlreadyDefinedFields().containsByName(s.getName())){
				m.addToAlreadyDefinedFields(s);
				m.addFieldToStackFrameFieldCounter(s);
			}*/
		}else if(type.isIdentifierToken()){
			if(type.getText().equals(clazz.getName())){
				s = new Field(new Type(clazz), name.getText(), clazz, name);
				m.addLocalVariable(s);
			}else{
				boolean err = false;
				Class cl = clazz.getUsedOrImportedClassByName(type.getText());
				if(cl != null){
					s = new Field(new Type(cl), name.getText(), clazz, name);
					m.addLocalVariable(s);
					/*if(!m.getAlreadyDefinedFields().containsByName(s.getName())){
						m.addToAlreadyDefinedFields(s);
						m.addFieldToStackFrameFieldCounter(s);
					}*/
				}else{
					err =  this.ifFileOrClassDoesntExists(type, clazz);
					if(!err){
						cl = clazz.getUsedOrImportedClassByName(type.getText());
						s = new Field(new Type(cl), name.getText(), clazz, name);
						m.addLocalVariable(s);
						/*if(!m.getAlreadyDefinedFields().containsByName(s.getName())){
							m.addToAlreadyDefinedFields(s);
							m.addFieldToStackFrameFieldCounter(s);
						}*/
					}else{
						b = false;
					}
				}
			}
		}else{
			this.expected(new Token(this.tks.IDENTIFIER, "\"Identifier\""), new Token(this.tks.INT, "\"Integer\""));
			this.lfc.readNextToken();
			this.expected(new Token(this.tks.SEMICOLON, "\";\""));
			this.p.setError(true);
			b = false;
		}
		return b;
	}
/*******************************************************************************************
 *  boolean checkArrayExistanceOrAddToLocals(Token t, Token t1, Method m)
 *  	- 
 *******************************************************************************************/
	private boolean checkArrayExistanceOrAddToLocals(Token t, Token t1, Method m) {
		boolean b = true;
		Class clazz = m.getClazz();
		Field s = null;
		if(t.isIntToken()){
			s = new Field(new Type(new Type(t)), t1.getText(), clazz, t1);
			m.addLocalVariable(s);
			if(!m.getAlreadyDefinedFields().containsByName(s.getName())){
				m.addToAlreadyDefinedFields(s);
				m.addFieldToStackFrameFieldCounter(s);
			}
		}else if(t.isIdentifierToken()){
			//TODO
		}else{
			this.expected(new Token(this.tks.IDENTIFIER, "\"Identifier\""), new Token(this.tks.INT, "\"Integer\""));
			this.lfc.readNextToken();
			this.expected(new Token(this.tks.SEMICOLON, "\";\""));
			this.p.setError(true);
			b = false;
		}
		return b;
	}
}