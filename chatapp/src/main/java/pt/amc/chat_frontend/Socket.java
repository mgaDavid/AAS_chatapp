package pt.amc.chat_frontend;

import java.net.*;
import java.util.*;

import javax.crypto.*;
import javax.crypto.spec.*;

public class Socket extends Thread {
	InetAddress Sender;					// IP Address object for "Sender"
	InetAddress Destination;			// IP Address object for "Destination", or "Recipient"
	DatagramSocket DatagramSocket;		// DatagramSocket, which is a UDP Socket
	byte[] Buffer = new byte[1024];		// Local buffer, for holding the incoming datagram
	Integer PORT;						// Port for the Socket

	private Boolean Destroy = false;
	
	// Password for Cipher DES
	private static final String LOCAL_PASSWORD = "ea6bdcc2c378";
	private static SecretKey LOCAL_KEY;
	// Password for Cipher DES

	Set<String> Addresses_to_BypassEncryption;
	
	Boolean EncryptDES = false;

	Login login;
	Chat chat;
	
	public Socket(Integer PORT) {		// Constructor, receiving "PORT" as an argument 
		this.PORT = PORT;
		Addresses_to_BypassEncryption = new HashSet<String>();
		try {
			this.setSecretKey(LOCAL_PASSWORD);
		} catch(Exception e) {
			System.out.println(e.getCause().getMessage());
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public Socket() {					// Another version for the constructor, considering NO arguments
	}
	
	public void run() {
		try {											  
			DatagramSocket = null;
			if(PORT != null) {                             // Initializing DatagramSocket to bound a UDP Socket
				DatagramSocket = new DatagramSocket(PORT); // #1: It uses PORT variable to define the Socket PORT
			}                                              // #2: Accepts datagrams from all IPv4 addresses on this computer
			if(PORT == null) {                             // Initializing DatagramSocket WITHOUT PORT
				DatagramSocket = new DatagramSocket(); 	   // #1: It uses any available PORT to send the request.
			}                                              // #2: Accepts datagrams from all IPv4 addresses on this computer
		} catch(Exception e){}
		while(Destroy == false) {
			receiveDatagramPacket();
		}
	}
	
	public void receiveDatagramPacket(){
		try {
			DatagramPacket DatagramPacket = new DatagramPacket(Buffer, Buffer.length);
			DatagramSocket.receive(DatagramPacket);
			Sender = DatagramPacket.getAddress();
			String Sender_IP = Sender.toString().substring(1);
			Integer Port = DatagramPacket.getPort();
			if(this.getType().equalsIgnoreCase("LOGIN")) {
				//String Message = new String(DatagramPacket.getData());
				String Message = new String(DatagramPacket.getData(), 0, DatagramPacket.getLength());
				login.handleRequest(Sender_IP, Message);
			}
			else if(this.getType().equalsIgnoreCase("CHAT")) {
				String Message = null;
				if(this.bypassEncryption(Sender_IP, Port)) {
					//Message = new String(DatagramPacket.getData());
					Message = new String(DatagramPacket.getData(), 0, DatagramPacket.getLength());
				} else {
					byte[] decrypted = this.decryptDES(DatagramPacket.getData());
					Message = new String(decrypted);
				}
				chat.handleRequest(Sender_IP, Message);
			}
		} catch (Exception e){
			System.out.println("Exception! receiveDatagramPacket (" + this.getType() + "-" + PORT + ")");
			e.printStackTrace();
		}
	}
	
	public void sendDatagramPacket(int Port, String Message, String Recipient){
		try{
			byte[] MessageBytes = Message.getBytes();
			Destination = InetAddress.getByName(Recipient);
			if(this.getType().equalsIgnoreCase("LOGIN")) {
				// DO NOTHING
			}
			else if(this.getType().equalsIgnoreCase("CHAT")) {
				if(this.bypassEncryption(Recipient, Port)) {
					// DO NOTHING
				} else {
					// WORKAROUND!
					// IN ORDER TO INITIALIZE DatagramPacket ON receiveDatagramPacket(), WE NEED TO INITIALIZE AN ARRAY BUFFER
					// THE THING IS, WHEN DEALING WITH ENCRYPTION, ANY NOISE CAN PREVENT AN SUCESSFUL DECRYPTION
					// SPECIFICALLY, THE BadPaddingException EXCEPTION, OR INVALID BYTE LENGTH
					// TO MAKE THIS WORK, WE INCREMENT THE REMAINING BYTES IF NEEDED.
					byte[] clearBytes = null; 
					if(Buffer.length - MessageBytes.length > 0) {
						clearBytes = new byte[Buffer.length - MessageBytes.length];
					} else {
						clearBytes = new byte[0];
					}
					byte[] finalArray = new byte[MessageBytes.length + clearBytes.length];
					// USING System.arraycopy() TO INCREMENT THE BYTES AFTER THE MessageBytes END
					System.arraycopy(MessageBytes, 0, finalArray, 0, MessageBytes.length);
					System.arraycopy(clearBytes, 0, finalArray, MessageBytes.length, clearBytes.length);
					// USING System.arraycopy() TO INCREMENT THE BYTES AFTER THE MessageBytes END
					// WE CAN ENCRYPT IT NOW!
					MessageBytes = this.encryptDES(finalArray);
				}
			}
			DatagramPacket DatagramPacket = new DatagramPacket(MessageBytes, MessageBytes.length, Destination, Port);
			DatagramSocket.send(DatagramPacket);
		}catch(Exception e){
			System.out.println("Exception! sendDatagramPacket (" + this.getType() + "-" + PORT + ")");
			e.printStackTrace();
		}
	}
	
	public void destroy() {
		this.Destroy = true;
	}
	
	public Boolean bypassEncryption(String IP, Integer Port) {
		return (this.EncryptDES == false) || (this.Addresses_to_BypassEncryption.contains(IP + ":" + String.valueOf(Port)));
	}	
	
	public String getType() {
		if(login != null) {
			return "LOGIN";
		}
		else if(chat != null) {
			return "CHAT";
		}
		return "IDLE";
	}

	// DES Encryption
	private void setSecretKey(String Key) throws Exception {
		byte key[] = Key.getBytes("UTF8");
		DESKeySpec desKeySpec = new DESKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
		LOCAL_KEY = secretKey;
	}
	// DES Encryption
	private byte[] encryptDES(byte[] message) throws Exception {
		Cipher desCipher = Cipher.getInstance("DES/ECB/NoPadding");
		desCipher.init(Cipher.ENCRYPT_MODE, LOCAL_KEY);
		byte[] encodedSTR = desCipher.doFinal(message);
		return encodedSTR;
	}
	// DES Encryption
	private byte[] decryptDES(byte[] message) throws Exception {
		Cipher desCipher = Cipher.getInstance("DES/ECB/NoPadding");
		desCipher.init(Cipher.DECRYPT_MODE, LOCAL_KEY);
		byte[] decodedSTR = desCipher.doFinal(message);
		return decodedSTR;
	}
	// DES Encryption
}
