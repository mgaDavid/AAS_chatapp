package trocamsg;

import java.util.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TrocaMsg extends Frame {

    TextField campo_destino = new TextField(30);
    TextField campo_msg = new TextField(30);
    TextArea ecra_mostra_msg = new TextArea(10, 30);
    Button botao_envia = new Button("Send");

    Datagrama sock;

    String Nickname;
    Integer PIN;

    public TrocaMsg(String Nickname, Integer PIN) {
        super(Nickname);
        this.Nickname = Nickname;
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
        sock = new Datagrama(Port);
        sock.chat = this;
        sock.start();
    }

    public void setActionListeners() {
        this.botao_envia.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent x) {
                String destino = campo_destino.getText();
                String mensagem = campo_msg.getText();
                
                String pin_destino = chamadaWebservice(destino).trim();
                System.out.println("O servidor respondeu à consulta: " + pin_destino);
                if(pin_destino.contains("Erro")){
                    System.out.println("Sinto muito. " + pin_destino);
                } else {
                    try{
                        sock.enviaDatagrama(Integer.valueOf(pin_destino), Nickname + " disse: " + mensagem, "127.0.0.1");
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }		
            }
        });
    }
    
    public void GUI() {
        setBackground(Color.lightGray);
        ecra_mostra_msg.setEditable(false);
        GridBagLayout GBL = new GridBagLayout();
        setLayout(GBL);
        Panel P1 = new Panel();
        P1.setLayout(new BorderLayout(5, 5));
        P1.add("North", campo_destino);
        P1.add("West", campo_msg);
        P1.add("East", botao_envia);
        P1.add("South", ecra_mostra_msg);
        GridBagConstraints P1C = new GridBagConstraints();
        P1C.gridwidth = GridBagConstraints.REMAINDER;
        GBL.setConstraints(P1, P1C);
        add(P1);
    }
    
    
    
    
    

    public String chamadaWebservice(String destino){
        // Construir URL para fazer a chamada ao web service
        // Não esquecer de alterar para o IP:PORT do Cluster, após deploy do Name Server! 
        
        // Ao fornecer um NICKNAME, o servidor deve responder com um PIN (se existir)
        String webservice = "http://192.168.132.120:8080/nameserver/ns/nameserver/forgotpin";
        String url_montada = webservice + "?nick=" + destino;
        String resultado = "";
        
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
        return resultado;        
    }
    
}
