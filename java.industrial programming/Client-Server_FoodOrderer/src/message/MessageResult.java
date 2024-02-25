package message;
import protocol.*;
import java.io.*;

public class MessageResult
extends Message implements Serializable
{

	private static final long serialVersionUID = 1L;
	
	private int errorCode;
	public int getErrorCode()
	{
		return errorCode;
	}
	
	private String errorMessage;
	public String getErrorMessage()
	{
		return errorMessage;
	}
	
	public boolean Error()
	{
		return errorCode != Protocol.RESULT_CODE_OK;
	}
	
	protected MessageResult()
	{
		super();
	}
	
	protected MessageResult(byte id, String errorMessage)
	{		
		super(id);
		this.errorCode = Protocol.RESULT_CODE_ERROR;
		this.errorMessage = errorMessage;
	}

	protected MessageResult(byte id)
	{
		super( id );
		this.errorCode = Protocol.RESULT_CODE_OK;
		this.errorMessage = "";
	}
}