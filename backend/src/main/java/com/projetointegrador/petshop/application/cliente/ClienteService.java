package com.projetointegrador.petshop.application.cliente;

import com.projetointegrador.petshop.domain.cliente.Cliente;
import com.projetointegrador.petshop.domain.cliente.ClienteRepository;
import com.projetointegrador.petshop.domain.exception.DomainException;

import java.util.List;
import java.util.Optional;

public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository){
        this.clienteRepository = clienteRepository;
    }

    public Cliente cadastrarCliente(String nome, String email, String cpf, String endereco, String sexo){
        clienteRepository.findByCpf(cpf).ifPresent(clienteExistente -> {
            throw new DomainException("CPF já cadastrado: " + cpf);
        });

        Cliente novoCliente = new Cliente(nome, email, cpf, endereco, sexo);
        return clienteRepository.save(novoCliente);
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente atualizarCliente(Long id, String nome, String email, String endereco, String sexo) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new DomainException("Cliente não encontrado com ID: " + id));
        
        cliente.atualizarDados(nome, email, endereco, sexo);
        return clienteRepository.save(cliente);
    }

    public void deletarCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new DomainException("Cliente não encontrado com ID: " + id);
        }
        clienteRepository.deleteById(id);
    }
}
