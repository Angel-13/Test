package milestone2;

import symbolTable.Field;
import symbolTable.Method;
import symbolTable.TokenArrayList;
import symbolTable.Type;
import tokens.Token;

public class ErrorsClass {
	
	public void printError(Token token, Token token1){
		System.out.println("Error at line: " + token.getTokenPosition().raw + ", raw: "
				+ token.getTokenPosition().column + ", next Token: " + token.getText()
					+ ", expeceted Token: " + token1.getText());
	}
	
	public void printError(Token token, Token token1, Token token2){
		System.out.println("Error at line: " + token.getTokenPosition().raw + ", raw: "
				+ token.getTokenPosition().column + ", next Token: " + token.getText()
					+ ", expeceted Token: " + token1.getText() + " or " + token2.getText());
	}
	
/*******************************************************************************************
 *  printIlegalStartOfType(Token t)
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	public void printIlegalStartOfType(Token t) {
		System.out.println("Error at line: " + t.getTokenPosition().raw + ", raw: "
				+ t.getTokenPosition().column + ", next Token: " + t.getText()
					+ ", illegal start of type!!!");
	}
	
/*******************************************************************************************
 *  printContainsFieldError(Token token)
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	public void printContainsFieldError(Token token) {
		System.out.println("Error at line: " + token.getTokenPosition().raw + ", raw: "
				+ token.getTokenPosition().column + ", field name: " + token.getText() + " allready exists in the class!!!");
	}
	
/*******************************************************************************************
 *  printErrorFieldDeclaration(Token t) - prints an error that happens before class definition
 *******************************************************************************************/	
	public void printErrorFieldDeclaration(Token t) {
		System.out.println("Error at line: " + t.getTokenPosition().raw + ", raw: "
				+ t.getTokenPosition().column + ", next Token: " + t.getText() +
					", next expected Tokens: Identifier or int type or char type!!!");
	}
	
/*******************************************************************************************
 *  printExpectsOperatorError(Token t) 
 *  	- prints an error missing operator
 *******************************************************************************************/	
	public void printExpectsOperatorError(Token t) {
		System.out.println("Error at line: " + t.getTokenPosition().raw + ", raw: "
				+ t.getTokenPosition().column + ", next Token: " + t.getText() +
					", next expected Tokens: Plus-Operator, Minus-Operator, Divide-Operator or Multiply-Operator!!!");
	}
/*******************************************************************************************
 *  printNotCompatibleTypes(Field f, Field f1)
 *  	- prints an error missing operator
 *******************************************************************************************/	
	public void printNotCompatibleTypes(Field f, Field f1) {
		System.out.println("Error at line: " + f1.getToken().getTokenPosition().raw + ", raw: "
				+ f1.getToken().getTokenPosition().column + " , Token Name: " + f1.getName() + " Type: " + f1.getType().toString() + 
					", is not compatible with the type " + f.getType().toString() + " of the variable " + f.getName() + "!!!");
	}
	
/*******************************************************************************************
 * void printNotCompatibleType(Field f, Token exptectedType)
 *  	- 
 *******************************************************************************************/	
	public void printNotCompatibleType(Field f, Token exptectedType) {
		System.out.println("Error at line: " + f.getToken().getTokenPosition().raw + ", raw: "
				+ f.getToken().getTokenPosition().column + " , Token Name: " + f.getName() + " Type: " + f.getType().toString() + 
					", is not compatible with the exptected type " + exptectedType.getText() + "!!!");
	}
	
/*******************************************************************************************
 * void printNotCompatibleType(Field f, Type baseType)
 *  	- 
 *******************************************************************************************/	
	public void printNotCompatibleArrayTypes(Field f, Field f1) {
		System.out.println("Error at line: " + f.getToken().getTokenPosition().raw + ", raw: "
				+ f.getToken().getTokenPosition().column + " , Token Name: " + f.getName() + " Type: " + f.getType().getBaseType().toString() + 
					", is not compatible with the type " + f1.getType().getBaseType().toString() + " of the variable" + f1.getName()  + "!!!");
		
	}
	
/*******************************************************************************************
 * void printNotCompatibleType(Field f, Token exptectedType)
 *  	- 
 *******************************************************************************************/	
	public void printNotCompatibleType(Field f, Token errPos, Token exptectedType) {
		System.out.println("Error at line: " + errPos.getTokenPosition().raw + ", raw: "
				+ errPos.getTokenPosition().column + " , Token Name: " + f.getName() + " Type: " + f.getType().toString() + 
					", is not compatible with the exptected type " + exptectedType.getText() + "!!!");
	}
/*******************************************************************************************
 *  printNotCompatibleTypes(Field f, Token t)
 *  	- prints an error missing operator
 *******************************************************************************************/	
	public void identifierDoesNotExistsError(Token t) {
		System.out.println("Error at line: " + t.getTokenPosition().raw + ", raw: "
				+ t.getTokenPosition().column + ", next Token: " + t.getText() +
					", no such defined Object-name!!!");
		
	}
/*******************************************************************************************
 *  printNotCompatibleTypes(Field f, Token t)
 *  	- prints an error missing operator
 *******************************************************************************************/
	public void printComparableOperatorError(Token t) {
		System.out.println("Error at line: " + t.getTokenPosition().raw + ", raw: "
				+ t.getTokenPosition().column + ", next Token: " + t.getText()
					+ ", expeceted Token: Equal or Greater Equal or Greater or Smaller or Smaller Equal Operator!!!");
	}
/*******************************************************************************************
 *  printContainsFieldError(Token token, Token token2)
 *  	- 
 *******************************************************************************************/
	public void printContainsFieldError(Token token, Token token2) {
		System.out.println("Error at line: " + token.getTokenPosition().raw + ", raw: "
				+ token.getTokenPosition().column + ", field name: " + token.getText() + " allready exists in the class at line: "
				 + token2.getTokenPosition().raw + ", raw: " + token2.getTokenPosition().column + "!!!");
		
	}
/*******************************************************************************************
 *  printMethodBodyError(Token err)
 *  	- 
 *******************************************************************************************/
	public void printMethodBodyError(Token token) {
		System.out.println("Error at line: " + token.getTokenPosition().raw + ", raw: "
				+ token.getTokenPosition().column + ", next token: " + token.getText() + " expected tokens: " +
				"Identifier or int as types, if or while conditions, return  or \"}\"!!!");
	}
/*******************************************************************************************
 *  printMethodBodyError(Token err)
 *  	- 
 *******************************************************************************************/
	public void printAlreadyImplementedMethodError(Method m) {
		System.out.println("Error: Method with name: " + m.getName() + ", and parameters: " + m.printParameters() + " already exsits!!!");
		
	}
/*******************************************************************************************
 *  printIsAlreadyImportedClass(String importName)
 *  	- 
 *******************************************************************************************/
	public void printIsAlreadyImportedClass(String importName) {
		System.out.println("Error: Class :" +importName + " is already imported!!!");
	}
	
/*******************************************************************************************
 *  printNotCompatibleTypes(Field f, Field f1)
 *  	- prints an error missing operator
 *******************************************************************************************/	
	public void printNotCompatibleTypes(Field f, Method m) {
		System.out.println("Error at line: " + f.getToken().getTokenPosition().raw + ", raw: "
				+	f.getToken().getTokenPosition().column + " , Token Name: " + f.getName() + " Type: " + f.getType().toString() + 
					", is not compatible with the return type " + m.getRetrunType().toString() + " of the method " + m.getName() + "!!!");
	}
/*******************************************************************************************
 *  printPackageDoesNotExists(Token token, String importName)
 *  	- 
 *******************************************************************************************/
	public void printPackageDoesNotExists(Token token, String packageName) {
		System.out.println("Error at line: " + token.getTokenPosition().raw + ", raw: "
				+	token.getTokenPosition().column + " , Package " + packageName + " does not exists!");
		
	}
/*******************************************************************************************
 *  printFileDoesNotExists(Token token, String importName)
 *  	- 
 *******************************************************************************************/
	public void printFileDoesNotExists(Token token, String importName) {
		System.out.println("Error at line: " + token.getTokenPosition().raw + ", raw: "
				+	token.getTokenPosition().column + " , File " + importName + " does not exists!");
		
	}
/*******************************************************************************************
 *  void printNotCompatibleClasses(Field f, Token type)
 *  	- 
 *******************************************************************************************/
	public void printNotCompatibleClasses(Field f, Token type) {
		System.out.println("Error at line: " + type.getTokenPosition().raw + ", raw: "
				+	type.getTokenPosition().column + " , Incompatible types " + f.getType().toString() + " with " + type.getText() + "!");
	}
/*******************************************************************************************
 *  printExpectedMoreTokensError(Token name, TokenArrayList tk)
 *  	- 
 *******************************************************************************************/
	public void printExpectedMoreTokensError(Token name, TokenArrayList tk) {
		String str = "Error at line: " + name.getTokenPosition().raw + ", raw: " + name.getTokenPosition().column + " , Next Token: \"" + name.getText() + "\", expected Toknes: \"";
		for(int i = 0; i < tk.size(); i++){
			if(i == (tk.size()-1)){
				str = str + tk.get(i).getText() + "\"!" ;
			}else{
				str = str + tk.get(i).getText() + "\", \"" ;
			}
		}
		System.out.println(str);
	}
/*******************************************************************************************
 *  void printFieldDoesNotExists(Token fieldOrMethodName)
 *  	- 
 *******************************************************************************************/
	public void printFieldDoesNotExists(Token fieldOrMethodName) {
		System.out.println("Error at line: " + fieldOrMethodName.getTokenPosition().raw + ", raw: "
				+	fieldOrMethodName.getTokenPosition().column + " , Field " + fieldOrMethodName.getText() + " does not exists!");
	}
	
/*******************************************************************************************
 *  void printFieldDoesNotExists(Token fieldOrMethodName)
 *  	- 
 *******************************************************************************************/
	public void printReferenceDoesNotExist(Token fielRef) {
		System.out.println("Error at line: " + fielRef.getTokenPosition().raw + ", raw: "
				+	fielRef.getTokenPosition().column + " , Field refefence " + fielRef.getText() + " does not exists!");
	}
	
/*******************************************************************************************
 *  void printExpectedArrayFieldRef(Field fielRef)
 *  	- 
 *******************************************************************************************/
	public void printExpectedArrayFieldRef(Field fielRef) {
		System.out.println("Syntax error at line: " + fielRef.getToken().getTokenPosition().raw + ", raw: "
				+	fielRef.getToken().getTokenPosition().column + " , Field refefence " + fielRef.getName() + ", type: " + fielRef.getType().toString()  + ", expected Array!");
	}

/*******************************************************************************************
 *  printNotCompatibleTypes(Field f, Field f1)
 *  	- 
 *******************************************************************************************/	
	public void printNotCompatibleRetunTypes(Token to, Method m, Type t) {
		System.out.println("Error at line: " +to.getTokenPosition().raw + ", raw: "
				+	to.getTokenPosition().column + " , Method : " + to.getText() + " with return type: " + m.getRetrunType().toString() + 
					", is not compatible with the expected return type " + t.toString() + "!");
	}
		
/*******************************************************************************************
 *  void printNotStatmementError(Token token)
 *  	- 
 *******************************************************************************************/
	public void printNotStatmementError(Token token) {
		System.out.println("Error at line: " +token.getTokenPosition().raw + ", raw: "
				+	token.getTokenPosition().column + " , token : " + token.getText() + " is not a statement!");
		
	}
/*******************************************************************************************
 *  void printNotStatmementError(Token token)
 *  	- 
 *******************************************************************************************/
	public void printToMuchRoundBracketCloseError(Token t) {
		System.out.println("Error at line: " +t.getTokenPosition().raw + ", raw: "
				+	t.getTokenPosition().column + " , to many \"" + t.getText() + "\" brackets");
		
	}
/*******************************************************************************************
 *  void printFieldNotDeclaredError(Token classR)
 *  	- 
 *******************************************************************************************/
	public void printFieldNotDeclaredError(Token classR) {
		System.out.println("Error at line: " +classR.getTokenPosition().raw + ", raw: "
				+	classR.getTokenPosition().column + " Field " + classR.getText() + " is not initialized");
	}
/*******************************************************************************************
 *  void printExpectedNoVoidReturnType(Token token) 
 *  	- 
 *******************************************************************************************/
	public void printExpectedNoVoidReturnType(Token token) {
		System.out.println("Error at line: " + token.getTokenPosition().raw + ", raw: "
				+	token.getTokenPosition().column + " retrun type: VOID, expected not-VOID return Type!");
		
	}
/*******************************************************************************************
 *  void printVoidRetrunTypeError(Token type, Method m)
 *  	- 
 *******************************************************************************************/
	public void printVoidRetrunTypeError(Token type, Method m) {
		System.out.println("Error at line: " + type.getTokenPosition().raw + ", raw: "
				+	type.getTokenPosition().column + " next Token: \"" + type.getText()  + "\", method name: \"" + m.getName() + "\" has retrun type: VOID, cannot retrunt value!");
		
	}
/*******************************************************************************************
 *  void printVariableIsNotAnObject(Token classR)
 *  	- 
 *******************************************************************************************/
	public void printVariableIsNotAnObject(Field classReference, Token classR) {
		System.out.println("Error at line: " + classR.getTokenPosition().raw + ", raw: "
				+	classR.getTokenPosition().column + " next Token: \"" + classReference.getName()  + "\" has Type: \"" + 
					classReference.getType().toString() + "\" is not and Object and cannot be dereferenced!");
		
		
	}
/*******************************************************************************************
 *  printExpectedVariableNotValue(TokenArrayList tList, int pos)
 *  	- 
 *******************************************************************************************/
	public void printExpectedVariableNotValue(TokenArrayList tList, int pos) {
		Token t = tList.get(pos);
		System.out.println("Error at line: " + t.getTokenPosition().raw + ", raw: "
				+	t.getTokenPosition().column + " at expression \"" + tList.printTokens()  + "\" left side is a method call which returns Value, " +
						"epected variable!");
		
	}
/*******************************************************************************************
 *  void printMethodExpectedRetrunTypeError(Method m, Method toBeChecked)
 *  	- 
 *******************************************************************************************/
	public void printMethodExpectedRetrunTypeError(Method m, Method toBeChecked) {
		Token t = toBeChecked.getToken();
		System.out.println("Error at line: " + t.getTokenPosition().raw + ", raw: "
				+	t.getTokenPosition().column + " method: \"" + toBeChecked.getName() +  "\" has RetrunType \"" + toBeChecked.getRetrunType().toString()
					+	"\", epected Retrun Type: \"" + m.getRetrunType().toString() + "\"!");
		
	}
/*******************************************************************************************
 *  void printMethodDoesNotExistsError(Method me, Token token)
 *  	- 
 *******************************************************************************************/
	public void printMethodDoesNotExistsError(Method me, Token t) {
		System.out.println("Error at line: " + t.getTokenPosition().raw + ", raw: "
				+	t.getTokenPosition().column + " method: \"" + me.getName() +  "\" with parameters: \"(" + me.printParameters()
					+	")\", does not exists!");
		
	}




}


