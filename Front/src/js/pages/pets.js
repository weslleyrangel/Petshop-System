import { fetchData, postData } from '../utils/api.js';

let listaPets = [];
let listaClientes = [];

export async function render(container) {
    const userRole = localStorage.getItem('role');

    container.innerHTML = `
        <div class="page-header">
            <h1 class="page-title">Lista de Pets</h1>
            <button id="btn-novo-pet" class="btn btn-primary">Cadastrar Novo Pet</button>
        </div>
        <div class="table-container">
            <table class="content-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Nome</th>
                    <th>Espécie</th>
                    <th>Raça</th>
                    <th>Idade</th>
                    <th>Dono</th>
                    <th>Ações</th>
                </tr>
                </thead>
                <tbody id="tabela-pets">
                    <tr><td colspan="7">Carregando...</td></tr>
                </tbody>
            </table>
        </div>
    `;

    document.getElementById('btn-novo-pet').addEventListener('click', () => {
        renderFormulario(container);
    });

    try {
        listaPets = await fetchData('/api/v1/pets');
        const tabela = document.getElementById('tabela-pets');

        if (listaPets && listaPets.length > 0) {
            tabela.innerHTML = listaPets.map(pet => `
                <tr>
                    <td>${pet.id}</td>
                    <td>${pet.nome}</td>
                    <td>${pet.especie}</td>
                    <td>${pet.raca}</td>
                    <td>${pet.idade}</td>
                    <td>${pet.dono || 'N/A'}</td>
                    <td class="actions-cell">
                        <button class="btn-icon btn-edit" data-id="${pet.id}" title="Editar">
                            <i class="fas fa-edit"></i>
                        </button>
                        ${userRole === 'ADMIN' ? `
                        <button class="btn-icon btn-delete" data-id="${pet.id}" title="Excluir">
                            <i class="fas fa-trash-alt"></i>
                        </button>` : ''}
                    </td>
                </tr>
            `).join('');

            document.querySelectorAll('.btn-edit').forEach(btn => {
                btn.addEventListener('click', () => {
                    const id = btn.getAttribute('data-id');
                    const pet = listaPets.find(p => p.id == id);
                    renderFormulario(container, pet);
                });
            });

            if (userRole === 'ADMIN') {
                document.querySelectorAll('.btn-delete').forEach(btn => {
                    btn.addEventListener('click', async () => {
                        const id = btn.getAttribute('data-id');
                        if (confirm('Tem certeza que deseja excluir este pet?')) {
                            try {
                                await postData(`/api/v1/pets?id=${id}`, {}, 'DELETE');
                                render(container);
                            } catch (error) {
                                alert('Erro ao excluir pet: ' + error.message);
                            }
                        }
                    });
                });
            }
        } else {
            tabela.innerHTML = '<tr><td colspan="7">Nenhum pet cadastrado.</td></tr>';
        }
    } catch (error) {
        document.getElementById('tabela-pets').innerHTML = `<tr><td colspan="7">Erro ao carregar pets.</td></tr>`;
    }
}

async function renderFormulario(container, pet = {}) {
    const isEdit = pet.id != null;
    const pageTitle = isEdit ? 'Editar Pet' : 'Cadastro de Pet';

    try {
        listaClientes = await fetchData('/api/v1/clientes');
    } catch (error) {
        container.innerHTML = `<p>Erro ao carregar clientes. Tente novamente.</p>`;
        return;
    }

    // Tenta encontrar o ID do dono pelo nome se o ID não estiver disponível diretamente no objeto pet
    let donoIdSelecionado = '';
    if (pet.dono) {
        // Se 'dono' for uma string (nome), tentamos achar o cliente correspondente
        const clienteEncontrado = listaClientes.find(c => c.nome === pet.dono);
        if (clienteEncontrado) donoIdSelecionado = clienteEncontrado.id;
    }

    const clientesOptions = listaClientes.map(cliente =>
        `<option value="${cliente.id}" ${donoIdSelecionado == cliente.id ? 'selected' : ''}>
            ${cliente.nome}
         </option>`
    ).join('');

    container.innerHTML = `
        <h1 class="page-title">${pageTitle}</h1>
        <div class="form-container">
            <form id="form-pet" class="form-layout">
                <input type="hidden" id="id" name="id" value="${pet.id || ''}">
                <div class="form-group">
                    <label for="nome" class="form-label">Nome do Pet:</label>
                    <input type="text" id="nome" name="nome" class="form-input" value="${pet.nome || ''}" required />
                </div>
                <div class="form-group">
                    <label for="especie" class="form-label">Espécie:</label>
                    <input type="text" id="especie" name="especie" class="form-input" value="${pet.especie || ''}" required />
                </div>
                <div class="form-group">
                    <label for="raca" class="form-label">Raça:</label>
                    <input type="text" id="raca" name="raca" class="form-input" value="${pet.raca || ''}" />
                </div>
                <div class="form-group">
                    <label for="idade" class="form-label">Idade:</label>
                    <input type="number" id="idade" name="idade" class="form-input" value="${pet.idade || 0}" />
                </div>
                <div class="form-group">
                    <label for="sexo" class="form-label">Sexo:</label>
                    <select id="sexo" name="sexo" class="form-select">
                        <option value="Macho" ${pet.sexo === 'Macho' ? 'selected' : ''}>Macho</option>
                        <option value="Femea" ${pet.sexo === 'Femea' ? 'selected' : ''}>Fêmea</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="cliente" class="form-label">Dono:</label>
                    <select id="cliente" name="donoId" class="form-select" required>
                        <option value="">Selecione um dono</option>
                        ${clientesOptions}
                    </select>
                </div>
                <div class="form-group form-group-top">
                    <label for="observacoes" class="form-label">Observações:</label>
                    <textarea id="observacoes" name="observacoes" class="form-textarea">${pet.observacoes || ''}</textarea>
                </div>
                <div class="form-actions">
                    <button type="button" id="btn-cancelar" class="btn btn-secondary">Cancelar</button>
                    <button type="submit" class="btn btn-primary">Salvar</button>
                </div>
            </form>
        </div>
    `;

    document.getElementById('btn-cancelar').addEventListener('click', () => render(container));
    document.getElementById('form-pet').addEventListener('submit', async (e) => {
        e.preventDefault();

        const form = e.target;
        const formData = new FormData(form);
        const petData = Object.fromEntries(formData.entries());

        const method = isEdit ? 'PUT' : 'POST';

        try {
            await postData('/api/v1/pets', petData, method);
            render(container);
        } catch (error) {
            console.error("Erro ao salvar pet:", error);
            alert(`Erro ao salvar: ${error.message}`);
        }
    });
}
