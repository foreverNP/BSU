package message;
import protocol.*;

import java.io.*;

public class MessageConnectResult 
extends MessageResult implements Serializable 
{
	private static final long serialVersionUID = 1L;
	
	public MessageConnectResult( String errorMessage )
	{
		super(Protocol.REQUEST_CONNECT, errorMessage);
	}
	
	public MessageConnectResult()
	{
		super(Protocol.REQUEST_CONNECT);
	}
}
