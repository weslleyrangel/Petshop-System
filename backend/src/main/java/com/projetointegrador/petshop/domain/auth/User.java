package com.projetointegrador.petshop.domain.auth;

import com.projetointegrador.petshop.domain.exception.DomainException;

public class User {
    private Long id;
    private String nome;
    private String username;
    private String hashedPassword;
    private String role;

    public User(Long id, String nome, String username, String hashedPassword, String role) {
        this.id = id;
        this.nome = nome;
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.role = role;
        validate();
    }

    public User(String nome, String username, String hashedPassword, String role){
        this(null, nome, username, hashedPassword, role);
    }

    private void validate(){
        if(username == null || !username.contains("@")){
            throw new DomainException("O username é obrigatório e deve ser válido.");
        }
        if(hashedPassword == null || hashedPassword.length() < 10){
            throw new DomainException("A senha é inválida.");
        }
    }

    public boolean checkPassword(String rawPassword, PasswordEncoder encoder){
        return encoder.matches(rawPassword, this.hashedPassword);
    }

    public void changePassword(String newRawPassword, PasswordEncoder encoder) {
        if (newRawPassword == null || newRawPassword.length() < 6) { // Ajustei para 6 pois 10 pode ser muito restritivo para raw, mas o hash tem que ser longo. O validate original checava o hash length < 10.
             // O validate original checa hashedPassword.length() < 10. BCrypt gera 60 chars.
             // Então aqui vou validar a raw password com uma regra de negócio razoável.
             throw new DomainException("A nova senha deve ter pelo menos 6 caracteres.");
        }
        this.hashedPassword = encoder.encode(newRawPassword);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getRole() {
        return role;
    }

    public interface PasswordEncoder{
        boolean matches(String rawPassword, String encodedPassword);
        String encode(String rawPassword);
    }
}
