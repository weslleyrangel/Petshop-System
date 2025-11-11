package com.projetointegrador.petshop.domain.produto;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository {
    Produto save(Produto produto);
    Optional<Produto> findById(Long id);
    List<Produto> findAll();
    void deleteById(Long id);

}
