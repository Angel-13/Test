package compileTable;

import mapsTable.ClassIntMap;
import mapsTable.FieldIntMap;
import mapsTable.IntStringMap;
import mapsTable.MethodIntMap;
import mapsTable.StringIntMap;
import symbolTable.Class;
import symbolTable.Field;
import symbolTable.Method;
import symbolTable.StringArrayList;

public class ConstantPool
{

	private final ByteWriter byteWriter;
	
	private final ClassIntMap classMap;
	
	private final MethodIntMap methodMap;
	
	private final StringIntMap utf8Map;
	
	private final StringIntMap stringMap;
	
	//private final IntStringMap utf8IntStringMap;
	
	private final FieldIntMap fieldMap;
	
	private final Class clazz;
	
	private final StringArrayList types;
	
	private int counter; 
	
	public ConstantPool(Class clazz){
		this.clazz = clazz;
		this.classMap = new ClassIntMap();
		this.methodMap = new MethodIntMap();
		this.utf8Map = new StringIntMap();
		this.fieldMap = new FieldIntMap();
		this.byteWriter = new ByteWriter();
		this.types = new StringArrayList();
		this.stringMap = new StringIntMap();
		//this.utf8IntStringMap = new IntStringMap();
		this.counter = 1;
	}
	
	public ByteWriter getByteWriter(){
		return this.byteWriter;
	}
	
	public ClassIntMap getClassMap(){
		return this.classMap;
	}
	
	public MethodIntMap getMethodMap(){
		return this.methodMap;
	}
	
	public StringIntMap getUtf8Map(){
		return this.utf8Map;
	}
	
	public StringIntMap getStringMap(){
		return this.stringMap;
	}
	/*public IntStringMap getUtf8IntStringMap(){
		return this.utf8IntStringMap;
	}*/
	
	/*public void addUtf8IntString(Integer integer, String name) {
		this.utf8IntStringMap.put(integer, name);
	}*/
	
	public void addStringMap(String str) {
		this.stringMap.put(str, this.counter);
		this.counter++;
	}
	
	public void addUtf8(String str) {
		this.utf8Map.put(str, this.counter);
		this.counter++;
	}
	
	public void addClassMap1(Class clazz){
		this.classMap.put(clazz, this.counter);
		this.counter = this.counter + 2;
	}
	
	public void addClassMap(Class clazz){
		this.classMap.put(clazz, this.counter);
		this.counter++;
	}
	
	public void addMethodMap(Method m){
		this.methodMap.put(m, this.counter);
		this.counter++;
	}
	
	public void addField(Field field){
		this.fieldMap.put(field, this.counter);
		this.counter++;
	}
	
	public int getSize(){
		return this.counter;
	}
	public FieldIntMap getFieldMap(){
		return this.fieldMap;
	}
	
	public int getCounter(){
		return this.counter;
	}

