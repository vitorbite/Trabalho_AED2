import src.br.edu.icev.aed.forense.MinhaAnaliseForense;

public class Main {
    public static void main(String[] args) {
        MinhaAnaliseForense a = new MinhaAnaliseForense();
        a.encontrarSessoesInvalidas("arquivo_logs.csv");
    }
}
