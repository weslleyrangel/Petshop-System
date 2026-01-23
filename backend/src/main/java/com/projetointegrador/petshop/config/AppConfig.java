package com.projetointegrador.petshop.config;

import com.projetointegrador.petshop.application.agendamento.AgendamentoService;
import com.projetointegrador.petshop.application.auth.AuthService;
import com.projetointegrador.petshop.application.auth.UserService;
import com.projetointegrador.petshop.application.cliente.ClienteService;
import com.projetointegrador.petshop.application.pet.PetService;
import com.projetointegrador.petshop.application.produto.ProdutoService;
import com.projetointegrador.petshop.application.venda.VendaService;
import com.projetointegrador.petshop.domain.agendamento.AgendamentoRepository;
import com.projetointegrador.petshop.domain.auth.User;
import com.projetointegrador.petshop.domain.auth.UserRepository;
import com.projetointegrador.petshop.domain.cliente.ClienteRepository;
import com.projetointegrador.petshop.domain.pet.PetRepository;
import com.projetointegrador.petshop.domain.produto.ProdutoRepository;
import com.projetointegrador.petshop.domain.vendas.VendaRepository;
import com.projetointegrador.petshop.infrastructure.auth.SimplePasswordEncoder;
import com.projetointegrador.petshop.infrastructure.persistence.jdbc.*;

public class AppConfig {

    // Repositories
    private final ClienteRepository clienteRepository;
    private final PetRepository petRepository;
    private final ProdutoRepository produtoRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final UserRepository userRepository;
    private final VendaRepository vendaRepository;

    // Services
    private final ClienteService clienteService;
    private final PetService petService;
    private final ProdutoService produtoService;
    private final AgendamentoService agendamentoService;
    private final UserService userService;
    private final AuthService authService;
    private final VendaService vendaService;

    public AppConfig() {
        // 0. Testar Conexão com Banco de Dados (Opcional, apenas para log)
        DatabaseConfig.testConnection();

        // 1. Instanciar Repositórios (JDBC)
        this.clienteRepository = new JdbcClienteRepository();
        this.petRepository = new JdbcPetRepository(clienteRepository);
        this.produtoRepository = new JdbcProdutoRepository();
        this.agendamentoRepository = new JdbcAgendamentoRepository(clienteRepository, petRepository);
        this.userRepository = new JdbcUserRepository();
        this.vendaRepository = new JdbcVendaRepository(clienteRepository, produtoRepository);

        // 2. Instanciar Dependências de Infraestrutura
        User.PasswordEncoder passwordEncoder = new SimplePasswordEncoder();

        // 3. Instanciar Serviços (Injeção de Dependência)
        this.clienteService = new ClienteService(clienteRepository);
        this.petService = new PetService(petRepository, clienteRepository);
        this.produtoService = new ProdutoService(produtoRepository);
        this.agendamentoService = new AgendamentoService(agendamentoRepository, clienteRepository, petRepository);
        this.userService = new UserService(userRepository, passwordEncoder);
        this.authService = new AuthService(userService, passwordEncoder);
        this.vendaService = new VendaService(vendaRepository, clienteRepository, produtoRepository);
    }

    // Getters para os serviços
    public ClienteService getClienteService() { return clienteService; }
    public PetService getPetService() { return petService; }
    public ProdutoService getProdutoService() { return produtoService; }
    public AgendamentoService getAgendamentoService() { return agendamentoService; }
    public UserService getUserService() { return userService; }
    public AuthService getAuthService() { return authService; }
    public VendaService getVendaService() { return vendaService; }
}
