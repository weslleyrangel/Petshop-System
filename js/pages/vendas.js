import { fetchData, postData, formatDate, formatCurrency } from '../utils/api.js';

/**
 * Renderiza a página de Vendas (Lista).
 * @param {HTMLElement} container - O elemento <main> onde o conteúdo será injetado.
 */
export async function render(container) {
    container.innerHTML = `
        <div class="page-header">
            <h1 class="page-title">Lista de Vendas</h1>
            <button id="btn-nova-venda" class="btn btn-primary">Nova Venda</button>
        </div>
        <div class="table-container">
            <table class="content-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Cliente</th>
                    <th>Data</th>
                    <th>Total</th>
                    <th>Status</th>
                </tr>
                </thead>
                <tbody id="tabela-vendas">
                    <tr><td colspan="5">Carregando...</td></tr>
                </tbody>
            </table>
        </div>
    `;

    document.getElementById('btn-nova-venda').addEventListener('click', () => {
        renderFormulario(container);
    });

    try {
        const vendas = await fetchData('/api/v1/vendas');
        const tabela = document.getElementById('tabela-vendas');

        if (vendas && vendas.length > 0) {
            tabela.innerHTML = vendas.map(venda => `
                <tr>
                    <td>${venda.id}</td>
                    <td>${venda.cliente?.nome || 'N/A'}</td>
                    <td>${formatDate(venda.dataHora)}</td>
                    <td>${formatCurrency(venda.valorTotal)}</td>
                    <td>${venda.status || 'N/A'}</td>
                </tr>
            `).join('');
        } else {
            tabela.innerHTML = '<tr><td colspan="5">Nenhuma venda encontrada.</td></tr>';
        }
    } catch (error) {
        document.getElementById('tabela-vendas').innerHTML = `<tr><td colspan="5">Erro ao carregar vendas.</td></tr>`;
    }
}

/**
 * Renderiza o Formulário de Cadastro de Venda.
 * @param {HTMLElement} container - O elemento <main>.
 */
async function renderFormulario(container) {
    // Listas para os <select>
    let clientesOptions = '';
    let produtosOptions = ''; // A Lógica de busca de produto seria mais complexa

    try {
        const clientes = await fetchData('/api/v1/clientes');
        clientesOptions = clientes.map(c => `<option value="${c.id}">${c.nome}</option>`).join('');

        const produtos = await fetchData('/api/v1/produtos');
        produtosOptions = produtos.map(p => `<option value="${p.id}">${p.nome} - ${formatCurrency(p.preco)}</option>`).join('');

    } catch (error) {
        container.innerHTML = `<p>Erro ao carregar dados para o formulário. Tente novamente.</p>`;
        return;
    }

    container.innerHTML = `
        <h1 class="page-title">Nova Venda</h1>
        <div class="form-container">
            <form id="form-venda" class="form-layout">
                <div class="form-group">
                    <label for="cliente" class="form-label">Cliente:</label>
                    <select id="cliente" name="clienteId" class="form-select" required>
                        <option value="">Selecione um cliente</option>
                        ${clientesOptions}
                    </select>
                </div>
                <div class="form-group form-group-top">
                    <label for="itens" class="form-label">Produtos:</label>
                    <!-- NOTA: Refatorar isso para um componente de "carrinho" é o ideal -->
                    <select id="itens" name="itens" multiple class="form-select" style="min-height: 200px;" required>
                        ${produtosOptions}
                    </select>
                </div>
                <div class="form-actions">
                    <button type="button" id="btn-cancelar" class="btn btn-secondary">Cancelar</button>
                    <button type="submit" class="btn btn-primary">Finalizar Venda</button>
                </div>
            </form>
        </div>
    `;

    document.getElementById('btn-cancelar').addEventListener('click', () => render(container));
    document.getElementById('form-venda').addEventListener('submit', async (e) => {
        e.preventDefault();
        const form = e.target;
        const clienteId = form.cliente.value;

        // Pega todos os IDs dos produtos selecionados no <select multiple>
        const selectedProdutos = [...form.itens.options]
            .filter(option => option.selected)
            .map(option => ({ produto: { id: parseInt(option.value) }, quantidade: 1 })); // Quantidade fixa 1 (aqui seria a refatoração)

        if (selectedProdutos.length === 0) {
            alert("Selecione ao menos um produto.");
            return;
        }

        const vendaData = {
            cliente: { id: parseInt(clienteId) },
            itens: selectedProdutos,
            status: 'CONCLUIDA' // Define um status padrão
        };

        try {
            await postData('/api/v1/vendas', vendaData, 'POST');
            render(container); // Sucesso! Volta para a lista
        } catch (error) {
            console.error("Erro ao salvar venda:", error);
            alert(`Erro ao salvar: ${error.message}`);
        }
    });
}
