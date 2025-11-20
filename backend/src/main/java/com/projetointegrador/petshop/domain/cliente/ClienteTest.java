package com.projetointegrador.petshop.domain.cliente;

import com.projetointegrador.petshop.domain.exception.DomainException;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClienteTest {

    private final String NOME_VALIDO = "Weslley Rangel";
    private final String EMAIL_VALIDO = "weslleyrangel@test.com";
    private final String CPF_VALIDO = "12345678901";
    private final String ENDERECO_VALIDO = "Rua Lagoas, 132";
    private final String SEXO_VALIDO = "Masculino";


    @Test
    public void givenValidInputs_whenCreatingCliente_thenShouldBeCreatedSuccessfully(){
        Cliente cliente = new Cliente(NOME_VALIDO,EMAIL_VALIDO, CPF_VALIDO, ENDERECO_VALIDO, SEXO_VALIDO);


        assertNotNull(cliente);
        assertEquals(NOME_VALIDO, cliente.getNome());
        assertEquals(EMAIL_VALIDO, cliente.getEmail());
        assertEquals(CPF_VALIDO, cliente.getCpf());
        assertEquals(ENDERECO_VALIDO, cliente.getEndereco());
        assertEquals(SEXO_VALIDO, cliente.getSexo());
        assertNull("O ID deve ser nulo antes de ser salvo pelo repositório.", cliente.getId());
    }

    @Test(expected = DomainException.class)
    public void givenNullName_whenCreatingCliente_thenShouldThrowDomainException(){
        new Cliente(null, EMAIL_VALIDO, CPF_VALIDO, ENDERECO_VALIDO, SEXO_VALIDO);
    }

    @Test(expected = DomainException.class)
    public void givenEmptyName_whenCreatingCliente_thenShouldThrowDomainException(){
        new Cliente("", EMAIL_VALIDO, CPF_VALIDO, ENDERECO_VALIDO, SEXO_VALIDO);
    }

    @Test(expected = DomainException.class)
    public void givenInvalidCpf_whenCreatingCliente_thenShouldThrowDomainException(){
        new Cliente(NOME_VALIDO, EMAIL_VALIDO, "123", ENDERECO_VALIDO, SEXO_VALIDO);
    }

    @Test(expected = DomainException.class)
    public void givenNullCpf_whenCreatingCliente_thenShouldThrowDomainException(){
        new Cliente(NOME_VALIDO, EMAIL_VALIDO, null, ENDERECO_VALIDO, SEXO_VALIDO);
    }

    @Test(expected = DomainException.class)
    public void givenInvalidEmail_whenCreatingCliente_thenShouldThrowDomainException(){
        new Cliente(NOME_VALIDO,"Emailinvalido", CPF_VALIDO, ENDERECO_VALIDO, SEXO_VALIDO);
    }

    @Test(expected = DomainException.class)
    public void givenNullEmail_whenCreatingCliente_thenShouldThrowDomainException(){
        new Cliente(NOME_VALIDO, null, CPF_VALIDO, ENDERECO_VALIDO, SEXO_VALIDO);
    }

    @Test
    public void whenUpdatingEndereco_thenShouldBeUpdated(){
        Cliente cliente = new Cliente(NOME_VALIDO, EMAIL_VALIDO, CPF_VALIDO,ENDERECO_VALIDO, SEXO_VALIDO);
        String novoEndereco = "Rua Nevaldo Rocha. 456";

        cliente.atualizarEndereco(novoEndereco);

        assertEquals(novoEndereco, cliente.getEndereco());
    }

}
