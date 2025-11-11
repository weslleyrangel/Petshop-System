package com.projetointegrador.petshop.domain.vendas;

import com.projetointegrador.petshop.domain.cliente.Cliente;
import com.projetointegrador.petshop.domain.exception.DomainException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Venda {
    private Long id;
    private Cliente cliente;
    private LocalDateTime dataHora;
    private VendaStatus status;
    private List<ItemVenda> itens;


    public Venda(Long id, Cliente cliente, LocalDateTime dataHora, VendaStatus status, List<ItemVenda> itens) {
        this.id = id;
        this.cliente = cliente;
        this.dataHora = dataHora;
        this.status = status;
        this.itens = itens;
        validate();
    }

    public Venda(Cliente cliente, List<ItemVenda> itens){
        this(null, cliente, LocalDateTime.now(), VendaStatus.PENDENTE, itens);
    }

    private void validate(){
        if(cliente == null){
            throw new DomainException("A venda deve ter um cliente associado.");
        }
        if(itens == null || itens.isEmpty()){
            throw new DomainException("A venda deve conter pelo menos um item.");
        }
        if(itens.stream().anyMatch(item -> item.getQuantidade() <= 0)){
            throw new DomainException("Todos os itens de venda devem ter quantidade positiva.");
        }
    }

    public BigDecimal getValoTotal(){
        return itens.stream().map(ItemVenda::getSubTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void concluirVenda(){
        if(this.status != VendaStatus.PENDENTE){
            throw new DomainException("A venda só pode ser concluida se estiver PENDENTE.");
        }
        this.status = VendaStatus.CONCLUIDA;
    }

    public Long getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public VendaStatus getStatus() {
        return status;
    }

    public List<ItemVenda> getItens() {
        return itens;
    }
}
