import { fetchData, postData } from '../utils/api.js';

// Variável para "cachear" os dados e evitar buscas repetidas
let listaClientes = [];

/**
 * Renderiza a página de Clientes (Lista).
 * @param {HTMLElement} container - O elemento <main> onde o conteúdo será injetado.
 */
export async function render(container) {
    // 1. Define o HTML da lista
    container.innerHTML = `
        <div class="page-header">
            <h1 class="page-title">Lista de Clientes</h1>
            <button id="btn-novo-cliente" class="btn btn-primary">Cadastrar Novo Cliente</button>
        </div>

        <div class="table-container">
            <table class="content-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Nome</th>
                    <th>Email</th>
                    <th>Telefone</th>
                    <th>CPF</th>
                </tr>
                </thead>
                <tbody id="tabela-clientes">
                    <tr><td colspan="5">Carregando...</td></tr>
                </tbody>
            </table>
        </div>
    `;

    // 2. Adiciona o listener para o botão "Cadastrar"
    document.getElementById('btn-novo-cliente').addEventListener('click', () => {
        renderFormulario(container); // Chama a função que renderiza o formulário
    });

    // 3. Busca e preenche os dados da tabela
    try {
        listaClientes = await fetchData('/api/v1/clientes');
        const tabela = document.getElementById('tabela-clientes');

        if (listaClientes && listaClientes.length > 0) {
            tabela.innerHTML = listaClientes.map(cliente => `
                <tr>
                    <td>${cliente.id}</td>
                    <td>${cliente.nome}</td>
                    <td>${cliente.email}</td>
                    <td>${cliente.telefone}</td>
                    <td>${cliente.cpf}</td>
                    <!-- Adicionar botões de Editar/Excluir aqui se necessário -->
                </tr>
            `).join('');
        } else {
            tabela.innerHTML = '<tr><td colspan="5">Nenhum cliente cadastrado.</td></tr>';
        }
    } catch (error) {
        document.getElementById('tabela-clientes').innerHTML = `<tr><td colspan="5">Erro ao carregar clientes.</td></tr>`;
    }
}

/**
 * Renderiza o Formulário de Cadastro/Edição de Cliente.
 * @param {HTMLElement} container - O elemento <main>.
 * @param {object} cliente - Opcional. O objeto do cliente para edição.
 */
function renderFormulario(container, cliente = {}) {
    const isEdit = cliente.id != null;
    const pageTitle = isEdit ? 'Editar Cliente' : 'Cadastro de Cliente';

    container.innerHTML = `
        <h1 class="page-title">${pageTitle}</h1>
        <div class="form-container">
            <form id="form-cliente" class="form-layout">
                <!-- Campo oculto para ID em caso de edição -->
                <input type="hidden" id="id" name="id" value="${cliente.id || ''}">

                <div class="form-group">
                    <label for="nome" class="form-label">Nome Completo:</label>
                    <input type="text" id="nome" name="nome" class="form-input" value="${cliente.nome || ''}" required />
                </div>
                <div class="form-group">
                    <label for="email" class="form-label">E-mail:</label>
                    <input type="email" id="email" name="email" class="form-input" value="${cliente.email || ''}" required />
                </div>
                <div class="form-group">
                    <label for="telefone" class="form-label">Telefone:</label>
                    <input type="text" id="telefone" name="telefone" class="form-input" value="${cliente.telefone || ''}" />
                </div>
                <div class="form-group">
                    <label for="cpf" class="form-label">CPF:</label>
                    <input type="text" id="cpf" name="cpf" class="form-input" value="${cliente.cpf || ''}" required />
                </div>
                <div class="form-group">
                    <label for="endereco" class="form-label">Endereço:</label>
                    <input type="text" id="endereco" name="endereco" class="form-input" value="${cliente.endereco || ''}" />
                </div>
                <div class="form-group">
                    <label for="sexo" class="form-label">Sexo:</label>
                    <select id="sexo" name="sexo" class="form-select">
                        <option value="Masculino" ${cliente.sexo === 'Masculino' ? 'selected' : ''}>Masculino</option>
                        <option value="Feminino" ${cliente.sexo === 'Feminino' ? 'selected' : ''}>Feminino</option>
                        <option value="Nao Informar" ${cliente.sexo === 'Nao Informar' ? 'selected' : ''}>Não Informar</option>
                    </select>
                </div>
                <div class="form-actions">
                    <button type="button" id="btn-cancelar" class="btn btn-secondary">Cancelar</button>
                    <button type="submit" class="btn btn-primary">Salvar</button>
                </div>
            </form>
        </div>
    `;

    // Adiciona os listeners aos botões do formulário
    document.getElementById('btn-cancelar').addEventListener('click', () => {
        render(container); // Volta para a lista
    });

    document.getElementById('form-cliente').addEventListener('submit', async (e) => {
        e.preventDefault();
        await handleSave(container, isEdit);
    });
}

/**
 * Lida com o salvamento (POST ou PUT) do formulário de cliente.
 * @param {HTMLElement} container - O elemento <main>.
 * @param {boolean} isEdit - Define se é uma atualização (PUT) ou criação (POST).
 */
async function handleSave(container, isEdit) {
    const form = document.getElementById('form-cliente');
    const formData = new FormData(form);
    const clienteData = Object.fromEntries(formData.entries()); 

    // Define o endpoint e o método
    const endpoint = isEdit ? `/api/v1/clientes/${clienteData.id}` : '/api/v1/clientes';
    const method = isEdit ? 'PUT' : 'POST';

    try {
        await postData(endpoint, clienteData, method);
        render(container); // Sucesso! Volta para a lista de clientes.
    } catch (error) {
        console.error("Erro ao salvar cliente:", error);
        alert(`Erro ao salvar: ${error.message}`); // Exibe um alerta simples
    }
}
