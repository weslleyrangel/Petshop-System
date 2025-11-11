import { fetchData, postData, formatCurrency } from '../utils/api.js';

/**
 * Renderiza a página de Produtos (Lista).
 * @param {HTMLElement} container - O elemento <main> onde o conteúdo será injetado.
 */
export async function render(container) {
    container.innerHTML = `
        <div class="page-header">
            <h1 class="page-title">Lista de Produtos</h1>
            <button id="btn-novo-produto" class="btn btn-primary">Cadastrar Produto</button>
        </div>

        <!-- Barra de Busca (Wireframe Page 10) -->
        <form id="search-form" class="search-bar" style="margin-bottom: 24px; display: flex; gap: 8px;">
            <input type="text" id="search-input" name="query" placeholder="Pesquisar produto..." class="form-input" style="flex: 1;" />
            <button type="submit" class="btn btn-secondary" style="background-color: #343a40; color: white;">Buscar</button>
        </form>

        <div class="table-container">
            <table class="content-table">
                <thead>
                <tr>
                    <th>Código</th>
                    <th>Nome</th>
                    <th>Categoria</th>
                    <th>Preço</th>
                    <th>Estoque</th>
                </tr>
                </thead>
                <tbody id="tabela-produtos">
                    <tr><td colspan="5">Carregando...</td></tr>
                </tbody>
            </table>
        </div>
    `;

    document.getElementById('btn-novo-produto').addEventListener('click', () => {
        renderFormulario(container);
    });

    // Listener para a busca
    document.getElementById('search-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const query = document.getElementById('search-input').value;
        await carregarProdutos(query);
    });

    // Carga inicial
    await carregarProdutos();
}

/**
 * Função auxiliar para carregar e exibir produtos na tabela.
 * @param {string} query - Opcional. Termo de busca.
 */
async function carregarProdutos(query = '') {
    const tabela = document.getElementById('tabela-produtos');
    tabela.innerHTML = `<tr><td colspan="5">Carregando...</td></tr>`;

    try {
        const endpoint = query ? `/api/v1/produtos?search=${query}` : '/api/v1/produtos';
        const produtos = await fetchData(endpoint);

        if (produtos && produtos.length > 0) {
            tabela.innerHTML = produtos.map(prod => `
                <tr>
                    <td>${prod.id}</td>
                    <td>${prod.nome}</td>
                    <td>${prod.categoria || 'N/A'}</td>
                    <td>${formatCurrency(prod.preco)}</td>
                    <td>${prod.quantidadeEstoque}</td>
                </tr>
            `).join('');
        } else {
            tabela.innerHTML = '<tr><td colspan="5">Nenhum produto encontrado.</td></tr>';
        }
    } catch (error) {
        tabela.innerHTML = `<tr><td colspan="5">Erro ao carregar produtos.</td></tr>`;
    }
}


/**
 * Renderiza o Formulário de Cadastro de Produto (baseado no Wireframe Page 13).
 * @param {HTMLElement} container - O elemento <main>.
 * @param {object} produto - Opcional. O objeto do produto para edição.
 */
function renderFormulario(container, produto = {}) {
    const isEdit = produto.id != null;
    const pageTitle = isEdit ? 'Editar Produto' : 'Cadastro de Produto';

    container.innerHTML = `
        <h1 class="page-title">${pageTitle}</h1>
        <div class="form-container">
            <form id="form-produto" class="form-grid">
                <input type="hidden" name="id" value="${produto.id || ''}">

                <div class="form-group-grid">
                    <label for="nome" class="form-label">Nome do Produto:</label>
                    <input type="text" id="nome" name="nome" class="form-input" value="${produto.nome || ''}" required />
                </div>
                <div class="form-group-grid">
                    <label for="categoria" class="form-label">Categoria:</label>
                    <input type="text" id="categoria" name="categoria" class="form-input" value="${produto.categoria || ''}" />
                </div>
                <div class="form-group-grid">
                    <label for="preco" class="form-label">Preço:</label>
                    <input type="number" step="0.01" id="preco" name="preco" class="form-input" value="${produto.preco || 0.00}" required />
                </div>
                <div class="form-group-grid">
                    <label for="quantidadeEstoque" class="form-label">Estoque:</label>
                    <input type="number" id="quantidadeEstoque" name="quantidadeEstoque" class="form-input" value="${produto.quantidadeEstoque || 0}" required />
                </div>
                <div class="form-group-grid col-span-2">
                    <label for="descricao" class="form-label">Descrição:</label>
                    <textarea id="descricao" name="descricao" class="form-textarea">${produto.descricao || ''}</textarea>
                </div>

                <div class="form-actions col-span-2">
                    <button type="button" id="btn-cancelar" class="btn btn-secondary">Cancelar</button>
                    <button type="submit" class="btn btn-primary">Cadastrar</button>
                </div>
            </form>
        </div>
    `;

    document.getElementById('btn-cancelar').addEventListener('click', () => render(container));
    document.getElementById('form-produto').addEventListener('submit', async (e) => {
        e.preventDefault();
        const form = e.target;
        const formData = new FormData(form);
        const produtoData = Object.fromEntries(formData.entries());

        const endpoint = isEdit ? `/api/v1/produtos/${produtoData.id}` : '/api/v1/produtos';
        const method = isEdit ? 'PUT' : 'POST';

        try {
            await postData(endpoint, produtoData, method);
            render(container); // Sucesso! Volta para a lista
        } catch (error) {
            console.error("Erro ao salvar produto:", error);
            alert(`Erro ao salvar: ${error.message}`);
        }
    });
}
