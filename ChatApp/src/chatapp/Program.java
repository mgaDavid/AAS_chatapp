// Quais funcionalidades estão implementadas?
// 1. Login de utilizador, dado um Nickname e PIN, faz chamada ao webservice REST/JSON e valida se estão corretos;
// 2. Ao tentar enviar uma mensagem, é feita uma chamada ao webservice REST/JSON para ir buscar o PIN mapeado e então enviar a mensagem;
// Esta aplicação, ao contrário do ServNomes, NÃO será instalada no Cluster DRBD.
// Deve estar na máquina hospedeira, i.e. máquina física, e irá comunicar com os Cluster DRBD para validação de nicknames/login
// Adicionalmente, irá comunicar com outras instâncias da TrocaMsg via DatagramSockets.
// Portanto:
// TrocaMsg -> ServNomes (Web-Service REST/JSON)
// TrocaMsg -> TrocaMsg  (DatagramSocket)
package chatapp;

public class Program {

    public static void main(String[] args) {
        Login login = new Login();
        login.setVisible(true);
    }

}
