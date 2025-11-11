package com.projetointegrador.petshop.domain.pet;

import com.projetointegrador.petshop.domain.cliente.Cliente;
import com.projetointegrador.petshop.domain.exception.DomainException;

public class Pet {
    private Long id;
    private String nome;
    private String especie;
    private String raca;
    private int idade;
    private String sexo;
    private String observacoes;

    private Cliente dono;

    public Pet(Long id, String nome, String especie, String raca, int idade, String sexo, String observacoes, Cliente dono) {
        this.id = id;
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.idade = idade;
        this.sexo = sexo;
        this.observacoes = observacoes;
        this.dono = dono;
        validade();
    }

    public Pet(String nome, String especie, String raca, int idade, String sexo, String obeservacoes, Cliente dono){
        this(null, nome, especie, raca, idade, sexo, obeservacoes, dono);
    }

    private void validade(){
        if(nome == null || nome.trim().isEmpty()){
            throw new DomainException("O nome do pet é obrigatório.");
        }
        if(dono == null){
            throw new DomainException("O pet deve estar associado a um dono.");
        }
        if(idade < 0){
            throw new DomainException("A idade do pet não pode ser negativa.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEspecie() {
        return especie;
    }

    public String getRaca() {
        return raca;
    }

    public int getIdade() {
        return idade;
    }

    public String getSexo() {
        return sexo;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public Cliente getDono() {
        return dono;
    }

    public void setDono(Cliente novoDono){
        if(novoDono == null){
            throw new DomainException("O novo dono não pode ser nulo.");
        }
        this.dono = novoDono;
    }
}
