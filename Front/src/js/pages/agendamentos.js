import { fetchData, postData, formatDate } from '../utils/api.js';

let listaAgendamentos = [];
let listaClientes = [];
let listaPets = [];

export async function render(container) {
    const userRole = localStorage.getItem('role');

    container.innerHTML = `
        <div class="page-header">
            <h1 class="page-title">Lista de Agendamentos</h1>
            <button id="btn-novo-agendamento" class="btn btn-primary">Novo Agendamento</button>
        </div>
        <div class="table-container">
            <table class="content-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Pet</th>
                    <th>Serviço</th>
                    <th>Data</th>
                    <th>Status</th>
                    <th>Ações</th>
                </tr>
                </thead>
                <tbody id="tabela-agendamentos">
                    <tr><td colspan="6">Carregando...</td></tr>
                </tbody>
            </table>
        </div>
    `;

    document.getElementById('btn-novo-agendamento').addEventListener('click', () => {
        renderFormulario(container);
    });

    try {
        listaAgendamentos = await fetchData('/api/v1/agendamentos');
        const tabela = document.getElementById('tabela-agendamentos');

        if (listaAgendamentos && listaAgendamentos.length > 0) {
            tabela.innerHTML = listaAgendamentos.map(ag => `
                <tr>
                    <td>${ag.id}</td>
                    <td>${ag.pet || 'N/A'}</td>
                    <td>${ag.servico || 'N/A'}</td>
                    <td>${formatDate(ag.data)}</td>
                    <td>${ag.status || 'N/A'}</td>
                    <td class="actions-cell">
                        ${userRole === 'ADMIN' ? `
                        <button class="btn-icon btn-delete" data-id="${ag.id}" title="Cancelar">
                            <i class="fas fa-ban"></i>
                        </button>` : ''}
                    </td>
                </tr>
            `).join('');

            if (userRole === 'ADMIN') {
                document.querySelectorAll('.btn-delete').forEach(btn => {
                    btn.addEventListener('click', async () => {
                        const id = btn.getAttribute('data-id');
                        if (confirm('Tem certeza que deseja cancelar este agendamento?')) {
                            try {
                                await postData(`/api/v1/agendamentos?id=${id}`, {}, 'DELETE');
                                render(container);
                            } catch (error) {
                                alert('Erro ao cancelar agendamento: ' + error.message);
                            }
                        }
                    });
                });
            }
        } else {
            tabela.innerHTML = '<tr><td colspan="6">Nenhum agendamento encontrado.</td></tr>';
        }
    } catch (error) {
        document.getElementById('tabela-agendamentos').innerHTML = `<tr><td colspan="6">Erro ao carregar agendamentos.</td></tr>`;
    }
}

async function renderFormulario(container, agendamento = {}) {
    const isEdit = agendamento.id != null;
    const pageTitle = isEdit ? 'Editar Agendamento' : 'Novo Agendamento';

    try {
        listaClientes = await fetchData('/api/v1/clientes');
        listaPets = await fetchData('/api/v1/pets');
    } catch (error) {
        container.innerHTML = `<p>Erro ao carregar dados. Tente novamente.</p>`;
        return;
    }

    const clientesOptions = listaClientes.map(c => `<option value="${c.id}">${c.nome}</option>`).join('');
    const petsOptions = listaPets.map(p => `<option value="${p.id}">${p.nome}</option>`).join('');

    container.innerHTML = `
        <h1 class="page-title">${pageTitle}</h1>
        <div class="form-container" style="max-width: 1024px;">
            <form id="form-agendamento" class="form-grid">
                <input type="hidden" name="id" value="${agendamento.id || ''}">

                <div class="form-group-grid">
                    <label for="cliente" class="form-label">Cliente:</label>
                    <select id="cliente" name="clienteId" class="form-select" required>
                        <option value="">Selecione um cliente</option>
                        ${clientesOptions}
                    </select>
                </div>

                <div class="form-group-grid">
                    <label for="pet" class="form-label">Pet:</label>
                    <select id="pet" name="petId" class="form-select" required>
                        <option value="">Selecione um pet</option>
                        ${petsOptions}
                    </select>
                </div>

                <div class="form-group-grid">
                    <label for="servico" class="form-label">Serviço:</label>
                    <select id="servico" name="servico" class="form-select" required>
                        <option value="Banho">Banho</option>
                        <option value="Tosa">Tosa</option>
                        <option value="Banho e Tosa">Banho e Tosa</option>
                        <option value="Consulta">Consulta Veterinária</option>
                    </select>
                </div>

                <div class="form-group-grid">
                    <label for="dataHora" class="form-label">Data e Hora:</label>
                    <input type="datetime-local" id="dataHora" name="dataHora" class="form-input" required />
                </div>

                <div class="form-group-grid">
                    <label for="observacoes" class="form-label">Observações:</label>
                    <textarea id="observacoes" name="observacoes" class="form-textarea" style="min-height: 80px;"></textarea>
                </div>

                <div class="form-actions col-span-2">
                    <button type="button" id="btn-cancelar" class="btn btn-secondary">Cancelar</button>
                    <button type="submit" class="btn btn-primary">Agendar</button>
                </div>
            </form>
        </div>
    `;

    document.getElementById('btn-cancelar').addEventListener('click', () => render(container));
    document.getElementById('form-agendamento').addEventListener('submit', async (e) => {
        e.preventDefault();
        const form = e.target;
        const formData = new FormData(form);
        const agendamentoData = Object.fromEntries(formData.entries());

        const method = isEdit ? 'PUT' : 'POST';

        try {
            await postData('/api/v1/agendamentos', agendamentoData, method);
            render(container);
        } catch (error) {
            console.error("Erro ao salvar agendamento:", error);
            alert(`Erro ao salvar: ${error.message}`);
        }
    });
}
