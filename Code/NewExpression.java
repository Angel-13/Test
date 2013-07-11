package Code;

import mapsTable.FieldIntMap;
import scanner.LookForwardScanner;
import symbolTable.Field;
import symbolTable.Method;
import symbolTable.Class;
import tokens.Token;
import tokens.Tokens;
import compileTable.ByteWriter;
import compileTable.Operations;

public class NewExpression implements Expression{

	private final Operations operations;
	
	private final Tokens tokens;

	private final ByteWriter code;
	
	private final FieldIntMap fieldsMap;
	
	private final Method method;
	
	private final LookForwardScanner lfc;
	
	private final Field f;
	
	public NewExpression(LookForwardScanner lfc, FieldIntMap fieldMap, Method method, Field f){
		this.operations = new Operations();
		//this.tokenList = new TokenArrayList();
		this.lfc = lfc;
		this.code = new ByteWriter();
		this.fieldsMap = fieldMap;
		this.method = method;
		this.tokens = new Tokens();
		this.f = f;
	}
	
	public NewExpression(FieldIntMap fieldMap, Method method){
		this.operations = new Operations();
		//this.tokenList = new TokenArrayList();
		this.lfc = null;
		this.code = new ByteWriter();
		this.fieldsMap = fieldMap;
		this.method = method;
		this.tokens = new Tokens();
		this.f = null;
	}
	
	
	@Override
	public ByteWriter getCode() {
		Class cl = this.method.getClazz();
		if(cl.isAllreadyContainingClassReference(f.getType().getClazz().getName())){
			//Class c = cl.getClassReferenceByName(f.getType().getClazz().getName());
			Class c = cl.getFromClassReferenceByName(f.getType().getClazz().getName());
			int num = this.method.getClazz().getClassIntMap().get(c);
			this.code.write1Byte(0xbb);
			this.code.write2Byte(num);
			this.code.write1Byte(0x59);
			this.code.write1Byte(0xb7);
			this.code.write2Byte(this.method.getClazz().getClassIntMap().get(c)+1);
		}else{
			this.code.write1Byte(0xbb);
			this.code.write2Byte(this.method.getClazz().getClassIntMap().get(this.f.getClazz()));
			this.code.write1Byte(0x59);
			this.code.write1Byte(0xb7);
			this.code.write2Byte(this.method.getClazz().getClassIntMap().get(this.f.getClazz())+1);
		}
		this.code.writeAll(this.getCodeForStore(this.fieldsMap.get(this.f)));
		return this.code;
	}
	
	public ByteWriter getCodeForFieldFromParsedClass() {
		Class c = this.method.getClazz();
		if(this.f.getClazz().getName().equals(c.getName())){
			this.code.write1Byte(this.operations.getALOADbyNumber(0));
		}
		/*sdad
		if()
			Field f = method.findFieldInsideMethoAndClassAndScope(lange);
		this.fieldRef.setSize(f.getValue());
		int mapPostition = method.getFieldMap().get(f);
		if((mapPostition>=0) && (mapPostition<=3)){
			this.code.write1Byte(this.operations.getILOADbyNumber(mapPostition));
		}else{
			this.code.write1Byte(this.operations.ILOAD);
			this.code.write1Byte(mapPostition);
		}*/
		this.code.write1Byte(0xbb);
		this.code.write2Byte(this.method.getClazz().getClassIntMap().get(this.f.getType().getClazz()));
		this.code.write1Byte(0x59);
		this.code.write1Byte(0xb7);
		this.code.write2Byte(this.method.getClazz().getClassIntMap().get(this.f.getType().getClazz())+1);
		this.code.write1Byte(this.operations.PUTFIELD);
		this.code.write2Byte(this.method.getClazz().getFieldIntMap().get(this.f));
		return this.code;
	}

	@Override
	public ByteWriter getExpressionCode() {
		// TODO Auto-generated method stub
		return this.code;
	}
	
	private ByteWriter getCodeForStore(int i){
		ByteWriter b = new ByteWriter();
		if((i>=0) && (i<=3)){
			b.write1Byte(this.operations.getASTROEbyNumber(i));
		}else{
			b.write1Byte(this.operations.ASTORE);
			b.write1Byte(i);
		}
		return b;
	}

	public void makeCodeForFieldRef(Token classField) {
		Class c = this.method.getClazz();
		if(c.isAllreadyContainingField(classField.getText())){
			Field fl = c.getFieldFromFieldRef(classField.getText());
			this.code.write1Byte(this.operations.getALOADbyNumber(0));
			this.code.write1Byte(this.operations.GETFIELD);
			this.code.write2Byte(this.method.getClazz().getFieldIntMap().get(fl));
			this.code.write1Byte(0xbb);
			this.code.write2Byte(this.method.getClazz().getClassIntMap().get(this.f.getType().getClazz()));
			this.code.write1Byte(0x59);
			this.code.write1Byte(0xb7);
			this.code.write2Byte(this.method.getClazz().getClassIntMap().get(this.f.getType().getClazz())+1);
			this.code.write1Byte(this.operations.PUTFIELD);
			this.code.write2Byte(this.method.getClazz().getFieldIntMap().get(this.f));
		}else{
			//Class cl = c.getClassReferenceByName(f.getType().getClazz().getName());
			Class cl = c.getFromClassReferenceByName(f.getType().getClazz().getName());
			int num = this.method.getClazz().getClassIntMap().get(cl);
			Field fl = this.method.getFieldByName(classField.getText());
			int pos = this.fieldsMap.get(fl);
			if(pos < 4){
				this.code.write1Byte(this.operations.getALOADbyNumber(pos));
			}else{
				this.code.write1Byte(this.operations.ALOAD);
				this.code.write1Byte(pos);
			}
			this.code.write1Byte(0xbb);
			this.code.write2Byte(num);
			this.code.write1Byte(0x59);
			this.code.write1Byte(0xb7);
			this.code.write2Byte(this.method.getClazz().getClassIntMap().get(cl)+1);
			this.code.write1Byte(this.operations.PUTFIELD);
			this.code.write2Byte(this.method.getClazz().getFieldIntMap().get(this.f));
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
