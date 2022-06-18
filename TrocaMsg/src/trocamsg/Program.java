// Quais funcionalidades estão implementadas?
// 1. Login de utilizador, dado um Nickname e PIN, faz chamada ao webservice REST/JSON e valida se estão corretos;
// 2. Ao tentar enviar uma mensagem, é feita uma chamada ao webservice REST/JSON para ir buscar o PIN mapeado e então enviar a mensagem;

// Quais funcionalidades NÃO estão implementadas?
// 1. Criptografia DES na classe Datagrama.java
// 2. Envio para lista de distribuição


// É muito importante ter cuidado:
// 1. Não tentar enviar mensagens para nicknames inexistentes; 

// A aplicação não está a validar estes casos.

// Esta aplicação, ao contrário do ServNomes, NÃO será instalada no Cluster DRBD.
// Deve estar na máquina hospedeira, i.e. máquina física, e irá comunicar com os Cluster DRBD para validação de nicknames/login
// Adicionalmente, irá comunicar com outras instâncias da TrocaMsg via DatagramSockets.

// Portanto:
// TrocaMsg -> ServNomes (Web-Service REST/JSON)
// TrocaMsg -> TrocaMsg  (DatagramSocket)

package trocamsg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Program {
    public static void main(String[] args) {
        // Abre um Scanner para entrar dados com o teclado no command prompt
        Scanner teclado = new Scanner(System.in);       
        
        // Questionar nickname
        System.out.print("Qual o teu nickname? => ");
        String nick = teclado.nextLine();
        
        // Questionar pin
        System.out.print("Qual o teu pin? => ");
        String pin = teclado.nextLine();
        
        // Poderia ser muito melhor do que isto, mas por mim tanto faz...
        Integer pin_conversao = Integer.valueOf(pin);
        String pin_conversao2 = String.valueOf(pin_conversao);
        
        // Fecha o Scanner
        teclado.close();
        
        // Construir URL para fazer a chamada ao web service
        // Não esquecer de alterar para o IP:PORT do Cluster, após deploy do Name Server!
        String webservice = "http://192.168.132.120:8080/nameserver/ns/nameserver/login";
        String url_montada = webservice + "?nick=" + nick + "&pin=" + pin_conversao2;
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
        if(resultado.contains("OK")){
            TrocaMsg chat = new TrocaMsg(nick, pin_conversao);
        } else {
            System.out.println(resultado);
            System.exit(1);
        }
        
    }
    
}
