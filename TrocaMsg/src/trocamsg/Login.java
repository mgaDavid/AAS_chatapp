package trocamsg;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Login extends Frame {

    Label Component_Title_Label = new Label("Welcome! To proceed, please login.");
    Label Component_Nickname_Label = new Label("Nickname: ");
    TextField Component_Nickname = new TextField(10);
    Label Component_PIN_Label = new Label("PIN: ");
    TextField Component_PIN = new TextField(10);
    Button Component_Login = new Button("Login");

    public String RegisterAgent_Address;

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
        String url_montada = webservice + "?nick=" + nick + "&pin=" + pin;
        String resultado = "";

        // Inicia 1ª conexão com web-service para fazer login
        HttpURLConnection con = null;
        try {
            URL obj = new URL(url_montada);
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
            resultado = response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Valida a resposta do web-service. Se "OK", então inicia a instância de chat.
        if (resultado.contains("OK")) {
            TrocaMsg chat = new TrocaMsg(nick, Integer.valueOf(pin));
            this.setVisible(false);
        } else {
            System.out.println(resultado);
            Component_Title_Label.setText(resultado);
        }
    }

    public void setActionListeners() {
        // THE ACTION LISTENERS FOR THIS COMPONENT ARE RATHER COMPLEX.
        // THERE ARE 3 BUTTONS, SO 3 ACTION LISTENERS ARE NEEDED
        this.Component_Login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent x) {
                String Nickname = Component_Nickname.getText();
                String PIN = Component_PIN.getText();

                if (PIN.matches("[0-9]+") == true) {
                    sendRequest(Nickname, PIN);
                } else {
                    Component_Title_Label.setText("PIN can only contain numbers.");
                }

            }
        });

    }

    // TRICK TO HAVE PERFECTLY ALIGNED PANELS, WITH COMPLEX COLUMNS AND LINES
    public Panel GUI_setFieldsPanel() {
        Panel NicknamePanel = new Panel();
        NicknamePanel.setLayout(new BorderLayout(5, 5));
        NicknamePanel.add("West", Component_Nickname_Label);
        NicknamePanel.add("East", Component_Nickname);

        Panel PINPanel = new Panel();
        PINPanel.setLayout(new BorderLayout(5, 5));
        PINPanel.add("West", Component_PIN_Label);
        PINPanel.add("East", Component_PIN);

        Panel FieldsContainer = new Panel();
        FieldsContainer.setLayout(new BorderLayout(5, 5));
        FieldsContainer.add("North", NicknamePanel);
        FieldsContainer.add("Center", PINPanel);
        return FieldsContainer;
    }

    public Panel GUI_setButtonsPanel() {
        Panel Buttons = new Panel();
        Buttons.setLayout(new BorderLayout(5, 5));
        Buttons.add("West", Component_Login);
        return Buttons;
    }
    // TRICK TO HAVE PERFECTLY ALIGNED PANELS, WITH COMPLEX COLUMNS AND LINES

    public void GUI() {
        setBackground(Color.lightGray);
        GridBagLayout GBL = new GridBagLayout();
        setLayout(GBL);
        Panel P1 = new Panel();
        P1.setLayout(new BorderLayout(5, 5));
        P1.add("North", Component_Title_Label);

        Panel P2 = this.GUI_setFieldsPanel();
        Panel P3 = this.GUI_setButtonsPanel();

        P1.add("Center", P2);
        P1.add("South", P3);

        GridBagConstraints P1C = new GridBagConstraints();
        P1C.gridwidth = GridBagConstraints.REMAINDER;
        GBL.setConstraints(P1, P1C);
        add(P1);
    }

}
