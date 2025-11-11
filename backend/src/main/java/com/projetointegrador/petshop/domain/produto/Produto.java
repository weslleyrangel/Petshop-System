package com.projetointegrador.petshop.domain.produto;

import com.projetointegrador.petshop.domain.exception.DomainException;

import java.math.BigDecimal;

public class Produto {
    private Long id;
    private String nome;
    private String categoria;
    private BigDecimal preco;
    private int quantidadeEstoque;
    private String descricao;

    public Produto(Long id, String nome, String categoria, BigDecimal preco, int quantidadeEstoque, String descricao) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
        this.descricao = descricao;
        validate();
    }

    public Produto(String nome, String categoria, BigDecimal preco, int quantidadeEstoque, String descricao) {
        this(null, nome, categoria, preco,quantidadeEstoque, descricao);
    }

    private void validate(){
        if(nome == null || nome.trim().isEmpty()){
            throw new DomainException("O nome do produto é obrigatório.");
        }
        if(preco == null || preco.compareTo(BigDecimal.ZERO) <= 0){
            throw new DomainException("O preço deve ser maior que zero.");
        }
        if(quantidadeEstoque < 0){
            throw new DomainException("A quantidade em estoque não pode ser negativo.");
        }
    }
    public void decrementarEstoque(int quantidade){
        if(quantidade <= 0){
            throw new DomainException("A quantidade do estoque deve ser positiva.");
        }
        if(this.quantidadeEstoque < quantidade){
            throw new DomainException("Estoque insuficiente para a venda. Disponivel: " + this.quantidadeEstoque);
        }
        this.quantidadeEstoque -= quantidade;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setPreco( BigDecimal preco){
        if(preco == null || preco.compareTo(BigDecimal.ZERO) <= 0){
            throw new DomainException("O preço não pode ser negativo ou nulo na edição.");
        }
        this.preco = preco;
    }
}
