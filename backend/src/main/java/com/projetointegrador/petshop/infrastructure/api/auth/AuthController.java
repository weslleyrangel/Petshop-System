package com.projetointegrador.petshop.infrastructure.api.auth;

import com.projetointegrador.petshop.application.auth.AuthService;
import com.projetointegrador.petshop.application.auth.UserService;
import com.projetointegrador.petshop.domain.auth.User;

public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    public boolean login(String username, String password) {
        return authService.autenticar(username, password);
    }

    public User register(String nome, String username, String password, String role) {
        return userService.cadastrarUsuario(nome, username, password, role);
    }
}
