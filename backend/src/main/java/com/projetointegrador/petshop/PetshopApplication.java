package com.projetointegrador.petshop;

import com.projetointegrador.petshop.config.AppConfig;
import com.projetointegrador.petshop.domain.cliente.Cliente;
import com.projetointegrador.petshop.domain.pet.Pet;
import com.projetointegrador.petshop.infrastructure.api.agendamento.AgendamentoController;
import com.projetointegrador.petshop.infrastructure.api.auth.AuthController;
import com.projetointegrador.petshop.infrastructure.api.cliente.ClienteController;
import com.projetointegrador.petshop.infrastructure.api.pet.PetController;
import com.projetointegrador.petshop.infrastructure.api.produto.ProdutoController;
import com.projetointegrador.petshop.infrastructure.api.venda.VendaController;
import com.projetointegrador.petshop.infrastructure.web.SimpleWebServer;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class PetshopApplication {

    public static void main(String[] args) {
        System.out.println("Inicializando Sistema Petshop...");

        AuthController authController = null;
        ClienteController clienteController = null;
        PetController petController = null;
        AgendamentoController agendamentoController = null;
        ProdutoController produtoController = null;
        VendaController vendaController = null;

        try {
            System.out.println("Tentando conectar ao banco de dados...");
            
            AppConfig app = new AppConfig();

            authController = new AuthController(app.getAuthService(), app.getUserService());
            clienteController = new ClienteController(app.getClienteService());
            petController = new PetController(app.getPetService());
            agendamentoController = new AgendamentoController(app.getAgendamentoService());
            produtoController = new ProdutoController(app.getProdutoService());
            vendaController = new VendaController(app.getVendaService());

            logDatabaseStatus(clienteController, petController, agendamentoController, produtoController, vendaController);

        } catch (Exception e) {
            System.err.println("\n=================================================================================");
            System.err.println("[AVISO] NÃO FOI POSSÍVEL CONECTAR AO BANCO DE DADOS.");
            System.err.println("Motivo: " + e.getMessage());
            System.err.println("O sistema continuará iniciando o Servidor Web para exibir o Frontend.");
            System.err.println("Nota: As funcionalidades que dependem do banco (Login, Cadastros) darão erro.");
            System.err.println("=================================================================================\n");
        }

        try {
            SimpleWebServer webServer = new SimpleWebServer(
                authController, 
                clienteController, 
                petController, 
                agendamentoController, 
                produtoController, 
                vendaController
            );
            webServer.start();

            openBrowser("http://localhost:8080/login.html");

        } catch (IOException e) {
            System.err.println("Erro fatal ao iniciar o servidor web: " + e.getMessage());
        }
    }

    private static void openBrowser(String url) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                System.err.println("Não foi possível abrir o navegador automaticamente: " + e.getMessage());
            }
        } else {
            System.out.println("Abra o seguinte link no seu navegador: " + url);
        }
    }

    private static void logDatabaseStatus(ClienteController clienteController, PetController petController, 
                                          AgendamentoController agendamentoController, ProdutoController produtoController, 
                                          VendaController vendaController) {
        System.out.println("\n========================================");
        System.out.println("STATUS DO BANCO DE DADOS (MYSQL)");
        System.out.println("========================================");

        List<Cliente> clientes = clienteController.listAll();
        System.out.println("Clientes cadastrados: " + clientes.size());

        List<Pet> pets = petController.listAll();
        System.out.println("Pets cadastrados: " + pets.size());

        System.out.println("Agendamentos: " + agendamentoController.listAll().size());
        System.out.println("Produtos: " + produtoController.listAll().size());
        System.out.println("Vendas: " + vendaController.listAll().size());
        System.out.println("========================================\n");
    }
}
