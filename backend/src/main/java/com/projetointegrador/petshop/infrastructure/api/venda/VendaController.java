package com.projetointegrador.petshop.infrastructure.api.venda;

import com.projetointegrador.petshop.application.venda.VendaService;
import com.projetointegrador.petshop.domain.vendas.Venda;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VendaController {
    private final VendaService vendaService;

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    public Venda create(Long clienteId, Map<Long, Integer> produtosQuantidade) {
        return vendaService.iniciarVenda(clienteId, produtosQuantidade);
    }

    public void complete(Long id) {
        vendaService.concluirVenda(id);
    }

    public void cancel(Long id) {
        vendaService.cancelarVenda(id);
    }

    public List<Venda> listAll() {
        return vendaService.listarTodas();
    }

    public Optional<Venda> getById(Long id) {
        return vendaService.buscarPorId(id);
    }
}
