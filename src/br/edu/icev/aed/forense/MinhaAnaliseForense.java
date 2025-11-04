package src.br.edu.icev.aed.forense;

import java.io.*;
import java.util.*;

import br.edu.icev.aed.forense.Alerta;
import br.edu.icev.aed.forense.AnaliseForenseAvancada;

public class MinhaAnaliseForense implements AnaliseForenseAvancada {

    // public List<String[]> LerArquivo(String caminhoArquivo) {

    @Override
    public Set<String> encontrarSessoesInvalidas(String arquivo)
    // throws IOException
    {
        Set<String> logins_invalidos = new TreeSet<>();
        Map<String, Stack<String>> mapa = new TreeMap<>();

        // List<String[]> linhas = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                // Divide a linha por vírgulas, respeitando o formato CSV simples
                String[] valores = linha.split(",");
                // linhas.add(valores);
                String userID = valores[1];
                String sessionID = valores[2];
                String actionType = valores[3];

                Stack<String> pilha = mapa.computeIfAbsent(valores[1], k -> new Stack<>());

                if (mapa.containsKey(userID)) {
                    if (actionType.equals("LOGIN")) {
                        pilha.push(sessionID);
                        // System.out.println(mapa);
                        // System.out.println("\n\n");
                    } else if (actionType.equals("LOGOUT")) {
                        if (pilha.empty()) {
                            logins_invalidos.add(sessionID);
                        }
                        else{
                            String last = pilha.pop();
                            if (!last.equals(sessionID)) {
                                logins_invalidos.add(last);
                                logins_invalidos.add(sessionID);
                            }
                        }

                    }
                }
                System.out.println(logins_invalidos);
                // mapa.put(valores[1], new Stack<String>());
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        } // Implementar usando Map<String, Stack<String>>
        if (logins_invalidos == null) {
            return new HashSet<>();
        }
        return logins_invalidos;
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