	public void makeByteWriter() {
		int k = 1;
		StringArrayList alreadyAddedNamesInUtf = new StringArrayList();
		while(this.clazz.isNumberMapped(k)){
			if(this.clazz.isNumberMappedToField(k)){
				Field f = this.clazz.getFieldMappedToValue(k);
				this.byteWriter.write1Byte(0x09);
				this.byteWriter.write2Byte(this.classMap.get(f.getClazz()));
				this.byteWriter.write2Byte(this.utf8Map.get("NameAndType:"+ f.getName()));
			}else if(this.clazz.isNumberMappedToClass(k)){
				Class c = this.clazz.getClassMappedToValue(k);
				this.byteWriter.write1Byte(0x07);
				this.byteWriter.write2Byte(this.utf8Map.get(c.getName()));
				if(this.clazz.isAllreadyContainingClassReferenceBotNotStatic(c.getName())){
					Method m = this.clazz.getMethodMappedToValue(1);
					this.byteWriter.write1Byte(0x0a);
					this.byteWriter.write2Byte(this.classMap.get(c));
					this.byteWriter.write2Byte(this.utf8Map.get("NameAndType:"+ m.getName()+m.getParametersDescriptor()));
					k++;
				}
			}else if(this.clazz.isNumberMappedToMethod(k)){
				
				//System.out.println(k);
				Method m = this.clazz.getMethodMappedToValue(k);
				//System.out.println(m .getName() +  "  CP LINE 141  TUKAA   "   + this.classMap.get(m.getClazz()));
				this.byteWriter.write1Byte(0x0a);
				//System.out.println(m.getName() + "  " + m.getParametersDescriptor() + "  " + this.utf8Map.get("NameAndType:"+ m.getName()+m.getParametersDescriptor()));
				this.byteWriter.write2Byte(this.classMap.get(m.getClazz()));
				//System.out.println(this.classMap.get(m.getClazz()) + "     number" + m.getClazz().getName());
				this.byteWriter.write2Byte(this.utf8Map.get("NameAndType:"+ m.getName()+m.getParametersDescriptor()));
				//System.out.println(this.utf8Map.get("NameAndType:"+ m.getName()+m.getParametersDescriptor()) + "     number init");
				
			}else{
				String str = this.clazz.getStringdMappedToValue(k);
				this.byteWriter.write1Byte(0x08);
				this.byteWriter.write2Byte(this.utf8Map.get(str));
			}
			k++;
		}
		
		StringArrayList allreadyAddedCLasses = new StringArrayList();
		//if(!this.classMap.containsKey(this.clazz)){
		if(!this.clazz.isAllreadyContainingClassReference(this.clazz.getName())){
			this.byteWriter.write1Byte(0x07);
			this.byteWriter.write2Byte(this.utf8Map.get(this.clazz.getName()));
		}
		allreadyAddedCLasses.add(this.clazz.getName());
		this.byteWriter.write1Byte(0x07);
		this.byteWriter.write2Byte(this.utf8Map.get(this.clazz.getSuperClass().getName()));
		allreadyAddedCLasses.add(this.clazz.getSuperClass().getName());
		
		this.writeUtfFields(alreadyAddedNamesInUtf);
		this.writeUtfMethods(alreadyAddedNamesInUtf);
		
		this.byteWriter.write1Byte(0x01);
		this.byteWriter.write2Byte("Code".length());
		this.writeUtfStrings("Code");
		
		if(this.utf8Map.containsKey("StackMapTable")){
			this.byteWriter.write1Byte(0x01);
			this.byteWriter.write2Byte("StackMapTable".length());
			this.writeUtfStrings("StackMapTable");
		}
		
		this.byteWriter.write1Byte(0x01);
		this.byteWriter.write2Byte("SourceFile".length());
		this.writeUtfStrings("SourceFile");
		
		String sourcefile = this.clazz.getName() + ".java";
		this.byteWriter.write1Byte(0x01);
		this.byteWriter.write2Byte(sourcefile.length());
		this.writeUtfStrings(sourcefile);
		
		for(int i = 0;i <this.clazz.getClassReferences().size(); i++){
			Class c = this.clazz.getClassReferences().get(i);
			if(!c.getName().equals(this.clazz.getName())){
				this.byteWriter.write1Byte(0x01);
				this.byteWriter.write2Byte(this.clazz.getClassReferences().get(i).getName().length());
				this.writeUtfStrings(this.clazz.getClassReferences().get(i).getName());
			}
			if(!alreadyAddedNamesInUtf.contains(c.getName()) && !c.getName().equals(this.clazz.getName())){
				alreadyAddedNamesInUtf.add(c.getName());
			}
			if(!allreadyAddedCLasses.contains(c.getName())){
				allreadyAddedCLasses.add(c.getName());
			}
			
		}
		
		for(int i = 0; i < this.clazz.getFieldReferences().size(); i++){
			
			Field fl = this.clazz.getFieldReferences().get(i);
			
			//System.out.println(fl.getName() + "   " + fl.getClazz().getName());
			/*if(!allreadyAddedCLasses.contains(fl.getClazz().getName())){
				System.out.println(" TUKAAA ");
				System.out.println(fl.getClazz().getName());
				//System.out.println(fl.getClazz().getName());
				allreadyAddedCLasses.add(fl.getClazz().getName());
				this.byteWriter.write1Byte(0x07);
				this.byteWriter.write2Byte(this.utf8Map.get(fl.getClazz().getName()));
				
			}*/
			if(!this.classMap.containsKey(fl.getClazz())){
				allreadyAddedCLasses.add(fl.getClazz().getName());
				this.byteWriter.write1Byte(0x07);
				this.byteWriter.write2Byte(this.utf8Map.get(fl.getClazz().getName()));
			}

			
			this.byteWriter.write1Byte(0x0c);
			this.byteWriter.write2Byte(this.utf8Map.get(fl.getName()));
			this.byteWriter.write2Byte(this.utf8Map.get(fl.getType().getDescriptor()));

			
		}

		for(int i = 0; i < this.clazz.getMethodReferences().size(); i++){
			Method m = this.clazz.getMethodReferences().get(i);
			if(!this.classMap.containsKey(m.getClazz())){
				allreadyAddedCLasses.add(m.getClazz().getName());
				this.byteWriter.write1Byte(0x07);
				this.byteWriter.write2Byte(this.utf8Map.get(m.getClazz().getName()));
			}
			this.byteWriter.write1Byte(0x0c);
			this.byteWriter.write2Byte(this.utf8Map.get(m.getName()));
			String s = this.parameterDescriptor(m);
			this.byteWriter.write2Byte(this.utf8Map.get(s));
			
			/*System.out.println( m.getName() + "    HHHHHHHHHHHHHHHHHHHHHHHH ");
			System.out.println( this.utf8Map.get(m.getName()) + "    " + m.getName() + "    JJJJJJJJJ ");
			System.out.println( this.utf8Map.get(s) + "    " + s + "    JJJJJJJJJ ");*/
		}
		
		for(int i = 0; i < this.clazz.getStringReferences().size(); i++){
			String str = this.clazz.getStringReferences().get(i);
			this.byteWriter.write1Byte(0x01);
			this.byteWriter.write2Byte(str.length());
			this.writeUtfStrings(str);
			alreadyAddedNamesInUtf.add(str);
		}
		
		this.byteWriter.write1Byte(0x01);
		this.byteWriter.write2Byte(this.clazz.getName().length());
		this.writeUtfStrings(this.clazz.getName());
		alreadyAddedNamesInUtf.add(this.clazz.getName());
		
		this.byteWriter.write1Byte(0x01);
		this.byteWriter.write2Byte(this.clazz.getSuperClass().getName().length());
		this.writeUtfStrings(this.clazz.getSuperClass().getName());
		alreadyAddedNamesInUtf.add(this.clazz.getSuperClass().getName());
		
		int j = 1;
		while(this.clazz.isNumberMapped(j)){
			if(this.clazz.isNumberMappedToField(j)){
				
				Field f = this.clazz.getFieldMappedToValue(j);
				Class fieldClass = f.getClazz();
				
				if(!alreadyAddedNamesInUtf.contains(fieldClass.getName())){
					alreadyAddedNamesInUtf.add(fieldClass.getName());
					this.byteWriter.write1Byte(0x01);
					this.byteWriter.write2Byte(fieldClass.getName().length());
					this.writeUtfStrings(fieldClass.getName());
				}
				if(!alreadyAddedNamesInUtf.contains(f.getName())){
					alreadyAddedNamesInUtf.add(f.getName());
					this.byteWriter.write1Byte(0x01);
					this.byteWriter.write2Byte(f.getName().length());
					this.writeUtfStrings(f.getName());
				}
				if(!alreadyAddedNamesInUtf.contains(f.getType().getDescriptor())){
					alreadyAddedNamesInUtf.add(f.getType().getDescriptor());
					this.byteWriter.write1Byte(0x01);
					this.byteWriter.write2Byte(f.getType().getDescriptor().length());
					this.writeUtfStrings(f.getType().getDescriptor());
				}
			}else if(this.clazz.isNumberMappedToClass(j)){
				Class c = this.clazz.getClassMappedToValue(j);
				
				if(!alreadyAddedNamesInUtf.contains(c.getName())){
					
					alreadyAddedNamesInUtf.add(c.getName());
					this.byteWriter.write1Byte(0x01);
					this.byteWriter.write2Byte(c.getName().length());
					this.writeUtfStrings(c.getName());
				}
				if(this.clazz.isAllreadyContainingClassReferenceBotNotStatic(c.getName())){
					j++;
				}
			}else if(this.clazz.isNumberMappedToMethod(j)){
				Method m = this.clazz.getMethodMappedToValue(j);
				Class fieldClass = m.getClazz();
				
				if(!alreadyAddedNamesInUtf.contains(fieldClass.getName())){
					alreadyAddedNamesInUtf.add(fieldClass.getName());
					this.byteWriter.write1Byte(0x01);
					this.byteWriter.write2Byte(fieldClass.getName().length());
					this.writeUtfStrings(fieldClass.getName());
				}
				if(!alreadyAddedNamesInUtf.contains(m.getName())){
					alreadyAddedNamesInUtf.add(m.getName());
					this.byteWriter.write1Byte(0x01);
					this.byteWriter.write2Byte(m.getName().length());
					this.writeUtfStrings(m.getName());
				}
				String s = this.parameterDescriptor(m);
				if(!alreadyAddedNamesInUtf.contains(s)){
					alreadyAddedNamesInUtf.add(s);
					this.byteWriter.write1Byte(0x01);
					this.byteWriter.write2Byte(s.length());
					this.writeUtfStrings(s);
				}
			}else{
				String str = this.clazz.getStringdMappedToValue(j);
				if(!alreadyAddedNamesInUtf.contains(str)){
					alreadyAddedNamesInUtf.add(str);
					this.byteWriter.write1Byte(0x01);
					this.byteWriter.write2Byte(str.length());
					this.writeUtfStrings(str);
				}
			}
			j++;
		}
		
	}
	
