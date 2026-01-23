package com.projetointegrador.petshop.infrastructure.persistence.jdbc;

import com.projetointegrador.petshop.domain.cliente.Cliente;
import com.projetointegrador.petshop.domain.cliente.ClienteRepository;
import com.projetointegrador.petshop.domain.pet.Pet;
import com.projetointegrador.petshop.domain.pet.PetRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcPetRepository implements PetRepository {

    private final ClienteRepository clienteRepository;

    public JdbcPetRepository(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public Pet save(Pet pet) {
        if (pet.getId() == null) {
            return insert(pet);
        } else {
            return update(pet);
        }
    }

    private Pet insert(Pet pet) {
        String sql = "INSERT INTO pet (nome, especie, raca, idade, sexo, observacoes, dono_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, pet.getNome());
            stmt.setString(2, pet.getEspecie());
            stmt.setString(3, pet.getRaca());
            stmt.setInt(4, pet.getIdade());
            stmt.setString(5, pet.getSexo());
            stmt.setString(6, pet.getObservacoes());
            stmt.setLong(7, pet.getDono().getId());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Pet(generatedKeys.getLong(1), pet.getNome(), pet.getEspecie(), pet.getRaca(), pet.getIdade(), pet.getSexo(), pet.getObservacoes(), pet.getDono());
                } else {
                    throw new SQLException("Falha ao criar pet, nenhum ID obtido.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar pet", e);
        }
    }

    private Pet update(Pet pet) {
        String sql = "UPDATE pet SET nome = ?, especie = ?, raca = ?, idade = ?, sexo = ?, observacoes = ?, dono_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, pet.getNome());
            stmt.setString(2, pet.getEspecie());
            stmt.setString(3, pet.getRaca());
            stmt.setInt(4, pet.getIdade());
            stmt.setString(5, pet.getSexo());
            stmt.setString(6, pet.getObservacoes());
            stmt.setLong(7, pet.getDono().getId());
            stmt.setLong(8, pet.getId());
            
            stmt.executeUpdate();
            return pet;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar pet", e);
        }
    }

    @Override
    public Optional<Pet> findById(Long id) {
        String sql = "SELECT * FROM pet WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToPet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pet por ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Pet> findAll() {
        List<Pet> pets = new ArrayList<>();
        String sql = "SELECT * FROM pet";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                pets.add(mapRowToPet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pets", e);
        }
        return pets;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM pet WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar pet", e);
        }
    }

    @Override
    public List<Pet> findByClienteId(Long clienteId) {
        List<Pet> pets = new ArrayList<>();
        String sql = "SELECT * FROM pet WHERE dono_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pets.add(mapRowToPet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pets por cliente", e);
        }
        return pets;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT count(*) FROM pet WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar existência de pet", e);
        }
        return false;
    }

    private Pet mapRowToPet(ResultSet rs) throws SQLException {
        Long donoId = rs.getLong("dono_id");
        Long petId = rs.getLong("id");

        Cliente dono = clienteRepository.findById(donoId)
                .orElseThrow(() -> new SQLException("Dono não encontrado para o pet ID: " + petId));

        return new Pet(
                petId,
                rs.getString("nome"),
                rs.getString("especie"),
                rs.getString("raca"),
                rs.getInt("idade"),
                rs.getString("sexo"),
                rs.getString("observacoes"),
                dono
        );
    }
}
