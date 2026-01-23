-- Script para Popular o Banco de Dados (Dados de Teste)
USE petshop;

-- 1. Inserir Usuários (Senha '123456' hash SHA-256 simulado ou texto plano para teste simples)
-- Nota: Se estiver usando SimplePasswordEncoder com SHA-256, a senha '123456' gera: 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI='
INSERT INTO users (nome, username, password, role) VALUES 
('Administrador', 'admin@petshop.com', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'ADMIN'),
('Atendente', 'atendente@petshop.com', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'USER');

-- 2. Inserir Clientes
INSERT INTO cliente (nome, email, cpf, endereco, sexo) VALUES 
('João Silva', 'joao@email.com', '12345678901', 'Rua das Flores, 123', 'M'),
('Maria Oliveira', 'maria@email.com', '98765432100', 'Av. Paulista, 1000', 'F'),
('Carlos Pereira', 'carlos@email.com', '45678912300', 'Rua Augusta, 500', 'M');

-- 3. Inserir Pets
-- IDs dos donos assumidos: 1 (João), 2 (Maria), 3 (Carlos)
INSERT INTO pet (nome, especie, raca, idade, sexo, observacoes, dono_id) VALUES 
('Rex', 'Cachorro', 'Labrador', 5, 'M', 'Dócil e brincalhão', 1),
('Mimi', 'Gato', 'Siamês', 3, 'F', 'Arisca com estranhos', 2),
('Thor', 'Cachorro', 'Bulldog', 2, 'M', 'Alérgico a frango', 1),
('Luna', 'Gato', 'Persa', 4, 'F', 'Precisa escovar pelos diariamente', 3);

-- 4. Inserir Produtos
INSERT INTO produto (nome, categoria, preco, quantidade_estoque, descricao) VALUES 
('Ração Premium 15kg', 'Alimentação', 189.90, 50, 'Ração completa para cães adultos'),
('Shampoo Pet Neutro', 'Higiene', 25.50, 100, 'Shampoo suave para cães e gatos'),
('Brinquedo Mordedor', 'Brinquedos', 15.00, 200, 'Mordedor de borracha resistente'),
('Coleira Ajustável', 'Acessórios', 35.00, 80, 'Coleira de nylon com fecho seguro'),
('Areia Sanitária 4kg', 'Higiene', 18.00, 60, 'Areia com alto poder de absorção');

-- 5. Inserir Agendamentos
-- IDs: Cliente 1/Pet 1, Cliente 2/Pet 2
INSERT INTO agendamento (cliente_id, pet_id, servico, data_hora, status, observacoes) VALUES 
(1, 1, 'Banho e Tosa', '2023-12-01 14:00:00', 'CONCLUIDO', 'Cortar unhas também'),
(2, 2, 'Consulta Veterinária', '2023-12-02 10:30:00', 'AGENDADO', 'Vacinação anual'),
(1, 3, 'Banho Simples', '2023-12-03 09:00:00', 'CANCELADO', 'Cliente desmarcou'),
(3, 4, 'Tosa Higiênica', '2023-12-05 15:00:00', 'AGENDADO', NULL);

-- 6. Inserir Vendas
INSERT INTO venda (cliente_id, data_hora, status) VALUES 
(1, '2023-11-20 10:00:00', 'CONCLUIDA'),
(2, '2023-11-21 16:30:00', 'CONCLUIDA'),
(3, '2023-11-22 11:15:00', 'PENDENTE');

-- 7. Inserir Itens da Venda
-- Venda 1 (Cliente 1): Comprou Ração (ID 1) e Shampoo (ID 2)
INSERT INTO item_venda (venda_id, produto_id, quantidade, preco_unitario) VALUES 
(1, 1, 1, 189.90),
(1, 2, 2, 25.50);

-- Venda 2 (Cliente 2): Comprou Areia (ID 5) e Brinquedo (ID 3)
INSERT INTO item_venda (venda_id, produto_id, quantidade, preco_unitario) VALUES 
(2, 5, 3, 18.00),
(2, 3, 1, 15.00);

-- Venda 3 (Cliente 3): Comprou Coleira (ID 4)
INSERT INTO item_venda (venda_id, produto_id, quantidade, preco_unitario) VALUES 
(3, 4, 1, 35.00);

SELECT 'Banco de dados populado com sucesso!' AS Status;
