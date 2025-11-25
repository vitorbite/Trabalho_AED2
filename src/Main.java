import br.edu.icev.aed.forense.Alerta;
import br.edu.icev.aed.forense.MinhaAnaliseForense;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String caminhoArquivo = "arquivo_logs.csv";
        MinhaAnaliseForense a = new MinhaAnaliseForense();
        // a.encontrarSessoesInvalidas(...);
        a.encontrarPicosTransferencia(caminhoArquivo);
        try {
           
            var alertas = a.priorizarAlertas(caminhoArquivo, 4);
            System.out.println("=== Alertas Priorizados ===");
            System.out.println(alertas);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        try {
            // Teste do Desafio: Reconstruir Linha do Tempo
            System.out.println("=== Teste Linha do Tempo (session-a-01) ===");
            
            // Chama o método procurando pela sessão 'session-a-01' (que existe no CSV)
            var timeline = a.reconstruirLinhaTempo(caminhoArquivo, "session-a-01");
            
            // Imprime o resultado
            System.out.println(timeline); 
            
            // Resultado esperado para session-a-01: [LOGIN, COMMAND_EXEC, FILE_ACCESS, LOGOUT]

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
