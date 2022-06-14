package pt.amc.registeragent;

import java.net.*;

public class Socket extends Thread {
	InetAddress Sender;					// IP Address object for "Sender"
	InetAddress Destination;			// IP Address object for "Destination", or "Recipient"
	DatagramSocket DatagramSocket;		// DatagramSocket, which is a UDP Socket
	byte[] Buffer = new byte[1024];		// Local buffer, for holding the incoming datagram
	Integer PORT;						// Port for the Socket
	
	RegisterAgent RegisterAgent;				// NameServer Controller
	
	public Socket(Integer PORT, RegisterAgent RegisterAgent) {	// Constructor, receiving "PORT" as an argument
		this.RegisterAgent = RegisterAgent;
		this.PORT = PORT;
	}

	public void run() {
		try {											  // Initializing DatagramSocket to bound a UDP Socket
			DatagramSocket = new DatagramSocket(PORT);    // #1: It uses PORT variable to define the Socket PORT
		}												  // #2: Accepts datagrams from all IPv4 addresses on this computer
		catch(Exception e){}
		while(true) {
			receiveDatagramPacket();
		}
	}
	
	public void receiveDatagramPacket(){
		try {
			DatagramPacket DatagramPacket = new DatagramPacket(Buffer, Buffer.length);
			DatagramSocket.receive(DatagramPacket);
			Sender = DatagramPacket.getAddress();
			String Message = new String(DatagramPacket.getData(), 0, DatagramPacket.getLength());
			String Sender_Address = Sender.toString().substring(1) + ":" + DatagramPacket.getPort();
			RegisterAgent.handleRequest(Sender_Address, Message);
		} catch (Exception e){}
	}
	
	public void sendDatagramPacket(int Port, String Message, String Recipient){
		try{
			Destination = InetAddress.getByName(Recipient);
			DatagramPacket DatagramPacket = new DatagramPacket(Message.getBytes(), Message.getBytes().length, Destination, Port);
			DatagramSocket.send(DatagramPacket);
			
		}catch(Exception e){}
	}	

}