	private void writeUtfFields(StringArrayList strList){
		for(int i = 0; i < this.clazz.getFields().size(); i++){
			Field f = this.clazz.getFields().get(i);
			this.byteWriter.write1Byte(0x01);
			this.byteWriter.write2Byte(f.getName().length());
			strList.add(f.getName());
			this.writeUtfStrings(f.getName());
			
			if(!this.types.contains(f.getType().getDescriptor())){
				this.types.add(f.getType().getDescriptor());
				this.byteWriter.write1Byte(0x01);
				this.byteWriter.write2Byte(f.getType().getDescriptor().length());
				this.writeUtfStrings(f.getType().getDescriptor());
				strList.add(f.getType().getDescriptor());
			}
		}
	}
	
	private void writeUtfMethods(StringArrayList strList) {
		for(int i = 0; i < this.clazz.getMethods().size(); i++){
			Method m = this.clazz.getMethods().get(i);
			this.byteWriter.write1Byte(0x01);
			this.byteWriter.write2Byte(m.getName().length());
			this.writeUtfStrings(m.getName());
			strList.add(m.getName());
			this.writeUtfMethodParameters(m, strList);
		}
	}

	private void writeUtfMethodParameters(Method method, StringArrayList strList) {
		String str = "(";
		for(int i = 0; i < method.getParameterList().getSize(); i++){
			str = str + method.getParameterList().getParameter(i).getType().getDescriptor();
		}
		str = str + ")" + method.getRetrunType().getDescriptor();
		if(!strList.contains(str)){
			this.byteWriter.write1Byte(0x01);
			this.byteWriter.write2Byte(str.length());
			this.writeUtfStrings(str);
			strList.add(str);
		}
	}
	
	private String parameterDescriptor(Method method) {
		String str = "(";
		for(int i = 0; i < method.getParameterList().getSize(); i++){
			str = str + method.getParameterList().getParameter(i).getType().getDescriptor();
		}
		str = str + ")" + method.getRetrunType().getDescriptor();
		return str;
	}
	
	private void writeUtfStrings(String str){
		for(int i = 0; i < str.length(); i++){
			this.byteWriter.write1Byte(str.charAt(i));
		}
	}
	
	public void printConstantPool(){
		this.byteWriter.printByteArray();
	}
	
}
