# Sistema de Gestão de Petshop

Este é um projeto de backend para um sistema de gestão de petshop, desenvolvido em Java puro, sem o uso de frameworks como Spring. Ele utiliza JDBC para comunicação com um banco de dados MySQL e expõe uma API REST através de um servidor HTTP embutido.

## Tecnologias Utilizadas

- **Backend:**
  - Java 17
  - Maven (para gerenciamento de dependências)
  - JDBC (para conexão com o banco de dados)
  - Servidor HTTP embutido do Java (`com.sun.net.httpserver`)
- **Banco de Dados:**
  - MySQL
- **Frontend:**
  - HTML5, CSS3, JavaScript (Puro)

## Pré-requisitos

Antes de começar, você precisará ter instalado em sua máquina:
- JDK 17 ou superior
- Apache Maven
- Servidor MySQL (ex: MySQL Community Server, XAMPP, WAMP)

## Configuração e Instalação

Siga os passos abaixo para configurar e rodar o projeto.

### 1. Configuração do Banco de Dados

1.  **Inicie seu servidor MySQL.**
2.  **Crie o banco de dados e as tabelas:**
    - Abra um cliente MySQL (MySQL Workbench, DBeaver, etc.).
    - Execute o script contido no arquivo `backend/schema.sql`. Isso criará o banco de dados `petshop` e todas as tabelas necessárias.
3.  **(Opcional) Popule o banco com dados de teste:**
    - Após criar as tabelas, execute o script do arquivo `backend/data.sql` para inserir dados de exemplo (clientes, pets, produtos, etc.).
4.  **Verifique as Credenciais:**
    - Abra o arquivo `backend/src/main/java/com/projetointegrador/petshop/infrastructure/persistence/jdbc/DatabaseConfig.java`.
    - Verifique se as constantes `USER` e `PASSWORD` correspondem às credenciais do seu servidor MySQL. O padrão é `root` e senha vazia.

### 2. Build do Projeto

Navegue até a pasta `backend` do projeto e execute o comando Maven para compilar e baixar as dependências:

```bash
mvn clean install
```

## Executando a Aplicação

1.  **Inicie o Servidor Backend:**
    - Abra o projeto em sua IDE (IntelliJ, Eclipse, etc.).
    - Encontre a classe `PetshopApplication.java` em `backend/src/main/java/com/projetointegrador/petshop/`.
    - Execute o método `main`.
2.  **Acesse o Frontend:**
    - O console do Java deverá exibir a mensagem "Servidor HTTP iniciado em http://localhost:8080".
    - A aplicação tentará abrir automaticamente o endereço `http://localhost:8080/login.html` no seu navegador padrão.
    - Se não abrir, copie e cole o link manualmente.

### Credenciais de Teste

Se você executou o script `data.sql`, pode usar as seguintes credenciais para testar:
- **Usuário Admin:**
  - **Email:** `admin@petshop.com`
  - **Senha:** `123456`
- **Usuário Comum:**
  - **Email:** `atendente@petshop.com`
  - **Senha:** `123456`

## Estrutura do Projeto

O projeto segue uma arquitetura em camadas para separar as responsabilidades:

- `domain`: Contém as entidades de negócio (ex: `Pet`, `Cliente`), regras de negócio e as interfaces dos repositórios. É o núcleo do sistema.
- `application`: A camada de serviço, que orquestra as operações e a lógica de domínio.
- `infrastructure`: Contém as implementações concretas de tecnologias externas:
  - `api`: Os "Controllers" que definem as operações da API.
  - `persistence`: A implementação da persistência de dados (JDBC).
  - `web`: O servidor HTTP embutido e os handlers que conectam a API ao mundo externo.
  - `auth`: Implementação do encoder de senha.
- `config`: A classe `AppConfig` que realiza a injeção de dependência manual, conectando todas as camadas.
