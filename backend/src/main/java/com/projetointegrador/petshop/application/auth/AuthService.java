package com.projetointegrador.petshop.application.auth;

import com.projetointegrador.petshop.domain.auth.User;

public class AuthService {

    private final UserService userService;

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public boolean autenticar(String username, String password) {
        try {
            User user = userService.buscarPorUsername(username);
            // Verifica se a senha corresponde (em produção, recomenda-se usar hash)
            return user.getPassword().equals(password);
        } catch (RuntimeException e) {
            // Usuário não encontrado
            return false;
        }
    }
}
