package chatapp;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Chat extends Frame {

    TextField fieldDestination = new TextField(30);
    TextField fieldMessage = new TextField(30);
    TextArea windowShowMessage = new TextArea(10, 30);
    Button buttonSend = new Button("Send");

    Datagram sock;

    String nickname;
    private Integer PIN;

    public Chat(String Nickname, Integer PIN) {
        super(Nickname);
        this.nickname = Nickname;
        this.PIN = PIN;
        this.startSocket(PIN);
        this.setActionListeners();
        this.setGUI();
    }

    public void setGUI() {
        this.setSize(350, 300);
        this.GUI();
        this.setVisible(true);
    }

    public void startSocket(Integer Port) {
        sock = new Datagram(Port);
        sock.chat = this;
        sock.start();
    }

    public void setActionListeners() {
        this.buttonSend.addActionListener((ActionEvent x1) -> {
            String[] nickNames = fieldDestination.getText().split(";");
            String message = fieldMessage.getText();

            for (String destinationNickName : nickNames) {
                String destinationPIN = webserviceCall(destinationNickName.trim()).trim();
                System.out.println("The server replied to the query: " + destinationPIN);
                if (destinationPIN.contains("Error")) {
                    windowShowMessage.append("Could not find user with nickname: " + destinationNickName);
                    System.out.println(destinationPIN);
                } else {
                    try {
                        sock.sendDatagram(Integer.valueOf(destinationPIN), nickname + " said: " + message, "127.0.0.1");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                System.exit(0);
            }

        });

    }

    public void GUI() {
        setBackground(Color.lightGray);
        windowShowMessage.setEditable(false);
        GridBagLayout GBL = new GridBagLayout();
        setLayout(GBL);
        Panel panel1 = new Panel();
        panel1.setLayout(new BorderLayout(5, 5));
        panel1.add("North", fieldDestination);
        panel1.add("West", fieldMessage);
        panel1.add("East", buttonSend);
        panel1.add("South", windowShowMessage);
        GridBagConstraints panel1Constraints = new GridBagConstraints();
        panel1Constraints.gridwidth = GridBagConstraints.REMAINDER;
        GBL.setConstraints(panel1, panel1Constraints);
        add(panel1);
    }

    public String webserviceCall(String destino) {
        // Create URL to make the call to the webservice 

        // By providing a nickname, the server must reply with a PIN (if it exists)
        String webservice = "http://192.168.132.120:8080/nameserver/ns/nameserver/forgotpin";
        String mountedURL = webservice + "?nick=" + destino;
        String result = "";

        HttpURLConnection con = null;
        try {
            URL obj = new URL(mountedURL);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            Integer responseCode = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            result = response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return result;
    }

}
