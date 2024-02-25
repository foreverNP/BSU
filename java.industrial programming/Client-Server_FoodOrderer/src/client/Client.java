package client;

import java.io.*;
import java.util.*;
import java.net.*;

import message.*;
import protocol.*;
import message.*;
import order.*;
import menu.*;

import static java.lang.Integer.parseInt;

public class Client
{

	public static Menu currentMenu;

	// main command line arguments: firstName, secondName, lastName, address, phoneNumber, [host]
	public static void main(String[] args)
	{
		try
		{
			if (args.length < 5 || args.length > 6)
			{
				throw new Exception("Invalid syntax of arguments.");
			}
			else
			{
				System.err.println("Connection...");
				Socket socket;
				if (args.length == 5)
				{
					socket = new Socket(InetAddress.getLocalHost(), Protocol.DEFAULT_PORT);
				}
				else
				{
					socket = new Socket(args[5], Protocol.DEFAULT_PORT);
				}
				connect(socket, args[0], args[1], args[2], args[3], args[4]);
			}
		}
		catch (Exception error)
		{
			System.err.println("Oops! " + error);
		}
	}

	// Session data
	static class Session
	{

		boolean isConnected 	= false;
		String firstName 		= null;
		String secondName 		= null;
		String lastName			= null;
		String address			= null;
		String phoneNumber 		= null;

		Session (String firstName, String secondName, String lastName, String address, String phoneNumber)
		{
			this.firstName = firstName;
			this.secondName = secondName;
			this.lastName = lastName;
			this.address = address;
			this.phoneNumber = phoneNumber;
		}

	}

	static void connect (Socket socket, String firstName, String secondName,
						 String lastName, String address, String phoneNumber)
	{
		try
				(
						Scanner input = new Scanner(System.in);
						ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
						ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())
				)
		{
			Session session = new Session(firstName, secondName, lastName, address, phoneNumber);

			if (startSession(session, inputStream, outputStream, input))
			{
				try
				{
					while (true) {
						Message message = getCommand(session, input);
						if (!processCommand(session, message, inputStream, outputStream))
						{
							break;
						}
					}
				}
				catch (Exception error)
				{
					System.err.println("Oops! " + error);
				}
				finally
				{
					finishSession(session, outputStream);
				}
			}
		}
		catch (Exception error)
		{
			System.err.println("Oops! " + error);
		}
	}

	// Finishing the session
	private static void finishSession(Session session, ObjectOutputStream outputStream)
			throws IOException
	{
		if (session.isConnected)
		{
			session.isConnected = false;
			outputStream.writeObject(new MessageDisconnect());
		}
	}

	// Starting the session
	private static boolean startSession(Session session, ObjectInputStream inputStream,
										ObjectOutputStream outputStream, Scanner scanner)
			throws IOException, ClassNotFoundException
	{
		outputStream.writeObject(new MessageConnect(session.firstName, session.secondName, session.lastName,
				session.address, session.phoneNumber));
		MessageConnectResult message = (MessageConnectResult) inputStream.readObject();
		if (!message.Error()) {
			System.err.println("Connected.");
			session.isConnected = true;
			return true;
		}
		System.err.println("Unable to connect: " + message.getErrorMessage());
		System.err.println("Press <Enter> to continue...");
		if(scanner.hasNextLine())
		{
			scanner.nextLine();
		}
		return false;
	}

	// Get command
	static Message getCommand (Session session, Scanner input)
	{
		while (true) {
			printPrompt();
			if (input.hasNextLine()== false)
			{
				break;
			}
			String string = input.nextLine();

			byte cmd = translateCmd(string);
			switch (cmd) {
				case -1:
					return null;
				case Protocol.REQUEST_GET_MENU:
					return new MessageGetMenu();
				case Protocol.REQUEST_SEND_ORDER:
					return inputOrder(input, session, currentMenu);
				case 0:
					continue;
				default:
					System.err.println("Unknow command!");
					continue;
			}
		}
		return null;
	}

	static MessageSendOrder inputOrder(Scanner input, Session session, Menu menu)
	{
		if (menu == null)
		{
			System.out.println("Oops! It seems like you don't know our menu!");
			return null;
		}
		else
		{
			Order currentOrder = new Order(session.address);
			System.out.println("To continue order enter position data, to finish it - \"finish\".");
			int id, count;
			String buffer;
			while (true)
			{
				System.out.print("Please, enter dish's ID to order: ");
				buffer = input.nextLine();
				if (buffer.equals("finish"))
				{
					break;
				}
				id = parseInt(buffer);
				System.out.print("Please, enter dish's count to order: ");
				buffer = input.nextLine();
				if (buffer.equals("finish"))
				{
					break;
				}
				count = parseInt(buffer);
				if (id < 1 || id > 6 || count < 0)
				{
					System.out.println("Oops! Something wrong, please, try this position again. ");
				}
				else
				{
					currentOrder.addDish(menu.getDishByNumberInMenu(id), count);
					System.out.println(currentOrder.toString());
				}

			}

			return new MessageSendOrder(currentOrder);
		}
	}

	// Command translator
	static TreeMap<String,Byte> commands = new TreeMap<String,Byte>();
	static
	{
		commands.put("q", (byte) -1);
		commands.put("quit", (byte) -1);
		commands.put("m", Protocol.REQUEST_GET_MENU);
		commands.put("menu", Protocol.REQUEST_GET_MENU);
		commands.put("o", Protocol.REQUEST_SEND_ORDER);
		commands.put("order", Protocol.REQUEST_SEND_ORDER);
	}

	// Translate command line to byte value
	static byte translateCmd(String line) {
		line = line.trim();
		Byte conversion = commands.get(line);
		if (conversion == null)
		{
			return 0;
		}
		else
		{
			return conversion.byteValue();
		}
	}

	// Print prompt of commands
	static void printPrompt()
	{
		System.out.println();
		System.out.print("(q)uit/(m)enu/(o)rder >");
		System.out.flush();
	}

	// Processing commands
	static boolean processCommand(Session session, Message message,
								  ObjectInputStream inputStream, ObjectOutputStream outputStream)
			throws IOException, ClassNotFoundException
	{
		if (message != null)
		{
			outputStream.writeObject(message);
			MessageResult result = (MessageResult) inputStream.readObject();
			if (result.Error())
			{
				System.err.println(result.getErrorMessage());
			}
			else
			{
				switch (result.getID())
				{
					case Protocol.REQUEST_GET_MENU:
						getMenuAction((MessageGetMenuResult)result);
						break;
					case Protocol.REQUEST_SEND_ORDER:
						System.err.println("Order's in process...");
						break;
					default:
						assert(false);
						break;
				}
			}
			return true;
		}
		return false;
	}

	static void getMenuAction(MessageGetMenuResult result)
	{
		if (result.menu != null)
		{
			System.out.println(result.menu.toString());
			currentMenu = result.menu;
		}
		else
		{
			System.out.println("No menu...");
		}
	}
}