import { fetchData, postData, formatDate } from '../utils/api.js';

/**
 * Renderiza a página de Agendamentos (Lista).
 * @param {HTMLElement} container - O elemento <main> onde o conteúdo será injetado.
 */
export async function render(container) {
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
                    <th>Cliente</th>
                    <th>Pet</th>
                    <th>Serviço</th>
                    <th>Data</th>
                    <th>Horário</th>
                    <th>Status</th>
                </tr>
                </thead>
                <tbody id="tabela-agendamentos">
                    <tr><td colspan="7">Carregando...</td></tr>
                </tbody>
            </table>
        </div>
    `;

    document.getElementById('btn-novo-agendamento').addEventListener('click', () => {
        renderFormulario(container);
    });

    try {
        const agendamentos = await fetchData('/api/v1/agendamentos');
        const tabela = document.getElementById('tabela-agendamentos');

        if (agendamentos && agendamentos.length > 0) {
            tabela.innerHTML = agendamentos.map(ag => `
                <tr>
                    <td>${ag.id}</td>
                    <td>${ag.cliente?.nome || 'N/A'}</td>
                    <td>${ag.pet?.nome || 'N/A'}</td>
                    <td>${ag.servico || 'N/A'}</td>
                    <td>${formatDate(ag.dataHora)}</td>
                    <td>${ag.horario || 'N/A'}</td>
                    <td>${ag.status || 'N/A'}</td>
                </tr>
            `).join('');
        } else {
            tabela.innerHTML = '<tr><td colspan="7">Nenhum agendamento encontrado.</td></tr>';
        }
    } catch (error) {
        document.getElementById('tabela-agendamentos').innerHTML = `<tr><td colspan="7">Erro ao carregar agendamentos.</td></tr>`;
    }
}

/**
 * Renderiza o Formulário de Cadastro de Agendamento (baseado no Wireframe Page 7).
 * @param {HTMLElement} container - O elemento <main>.
 * @param {object} agendamento - Opcional. O objeto para edição.
 */
async function renderFormulario(container, agendamento = {}) {
    const isEdit = agendamento.id != null;
    const pageTitle = isEdit ? 'Editar Agendamento' : 'Novo Agendamento';

    // Listas para os <select>
    let clientesOptions = '';
    let petsOptions = '';

    try {
        const clientes = await fetchData('/api/v1/clientes');
        clientesOptions = clientes.map(c => `<option value="${c.id}">${c.nome}</option>`).join('');

        // Em um app real, o <select> de pets mudaria baseado no cliente
        const pets = await fetchData('/api/v1/pets');
        petsOptions = pets.map(p => `<option value="${p.id}">${p.nome}</option>`).join('');

    } catch (error) {
        container.innerHTML = `<p>Erro ao carregar dados para o formulário. Tente novamente.</p>`;
        return;
    }

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
                        <option value="Exames de laboratório">Exames de laboratório</option>
                        <option value="Vacinação">Vacinação</option>
                        <option value="Hidratação">Hidratação</option>
                        <option value="Massagem">Massagem</option>
                    </select>
                </div>

                <div class="form-group-grid">
                    <label for="dataHora" class="form-label">Data:</label>
                    <input type="date" id="dataHora" name="dataHora" class="form-input" required />
                </div>

                <div class="form-group-grid">
                    <label for="horario" class="form-label">Horário:</label>
                    <input type="time" id="horario" name="horario" class="form-input" required />
                </div>

                <div class="form-group-grid">
                    <label for="observacoes" class="form-label">Observações:</label>
                    <textarea id="observacoes" name="observacoes" class="form-textarea" style="min-height: 80px;"></textarea>
                </div>

                <div class="form-group-grid">
                    <label class="form-label">Horários Disponíveis:</label>
                    <div class="horarios-disponiveis" style="padding: 16px; border: 1px solid var(--border-color); border-radius: 8px; height: 180px; overflow-y: auto; background: var(--bg-light); display: flex; flex-direction: column; gap: 8px;">
                        <!-- Esta parte seria preenchida dinamicamente -->
                        <button type="button" class="btn" style="background: var(--bg-white); ...">09:15</button>
                        <button type="button" class="btn" style="background: var(--bg-white); ...">10:40</button>
                    </div>
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

        // Converte IDs em objetos, como o back-end pode esperar
        agendamentoData.cliente = { id: parseInt(agendamentoData.clienteId) };
        agendamentoData.pet = { id: parseInt(agendamentoData.petId) };
        delete agendamentoData.clienteId;
        delete agendamentoData.petId;

        // Define o status padrão
        agendamentoData.status = 'AGENDADO';

        const endpoint = isEdit ? `/api/v1/agendamentos/${agendamentoData.id}` : '/api/v1/agendamentos';
        const method = isEdit ? 'PUT' : 'POST';

        try {
            await postData(endpoint, agendamentoData, method);
            render(container); // Sucesso! Volta para a lista
        } catch (error) {
            console.error("Erro ao salvar agendamento:", error);
            alert(`Erro ao salvar: ${error.message}`);
        }
    });
}
