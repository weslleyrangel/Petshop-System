package com.projetointegrador.petshop.application.agendamento;

import com.projetointegrador.petshop.domain.agendamento.Agendamentos;
import com.projetointegrador.petshop.domain.agendamento.AgendamentoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;

    public AgendamentoService(AgendamentoRepository agendamentoRepository) {
        this.agendamentoRepository = agendamentoRepository;
    }

    public Agendamentos criarAgendamento(Agendamentos agendamento) {
        return agendamentoRepository.save(agendamento);
    }

    public List<Agendamentos> listarTodos() {
        return agendamentoRepository.findAll();
    }

    public Optional<Agendamentos> buscarPorId(Long id) {
        return agendamentoRepository.findById(id);
    }

    public void cancelarAgendamento(Long id) {
        Agendamentos agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado com ID: " + id));
        agendamento.cancelar();
        agendamentoRepository.save(agendamento);
    }

    public void reagendar(Long id, LocalDateTime novaDataHora) {
        Agendamentos agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado com ID: " + id));
        agendamento.setDataHora(novaDataHora);
        agendamentoRepository.save(agendamento);
    }
}
