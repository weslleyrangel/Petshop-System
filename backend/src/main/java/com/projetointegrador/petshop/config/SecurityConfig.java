package com.projetointegrador.petshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // 1. A "Lista VIP": Permite que todos acessem os arquivos estáticos
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                        // 2. Permite que todos acessem a página de login e de cadastro
                        .requestMatchers("/login", "/cadastrar-se").permitAll()

                        // 3. Exige autenticação para qualquer outra requisição
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        // 4. Diz ao Spring onde está sua página de login
                        .loginPage("/login")

                        // 5. Para onde o usuário vai após o login sucesso
                        .defaultSuccessUrl("/", true) // Redireciona para o Dashboard (index.html)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL para acionar o logout
                        .logoutSuccessUrl("/login?logout") // Para onde vai após o logout
                        .permitAll()
                );

        return http.build();
    }
}
