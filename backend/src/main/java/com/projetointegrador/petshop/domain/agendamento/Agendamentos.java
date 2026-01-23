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
        validate();
    }

    public Agendamentos(Cliente cliente, Pet pet, String servico, LocalDateTime dataHora, String observacoes){
        this(null, cliente, pet, servico, dataHora, AgendamentoStatus.AGENDADO, observacoes );
    }

    private void validate(){
        if(cliente == null || pet == null){
            throw new DomainException("Agendamento deve ter Cliente e Pet.");
        }
        if(servico == null || servico.trim().isEmpty()){
            throw new DomainException("O serviço agendado é obrigatório.");
        }
        // Removida validação de data passada para permitir lançamentos retroativos
        // if(dataHora != null && dataHora.isBefore(LocalDateTime.now().minusMinutes(5))){
        //    throw new DomainException("Não é possível agendar para uma data ou hora passada.");
        // }
    }

    public void cancelar(){
        if(this.status == AgendamentoStatus.CONCLUIDO){
            throw new DomainException("Não é possível cancelar um agendamento já concluido.");
        }
        this.status = AgendamentoStatus.CANCELADO;
    }

    public void concluir() {
        if (this.status != AgendamentoStatus.AGENDADO) {
            throw new DomainException("Apenas agendamentos pendentes podem ser concluídos.");
        }
        this.status = AgendamentoStatus.CONCLUIDO;
    }

    public void reagendar(LocalDateTime novaDataHora) {
        if (this.status != AgendamentoStatus.AGENDADO) {
            throw new DomainException("Apenas agendamentos pendentes podem ser reagendados.");
        }
        // Removida validação de data passada no reagendamento também
        // if (novaDataHora.isBefore(LocalDateTime.now().minusMinutes(5))) {
        //    throw new DomainException("Não é possível reagendar para uma data ou hora passada.");
        // }
        this.dataHora = novaDataHora;
    }

    public void atualizarDados(String servico, String observacoes) {
        if (this.status != AgendamentoStatus.AGENDADO) {
            throw new DomainException("Não é possível alterar dados de um agendamento que não está pendente.");
        }
        this.servico = servico;
        this.observacoes = observacoes;
        validate();
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
}
