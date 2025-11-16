package src.br.edu.icev.aed.forense;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import br.edu.icev.aed.forense.Alerta;
import br.edu.icev.aed.forense.AnaliseForenseAvancada;

public class MinhaAnaliseForense implements AnaliseForenseAvancada {

    @Override
    public Set<String> encontrarSessoesInvalidas(String arquivo) {
        Set<String> logins_invalidos = new TreeSet<>();
        Map<String, Stack<String>> mapa = new TreeMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            boolean primeiraExec = true;
            while ((linha = br.readLine()) != null) {
                if (primeiraExec) {
                    primeiraExec = false;
                    continue;
                }
                String[] valores = linha.split(",");

                if (valores.length < 7) {
                    continue;
                }

                Alerta alerta = new Alerta(Long.parseLong(valores[0]), valores[1], valores[2], valores[3], valores[4],
                        Integer.parseInt(valores[5]), Long.parseLong(valores[6]));
                Stack<String> pilha = mapa.computeIfAbsent(alerta.getUserId(), (k) -> new Stack<>());

                if (mapa.containsKey(alerta.getUserId())) {
                    if (alerta.getActionType().equals("LOGIN")) {
                        pilha.push(alerta.getSessionId());

                    } else if (alerta.getActionType().equals("LOGOUT")) {
                        if (pilha.empty()) {
                            logins_invalidos.add(alerta.getSessionId());

                        } else {
                            String sessao = pilha.pop();

                            if (!sessao.equals(alerta.getSessionId())) {
                                logins_invalidos.add(sessao);
                                logins_invalidos.add(alerta.getSessionId());
                            }
                        }

                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
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
    public Map<Long, Long> encontrarPicosTransferencia(String arquivo) {
        // Implementar usando Stack (Next Greater Element)
        Map<Long, Long> mapa = new HashMap<>();
        Stack<Alerta> pilhaEventos = new Stack<>();
        boolean primeiraExec = true;
        
        try {
            List<String> linhas = Files.readAllLines(Paths.get(arquivo));
            Collections.reverse(linhas);
            for (String linha : linhas) {
                String[] valores = linha.split(",");
                if (valores.length < 7) {
                    continue;
                }

                Alerta eventoAtual = new Alerta(Long.parseLong(valores[0]), valores[1], valores[2], valores[3],
                        valores[4], Integer.parseInt(valores[5]), Long.parseLong(valores[6]));
                pilhaEventos.push(eventoAtual);

                if (primeiraExec) {
                    primeiraExec = false;
                    continue;
                }
                while (!pilhaEventos.isEmpty()
                        && pilhaEventos.getLast().getBytesTransferred() >= eventoAtual.getBytesTransferred()) {
                    pilhaEventos.removeLast();
                    continue;
                }
                System.out.println(mapa);
                mapa.put(eventoAtual.getTimestamp(), pilhaEventos.getLast().getTimestamp());
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
        return mapa;
    }

    @Override
    public Optional<List<String>> rastrearContaminacao(String arquivo, String origem, String destino)
            throws IOException {
        // Implementar usando BFS em grafo
    }
}