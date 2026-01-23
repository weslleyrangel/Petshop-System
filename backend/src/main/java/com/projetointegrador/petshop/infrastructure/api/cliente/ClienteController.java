package com.projetointegrador.petshop.infrastructure.api.cliente;

import com.projetointegrador.petshop.application.cliente.ClienteService;
import com.projetointegrador.petshop.domain.cliente.Cliente;

import java.util.List;
import java.util.Optional;

public class ClienteController {
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }
    public Cliente create(String nome, String email, String cpf, String endereco, String sexo) {
        return clienteService.cadastrarCliente(nome, email, cpf, endereco, sexo);
    }
    public List<Cliente> listAll() {
        return clienteService.listarTodos();
    }
    public Optional<Cliente> getById(Long id) {
        return clienteService.buscarPorId(id);
    }
    public Cliente update(Long id, String nome, String email, String endereco, String sexo) {
        return clienteService.atualizarCliente(id, nome, email, endereco, sexo);
    }
    public void delete(Long id) {
        clienteService.deletarCliente(id);
    }
}
