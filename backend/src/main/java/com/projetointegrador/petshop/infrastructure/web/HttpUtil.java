package com.projetointegrador.petshop.infrastructure.web;

import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpUtil {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json; charset=UTF-8";

    public static void sendResponse(HttpExchange exchange, int statusCode, Object responseBody) throws IOException {
        String jsonResponse = (responseBody instanceof String) ? (String) responseBody : JsonUtil.toJson(responseBody);
        
        addCorsHeaders(exchange);
        exchange.getResponseHeaders().set(CONTENT_TYPE, APPLICATION_JSON);
        
        byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    public static void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        // Formato padrão de erro: {"error": "mensagem"}
        sendResponse(exchange, statusCode, Map.of("error", message));
    }

    public static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    public static String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }
    
    public static Long getQueryParamId(HttpExchange exchange) {
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.contains("id=")) {
            try {
                return Long.parseLong(query.split("=")[1]);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                return null;
            }
        }
        return null;
    }
}
