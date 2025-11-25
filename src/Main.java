import br.edu.icev.aed.forense.MinhaAnaliseForense;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        MinhaAnaliseForense a = new MinhaAnaliseForense();
        // a.encontrarSessoesInvalidas("arquivo_logs.csv");
        // a.encontrarPicosTransferencia("arquivo_logs.csv");
        try {
           
        // a.priorizarAlertas("arquivo_logs.csv", 4);
        a.rastrearContaminacao("arquivo_logs.csv", "/usr/bin/sshd", "/usr/bin/telnetd");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
