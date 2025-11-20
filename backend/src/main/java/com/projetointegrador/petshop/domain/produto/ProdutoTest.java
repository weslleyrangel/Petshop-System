package com.projetointegrador.petshop.domain.produto;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class ProdutoTest {
    private final String NOME_VALIDO = "Ração para Cães";
    private final String CATEGORIA_VALIDA = "Alimentos";
    private final BigDecimal PRECO_VALIDO = new BigDecimal("50,00");
    private final int ESTOQUE_VALIDO = 100;
    private final String DESCRICAO_VALIDA = "Ração premium para cães adultos.";

    @Test
    public void givenValidInputs_whenCreatingProduto_thenShouldBeCreatedSucessesfully(){
        Produto produto = new Produto(NOME_VALIDO, CATEGORIA_VALIDA, PRECO_VALIDO,ESTOQUE_VALIDO, DESCRICAO_VALIDA);

        assertNotNull(produto);
        assertEquals(NOME_VALIDO, produto.getNome());
        assertEquals(PRECO_VALIDO, produto.getPreco());
        assertEquals(ESTOQUE_VALIDO, produto.getQuantidadeEstoque());
        assertNull("O ID deve ser nulo antes de ser salvo", produto.getId());
    }
}
