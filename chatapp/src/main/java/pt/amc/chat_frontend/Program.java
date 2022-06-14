package pt.amc.chat_frontend;

import java.util.*;

public class Program {

	public static void main(String[] args) {
		// STEP #1: SET ADDRESS FOR REGISTER AGENT //
		Scanner in = new Scanner(System.in);
		System.out.print("SET ADDRESS FOR AGENT [IP:PORT] => ");
		String RegisterAgent_Address = in.nextLine();
		
		System.out.print("ENABLE DEBUG MESSAGES? [y/n] => ");
		String ShowDebug = in.nextLine().toUpperCase();
		
		// System.out.print("ENABLE DES ENCRYPTION? [y/n] => ");
		// String EncryptDES = in.nextLine().toUpperCase();
                String EncryptDES = "Y";
		
		in.close();
		// STEP #1: SET ADDRESS FOR REGISTER AGENT //
		
		// STEP #2: SHOW LOGIN SCREEN //		
		Login login = new Login();
		login.RegisterAgent_Address = RegisterAgent_Address;
		login.setVisible(true);
		login.setDebug((ShowDebug.equalsIgnoreCase("N")) ? false : true);
		login.setEncrypt((EncryptDES.equalsIgnoreCase("N")) ? false : true);
		// STEP #2: SHOW LOGIN SCREEN //
	}

}
