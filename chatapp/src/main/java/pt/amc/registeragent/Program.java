package pt.amc.registeragent;

import java.util.*;

public class Program {

	public static void main(String[] args) {
		RegisterAgent registerag = null;
		System.out.println("Welcome to Users Agent. Type HELP to see supported commands.");
		Scanner input = new Scanner(System.in);
		while(input.hasNextLine()) {
			String line = input.nextLine();
			if(line.isBlank()) {
				input.close();
				System.exit(0);
			}
			String[] commands = line.split(" ");
			switch(commands[0].toUpperCase()) {
			case "HELP":
				System.out.println("");
				System.out.println("START     " + " => " + "Starts the users agent. Syntax: START [A] [B], where [A] is the users agent, and [B] is the nameserver address in IP:PORT format.");
				System.out.println("STATUS    " + " => " + "If the server is running, returns the port allocated to the users agent server. Returns an error otherwise.");
				System.out.println("REQUEST   " + " => " + "Manually sends an request for the nameserver. Syntax: REQUEST [OP] [NK] [PW]");
				System.out.println("DEBUG     " + " => " + "Prints all debug messages to the terminal. Syntax is DEBUG [A], where [A] can be Y or N. Default is N.");
				System.out.println("CLEAR     " + " => " + "Clears the terminal.");
				System.out.println("SHUTDOWN  " + " => " + "Destroy the server.");
				System.out.println("");
				break;
			case "START":
				if(commands.length == 3) {
					if(registerag != null) {
						System.out.println("The service is already started. Please review.");
					} else {
						Integer RGPort = Integer.valueOf(commands[1]);
						String NSAddress = commands[2];
						registerag = new RegisterAgent(RGPort, NSAddress);
						Socket sock = new Socket(RGPort, registerag);
						StartSocket(sock);
						registerag.setSocket(sock);
						
						System.out.println("The service was started successfully!");
					}
					
				} else {
					System.out.println(commands[0] + ": " + "command not found");
				}
				break;
			case "STATUS":
				if(commands.length == 1) {
					if(registerag != null) {
						System.out.println("Port: " + registerag.getSocket().PORT);
					} else {
						System.out.println("The service is not started.");
					}
				} else {
					System.out.println(commands[0] + ": " + "command not found");
				}
				break;
			case "REQUEST":
				if(commands.length == 4) {
					if(registerag != null) {
						Map<String, String> manualRequest = new HashMap<String, String>();
						manualRequest.put("OP", commands[1].toUpperCase());
						manualRequest.put("NK", commands[2]);
						manualRequest.put("PIN", commands[3]);	
						registerag.sendPayload(registerag.NameserverAddress, manualRequest.toString());
					} else {
						System.out.println("The service is not started.");
					}
				} else {
					System.out.println(commands[0] + ": " + "please specify the request.");
				}
				break;
			case "DEBUG":
				if(commands.length == 2) {
					if(registerag != null) {
						Boolean ShowDebug = (commands[1].equalsIgnoreCase("N")) ? false : true;
						registerag.ShowDebug = ShowDebug;
						System.out.println("Debug messages is now " + ((ShowDebug == true) ? "ENABLED" : "DISABLED"));

					} else {
						System.out.println("The service is not started.");
					}
				} else {
					System.out.println(commands[0] + ": " + "command not found");
				}
				break;
			case "CLEAR":
				if(commands.length == 1) {
			        System.out.print("\033[H\033[2J");
			        System.out.flush();
				} else {
					System.out.println(line + ": " + "command not found");
				}
				break;
			case "SHUTDOWN":
				if(commands.length == 1) {
					System.exit(0);
				} else {
					System.out.println(line + ": " + "command not found");
				}
				break;
			default:
				System.out.println(commands[0] + ": " + "command not found");
			}
		}
		input.close();
	}
	
	public static void StartSocket(Socket sock){
		sock.start();
	}	
}