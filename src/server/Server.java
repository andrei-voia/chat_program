package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Server {
	
	private static DatagramSocket socket;
	private static boolean running;
	private static int clientID = 0;
	private static List<ClientInfo> clients = new ArrayList<ClientInfo>();

	
	public static void start(int port)
	{
		try {
			socket = new DatagramSocket(port);
			running = true;
			
			System.out.println("server started on port " + port);
			
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	
	private static void broadcast(String message)
	{
		for(ClientInfo info: clients)
		{
			send(message, info.getAddress(), info.getPort());
		}
		
	}
	
	
	private static void send(String message, InetAddress address, int port)
	{
		try 
		{
			message += "\\e";
			byte[] data = message.getBytes();
			DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
			socket.send(packet);
			
			System.out.println("Send message to: " + address.getHostAddress() + ":" + port);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//private
	public static void listen()
	{
		Thread listenThread = new Thread("ChatProgram Listener") 
		{
			public void run()
			{
	
				try 
				{
					while(running)
					{
						byte[] data = new byte[1024];
						DatagramPacket packet = new DatagramPacket(data, data.length);
							
						socket.receive(packet);
						
						String message = new String(data);
						message = message.substring(0, message.indexOf("\\e"));
						
						//manage message here
						if(!isCommand(message, packet))
						{
							broadcast(message);
						}
			
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		
		listenThread.start();
	}
	
	
	/*
	 * SERVER COMMAND LIST
	 * \con:[name] -> connects client to server
	 * \dis:[ID] -> disconnects client from server
	 */
	private static boolean isCommand(String message, DatagramPacket packet)
	{
		//connects client to server
		if(message.startsWith("\\con:"))
		{
			//run connection code
			String name = message.substring(message.indexOf(":") + 1);
			
			clients.add(new ClientInfo(name, clientID++, packet.getAddress(), packet.getPort()));
			broadcast("User " + name + ", Connected!");
			
			return true;
		}
		
		return false;
	}
	
	
	public static void stop()
	{
		running = false;
	}
}
