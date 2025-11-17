import br.edu.icev.aed.forense.MinhaAnaliseForense;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        MinhaAnaliseForense a = new MinhaAnaliseForense();
        // a.encontrarSessoesInvalidas(...);
        a.encontrarPicosTransferencia("C:\\Users\\arthu\\OneDrive\\Desktop\\AED2\\Trabalho_AED2\\arquivo_logs.csv");
        try {
            a.priorizarAlertas("C:\\Users\\arthu\\OneDrive\\Desktop\\AED2\\Trabalho_AED2\\arquivo_logs.csv", 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
