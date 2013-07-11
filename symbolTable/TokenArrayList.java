package symbolTable;

import java.util.ArrayList;

import tokens.Token;

public class TokenArrayList {
	
	private ArrayList<Token> list = new ArrayList<Token>();

	public boolean add(Token e)
	{
		return this.list.add(e);
	}

	public Token get(int index)
	{
		return this.list.get(index);
	}
	
	public int getPositionOfToken(int token)
	{
		for(int i = 0; i < this.list.size(); i++){
			if(this.list.get(i).getToken() == token){
				return i;
			}
		}
		return -1;
	}
	
	public int size()
	{
		return this.list.size();
	}
	
	public void remove(int i){
		this.list.remove(i);
	}

	public String printTokens() {
		String str =  "";
		for(int i = 0; i < this.list.size(); i++){
			str = str + this.list.get(i).getText() + " ";
		}
		//System.out.println(str);
		return str;
	}
}
