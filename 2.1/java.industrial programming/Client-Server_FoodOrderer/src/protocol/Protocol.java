package protocol;

// Client Request ID
interface REQUEST
{
	static final byte REQUEST_CONNECT 		= 1;	 // Connect to Server
	static final byte REQUEST_DISCONNECT	= 2;	 // Disconnect from Server
	static final byte REQUEST_SEND_ORDER	= 3;	 // Send order from Client to Server
	static final byte REQUEST_GET_MENU 		= 4;	 // Receive menu from Server to Client
}

// Result Request ID
interface RESULT
{
	static final int RESULT_CODE_OK 		= 0;	 // OK
	static final int RESULT_CODE_ERROR 		= -1;	 // Error
}	

// Port ID
interface PORT
{
	public static final int DEFAULT_PORT 	= 8071;	 // Default port in case not marked by main's args
}

// Protocol
public class Protocol implements REQUEST, RESULT, PORT
{
	private static final byte REQUEST_MIN = REQUEST_CONNECT;
	private static final byte REQUEST_MAX = REQUEST_GET_MENU;
	
	// Protocol ID validator
	public static boolean validID (byte ID)
	{
		return (ID >= REQUEST_MIN && ID <= REQUEST_MAX); 
	}
}
