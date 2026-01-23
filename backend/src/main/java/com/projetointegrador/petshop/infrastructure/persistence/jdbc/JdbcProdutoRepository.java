package com.projetointegrador.petshop.infrastructure.persistence.jdbc;

import com.projetointegrador.petshop.domain.produto.Produto;
import com.projetointegrador.petshop.domain.produto.ProdutoRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcProdutoRepository implements ProdutoRepository {

    @Override
    public Produto save(Produto produto) {
        if (produto.getId() == null) {
            return insert(produto);
        } else {
            return update(produto);
        }
    }

    private Produto insert(Produto produto) {
        String sql = "INSERT INTO produto (nome, categoria, preco, quantidade_estoque, descricao) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getCategoria());
            stmt.setBigDecimal(3, produto.getPreco());
            stmt.setInt(4, produto.getQuantidadeEstoque());
            stmt.setString(5, produto.getDescricao());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Produto(generatedKeys.getLong(1), produto.getNome(), produto.getCategoria(), produto.getPreco(), produto.getQuantidadeEstoque(), produto.getDescricao());
                } else {
                    throw new SQLException("Falha ao criar produto, nenhum ID obtido.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar produto", e);
        }
    }

    private Produto update(Produto produto) {
        String sql = "UPDATE produto SET nome = ?, categoria = ?, preco = ?, quantidade_estoque = ?, descricao = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getCategoria());
            stmt.setBigDecimal(3, produto.getPreco());
            stmt.setInt(4, produto.getQuantidadeEstoque());
            stmt.setString(5, produto.getDescricao());
            stmt.setLong(6, produto.getId());
            
            stmt.executeUpdate();
            return produto;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar produto", e);
        }
    }

    @Override
    public Optional<Produto> findById(Long id) {
        String sql = "SELECT * FROM produto WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToProduto(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produto por ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Produto> findAll() {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produto";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                produtos.add(mapRowToProduto(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos", e);
        }
        return produtos;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM produto WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar produto", e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT count(*) FROM produto WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar existência de produto", e);
        }
        return false;
    }

    private Produto mapRowToProduto(ResultSet rs) throws SQLException {
        return new Produto(
                rs.getLong("id"),
                rs.getString("nome"),
                rs.getString("categoria"),
                rs.getBigDecimal("preco"),
                rs.getInt("quantidade_estoque"),
                rs.getString("descricao")
        );
    }
}
