package com.projetointegrador.petshop.application.cliente;

import com.projetointegrador.petshop.domain.cliente.Cliente;
import com.projetointegrador.petshop.domain.cliente.ClienteRepository;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ClienteServiceTest {
    private static class FakeClienteRepository implements ClienteRepository {
        private final Map<Long, Cliente> storage = new HashMap<>();
        private long sequence = 1L;

        @Override
        public Cliente save(Cliente cliente) {
            Cliente clienteSalvo;
            if (cliente.getId() == null) {
                clienteSalvo = new Cliente(sequence++, cliente.getNome(), cliente.getEmail(), cliente.getCpf(), cliente.getEndereco(), cliente.getSexo());
            } else {
                clienteSalvo = cliente;
            }
            storage.put(clienteSalvo.getId(), clienteSalvo);
            return clienteSalvo;
        }

        @Override
        public Optional<Cliente> findById(Long id) {
            return Optional.ofNullable(storage.get(id));
        }

        @Override
        public List<Cliente> findAll() {
            return new ArrayList<>(storage.values());
        }

        @Override
        public void deleteById(Long id) {
            storage.remove(id);
        }

        @Override
        public Optional<Cliente> findByCpf(String cpf) {
            return storage.values().stream()
                    .filter(c -> c.getCpf() != null && c.getCpf().equals(cpf))
                    .findFirst();
        }

        @Override
        public boolean existsById(Long id) {
            return storage.containsKey(id);
        }
    }

    @Test
    public void givenValidData_whenCadastrarCliente_thenShouldSaveAndReturnCliente() {
        FakeClienteRepository fakeRepository = new FakeClienteRepository();
        ClienteService clienteService = new ClienteService(fakeRepository);

        String nome = "Lelo Rangel";
        String email = "lelorangel@test.com";
        String cpf = "12345678901";
        String endereco = "Rua Natal, 123";
        String sexo = "Masculino";

        Cliente novoCliente = clienteService.cadastrarCliente(nome, email, cpf, endereco, sexo);

        assertNotNull(novoCliente);
        assertNotNull(novoCliente.getId());
        assertEquals(Long.valueOf(1L), novoCliente.getId());

        Optional<Cliente> clienteSalvoOpt = fakeRepository.findById(novoCliente.getId());
        assertTrue(clienteSalvoOpt.isPresent(), "O cliente deveria ter sido salvo no repositório");
        
        Cliente clienteSalvo = clienteSalvoOpt.get();
        assertEquals(nome, clienteSalvo.getNome());
        assertEquals(email, clienteSalvo.getEmail());
    }
}
