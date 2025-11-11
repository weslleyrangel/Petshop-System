import { fetchData } from '../utils/api.js';
import { navigateTo } from '../routes.js'; // 1. Importa a função de navegação

/**
 * Renderiza a página do Dashboard.
 * @param {HTMLElement} container - O elemento <main> onde o conteúdo será injetado.
 */
export async function render(container) {
    // 1. Define o HTML estático da página (títulos, layout dos cards, etc.)
    container.innerHTML = `
        <h1 class="page-title">Dashboard</h1>

        <!-- Ações Rápidas -->
        <div class="dashboard-actions">
            <a id="action-cad-cliente" href="#" class="action-card">
                <h2>Cadastro de Cliente</h2>
                <p>Adicionar um novo cliente ao sistema.</p>
            </a>
            <a id="action-cad-pet" href="#" class="action-card">
                <h2>Cadastro de Pet</h2>
                <p>Adicionar um novo pet para um cliente existente.</p>
            </a>
        </div>

        <!-- Cartões de Estatísticas (com IDs para preenchimento) -->
        <div class="stats-grid">
            <div class="stat-card">
                <h3>Total de Clientes</h3>
                <p id="stat-total-clientes">...</p>
            </div>
            <div class="stat-card">
                <h3>Total de Pets</h3>
                <p id="stat-total-pets">...</p>
            </div>
            <div class="stat-card">
                <h3>Agendamentos do Dia</h3>
                <p id="stat-agendamentos-hoje">...</p>
            </div>
        </div>

        <!-- Tabela de Próximos Agendamentos -->
        <div class="table-container dashboard-table">
            <h2>Próximos Agendamentos</h2>
            <table class="content-table">
                <thead>
                <tr>
                    <th>Serviço</th>
                    <th>Horário</th>
                    <th>Cliente</th>
                    <th>Pet</th>
                </tr>
                </thead>
                <tbody id="tabela-proximos-agendamentos">
                    <tr><td colspan="4">Carregando...</td></tr>
                </tbody>
            </table>
        </div>
    `;

    // 2. Adiciona os listeners para os botões de ação rápida
    document.getElementById('action-cad-cliente').addEventListener('click', (e) => {
        e.preventDefault(); // Impede que o link '#' recarregue a página
        navigateTo('/clientes'); // Usa nossa função para navegar
    });
    document.getElementById('action-cad-pet').addEventListener('click', (e) => {
        e.preventDefault();
        navigateTo('/pets');
    });

    // 2. Busca os dados dinâmicos da API
    // NOTA: Você precisará criar estes endpoints no seu back-end!
    try {
        // Busca estatísticas
        const stats = await fetchData('/api/v1/dashboard/stats');
        document.getElementById('stat-total-clientes').textContent = stats.totalClientes || 0;
        document.getElementById('stat-total-pets').textContent = stats.totalPets || 0;
        document.getElementById('stat-agendamentos-hoje').textContent = stats.agendamentosHoje || 0;

        // Busca próximos agendamentos
        const agendamentos = await fetchData('/api/v1/agendamentos/proximos');
        const tabelaAgendamentos = document.getElementById('tabela-proximos-agendamentos');

        if (agendamentos && agendamentos.length > 0) {
            tabelaAgendamentos.innerHTML = agendamentos.map(ag => `
                <tr>
                    <td>${ag.servico || 'N/A'}</td>
                    <td>${ag.horario || 'N/A'}</td>
                    <td>${ag.cliente?.nome || 'N/A'}</td>
                    <td>${ag.pet?.nome || 'N/A'}</td>
                </tr>
            `).join('');
        } else {
            tabelaAgendamentos.innerHTML = '<tr><td colspan="4">Nenhum agendamento para hoje.</td></tr>';
        }

    } catch (error) {
        console.error("Erro ao carregar dados do dashboard:", error);
        container.innerHTML += `<div class="login-error-box" style="display: block; margin-top: 20px;"><p>Não foi possível carregar os dados do dashboard.</p></div>`;
    }
}
