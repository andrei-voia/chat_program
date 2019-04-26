package client; 

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Client {
	
	private static DatagramSocket socket;
	private InetAddress address;
	private int port;
	private boolean running;
	
	
	public Client(String name, String address, int port)
	{
		try 
		{
			this.address = InetAddress.getByName(address);
			this.port = port;
			
			socket = new DatagramSocket();
			
			listen();
			running = true;
			send("\\con:" + name); 
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void send(String message)
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
	
	
	private void listen()
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
							//print message
							ClientWindow.printToConsole(message);
						}
			
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		
		listenThread.start();
	}
	
	
	private static boolean isCommand(String message, DatagramPacket packet)
	{
		//connects client to server
		if(message.startsWith("\\con:"))
		{
			//run connection code

			return true;
		}
		return false;
	}

}
