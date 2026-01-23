package com.projetointegrador.petshop.infrastructure.persistence.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    // Configurações do MySQL
    private static final String URL = "jdbc:mysql://localhost:3306/petshop?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    public static void testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("Conexão com o banco de dados MySQL estabelecida com sucesso!");
        } catch (SQLException e) {
            System.err.println("FALHA ao conectar no MySQL.");
            System.err.println("Verifique se o servidor está rodando e se o banco 'petshop' foi criado.");
            throw new RuntimeException("Erro de conexão com o banco de dados", e);
        }
    }
}
