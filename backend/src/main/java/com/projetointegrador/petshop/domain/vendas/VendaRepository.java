package com.projetointegrador.petshop.domain.vendas;

import java.util.List;
import java.util.Optional;

public interface VendaRepository {
    Venda save(Venda venda);
    Optional<Venda> findById(Long id);
    List<Venda> findAll();
}
