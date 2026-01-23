package com.projetointegrador.petshop.infrastructure.persistence.jdbc;

import com.projetointegrador.petshop.domain.cliente.Cliente;
import com.projetointegrador.petshop.domain.cliente.ClienteRepository;
import com.projetointegrador.petshop.domain.produto.Produto;
import com.projetointegrador.petshop.domain.produto.ProdutoRepository;
import com.projetointegrador.petshop.domain.vendas.ItemVenda;
import com.projetointegrador.petshop.domain.vendas.Venda;
import com.projetointegrador.petshop.domain.vendas.VendaRepository;
import com.projetointegrador.petshop.domain.vendas.VendaStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcVendaRepository implements VendaRepository {

    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public JdbcVendaRepository(ClienteRepository clienteRepository, ProdutoRepository produtoRepository) {
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    @Override
    public Venda save(Venda venda) {
        if (venda.getId() == null) {
            return insert(venda);
        } else {
            return update(venda);
        }
    }

    private Venda insert(Venda venda) {
        String sqlVenda = "INSERT INTO venda (cliente_id, data_hora, status) VALUES (?, ?, ?)";
        String sqlItem = "INSERT INTO item_venda (venda_id, produto_id, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            long vendaId;
            try (PreparedStatement stmt = conn.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setLong(1, venda.getCliente().getId());
                stmt.setTimestamp(2, Timestamp.valueOf(venda.getDataHora()));
                stmt.setString(3, venda.getStatus().name());
                stmt.executeUpdate();
                
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        vendaId = generatedKeys.getLong(1);
                    } else {
                        throw new SQLException("Falha ao criar venda, nenhum ID obtido.");
                    }
                }
            }

            try (PreparedStatement stmtItem = conn.prepareStatement(sqlItem)) {
                for (ItemVenda item : venda.getItens()) {
                    stmtItem.setLong(1, vendaId);
                    stmtItem.setLong(2, item.getProduto().getId());
                    stmtItem.setInt(3, item.getQuantidade());
                    stmtItem.setBigDecimal(4, item.getPrecoUnitario());
                    stmtItem.addBatch();
                }
                stmtItem.executeBatch();
            }

            conn.commit();
            
            return findByIdInternal(vendaId, conn).orElseThrow(() -> new SQLException("Erro ao recuperar venda salva"));

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException("Erro ao salvar venda: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Venda update(Venda venda) {
        String sql = "UPDATE venda SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, venda.getStatus().name());
            stmt.setLong(2, venda.getId());
            stmt.executeUpdate();
            
            return venda;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar venda", e);
        }
    }

    @Override
    public Optional<Venda> findById(Long id) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            return findByIdInternal(id, conn);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar venda por ID", e);
        }
    }

    private Optional<Venda> findByIdInternal(Long id, Connection conn) throws SQLException {
        String sqlVenda = "SELECT * FROM venda WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sqlVenda)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToVenda(rs, conn));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Venda> findAll() {
        List<Venda> vendas = new ArrayList<>();
        String sql = "SELECT * FROM venda ORDER BY data_hora DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                vendas.add(mapRowToVenda(rs, conn));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar vendas", e);
        }
        return vendas;
    }

    private Venda mapRowToVenda(ResultSet rs, Connection conn) throws SQLException {
        Long vendaId = rs.getLong("id");
        Long clienteId = rs.getLong("cliente_id");
        
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new SQLException("Cliente não encontrado para a venda ID: " + vendaId));

        List<ItemVenda> itens = getItensVenda(vendaId, conn);

        return new Venda(
                vendaId,
                cliente,
                rs.getTimestamp("data_hora").toLocalDateTime(),
                VendaStatus.valueOf(rs.getString("status")),
                itens
        );
    }

    private List<ItemVenda> getItensVenda(Long vendaId, Connection conn) throws SQLException {
        List<ItemVenda> itens = new ArrayList<>();
        String sql = "SELECT * FROM item_venda WHERE venda_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, vendaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Long itemId = rs.getLong("id");
                    Long produtoId = rs.getLong("produto_id");
                    
                    Produto produto = produtoRepository.findById(produtoId)
                            .orElseThrow(() -> new SQLException("Produto não encontrado para o item ID: " + itemId));
                    
                    itens.add(new ItemVenda(
                            itemId,
                            produto,
                            rs.getInt("quantidade"),
                            rs.getBigDecimal("preco_unitario")
                    ));
                }
            }
        }
        return itens;
    }
}
