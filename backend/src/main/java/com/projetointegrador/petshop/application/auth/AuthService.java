package com.projetointegrador.petshop.application.auth;

import com.projetointegrador.petshop.domain.auth.User;
import com.projetointegrador.petshop.domain.exception.DomainException;

public class AuthService {

    private final UserService userService;
    private final User.PasswordEncoder passwordEncoder;

    public AuthService(UserService userService, User.PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean autenticar(String username, String rawPassword) {
        try {
            User user = userService.buscarPorUsername(username);
            return user.checkPassword(rawPassword, passwordEncoder);
        } catch (DomainException e) {
            // Usuário não encontrado
            return false;
        }
    }
}
