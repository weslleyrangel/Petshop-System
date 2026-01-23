package com.projetointegrador.petshop.infrastructure.api.produto;

import com.projetointegrador.petshop.application.produto.ProdutoService;
import com.projetointegrador.petshop.domain.produto.Produto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ProdutoController {
    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    public Produto create(String nome, String categoria, BigDecimal preco, int quantidadeEstoque, String descricao) {
        return produtoService.cadastrarProduto(nome, categoria, preco, quantidadeEstoque, descricao);
    }

    public List<Produto> listAll() {
        return produtoService.listarTodos();
    }

    public Optional<Produto> getById(Long id) {
        return produtoService.buscarPorId(id);
    }

    public Produto update(Long id, String nome, String categoria, BigDecimal preco, String descricao) {
        return produtoService.atualizarProduto(id, nome, categoria, preco, descricao);
    }

    public void addStock(Long id, int quantidade) {
        produtoService.adicionarEstoque(id, quantidade);
    }

    public void removeStock(Long id, int quantidade) {
        produtoService.decrementarEstoque(id, quantidade);
    }

    public void delete(Long id) {
        produtoService.deletarProduto(id);
    }
}
