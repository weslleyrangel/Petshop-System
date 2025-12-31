package com.projetointegrador.petshop.application.cliente;

import com.projetointegrador.petshop.domain.cliente.Cliente;
import com.projetointegrador.petshop.domain.cliente.ClienteRepository;
import com.projetointegrador.petshop.domain.exception.DomainException;
import org.springframework.stereotype.Service;

public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository){
        this.clienteRepository = clienteRepository;
    }

    public Cliente cadastrarCliente(String nome, String email, String cpf, String endereco, String sexo){
        Cliente novoCliente = new Cliente(nome, email, cpf, endereco, sexo);

        clienteRepository.findByCpf(cpf).ifPresent(clienteExistente -> {
            throw new DomainException("CPF já cadastrado: " + cpf);
        });

        return clienteRepository.save(novoCliente);
    }
}
