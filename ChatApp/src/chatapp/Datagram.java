package chatapp;

import java.net.*;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class Datagram extends Thread {

    private static final String password = "ea6bdcc2c378";
    private static SecretKey secretKey;

    InetAddress Sender;
    InetAddress Destination;
    DatagramSocket DatagramSocket;
    byte[] Buffer = new byte[1024];

    Chat chat;
    Integer portNumber = null;

    public Datagram(Integer port_number) {
        this.portNumber = port_number;
    }

    public void run() {
        try {
            DatagramSocket = new DatagramSocket(portNumber);
            this.setSecretKey(password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (true) {
            receiveDatagram();
        }
    }

    public void receiveDatagram() {
        try {
            DatagramPacket DatagramPacket = new DatagramPacket(Buffer, Buffer.length);
            DatagramSocket.receive(DatagramPacket);
            Sender = DatagramPacket.getAddress();

            byte[] decrypted = this.decryptDES(DatagramPacket.getData());
            String Message = new String(decrypted);
            System.out.println(Message);

            chat.windowShowMessage.append(Message);
            chat.windowShowMessage.append("\n");
        } catch (Exception e) {
            System.out.println("Error trying to receive datagram.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void sendDatagram(int Port, String Message, String Recipient) {
        try {
            byte[] MessageBytes = Message.getBytes();
            Destination = InetAddress.getByName(Recipient);

            byte[] clearBytes;
            if (Buffer.length - MessageBytes.length > 0) {
                clearBytes = new byte[Buffer.length - MessageBytes.length];
            } else {
                clearBytes = new byte[0];
            }
            byte[] finalArray = new byte[MessageBytes.length + clearBytes.length];
            System.arraycopy(MessageBytes, 0, finalArray, 0, MessageBytes.length);
            System.arraycopy(clearBytes, 0, finalArray, MessageBytes.length, clearBytes.length);
            MessageBytes = this.encryptDES(finalArray);
            System.out.println(MessageBytes);

            DatagramPacket DatagramPacket = new DatagramPacket(MessageBytes, MessageBytes.length, Destination, Port);
            DatagramSocket.send(DatagramPacket);
        } catch (Exception e) {
            System.out.println("Error trying to send datagram.");
            e.printStackTrace();
        }
    }

    private void setSecretKey(String Key) throws Exception {
        byte key[] = Key.getBytes("UTF8");
        DESKeySpec desKeySpec = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        Datagram.secretKey = secretKey;
    }

    private byte[] encryptDES(byte[] message) throws Exception {
        Cipher desCipher = Cipher.getInstance("DES/ECB/NoPadding");
        desCipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encodedSTR = desCipher.doFinal(message);
        return encodedSTR;
    }

    private byte[] decryptDES(byte[] message) throws Exception {
        Cipher desCipher = Cipher.getInstance("DES/ECB/NoPadding");
        desCipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedSTR = desCipher.doFinal(message);
        return decodedSTR;
    }

}
