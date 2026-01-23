package com.projetointegrador.petshop.infrastructure.api.agendamento;

import com.projetointegrador.petshop.application.agendamento.AgendamentoService;
import com.projetointegrador.petshop.domain.agendamento.Agendamentos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class AgendamentoController {
    private final AgendamentoService agendamentoService;

    public AgendamentoController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }
    public Agendamentos create(Long clienteId, Long petId, String servico, LocalDateTime dataHora, String observacoes) {
        return agendamentoService.criarAgendamento(clienteId, petId, servico, dataHora, observacoes);
    }
    public List<Agendamentos> listAll() {
        return agendamentoService.listarTodos();
    }
    public Optional<Agendamentos> getById(Long id) {
        return agendamentoService.buscarPorId(id);
    }
    public void cancel(Long id) {
        agendamentoService.cancelarAgendamento(id);
    }
    public void complete(Long id) {
        agendamentoService.concluirAgendamento(id);
    }
    public void reschedule(Long id, LocalDateTime novaDataHora) {
        agendamentoService.reagendar(id, novaDataHora);
    }
}
