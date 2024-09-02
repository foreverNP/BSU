package message;
import protocol.*;
import java.io.*;

// Client get menu message, extends from super-class Message
public class MessageGetMenu 
extends Message implements Serializable
{
	private static final long serialVersionUID = 1L;

	public MessageGetMenu() {
		super(Protocol.REQUEST_GET_MENU);
	}
	
}