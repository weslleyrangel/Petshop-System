package com.projetointegrador.petshop.domain.agendamento;

import com.projetointegrador.petshop.domain.cliente.Cliente;
import com.projetointegrador.petshop.domain.exception.DomainException;
import com.projetointegrador.petshop.domain.pet.Pet;

import java.time.LocalDateTime;

public class Agendamentos {
    private Long id;
    private Cliente cliente;
    private Pet pet;
    private String servico;
    private LocalDateTime dataHora;
    private AgendamentoStatus status;
    private String observacoes;

    public Agendamentos(Long id, Cliente cliente, Pet pet, String servico, LocalDateTime dataHora, AgendamentoStatus status, String observacoes) {
        this.id = id;
        this.cliente = cliente;
        this.pet = pet;
        this.servico = servico;
        this.dataHora = dataHora;
        this.status = status;
        this.observacoes = observacoes;
        validade();
    }

    public Agendamentos(Cliente cliente, Pet pet, String servico, LocalDateTime dataHora, String observacoes){
        this(null, cliente, pet, servico, dataHora, AgendamentoStatus.AGENDADO, observacoes );
    }

    private void validade(){
        if(cliente == null || pet == null){
            throw new DomainException("Agendamento deve ter Cliente e Pet.");
        }
        if(servico == null || servico.trim().isEmpty()){
            throw new DomainException("O serviço agendado é obrigatório.");
        }
        if(dataHora != null && dataHora.isBefore(LocalDateTime.now().minusMinutes(5))){
            throw new DomainException("Não é possível agendar para uma data ou hora passada.");
        }
    }

    public void cancelar(){
        if(this.status == AgendamentoStatus.CONCLUIDO){
            throw new DomainException("Não é  possível cancelar um agendamento já concluido.");
        }
        this.status = AgendamentoStatus.CANCELADO;
    }

    public Long getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Pet getPet() {
        return pet;
    }

    public String getServico() {
        return servico;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public AgendamentoStatus getStatus() {
        return status;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setDataHora(LocalDateTime dataHora){
        if(dataHora.isBefore(LocalDateTime.now().minusMinutes(5))){
            throw new DomainException("Não é possível reagendar para uma data ou hora passada.");
        }
        this.dataHora = dataHora;
    }
}
