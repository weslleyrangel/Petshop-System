package com.projetointegrador.petshop.infrastructure.api.pet;

import com.projetointegrador.petshop.application.pet.PetService;
import com.projetointegrador.petshop.domain.pet.Pet;

import java.util.List;
import java.util.Optional;

public class PetController {
    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }
    public Pet create(String nome, String especie, String raca, int idade, String sexo, String observacoes, Long donoId) {
        return petService.cadastrarPet(nome, especie, raca, idade, sexo, observacoes, donoId);
    }
    public List<Pet> listAll() {
        return petService.listarTodos();
    }
    public Optional<Pet> getById(Long id) {
        return petService.buscarPorId(id);
    }
    public Pet update(Long id, String nome, String especie, String raca, int idade, String sexo, String observacoes) {
        return petService.atualizarPet(id, nome, especie, raca, idade, sexo, observacoes);
    }
    public void delete(Long id) {
        petService.deletarPet(id);
    }
}
