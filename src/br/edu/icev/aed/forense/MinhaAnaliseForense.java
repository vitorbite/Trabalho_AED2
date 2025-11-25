package br.edu.icev.aed.forense;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

//import br.edu.icev.aed.forense.Alerta;
//import br.edu.icev.aed.forense.AnaliseForenseAvancada;

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
        return new ArrayList<>();
    }

    // Prioriza eventos de log com base no SEVERITY_LEVEL (decrescente)
    @Override
    public List<Alerta> priorizarAlertas(String arquivo, int n) throws IOException {
        // 1. Caso Específico: n <= 0, retorna List vazia.
        if (n <= 0) {
            return new ArrayList<>();
        }

        // Criando uma PriorityQueue com um Comparator para ordenar por SEVERITY_LEVEL
        // decrescente
        // O Comparator usa b.getSeverityLevel() - a.getSeverityLevel() para priorizar
        // severidade maior
        Comparator<Alerta> comparatorSeveridadeDesc = (a, b) -> Integer.compare(b.getSeverityLevel(),
                a.getSeverityLevel());
        PriorityQueue<Alerta> priorityQueue = new PriorityQueue<>(comparatorSeveridadeDesc);

        // Ler cada linha do log, criar um objeto Alerta e adicionr à PriorityQueue
        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            boolean primeiraExec = true;
            while ((linha = br.readLine()) != null) {
                if (primeiraExec) {
                    primeiraExec = false;
                    continue; // Pula o cabeçalho CSV
                }

                String[] valores = linha.split(",");

                // Valida se a linha tem 7 campos
                if (valores.length < 7) {
                    continue;
                }

                try {
                    long timestamp = Long.parseLong(valores[0]);
                    String userId = valores[1];
                    String sessionId = valores[2];
                    String actionType = valores[3];
                    String targetResource = valores[4];
                    int severityLevel = Integer.parseInt(valores[5]);
                    // Trata o campo BYTES_TRANSFERRED (índice 6), que pode estar vazio em algumas
                    // linhas
                    long bytesTransferred = Long.parseLong(valores[6].isEmpty() ? "0" : valores[6]);

                    Alerta novoAlerta = new Alerta(timestamp, userId, sessionId, actionType, targetResource,
                            severityLevel, bytesTransferred);
                    priorityQueue.add(novoAlerta);

                } catch (NumberFormatException e) {
                    // Ignora linhas com valores mal formatados
                    continue;
                }
            }
        }
        // 4. Extrair os n primeiros itens da fila usando poll()
        List<Alerta> alertasPriorizados = new ArrayList<>();
        // Extrai até 'n' alertas, ou até que a fila esteja vazia (tratando o Caso
        // Específico de menos de 'n' eventos).
        for (int i = 0; i < n && !priorityQueue.isEmpty(); i++) {
            alertasPriorizados.add(priorityQueue.poll());
        }
        // 5. NUNCA retornar null. (Adicionei a palavra-chave return em outras partes
        // para cumprir com o requisito de NUNCA retornar null)
        return alertasPriorizados;
        // Com isso os 5 requisitos para o Segundo desafio estão concluidos
    }

    @Override
    public Map<Long, Long> encontrarPicosTransferencia(String arquivo) {
        // Implementar usando Stack (Next Greater Element)
        Map<Long, Long> mapa = new HashMap<>();
        Stack<Alerta> pilhaEventos = new Stack<>();
        try {
            List<String> linhas = Files.readAllLines(Paths.get(arquivo));
            Collections.reverse(linhas);
            for (String linha : linhas) {
                String[] valores = linha.split(",");

                if (valores[0].equals("TIMESTAMP")) {
                    continue;
                }
                if (valores.length < 7) {
                    continue;
                }

                Alerta eventoAtual = new Alerta(Long.parseLong(valores[0]), valores[1], valores[2], valores[3],
                        valores[4], Integer.parseInt(valores[5]), Long.parseLong(valores[6]));

                if (!eventoAtual.getActionType().equals("DATA_TRANSFER")) {
                    continue;
                }

                // Desempilha eventos com bytes <= eventoAtual (buscando o primeiro maior)
                while (!pilhaEventos.isEmpty()
                        && pilhaEventos.peek().getBytesTransferred() <= eventoAtual.getBytesTransferred()) {
                    Alerta removido = pilhaEventos.pop();
                    // Se ainda há elementos na pilha, o topo é o próximo maior
                    if (!pilhaEventos.isEmpty()) {
                        mapa.put(removido.getTimestamp(), pilhaEventos.peek().getTimestamp());
                    }
                }
                pilhaEventos.push(eventoAtual);
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
        mapa.keySet().forEach(e -> System.out.println(e));
        System.out.println("Chave 1700000506: " + mapa.get(Long.parseLong("1700000506")));
        System.out.println("-----");
        mapa.values().forEach(e -> System.out.println(e));
        return mapa;
    }

    @Override
    public Optional<List<String>> rastrearContaminacao(String arquivo, String origem, String destino)
            throws IOException {
        Map<String, List<String>> lista_adjacencia = new HashMap<>();
        Map<String, String> predecessor = new HashMap<>();
        Queue<String> fila = new LinkedList<>();
        
        Set<String> visitados = new HashSet<>();
        boolean primeiraExec = true;
        boolean encontrado = false;
        

        Map<String, List<String>> recursosPorSessao = new HashMap<>();
        Set<String> recursosExistentes = new HashSet<>();

        try {
            List<String> linhas = Files.readAllLines(Paths.get(arquivo));
            
            // Primeiro passo: agrupar recursos por sessão
            for (String linha : linhas) {
                if (primeiraExec) {
                    primeiraExec = false;
                    continue;
                }
                String[] valores = linha.split(",");
                if (valores.length < 7) {
                    continue;
                }
                String sessionId = valores[2];
                String recurso = valores[4];
                recursosExistentes.add(recurso);
                
                recursosPorSessao.computeIfAbsent(sessionId, k -> new ArrayList<>()).add(recurso);
            }

            for (String sessionId : recursosPorSessao.keySet()) {
                List<String> recursos = recursosPorSessao.get(sessionId);
                
                for (int i = 0; i < recursos.size() - 1; i++) {
                    String atual = recursos.get(i);
                    String proximo = recursos.get(i + 1);
                    
                    if (!atual.equals(proximo)) {
                        lista_adjacencia.computeIfAbsent(atual, k -> new ArrayList<>());
                        if (!lista_adjacencia.get(atual).contains(proximo)) {
                            lista_adjacencia.get(atual).add(proximo);
                        }
                    }
                }
            }

            // Verifica se origem é igual ao destino
            if (origem.equals(destino)) {
                if (recursosExistentes.contains(origem)) {
                    return Optional.of(Collections.singletonList(origem));
                } else {
                    return Optional.empty();
                }
            }

            // Se a origem não existe nos logs, não há caminho
            if (!recursosExistentes.contains(origem)) {
                return Optional.empty();
            }
            fila.add(origem);
            visitados.add(origem);

            while (!fila.isEmpty() && !encontrado) {
                String u = fila.remove();
                List<String> vizinhos = lista_adjacencia.getOrDefault(u, Collections.emptyList());
                
                for (String v : vizinhos) {
                    if (visitados.contains(v)) {
                        continue;
                    }
                    
                    visitados.add(v);
                    predecessor.put(v, u);
                    
                    if (v.equals(destino)) {
                        encontrado = true;
                        break;
                    }
                    
                    fila.add(v);
                }
            }

            if (encontrado) {
                LinkedList<String> caminho = new LinkedList<>();
                String atual = destino;
                
                while (atual != null) {
                    caminho.add(atual);
                    if (atual.equals(origem)) {
                        break;
                    }
                    atual = predecessor.get(atual);
                }
                
                Collections.reverse(caminho);
                
                return Optional.of(caminho);
            }

        } catch (IOException e) {
            System.err.println(e);
        }

        return Optional.empty();
    }
}