package src.br.edu.icev.aed.forense;

import java.io.*;
import java.util.*;

import br.edu.icev.aed.forense.Alerta;
import br.edu.icev.aed.forense.AnaliseForenseAvancada;

public class MinhaAnaliseForense implements AnaliseForenseAvancada {

    // public List<String[]> LerArquivo(String caminhoArquivo) {
    Alerta alerta = new Alerta(0, null, null, null, null, 0, 0);

    @Override
    public Set<String> encontrarSessoesInvalidas(String arquivo)
    // throws IOException
    {
        Set<String> logins_invalidos = new TreeSet<>();
        Map<String, Stack<String>> mapa = new TreeMap<>();

        // List<String[]> linhas = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            boolean primeiraExec = true;
            while ((linha = br.readLine()) != null) {
                if (primeiraExec) {
                    primeiraExec = false;
                    continue;                    
                }
                // Divide a linha por vírgulas, respeitando o formato CSV simples
                String[] valores = linha.split(",");
                // linhas.add(valores);

                // Valida se a linha tem todos os campos necessários
                if (valores.length < 7) {
                    continue;
                }

                Alerta alerta = new Alerta(Long.parseLong(valores[0]), valores[1], valores[2], valores[3], valores[4], Integer.parseInt(valores[5]), Long.parseLong(valores[6].isEmpty() ? "0" : valores[6]));
                Stack<String> pilha = mapa.computeIfAbsent(alerta.getUserId(), (k) -> new Stack<>());

                if (mapa.containsKey(alerta.getUserId())) {
                    if (alerta.getActionType().equals("LOGIN")) {
                        pilha.push(alerta.getSessionId());
                        // System.out.println(mapa);
                        // System.out.println("\n\n");
                        // System.out.println(pilha);
                    } else if (alerta.getActionType().equals("LOGOUT")) {
                        if (pilha.empty()) {
                            logins_invalidos.add(alerta.getSessionId());
                            // System.out.println(pilha);
                        }else{
                            String sessao = pilha.pop();
                            // System.out.println(pilha);
                            if (!sessao.equals(alerta.getSessionId())) {
                                logins_invalidos.add(sessao);
                                logins_invalidos.add(alerta.getSessionId());
                            }
                        }

                    }
                }
                // System.out.println(logins_invalidos);
                // mapa.put(valores[1], new Stack<String>());
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        } // Implementar usando Map<String, Stack<String>>
        // if (logins_invalidos == null) {
        //     return new HashSet<>();
        // }
        System.out.println(logins_invalidos);
        return logins_invalidos.isEmpty() ? new HashSet<>() : logins_invalidos;
    }

    @Override
    public List<String> reconstruirLinhaTempo(String arquivo, String sessionId) throws IOException {
        // Implementar usando Queue<String>
    }

    @Override
    public List<Alerta> priorizarAlertas(String arquivo, int n) throws IOException {
        // Implementar usando PriorityQueue<Alerta>
    }

    @Override
    public Map<Long, Long> encontrarPicosTransferencia(String arquivo) throws IOException {
        // Implementar usando Stack (Next Greater Element)
        
    }

    @Override
    public Optional<List<String>> rastrearContaminacao(String arquivo, String origem, String destino)
            throws IOException {
        // Implementar usando BFS em grafo
    }
}