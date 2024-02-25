package message;
import protocol.*;

import java.io.*;

// Client disconnect message, extends from super-class Message
public class MessageDisconnect
extends Message implements Serializable
{
		
	private static final long serialVersionUID = 1L;
	
	public MessageDisconnect()
	{
		super(Protocol.REQUEST_DISCONNECT);
	}
	
}
