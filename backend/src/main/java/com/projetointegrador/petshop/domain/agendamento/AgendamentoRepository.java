package com.projetointegrador.petshop.domain.agendamento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AgendamentoRepository {
    Agendamentos save(Agendamentos agendamentos);
    Optional<Agendamentos> findById(Long id);
    List<Agendamentos> findAll();
    List<Agendamentos> findBetweenDates(LocalDateTime start, LocalDateTime end);
}
