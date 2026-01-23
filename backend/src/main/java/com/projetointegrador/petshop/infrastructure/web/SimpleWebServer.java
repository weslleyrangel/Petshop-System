package com.projetointegrador.petshop.infrastructure.web;

import com.projetointegrador.petshop.domain.cliente.Cliente;
import com.projetointegrador.petshop.domain.exception.DomainException;
import com.projetointegrador.petshop.domain.pet.Pet;
import com.projetointegrador.petshop.domain.produto.Produto;
import com.projetointegrador.petshop.domain.vendas.Venda;
import com.projetointegrador.petshop.infrastructure.api.agendamento.AgendamentoController;
import com.projetointegrador.petshop.infrastructure.api.auth.AuthController;
import com.projetointegrador.petshop.infrastructure.api.cliente.ClienteController;
import com.projetointegrador.petshop.infrastructure.api.pet.PetController;
import com.projetointegrador.petshop.infrastructure.api.produto.ProdutoController;
import com.projetointegrador.petshop.infrastructure.api.venda.VendaController;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleWebServer {

    private static final int PORT = 8080;
    // Caminho absoluto para a pasta do Front-end original
    private static final String FRONTEND_PATH = "C:/Users/wesll/Desktop/Petshop System/Front/src";

    private HttpServer server;
    private final AuthController authController;
    private final ClienteController clienteController;
    private final PetController petController;
    private final AgendamentoController agendamentoController;
    private final ProdutoController produtoController;
    private final VendaController vendaController;

    public SimpleWebServer(AuthController authController, ClienteController clienteController, 
                           PetController petController, AgendamentoController agendamentoController,
                           ProdutoController produtoController, VendaController vendaController) {
        this.authController = authController;
        this.clienteController = clienteController;
        this.petController = petController;
        this.agendamentoController = agendamentoController;
        this.produtoController = produtoController;
        this.vendaController = vendaController;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Handler genérico para servir arquivos estáticos do Front-end original
        server.createContext("/", new StaticFileHandler());

        // Rotas de API (mantidas iguais)
        server.createContext("/api/v1/auth/register", new RegisterHandler(authController));
        server.createContext("/api/v1/auth/login", new LoginHandler(authController));
        server.createContext("/api/v1/auth/forgot-password", new ForgotPasswordHandler());
        server.createContext("/api/v1/dashboard/stats", new DashboardStatsHandler(clienteController, petController, agendamentoController, produtoController, vendaController));
        server.createContext("/api/v1/clientes", new ClienteHandler(clienteController));
        server.createContext("/api/v1/pets", new PetHandler(petController));
        server.createContext("/api/v1/agendamentos", new AgendamentoHandler(agendamentoController));
        server.createContext("/api/v1/produtos", new ProdutoHandler(produtoController));
        server.createContext("/api/v1/vendas", new VendaHandler(vendaController));

        server.setExecutor(null);
        server.start();
        System.out.println("Servidor HTTP iniciado em http://localhost:" + PORT);
    }

    public void stop() {
        if (server != null) server.stop(0);
    }

    // --- Handlers ---

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String uri = exchange.getRequestURI().getPath();
            
            // Se for raiz, serve o login.html (ou index.html se preferir)
            if (uri.equals("/")) {
                uri = "/login.html";
            }
            
            if (uri.contains("..")) { sendResponse(exchange, 403, "Forbidden"); return; }

            File file = new File(FRONTEND_PATH + uri);
            
            if (file.exists() && !file.isDirectory()) {
                String contentType = "text/plain";
                if (uri.endsWith(".html")) contentType = "text/html";
                else if (uri.endsWith(".css")) contentType = "text/css";
                else if (uri.endsWith(".js")) contentType = "application/javascript";
                else if (uri.endsWith(".png")) contentType = "image/png";
                else if (uri.endsWith(".jpg") || uri.endsWith(".jpeg")) contentType = "image/jpeg";
                
                // Adiciona cabeçalhos CORS para permitir que o front (se rodar separado) acesse a API, 
                // embora aqui estejamos servindo tudo junto.
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().set("Content-Type", contentType);
                
                exchange.sendResponseHeaders(200, file.length());
                try (OutputStream os = exchange.getResponseBody()) { Files.copy(file.toPath(), os); }
            } else {
                System.err.println("Arquivo não encontrado: " + file.getAbsolutePath());
                sendResponse(exchange, 404, "404 (Not Found)");
            }
        }
    }

    static class RegisterHandler implements HttpHandler {
        private final AuthController controller;
        public RegisterHandler(AuthController controller) { this.controller = controller; }
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                try {
                    Map<String, String> json = parseJson(readRequestBody(exchange.getRequestBody()));
                    if (controller != null) {
                        controller.register(json.get("nome"), json.get("username"), json.get("password"), "CLIENTE");
                        sendResponse(exchange, 201, "{\"message\":\"Sucesso\"}");
                    } else sendResponse(exchange, 503, "{\"error\":\"DB Offline\"}");
                } catch (DomainException e) { sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}"); }
                  catch (Exception e) { sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}"); }
            } else sendResponse(exchange, 405, "Method Not Allowed");
        }
    }

    static class LoginHandler implements HttpHandler {
        private final AuthController controller;
        public LoginHandler(AuthController controller) { this.controller = controller; }
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                try {
                    Map<String, String> json = parseJson(readRequestBody(exchange.getRequestBody()));
                    if (controller != null && controller.login(json.get("username"), json.get("password"))) {
                        String role = json.get("username").contains("admin") ? "ADMIN" : "USER";
                        sendResponse(exchange, 200, "{\"message\":\"Login OK\",\"token\":\"fake-jwt\",\"role\":\"" + role + "\"}");
                    } else sendResponse(exchange, 401, "{\"error\":\"Credenciais inválidas\"}");
                } catch (DomainException e) { sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}"); }
                  catch (Exception e) { sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}"); }
            } else sendResponse(exchange, 405, "Method Not Allowed");
        }
    }

    static class ForgotPasswordHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                try {
                    readRequestBody(exchange.getRequestBody());
                    sendResponse(exchange, 200, "{\"message\":\"Email de recuperação enviado com sucesso.\"}");
                } catch (Exception e) {
                    sendResponse(exchange, 500, "{\"error\":\"Erro ao processar solicitação.\"}");
                }
            } else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        }
    }

    static class DashboardStatsHandler implements HttpHandler {
        private final ClienteController clienteController;
        private final PetController petController;
        private final AgendamentoController agendamentoController;
        private final ProdutoController produtoController;
        private final VendaController vendaController;

        public DashboardStatsHandler(ClienteController c, PetController p, AgendamentoController a, ProdutoController pr, VendaController v) {
            this.clienteController = c; this.petController = p; this.agendamentoController = a; this.produtoController = pr; this.vendaController = v;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                try {
                    int clientes = clienteController != null ? clienteController.listAll().size() : 0;
                    int pets = petController != null ? petController.listAll().size() : 0;
                    int agendamentos = agendamentoController != null ? agendamentoController.listAll().size() : 0;
                    int produtos = produtoController != null ? produtoController.listAll().size() : 0;
                    int vendas = vendaController != null ? vendaController.listAll().size() : 0;

                    String json = String.format("{\"clientes\":%d,\"pets\":%d,\"agendamentos\":%d,\"produtos\":%d,\"vendas\":%d}",
                            clientes, pets, agendamentos, produtos, vendas);
                    sendResponse(exchange, 200, json);
                } catch (Exception e) {
                    sendResponse(exchange, 500, "{\"error\":\"Erro ao buscar estatísticas\"}");
                }
            } else sendResponse(exchange, 405, "Method Not Allowed");
        }
    }

    static class ClienteHandler implements HttpHandler {
        private final ClienteController controller;
        public ClienteHandler(ClienteController controller) { this.controller = controller; }
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if (controller == null) { sendResponse(exchange, 503, "DB Offline"); return; }
            try {
                String method = exchange.getRequestMethod();
                if ("GET".equalsIgnoreCase(method)) {
                    List<Cliente> lista = controller.listAll();
                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < lista.size(); i++) {
                        Cliente c = lista.get(i);
                        json.append(String.format("{\"id\":%d,\"nome\":\"%s\",\"email\":\"%s\",\"cpf\":\"%s\"}", c.getId(), c.getNome(), c.getEmail(), c.getCpf()));
                        if (i < lista.size() - 1) json.append(",");
                    }
                    json.append("]");
                    sendResponse(exchange, 200, json.toString());
                } else if ("POST".equalsIgnoreCase(method)) {
                    Map<String, String> json = parseJson(readRequestBody(exchange.getRequestBody()));
                    controller.create(json.get("nome"), json.get("email"), json.get("cpf"), json.get("endereco"), json.get("sexo"));
                    sendResponse(exchange, 201, "{\"message\":\"Cliente criado\"}");
                } else if ("PUT".equalsIgnoreCase(method)) {
                    Map<String, String> json = parseJson(readRequestBody(exchange.getRequestBody()));
                    Long id = Long.parseLong(json.get("id"));
                    controller.update(id, json.get("nome"), json.get("email"), json.get("endereco"), json.get("sexo"));
                    sendResponse(exchange, 200, "{\"message\":\"Cliente atualizado\"}");
                } else if ("DELETE".equalsIgnoreCase(method)) {
                    String query = exchange.getRequestURI().getQuery();
                    Long id = null;
                    if (query != null && query.contains("id=")) {
                         id = Long.parseLong(query.split("=")[1]);
                    } else {
                        String path = exchange.getRequestURI().getPath();
                        String idStr = path.substring(path.lastIndexOf('/') + 1);
                        try { id = Long.parseLong(idStr); } catch (NumberFormatException e) {}
                    }
                    
                    if (id != null) {
                        try {
                            controller.delete(id);
                            sendResponse(exchange, 200, "{\"message\":\"Cliente excluído\"}");
                        } catch (Exception e) {
                            // Captura erro de chave estrangeira (cliente tem pets/vendas)
                            if (e.getMessage().contains("foreign key constraint fails") || e.getMessage().contains("ConstraintViolation")) {
                                sendResponse(exchange, 400, "{\"error\":\"Não é possível excluir o cliente pois ele possui registros vinculados (Pets, Vendas, etc).\"}");
                            } else {
                                throw e;
                            }
                        }
                    } else {
                        sendResponse(exchange, 400, "{\"error\":\"ID inválido\"}");
                    }
                } else sendResponse(exchange, 405, "Method Not Allowed");
            } catch (DomainException e) { sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}"); }
              catch (Exception e) { 
                  e.printStackTrace(); // Loga o erro completo no console do backend
                  sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}"); 
              }
        }
    }

    static class PetHandler implements HttpHandler {
        private final PetController controller;
        public PetHandler(PetController controller) { this.controller = controller; }
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if (controller == null) { sendResponse(exchange, 503, "DB Offline"); return; }
            try {
                String method = exchange.getRequestMethod();
                if ("GET".equalsIgnoreCase(method)) {
                    List<Pet> lista = controller.listAll();
                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < lista.size(); i++) {
                        Pet p = lista.get(i);
                        String donoNome = p.getDono() != null ? p.getDono().getNome() : "Sem dono";
                        Long donoId = p.getDono() != null ? p.getDono().getId() : null;
                        json.append(String.format("{\"id\":%d,\"nome\":\"%s\",\"especie\":\"%s\",\"raca\":\"%s\",\"idade\":%d,\"dono\":\"%s\",\"donoId\":%d,\"sexo\":\"%s\",\"observacoes\":\"%s\"}", 
                                p.getId(), p.getNome(), p.getEspecie(), p.getRaca(), p.getIdade(), donoNome, donoId, p.getSexo(), p.getObservacoes()));
                        if (i < lista.size() - 1) json.append(",");
                    }
                    json.append("]");
                    sendResponse(exchange, 200, json.toString());
                } else if ("POST".equalsIgnoreCase(method)) {
                    Map<String, String> json = parseJson(readRequestBody(exchange.getRequestBody()));
                    String donoIdStr = json.get("donoId");
                    if (donoIdStr == null) donoIdStr = json.get("dono_id");
                    if (donoIdStr == null) donoIdStr = json.get("clienteId");
                    if (donoIdStr == null && json.containsKey("cliente")) {
                        String clienteObj = json.get("cliente");
                        int idIndex = clienteObj.indexOf("id");
                        if (idIndex != -1) {
                            donoIdStr = clienteObj.replaceAll("[^0-9]", ""); 
                        }
                    }
                    if (donoIdStr == null || donoIdStr.isEmpty()) throw new IllegalArgumentException("ID do dono obrigatório");
                    
                    controller.create(json.get("nome"), json.get("especie"), json.get("raca"), 
                            Integer.parseInt(json.get("idade")), json.get("sexo"), json.get("observacoes"), Long.parseLong(donoIdStr));
                    sendResponse(exchange, 201, "{\"message\":\"Pet criado\"}");
                } else if ("PUT".equalsIgnoreCase(method)) {
                    Map<String, String> json = parseJson(readRequestBody(exchange.getRequestBody()));
                    Long id = Long.parseLong(json.get("id"));
                    controller.update(id, json.get("nome"), json.get("especie"), json.get("raca"), 
                            Integer.parseInt(json.get("idade")), json.get("sexo"), json.get("observacoes"));
                    sendResponse(exchange, 200, "{\"message\":\"Pet atualizado\"}");
                } else if ("DELETE".equalsIgnoreCase(method)) {
                    String query = exchange.getRequestURI().getQuery();
                    Long id = null;
                    if (query != null && query.contains("id=")) {
                         id = Long.parseLong(query.split("=")[1]);
                    } else {
                        String path = exchange.getRequestURI().getPath();
                        String idStr = path.substring(path.lastIndexOf('/') + 1);
                        try { id = Long.parseLong(idStr); } catch (NumberFormatException e) {}
                    }
                    
                    if (id != null) {
                        controller.delete(id);
                        sendResponse(exchange, 200, "{\"message\":\"Pet excluído\"}");
                    } else {
                        sendResponse(exchange, 400, "{\"error\":\"ID inválido\"}");
                    }
                } else sendResponse(exchange, 405, "Method Not Allowed");
            } catch (DomainException e) { sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}"); }
              catch (Exception e) { sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}"); }
        }
    }

    static class AgendamentoHandler implements HttpHandler {
        private final AgendamentoController controller;
        public AgendamentoHandler(AgendamentoController controller) { this.controller = controller; }
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if (controller == null) { sendResponse(exchange, 503, "DB Offline"); return; }
            try {
                String method = exchange.getRequestMethod();
                if ("GET".equalsIgnoreCase(method)) {
                    var lista = controller.listAll();
                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < lista.size(); i++) {
                        var a = lista.get(i);
                        String petNome = a.getPet() != null ? a.getPet().getNome() : "N/A";
                        json.append(String.format("{\"id\":%d,\"pet\":\"%s\",\"petNome\":\"%s\",\"servico\":\"%s\",\"data\":\"%s\",\"status\":\"%s\"}", 
                                a.getId(), petNome, petNome, a.getServico(), a.getDataHora().toString(), a.getStatus()));
                        if (i < lista.size() - 1) json.append(",");
                    }
                    json.append("]");
                    sendResponse(exchange, 200, json.toString());
                } else if ("POST".equalsIgnoreCase(method)) {
                    Map<String, String> json = parseJson(readRequestBody(exchange.getRequestBody()));
                    controller.create(Long.parseLong(json.get("clienteId")), Long.parseLong(json.get("petId")), 
                            json.get("servico"), LocalDateTime.parse(json.get("dataHora")), json.get("observacoes"));
                    sendResponse(exchange, 201, "{\"message\":\"Agendamento criado\"}");
                } else if ("DELETE".equalsIgnoreCase(method)) {
                    String query = exchange.getRequestURI().getQuery();
                    Long id = null;
                    if (query != null && query.contains("id=")) {
                         id = Long.parseLong(query.split("=")[1]);
                    } else {
                        String path = exchange.getRequestURI().getPath();
                        String idStr = path.substring(path.lastIndexOf('/') + 1);
                        try { id = Long.parseLong(idStr); } catch (NumberFormatException e) {}
                    }
                    
                    if (id != null) {
                        controller.cancel(id);
                        sendResponse(exchange, 200, "{\"message\":\"Agendamento cancelado\"}");
                    } else {
                        sendResponse(exchange, 400, "{\"error\":\"ID inválido\"}");
                    }
                } else sendResponse(exchange, 405, "Method Not Allowed");
            } catch (DomainException e) { sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}"); }
              catch (Exception e) { sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}"); }
        }
    }

    static class ProdutoHandler implements HttpHandler {
        private final ProdutoController controller;
        public ProdutoHandler(ProdutoController controller) { this.controller = controller; }
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if (controller == null) { sendResponse(exchange, 503, "DB Offline"); return; }
            try {
                String method = exchange.getRequestMethod();
                if ("GET".equalsIgnoreCase(method)) {
                    List<Produto> lista = controller.listAll();
                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < lista.size(); i++) {
                        Produto p = lista.get(i);
                        json.append(String.format("{\"id\":%d,\"nome\":\"%s\",\"categoria\":\"%s\",\"preco\":%s,\"estoque\":%d,\"quantidadeEstoque\":%d,\"descricao\":\"%s\"}", 
                                p.getId(), p.getNome(), p.getCategoria(), p.getPreco(), p.getQuantidadeEstoque(), p.getQuantidadeEstoque(), p.getDescricao()));
                        if (i < lista.size() - 1) json.append(",");
                    }
                    json.append("]");
                    sendResponse(exchange, 200, json.toString());
                } else if ("POST".equalsIgnoreCase(method)) {
                    Map<String, String> json = parseJson(readRequestBody(exchange.getRequestBody()));
                    controller.create(json.get("nome"), json.get("categoria"), new BigDecimal(json.get("preco")), 
                            Integer.parseInt(json.get("quantidadeEstoque")), json.get("descricao"));
                    sendResponse(exchange, 201, "{\"message\":\"Produto criado\"}");
                } else if ("PUT".equalsIgnoreCase(method)) {
                    Map<String, String> json = parseJson(readRequestBody(exchange.getRequestBody()));
                    Long id = Long.parseLong(json.get("id"));
                    controller.update(id, json.get("nome"), json.get("categoria"), new BigDecimal(json.get("preco")), 
                            Integer.parseInt(json.get("quantidadeEstoque")), json.get("descricao"));
                    sendResponse(exchange, 200, "{\"message\":\"Produto atualizado\"}");
                } else if ("DELETE".equalsIgnoreCase(method)) {
                    String query = exchange.getRequestURI().getQuery();
                    Long id = null;
                    if (query != null && query.contains("id=")) {
                         id = Long.parseLong(query.split("=")[1]);
                    } else {
                        String path = exchange.getRequestURI().getPath();
                        String idStr = path.substring(path.lastIndexOf('/') + 1);
                        try { id = Long.parseLong(idStr); } catch (NumberFormatException e) {}
                    }
                    
                    if (id != null) {
                        controller.delete(id);
                        sendResponse(exchange, 200, "{\"message\":\"Produto excluído\"}");
                    } else {
                        sendResponse(exchange, 400, "{\"error\":\"ID inválido\"}");
                    }
                } else sendResponse(exchange, 405, "Method Not Allowed");
            } catch (DomainException e) { sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}"); }
              catch (Exception e) { sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}"); }
        }
    }

    static class VendaHandler implements HttpHandler {
        private final VendaController controller;
        public VendaHandler(VendaController controller) { this.controller = controller; }
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if (controller == null) { sendResponse(exchange, 503, "DB Offline"); return; }
            try {
                String method = exchange.getRequestMethod();
                if ("GET".equalsIgnoreCase(method)) {
                    List<Venda> lista = controller.listAll();
                    StringBuilder json = new StringBuilder("[");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    for (int i = 0; i < lista.size(); i++) {
                        Venda v = lista.get(i);
                        String clienteNome = v.getCliente() != null ? v.getCliente().getNome() : "N/A";
                        json.append(String.format("{\"id\":%d,\"cliente\":\"%s\",\"clienteNome\":\"%s\",\"data\":\"%s\",\"total\":%s,\"status\":\"%s\"}", 
                                v.getId(), clienteNome, clienteNome, v.getDataHora().format(formatter), v.getValorTotal(), v.getStatus()));
                        if (i < lista.size() - 1) json.append(",");
                    }
                    json.append("]");
                    sendResponse(exchange, 200, json.toString());
                } else if ("POST".equalsIgnoreCase(method)) {
                    String body = readRequestBody(exchange.getRequestBody());

                    Long clienteId = extractIdFromNested(body, "cliente");
                    if (clienteId == null) clienteId = extractLong(body, "clienteId");

                    Map<Long, Integer> itens = new HashMap<>();
                    
                    int itensStart = body.indexOf("\"itens\":");
                    if (itensStart != -1) {
                        int bracketStart = body.indexOf("[", itensStart);
                        int bracketEnd = body.lastIndexOf("]");
                        if (bracketStart != -1 && bracketEnd != -1) {
                            String itensArray = body.substring(bracketStart + 1, bracketEnd);
                            Pattern pattern = Pattern.compile("\\{.*?\"produto\".*?\"id\":(\\d+).*?\"quantidade\":(\\d+).*?\\}");
                            Matcher matcher = pattern.matcher(itensArray);

                            while (matcher.find()) {
                                Long prodId = Long.parseLong(matcher.group(1));
                                Integer qtd = Integer.parseInt(matcher.group(2));
                                itens.put(prodId, qtd);
                            }
                        }
                    }

                    if (clienteId != null && !itens.isEmpty()) {
                        Venda venda = controller.create(clienteId, itens);
                        controller.complete(venda.getId());
                        sendResponse(exchange, 201, "{\"message\":\"Venda realizada\"}");
                    } else {
                        sendResponse(exchange, 400, "{\"error\":\"Dados de venda inválidos. Verifique cliente e itens.\"}");
                    }
                } else sendResponse(exchange, 405, "Method Not Allowed");
            } catch (DomainException e) { sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}"); }
              catch (Exception e) { sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}"); }
        }
        
        private Long extractIdFromNested(String json, String key) {
            int keyIdx = json.indexOf("\"" + key + "\"");
            if (keyIdx == -1) return null;
            int openBrace = json.indexOf("{", keyIdx);
            if (openBrace == -1) return null;
            int idIdx = json.indexOf("\"id\"", openBrace);
            if (idIdx == -1) idIdx = json.indexOf("id:", openBrace);
            if (idIdx == -1) return null;
            int colonIdx = json.indexOf(":", idIdx);
            int commaIdx = json.indexOf(",", colonIdx);
            int closeBrace = json.indexOf("}", colonIdx);
            int endIdx = -1;
            if (commaIdx != -1 && closeBrace != -1) endIdx = Math.min(commaIdx, closeBrace);
            else if (commaIdx != -1) endIdx = commaIdx;
            else if (closeBrace != -1) endIdx = closeBrace;
            if (endIdx == -1) return null;
            String val = json.substring(colonIdx + 1, endIdx).trim();
            try { return Long.parseLong(val.replaceAll("[^0-9]", "")); } catch (Exception e) { return null; }
        }

        private Long extractLong(String json, String key) {
            try { String val = extractValue(json, key); return val != null ? Long.parseLong(val) : null; } catch (Exception e) { return null; }
        }
        private String extractValue(String json, String key) {
            int keyIdx = json.indexOf("\"" + key + "\"");
            if (keyIdx == -1) keyIdx = json.indexOf(key + ":");
            if (keyIdx == -1) return null;
            int colonIdx = json.indexOf(":", keyIdx);
            int commaIdx = json.indexOf(",", colonIdx);
            int braceIdx = json.indexOf("}", colonIdx);
            int endIdx = -1;
            if (commaIdx != -1 && braceIdx != -1) endIdx = Math.min(commaIdx, braceIdx);
            else if (commaIdx != -1) endIdx = commaIdx;
            else if (braceIdx != -1) endIdx = braceIdx;
            if (endIdx == -1) return null;
            String raw = json.substring(colonIdx + 1, endIdx).trim();
            return raw.replace("\"", "");
        }
    }

    // --- Utilitários ---

    private static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        addCorsHeaders(exchange);
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(responseBytes); }
    }

    private static String readRequestBody(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    private static Map<String, String> parseJson(String json) {
        Map<String, String> map = new HashMap<>();
        if (json == null || json.isEmpty()) return map;
        json = json.trim();
        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1);
            List<String> pairs = new ArrayList<>();
            int start = 0;
            boolean inQuotes = false;
            for (int i = 0; i < json.length(); i++) {
                char c = json.charAt(i);
                if (c == '\"') inQuotes = !inQuotes;
                if (c == ',' && !inQuotes) {
                    pairs.add(json.substring(start, i));
                    start = i + 1;
                }
            }
            pairs.add(json.substring(start));
            for (String pair : pairs) {
                String[] keyValue = pair.split(":", 2);
                if (keyValue.length == 2) {
                    map.put(cleanString(keyValue[0]), cleanString(keyValue[1]));
                    if (keyValue[1].trim().startsWith("{")) {
                        map.put(cleanString(keyValue[0]), keyValue[1].trim());
                    }
                }
            }
        }
        return map;
    }

    private static String cleanString(String s) {
        if (s == null) return "";
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"")) s = s.substring(1, s.length() - 1);
        return s.trim();
    }
}
