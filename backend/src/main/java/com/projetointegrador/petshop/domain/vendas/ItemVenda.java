package com.projetointegrador.petshop.domain.vendas;

import com.projetointegrador.petshop.domain.exception.DomainException;
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
        validate();
    }

    public ItemVenda(Produto produto, int quantidade){
        this(null, produto, quantidade, produto.getPreco());
    }

    private void validate() {
        if (produto == null) {
            throw new DomainException("O item de venda deve ter um produto associado.");
        }
        if (quantidade <= 0) {
            throw new DomainException("A quantidade do item deve ser positiva.");
        }
        if (precoUnitario == null || precoUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("O preço unitário não pode ser negativo.");
        }
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
