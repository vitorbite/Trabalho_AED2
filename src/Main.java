package src;
import src.br.edu.icev.aed.forense.MinhaAnaliseForense;

public class Main {
    public static void main(String[] args) {
        MinhaAnaliseForense a = new MinhaAnaliseForense();
        // a.encontrarSessoesInvalidas("/home/vitor/Documentos/Trabalhos de Novembro/Trabalho_AED2/arquivo_logs.csv");
        a.encontrarPicosTransferencia("/home/vitor/Documentos/Trabalhos de Novembro/Trabalho_AED2/arquivo_logs.csv");
    }
}
