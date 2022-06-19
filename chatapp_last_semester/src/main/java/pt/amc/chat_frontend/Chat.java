package pt.amc.chat_frontend;

import java.util.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Chat extends Frame {
	TextField Component_Recipient = new TextField(30);
	TextField Component_Message = new TextField(30);
	TextArea Component_Display = new TextArea(10,30);
	Button Component_Send = new Button("Send");
	
	Map<String, Message> PendingRequests;
	
	String RegisterAgent_Address;
	Socket sock;
	Boolean ShowDebug;
	Boolean EncryptDES;
	
	String Nickname;
	Integer PIN;
	
	// CONSTRUCTOR FOR CHAT APPLICATION
	// IT REQUIRES THE NICKNAME, PIN, AND REGISTER AGENT ADDRESS IN ORDER TO WORK
	public Chat(String Nickname, Integer PIN, String RegisterAgent_Address) {
		super("Chat: " + Nickname);
		// SETTING LOCAL VARIABLES
		this.Nickname = Nickname;
		this.PIN = PIN;		
		this.RegisterAgent_Address = RegisterAgent_Address;
		PendingRequests = new HashMap<String, Message>();
		// SETTING LOCAL VARIABLES

		// SETTING SOCKET FOR DATAGRAM
		this.startSocket(PIN);
		// SETTING SOCKET FOR DATAGRAM

		// SETTING GUI
		this.setActionListeners();
		this.setGUI();
		// SETTING GUI
	}

	public void setGUI(){
		this.setSize(350, 300);
		this.GUI();
		this.setVisible(true);
	}

	public void startSocket(Integer Port){
		sock = new Socket(Port);
		sock.chat = this;
		sock.EncryptDES = this.EncryptDES;
		sock.Addresses_to_BypassEncryption.add(this.RegisterAgent_Address);
		sock.start();
	}
	
	public void setActionListeners() {
		this.Component_Send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent x){
				// IN ORDER TO SEND A MESSAGE, WE NEED TO QUERY THE NICKNAMES FIRST
				// STORE THE MESSAGE LOCALLY, QUERY, THEN SEND IF POSSIBLE
				String Id = UUID.randomUUID().toString();
				String Message = Component_Message.getText();
				String Recipient = Component_Recipient.getText();
				
				Message message = new Message();
				message.Id = Id;
				message.Message = Message;
				// STORE THE MESSAGE LOCALLY...
				PendingRequests.put(Id, message);
				
				// ...THEN QUERY THE AGENT
				Map<String, String> request = new HashMap<String, String>();
				request.put("Id", Id);
				request.put("APP", "FRONTEND");
				request.put("OP", "RESOLVE");
				request.put("NK", Recipient);
				
				sendRequest(RegisterAgent_Address, request.toString());
			}
		});
	}	
	
	public void handleRequest(String sender, String request) {
		printDebug("Received DATAGRAM from '" + sender + "', with PAYLOAD '" + request + "'");
		Map<String, String> req = this.formatRequest(request);
		switch(req.get("TYPE")) {
			case "RESOLVE":
				// THE "RESOLVE" TYPE TAKES A COUPLE OF PARAMETERS
				// "SUCCESS" KEY HAS AN COUNTER FOR HOW MANY ADDRESSES WERE FOUND FOR THE PROVIDED NICKNAMES
				Integer RESOLVE_Success = Integer.valueOf(req.get("SUCCESS"));
				// IF "SUCCESS" > 0, HANDLE SENDING THE CACHED MESSAGES
				if(RESOLVE_Success > 0) {
					// GET THE "ID" TO RETRIEVE CACHED MESSAGE
					String RESOLVE_Id = req.get("Id");
					Message RESOLVE_PendingMessage = PendingRequests.get(RESOLVE_Id);
					// GET THE "ID" TO RETRIEVE CACHED MESSAGE

					// PREPARE THE SEND PAYLOAD WITH NICKNAME AND MESSAGE
					Map<String, String> RESOLVE_SendPayload = new HashMap<String, String>();
					RESOLVE_SendPayload.put("TYPE", "MESSAGE");
					RESOLVE_SendPayload.put("NK", this.Nickname);
					RESOLVE_SendPayload.put("MESSAGE", RESOLVE_PendingMessage.Message);
					// PREPARE THE SEND PAYLOAD WITH NICKNAME AND MESSAGE

					// FOR EACH SUCCESS, SEND THE MESSAGE
					for(String key : req.keySet()) {
						if(key.contains("ITEM_ADDRESS") == true) {
							String ItemAddress = req.get(key);
							this.sendRequest(ItemAddress, RESOLVE_SendPayload.toString());
						}
					}
					// FOR EACH SUCCESS, SEND THE MESSAGE
				}
				break;
			case "MESSAGE":
					// FOR RECEIVED MESSAGES, APPEND TO THE DISPLAY COMPONENT
					String MESSAGE_NicknameSender = req.get("NK");
					String MESSAGE_Message = req.get("MESSAGE");
                                        String message = String.format(": %s\n", MESSAGE_Message);
                                        Component_Display.append(MESSAGE_NicknameSender);
					Component_Display.append(message);
					// FOR RECEIVED MESSAGES, APPEND TO THE DISPLAY COMPONENT
				break;
			default:
				break;
		}
	}

	public void sendRequest(String recipient, String payload) {
		// SEND REQUEST
		String IP = recipient.split(":")[0].trim();
		String Port = recipient.split(":")[1].trim();
		sock.sendDatagramPacket(Integer.valueOf(Port), payload, IP);
		// SEND REQUEST
	}	
	
	public void GUI(){
		// GUI SETUP USING AWT JAVA
		setBackground(Color.lightGray);
		Component_Display.setEditable(false);
		GridBagLayout GBL = new GridBagLayout();
		setLayout(GBL);
		Panel P1 = new Panel();
		P1.setLayout(new BorderLayout(5,5));
		P1.add("North",Component_Recipient);
		P1.add("West",Component_Message);
		P1.add("East",Component_Send);
		P1.add("South",Component_Display);
		GridBagConstraints P1C=new GridBagConstraints();
		P1C.gridwidth=GridBagConstraints.REMAINDER;
		GBL.setConstraints(P1,P1C);
		add(P1);
		// GUI SETUP USING AWT JAVA
	}
	
	public Map<String, String> formatRequest(String request){
		Map<String, String> response = new HashMap<String, String>();
		String[] splitted = request.replace("{", "").replace("}", "").split(", ");
		for(String item : splitted) {
			String[] keyval = item.split("=");
			response.put(keyval[0], keyval[1]);
		}
		return response;
	}	
	
	public void setDebug(Boolean a) {
		this.ShowDebug = a;
	}
	
	public void setEncrypt(Boolean a) {
		this.EncryptDES = a;
		if(sock != null) {
			sock.EncryptDES = this.EncryptDES;	
		}
		
	}	
	
	public void printDebug(String message) {
		if(this.ShowDebug == true) {
			System.out.println("DEBUG: " + message);	
		}
	}	
}