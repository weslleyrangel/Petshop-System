import { fetchData, postData } from '../utils/api.js';

let listaClientes = [];

export async function render(container) {
    const userRole = localStorage.getItem('role'); // Pega a role do usuário

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
                    <th>CPF</th>
                    <th>Ações</th>
                </tr>
                </thead>
                <tbody id="tabela-clientes">
                    <tr><td colspan="5">Carregando...</td></tr>
                </tbody>
            </table>
        </div>
    `;

    document.getElementById('btn-novo-cliente').addEventListener('click', () => {
        renderFormulario(container);
    });

    try {
        listaClientes = await fetchData('/api/v1/clientes');
        const tabela = document.getElementById('tabela-clientes');

        if (listaClientes && listaClientes.length > 0) {
            tabela.innerHTML = listaClientes.map(cliente => `
                <tr>
                    <td>${cliente.id}</td>
                    <td>${cliente.nome}</td>
                    <td>${cliente.email}</td>
                    <td>${cliente.cpf}</td>
                    <td class="actions-cell">
                        <button class="btn-icon btn-edit" data-id="${cliente.id}" title="Editar">
                            <i class="fas fa-edit"></i>
                        </button>
                        ${userRole === 'ADMIN' ? `
                        <button class="btn-icon btn-delete" data-id="${cliente.id}" title="Excluir">
                            <i class="fas fa-trash-alt"></i>
                        </button>` : ''}
                    </td>
                </tr>
            `).join('');

            // Adiciona listeners aos botões de ação
            document.querySelectorAll('.btn-edit').forEach(btn => {
                btn.addEventListener('click', () => {
                    const id = btn.getAttribute('data-id');
                    const cliente = listaClientes.find(c => c.id == id);
                    renderFormulario(container, cliente);
                });
            });

            if (userRole === 'ADMIN') {
                document.querySelectorAll('.btn-delete').forEach(btn => {
                    btn.addEventListener('click', async () => {
                        const id = btn.getAttribute('data-id');
                        if (confirm('Tem certeza que deseja excluir este cliente?')) {
                            try {
                                await postData(`/api/v1/clientes?id=${id}`, {}, 'DELETE');
                                render(container); // Recarrega a lista
                            } catch (error) {
                                alert('Erro ao excluir cliente: ' + error.message);
                            }
                        }
                    });
                });
            }

        } else {
            tabela.innerHTML = '<tr><td colspan="5">Nenhum cliente cadastrado.</td></tr>';
        }
    } catch (error) {
        document.getElementById('tabela-clientes').innerHTML = `<tr><td colspan="5">Erro ao carregar clientes.</td></tr>`;
    }
}

function renderFormulario(container, cliente = {}) {
    const isEdit = cliente.id != null;
    const pageTitle = isEdit ? 'Editar Cliente' : 'Cadastro de Cliente';

    container.innerHTML = `
        <h1 class="page-title">${pageTitle}</h1>
        <div class="form-container">
            <form id="form-cliente" class="form-layout">
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
                        <option value="M" ${cliente.sexo === 'M' ? 'selected' : ''}>Masculino</option>
                        <option value="F" ${cliente.sexo === 'F' ? 'selected' : ''}>Feminino</option>
                    </select>
                </div>
                <div class="form-actions">
                    <button type="button" id="btn-cancelar" class="btn btn-secondary">Cancelar</button>
                    <button type="submit" class="btn btn-primary">Salvar</button>
                </div>
            </form>
        </div>
    `;

    document.getElementById('btn-cancelar').addEventListener('click', () => {
        render(container);
    });

    document.getElementById('form-cliente').addEventListener('submit', async (e) => {
        e.preventDefault();
        await handleSave(container, isEdit);
    });
}

async function handleSave(container, isEdit) {
    const form = document.getElementById('form-cliente');
    const formData = new FormData(form);
    const clienteData = Object.fromEntries(formData.entries()); 

    // O backend espera PUT na mesma URL base para edição, ou POST para criação
    const method = isEdit ? 'PUT' : 'POST';

    try {
        await postData('/api/v1/clientes', clienteData, method);
        render(container);
    } catch (error) {
        console.error("Erro ao salvar cliente:", error);
        alert(`Erro ao salvar: ${error.message}`);
    }
}
