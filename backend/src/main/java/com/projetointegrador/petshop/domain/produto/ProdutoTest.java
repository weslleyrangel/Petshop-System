package com.projetointegrador.petshop.domain.produto;

import com.projetointegrador.petshop.domain.exception.DomainException;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class ProdutoTest {

    private final String NOME_VALIDO = "Ração para Cães";
    private final String CATEGORIA_VALIDA = "Alimentos";
    private final BigDecimal PRECO_VALIDO = new BigDecimal("50.00");
    private final int ESTOQUE_VALIDO = 100;
    private final String DESCRICAO_VALIDA = "Ração premium para cães adultos.";

    @Test
    public void givenValidInputs_whenCreatingProduto_thenShouldBeCreatedSuccessfully() {
        Produto produto = new Produto(NOME_VALIDO, CATEGORIA_VALIDA, PRECO_VALIDO, ESTOQUE_VALIDO, DESCRICAO_VALIDA);

        assertNotNull(produto);
        assertEquals(NOME_VALIDO, produto.getNome());
        assertEquals(PRECO_VALIDO, produto.getPreco());
        assertEquals(ESTOQUE_VALIDO, produto.getQuantidadeEstoque());
        assertNull("O ID deve ser nulo antes de ser salvo", produto.getId());
    }

    @Test(expected = DomainException.class)
    public void givenNullName_whenCreatingProduto_thenShouldThrowDomainException() {
        new Produto(null, CATEGORIA_VALIDA, PRECO_VALIDO, ESTOQUE_VALIDO, DESCRICAO_VALIDA);
    }

    @Test(expected = DomainException.class)
    public void givenZeroPrice_whenCreatingProduto_thenShouldThrowDomainException() {
        new Produto(NOME_VALIDO, CATEGORIA_VALIDA, BigDecimal.ZERO, ESTOQUE_VALIDO, DESCRICAO_VALIDA);
    }

    @Test(expected = DomainException.class)
    public void givenNegativePrice_whenCreatingProduto_thenShouldThrowDomainException() {
        new Produto(NOME_VALIDO, CATEGORIA_VALIDA, new BigDecimal("-10.00"), ESTOQUE_VALIDO, DESCRICAO_VALIDA);
    }

    @Test(expected = DomainException.class)
    public void givenNegativeStock_whenCreatingProduto_thenShouldThrowDomainException() {
        new Produto(NOME_VALIDO, CATEGORIA_VALIDA, PRECO_VALIDO, -1, DESCRICAO_VALIDA);
    }

    @Test
    public void whenDecrementingStock_thenShouldUpdateQuantity() {
        Produto produto = new Produto(NOME_VALIDO, CATEGORIA_VALIDA, PRECO_VALIDO, 20, DESCRICAO_VALIDA);
        produto.decrementarEstoque(5);
        assertEquals(15, produto.getQuantidadeEstoque());
    }

    @Test(expected = DomainException.class)
    public void givenInsufficientStock_whenDecrementing_thenShouldThrowDomainException() {
        Produto produto = new Produto(NOME_VALIDO, CATEGORIA_VALIDA, PRECO_VALIDO, 5, DESCRICAO_VALIDA);
        produto.decrementarEstoque(10);
    }

    @Test(expected = DomainException.class)
    public void givenNonPositiveDecrement_whenDecrementingStock_thenShouldThrowDomainException() {
        Produto produto = new Produto(NOME_VALIDO, CATEGORIA_VALIDA, PRECO_VALIDO, 20, DESCRICAO_VALIDA);
        produto.decrementarEstoque(0);
    }

    @Test
    public void whenSettingPrice_thenShouldUpdatePrice() {
        Produto produto = new Produto(NOME_VALIDO, CATEGORIA_VALIDA, PRECO_VALIDO, ESTOQUE_VALIDO, DESCRICAO_VALIDA);
        BigDecimal novoPreco = new BigDecimal("55.50");
        produto.setPreco(novoPreco);
        assertEquals(novoPreco, produto.getPreco());
    }

    @Test(expected = DomainException.class)
    public void givenNegativePrice_whenSettingPrice_thenShouldThrowDomainException() {
        Produto produto = new Produto(NOME_VALIDO, CATEGORIA_VALIDA, PRECO_VALIDO, ESTOQUE_VALIDO, DESCRICAO_VALIDA);
        produto.setPreco(new BigDecimal("-1.00"));
    }
}
