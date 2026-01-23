# Sistema de Gestão de Petshop

## Status do Projeto
> ✅ **Concluído**

## Tecnologias Utilizadas
As tecnologias aplicadas neste projeto são:
- **Backend:**
  - Java 17
  - Maven
  - JDBC
  - Servidor HTTP embutido do Java (`com.sun.net.httpserver`)
- **Banco de Dados:**
  - MySQL
- **Frontend:**
  - HTML5, CSS3, JavaScript (Puro)

## Time de Desenvolvedores
- Weslley Rangel

## Objetivo do Software
O objetivo deste software é fornecer uma solução para a gestão eficiente de um petshop. Ele visa facilitar o controle de clientes, animais de estimação, produtos e serviços, oferecendo uma interface amigável e uma API robusta para suportar as operações diárias do estabelecimento.

## Funcionalidades do Sistema (Requisitos)
O sistema conta com as seguintes funcionalidades principais:
- [x] Cadastro e gestão de Clientes
- [x] Cadastro e gestão de Pets
- [x] Gestão de Produtos e Serviços
- [x] Autenticação de usuários (Login)

---

## Pré-requisitos
Antes de começar, você precisará ter instalado em sua máquina:
- JDK 17 ou superior
- Apache Maven
- Servidor MySQL (ex: MySQL Community Server, XAMPP, WAMP)

## Configuração e Instalação

### 1. Configuração do Banco de Dados
1.  **Inicie seu servidor MySQL.**
2.  **Crie o banco de dados e as tabelas:**
    - Abra um cliente MySQL (MySQL Workbench, DBeaver, etc.).
    - Execute o script contido no arquivo `backend/schema.sql`. Isso criará o banco de dados `petshop` e todas as tabelas necessárias.
3.  **(Opcional) Popule o banco com dados de teste:**
    - Após criar as tabelas, execute o script do arquivo `backend/data.sql` para inserir dados de exemplo.
4.  **Verifique as Credenciais:**
    - Abra o arquivo `backend/src/main/java/com/projetointegrador/petshop/infrastructure/persistence/jdbc/DatabaseConfig.java`.
    - Verifique se as constantes `USER` e `PASSWORD` correspondem às credenciais do seu servidor MySQL.

### 2. Build do Projeto
Navegue até a pasta `backend` do projeto e execute o comando Maven:
```bash
mvn clean install
```

## Executando a Aplicação
1.  **Inicie o Servidor Backend:**
    - Execute a classe `PetshopApplication.java` em `backend/src/main/java/com/projetointegrador/petshop/`.
2.  **Acesse o Frontend:**
    - O console exibirá "Servidor HTTP iniciado em http://localhost:8080".
    - Acesse `http://localhost:8080/login.html`.

### Credenciais de Teste (se usar data.sql)
- **Admin:** `admin@petshop.com` / `123456`
- **Atendente:** `atendente@petshop.com` / `123456`

## Estrutura do Projeto
- `domain`: Entidades e regras de negócio.
- `application`: Camada de serviço.
- `infrastructure`: Implementações concretas (API, JDBC, Web).
- `config`: Injeção de dependência manual.
