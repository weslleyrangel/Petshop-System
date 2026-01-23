package com.projetointegrador.petshop.application.agendamento;

import com.projetointegrador.petshop.domain.agendamento.AgendamentoRepository;
import com.projetointegrador.petshop.domain.agendamento.Agendamentos;
import com.projetointegrador.petshop.domain.cliente.Cliente;
import com.projetointegrador.petshop.domain.cliente.ClienteRepository;
import com.projetointegrador.petshop.domain.exception.DomainException;
import com.projetointegrador.petshop.domain.pet.Pet;
import com.projetointegrador.petshop.domain.pet.PetRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final ClienteRepository clienteRepository;
    private final PetRepository petRepository;

    public AgendamentoService(AgendamentoRepository agendamentoRepository, ClienteRepository clienteRepository, PetRepository petRepository) {
        this.agendamentoRepository = agendamentoRepository;
        this.clienteRepository = clienteRepository;
        this.petRepository = petRepository;
    }

    public Agendamentos criarAgendamento(Long clienteId, Long petId, String servico, LocalDateTime dataHora, String observacoes) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new DomainException("Cliente não encontrado com ID: " + clienteId));
        
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new DomainException("Pet não encontrado com ID: " + petId));

        // Validação: verificar se o pet pertence ao cliente
        if (!pet.getDono().getId().equals(cliente.getId())) {
            throw new DomainException(String.format("O pet '%s' não pertence ao cliente '%s'. Pertence a '%s'.", 
                    pet.getNome(), cliente.getNome(), pet.getDono().getNome()));
        }

        Agendamentos agendamento = new Agendamentos(cliente, pet, servico, dataHora, observacoes);
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
                .orElseThrow(() -> new DomainException("Agendamento não encontrado com ID: " + id));
        agendamento.cancelar();
        agendamentoRepository.save(agendamento);
    }

    public void concluirAgendamento(Long id) {
        Agendamentos agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new DomainException("Agendamento não encontrado com ID: " + id));
        agendamento.concluir();
        agendamentoRepository.save(agendamento);
    }

    public void reagendar(Long id, LocalDateTime novaDataHora) {
        Agendamentos agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new DomainException("Agendamento não encontrado com ID: " + id));
        agendamento.reagendar(novaDataHora);
        agendamentoRepository.save(agendamento);
    }
}
