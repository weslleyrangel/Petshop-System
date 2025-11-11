package com.projetointegrador.petshop.domain.cliente;

import com.projetointegrador.petshop.domain.exception.DomainException;

public class Cliente {
    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private String endereco;
    private String sexo;


    public Cliente(Long id, String nome, String email, String cpf, String endereco, String sexo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
        this.endereco = endereco;
        this.sexo = sexo;
        validate();
    }

    public Cliente(String nome, String email, String cpf, String endereco, String sexo){
        this(null, nome, email, cpf, endereco, sexo );
    }

    // regras de negócio
    private void validate(){
        if(nome == null || nome.trim().isEmpty()){
            throw new DomainException("O nome do cliente é obrigatório.");
        }
        if(!isValidCpf(cpf)){
            throw new DomainException("CPF inválido.");
        }
        if(!isValidEmail(email)){
            throw new DomainException("Email inválido.");
        }
    }

    private boolean isValidCpf(String cpf){
        if(cpf == null) return false;
        String digits = cpf.replaceAll("[^0-9]", "");
        return digits.length() == 11;
    }

    private boolean isValidEmail(String email){
        return email != null && email.contains("@") && email.contains(".");
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getSexo() {
        return sexo;
    }

    public void atualizarEndereco(String novoEndereco){
        this.endereco = novoEndereco;
    }
}
