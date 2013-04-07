package tokens;

import java.text.MessageFormat;


public class Token
{
	
	private final int token;
	
	private final String text;

	public Token(int token, String text)
	{
		this.token = token;
		this.text = text;
	}

	public Token(int token)
	{
		this.token = token;
		this.text = "";
	}

	public int getToken()
	{
		return this.token;
	}

	public String getText()
	{
		return this.text;
	}

	public String toString()
	{
		Tokens t = new Tokens();
		MessageFormat messageFormat = new MessageFormat("");
		if(this.getText()==null){
			String[] args = new String[1];
			args[0] = t.tokenInt2String(this.getToken());
			return messageFormat.format("Token<{0}>", args);
		}else{
			String[] args = new String[2];
			args[0] = t.tokenInt2String(this.getToken());
			args[1] = this.getText();
			return messageFormat.format("Token<{0}, {1}>", args);
		}
	}
}
