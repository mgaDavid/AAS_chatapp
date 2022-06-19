package chatapp;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Login extends Frame {

    Label componentTitleLabel = new Label("Welcome! To proceed, please login.");
    Label componentNicknameLabel = new Label("Nickname: ");
    TextField componentNickname = new TextField(10);
    Label componentPINLabel = new Label("PIN: ");
    TextField componentPIN = new TextField(10);
    Button componentLogin = new Button("Login");

    public Login() {
        super("Chat: Login");
        this.setActionListeners();
        this.setGUI();
    }

    public void setGUI() {
        this.setSize(320, 290);
        this.GUI();
    }

    public void sendRequest(String nick, String pin) {
        String webservice = "http://192.168.132.120:8080/nameserver/ns/nameserver/login";
        String mountedURL = webservice + "?nick=" + nick + "&pin=" + pin;
        String result = "";

        // Starts the connection with the web-service
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

        // Validate the user credentials, if it is ok then runs the chat application
        if (result.contains("OK")) {
            Chat chat = new Chat(nick, Integer.valueOf(pin));
            dispose();
        } else {
            System.out.println(result);
            componentTitleLabel.setText(result);
        }
    }

    public void setActionListeners() {
        this.componentLogin.addActionListener((ActionEvent x1) -> {
            String Nickname = componentNickname.getText();
            String PIN = componentPIN.getText();
            
            if (Nickname.length() == 0) {
                componentTitleLabel.setText("Nickname cannot be empty.");
            } else if (PIN.length() != 4 || PIN.matches("[0-9]+") != true || Integer.valueOf(PIN) < 8000 || Integer.valueOf(PIN) > 8010) {
                componentTitleLabel.setText("PIN must be a number in range [8000; 8010].");
            } else {
                sendRequest(Nickname, PIN);
            }
        });
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                System.exit(0);
            }

        });

    }

    // ALIGN PANELS, WITH COMPLEX COLUMNS AND LINES
    public Panel GUI_setFieldsPanel() {
        Panel nicknamePanel = new Panel();
        nicknamePanel.setLayout(new BorderLayout(5, 5));
        nicknamePanel.add("West", componentNicknameLabel);
        nicknamePanel.add("East", componentNickname);

        Panel PINPanel = new Panel();
        PINPanel.setLayout(new BorderLayout(5, 5));
        PINPanel.add("West", componentPINLabel);
        PINPanel.add("East", componentPIN);

        Panel fieldsContainer = new Panel();
        fieldsContainer.setLayout(new BorderLayout(5, 5));
        fieldsContainer.add("North", nicknamePanel);
        fieldsContainer.add("Center", PINPanel);
        return fieldsContainer;
    }

    public Panel GUI_setButtonsPanel() {
        Panel buttonsPanel = new Panel();
        buttonsPanel.setLayout(new BorderLayout(5, 5));
        buttonsPanel.add("West", componentLogin);
        return buttonsPanel;
    }
    // ALIGN PANELS, WITH COMPLEX COLUMNS AND LINES

    public void GUI() {
        setBackground(Color.lightGray);
        GridBagLayout GBL = new GridBagLayout();
        setLayout(GBL);
        Panel panel1 = new Panel();
        panel1.setLayout(new BorderLayout(5, 5));
        panel1.add("North", componentTitleLabel);

        Panel panel2 = this.GUI_setFieldsPanel();
        Panel panel3 = this.GUI_setButtonsPanel();

        panel1.add("Center", panel2);
        panel1.add("South", panel3);

        GridBagConstraints panel1Constraints = new GridBagConstraints();
        panel1Constraints.gridwidth = GridBagConstraints.REMAINDER;
        GBL.setConstraints(panel1, panel1Constraints);
        add(panel1);
    }

}
