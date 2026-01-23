package com.projetointegrador.petshop.application.auth;

import com.projetointegrador.petshop.domain.auth.User;
import com.projetointegrador.petshop.domain.auth.UserRepository;
import com.projetointegrador.petshop.domain.exception.DomainException;

public class UserService {

    private final UserRepository userRepository;
    private final User.PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, User.PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User buscarPorUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new DomainException("Usuário não encontrado: " + username));
    }

    public User cadastrarUsuario(String nome, String username, String rawPassword, String role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new DomainException("Nome de usuário já existe");
        }
        
        // Aqui assumimos que o encoder faz o hash da senha
        String hashedPassword = passwordEncoder.encode(rawPassword);
        
        User user = new User(nome, username, hashedPassword, role);
        return userRepository.save(user);
    }
    
    public void alterarSenha(Long userId, String novaSenhaRaw) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException("Usuário não encontrado com ID: " + userId));
        
        user.changePassword(novaSenhaRaw, passwordEncoder);
        userRepository.save(user);
    }
}
