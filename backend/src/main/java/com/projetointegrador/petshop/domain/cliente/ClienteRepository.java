package com.projetointegrador.petshop.domain.cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository {
    Cliente save(Cliente cliente);
    Optional<Cliente> findById(Long id);
    List<Cliente> findAll();
    void deleteById(Long id);
    Optional<Cliente> findByCpf(String cpf);
}
