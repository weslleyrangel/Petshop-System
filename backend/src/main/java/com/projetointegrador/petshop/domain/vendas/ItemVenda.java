package com.projetointegrador.petshop.domain.vendas;

import com.projetointegrador.petshop.domain.produto.Produto;

import java.math.BigDecimal;

public class ItemVenda {
    private Long id;
    private Produto produto;
    private int quantidade;
    private BigDecimal precoUnitario;

    public ItemVenda(Long id, Produto produto, int quantidade, BigDecimal precoUnitario) {
        this.id = id;
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public ItemVenda(Produto produto, int quantidade){
        this(null, produto, quantidade, produto.getPreco());
    }

    public BigDecimal getSubTotal(){
        return precoUnitario.multiply(new BigDecimal(quantidade));
    }

    public Long getId() {
        return id;
    }

    public Produto getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }
}
