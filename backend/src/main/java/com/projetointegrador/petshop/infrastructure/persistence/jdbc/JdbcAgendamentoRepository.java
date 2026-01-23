package com.projetointegrador.petshop.infrastructure.persistence.jdbc;

import com.projetointegrador.petshop.domain.agendamento.AgendamentoRepository;
import com.projetointegrador.petshop.domain.agendamento.AgendamentoStatus;
import com.projetointegrador.petshop.domain.agendamento.Agendamentos;
import com.projetointegrador.petshop.domain.cliente.Cliente;
import com.projetointegrador.petshop.domain.cliente.ClienteRepository;
import com.projetointegrador.petshop.domain.pet.Pet;
import com.projetointegrador.petshop.domain.pet.PetRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcAgendamentoRepository implements AgendamentoRepository {

    private final ClienteRepository clienteRepository;
    private final PetRepository petRepository;

    public JdbcAgendamentoRepository(ClienteRepository clienteRepository, PetRepository petRepository) {
        this.clienteRepository = clienteRepository;
        this.petRepository = petRepository;
    }

    @Override
    public Agendamentos save(Agendamentos agendamento) {
        if (agendamento.getId() == null) {
            return insert(agendamento);
        } else {
            return update(agendamento);
        }
    }

    private Agendamentos insert(Agendamentos agendamento) {
        String sql = "INSERT INTO agendamento (cliente_id, pet_id, servico, data_hora, status, observacoes) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, agendamento.getCliente().getId());
            stmt.setLong(2, agendamento.getPet().getId());
            stmt.setString(3, agendamento.getServico());
            stmt.setTimestamp(4, Timestamp.valueOf(agendamento.getDataHora()));
            stmt.setString(5, agendamento.getStatus().name());
            stmt.setString(6, agendamento.getObservacoes());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Agendamentos(generatedKeys.getLong(1), agendamento.getCliente(), agendamento.getPet(), agendamento.getServico(), agendamento.getDataHora(), agendamento.getStatus(), agendamento.getObservacoes());
                } else {
                    throw new SQLException("Falha ao criar agendamento, nenhum ID obtido.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar agendamento", e);
        }
    }

    private Agendamentos update(Agendamentos agendamento) {
        String sql = "UPDATE agendamento SET cliente_id = ?, pet_id = ?, servico = ?, data_hora = ?, status = ?, observacoes = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, agendamento.getCliente().getId());
            stmt.setLong(2, agendamento.getPet().getId());
            stmt.setString(3, agendamento.getServico());
            stmt.setTimestamp(4, Timestamp.valueOf(agendamento.getDataHora()));
            stmt.setString(5, agendamento.getStatus().name());
            stmt.setString(6, agendamento.getObservacoes());
            stmt.setLong(7, agendamento.getId());
            
            stmt.executeUpdate();
            return agendamento;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar agendamento", e);
        }
    }

    @Override
    public Optional<Agendamentos> findById(Long id) {
        String sql = "SELECT * FROM agendamento WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToAgendamento(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar agendamento por ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Agendamentos> findAll() {
        List<Agendamentos> agendamentos = new ArrayList<>();
        String sql = "SELECT * FROM agendamento";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                agendamentos.add(mapRowToAgendamento(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar agendamentos", e);
        }
        return agendamentos;
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM agendamento WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar agendamento", e);
        }
    }

    public boolean existsById(Long id) {
        String sql = "SELECT count(*) FROM agendamento WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar existência de agendamento", e);
        }
        return false;
    }

    @Override
    public List<Agendamentos> findBetweenDates(LocalDateTime start, LocalDateTime end) {
        List<Agendamentos> agendamentos = new ArrayList<>();
        String sql = "SELECT * FROM agendamento WHERE data_hora BETWEEN ? AND ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(start));
            stmt.setTimestamp(2, Timestamp.valueOf(end));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    agendamentos.add(mapRowToAgendamento(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar agendamentos por data", e);
        }
        return agendamentos;
    }

    private Agendamentos mapRowToAgendamento(ResultSet rs) throws SQLException {
        Long agendamentoId = rs.getLong("id");
        Long clienteId = rs.getLong("cliente_id");
        Long petId = rs.getLong("pet_id");

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new SQLException("Cliente não encontrado para o agendamento ID: " + agendamentoId));
        
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new SQLException("Pet não encontrado para o agendamento ID: " + agendamentoId));

        return new Agendamentos(
                agendamentoId,
                cliente,
                pet,
                rs.getString("servico"),
                rs.getTimestamp("data_hora").toLocalDateTime(),
                AgendamentoStatus.valueOf(rs.getString("status")),
                rs.getString("observacoes")
        );
    }
}
