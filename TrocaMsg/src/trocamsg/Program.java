// Quais funcionalidades estão implementadas?
// 1. Login de utilizador, dado um Nickname e PIN, faz chamada ao webservice REST/JSON e valida se estão corretos;
// 2. Ao tentar enviar uma mensagem, é feita uma chamada ao webservice REST/JSON para ir buscar o PIN mapeado e então enviar a mensagem;
// Quais funcionalidades NÃO estão implementadas?
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

public class Program {

    public static void main(String[] args) {
        Login login = new Login();
        login.setVisible(true);
    }

}
