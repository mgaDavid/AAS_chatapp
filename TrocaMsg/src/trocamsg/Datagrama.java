package trocamsg;

import java.net.*;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class Datagrama extends Thread {
    
    private static final String palavra_passe = "ea6bdcc2c378";
    private static SecretKey chave_secreta;

    InetAddress Sender;
    InetAddress Destination;
    DatagramSocket DatagramSocket;
    byte[] Buffer = new byte[1024];

    TrocaMsg chat;	
    Integer port_number = null;

    public Datagrama(Integer port_number) {
        this.port_number = port_number;
    }

    public void run() {
        try {
            DatagramSocket = new DatagramSocket(port_number);
            this.setSecretKey(palavra_passe);
        } catch (Exception e) {
            e.printStackTrace();
        }
        while(true) {
            recebeDatagrama();
        }
    }

    public void recebeDatagrama() {
        try {
            DatagramPacket DatagramPacket = new DatagramPacket(Buffer, Buffer.length);
            DatagramSocket.receive(DatagramPacket);
            Sender = DatagramPacket.getAddress();
            String Sender_IP = Sender.toString().substring(1);
            Integer Port = DatagramPacket.getPort();
            String Message = new String(DatagramPacket.getData(), 0, DatagramPacket.getLength());
            //NEW
            byte[] decrypted = this.decryptDES(DatagramPacket.getData());
            Message = new String(decrypted);
            System.out.println(Message);
            chat.ecra_mostra_msg.append(Message + "\n");
        } catch (Exception e) {
            System.out.println("Erro ao tentar receber datagrama.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void enviaDatagrama(int Port, String Message, String Recipient) {
        try {
            byte[] MessageBytes = Message.getBytes();
            Destination = InetAddress.getByName(Recipient);
            //NEW
            byte[] clearBytes = null; 
            if(Buffer.length - MessageBytes.length > 0) {
                clearBytes = new byte[Buffer.length - MessageBytes.length];
            } else {
                clearBytes = new byte[0];
            }
            byte[] finalArray = new byte[MessageBytes.length + clearBytes.length];
            MessageBytes = this.encryptDES(finalArray);
            System.out.println(MessageBytes);
            DatagramPacket DatagramPacket = new DatagramPacket(MessageBytes, MessageBytes.length, Destination, Port);
            DatagramSocket.send(DatagramPacket);
        } catch (Exception e) {
            System.out.println("Erro ao tentar enviar datagrama.");
            e.printStackTrace();
        }
    }
    
    // A implementação do DES não está terminada.
    // Falta chamar o encryptDES() no método enviaDatagrama()
    // E, por semelhança, falta chamar decryptDES() no recebeDatagrama()
    // Isto é, cifrar a mensagem ao enviar, decifrar a mensagem ao receber

    
    private void setSecretKey(String Key) throws Exception {
        byte key[] = Key.getBytes("UTF8");
        DESKeySpec desKeySpec = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        chave_secreta = secretKey;
    }

    private byte[] encryptDES(byte[] message) throws Exception {
        Cipher desCipher = Cipher.getInstance("DES/ECB/NoPadding");
        desCipher.init(Cipher.ENCRYPT_MODE, chave_secreta);
        byte[] encodedSTR = desCipher.doFinal(message);
        return encodedSTR;
    }

    private byte[] decryptDES(byte[] message) throws Exception {
        Cipher desCipher = Cipher.getInstance("DES/ECB/NoPadding");
        desCipher.init(Cipher.DECRYPT_MODE, chave_secreta);
        byte[] decodedSTR = desCipher.doFinal(message);
        return decodedSTR;
    }

}
