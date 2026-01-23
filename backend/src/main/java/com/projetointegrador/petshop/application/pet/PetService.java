package com.projetointegrador.petshop.application.pet;

import com.projetointegrador.petshop.domain.cliente.Cliente;
import com.projetointegrador.petshop.domain.cliente.ClienteRepository;
import com.projetointegrador.petshop.domain.exception.DomainException;
import com.projetointegrador.petshop.domain.pet.Pet;
import com.projetointegrador.petshop.domain.pet.PetRepository;

import java.util.List;
import java.util.Optional;

public class PetService {
    private final PetRepository petRepository;
    private final ClienteRepository clienteRepository;

    public PetService(PetRepository petRepository, ClienteRepository clienteRepository) {
        this.petRepository = petRepository;
        this.clienteRepository = clienteRepository;
    }

    public Pet cadastrarPet(String nome, String especie, String raca, int idade, String sexo, String observacoes, Long donoId) {
        Cliente dono = clienteRepository.findById(donoId)
                .orElseThrow(() -> new DomainException("Dono não encontrado com ID: " + donoId));
        
        Pet novoPet = new Pet(nome, especie, raca, idade, sexo, observacoes, dono);
        return petRepository.save(novoPet);
    }

    public List<Pet> listarTodos() {
        return petRepository.findAll();
    }

    public Optional<Pet> buscarPorId(Long id) {
        return petRepository.findById(id);
    }

    public Pet atualizarPet(Long id, String nome, String especie, String raca, int idade, String sexo, String observacoes) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new DomainException("Pet não encontrado com ID: " + id));
        
        pet.atualizarDados(nome, especie, raca, idade, sexo, observacoes);
        return petRepository.save(pet);
    }

    public void deletarPet(Long id) {
        if (!petRepository.existsById(id)) {
            throw new DomainException("Pet não encontrado com ID: " + id);
        }
        petRepository.deleteById(id);
    }
}
