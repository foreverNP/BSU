package message;
import protocol.*;
import java.io.*;

//Client connect message, extends from super-class Message
public class MessageConnect
extends Message implements Serializable
{
	
	private static final long serialVersionUID = 1L;
	
	public String firstName;
	public String secondName;
	public String lastName;
	public String address;
	public String phoneNumber;
	
	public MessageConnect(String firstName, String secondName, String lastName, String address, String phoneNumber)
	{
		super(Protocol.REQUEST_CONNECT);
		this.firstName = firstName;
		this.secondName = secondName;
		this.lastName = lastName;
		this.address = address;
		this.phoneNumber = phoneNumber;
	}
	
}