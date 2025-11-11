package com.projetointegrador.petshop.domain.pet;

import java.util.List;
import java.util.Optional;

public interface PetRepository {
    Pet save(Pet pet);
    Optional<Pet> findById(Long id);
    List<Pet> findAll();
    void deleteById(Long id);
    List<Pet> findByClienteId(Long clienteId);
}
