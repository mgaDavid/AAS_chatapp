package pt.amc.chat_frontend;

import java.util.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Login extends Frame {
	
	Boolean ShowDebug = false;
	Boolean EncryptDES = false;
	
	Label Component_Title_Label = new Label("Welcome! To proceed, please login.");
	Label Component_Nickname_Label = new Label("Nickname: ");
	TextField Component_Nickname = new TextField(10);
	Label Component_PIN_Label = new Label("PIN: ");
	TextField Component_PIN = new TextField(10);
	Button Component_Login = new Button("Login");
	Button Component_Register = new Button("Register");
	Button Component_ForgotPIN = new Button("Forgot PIN");
	
	public Socket sock;
	
	public String RegisterAgent_Address;
	
	public Login() {
		super("Chat: Login");
		this.setActionListeners();
		this.setGUI();
		this.StartSocket();
	}

	public void StartSocket(){
		// CREATE A TEMPORARY SOCKET WITHOUT SETTING PORT, SO THE SOCKET BOUNDS TO ANY AVAILABLE PORT
		sock = new Socket();
		sock.login = this;
		sock.start();
		// CREATE A TEMPORARY SOCKET WITHOUT SETTING PORT, SO THE SOCKET BOUNDS TO ANY AVAILABLE PORT
	}

	public void setGUI(){
		this.setSize(320, 290);
		this.GUI();
	}
	
	public void handleRequest(String sender, String request) {
		printDebug("Received DATAGRAM from '" + sender + "', with PAYLOAD '" + request + "'");
		Map<String, String> req = this.formatRequest(request);
		switch(req.get("STATUS").trim()) {
			case "SUCCESS":
				// THE REGISTER AGENT ANSWERS "SUCCESS" WHEN THE NAMESERVER COULD RESOLVE THE REQUEST WITHOUT FAILURE
				Component_Title_Label.setText(req.get("CODE") + ": " + req.get("MESSAGE"));
				// WHEN TYPE IS "RECOVER", WE JUST PRESENT THE MESSAGE
				// ELSE, REDIRECT USER TO THE CHAT APPLICATION
				Boolean IsValid = (req.get("PIN") != null && req.get("NK") != null);
				if(req.get("TYPE").trim().equalsIgnoreCase("RECOVER") == false && IsValid) {
					// DESTROY TEMPORARY SOCKET, IN ORDER TO CREATE THE REAL PORT ALLOCATED SOCKET
					sock.destroy();
					// CALL CHAT APPLICATION FRONTEND WITH ALL REQUIRED PARAMETERS
					String UserNickname = req.get("NK");
					Integer UserPIN = Integer.valueOf(req.get("PIN").trim());
					Chat chat = new Chat(UserNickname, UserPIN, this.RegisterAgent_Address);
					chat.setDebug(ShowDebug);
					chat.setEncrypt(EncryptDES);
					this.setVisible(false);	
				}
				// THE REGISTER AGENT ANSWERS "SUCCESS" WHEN THE NAMESERVER COULD RESOLVE THE REQUEST WITHOUT FAILURE
				break;
			case "ERROR":
				// THE REGISTER AGENT ANSWERS "ERROR" WHEN THE NAMESERVER COULD NOT RESOLVE THE REQUEST
				Component_Title_Label.setText(req.get("CODE") + ": " + req.get("MESSAGE"));
				// THE REGISTER AGENT ANSWERS "ERROR" WHEN THE NAMESERVER COULD NOT RESOLVE THE REQUEST
				break;
			default:
				break;
		}
	}

	public void sendRequest(String recipient, String payload) {
		String IP = recipient.split(":")[0];
		String Port = recipient.split(":")[1];
		sock.sendDatagramPacket(Integer.valueOf(Port), payload, IP);
	}
	
	public void setActionListeners() {
		// THE ACTION LISTENERS FOR THIS COMPONENT ARE RATHER COMPLEX.
		// THERE ARE 3 BUTTONS, SO 3 ACTION LISTENERS ARE NEEDED
		this.Component_Login.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent x){
				String Nickname = Component_Nickname.getText();
				String PIN = Component_PIN.getText();
				if(PIN.matches("[0-9]+") == true) {
					Map<String, String> Request = new HashMap<String, String>();
					Request.put("APP", "FRONTEND");
					Request.put("OP", "LOGIN");
					Request.put("NK", Nickname);
					Request.put("PIN", PIN);
					
					
					sendRequest(RegisterAgent_Address, Request.toString());					
				} else {
					Component_Title_Label.setText("PIN can only contain numbers.");
				}

			}
		});
		this.Component_Register.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent x){
				String Nickname = Component_Nickname.getText();
				String PIN = Component_PIN.getText();
				
				if(PIN.matches("[0-9]+") == true) {
					Map<String, String> Request = new HashMap<String, String>();
					Request.put("APP", "FRONTEND");
					Request.put("OP", "REGISTER");
					Request.put("NK", Nickname);
					Request.put("PIN", PIN);
					
					sendRequest(RegisterAgent_Address, Request.toString());					
				} else {
					Component_Title_Label.setText("PIN can only contain numbers.");
				}
							

			}
		});
		this.Component_ForgotPIN.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent x){
				String Nickname = Component_Nickname.getText();
							
				Map<String, String> Request = new HashMap<String, String>();
				Request.put("APP", "FRONTEND");
				Request.put("OP", "RECOVER");
				Request.put("NK", Nickname);
				
				sendRequest(RegisterAgent_Address, Request.toString());				
				
			}
		});
	}
	
	// TRICK TO HAVE PERFECTLY ALIGNED PANELS, WITH COMPLEX COLUMNS AND LINES
	public Panel GUI_setFieldsPanel() {
		Panel NicknamePanel = new Panel();
		NicknamePanel.setLayout(new BorderLayout(5,5));
		NicknamePanel.add("West",Component_Nickname_Label);
		NicknamePanel.add("East",Component_Nickname);
		
		Panel PINPanel = new Panel();
		PINPanel.setLayout(new BorderLayout(5,5));
		PINPanel.add("West",Component_PIN_Label);
		PINPanel.add("East",Component_PIN);
		
		Panel FieldsContainer = new Panel();
		FieldsContainer.setLayout(new BorderLayout(5,5));
		FieldsContainer.add("North", NicknamePanel);
		FieldsContainer.add("Center", PINPanel);
		return FieldsContainer;
	}
	
	public Panel GUI_setButtonsPanel() {
		Panel Buttons = new Panel();
		Buttons.setLayout(new BorderLayout(5,5));
		Buttons.add("West",Component_Login);
		Buttons.add("East",Component_Register);
		Buttons.add("South",Component_ForgotPIN);
		return Buttons;
	}
	// TRICK TO HAVE PERFECTLY ALIGNED PANELS, WITH COMPLEX COLUMNS AND LINES

	public void GUI() {
		setBackground(Color.lightGray);
		GridBagLayout GBL = new GridBagLayout();
		setLayout(GBL);
		Panel P1 = new Panel();
		P1.setLayout(new BorderLayout(5,5));
		P1.add("North",Component_Title_Label);
		
		Panel P2 = this.GUI_setFieldsPanel();
		Panel P3 = this.GUI_setButtonsPanel();
		
		P1.add("Center", P2);
		P1.add("South", P3);
		
		GridBagConstraints P1C=new GridBagConstraints();
		P1C.gridwidth=GridBagConstraints.REMAINDER;
		GBL.setConstraints(P1,P1C);
		add(P1);
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
	}
	
	public void printDebug(String message) {
		if(this.ShowDebug == true) {
			System.out.println("DEBUG: " + message);	
		}
	}		
	
}
