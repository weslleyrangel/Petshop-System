package com.projetointegrador.petshop.application.auth;

import com.projetointegrador.petshop.domain.auth.User;
import com.projetointegrador.petshop.domain.auth.UserRepository;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User buscarPorUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + username));
    }

    public User cadastrarUsuario(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Nome de usuário já existe");
        }
        return userRepository.save(user);
    }
}