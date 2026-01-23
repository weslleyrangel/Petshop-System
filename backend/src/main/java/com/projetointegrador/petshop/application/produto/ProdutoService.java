package com.projetointegrador.petshop.application.produto;

import com.projetointegrador.petshop.domain.exception.DomainException;
import com.projetointegrador.petshop.domain.produto.Produto;
import com.projetointegrador.petshop.domain.produto.ProdutoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Produto cadastrarProduto(String nome, String categoria, BigDecimal preco, int quantidadeEstoque, String descricao) {
        Produto produto = new Produto(nome, categoria, preco, quantidadeEstoque, descricao);
        return produtoRepository.save(produto);
    }

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    public Produto atualizarProduto(Long id, String nome, String categoria, BigDecimal preco, String descricao) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new DomainException("Produto não encontrado com ID: " + id));
        
        produto.atualizarDados(nome, categoria, preco, descricao);
        return produtoRepository.save(produto);
    }

    public void adicionarEstoque(Long id, int quantidade) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new DomainException("Produto não encontrado com ID: " + id));
        produto.incrementarEstoque(quantidade);
        produtoRepository.save(produto);
    }

    public void decrementarEstoque(Long id, int quantidade) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new DomainException("Produto não encontrado com ID: " + id));
        produto.decrementarEstoque(quantidade);
        produtoRepository.save(produto);
    }

    public void deletarProduto(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new DomainException("Produto não encontrado com ID: " + id);
        }
        produtoRepository.deleteById(id);
    }
}
